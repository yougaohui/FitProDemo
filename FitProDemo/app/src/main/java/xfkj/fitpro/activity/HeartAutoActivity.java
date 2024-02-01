package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getHeartAutoValue;
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

public class HeartAutoActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "HeartAutoActivity";
    private ToggleButton mSwitch,iSwitch;
    private LinearLayout set_heart_auto_boxs, frequency_time_linear, heart_auto_star_time_linear, heart_auto_end_time_linear,heart_auto_item_box;
    private TextView heart_frequency, heart_auto_star_time, heart_auto_end_time;

    private Map<String, Object> map;
    LongSitPopupWin popWin;

    private ArrayList listItems;
    private int itemIndex = 6;
    private String itemText;

    private LeReceiver leReceiver;

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what)
            {
                case Profile.MsgWhat.what302:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Constants.dialog != null)
                                Constants.dialog.dismiss();
                            Toast toast;
                            if(map.get("is_ok") != null && map.get("is_ok").equals("0")){
                                toast = Toast.makeText(HeartAutoActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }else {
                                toast = Toast.makeText(HeartAutoActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
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
    protected void setActivityTitle() {
        initTitle();
        setTitle(getString(R.string.heart_auto_title), HeartAutoActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_heart_auto);
    }

    @Override
    protected void initValues() {
        map = new HashMap<>();
        popWin = new LongSitPopupWin(this);
        listItems = new ArrayList<>();
        leReceiver = new LeReceiver(HeartAutoActivity.this, mHandler);
    }

    @Override
    protected void initViews() {
        mSwitch = (ToggleButton) findViewById(R.id.heart_auto_status);
        iSwitch = (ToggleButton) findViewById(R.id.heart_sleep_assist);
        set_heart_auto_boxs = (LinearLayout) findViewById(R.id.set_heart_auto_box);

        frequency_time_linear = (LinearLayout) findViewById(R.id.frequency_time_linear);
        heart_auto_star_time_linear = (LinearLayout) findViewById(R.id.heart_auto_star_time_linear);
        heart_auto_end_time_linear = (LinearLayout) findViewById(R.id.heart_auto_end_time_linear);
        heart_auto_item_box = (LinearLayout) findViewById(R.id.heart_auto_item_box);

        heart_frequency = (TextView) findViewById(R.id.heart_frequency);
        heart_auto_star_time = (TextView) findViewById(R.id.heart_auto_star_time);
        heart_auto_end_time = (TextView) findViewById(R.id.heart_auto_end_time);
    }

    @Override
    protected void setViewsListener() {
        // 添加监听
        mSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged()
        {
            @Override
            public void onToggle(boolean on)
            {
                if(SDKTools.BleState != 1){
                    Toast.makeText(HeartAutoActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (on) {
                        mSwitch.setToggleOff();
                    }else {
                        mSwitch.setToggleOn();
                    }
                    return;
                }
                Logdebug(TAG, "开关:" + on);
                setSendBeforeValue("heart_auto_status",0,SaveKeyValues.getIntValues("heart_auto_status",0)+"");
                if (on)
                {
                    SaveKeyValues.putIntValues("heart_auto_status", 1);
                //    heart_auto_item_box.setVisibility(View.VISIBLE);
                } else
                {
                    SaveKeyValues.putIntValues("heart_auto_status", 0);
                //    heart_auto_item_box.setVisibility(View.GONE);
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
                    Toast.makeText(HeartAutoActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (on) {
                        iSwitch.setToggleOff();
                    }else {
                        iSwitch.setToggleOn();
                    }
                    return;
                }
                setSendBeforeValue("heart_sleep_assist",0,SaveKeyValues.getIntValues("heart_sleep_assist",0)+"");
                if (on)
                {
                    SaveKeyValues.putIntValues("heart_sleep_assist", 1);
                } else
                {
                    SaveKeyValues.putIntValues("heart_sleep_assist", 0);
                }
                setWatchLongSit();
            }
        });
        frequency_time_linear.setOnClickListener(this);
        heart_auto_star_time_linear.setOnClickListener(this);
        heart_auto_end_time_linear.setOnClickListener(this);
    }

    public void setWatchLongSit(){
        if(SDKTools.BleState != 1){
            Toast.makeText(HeartAutoActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(HeartAutoActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true,5000);
        byte [] LongSit = getHeartAutoValue();
        SDKCmdMannager.setHeartRateAutoMeas(LongSit);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        if(SDKTools.BleState != 1){
            Toast.makeText(HeartAutoActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        int vid = v.getId();
        if (vid == R.id.frequency_time_linear)
        {
            Integer time =  SaveKeyValues.getIntValues("heart_frequency", 2);
            getListItem(vid, time,"show");
            map.put("sit_title", this.getString(R.string.heart_frequency));
            map.put("sit_vid", vid);
        } else if (vid == R.id.heart_auto_star_time_linear)
        {
            Integer star_time = SaveKeyValues.getIntValues("heart_auto_star_time", 6);
            getListItem(vid, star_time,"show");
            map.put("sit_title", this.getString(R.string.warn_star_time_txt));
            map.put("sit_vid", vid);
        } else if (vid == R.id.heart_auto_end_time_linear)
        {
            Integer end_time = SaveKeyValues.getIntValues("heart_auto_end_time", 22);
            getListItem(vid, end_time,"show");
            map.put("sit_title", this.getString(R.string.warn_end_time_txt));
            map.put("sit_vid", vid);
        }
        showPopFormBottom();
    }

    public void getListItem(int vid, int index,String res)
    {
        listItems.clear();
        if (vid == R.id.frequency_time_linear)
        {
            Integer [] times = {2,60,90,120};
            int i = 0;
            for(int t:times){
                String text = t + this.getString(R.string.minute_txt);
                if(res.equals("res")){
                    if(i == index){
                        itemIndex = t;
                        itemText = text;
                    };
                }else{
                    if(t == index){
                        itemIndex = i;
                        itemText = text;
                    };
                    listItems.add(text);
                }
                i++;
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

        popWin.showAtLocation(set_heart_auto_boxs, Gravity.CENTER, 0, 0);

    }

    public void showNext(){
        Integer star_time = SaveKeyValues.getIntValues("heart_auto_star_time", 6);
        Integer end_time = SaveKeyValues.getIntValues("heart_auto_end_time", 22);
        String is_next = "";
        if(end_time<star_time){
            is_next = getString(R.string.is_next);
        }
        String text = is_next+" "+(end_time<10?"0"+end_time:end_time) + ":00";
        heart_auto_end_time.setText(text);
    }

    public int checkstTime(int vid){
        Integer star_time = SaveKeyValues.getIntValues("heart_auto_star_time", 6);
        Integer end_time = SaveKeyValues.getIntValues("heart_auto_end_time", 22);
        if (vid == R.id.heart_auto_star_time_linear)
        {
            star_time = itemIndex;
        } else if (vid == R.id.heart_auto_end_time_linear)
        {
            end_time = itemIndex;
        }
        if(star_time == end_time){
            return 2;
        }else{
            return 0;
        }
    }

    public void resultSitValue(int nSectlect, Map<String, Object> map)
    {
        Integer vid = (Integer) map.get("sit_vid");
        getListItem(vid, nSectlect,"res");
        int res = checkstTime(vid);
        if(res == 2){
            Toast.makeText(HeartAutoActivity.this, getString(R.string.err_starend_time2), Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == R.id.frequency_time_linear)
        {
        //    heart_frequency.setText(itemText);
            setSendBeforeValue("heart_frequency",0,SaveKeyValues.getIntValues("heart_frequency",2)+"");
            SaveKeyValues.putIntValues("heart_frequency", itemIndex);
        } else if (vid == R.id.heart_auto_star_time_linear)
        {
        //    heart_auto_star_time.setText(itemText);
            setSendBeforeValue("heart_auto_star_time",0,SaveKeyValues.getIntValues("heart_auto_star_time",6)+"");
            SaveKeyValues.putIntValues("heart_auto_star_time", itemIndex);
        } else if (vid == R.id.heart_auto_end_time_linear)
        {
            setSendBeforeValue("heart_auto_end_time",0,SaveKeyValues.getIntValues("heart_auto_end_time",22)+"");
            SaveKeyValues.putIntValues("heart_auto_end_time", itemIndex);
        }
        Logdebug(TAG,"选择返回的值---"+itemIndex);
        setWatchLongSit();
    }

    public void showUI()
    {
        Integer isopen = SaveKeyValues.getIntValues("heart_auto_status", 0);
        Logdebug(TAG,"showUI---isopen--"+isopen);
        if (isopen == 1)
        {
            mSwitch.setToggleOn();
            heart_auto_item_box.setVisibility(View.VISIBLE);
            Integer is_siesta = SaveKeyValues.getIntValues("heart_sleep_assist", 0);
            if(is_siesta == 1){
                iSwitch.setToggleOn();
            }else{
                iSwitch.setToggleOff();
            }
            Integer time = SaveKeyValues.getIntValues("heart_frequency", 2);
            heart_frequency.setText(time+this.getString(R.string.minute_txt));
            Logdebug(TAG,"showUI---time--"+time);

            Integer star_time = SaveKeyValues.getIntValues("heart_auto_star_time", 6);
            String text2 = (star_time<10?"0"+star_time:star_time) + ":00";
            heart_auto_star_time.setText(text2);
            showNext();
        } else
        {
            heart_auto_item_box.setVisibility(View.GONE);
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
            SDKCmdMannager.getHearRateAutoMeInfo();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(HeartAutoActivity.this)
                    .setMessage(getString(R.string.getdatas))
                    .setCancelable(false);
            Constants.dialog = mBuilder.create(true,2000);
        }else{
            showUI();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(leReceiver != null)
            leReceiver.unregisterLeReceiver();
    }

    @Override
    protected void setViewsFunction() {

    }
}
