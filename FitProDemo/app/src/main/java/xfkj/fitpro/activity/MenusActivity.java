package xfkj.fitpro.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.PermissionUtils;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.SleepDetailsModel;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.fragment.BluetoothFragment;
import xfkj.fitpro.fragment.MineFragment;
import xfkj.fitpro.fragment.SportFragment;
import xfkj.fitpro.utils.LoadingDailog;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static xfkj.fitpro.application.MyApplication.removeALLActivity_;
import static xfkj.fitpro.service.NotifyService.showNotifyPermissionDialog;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MenusActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener
{
    private String TAG = "MenusActivity";


    //变量
    private long exitTime;//第一次单机退出键的时间
    private int load_values;//判断加载fragment的变量
    //控件
    private RadioGroup radioGroup;//切换按钮的容器
    private RadioButton sport_btn, state_btn, mine_btn;//切换按钮
    //碎片 //当前 运动 睡眠 心率 我的 蓝牙
    private Fragment currentFragment,sportFragment,mineFragment,bluetoothFragment;

    private Integer battery = 0;

    private LeReceiver leReceiver;
    private int enterBluetooth = 0;

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            Logdebug(TAG, TAG+"----state-------["+map.get("state")+"]-----msg.what----"+msg.what);
            switch (msg.what)
            {
                case Profile.MsgWhat.what2:
                    if (map.get("state").equals("0"))
                    {//断开连接
                   //     batteryProgress.setProgress(0,getString(R.string.un_connect_txt));//
                    state_btn.setChecked(false);
                    } else if (map.get("state").equals("1"))
                    {//连接成功
                  //      battery = SaveKeyValues.getIntValues("battery", 0);
                  //      battery = battery<=0?1:battery;
                 //       batteryProgress.setProgress(battery,getString(R.string.connectd_txt));
                        state_btn.setChecked(true);
                    } else if (map.get("state").equals(-1))
                    {//连接失败
                  //      batteryProgress.setProgress(0,getString(R.string.un_connect_txt));//
                        state_btn.setChecked(false);
                    } else if (map.get("state").equals("3"))
                    {//连接失败
                    //    batteryProgress.setProgress(0,getString(R.string.connecting_txt));
                        state_btn.setChecked(false);
                    }else{
                     //   batteryProgress.setProgress(0,getString(R.string.un_connect_txt));//
                        state_btn.setChecked(false);
                    }
                    break;
                case Profile.MsgWhat.what4:
                 //   battery = (Integer) Integer.valueOf(map.get("battery").toString());
                //    battery = battery<=0?1:battery;
                 //   batteryProgress.setProgress(battery,getString(R.string.connectd_txt));//
                    state_btn.setChecked(true);
                    break;
                case Profile.MsgWhat.what80:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MenusActivity.this);
                    dialog.setTitle("温馨提示");
                    dialog.setMessage("手环正在找手机");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SDKCmdMannager.pauseRingtone();
                        }
                    });
                    dialog.show();
                    break;
                case Profile.MsgWhat.what11:
                    if(SDKTools.isGetDataLoading){
                        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(MenusActivity.this)
                                .setMessage(getString(R.string.getdatas))
                                .setCancelable(false);
                        Constants.dialog = mBuilder.create(true,15000);
                    }
                    break;
                case Profile.MsgWhat.what12:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Constants.dialog != null){
                                Constants.dialog.dismiss();
                            }
                            SDKTools.isGetDataLoading = false;
                        }
                    },1000);
                    break;
                case Profile.MsgWhat.what72://进入拍照页面
                    startActivity(new Intent(MenusActivity.this, CameraActivity.class));
                    break;
                case Profile.MsgWhat.what90://sleep data item
                    SleepDetailsModel sleepItem = (SleepDetailsModel) map.get("sleepData");
                    Log.e(TAG, "=====sleep data item:" + sleepItem.toString());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置标题
     */
    @Override
    protected void setActivityTitle()
    {
    }


    /**
     * 初始化界面
     */
    @Override
    protected void getLayoutToView()
    {
        setContentView(R.layout.activity_menus);
    }

    /**
     * 初始化相关变量
     */
    @Override
    protected void initValues()
    {
        initTitle();
        //实例化相关碎片
        sportFragment = new SportFragment();
        mineFragment = new MineFragment();
        bluetoothFragment = new BluetoothFragment();
        //初始化界面

        setTitle(getString(R.string.app_name));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        switchFragment(sportFragment).commit();
        leReceiver = new LeReceiver(MenusActivity.this, mHandler);
    }

    @Override
    protected void initViews()
    {
        //初始化标题
        initTitle();

        radioGroup = findViewById(R.id.ui_btn_group);
        sport_btn = findViewById(R.id.sport_btn);
        state_btn = findViewById(R.id.state_btn);
        mine_btn = findViewById(R.id.mine_btn);
        state_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (SDKTools.BleState>0){
                    state_btn.setChecked(true);
                }else{
                    state_btn.setChecked(false);
                }
                enterBluetooth += 1;
                if(enterBluetooth >= 8){
                    enterBluetooth = 0;
                    gotoBluetooth();
                }
            }
        });
        initButton(sport_btn);
        initButton(mine_btn);

        battery = SaveKeyValues.getIntValues("battery", 0);
        PermissionUtils.permission(Manifest.permission.POST_NOTIFICATIONS).request();

    }
    public void initButton(RadioButton btn)
    {
        Drawable[] drawables = btn.getCompoundDrawables();//通过RadioButton的getCompoundDrawables()方法，拿到图片的drawables,分别是左上右下的图片
        drawables[1].setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.x25), getResources().getDimensionPixelSize(R.dimen.x25));
        btn.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    @Override
    protected void setViewsListener()
    {
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void setViewsFunction()
    {
    }

    public void gotoBluetooth()
    {
		setTitle(getString(R.string.bluetooth_title));
		findViewById(R.id.right_btn).setVisibility(View.GONE);
        radioGroup.clearCheck();
        switchFragment(bluetoothFragment).commit();
        enterBluetooth = 0;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
		if(leReceiver != null)
        leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void onResume()
    {
        simulateProgress();
		if(leReceiver != null)
        leReceiver.registerLeReceiver();
        showNotifyPermissionDialog(MenusActivity.this);
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void simulateProgress()
    {
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        enterBluetooth = 0;
        switch (checkedId)
        {
            case R.id.sport_btn://运动
                setTitle(getString(R.string.app_name));
                switchFragment(sportFragment).commit();
                break;
            case R.id.mine_btn://更多
                setTitle("");
                findViewById(R.id.right_btn).setVisibility(View.GONE);
                switchFragment(mineFragment).commit();
                break;
            default:
                break;
        }
        findViewById(R.id.left_btn).setVisibility(View.GONE);
    }

    /**
     * 按两次退出按钮退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            // System.currentTimeMillis()无论何时调用，肯定大于2000
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(this, getString(R.string.exit_tips), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else
            {

            //    Intent intent = new Intent(this, AppStatusService.class);
             //   stopService(intent);
                removeALLActivity_();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Fragment优化
    public FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.frag_home, targetFragment,targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }


}
