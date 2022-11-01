package xfkj.fitpro.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.MoreSleepActivity;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseFragment;
import xfkj.fitpro.db.SqliteDBAcces;

import static com.legend.bluetooth.fitprolib.utils.DateUtils.getCalendars;
import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;
import static xfkj.fitpro.application.MyApplication.returnshi;


public class FindFragment extends BaseFragment
{

    private ArrayList<HashMap<String, Object>> sleepItem;
    private int [] sTypeBg;
    private String TAG = "FindFragment";
    private View view;//界面的布局
    private Context context;
    private TextView sleep_qk_txt,start_sleep_time,end_sleep_time,total_sleep_time,deep_sleep_time,somnolence_sleep_time,sober_time,deep_sleep_bfb,
            somnolence_sleep_bfb,deep_sleep_bgview,somnolence_sleep_bgview,sleep_title,dsleep_icon,ssleep_icon,sosleep_icon;
    private LinearLayout sChartLinear;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_find, null);
        initView();
        return view;
    }

    //初始化View
    private void initView()
    {
        sleep_qk_txt = view.findViewById(R.id.sleep_qk_txt);
        start_sleep_time = view.findViewById(R.id.start_sleep_time);
        end_sleep_time = view.findViewById(R.id.end_sleep_time);
        total_sleep_time = view.findViewById(R.id.total_sleep_time);
        deep_sleep_time = view.findViewById(R.id.deep_sleep_time);
        somnolence_sleep_time = view.findViewById(R.id.somnolence_sleep_time);
        sober_time = view.findViewById(R.id.sober_time);
        deep_sleep_bfb = view.findViewById(R.id.deep_sleep_bfb);
        somnolence_sleep_bfb = view.findViewById(R.id.somnolence_sleep_bfb);
        deep_sleep_bgview = view.findViewById(R.id.deep_sleep_bgview);
        somnolence_sleep_bgview = view.findViewById(R.id.somnolence_sleep_bgview);
        sChartLinear = view.findViewById(R.id.sChartLinear);
        dsleep_icon = view.findViewById(R.id.dsleep_icon);
        ssleep_icon = view.findViewById(R.id.ssleep_icon);
        sosleep_icon = view.findViewById(R.id.sosleep_icon);
        sTypeBg = new int[]{R.color.deep_sleep_background,R.color.somnolence_sleep_background,R.color.sober_sleep_background};
        sleep_title = view.findViewById(R.id.sleep_title);
        sleep_title.setText(getDate().get("date").toString());
        ImageView sleep_btn = view.findViewById(R.id.moren_sleep_btn);
        sleepItem = new ArrayList<HashMap<String, Object>>();
        sleep_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(context, MoreSleepActivity.class));
            }
        });
        SleepData();
    }


    private void SleepData()
    {
        sleepItem.clear();
        Map dates = getDate();
        String year = dates.get("year").toString();
        String month = dates.get("month").toString();
        Integer day = Integer.valueOf(dates.get("day").toString());
        String sday3 = day < 10 ? "0" + day : day + "";
        String endStr = year + "-" + month + "-" + sday3 + " 12:00:00";
        Calendar ca = getCalendars(1);
        int month1 = ca.get(Calendar.MONTH) + 1;
        day = ca.get(Calendar.DAY_OF_MONTH);
        month = month1 < 10 ? "0" + month1 : month1 + "";
        String sday2 = day < 10 ? "0" + day : day + "";
        String starStr = year + "-" + month + "-" + sday2 + " 18:00:00";
        Timestamp stime = Timestamp.valueOf(starStr);
        Timestamp etime = Timestamp.valueOf(endStr);
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Sleep where LongDate>=" + stime.getTime() + " and LongDate <= " + etime.getTime() + " group by LongDate order by LongDate asc");
        float deep_sleep_times = 0, somnolence_times = 0, sober_times = 0, wake_nums = 0;
        long start_sleep_data=0;
        String sleep_times = "", wake_times = "";
        int pstype = 0;
        if (cursor != null && cursor.getCount() >= 6)
        {
            while (cursor.moveToNext())
            {
                HashMap<String, Object> sleeps = new HashMap<>();
                int stype = Integer.valueOf(cursor.getString(cursor.getColumnIndex("SleepTypes")));
                String stimes = cursor.getString(cursor.getColumnIndex("RevDate"));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                if(start_sleep_data == 0){
                    start_sleep_data = longDate;
                    pstype = stype;
                    sleep_times = stimes;
                }else if(stype == 3){
                    wake_nums++;
                }
                float t = longDate - start_sleep_data;
                if(pstype == 2 && (stype == 1 || stype == 3)){//2到1是或者2到3都是浅睡时间
                    sleeps.put("stype",2);
                    somnolence_times += t;
                }else if(pstype == 1 && stype == 2){//1到2是深睡时间
                    sleeps.put("stype",1);
                    deep_sleep_times += t;
                }else if(pstype == 3 && stype == 2){//3到2是清醒时间
                    sleeps.put("stype",3);
                    sober_times += t;
                }
                sleeps.put("stime",t);
                wake_times = stimes;
                start_sleep_data = longDate;
                pstype = stype;
                if (t>0) {
                    sleepItem.add(sleeps);
                }
              }
            cursor.close();
			deep_sleep_times= deep_sleep_times/1000/60;
			somnolence_times= somnolence_times/1000/60;
			sober_times = sober_times/1000/60;
        }
        showView(deep_sleep_times,somnolence_times,sober_times ,sleep_times,wake_times);
    }

    public void showView(float deep_sleep_times,float somnolence_times,float sober_times ,String sleep_t,String wake_t) {
        if(deep_sleep_times>somnolence_times){//深睡时间大于浅睡时间不展示当天睡眠
            deep_sleep_times = somnolence_times = sober_times = 0;
            sleep_t = wake_t = "";
            sleepItem.clear();
        }
        String sleep_quality = getSleepQuality(deep_sleep_times);
        sleep_qk_txt.setText(sleep_quality);
        setSleepChart();
        start_sleep_time.setText(sleep_t);
        end_sleep_time.setText(wake_t);
        float total = deep_sleep_times + somnolence_times;
        deep_sleep_bfb.setText(Math.round((deep_sleep_times / total * 100)) + "%");
        somnolence_sleep_bfb.setText(Math.round((somnolence_times / total * 100)) + "%");

        resetTextSize(total_sleep_time, getStringTime(total), 34);
        resetTextSize(deep_sleep_time, getStringTime(deep_sleep_times), 22);
        resetTextSize(somnolence_sleep_time, getStringTime(somnolence_times), 22);
        resetTextSize(sober_time, getStringTime(sober_times), 22);

        if (deep_sleep_times > 0){
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams1.weight = deep_sleep_times;
            deep_sleep_bgview.setLayoutParams(layoutParams1);
        }
        if (somnolence_times > 0) {
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams2.weight = somnolence_times;
            somnolence_sleep_bgview.setLayoutParams(layoutParams2);
        }
    }

    public void setSleepChart(){
        sChartLinear.removeAllViews();
        int totalItem = sleepItem.size();
        for (int i = 0; i < totalItem; i++) {
            HashMap<String, Object> sleeps = sleepItem.get(i);
            TextView textView = new TextView(context);
            if(sleeps.get("stype") == null){
                continue;
            }
            float stime = (float) sleeps.get("stime");
            int stype = (int) sleeps.get("stype");
            stime = stime/1000/60;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = stime;
            textView.setBackgroundResource(sTypeBg[stype-1]); //设置背景
            textView.setLayoutParams(layoutParams);
            sChartLinear.addView(textView);
        }
        if(totalItem > 0){
            dsleep_icon.setVisibility(View.VISIBLE);
            ssleep_icon.setVisibility(View.VISIBLE);
            sosleep_icon.setVisibility(View.VISIBLE);
        }else{
            dsleep_icon.setVisibility(View.GONE);
            ssleep_icon.setVisibility(View.GONE);
            sosleep_icon.setVisibility(View.GONE);
        }

    }

    public void resetTextSize(TextView view,String txt,int size){
        Spannable sp = new SpannableString(txt) ;
        sp.setSpan(new AbsoluteSizeSpan(size,true),0,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(size,true),3,5,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.BLACK),0,2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.BLACK),3,5,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(sp);
    }

    public String getStringTime(float value){
        int hour = (int) Math.floor(value/60);
        int minute =  (int) value%60;
        return returnshi(hour)+getString(R.string.hours_txt)+returnshi(minute)+getString(R.string.minute_txt);
    }
	
	public String getSleepQuality(float deep_sleep_times){
        String smqk = getString(R.string.none);
		if(deep_sleep_times<=0){
			return smqk;
		}
        smqk = getString(R.string.sleep_quality);
		if(deep_sleep_times >2*60){
			return smqk+getString(R.string.good_txt);//"良";
		}else if(deep_sleep_times >1*60){
			return smqk+getString(R.string.excellent_txt);//"优";
		}
		return smqk+getString(R.string.commonly_txt);//"一般";
	}


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            SleepData();
        }
    }
}

/*
* */
