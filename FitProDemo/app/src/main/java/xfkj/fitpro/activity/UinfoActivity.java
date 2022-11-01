package xfkj.fitpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.adapter.SettingAdapter;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.view.SettingMenuItem;
import xfkj.fitpro.utils.LoadingDailog;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;

public class UinfoActivity extends BaseActivity {
	
	
    private static final int CHANGE = 201;
    private RecyclerView rlv;
	
    private ArrayList<SettingMenuItem> mData = new ArrayList<SettingMenuItem>();

    private SettingAdapter adapter = null;
    private String Title = "";
    private LeReceiver leReceiver;
    	
    @Override
    protected void setActivityTitle() {
        Title = getIntent().getStringExtra("Title");
        initTitle();
        setTitle(Title, this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_uinfo);
    }


    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
             if(msg.what == Profile.MsgWhat.what14)
            {
                DataToUI();
                if(Constants.dialog != null)
                    Constants.dialog.dismiss();
            }
        }
    };



    @Override
    protected void initValues() {
        leReceiver = new LeReceiver(UinfoActivity.this, mHandler);
    }

    @Override
    protected void initViews() {
        rlv = findViewById(R.id.rlv);
        mData = new ArrayList<>();
		adapter = new SettingAdapter(this, mData);
        adapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {

                GotoNext(position);

            }
        });
        rlv.setLayoutManager(new LinearLayoutManager(this));
//        rlv.addItemDecoration(new GridSpanline1(context, 0, 1, mData));
        rlv.setAdapter(adapter);
    }
	
	private void DataToUI()
    {		
        mData.clear();
		mData.add(new SettingMenuItem(R.string.sex,getString(R.string.sex), (SaveKeyValues.getIntValues("gender", 1) == 0 ? getString(R.string.girl) : getString(R.string.boy)), R.drawable.icon_set_more, R.drawable.setting_sex, true, 1, SetInfoActivity.class));
        mData.add(new SettingMenuItem(R.string.age,getString(R.string.age), (SaveKeyValues.getIntValues("age", 25) + " "+getString(R.string.age_unit)), R.drawable.icon_set_more, R.drawable.setting_birthday, true, 1, SetInfoActivity.class));
        mData.add(new SettingMenuItem(R.string.height,getString(R.string.height), (SaveKeyValues.getIntValues("height", 170) + " cm"), R.drawable.icon_set_more, R.drawable.setting_height, true, 1, SetInfoActivity.class));
        mData.add(new SettingMenuItem(R.string.weight,getString(R.string.weight), (SaveKeyValues.getIntValues("weight", 65) + " kg"), R.drawable.icon_set_more, R.drawable.setting_weight, true, 1, SetInfoActivity.class));
        adapter.notifyDataSetChanged();		
	}
	
	
	/**
     * 跳转到下一个界面
     *
     * @param position
     */
    private void GotoNext(int position)
    {
        //
        SettingMenuItem item = mData.get(position);
        if (item.MenuType != 1 || item.ClassObj == null)
        {
            return;
        }

        Intent intent = new Intent(this, item.ClassObj);
        intent.putExtra("Title", item.Name);
        intent.putExtra("showbtn", 1);
        Logdebug("Uinfo", "Name"+item.Name);

        if (item.Id == R.string.sex || item.Id ==R.string.age || item.Id == R.string.height || item.Id ==R.string.weight)
        {
            String str = item.getNameInfo().split(" ")[0];
            int value = 0;
            Logdebug("Uinfo", "str"+str);
            if (item.Id  == R.string.sex)
                value = str.equals(getString(R.string.boy)) ? 1 : 0;
            else
                value = Integer.parseInt(str);
            intent.putExtra("type", item.Id);
            intent.putExtra("value", value);
             Logdebug("Uinfo", "value"+value);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE && resultCode == Activity.RESULT_OK)
        {
            //重新刷新UI
            DataToUI();
        }
    }


    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //    mService.unService(AlarmActivity.this);
        if(leReceiver != null)
            leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(leReceiver != null)
            leReceiver.registerLeReceiver();
        if(SDKTools.BleState == 1){
            SDKCmdMannager.getPersonalInfo();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(UinfoActivity.this)
                    .setMessage(getString(R.string.getdatas))
                    .setCancelable(false);
            Constants.dialog = mBuilder.create(true,2000);
        }else{
            DataToUI();
        }
    }

}
