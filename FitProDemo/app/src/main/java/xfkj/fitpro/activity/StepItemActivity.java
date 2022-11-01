package xfkj.fitpro.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.db.SqliteDBAcces;

public class StepItemActivity extends BaseActivity {

    private static final String TAG = StepItemActivity.class.getSimpleName();
    private ListView listView;
    private TextView textView,pre_day,next_day,step_item_day;
    private ArrayList<String> list;
    private StepAdapter adapter;
    private int d = 0;

    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle("15分钟列表",StepItemActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_step_item);
    }

    @Override
    protected void initValues() {
        list = new ArrayList();
    }

    @Override
    protected void initViews() {
        listView = findViewById(R.id.stept_item_list_view);
        textView = findViewById(R.id.total_step_txt);
        pre_day = findViewById(R.id.pre_day_txt);
        next_day = findViewById(R.id.next_day_txt);
        step_item_day = findViewById(R.id.step_item_day_txt);
        adapter = new StepAdapter(this, list);
        listView.setAdapter(adapter);
        getDatas(d);
    }

    @Override
    protected void setViewsListener() {
        pre_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = d-1;
                getDatas(d);
            }
        });
        next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = d+1;
                getDatas(d);
            }
        });
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

    public void getDatas(int d){
        list.clear();
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, d);//取当前日期的前一天
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month2 = month<10?"0"+month:month+"";
        String day2 = day<10?"0"+day:day+"";

        String today2 = year+"-"+month2+"-"+day2;
        Cursor cursor = DBAccess.Query("select * from Step where SportDate='" + today2 + "' group by LongDate order by id desc");
        //    Map<String, Integer> hstep = new HashMap<>();
        Double totalDistance = 0.0;
        int totalSteps = 0,totalCalory=0;
        //模拟数据
        if (cursor != null && cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                int offset = cursor.getInt(cursor.getColumnIndex("Offset"));
                int hours = (int) Math.floor(offset * 15 / 60);
                int minute = offset * 15 % 60;
                int stept = cursor.getInt(cursor.getColumnIndex("Steps"));
                //    int hs = hstep.get("hourStep_" + hours) != null ? hstep.get("hourStep_" + hours) : 0;
                //    hstep.put("hourStep_" + hours, hs + stept);
                totalSteps += stept;

                int calory = cursor.getInt(cursor.getColumnIndex("Calory"));
                int distance = cursor.getInt(cursor.getColumnIndex("Distance"));
                int mode = cursor.getInt(cursor.getColumnIndex("Mode"));
                totalDistance += distance;
                totalCalory += calory;
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String sportDate = cursor.getString(cursor.getColumnIndex("SportDate"));
                String stepItem = sportDate+" "+hours+":"+minute+"-步:"+ stept +",-卡:"+calory+",-距:"+distance+",-类型:"+mode;
                list.add(stepItem);
            }
            cursor.close();
        }
        step_item_day.setText(today2);
        Collections.reverse(list);
        textView.setText("步数:"+totalSteps+",卡里路:"+totalCalory+",距离:"+totalDistance);
        adapter.notifyDataSetChanged();
    }


}
