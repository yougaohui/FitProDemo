package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetStepValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetUinfoValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.LoadingDailog;

public class SetInfoActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "SetInfoActivity";
    private String Title = "";
    private Integer val,yval, vid, showbtn, val_index = 0;
    private int mType;
	
	private Button sure_update;
	private LinearLayout btn_box;

    Intent intent;
    ImageView iv_Boy, iv_Gril;
    private ArrayList<String> mData = null;//集合
    private LoopView data_picker;//横下滑动选择身高
    private LeReceiver leReceiver;

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            if(msg.what == Profile.MsgWhat.what31 || msg.what == Profile.MsgWhat.what32){
                SDKTools.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
						if(Constants.dialog != null)
                        Constants.dialog.dismiss();
                        Toast toast;
                        if(map.get("is_ok") != null && map.get("is_ok").equals("0")){
                            toast = Toast.makeText(SetInfoActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }else{
                            toast = Toast.makeText(SetInfoActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                },1000);
            }
        }
    };

    @Override
    protected void setActivityTitle()
    {
        initTitle();
        setTitle(Title);
    }

    @Override
    protected void getLayoutToView()
    {
        setContentView(R.layout.activity_set_info);
    }
	

    @Override
    protected void initValues()
    {
        Title = getString(R.string.set_uinfo_txt);
        intent = getIntent();
        mType = intent.getIntExtra("type",0);
        val = intent.getIntExtra("value", 0);
		yval = val;
        showbtn = intent.getIntExtra("showbtn", 0);
        mData = new ArrayList<>();
        if(mType == R.string.sex){
            Title = this.getString(R.string.set_sex);
        }else if (mType == R.string.age){
            Title = this.getString(R.string.set_age);
            for (int i = 6; i <= 127; i++)
            {
                mData.add(i + "");
            }
            val_index = val - 6;
        }else if(mType == R.string.height){
            Title = this.getString(R.string.set_height);
            for (int i = 100; i <= 250; i++)
            {
                mData.add(i + "");
            }
            val_index = val - 100;
        }else if(mType == R.string.weight){
            Title = this.getString(R.string.set_weight);
            for (int i = 30; i <= 180; i++)
            {
                mData.add(i + "");
            }
            val_index = val - 30;
        }else if(mType == R.string.target_txt){
            Title = this.getString(R.string.set_step);
            for (int i = 1; i <= 150; i++)
            {
                mData.add((i * 1000) + "");
            }
            val_index = (val / 1000) - 1;
        }
        leReceiver = new LeReceiver(SetInfoActivity.this, mHandler);

    }

    @Override
    protected void initViews()
    {
        iv_Boy = findViewById(R.id.boy);
        iv_Gril = findViewById(R.id.girl);
        data_picker = findViewById(R.id.data_picker);
        sure_update = findViewById(R.id.sure_update);
        btn_box = findViewById(R.id.btn_box);

        if (mType == R.string.sex == true)
        {
            findViewById(R.id.ll_sex).setVisibility(View.VISIBLE);
            findViewById(R.id.lldata).setVisibility(View.GONE);
            //显示默认的设置
            setSexValue(val == 1);

        } else
        {
            findViewById(R.id.ll_sex).setVisibility(View.GONE);
            findViewById(R.id.lldata).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setViewsListener()
    {
        ImageView title_left = (ImageView) findViewById(R.id.left_btn);
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(showbtn == 0) {
                    returnResult();
                }
                setResult(RESULT_OK, intent);
				finish();
            }
        });

        iv_Boy.setOnClickListener(this);
        iv_Gril.setOnClickListener(this);
        sure_update.setOnClickListener(this);

        data_picker.setListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int index)
            {
				if(showbtn == 1){
                    btn_box.setVisibility(View.VISIBLE);
				}else{
                    btn_box.setVisibility(View.GONE);
				}
                val = Integer.parseInt(mData.get(index));
            }
        });
    }

    protected void returnResult()
    {
        //数据缓存到本地
        String name = "";
        int defval = 0;
        if(mType == R.string.sex){
            name = "gender";
            defval = 1;
        }else if(mType == R.string.age){
            name = "age";
            defval = 25;
        }else if(mType == R.string.height){
            name = "height";
            defval = 170;
        }else if(mType == R.string.weight){
            name = "weight";
            defval = 65;
        }else if(mType == R.string.target_txt){
            name = "step";
            defval = 5000;
        }
        setSendBeforeValue(name,0,SaveKeyValues.getIntValues(name,defval)+"");
        SaveKeyValues.putIntValues(name, val);
    }


    /**
     * 返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(showbtn == 0) {
                returnResult();
            }
            setResult(RESULT_OK, intent);
            finish();
        }
        return false;
    }

    @Override
    protected void setViewsFunction()
    {
        data_picker.setItems(mData);
        data_picker.setInitPosition(val_index);
    }

    @Override
    public void onClick(View view)
    {
		if(view.getId() == R.id.sure_update){
			setUserinfo();
		}else{			
			setSexValue(view == iv_Boy);
		}
    }

    private void setSexValue(boolean isboy)
    {
        if (isboy == true)
        {
            val = 1;
            iv_Gril.setImageResource(R.drawable.set_user_info_female_0);
            iv_Boy.setImageResource(R.drawable.set_user_info_male_1);
        } else
        {
            val = 0;
            iv_Gril.setImageResource(R.drawable.set_user_info_female_1);
            iv_Boy.setImageResource(R.drawable.set_user_info_male_0);
        }
		if(showbtn == 1){
            btn_box.setVisibility(View.VISIBLE);
		}else{
            btn_box.setVisibility(View.GONE);
		}
    }
	
	public void setUserinfo(){
        if(SDKTools.BleState != 1) {
            Toast.makeText(SetInfoActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }

        returnResult();
		byte [] uinfo;
		String desc;
		if(mType == R.string.target_txt){
			uinfo = getSetStepValue();
            desc = "设置目标步数";
		}else{
			uinfo = getSetUinfoValue();
            desc = "设置个人信息";
		}
        SDKTools.mService.commandPoolWrite(uinfo,desc);
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(SetInfoActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true,8000);
        /*
        Constant.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
				if(Constant.dialog != null)
                Constant.dialog.dismiss();
                setResult(RESULT_OK, intent);
                finish();
            }
        },5000);*/
	}

    @Override
    protected void onPause()
    {
        super.onPause();
		if(leReceiver != null)
        leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
		if(leReceiver != null)
        leReceiver.registerLeReceiver();
    }


}
