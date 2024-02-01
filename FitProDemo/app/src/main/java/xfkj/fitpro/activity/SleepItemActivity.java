package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.utils.DateUtils.getCalendars;
import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.db.SqliteDBAcces;

public class SleepItemActivity extends BaseActivity {

    private ListView listView;
    private TextView textView;
    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle("睡眠列表",SleepItemActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_sleep_item);
    }

    @Override
    protected void initValues() {
    }

    @Override
    protected void initViews() {
        listView = findViewById(R.id.sleep_item_list_view);
        textView = findViewById(R.id.total_sleep_txt);
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
        Cursor cursor = DBAccess.Query("select * from Sleep where LongDate>=" + stime.getTime() + " and LongDate <= " + etime.getTime() + " order by LongDate asc");
        //    Map<String, Integer> hstep = new HashMap<>();
        int deep_sleep_times = 0, somnolence_times = 0, sober_times = 0, wake_nums = 0;
        long start_sleep_data=0;
        String sleep_times = "", wake_times = "";
        int total = 1;
        //模拟数据
        ArrayList<String> list = new ArrayList();
        if (cursor != null && cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                int stype = Integer.valueOf(cursor.getString(cursor.getColumnIndex("SleepTypes")));
                int sleepdata = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Data")));
                String stimes = cursor.getString(cursor.getColumnIndex("RevDate"));
            //    long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                if(start_sleep_data == 0){
                    start_sleep_data = sleepdata;
                }
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                //Logdebug(TAG, "id-->" + id + "--sleepdata-->" + sleepdata + "--stype-->" + stype + "--stimes-->" + stimes + "--longdata-->" + cursor.getString(cursor.getColumnIndex("LongDate")) );
                if (stype == 1)
                {//0x01:深度睡眠
                    deep_sleep_times += (sleepdata-start_sleep_data);
                } else {
                    if (sleep_times == "")
                    {
                        sleep_times = stimes;
                    }
                    if (stype == 2)
                    {//0x02:轻度睡眠
                        somnolence_times += (sleepdata-start_sleep_data);
                    } else if (stype == 3)
                    {//0x03:未睡眠
                        wake_times = stimes;
                        sober_times += (sleepdata-start_sleep_data);
                        wake_nums++;
                    }
                }
                String stepItem = id+"-"+stimes+"-类型-"+ stype +"-当前时间-"+sleepdata+"-睡眠时间-"+(sleepdata-start_sleep_data);
                list.add(stepItem);
                start_sleep_data = sleepdata;
            }
            cursor.close();
        //    deep_sleep_times=new Long(deep_sleep_times/1000/60).intValue();
        //    somnolence_times=new Long(somnolence_times/1000/60).intValue();
        //    sober_times=new Long(sober_times/1000/60).intValue();
            total = deep_sleep_times+somnolence_times+sober_times;


        }
        cursor.close();
        Collections.reverse(list);
        StepAdapter adapter = new StepAdapter(this, list);
        listView.setAdapter(adapter);
        textView.setText("总睡眠:"+total+",清醒:"+sober_times+",深睡:"+deep_sleep_times+",浅睡:"+somnolence_times);
    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }


    public class StepAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;//布局填充器
        private List<String> list;//数据源

        public StepAdapter(Context context, List list)
        {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View view2, ViewGroup viewGroup)
        {
            View view = inflater.inflate(R.layout.list_item, null);
            TextView detail = (TextView) view.findViewById(R.id.details);//获取该布局内的文本视图
            detail.setText(list.get(position).toString());
            return view;
        }
    }

}
