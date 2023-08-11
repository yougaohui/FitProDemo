package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetLongSitValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.view.LongSitPopupWin;

public class LongsitWarnActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "LongsitWarnActivity";
    private ToggleButton mSwitch,iSwitch;
    private LinearLayout set_linear_boxs, long_sit_time_linear, warn_star_time_linear, warn_end_time_linear;
    private TextView long_sit_time, warn_star_time, warn_end_time;

    private Map<String, Object> map;
    LongSitPopupWin popWin;

    private ArrayList listItems;
    private int itemIndex = 0;
    private String itemText;

    private LeReceiver leReceiver;

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what)
            {
                case Profile.MsgWhat.what36:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
							if(Constants.dialog != null)
                            Constants.dialog.dismiss();
                            Toast toast;
                            if(map.get("is_ok") != null && map.get("is_ok").equals("0")){
                                toast = Toast.makeText(LongsitWarnActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }else {
                                toast = Toast.makeText(LongsitWarnActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            showUI();
                        }
                    },1000);
                    break;
                case Profile.MsgWhat.what14:
                    showUI();
                    if(Constants.dialog != null)
                        Constants.dialog.dismiss();
                break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void setActivityTitle()
    {
        initTitle();
        setTitle(getString(R.string.long_sit), LongsitWarnActivity.this);
    }

    @Override
    protected void getLayoutToView()
    {
        setContentView(R.layout.activity_longsit_warn);
    }

    @Override
    protected void initValues()
    {
        map = new HashMap<>();
        popWin = new LongSitPopupWin(this);
        listItems = new ArrayList<>();
        leReceiver = new LeReceiver(LongsitWarnActivity.this, mHandler);
    }


    @Override
    protected void initViews()
    {
        mSwitch = (ToggleButton) findViewById(R.id.alarm_status);
        iSwitch = (ToggleButton) findViewById(R.id.alarm_siesta);
        set_linear_boxs = (LinearLayout) findViewById(R.id.set_linear_boxs);
        long_sit_time_linear = (LinearLayout) findViewById(R.id.long_sit_time_linear);
        warn_star_time_linear = (LinearLayout) findViewById(R.id.warn_star_time_linear);
        warn_end_time_linear = (LinearLayout) findViewById(R.id.warn_end_time_linear);

        long_sit_time = (TextView) findViewById(R.id.long_sit_time);
        warn_star_time = (TextView) findViewById(R.id.warn_star_time);
        warn_end_time = (TextView) findViewById(R.id.warn_end_time);
    }

    @Override
    protected void setViewsListener()
    {
        // 添加监听
        mSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged()
        {
            @Override
            public void onToggle(boolean on)
            {
                if(SDKTools.BleState != 1){
                    Toast.makeText(LongsitWarnActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (on) {
                        mSwitch.setToggleOff();
                    }else {
                        mSwitch.setToggleOn();
                    }
                    return;
                }
                setSendBeforeValue("longsit_is_open",0,SaveKeyValues.getIntValues("longsit_is_open" ,0)+"");
                Logdebug(TAG, "开关:" + on);
                if (on)
                {
                    SaveKeyValues.putIntValues("longsit_is_open", 1);
                //    set_linear_boxs.setVisibility(View.VISIBLE);
                } else
                {
                    SaveKeyValues.putIntValues("longsit_is_open", 0);
                 //   set_linear_boxs.setVisibility(View.GONE);
                }
                setWatchLongSit();
            }
        });

        // 添加监听
        iSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged()
        {
            @Override
            public void onToggle(boolean on)
            {
                Logdebug(TAG, "开关:" + on);
                if(SDKTools.BleState != 1){
                    Toast.makeText(LongsitWarnActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (on) {
                        iSwitch.setToggleOff();
                    }else {
                        iSwitch.setToggleOn();
                    }
                    return;
                }
                setSendBeforeValue("longsit_is_siesta",0,SaveKeyValues.getIntValues("longsit_is_siesta" ,0)+"");
                if (on)
                {
                    SaveKeyValues.putIntValues("longsit_is_siesta", 1);
                } else
                {
                    SaveKeyValues.putIntValues("longsit_is_siesta", 0);
                }
                setWatchLongSit();
            }
        });
        long_sit_time_linear.setOnClickListener(this);
        warn_star_time_linear.setOnClickListener(this);
        warn_end_time_linear.setOnClickListener(this);
    }


    public void setWatchLongSit(){
        if(SDKTools.BleState != 1){
            Toast.makeText(LongsitWarnActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(LongsitWarnActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true,8000);
        byte [] LongSit = getSetLongSitValue();
        SDKTools.mService.commandPoolWrite(LongSit,"设置久坐提醒");
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (SDKTools.BleState != 1) {
            Toast.makeText(LongsitWarnActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        int vid = v.getId();
        if (vid == R.id.long_sit_time_linear) {
            Integer time = SaveKeyValues.getIntValues("longsit_" + R.id.long_sit_time, 4);
            time = time - 3;
            getListItem(vid, time);
            map.put("sit_title", this.getString(R.string.long_sit_time_txt));
            map.put("sit_vid", R.id.long_sit_time);
        } else if (vid == R.id.warn_star_time_linear) {
            Integer star_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_star_time, 8);
            getListItem(vid, star_time);
            map.put("sit_title", this.getString(R.string.warn_star_time_txt));
            map.put("sit_vid", R.id.warn_star_time);
        } else if (vid == R.id.warn_end_time_linear) {
            Integer end_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_end_time, 22);
            getListItem(vid, end_time);
            map.put("sit_title", this.getString(R.string.warn_end_time_txt));
            map.put("sit_vid", R.id.warn_end_time);
        }
        showPopFormBottom();
    }

    public void getListItem(int vid, int index)
    {
        listItems.clear();
        if (vid == R.id.long_sit_time)
        {
            for (int i = 0; i <= 5; i++)
            {
                String text = (i * 15+45) + this.getString(R.string.minute_txt);
                listItems.add(text);
                if (index == i)
                {
                    itemIndex = index;
                    itemText = text;
                }
            }
        } else
        {
            for (int i = 0; i <= 23; i++)
            {
                String item = (i < 10) ? ("0" + i) : i + "";
                String text = item + ":00";
                listItems.add(text);
                if (index == i)
                {
                    itemIndex = i;
                    itemText = text;
                }
            }
        }
    }

    public void showPopFormBottom()
    {
        if (popWin == null)
            return;

        //回到接受
        popWin.setOnData(new LongSitPopupWin.OnGetData()
        {

            //记录上一次选中的item
            @Override
            public int onSeclectItem()
            {
                Logdebug(TAG, "记录上一次选中的item:" + itemIndex);
                return itemIndex;
            }

            //回调接受函数
            @Override
            public void onDataCallBack(int nSectlect, Map<String, Object> map)
            {
                Logdebug(TAG, "回调返回选中Sectlect:" + nSectlect);
                resultSitValue(nSectlect, map);
            }

            //传递数据源过去
            @Override
            public Map<String, Object> onMaps()
            {
                return map;
            }

            @Override
            public ArrayList onListItems()
            {
                return listItems;
            }
        });

        popWin.showAtLocation(findViewById(R.id.set_longsit_box), Gravity.CENTER, 0, 0);

    }

	public int checkstTime(int vid){
		Integer star_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_star_time, 8);
        Integer end_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_end_time, 22);
		if (vid == R.id.warn_star_time)
        {
			star_time = itemIndex;
        } else if (vid == R.id.warn_end_time)
        {
			end_time = itemIndex;
        }
        if(star_time>end_time){
		    return 1;
        }else if(star_time == end_time){
            return 2;
        }else{
            return 0;
        }
	}
	

    public void resultSitValue(int nSectlect, Map<String, Object> map)
    {
        Integer vid = (Integer) map.get("sit_vid");
        getListItem(vid, nSectlect);
        int res = checkstTime(vid);
		if(res == 1){
			 Toast.makeText(LongsitWarnActivity.this, getString(R.string.err_starend_time), Toast.LENGTH_SHORT).show();
			return;
		}else if(res == 2){
            Toast.makeText(LongsitWarnActivity.this, getString(R.string.err_starend_time2), Toast.LENGTH_SHORT).show();
            return;
        }
		int defval = 0;
        if (vid == R.id.long_sit_time)
        {
        //    long_sit_time.setText(itemText);
            itemIndex = itemIndex + 3;
            defval = 4;
        } else if (vid == R.id.warn_star_time)
        {
            defval = 8;
        //    warn_star_time.setText(itemText);
        } else if (vid == R.id.warn_end_time)
        {
            defval = 22;
         //   warn_end_time.setText(itemText);
        }
        Logdebug(TAG,"选择返回的值---"+itemIndex);
        setSendBeforeValue("longsit_" + vid,0,SaveKeyValues.getIntValues("longsit_" + vid,defval)+"");
        SaveKeyValues.putIntValues("longsit_" + vid, itemIndex);
        setWatchLongSit();
    }


    public void showUI()
    {
        Integer isopen = SaveKeyValues.getIntValues("longsit_is_open", 0);
        Logdebug(TAG,"showUI---isopen--"+isopen);
        if (isopen == 1)
        {
            mSwitch.setToggleOn();
            set_linear_boxs.setVisibility(View.VISIBLE);
            Integer is_siesta = SaveKeyValues.getIntValues("longsit_is_siesta", 0);
            if(is_siesta == 1){
                iSwitch.setToggleOn();
            }else{
                iSwitch.setToggleOff();
            }
            Integer time = SaveKeyValues.getIntValues("longsit_" + R.id.long_sit_time, 4);
            time = time - 3;
            String text1 = (time * 15 + 45) + this.getString(R.string.minute_txt);
            long_sit_time.setText(text1);

            Integer star_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_star_time, 8);
            String text2 = (star_time<10?"0"+star_time:star_time) + ":00";
            warn_star_time.setText(text2);

            Integer end_time = SaveKeyValues.getIntValues("longsit_" + R.id.warn_end_time, 22);
            String text3 = (end_time<10?"0"+end_time:end_time) + ":00";
            warn_end_time.setText(text3);
        } else
        {
            set_linear_boxs.setVisibility(View.GONE);
            mSwitch.setToggleOff();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
		if(leReceiver != null)
        leReceiver.registerLeReceiver();
        if(SDKTools.BleState == 1){
            SDKCmdMannager.getLongSitWarnInfo();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(LongsitWarnActivity.this)
                    .setMessage(getString(R.string.getdatas))
                    .setCancelable(false);
            Constants.dialog = mBuilder.create(true,2000);
        }
        showUI();
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
    protected void setViewsFunction()
    {

    }

}
