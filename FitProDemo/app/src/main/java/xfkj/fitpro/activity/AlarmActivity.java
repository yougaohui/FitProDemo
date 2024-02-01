package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetAlarmValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;
import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.view.MyPopupWin;

public class AlarmActivity extends BaseActivity
{

    private String TAG = "AlarmActivity";
    MyPopupWin popWin;

    /**
     * 闹铃数据源
     */
    private JSONArray mData = null;

    private ListView alarm_lists;
    private MyAdapter ListAdapter;
    private Integer[] week_ids;
    private LeReceiver leReceiver;

    private LinearLayout no_alarm_tip_box;

    @Override
    protected void setActivityTitle()
    {
        initTitle();
        ImageView add_btn = setTitle(getString(R.string.alarm_set), AlarmActivity.this, R.mipmap.add_alarm);
        add_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPopFormBottom(view);
            }
        });
    }

    @Override
    protected void getLayoutToView()
    {
        setContentView(R.layout.activity_alarm);
    }

    @Override
    protected void initValues()
    {
        //  week_ids = new Integer[]{R.id.mon_id, R.id.tue_id, R.id.wed_id, R.id.thu_id, R.id.fri_id, R.id.sat_id, R.id.sun_id};

        popWin = new MyPopupWin(this);
        leReceiver = new LeReceiver(AlarmActivity.this, mHandler);

    }

    @Override
    protected void initViews()
    {
        alarm_lists = findViewById(R.id.alarm_list_view);
        no_alarm_tip_box = findViewById(R.id.no_alarm_tip_box);

        //  alist.add(map);
        mData = new JSONArray();
        Logdebug(TAG, "------------initViews--------- : " + SaveKeyValues.getStringValues("alarms", "[]"));
        if(SDKTools.BleState == 1){
            SDKCmdMannager.getAlarms();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(AlarmActivity.this)
                    .setMessage(getString(R.string.get_alarms_txt))
                    .setCancelable(false);
            Constants.dialog = mBuilder.create(true,2000);
        }
        getAlarmLists();
    }

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what)
            {
                case Profile.MsgWhat.what34:
                    getAlarmLists();
					if(Constants.dialog != null)
                    Constants.dialog.dismiss();
                    break;
                case Profile.MsgWhat.what35:
                    SDKTools.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Constants.dialog != null)
                            Constants.dialog.dismiss();
                            Toast toast;
                            if(map.get("is_ok") != null && map.get("is_ok").equals("0")){
                                toast = Toast.makeText(AlarmActivity.this, getString(R.string.set_err), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }else {
                                toast = Toast.makeText(AlarmActivity.this, getString(R.string.set), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                getAlarmLists();
                            }
                        }
                    },1000);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取已保存的闹铃
     */
    public void getAlarmLists()
    {
        try
        {
            String stralarms = SaveKeyValues.getStringValues("alarms", "[]");
            mData = new JSONArray(stralarms);
        } catch (Exception e)
        {
        }

        if (mData == null || mData.length() == 0)
        {
            mData = new JSONArray();
            no_alarm_tip_box.setVisibility(View.VISIBLE);
            alarm_lists.setVisibility(View.GONE);
        } else
        {
            no_alarm_tip_box.setVisibility(View.GONE);
            alarm_lists.setVisibility(View.VISIBLE);
            sortDate();
            if (ListAdapter != null)
                ListAdapter.notifyDataSetChanged();
            else
            {
                ListAdapter = new MyAdapter();
                alarm_lists.setAdapter(ListAdapter);
            }
        }
    }

   public void sortDate(){
       List<JSONObject> jsonValue=new ArrayList<JSONObject>();
       for(int i=0;i<mData.length();i++){
           try {
               jsonValue.add(mData.getJSONObject(i));
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       Collections.sort(jsonValue,new Comparator<JSONObject>() {
           private static final String key="id";
           public int compare(JSONObject a, JSONObject b) {
               Integer valA= null;
               Integer valB= null;
               try {
                   valA = a.getInt(key);
                   valB = b.getInt(key);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               return valA.compareTo(valB);
           }
       });
       mData = new JSONArray();
       for(int i=0;i<jsonValue.size();i++){
           mData.put(jsonValue.get(i));
       }
   }

    @Override
    protected void setViewsListener()
    {

    }

    @Override
    protected void setViewsFunction()
    {

    }

    public void setWatchAlarm()
    {
        if (SDKTools.BleState != 1)
        {
            Toast.makeText(AlarmActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(AlarmActivity.this)
                .setMessage(getString(R.string.setting))
                .setCancelable(false);
        Constants.dialog = mBuilder.create(true,8000);
        byte[] Alarms = new byte[0];
        try
        {
            Alarms = getSetAlarmValue();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
		if(SDKTools.mService != null){
		    SDKCmdMannager.setAlarms(Alarms);
		}
    }


    public void showPopFormBottom(View view)
    {
        if (SDKTools.BleState != 1)
        {
            Toast.makeText(AlarmActivity.this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mData != null && mData.length() >= 8)
        {
            Toast.makeText(AlarmActivity.this, getString(R.string.more_alarm_txt), Toast.LENGTH_SHORT).show();
            return;
        }
        showSetAlarmPop();
    }


    public void showSetAlarmPop()
    {
        if (popWin == null)
            return;

        //回到接受
        popWin.setOnData(new MyPopupWin.OnGetAlarmData()
        {
            //回调接受函数
            @Override
            public void onDataCallBack(String hours, String minute, Integer[] weeks)
            {
                try
                {
                    if (SDKTools.BleState != 1)
                    {
                        return;
                    }
                    resultSitValue(hours, minute, weeks);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        popWin.showAtLocation(findViewById(R.id.edit_box), Gravity.CENTER, 0, 0);

    }

    //添加闹铃返回
    public void resultSitValue(String hours, String minute, Integer[] weeks) throws JSONException
    {
        no_alarm_tip_box.setVisibility(View.GONE);
        String hm = hours+minute;
        int id = Integer.valueOf(hm);
        String weekstr = "";
        for (Integer value : weeks)
        {
            weekstr += value + "";
        }
        boolean has = chechExistAlarm(hours,minute,weekstr);
        if(has){
            Toast.makeText(AlarmActivity.this, getString(R.string.has_alarm_txt), Toast.LENGTH_SHORT).show();
            return;
        }
        Map dates = getDate();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("year", dates.get("year"));
        obj.put("month", dates.get("month"));
        obj.put("day", dates.get("day"));
        obj.put("hours", hours);
        obj.put("minute", minute);
        obj.put("weeks", weekstr);
        obj.put("num", 1);
        mData.put(obj);
        setSendBeforeValue("alarms",1,SaveKeyValues.getStringValues("alarms","[]"));
        //	String str = "{'alarm':[{'id':1,'year':2018,'month':7,'day':7,'hour':12,'minute':11,'num':0,'weeks':0000000}]}";
        SaveKeyValues.putStringValues("alarms", mData.toString());
        Logdebug(TAG, "添加闹铃返回alarms : " + mData.toString());
        setWatchAlarm();
    }

    public boolean chechExistAlarm(String hours, String minute,String weeks){
        boolean res = false;
        for(int i=0;i<mData.length();i++){
            try {
                JSONObject alarm = mData.getJSONObject(i);
                String h = alarm.get("hours").toString();
                String m = alarm.get("minute").toString();
                String w = alarm.get("weeks").toString();
                if(hours.equals(h) && m.equals(minute)&& w.equals(weeks)){
                    res = true;
                    i = mData.length();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public class MyAdapter extends BaseAdapter
    {
        Integer[] week_txt = new Integer[]{R.string.mon_txt, R.string.tue_txt, R.string.wed_txt, R.string.thu_txt, R.string.fri_txt, R.string.sat_txt, R.string.sun_txt};

        @Override
        public int getCount()
        {
            return mData == null ? 0 : mData.length();
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup)
        {
            view = LayoutInflater.from(AlarmActivity.this).inflate(R.layout.alarm_item, viewGroup, false);
            TextView alarm_time = view.findViewById(R.id.alarm_time);
            LinearLayout weeks_box = view.findViewById(R.id.weeks_box);
            Button delBtn = view.findViewById(R.id.alarm_del_btn);

            try
            {
                JSONObject obj = mData.getJSONObject(position);
                alarm_time.setText(obj.getString("hours") + ":" + obj.getString("minute"));

                String strweek = obj.getString("weeks");//1100000  数据代表七天 1表示选择
                StringBuffer buf = new StringBuffer(strweek);
                strweek = buf.reverse().toString();
                for (int n = 0; n < strweek.length(); n++)
                {
                    if (strweek.substring(n, n + 1).equals("0"))
                        continue;
                    TextView lab = new TextView(AlarmActivity.this);
                    lab.setTextSize(14);
                    lab.setBackgroundResource(R.drawable.alarm_set_cancel_btn); //设置背景
                    lab.setText(getResources().getString(week_txt[n]));
                    lab.setTextColor(getResources().getColor(R.color.white));
                    lab.setPadding(10, 5, 10, 5);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 5, 0);
                    lab.setLayoutParams(params);

                    weeks_box.addView(lab);
                }
            } catch (Exception e)
            {

            }

            delBtn.setOnClickListener(new View.OnClickListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AlarmActivity.this);
					
                    dialog.setTitle(getString(R.string.tips_txt));
                    dialog.setMessage(getString(R.string.confirm_del_txt));
                    dialog.setNeutralButton(getString(R.string.cancel_txt), null);
                    dialog.setPositiveButton(getString(R.string.confirm_txt), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            try
                            {
                                setSendBeforeValue("alarms",1,SaveKeyValues.getStringValues("alarms","[]"));
                                mData.remove(position);
                                //异常JSON数组数据保存到本地
                                SaveKeyValues.putStringValues("alarms", mData.toString());

                                setWatchAlarm();

                            } catch (Exception e)
                            {

                            }
                        }
                    });
                    dialog.show();
                }
            });

            return view;
        }
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
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
