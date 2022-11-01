package xfkj.fitpro.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.MainActivity;
import xfkj.fitpro.activity.SleepItemActivity;
import xfkj.fitpro.activity.StepItemActivity;
import xfkj.fitpro.activity.UpdateOtaActivity;
import xfkj.fitpro.adapter.SettingAdapter;
import xfkj.fitpro.base.BaseFragment;
import xfkj.fitpro.view.SettingMenuItem;

import static com.legend.bluetooth.fitprolib.bluetooth.ByteUtil.hexStringToBytes;


public class BluetoothFragment extends BaseFragment
{
    private String TAG = "BluetoothFragment";
    private View view;//界面的布局
    private Context context;

    private RecyclerView rlv;
    private ArrayList<SettingMenuItem> mData = new ArrayList<SettingMenuItem>();

    private LeReceiver leReceiver;
    private boolean is_click = false;

    private SettingAdapter adapter = null;

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what)
            {
                case Profile.MsgWhat.what2:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
//

        view = inflater.inflate(R.layout.fragment_bluetooth, null);
        initView();

        //开始连接蓝牙监听服务
        return view;
    }   


    /**
     * 初始化控件
     */
    private void initView()
    {
     //   byte val0 []  = hexStringToBytes("ab001905010300142566000401ae000201cd000101d6000201ec0003");
    //    Constant.mService.onResValueToData(val0);
        rlv = view.findViewById(R.id.rlv);
        mData.clear();
        mData.add(new SettingMenuItem(R.string.app_version,getString(R.string.app_version), SDKTools.getLocalVersionName(context), 0, R.drawable.app_version, true, 1, null));
        mData.add(new SettingMenuItem(R.string.upgrade_txt,"固件升级工具", "", R.drawable.icon_set_more, R.drawable.device_update, 1, true, UpdateOtaActivity.class));
        mData.add(new SettingMenuItem(R.string.upgrade_txt,"固件在线升级", "", R.drawable.icon_set_more, R.drawable.device_update, 1, false, UpdateOtaActivity.class));
        // mData.add(new SettingMenuItem(R.string.language,getString(R.string.language), "", R.drawable.icon_set_more, R.drawable.contract_qq, false, 1, AboutActivity.class));
        mData.add(new SettingMenuItem(0,"", 0, 0, 3, null));
        mData.add(new SettingMenuItem(R.string.back_to_welcome,getString(R.string.back_to_welcome), "", R.drawable.icon_set_more, R.drawable.more_back, false, 1, MainActivity.class));
        if (SDKTools.isDeubg)
        {
			mData.add(new SettingMenuItem(0,"", 0, 0, 3, null));
            mData.add(new SettingMenuItem(1,"打开测试数据", "", R.drawable.icon_set_more, R.drawable.sb1, 1, true, null));
            mData.add(new SettingMenuItem(2,"关闭测试数据", "", R.drawable.icon_set_more, R.drawable.sb1, 1, true, null));
        //    mData.add(new SettingMenuItem(3,"查看日志", "", R.drawable.icon_set_more, R.drawable.sb1, true, 1, LogsActivity.class));
            mData.add(new SettingMenuItem(4,"打开15分钟步数列表", "", R.drawable.icon_set_more, R.drawable.sb1, true, 1, StepItemActivity.class));
            mData.add(new SettingMenuItem(5,"打开睡眠数据列表", "", R.drawable.icon_set_more, R.drawable.sb1, true, 1, SleepItemActivity.class));
            mData.add(new SettingMenuItem(6,"发送命令", "", R.drawable.icon_set_more, R.drawable.sb1, 1, true, null));
            mData.add(new SettingMenuItem(7,"接收数据命令", "", R.drawable.icon_set_more, R.drawable.sb1, 1, true, null));
        }
        leReceiver = new LeReceiver(context, mHandler);

        adapter = new SettingAdapter(context, mData);
        adapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {
                SettingMenuItem item = mData.get(position);
                if(is_click == true){
                    return;//5秒之内不能连续点击
                }
                is_click = true;

                mHandler.postDelayed(new Runnable()
                {//
                    @Override
                    public void run()
                    {
                        is_click = false;
                    }
                }, 5000);
                if (item.Id  == 1)
                {
                    if (SDKTools.BleState != 1)
                    {
                        Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SDKCmdMannager.getTestData();
                } else if (item.Id == 2)
                {
                    if (SDKTools.BleState != 1)
                    {
                        Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SDKCmdMannager.closeTestData();
                } else if (item.Id == 6)
                {
                    if (SDKTools.BleState != 1)
                    {
                        Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请输入命令");    //设置对话框标题
                    final EditText edit = new EditText(context);
                    builder.setView(edit);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            byte val []  = hexStringToBytes(edit.getText().toString());
                            if(val != null && val.length>0 && val[0] == (byte) 0xab)
                                SDKCmdMannager.sendCustomOrder(val);
                        }
                    });
                    builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                    AlertDialog dialog = builder.create();  //创建对话框
                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                } else if (item.Id == 7)
                {
                    if (SDKTools.BleState != 1)
                    {
                        Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请输入数据");    //设置对话框标题
                    final EditText edit = new EditText(context);
                    builder.setView(edit);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            byte val []  = hexStringToBytes(edit.getText().toString());
                            if(val != null && val.length>0 && val[0] == (byte) 0xab)
                                SDKTools.mService.onResValueToData(val);
                        }
                    });
                    builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                    AlertDialog dialog = builder.create();  //创建对话框
                    dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                }  else if(item.ClassObj != null){
                    Intent intent = new Intent(context, item.ClassObj);
                    startActivity(intent);
                }
                return;
            }
        });
        rlv.setLayoutManager(new LinearLayoutManager(context));
//        rlv.addItemDecoration(new GridSpanline1(context, 0, 1, mData));
        rlv.setAdapter(adapter);

		/*
		
		《环趣》安卓版广告资料:
		应用id: ca-app-pub-5581379969959694~4707769496
		banner id: ca-app-pub-5581379969959694/5083599047
		插页式id: ca-app-pub-5581379969959694/4871421026
		*/

    }

    public  void onResume() {
        super.onResume();
        is_click = false;
		if(leReceiver != null)
        leReceiver.registerLeReceiver();
    }

    public void onPause() {
        super.onPause();
		if(leReceiver != null)
        leReceiver.unregisterLeReceiver();
    }

}
