package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetCallRemindValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetHandSideValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetInfoByKey;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetWatchRemindValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;
import static xfkj.fitpro.application.MyApplication.notificationSettings;
import static xfkj.fitpro.service.NotifyService.gotoNotificationAccessSetting;
import static xfkj.fitpro.service.NotifyService.isNotificationListenersEnabled;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.adapter.MessageSettingAdapter;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.view.SettingMenuItem;

/**
 * 消息设置
 */
public class MessageSettingActivity extends BaseActivity {
    private String TAG = "MessageSettingActivity";
    private ArrayList<SettingMenuItem> mData;
    private RecyclerView rlv;
    private RelativeLayout handbox;
    private MessageSettingAdapter adapter;
    private View mFrmLoadding;
    private String Title = "";
    private int Id;
    private LeReceiver leReceiver;
    private Switch hSwitch;

    private final int MSG_HIDE_LOADDING = 0x1111;//隐藏加载dialog


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            if (msg.what == Profile.MsgWhat.what37 || msg.what == Profile.MsgWhat.what40) {
                SDKTools.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constants.dialog != null)
                            Constants.dialog.dismiss();
                        Toast toast;
                        if (map.get("is_ok") != null && map.get("is_ok").equals("0")) {
                            toast = Toast.makeText(MessageSettingActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            DataToUI();
                        } else {
                            toast = Toast.makeText(MessageSettingActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }, 1000);
            } else if (msg.what == Profile.MsgWhat.what14) {
                DataToUI();
                if (Constants.dialog != null)
                    Constants.dialog.dismiss();
            } else if (msg.what == MSG_HIDE_LOADDING) {
                mFrmLoadding.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(Title, this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_message_setting);
    }


    @Override
    protected void initValues() {
        Title = getIntent().getStringExtra("Title");
        Id = getIntent().getIntExtra("type", 0);
        leReceiver = new LeReceiver(MessageSettingActivity.this, mHandler);
    }


    private void DataToUI() {
        Log.i(TAG, "===============DataToUI");
        if (Id == R.string.push_setting_txt) {
            handbox.setVisibility(View.GONE);
            if (null == mData || mData.size() == 0) {
                mData.add(new SettingMenuItem(R.string.calls_remind, getString(R.string.calls_remind), SaveKeyValues.getStringValues("CALLState", "0"), 1, R.drawable.device_switch_phone, true, 1, null));
                mData.add(new SettingMenuItem(R.string.sms_remind, getString(R.string.sms_remind), SaveKeyValues.getStringValues("SMSState", "0"), 1, R.drawable.device_switch_sms, true, 1, null));
                mData.add(new SettingMenuItem(R.string.wechat_remind, getString(R.string.wechat_remind), SaveKeyValues.getStringValues("WECHATState", "0"), 1, R.drawable.device_switch_wechat, true, 1, null));
                mData.add(new SettingMenuItem(R.string.qq_remind, getString(R.string.qq_remind), SaveKeyValues.getStringValues("QQState", "0"), 1, R.drawable.device_switch_qq, true, 1, null));
                mData.add(new SettingMenuItem(R.string.face_book_remind, getString(R.string.face_book_remind), SaveKeyValues.getStringValues("FaceBookState", "0"), 1, R.drawable.device_switch_face_book, true, 1, null));
                mData.add(new SettingMenuItem(R.string.twitter_remind, getString(R.string.twitter_remind), SaveKeyValues.getStringValues("TwitterState", "0"), 1, R.drawable.device_switch_twitter, true, 1, null));
                //    mData.add(new SettingMenuItem(R.string.skype_remind,getString(R.string.skype_remind), SaveKeyValues.getStringValues("SkypeState", "0"), 1, R.drawable.device_switch_skype, true, 1, null));
                mData.add(new SettingMenuItem(R.string.line_remind, getString(R.string.line_remind), SaveKeyValues.getStringValues("LineState", "0"), 1, R.drawable.device_switch_line, true, 1, null));
                mData.add(new SettingMenuItem(R.string.whatsapp_remind, getString(R.string.whatsapp_remind), SaveKeyValues.getStringValues("WhatsappState", "0"), 1, R.drawable.device_switch_whatsapp, true, 1, null));
                mData.add(new SettingMenuItem(R.string.instagram_remind, getString(R.string.instagram_remind), SaveKeyValues.getStringValues("INSTAGRAMState", "0"), 1, R.drawable.device_switch_instagram, false, 1, null));
                //    mData.add(new SettingMenuItem(R.string.kakao_talk_remind,getString(R.string.kakaotalk_remind), SaveKeyValues.getStringValues("KakaoTalkState", "0"), 1, R.drawable.device_switch_talk, true, 1, null));
            } else {
                refreshMsgNotifiUI();
                return;
            }
        } else {
            String hr = SaveKeyValues.getStringValues("HANDState", "0");//1：右手 0：左手
            Logdebug(TAG, "选中开关:HANDState--开关状态:" + hr);
            hSwitch.setChecked(hr.equals("1") ? true : false);
            if (null == mData || mData.size() == 0) {
                mData.add(new SettingMenuItem(R.string.vibrate_setting, getString(R.string.vibrate_setting), SaveKeyValues.getStringValues("SHOCKState", "0"), 1, R.drawable.device_switch_zhendong, false, 1, null));
            } else {//已经初始化后
                String shakeStatus = SaveKeyValues.getStringValues("SHOCKState", "0");
                SettingMenuItem data = mData.get(0);
                if (!shakeStatus.equals(data.getNameInfo())) {//状态不一致的情况才进行UI刷新
                    data.setNameInfo(shakeStatus);
                    adapter.notifyDataSetChanged();
                }
                return;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 刷新消息通知UI,单独用次方法是防止多次调用适配器，出现重复开关的现象
     */
    private void refreshMsgNotifiUI() {
        for (int i = 0; i < mData.size(); i++) {
            SettingMenuItem data = mData.get(i);
            switch (i) {
                case 0:
                    String callState = SaveKeyValues.getStringValues("CALLState", "0");
                    if (!callState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(callState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 1:
                    String SMSState = SaveKeyValues.getStringValues("SMSState", "0");
                    if (!SMSState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(SMSState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 2:
                    String WECHATState = SaveKeyValues.getStringValues("WECHATState", "0");
                    if (!WECHATState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(WECHATState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 3:
                    String QQState = SaveKeyValues.getStringValues("QQState", "0");
                    if (!QQState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(QQState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 4:
                    String FaceBookState = SaveKeyValues.getStringValues("FaceBookState", "0");
                    if (!FaceBookState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(FaceBookState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 5:
                    String TwitterState = SaveKeyValues.getStringValues("TwitterState", "0");
                    if (!TwitterState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(TwitterState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 6:
                    String LineState = SaveKeyValues.getStringValues("LineState", "0");
                    if (!LineState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(LineState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 7:
                    String WhatsappState = SaveKeyValues.getStringValues("WhatsappState", "0");
                    if (!WhatsappState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(WhatsappState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 8:
                    String INSTAGRAMState = SaveKeyValues.getStringValues("INSTAGRAMState", "0");
                    if (!INSTAGRAMState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(INSTAGRAMState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
            }
        }
    }

    @Override
    protected void initViews() {
        rlv = findViewById(R.id.rlv);
        hSwitch = (Switch) findViewById(R.id.hand_status);
        mData = new ArrayList<>();
        handbox = findViewById(R.id.hand_lr_box);
        mFrmLoadding = findViewById(R.id.frm_loadding);

        adapter = new MessageSettingAdapter(this, mData);
        adapter.setOnItemClickListener(new MessageSettingAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position, boolean isCheck) {
                SettingMenuItem item = mData.get(position);
                String val = item.getNameInfo();
                Logdebug(TAG, "选中开关:" + item.Name + "--开关状态:" + val);
                if (SDKTools.BleState != 1) {
                    Toast.makeText(MessageSettingActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (view == null) {
                    String key = "";
                    if (item.Id == R.string.calls_remind) {
                        key = "CALLState";
                    } else if (item.Id == R.string.sms_remind) {
                        key = "SMSState";
                    } else if (item.Id == R.string.wechat_remind) {
                        key = "WECHATState";
                    } else if (item.Id == R.string.qq_remind) {
                        key = "QQState";
                    } else if (item.Id == R.string.face_book_remind) {
                        key = "FaceBookState";
                    } else if (item.Id == R.string.twitter_remind) {
                        key = "TwitterState";
                    } else if (item.Id == R.string.skype_remind) {
                        key = "SkypeState";
                    } else if (item.Id == R.string.line_remind) {
                        key = "LineState";
                    } else if (item.Id == R.string.whatsapp_remind) {
                        key = "WhatsappState";
                    } else if (item.Id == R.string.instagram_remind) {
                        key = "INSTAGRAMState";
                    } else if (item.Id == R.string.vibrate_setting) {
                        key = "SHOCKState";
                    } else if (item.Id == R.string.lift_screen) {
                        key = "BRIGHTState";
                    } else if (item.Id == R.string.sleep_monitoring) {
                        key = "SLEEPState";
                    } else if (item.Id == R.string.automatic_heart_rate) {
                        key = "HEARTState";
                    } else if (item.Id == R.string.lr_hand_wearing) {
                        key = "HANDState";
                    }
                    setSendBeforeValue(key, 1, SaveKeyValues.getStringValues(key, "1"));
                    SaveKeyValues.putStringValues(key, val + "");
                    Logdebug(TAG, "选中开关:" + item.Name + "--开关状态:" + SaveKeyValues.getStringValues(key, ""));
                    setMessageTofitpro((key == "HANDState") ? true : false);
                }
            }
        });
        rlv.setLayoutManager(new LinearLayoutManager(this));
        rlv.setAdapter(adapter);
        byte CommandKey = (byte) 0x06;
        DataToUI();
        if (Id == R.string.push_setting_txt) {
            if (!isNotificationListenersEnabled(MessageSettingActivity.this)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoNotificationAccessSetting(MessageSettingActivity.this);
                    }
                }, 2000);
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationSettings();
                }
            }, 1000);
            CommandKey = (byte) 0x04;
        }
        if (SDKTools.BleState == 1) {
            SDKCmdMannager.getMessagesInfo(getSetInfoByKey(CommandKey));
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(MessageSettingActivity.this)
                    .setMessage(getString(R.string.getdatas))
                    .setCancelable(false);
            //Constant.dialog = mBuilder.create(true, 2000);
        }
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_LOADDING, 2000);
    }

    @Override
    protected void setViewsListener() {

        hSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //控制开关字体颜色
                int val = 0;
                if (hSwitch.isChecked()) {
                    val = 1;
                }//1：右手 0：左手
                Logdebug(TAG, "选中开关:HANDState--开关状态:" + val);
                if (SDKTools.BleState != 1) {
                    Toast.makeText(MessageSettingActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (hSwitch.isChecked()) {
                        hSwitch.setChecked(false);
                    } else {
                        hSwitch.setChecked(true);
                    }
                    return;
                }
                setSendBeforeValue("HANDState", 1, SaveKeyValues.getStringValues("HANDState", "1"));
                SaveKeyValues.putStringValues("HANDState", val + "");
                setMessageTofitpro(true);
            }
        });
    }

    @Override
    protected void setViewsFunction() {

    }

    public void setMessageTofitpro(boolean is_hand) {
        if (SDKTools.BleState != 1) {
            Toast.makeText(MessageSettingActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(MessageSettingActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true, 5000);
        byte[] uinfo;
        String desc;
        if (is_hand) {
            uinfo = getSetHandSideValue();//获取设置左右手佩戴协议
            desc = "设置左右手佩戴";
        } else {
            if (Title.equals(getString(R.string.push_setting_txt))) {
                desc = "设置消息推送开关";
                uinfo = getSetCallRemindValue();//获取设置消息推送开关（来电、短信、微信、QQ）协议
            } else {
                desc = "设置设备提醒开关";
                uinfo = getSetWatchRemindValue();//获取设置设备提醒开关（(抬手亮屏、马达开关、睡眠开启、心率自动测量）协议
            }
        }
        SDKTools.mService.commandPoolWrite(uinfo, desc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (leReceiver != null)
            leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_HIDE_LOADDING);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leReceiver != null)
            leReceiver.registerLeReceiver();
    }
}
