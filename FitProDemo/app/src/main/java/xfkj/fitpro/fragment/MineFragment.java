package xfkj.fitpro.fragment;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.utils.BleUtils.refreshBleAppFromSystem;
import static com.legend.bluetooth.fitprolib.utils.BleUtils.releaseAllScanClient;
import static com.legend.bluetooth.fitprolib.utils.BleUtils.setLeServiceEnable;
import static xfkj.fitpro.application.MyApplication.getRequset;
import static xfkj.fitpro.application.MyApplication.setLanguage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.ReceiveData;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.BuildConfig;
import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.activity.AboutActivity;
import xfkj.fitpro.activity.AlarmActivity;
import xfkj.fitpro.activity.BrightScreenActivity;
import xfkj.fitpro.activity.CameraActivity;
import xfkj.fitpro.activity.DisturbSwitchActivity;
import xfkj.fitpro.activity.LongsitWarnActivity;
import xfkj.fitpro.activity.MainActivity;
import xfkj.fitpro.activity.MessageSettingActivity;
import xfkj.fitpro.activity.PlusCmdActivity;
import xfkj.fitpro.activity.SetInfoActivity;
import xfkj.fitpro.activity.UinfoActivity;
import xfkj.fitpro.activity.UpdateOtaActivity;
import xfkj.fitpro.activity.test.BluetoothCommandActivity;
import xfkj.fitpro.activity.test.OtherBluetoothDebugActivity;
import xfkj.fitpro.activity.watchTheme1.ClockDialListActivity;
import xfkj.fitpro.adapter.SettingAdapter;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseFragment;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.event.ClockDialInfoEvent;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.view.SettingMenuItem;

public class MineFragment extends BaseFragment {
    private static final int CHANGE = 200;
    private View view;//界面的布局
    private Context context;
    private SettingAdapter adapter;

    private RecyclerView rlv;

    private LeReceiver leReceiver;
    private String TAG = "MineFragment";
    private String bluetooth_name = "", bluetooth_battery = "";
    AlertDialog dialog;

    private ArrayList<SettingMenuItem> mData = new ArrayList<SettingMenuItem>();

    private TextView b_statue, b_version, b_battery;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what) {
                case Profile.MsgWhat.what2:
                    DataToUI();
                    break;
                case Profile.MsgWhat.what4:
                    bluetooth_battery = SaveKeyValues.getIntValues("battery", 0) + "%";
                    DataToUI();
                    break;
                case Profile.MsgWhat.what10:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            do_del();
                            SDKTools.BleState = 0;
                        }
                    }, 1000);
                    break;
                case Profile.MsgWhat.what13:

                    break;
                case Profile.MsgWhat.what38:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Constants.dialog != null) {
                                Constants.dialog.dismiss();
                            }
                            //        Toast toast = Toast.makeText(context, getString(R.string.set), Toast.LENGTH_SHORT);
                            //        toast.setGravity(Gravity.CENTER, 0, 0);
                            //        toast.show();
                        }
                    }, 1000);
                    break;
                case Profile.MsgWhat.what33:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Constants.dialog != null) {
                                Constants.dialog.dismiss();
                            }
                            Toast toast = Toast.makeText(context, getString(R.string.sync_suc), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }, 1000);
                    break;
                case Profile.MsgWhat.what14:
                default:
                    break;
            }
        }
    };

    public void do_del() {
        if (Constants.dialog != null) {
            Constants.dialog.dismiss();
        }
        Toast toast = Toast.makeText(context, getString(R.string.del_device_txt), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        MyApplication.resetTables();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            refreshBleAppFromSystem(getContext(), getContext().getPackageName());
            //   setLeServiceEnable(true);
            releaseAllScanClient();
        }
        if (SDKTools.mService != null) {
            SDKTools.mService.close();
        }
        /*
        String addr = SaveKeyValues.getStringValues("bluetooth_address", "");
        String url = Constant.wx_sport_url + "?dtype=unbind&addr=" + addr;
        String res = getRequset(url);
        Logdebug("wxSport", res);
        */

        mHandler.postDelayed(new Runnable() {//延迟2秒后跳转
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setLeServiceEnable(false);
                }
                startActivity(new Intent(context, MainActivity.class));
            }
        }, 1000);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, null);
        rlv = view.findViewById(R.id.rlv);
        b_battery = view.findViewById(R.id.ble_battery);
        b_version = view.findViewById(R.id.ble_version);
        b_statue = view.findViewById(R.id.ble_state);
        (view.findViewById(R.id.img_test)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BuildConfig.DEBUG) {
                    ReceiveData.getInstance().testParse2(ConvertUtils.hexString2Bytes("cd0011150103000c273000020528000205640001"));
                }
                return true;
            }
        });

        adapter = new SettingAdapter(context, mData);
        adapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

                GotoNext(position);

            }
        });
        leReceiver = new LeReceiver(context, mHandler);

        rlv.setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });
        rlv.setAdapter(adapter);
        //解决数据加载不完的问题
        rlv.setNestedScrollingEnabled(false);
        rlv.setHasFixedSize(true);
        //解决数据加载完成后, 没有停留在顶部的问题
        rlv.setFocusable(false);
        bluetooth_battery = SaveKeyValues.getIntValues("battery", 0) + "";
        DataToUI();
        SDKCmdMannager.getTargetSteps();
        return view;
    }

    private void DataToUI() {
        mData.clear();
        bluetooth_name = SaveKeyValues.getStringValues("bluetooth_name", "");
        String deviceVersion = SaveKeyValues.getStringValues("deviceVersion", "");
        String show_ota = SaveKeyValues.getStringValues("show_ota", "0");
        String show_battery = SaveKeyValues.getStringValues("show_battery", "0");

        b_version.setText(getString(R.string.device_version_txt) + ":" + deviceVersion + "");
        if (SDKTools.BleState > 0) {
            b_statue.setText(bluetooth_name);
        } else {
            b_statue.setText(getString(R.string.unconnected));
        }
        if (show_battery.equals("1")) {
            b_battery.setText(getString(R.string.device_electric_txt) + ":" + bluetooth_battery + "%");
        } else {
            b_battery.setVisibility(View.GONE);
        }
        /*
        mData.add(new SettingMenuItem(R.string.device_name,getString(R.string.device_name), bluetooth_name, -1, R.drawable.device_name, 1, true, null));
        mData.add(new SettingMenuItem(R.string.device_state_txt,getString(R.string.device_state_txt), (Constant.BleState>0?getString(R.string.connectd_txt):getString(R.string.ununited_txt)), (Constant.BleState>0?-1:R.drawable.icon_set_more), R.drawable.device_state, 1, true, null));
        mData.add(new SettingMenuItem(R.string.device_version_txt,getString(R.string.device_version_txt), (Constant.BleState>0?deviceVersion:""), -1, R.drawable.device_version, 1, (show_battery.equals("1") || show_ota.equals("1"))?true:false, null));
        if(show_battery.equals("1")){
            mData.add(new SettingMenuItem(R.string.device_electric_txt,getString(R.string.device_electric_txt), (Constant.BleState>0?bluetooth_battery:""), -1, R.drawable.device_battery, 1, show_ota.equals("1")?true:false, null));
        }
        if(show_ota.equals("1")){
            mData.add(new SettingMenuItem(R.string.upgrade_txt,getString(R.string.upgrade_txt), "", R.drawable.icon_set_more, R.drawable.device_update, 1, false, UpdateOtaActivity.class));
        }*/

        mData.add(new SettingMenuItem(R.string.upgrade_txt, getString(R.string.upgrade_txt), "", R.drawable.icon_set_more, R.drawable.device_update, 1, false, UpdateOtaActivity.class));
        mData.add(new SettingMenuItem(R.string.find_device_txt, getString(R.string.find_device_txt), "", R.drawable.icon_set_more, R.mipmap.su_search_equipment_icon, 1, true, null));
        mData.add(new SettingMenuItem(R.string.user_profile_txt, getString(R.string.user_profile_txt), "", R.drawable.icon_set_more, R.mipmap.su_personal_information_icon, 1, true, UinfoActivity.class));
        mData.add(new SettingMenuItem(R.string.target_txt, getString(R.string.trget_txt), (SaveKeyValues.getIntValues("step", 5000) + " " + getString(R.string.step)), R.drawable.icon_set_more, R.mipmap.su_goal_setting_icon, 1, true, SetInfoActivity.class));
        mData.add(new SettingMenuItem(R.string.take_photos_txt, getString(R.string.take_photos_txt), "", R.drawable.icon_set_more, R.mipmap.su_camera_icon, 1, true, CameraActivity.class));
        mData.add(new SettingMenuItem(R.string.alarm_set_txt, getString(R.string.alarm_set_txt), "", R.drawable.icon_set_more, R.mipmap.su_alarm_clock_icon, 1, true, AlarmActivity.class));
        mData.add(new SettingMenuItem(R.string.long_sit_txt, getString(R.string.long_sit_txt), "", R.drawable.icon_set_more, R.mipmap.su_sedentary_icon, 1, true, LongsitWarnActivity.class));
        mData.add(new SettingMenuItem(R.string.bright_screen_title, getString(R.string.bright_screen_title), "", R.drawable.icon_set_more, R.mipmap.su_bright_screen_icon, 1, true, BrightScreenActivity.class));
        mData.add(new SettingMenuItem(R.string.disturb_title, getString(R.string.disturb_title), "", R.drawable.icon_set_more, R.mipmap.su_no_disturb_icon, 1, false, DisturbSwitchActivity.class));
        //    mData.add(new SettingMenuItem(R.string.sleep_switch_title,getString(R.string.sleep_switch_title), "", R.drawable.icon_set_more, R.drawable.disturb_switch_icon, 1, true, SleepSwitchActivity.class));
        //    mData.add(new SettingMenuItem(R.string.heart_auto_title,getString(R.string.heart_auto_title), "", R.drawable.icon_set_more, R.drawable.heart_auto_icon, 1, false, HeartAutoActivity.class));

        mData.add(new SettingMenuItem(0, "", 0, 0, 3, null));
        mData.add(new SettingMenuItem(R.string.push_setting_txt, getString(R.string.push_setting_txt), "", R.drawable.icon_set_more, R.mipmap.su_app_push_icon, 1, true, MessageSettingActivity.class));
        mData.add(new SettingMenuItem(R.string.other_setting_txt, getString(R.string.other_setting_txt), "", R.drawable.icon_set_more, R.mipmap.su_other_icon, 1, false, MessageSettingActivity.class));
        //mData.add(new SettingMenuItem(R.string.language,getString(R.string.language), "", R.drawable.icon_set_more, R.drawable.language, 1, false, null));

        mData.add(new SettingMenuItem(0, "", 0, 0, 3, null));
        mData.add(new SettingMenuItem(R.string.about, getString(R.string.about), "", R.drawable.icon_set_more, R.mipmap.su_about_icon, false, 1, AboutActivity.class));

        //    mData.add(new SettingMenuItem(0,"", 0, 0, 3, null));
        //    mData.add(new SettingMenuItem(R.string.wx_sport,getString(R.string.wx_sport), "", R.drawable.icon_set_more, R.drawable.device_about_us, false, 1, WxSportActivity.class));

        mData.add(new SettingMenuItem(0, "", 0, 0, 3, null));
        //    mData.add(new SettingMenuItem(R.string.sync_info,getString(R.string.sync_info), "", R.drawable.icon_set_more, R.drawable.sync_icon, 1, true, null));
        mData.add(new SettingMenuItem(R.string.device_reset_txt, getString(R.string.device_reset_txt), "", R.drawable.icon_set_more, R.mipmap.su_reset_icon, 2, true, null));
        mData.add(new SettingMenuItem(R.string.del_device_txt, getString(R.string.del_device_txt), "", R.drawable.icon_set_more, R.mipmap.su_unlock_icon, 2, false, null));
        mData.add(new SettingMenuItem(R.string.get_more_funcion, getString(R.string.get_more_funcion), "", R.drawable.icon_set_more, R.mipmap.su_other_icon, 1, true, PlusCmdActivity.class));
        mData.add(new SettingMenuItem(R.string.weather_debug, getString(R.string.weather_debug), "", R.drawable.icon_set_more, R.mipmap.su_other_icon, 1, true, BluetoothCommandActivity.class));
        mData.add(new SettingMenuItem(R.string.watch_theme, getString(R.string.watch_theme), "", R.drawable.icon_set_more, R.mipmap.su_reset_icon, 2, true, null));
        mData.add(new SettingMenuItem(R.string.ble_other_func_debug, getString(R.string.ble_other_func_debug), "", R.drawable.icon_set_more, R.mipmap.su_other_icon, 1, true, OtherBluetoothDebugActivity.class));



        adapter.notifyDataSetChanged();

    }

    /**
     * 跳转到下一个界面
     *
     * @param position
     */
    private void GotoNext(int position) {
        //
        SettingMenuItem item = mData.get(position);
        if (item.MenuType != 1 || item.ClassObj == null) {
            if (item.Id == R.string.find_device_txt) {
                if (!SDKCmdMannager.isConnected()) {
                    Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                SDKCmdMannager.findWatch();
                Toast.makeText(context, getString(R.string.find_device_txt), Toast.LENGTH_SHORT).show();
            } else if (item.Id == R.string.sync_info) {
                if (!SDKCmdMannager.isConnected()) {
                    Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                SDKCmdMannager.getDeviceInfo();
                LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context)
                        .setMessage(getString(R.string.syncing))
                        .setCancelable(false);
                Constants.dialog = mBuilder.create(true, 5000);
            } else if (item.Id == R.string.language) {
                //创建单选框
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);//"繁體中文",
                builder.setSingleChoiceItems(new String[]{"简体中文", "ENGLISH"}, SaveKeyValues.getIntValues("language", 0),
                        new DialogInterface.OnClickListener() {
                            //点击单选框某一项以后
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SaveKeyValues.putIntValues("language", i);
                                if (SDKCmdMannager.isConnected() && SDKTools.mService != null) {
                                    SDKCmdMannager.setLanguage(i);
                                    LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context)
                                            .setMessage(getString(R.string.setting))
                                            .setCancelable(false);
                                    Constants.dialog = mBuilder.create(true, 5000);
                                }
                                setLanguage();
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
            } else if (item.Id == R.string.del_device_txt) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(getString(R.string.tips_txt));
                dialog.setMessage(getString(R.string.confirm_remove_device_txt));
                dialog.setNeutralButton(getString(R.string.cancel_txt), null);
                dialog.setPositiveButton(getString(R.string.confirm_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveKeyValues.deleteAllValues();
                        if (SDKTools.BleState == 1 && SDKTools.mService != null) {
                            SDKCmdMannager.unbondWatch();
                            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context).setCancelable(false);
                            Constants.dialog = mBuilder.create(true, 5000);
                        } else {
                            SDKTools.mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    do_del();
                                }
                            }, 1000);
                        }
                    }
                });
                dialog.show();

            } else if (item.Id == R.string.device_reset_txt) {
                if (!SDKCmdMannager.isConnected()) {
                    Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(getString(R.string.tips_txt));
                dialog.setMessage(getString(R.string.confirm_reset_device_txt));
                dialog.setNeutralButton(getString(R.string.cancel_txt), null);
                dialog.setPositiveButton(getString(R.string.confirm_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SDKCmdMannager.resetWatch();
                        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context).setCancelable(false);
                        Constants.dialog = mBuilder.create(true, 1000);
                        final String address = SaveKeyValues.getStringValues("bluetooth_address", "");
                        String url = SDKTools.wx_sport_url + "?dtype=unbind&addr=" + address;
                        String res = getRequset(url);
                        Logdebug("wxSport", res);
                        SDKTools.mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SDKTools.mService.close();
                                SDKTools.BleState = 0;
                                SaveKeyValues.deleteAllValues();
                                SaveKeyValues.putStringValues("bluetooth_address", address);
                            }
                        }, 2000);
                    }
                });
                dialog.show();
            } else if (item.Id == R.string.get_more_funcion) {

            } else if (item.Id == R.string.watch_theme) {
                ClockDialInfoBody info = CacheHelper.getClockDialInfo();
                if (info != null) {
                    ActivityUtils.startActivity(ClockDialListActivity.class);
                }else {
                    if (!SDKCmdMannager.getClockDialInfo()) {
                        ToastUtils.showShort(R.string.unconnected);
                    } else {
                        startTimeOut(R.string.watch_theme, 5 * 1000);
                        DialogHelper.showDialog(getActivity(), getString(R.string.query_clock_dial_info), false);
                    }
                }
            }
            return;
        }

        Intent intent = new Intent(context, item.ClassObj);
        intent.putExtra("Title", item.Name);
        intent.putExtra("type", item.Id);


        if (item.Id == R.string.target_txt) {
            String str = item.getNameInfo().split(" ")[0];
            int value = Integer.parseInt(str);
            intent.putExtra("value", value);
            intent.putExtra("showbtn", 1);
        } else if (item.Name.equals(getString(R.string.device_state_txt))) {
            if (SDKTools.BleState > 0) {
                return;
            }
        } else if (item.Id == R.string.upgrade_txt) {
            if (!SDKCmdMannager.isConnected()) {
                Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (item.Id == R.string.wx_sport) {
            if (bluetooth_name.equals("")) {
                Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        startActivityForResult(intent, CHANGE);
    }

    /**
     * 返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE && resultCode == Activity.RESULT_OK) {
            //重新刷新UI
            DataToUI();
        }
    }

    public void onResume() {
        super.onResume();
        if (leReceiver != null)
            leReceiver.registerLeReceiver();
    }

    public void onPause() {
        super.onPause();
        if (leReceiver != null)
            leReceiver.unregisterLeReceiver();
    }

    @Override
    public void onMessageEvent(Object event) {
        super.onMessageEvent(event);
        if (event instanceof ClockDialInfoEvent) {
            DialogHelper.hideDialog();
            if (getDelayWhats().contains(R.string.watch_theme)) {
                ActivityUtils.startActivity(ClockDialListActivity.class);
            }
            stopTimeOut(R.string.watch_theme);
        }
    }

    @Override
    protected void localHandleMessage(Message msg) {
        super.localHandleMessage(msg);
        if(msg.what == R.string.watch_theme){
            ToastUtils.showShort(R.string.query_device_info_over);
            DialogHelper.hideDialog();
        }
    }
}
