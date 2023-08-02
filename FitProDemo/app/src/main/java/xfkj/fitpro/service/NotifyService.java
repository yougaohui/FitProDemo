package xfkj.fitpro.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.application.FitProSDK.getContext;
import static com.legend.bluetooth.fitprolib.utils.NotifiMsgHelper.CALL;
import static com.legend.bluetooth.fitprolib.utils.NotifiMsgHelper.sendNotifyPush;
import static xfkj.fitpro.application.MyApplication.removeALLActivity_;

import androidx.core.app.NotificationManagerCompat;


public class NotifyService extends NotificationListenerService {
    private final String TAG = NotifyService.class.getSimpleName();
    private String telNum = "";

    // 电话管理者对象
    private TelephonyManager mTelephonyManager;
    // 电话状态监听者
    private MyPhoneStateListener myPhoneStateListener;
    private BleManager mBle;
    private String sendVal = "";//20秒内状态栏上面收到的同一条通知，只推送一条
    private static NotifyService mNotifyService;

    public static NotifyService getInstance() {
        return mNotifyService;
    }

    @Override
    public void onCreate() {
        Logdebug("NotifyService", "----NotificationListenerService-------启动状态栏通知服务----");
        mNotifyService = this;
        PermissionUtils.permission(PermissionConstants.PHONE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                mTelephonyManager = (TelephonyManager) getSystemService(getContext().TELEPHONY_SERVICE);
                myPhoneStateListener = new MyPhoneStateListener();
                mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

            @Override
            public void onDenied() {

            }
        }).request();

        super.onCreate();

        mBle = BleManager.getInstance();
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        if (SDKTools.IsAppOnForeground == 0) {
                            Thread.sleep(25000);
                            if (SDKTools.otaState > 0) {
                                return;
                            }
                            mBle.getConnetedBleState();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            boolean isForground = intent.getBooleanExtra("isForground", true);//是前台启动还是后台启动
            if (!isForground) {
                startForegroundNotifi();
            }
        }
        return START_STICKY;
    }

    /**
     * 启动前台通知
     */
    private void startForegroundNotifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1234", "MyNotifyService",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), "1234").build();
            startForeground(1, notification);

            Log.i(TAG, "=================>>startForegroundNotifi");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();
        String msgs = "";
        if (sbn.getPackageName().isEmpty()) {
            return;
        }
        if (sbn.getNotification().tickerText != null) {
            msgs = sbn.getNotification().tickerText.toString();
        } else {
            Bundle extras = sbn.getNotification().extras;
            String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
            CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
            if (notificationTitle != null) {
                msgs += notificationTitle;
            }
            if (notificationText != null) {
                msgs += notificationText.toString();
            }
            if (notificationSubText != null) {
                msgs += notificationSubText.toString();
            }
        }
        if (packageName.equals("com.android.incallui")) {
            telNum = msgs;
        }
        Logdebug("NotifyService", "状态栏收到消息是 --ID :" + sbn.getId() + "--内容：" + msgs + "--包名：" + packageName);
        sendNotifyPush(packageName, msgs, 0);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Logdebug("NotifyService", "状态栏清除消息是   --ID :" + sbn.getId() + "--内容：-----" + sbn.getNotification().tickerText + "--包名：" + sbn.getPackageName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mNotifyService = null;
        // 取消来电的电话状态监听服务
        if (mTelephonyManager != null && myPhoneStateListener != null) {
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }


    int lastComeState = -1;//上一次的来电状态

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            String msgs = incomingNumber;
            int t_call;
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://挂断
                    //    msgs ="来电挂断";
                    t_call = 0;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //    msgs ="未知来电";
                    t_call = 1;
                    lastComeState = t_call;
                    break;
                default:
                    t_call = -1;
                    lastComeState = t_call;
            }
            String name = queryNameByNum(incomingNumber);
            if (!StringUtils.isEmpty(name)) {
                msgs = name;
            }
            if (t_call >= 0 && !StringUtils.isEmpty(msgs)) {
                String content = msgs;
                //判断是否支持语音通报
                if (FitProSpUtils.isSurpportVoicePlay()) {
                    name = StringUtils.isTrimEmpty(name) ? "" : name;
                    content = name + "/" + incomingNumber;
                }
                sendNotifyPush(CALL, content.trim(), t_call);
                Log.i(TAG, "发送通话状态");
            }
            Logdebug(TAG, "电话监听收到--ID : 内容：" + msgs + "--包名：" + CALL + "--号码--" + incomingNumber + "---t_call---" + t_call);
        }
    }

    public String queryNameByNum(String num) {
        if (!PermissionUtils.isGranted(Manifest.permission.READ_CONTACTS)) return null;
        String num1 = separateString(num, 3, 4, '-');
        String num2 = separateString(num, 3, 4, ' ');
        Cursor cursorOriginal = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + num + "' or "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + num1 + "' or "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + num2 + "'", null, null);
        if (null != cursorOriginal) {
            if (cursorOriginal.getCount() > 1) {
                return null;
            } else {
                if (cursorOriginal.moveToFirst()) {
                    return cursorOriginal.getString(cursorOriginal.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    /**
     * 用指定字符分隔格式化字符串
     * <br/>（如电话号为1391235678 指定startIndex为3，step为4，separator为'-'经过此处理后的结果为 <br/> 139-1234-5678）
     *
     * @param source     需要分隔的字符串
     * @param startIndex 开始分隔的索引号
     * @param step       步长
     * @param separator  指定的分隔符
     * @return 返回分隔格式化处理后的结果字符串
     */
    private String separateString(String source, int startIndex, int step, char separator) {
        int times = 0;
        StringBuilder tmpBuilder = new StringBuilder(source);
        for (int i = 0; i < tmpBuilder.length(); i++) {
            if (i == startIndex + step * times + times) {//if(i == 3 || i == 8){
                if (separator != tmpBuilder.charAt(i)) {
                    tmpBuilder.insert(i, separator);
                }
                times++;
            } else {
                if (separator == tmpBuilder.charAt(i)) {
                    tmpBuilder.deleteCharAt(i);
                    i = -1;
                    times = 0;
                }
            }
        }
        return tmpBuilder.toString();
    }

    public static boolean isNotificationListenersEnabled(Context context) {
        return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.getPackageName());
    }

    public static boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    public static void showNotifyPermissionDialog(final Context context) {
        if (isNotificationListenersEnabled(context)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("通知读取权限");
        builder.setMessage("是否允许开启？");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeALLActivity_();
                    }
                });
        builder.setPositiveButton("开启",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoNotificationAccessSetting(context);
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }


}
