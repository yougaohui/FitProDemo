package xfkj.fitpro.application;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.legend.bluetooth.fitprolib.application.FitProSDK;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.ProfilePlus;
import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;
import com.legend.bluetooth.fitprolib.model.DeviceHardInfoModel;
import com.legend.bluetooth.fitprolib.model.MeasureBloodModel;
import com.legend.bluetooth.fitprolib.model.MeasureDetailsModel;
import com.legend.bluetooth.fitprolib.model.MeasureHeartModel;
import com.legend.bluetooth.fitprolib.model.MeasureSpoModel;
import com.legend.bluetooth.fitprolib.model.ProductInfoModel;
import com.legend.bluetooth.fitprolib.model.SleepDetailsModel;
import com.legend.bluetooth.fitprolib.model.SportDetailsModel;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import xfkj.fitpro.db.SqliteDBAcces;
import xfkj.fitpro.service.NotifyService;

public class MyApplication extends Application {

    private final String TAG = "MyApplication";

    /**
     * ?????????????????????
     */
    public static SqliteDBAcces DBAcces = null;

    private static MyApplication context;

    private LeReceiver leReceiver;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//??????msg?????????????????????
            Log.i(TAG, TAG + "----state-------[" + map.get("state") + "]-----msg.what----" + msg.what);
            String Sql = "";
            switch (msg.what) {
                case Profile.MsgWhat.what90://??????????????????
                    SleepDetailsModel sleepItem = (SleepDetailsModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "sleepItem:" + sleepItem.toString());
                    Sql = "insert into Sleep (RevDate,Offset,SleepTypes,Data,LongDate) values ('" + sleepItem.getRevDate() + "'," + sleepItem.getOffset() + "," + sleepItem.getSleepType() + "," + sleepItem.getOffset() + "," + sleepItem.getTime() + ")";
                    DBAcces.Execute(Sql);
                    break;
                case Profile.MsgWhat.what51://??????????????????
                    SportDetailsModel sportDetailsModel = (SportDetailsModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "sportDetailsModel:" + sportDetailsModel.toString());
                    Sql = "insert into Step (SportDate,ActiveTime,Mode,Offset,Distance,Calory,Steps,LongDate) values ('" + sportDetailsModel.getSysDate() + "'," + sportDetailsModel.getMin() + "," + sportDetailsModel.getMode() + "," + sportDetailsModel.getOffset() + "," + sportDetailsModel.getDistance() + "," + sportDetailsModel.getCalory() + "," + sportDetailsModel.getStep() + "," + sportDetailsModel.getLongDate() + ") ";
                    DBAcces.Execute(Sql);
                    break;
                case Profile.MsgWhat.what60://????????????????????????
                    MeasureDetailsModel measureDetailsModel = (MeasureDetailsModel) map.get(SDKTools.EXTRA_DATA);
                    String ymd = TimeUtils.millis2String(measureDetailsModel.getTime(), new SimpleDateFormat("yyyy-MM-dd"));
                    String hhmmss = TimeUtils.millis2String(measureDetailsModel.getTime(), new SimpleDateFormat("HH:mm:ss"));
                    Log.e(TAG, "measureDetailsModel:" + measureDetailsModel.toString() + ";ymd:" + ymd + ";hhmm:" + hhmmss);
                    Sql = "insert into Measure (SysDate,RevDate,Heart,hBlood,lBlood,Spo,LongDate) values ('" + ymd + "','" + hhmmss + "','" + measureDetailsModel.getHeart() + "','" + measureDetailsModel.getHblood() + "','" + measureDetailsModel.getLblood() + "','" + measureDetailsModel.getSpo() + "'," + measureDetailsModel.getTime() + ") ";
                    DBAcces.Execute(Sql);
                    break;
                //???????????????????????????????????????????????????????????????????????????
                case ProfilePlus.MsgWhat.what1://??????????????????
                    ClockDialInfoBody body = (ClockDialInfoBody) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "clock info:" + body.toString());
                    ToastUtils.showShort(body.toString());
                    break;
                case ProfilePlus.MsgWhat.what2://??????????????????
                    DeviceHardInfoModel deviceInfo = (DeviceHardInfoModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "deviceInfo:" + deviceInfo.toString());
                    ToastUtils.showShort(deviceInfo.toString());
                    break;
                case ProfilePlus.MsgWhat.what3://??????????????????
                    ProductInfoModel productInfo = (ProductInfoModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "deviceInfo:" + productInfo.toString());
                    ToastUtils.showShort(productInfo.toString());
                    break;
                case Profile.MsgWhat.what69://??????????????????
                    MeasureHeartModel measureHeartModel = (MeasureHeartModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "measureHeartModel:" + measureHeartModel.toString());
                    ToastUtils.showShort(measureHeartModel.toString());
                    break;
                case Profile.MsgWhat.what62://??????????????????
                    MeasureBloodModel measureBloodModel = (MeasureBloodModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "measureBloodModel:" + measureBloodModel.toString());
                    ToastUtils.showShort(measureBloodModel.toString());
                    break;
                case Profile.MsgWhat.what67://??????????????????
                    MeasureSpoModel measureSpoModel = (MeasureSpoModel) map.get(SDKTools.EXTRA_DATA);
                    Log.e(TAG, "measureSpoModel:" + measureSpoModel.toString());
                    ToastUtils.showShort(measureSpoModel.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Utils.init(this);
        FitProSDK.getFitProSDK().init(this);
        CrashUtils.init();
        notificationSettings();
        startNotifyService(this);
        setLanguage();

        String processName = ProcessUtils.getCurrentProcessName();
        String packageName = this.getPackageName();
        if (packageName.equals(processName)) {
            leReceiver = new LeReceiver(this, mHandler);
            leReceiver.registerLeReceiver();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static MyApplication getContext() {
        return context;
    }

    /**
     * ???????????????
     */
    public static void setLanguage() {
        int language = SaveKeyValues.getIntValues("language", 0);
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale locale = Locale.getDefault();
        switch (language) {
            case 0:
                locale = Locale.CHINESE;
                break;
            case 1:
                locale = Locale.ENGLISH;
                break;
            case 2:
                locale = Locale.TAIWAN;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }


    public static void startNotifyService(Context context) {
        if (!ServiceUtils.isServiceRunning("com.legend.bluetooth.fitprolib.service.NotifyService")) {
            //????????????????????????
            Intent intent = new Intent(context, NotifyService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //android 8.0?????????????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !AppUtils.isAppForeground()) {
                intent.putExtra("isForground", false);
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            toggleNotificationListenerService(context);
        }
    }

    public static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotifyService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, NotifyService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        Window window = activity.getWindow();
        //?????????????????????
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //??????Flag?????????????????????????????????
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //?????????????????????
        window.setStatusBarColor(colorResId);
        //???????????????????????????????????????
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //???view?????????????????????????????????????????????
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    public static String returnshi(int data) {
        return data < 10 ? "0" + data : data + "";
    }

    public static String getRequset(final String url) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL requestUrl = new URL(url);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (connection != null) {
                        connection.disconnect();//???????????????????????????
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void clearChatMsg(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationId == -1) {
            //??????????????????
            notificationManager.cancelAll();
        } else {
            //???????????????id????????? (??????????????????Context????????????Notification)
            notificationManager.cancel(notificationId);
        }
    }

    public static void notificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "fitpro";
            NotificationManager manager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                getContext().startActivity(intent);
            }
        }
    }

    /**
     * ??????Activity ???list
     */
    private static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>());

    /**
     * @param activity ???????????? ???????????????activity????????????
     */
    public void addActivity_(Activity activity) {
        if (!mActivitys.contains(activity)) {
            mActivitys.add(activity);//?????????Activity??????????????????
        }
    }

    /**
     * @param activity ???????????? ???????????????activity????????????
     *                 ???????????????Activity
     */
    public static void removeActivity_(Activity activity) {
        if (mActivitys.contains(activity)) {
            mActivitys.remove(activity);
            activity.finish();
        }
    }

    /**
     * ????????????Activity
     */
    public static void removeALLActivity_() {
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    public static void resetTables() {
        if (DBAcces != null) {
            SqliteDBAcces DBAccess = DBAcces;
            DBAccess.Execute("DELETE FROM Measure");
            DBAccess.Execute("DELETE FROM Sleep");
            DBAccess.Execute("DELETE FROM Step");
        }
    }
}
