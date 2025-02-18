package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getBrightScreenValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetCallRemindValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetHandSideValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetInfoByKey;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetWatchRemindValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;
import static xfkj.fitpro.application.MyApplication.notificationSettings;

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

import com.blankj.utilcode.util.StringUtils;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.constants.SPKey;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.adapter.MessageSettingAdapter;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.utils.UIHelper;
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
                SDKTools.mHandler.postDelayed(() -> {
                    DialogHelper.hideDialog();
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
                }, 1000);
            } else if (msg.what == Profile.MsgWhat.what14) {
                DataToUI();
                DialogHelper.hideDialog();
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
                mData.add(new SettingMenuItem(R.string.calls_remind, getString(R.string.calls_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_CALL, "0"), 1, R.drawable.device_switch_phone, true, 1, null));
                mData.add(new SettingMenuItem(R.string.sms_remind, getString(R.string.sms_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SMS, "0"), 1, R.drawable.device_switch_sms, true, 1, null));
                mData.add(new SettingMenuItem(R.string.wechat_remind, getString(R.string.wechat_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_WECHAT, "0"), 1, R.drawable.device_switch_wechat, true, 1, null));
                mData.add(new SettingMenuItem(R.string.qq_remind, getString(R.string.qq_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_QQ, "0"), 1, R.drawable.device_switch_qq, true, 1, null));
                mData.add(new SettingMenuItem(R.string.face_book_remind, getString(R.string.face_book_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_FACEBOOK, "0"), 1, R.drawable.device_switch_face_book, true, 1, null));
                mData.add(new SettingMenuItem(R.string.twitter_remind, getString(R.string.twitter_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TWITTER, "0"), 1, R.drawable.device_switch_twitter, true, 1, null));
                mData.add(new SettingMenuItem(R.string.line_remind, getString(R.string.line_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_LINE, "0"), 1, R.drawable.device_switch_line, true, 1, null));
                mData.add(new SettingMenuItem(R.string.whatsapp_remind, getString(R.string.whatsapp_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_WHATSAPP, "0"), 1, R.drawable.device_switch_whatsapp, true, 1, null));
                mData.add(new SettingMenuItem(R.string.instagram_remind, getString(R.string.instagram_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_INSTAGRAM, "0"), 1, R.drawable.device_switch_instagram, false, 1, null));
                mData.add(new SettingMenuItem(R.string.skype_remind, getString(R.string.skype_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SKYPE, "0"), 1, R.drawable.device_switch_skype, true, 1, null));
                mData.add(new SettingMenuItem(R.string.kakao_talk_remind, getString(R.string.kakao_talk_remind), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_KAKAOTALK, "0"), 1, R.drawable.device_switch_talk, true, 1, null));
                mData.add(new SettingMenuItem(R.string.linkdedIn, getString(R.string.linkdedIn), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_LINKEDIN, "0"), 1, R.drawable.device_switch_linkedin, true, 1, null));
                mData.add(new SettingMenuItem(R.string.snap_chat, getString(R.string.snap_chat), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SNAPCHAT, "0"), 1, R.drawable.ic_snap_chat, true, 1, null));
                mData.add(new SettingMenuItem(R.string.tiktok, getString(R.string.tiktok), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TIKTOK, "0"), 1, R.drawable.icon_tk, true, 1, null));
                mData.add(new SettingMenuItem(R.string.msg_telegram, getString(R.string.msg_telegram), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TELEGRAM, "0"), 1, R.drawable.icon_msg_telegram, true, 1, null));
                mData.add(new SettingMenuItem(R.string.msg_ok_ru, getString(R.string.msg_ok_ru), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_OK_RU, "0"), 1, R.drawable.icon_msg_ok_ru, true, 1, null));
                mData.add(new SettingMenuItem(R.string.msg_vk, getString(R.string.msg_vk), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_VK, "0"), 1, R.drawable.icon_msg_vk, true, 1, null));
                mData.add(new SettingMenuItem(R.string.msg_ten_chat, getString(R.string.msg_ten_chat), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TEN_CHAT, "0"), 1, R.drawable.icon_msg_ten_chat, true, 1, null));
                mData.add(new SettingMenuItem(R.string.msg_viber_chat, getString(R.string.msg_viber_chat), SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_VIBER, "0"), 1, R.drawable.icon_msg_viber, true, 1, null));
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
        String state;
        for (int i = 0; i < mData.size(); i++) {
            SettingMenuItem data = mData.get(i);
            switch (i) {
                case 0:
                    String callState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_CALL, "0");
                    if (!callState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(callState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 1:
                    String SMSState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SMS, "0");
                    if (!SMSState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(SMSState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 2:
                    String WECHATState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_WECHAT, "0");
                    if (!WECHATState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(WECHATState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 3:
                    String QQState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_QQ, "0");
                    if (!QQState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(QQState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 4:
                    String FaceBookState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_FACEBOOK, "0");
                    if (!FaceBookState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(FaceBookState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 5:
                    String TwitterState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TWITTER, "0");
                    if (!TwitterState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(TwitterState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 6:
                    String LineState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_LINE, "0");
                    if (!LineState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(LineState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 7:
                    String WhatsappState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_WHATSAPP, "0");
                    if (!WhatsappState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(WhatsappState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 8:
                    String INSTAGRAMState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_INSTAGRAM, "0");
                    if (!INSTAGRAMState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(INSTAGRAMState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 9:
                    String SkypeState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SKYPE, "0");
                    if (!SkypeState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(SkypeState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 10:
                    String KakaoTalkState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_KAKAOTALK, "0");
                    if (!KakaoTalkState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(KakaoTalkState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 11:
                    String linkdedInState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_LINKEDIN, "0");
                    if (!linkdedInState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(linkdedInState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 12:
                    String SNAPCHATState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_SNAPCHAT, "0");
                    if (!SNAPCHATState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(SNAPCHATState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 13:
                    String tikTokState = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TIKTOK, "0");
                    if (!tikTokState.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(tikTokState);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 14:
                    state = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TELEGRAM, "0");
                    if (!state.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(state);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 15:
                    state = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_OK_RU, "0");
                    if (!state.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(state);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 16:
                    state = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_VK, "0");
                    if (!state.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(state);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 17:
                    state = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_TEN_CHAT, "0");
                    if (!state.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(state);
                        adapter.notifyItemChanged(i);
                    }
                    break;
                case 18:
                    state = SaveKeyValues.getStringValues(SPKey.MSG_NOTIFY_STATE_VIBER, "0");
                    if (!state.equals(data.getNameInfo())) {//有变化的时候才进行更新UI
                        data.setNameInfo(state);
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
                        key = SPKey.MSG_NOTIFY_STATE_CALL;
                    } else if (item.Id == R.string.sms_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_SMS;
                    } else if (item.Id == R.string.wechat_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_WECHAT;
                    } else if (item.Id == R.string.qq_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_QQ;
                    } else if (item.Id == R.string.face_book_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_FACEBOOK;
                    } else if (item.Id == R.string.twitter_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_TWITTER;
                    } else if (item.Id == R.string.skype_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_SKYPE;
                    } else if (item.Id == R.string.line_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_LINE;
                    } else if (item.Id == R.string.whatsapp_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_WHATSAPP;
                    } else if (item.Id == R.string.instagram_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_INSTAGRAM;
                    } else if (item.Id == R.string.kakao_talk_remind) {
                        key = SPKey.MSG_NOTIFY_STATE_KAKAOTALK;
                    } else if (item.Id == R.string.linkdedIn) {
                        key = SPKey.MSG_NOTIFY_STATE_LINKEDIN;
                    } else if (item.Id == R.string.vibrate_setting) {
                        key = SPKey.MSG_NOTIFY_STATE_VIBER;
                    } else if (item.Id == R.string.lift_screen) {
                        key = "BRIGHTState";
                    } else if (item.Id == R.string.sleep_monitoring) {
                        key = "SLEEPState";
                    } else if (item.Id == R.string.automatic_heart_rate) {
                        key = "HEARTState";
                    } else if (item.Id == R.string.lr_hand_wearing) {
                        key = "HANDState";
                    } else if (item.Id == R.string.snap_chat) {
                        key = SPKey.MSG_NOTIFY_STATE_SNAPCHAT;
                    } else if (item.Id == R.string.tiktok) {
                        key = SPKey.MSG_NOTIFY_STATE_TIKTOK;
                    } else if (item.Id == R.string.msg_telegram) {
                        key = SPKey.MSG_NOTIFY_STATE_TELEGRAM;
                    } else if (item.Id == R.string.msg_ok_ru) {
                        key = SPKey.MSG_NOTIFY_STATE_OK_RU;
                    } else if (item.Id == R.string.msg_vk) {
                        key = SPKey.MSG_NOTIFY_STATE_VK;
                    } else if (item.Id == R.string.msg_ten_chat) {
                        key = SPKey.MSG_NOTIFY_STATE_TEN_CHAT;
                    }else if (item.Id == R.string.msg_viber_chat) {
                        key = SPKey.MSG_NOTIFY_STATE_VIBER;
                    }
                    setSendBeforeValue(key, 1, SaveKeyValues.getStringValues(key, "1"));
                    SaveKeyValues.putStringValues(key, val + "");
                    Logdebug(TAG, "选中开关:" + item.Name + "--开关状态:" + SaveKeyValues.getStringValues(key, ""));
                    setMessageTofitpro((key == "HANDState") ? true : false);
                    /**
                     * APP端开启抬腕亮屏功能后，将振动关闭或打开，
                     * 此时抬腕亮屏功能不能再实现，需要app重新设置一下
                     */
                    if (StringUtils.equals(key, "SHOCKState")) {
                        resetBrightScreen();
                    }
                }
            }
        });
        rlv.setLayoutManager(new LinearLayoutManager(this));
        rlv.setAdapter(adapter);
        byte CommandKey = (byte) 0x06;
        DataToUI();
        if (Id == R.string.push_setting_txt) {
            mHandler.postDelayed(() -> notificationSettings(), 1000);
            CommandKey = (byte) 0x04;
        }
        if (SDKTools.BleState == 1) {
            SDKTools.mService.commandPoolWrite(getSetInfoByKey(CommandKey), "获取个人信息");
        }
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_LOADDING, 2000);
    }

    private void resetBrightScreen() {
        Integer is_siesta = SaveKeyValues.getIntValues("screen_status", 0);
        if (is_siesta == 1) {
            SDKTools.mService.commandPoolWrite(getBrightScreenValue(), "设置抬腕亮屏");
        }
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
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(MessageSettingActivity.this).setMessage(getString(R.string.setting)).setCancelable(false);
        Constants.dialog = mBuilder.create(true, 5000);
        byte[] uinfo;
        String desc;
        if (is_hand) {
            uinfo = getSetHandSideValue();//获取设置左右手佩戴协议
            desc = "设置左右手佩戴";
        } else {
            if (StringUtils.equals(Title, UIHelper.getString(R.string.push_setting_txt))) {
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
        if (leReceiver != null) leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_HIDE_LOADDING);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leReceiver != null) leReceiver.registerLeReceiver();
    }

    public int getId() {
        return Id;
    }
}
