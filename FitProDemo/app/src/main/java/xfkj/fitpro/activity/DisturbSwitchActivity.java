package xfkj.fitpro.activity;

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
import xfkj.fitpro.view.LongSitPopupWin;
import xfkj.fitpro.utils.LoadingDailog;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getDisturbSwitchValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;

public class DisturbSwitchActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "DisturbSwitchActivity";
    private ToggleButton iSwitch;
    private LinearLayout disturb_star_time_linear,disturb_end_time_linear,set_disturb_box_linear,disturb_item_box;
    private TextView disturb_star_time, disturb_end_time;

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
                case Profile.MsgWhat.what300:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Constants.dialog != null)
                                Constants.dialog.dismiss();
                            Toast toast;
                            if(map.get("is_ok") != null && map.get("is_ok").equals("0")){
                                toast = Toast.makeText(DisturbSwitchActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }else {
                                toast = Toast.makeText(DisturbSwitchActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
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
        setTitle(getString(R.string.disturb_title), DisturbSwitchActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_disturb_switch);
    }

    @Override
    protected void initValues() {
        map = new HashMap<>();
        popWin = new LongSitPopupWin(this);
        listItems = new ArrayList<>();
        leReceiver = new LeReceiver(DisturbSwitchActivity.this, mHandler);
    }

    @Override
    protected void initViews() {
        iSwitch = (ToggleButton) findViewById(R.id.disturb_status);

        set_disturb_box_linear = (LinearLayout) findViewById(R.id.set_disturb_box);
        disturb_item_box = (LinearLayout) findViewById(R.id.disturb_item_box);

        disturb_star_time_linear = (LinearLayout) findViewById(R.id.disturb_star_time_linear);
        disturb_end_time_linear = (LinearLayout) findViewById(R.id.disturb_end_time_linear);

        disturb_star_time = (TextView) findViewById(R.id.disturb_star_time);
        disturb_end_time = (TextView) findViewById(R.id.disturb_end_time);
    }

    @Override
    protected void setViewsListener() {
// 添加监听
        iSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged()
        {
            @Override
            public void onToggle(boolean on)
            {
                Logdebug(TAG, "开关:" + on);
                if(SDKTools.BleState != 1){
                    Toast.makeText(DisturbSwitchActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    if (on) {
                        iSwitch.setToggleOff();
                    }else {
                        iSwitch.setToggleOn();
                    }
                    return;
                }
                setSendBeforeValue("disturb_status",0,SaveKeyValues.getIntValues("disturb_status",0)+"");
                if (on)
                {
                    SaveKeyValues.putIntValues("disturb_status", 1);
                //    disturb_item_box.setVisibility(View.VISIBLE);
                } else
                {
                    SaveKeyValues.putIntValues("disturb_status", 0);
                //    disturb_item_box.setVisibility(View.GONE);
                }
                setWatchLongSit();
            }
        });
        disturb_star_time_linear.setOnClickListener(this);
        disturb_end_time_linear.setOnClickListener(this);
    }

    public void setWatchLongSit(){
        if(SDKTools.BleState != 1){
            Toast.makeText(DisturbSwitchActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(DisturbSwitchActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true,5000);
        byte [] LongSit = getDisturbSwitchValue();
        SDKCmdMannager.setDistuMode(LongSit);
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
            Toast.makeText(DisturbSwitchActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        int vid = v.getId();
        int index = 0;
        if (vid == R.id.disturb_star_time_linear)
        {
            index = SaveKeyValues.getIntValues("disturb_star_time", 22);
            map.put("sit_title", this.getString(R.string.warn_star_time_txt));
        } else if (vid == R.id.disturb_end_time_linear)
        {
            index = SaveKeyValues.getIntValues("disturb_end_time", 8);
            map.put("sit_title", this.getString(R.string.warn_end_time_txt));
        }
        map.put("sit_vid", vid);
        getListItem(index);
        showPopFormBottom();
    }

    public void getListItem(int index){
        listItems.clear();
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

        popWin.showAtLocation(set_disturb_box_linear, Gravity.CENTER, 0, 0);

    }

    public void showNext(){
        Integer star_time = SaveKeyValues.getIntValues("disturb_star_time", 22);
        Integer end_time = SaveKeyValues.getIntValues("disturb_end_time", 8);
        String is_next = "";
        if(end_time<star_time){
            is_next = getString(R.string.is_next);
        }
        String text = is_next+" "+(end_time<10?"0"+end_time:end_time) + ":00";
        disturb_end_time.setText(text);
    }

    public int checkstTime(int vid){
        Integer star_time = SaveKeyValues.getIntValues("disturb_star_time", 22);
        Integer end_time = SaveKeyValues.getIntValues("disturb_end_time", 8);
        if (vid == R.id.disturb_star_time_linear)
        {
            star_time = itemIndex;
        } else if (vid == R.id.disturb_end_time_linear)
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
        getListItem(nSectlect);
        int res = checkstTime(vid);
        if(res == 2){
            Toast.makeText(DisturbSwitchActivity.this, getString(R.string.err_starend_time2), Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == R.id.disturb_star_time_linear)
        {
        //    disturb_star_time.setText(itemText);
            setSendBeforeValue("disturb_star_time",0,SaveKeyValues.getIntValues("disturb_star_time",22)+"");
            SaveKeyValues.putIntValues("disturb_star_time", itemIndex);
        } else if (vid == R.id.disturb_end_time_linear)
        {
            setSendBeforeValue("disturb_end_time",0,SaveKeyValues.getIntValues("disturb_end_time",8)+"");
            SaveKeyValues.putIntValues("disturb_end_time", itemIndex);
        }
        Logdebug(TAG,"选择返回的值---"+itemIndex+"-----vid---"+vid);
        setWatchLongSit();
    }


    public void showUI()
    {
        Integer is_siesta = SaveKeyValues.getIntValues("disturb_status", 0);
        if(is_siesta == 1){
            disturb_item_box.setVisibility(View.VISIBLE);
            iSwitch.setToggleOn();
        }else{
            disturb_item_box.setVisibility(View.GONE);
            iSwitch.setToggleOff();
        }

        Integer star_time = SaveKeyValues.getIntValues("disturb_star_time", 22);
        String text2 = (star_time<10?"0"+star_time:star_time) + ":00";
        disturb_star_time.setText(text2);
        showNext();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(leReceiver != null)
            leReceiver.registerLeReceiver();
        if(SDKTools.BleState == 1){
            SDKCmdMannager.getDistuModeInfo();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(DisturbSwitchActivity.this)
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
    protected void setViewsFunction() {

    }
}
