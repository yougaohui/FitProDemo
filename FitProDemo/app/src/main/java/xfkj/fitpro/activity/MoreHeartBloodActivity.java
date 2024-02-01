package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static xfkj.fitpro.application.MyApplication.removeActivity_;
import static xfkj.fitpro.application.MyApplication.returnshi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.db.SqliteDBAcces;
import xfkj.fitpro.view.MyGridView;

public class MoreHeartBloodActivity extends BaseActivity {

    private String mType = "heart";
    private static LineChart hbChart;
    private Map<String, ArrayList<HashMap<String, Object>>> hbItems;

    private static String TAG = "MoreHeartBloodActivity";
    private MyGridView bhgv;
    private Map<String, Map> hbdata;
    private Map<String, List> titleList;
    private Map<Integer, Map> mhbData;
    private HorizontalScrollView mhb_tabbar;
    private RadioGroup tabbarRadio,title_btn_box;

    private String current_title_btn,current_tabber_btn;
    private SimpleAdapter mAdapter;

    private int pre_select_id = -1;
    private ListView hb_lists;
    private ImageView mhb_back_btn;
    private TextView mbh_title;
    String title = "";

    @Override
    protected void setActivityTitle() {
        title = getString(R.string.more_heart);
        if(mType.equals("blood")){
            title = getString(R.string.more_blood);
        }
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_more_heart_blood);
    }

    @Override
    protected void initValues() {
        Intent intent = getIntent();
        mType = intent.getStringExtra("bhtype");
        hbdata = new HashMap<>();
        mhbData = new HashMap<>();
        titleList = new HashMap<>();
        hbItems = new HashMap<>();
        getHbDatas();
    }

    public void initTabbar(){
    //    mhb_tabbar.scrollTo(0,0);
        List<String> tabbarList = titleList.get(current_title_btn);
        tabbarRadio.clearCheck();
        tabbarRadio.removeAllViews();
        final int totalList = tabbarList.size();
        for (int i = 0; i < totalList; i++) {
            String channel = tabbarList.get(i);
            String[] channels = channel.split("\\*");
            RadioButton radio = new RadioButton(this);
            radio.setId(i);
            radio.setButtonDrawable(android.R.color.transparent);
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            l.setMargins(20,0,20,0);
            radio.setLayoutParams(l);
            radio.setTextSize(17);
            radio.setGravity(Gravity.CENTER);
            radio.setText(channels[1]);
            radio.setTag(channel);
            radio.setPadding(5,0,5,5);
            radio.setTextColor(getResources().getColor(R.color.black));
            if(totalList - i == 1){
                current_tabber_btn = channel;
            }
            tabbarRadio.addView(radio);
        }
        SDKTools.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabbarRadio.clearCheck();
                tabbarRadio.check(totalList - 1);
                mhb_tabbar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);
    }

    @Override
    protected void initViews() {
        mhb_back_btn = findViewById(R.id.mhb_back_btn);
        mbh_title = findViewById(R.id.mhb_title);
        mbh_title.setText(title);
        hbChart = findViewById(R.id.hb_chart);
        bhgv = findViewById(R.id.bhgv);
        hb_lists = findViewById(R.id.hb_lists);
        mhb_tabbar = (HorizontalScrollView) this.findViewById(R.id.hbsv_menu);
        tabbarRadio = findViewById(R.id.hb_tabbar_items);
        title_btn_box = findViewById(R.id.more_title_btns);
        RadioButton tbtn = findViewById(title_btn_box.getCheckedRadioButtonId());
        if(tbtn != null){
            current_title_btn = tbtn.getTag().toString();
        }else{
            current_title_btn = "day";
        }
        getHbs();
        setChartView();
        if(mhbData.size() == 0){
            showView(0,0,0,0,0,0);
            setChartValue();
        }
    }

    @Override
    protected void setViewsListener() {
        title_btn_box.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)findViewById(i);
                if(rb == null || !rb.isChecked())return;
                rb.setTextColor(getResources().getColor(R.color.white));
                int [] btn_ids  = new int[]{R.id.day_btn, R.id.week_btn,R.id.month_btn};
                for (int j = 0;j <= 2;j ++){
                    if (i == btn_ids[j]){
                        continue;
                    }
                    ((RadioButton) findViewById(btn_ids[j])).setTextColor(getResources().getColor(R.color.more_bh_color));
                }
                current_title_btn = rb.getTag().toString();
                Logdebug("setOnCheckedChangeListener","---current_title_btn--"+current_title_btn);
                if(hbdata.get(current_title_btn) == null){
                    getHbs();
                }else{
                    initTabbar();
                }
                setChartView();
            }
        });
        tabbarRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                if(rb == null || !rb.isChecked())return;
                rb.setTextColor(getResources().getColor(R.color.text1));
                Drawable drawable_news = getResources().getDrawable(R.drawable.ui_mstept_tabber_selector);
                drawable_news.setBounds(0, 0, rb.getMeasuredWidth()-rb.getPaddingLeft()-rb.getPaddingRight() , 5);
                rb.setCompoundDrawables(null, null, null, drawable_news);
                rb.setPadding(5,0,5,0);
                current_tabber_btn = rb.getTag()+"";
                setChartValue();
                RadioButton rb2 = (RadioButton)findViewById(pre_select_id);
                if(rb2 != null)
                    rb2.setTextColor(getResources().getColor(R.color.black));
                pre_select_id = i;
                Logdebug("setOnCheckedChangeListener","---current_tabber_btn--"+current_tabber_btn);
            }

        });

        mhb_back_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                removeActivity_(MoreHeartBloodActivity.this);
            }
        });
    }

    @Override
    protected void setViewsFunction() {

    }

    //获取数据
    private void getHbDatas() {
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Measure where Types = '"+mType+"' group by LongDate order by LongDate desc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                int hdata = cursor.getInt(cursor.getColumnIndex("Data"));
                int ddata = cursor.getInt(cursor.getColumnIndex("Blood2"));
                String revDate = cursor.getString(cursor.getColumnIndex("RevDate"));
                Map<String,String> s = new HashMap<>();
                s.put("ddata",ddata+"");
                s.put("hdata",hdata+"");
                s.put("revDate",revDate+"");
                s.put("longDate",longDate+"");
                mhbData.put(id,s);
            }
            cursor.close();
        }else{
            Toast toast = Toast.makeText(MoreHeartBloodActivity.this, getString(R.string.no_more_data), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //按 日 周 月 区分数据
    public void getHbs(){
        List<String> tabbarList = new ArrayList<String>();
        Map<String, Integer> mHb = new HashMap<>();
        ArrayList<HashMap<String, Object>> hbItem = new ArrayList<HashMap<String, Object>>();
        String current = "none";
        int total = mhbData.size();
        for (Map s : mhbData.values()) {
            long longDate = Long.parseLong(s.get("longDate").toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(longDate);
            int year = calendar.get(Calendar.YEAR);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            String month = returnshi(calendar.get(Calendar.MONTH) + 1);
            String day = returnshi(d);
            int ddata = Integer.parseInt(s.get("ddata").toString());
            int hdata = Integer.parseInt(s.get("hdata").toString());
            String revDate =s.get("revDate").toString();
            String title2 = year+"*";//年份区分
            String key2 = "";
            int nm = d;
            if(current_title_btn.equals("day")){
                title2 += month+"-"+day;
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                key2 =  "";
                nm = h;
            }else if(current_title_btn.equals("week")){
                int w = calendar.get(Calendar.DAY_OF_WEEK)-1;
                String week = returnshi(w);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 获取星期日开始时间
                title2 += returnshi(calendar.get(Calendar.MONTH) + 1)+"-"+returnshi(calendar.get(Calendar.DAY_OF_MONTH))+"/";
                calendar.set(Calendar. DAY_OF_WEEK, Calendar.SATURDAY );// 获取星期六结束时间
                title2 += returnshi(calendar.get(Calendar.MONTH) + 1)+"-"+returnshi(calendar.get(Calendar.DAY_OF_MONTH));
                key2 =  week+"";
                nm = w+1;
            }else if(current_title_btn.equals("month")){
                title2 += year+"-"+month;
                key2 =  day+"";
                nm = d;
            }



            if (tabbarList != null && !tabbarList.contains(title2)) {
                tabbarList.add(title2);
            }
            String key = title2+"_"+current_title_btn+"_"+key2;
            int maxh = 0,minh = hdata,maxb = 0,minb = ddata,totalh = 0,totalb = 0, num=0;
            if(mHb.get("maxh_"+key) != null ){
                maxh = mHb.get("maxh_"+key);
                minh = mHb.get("minh_"+key);
                maxb = mHb.get("maxb_"+key);
                minb = mHb.get("minb_"+key);
                totalh = mHb.get("totalh_"+key);
                totalb = mHb.get("totalb_"+key);
                num = mHb.get("num_"+key);
            }else{
                int min = mHb.get(title2 + "_" + current_title_btn + "_min") != null ? mHb.get(title2 + "_" + current_title_btn + "_min") : 31;
                int max = mHb.get(title2 + "_" + current_title_btn + "_max") != null ? mHb.get(title2 + "_" + current_title_btn + "_max") : 0;
                if(nm<min){//当前月有数据的最小天数
                    mHb.put(title2 + "_" + current_title_btn + "_min",nm);
                }
                if(nm>max){//当前月有数据的最大天数
                    mHb.put(title2 + "_" + current_title_btn + "_max",nm);
                }
            }
            if(minh>hdata && hdata>0){
                minh = hdata;
            }
            mHb.put("minh_"+key, minh);
            if(maxh<hdata){
                maxh = hdata;
            }
            mHb.put("maxh_"+key, maxh);
            if(minb>ddata && ddata>0){
                minb = ddata;
            }
            mHb.put("minb_"+key, minb);
            if(maxb<ddata){
                maxb = ddata;
            }
            mHb.put("maxb_"+key, maxb);
            mHb.put("totalh_"+key, totalh + hdata);
            mHb.put("totalb_"+key, totalb + ddata);
            num = num+1;
            mHb.put("num_"+key,num);

            if(current_title_btn.equals("day")) {
                HashMap<String, Object> hbs = new HashMap<>();
                total--;
                if (!current.equals(key)) {
                    if (current != "none" && !current.equals("none")) {
                        Collections.reverse(hbItem);
                        hbItems.put(current, hbItem);
                        hbs.clear();
                    }
                    hbItem = new ArrayList<HashMap<String, Object>>();
                }
                hbs.put("ddata", ddata);
                hbs.put("hdata", hdata);
                hbs.put("revDate", revDate);
                hbItem.add(hbs);
                current = key;
                if (total <= 0) {
                    Collections.reverse(hbItem);
                    hbItems.put(current, hbItem);
                }
            }


        }
        hbdata.put(current_title_btn,mHb);
        Collections.sort(tabbarList);
        titleList.put(current_title_btn,tabbarList);
        initTabbar();
    }

    public void showView(int maxh,int minh,int totalh,int maxb,int minb,int totalb){
        if(mType.equals("blood")){
            bhgv.setNumColumns(4);
            bhgv.setBackgroundColor(getResources().getColor(R.color.line_color));
            bhgv.setHorizontalSpacing(1);
            bhgv.setVerticalSpacing(1);
            bhgv.setPadding(1,1,1,1);
            String vals[] = new String[]{getString(R.string.b_type),getString(R.string.high_b),getString(R.string.low_b),getString(R.string.avg_hb),getString(R.string.diastolic),maxh+"",minh+"",(totalh)+"",getString(R.string.systolic),maxb+"",minb+"",(totalb)+""};
            ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
            for (int n = 0; n < 12; n++)
            {
                HashMap<String, Object> map = new HashMap<>();
                map.put("texts", vals[n]);
                mData.add(map);
            }
            String[] from = new String[]{"texts"};
            int[] to = new int[]{R.id.text_val};
            SimpleAdapter adapter = new SimpleAdapter(this, mData, R.layout.layout_text_item, from, to);
            bhgv.setAdapter(adapter);
            int start = bhgv.getFirstVisiblePosition();
            View convertView = bhgv.getChildAt(start);
            if(convertView != null){
           //     convertView.setBackgroundColor(getResources().getColor(R.color.black));

                adapter.getView(start,convertView,bhgv).setBackgroundColor(getResources().getColor(R.color.black));

            }
        }else{
            bhgv.setNumColumns(3);
            bhgv.setBackgroundColor(getResources().getColor(R.color.white));
            bhgv.setHorizontalSpacing(0);
            bhgv.setVerticalSpacing(0);
            bhgv.setPadding(0,0,0,0);
            int imgs[] = new int[]{R.drawable.avg_hb_icon, R.drawable.max_hb_icon, R.drawable.min_hb_icon};
            String titles[] = new String[]{getString(R.string.avg_hb),getString(R.string.max_hb),getString(R.string.min_hb)};
            String vals[] = new String[]{(totalh)+"",maxh+"",minh+""};
            ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
            for (int n = 0; n < 3; n++)
            {
                HashMap<String, Object> map = new HashMap<>();
                map.put("img", imgs[n]);
                map.put("headvalue", titles[n]);
                map.put("value", vals[n]);
                map.put("danwei", "");
                mData.add(map);
            }
            String[] from = new String[]{"img", "headvalue", "value","danwei"};
            int[] to = new int[]{R.id.iv, R.id.labHead, R.id.labBottom,R.id.labDanwei};
            SimpleAdapter adapter = new SimpleAdapter(this, mData, R.layout.layout_hb_more_item, from, to);
            bhgv.setAdapter(adapter);
        }


    }


    /**
     * 初始化图表
     */
    public void setChartValue(){
        //模拟数据
        Map<String, Integer> mHb = hbdata.get(current_title_btn);
        String key = current_tabber_btn+"_"+current_title_btn+"_";
        XAxis xAxis = hbChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getChartXval((int)value); //mList为存有月份的集合
            }
        });
        xAxis.setAxisMinimum(0);
        int maxh = 0,minh = 0,maxb = 0,minb = 0,totalh = 0,totalb = 0,num=1;
        int barNum = 0;
        int starn = 0;
        List<Entry> hvalues = new ArrayList<>();
        List<Entry> bvalues = new ArrayList<>();
        if(current_title_btn.equals("day")){
            final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> hbItem = hbItems.get(key);
            if(hbItem != null && hbItem.size()>0){
                int items = hbItem.size();
                for (int j = 0;j<items;j++) {
                    HashMap<String, Object> hbs = hbItem.get(j);
                    int hv = 0,bv = 0;
                    if (hbs.get("hdata") != null){
                        hv = Integer.valueOf(hbs.get("hdata").toString());
                        bv = Integer.valueOf(hbs.get("ddata").toString());
                    }
                    if(hv>0){
                        hvalues.add(new Entry((float)(j),(float)hv));
                    }
                    if(bv>0){
                        bvalues.add(new Entry((float)(j),(float)bv));
                    }
                    if(current_title_btn.equals("day") && hv > 0) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("time_icon", R.drawable.time_icon);// 图像资源的ID
                        map.put("time_val", hbs.get("revDate").toString());
                        if (mType.equals("blood")) {
                            map.put("hb_val", bv + "/" + hv);//测量数据
                        } else {
                            map.put("hb_val", hv + "");//测量数据
                        }
                        listItem.add(map);
                    }
                    xAxis.setAxisMaximum(items-1);
                    xAxis.setLabelCount(items,true);
                }
            }
            if(mHb.get("maxh_"+key) != null){
                maxh = Integer.valueOf(mHb.get("maxh_"+key));
                minh = Integer.valueOf(mHb.get("minh_"+key));
                maxb = Integer.valueOf(mHb.get("maxb_"+key));
                minb = Integer.valueOf(mHb.get("minb_"+key));
                totalh = Integer.valueOf(mHb.get("totalh_"+key));
                totalb = Integer.valueOf(mHb.get("totalb_"+key));
                num = Integer.valueOf(mHb.get("num_"+key));
                totalh = totalh / (num>1?num:1);
                totalb = totalb / (num>1?num:1);
            }
            hb_lists.setVisibility(View.VISIBLE);
            mAdapter = new SimpleAdapter(MoreHeartBloodActivity.this, listItem, R.layout.layout_heat_item, new String[]{"day_icon", "day_val", "time_icon", "time_val", "hb_val"}, new int[]{R.id.day_icon, R.id.day_val, R.id.time_icon, R.id.time_val, R.id.hb_val});
            hb_lists.setAdapter(mAdapter);
        }else{
            hb_lists.setVisibility(View.GONE);
            if (current_title_btn.equals("week")){
                barNum = 6;
                xAxis.setLabelCount(7,true);
                xAxis.setAxisMaximum(6);
            }else if (current_title_btn.equals("month")){
                starn += mHb.get(key+"min") != null ? mHb.get(key+"min") : 1;
                barNum = mHb.get(key+"max") != null ? mHb.get(key+"max") : 31;
                Logdebug("setChartValue","--starn--"+starn+"--barNum--"+barNum);
                if(starn>10){
                    starn -= 5;
                }else{
                    starn = 1;
                }
                if(barNum <20){
                    barNum += 5;
                }
                xAxis.setAxisMaximum(barNum);
                xAxis.setAxisMinimum(starn);
                xAxis.setLabelCount(barNum-starn+1,true);
            }
            int maxh1 = 0,minh1 = 0,maxb1 = 0,minb1 = 0,totalh1 = 0,totalb1 = 0,num1=0,j = 0;
            for (int i = starn; i <= barNum; i++) {
                String k = key + ((i < 10) ? "0" + i : i);
                if(mHb.get("maxh_"+k) != null){
                    maxh1 = Integer.valueOf(mHb.get("maxh_"+k));
                    minh1 = Integer.valueOf(mHb.get("minh_"+k));
                    maxb1 = Integer.valueOf(mHb.get("maxb_"+k));
                    minb1 = Integer.valueOf(mHb.get("minb_"+k));
                    totalh1 = Integer.valueOf(mHb.get("totalh_"+k));
                    totalb1 = Integer.valueOf(mHb.get("totalb_"+k));
                    num1 = Integer.valueOf(mHb.get("num_"+k));
                    totalh1 = totalh1 / (num1>1?num1:1);
                    totalb1 = totalb1 / (num1>1?num1:1);
                    if(totalh1>0){
                        hvalues.add(new Entry((float)(i),(float)totalh1));
                    }
                    if(totalb1>0){
                        bvalues.add(new Entry((float)(i),(float)totalb1));
                    }
                    totalh += totalh1;
                    totalb += totalb1;
                    j++;
                }
                if(maxh < maxh1){
                    maxh = maxh1;
                }
                if(maxb < maxb1){
                    maxb = maxb1;
                }
                if((minh > minh1 || minh == 0) && minh1 > 0){
                    minh = minh1;
                }
                if((minb > minb1 || minb == 0) && minb1 > 0){
                    minb = minb1;
                }
            }
            if(j>0){
                totalh = totalh/j;
                totalb = totalb/j;
            }
        }
        showView(maxh,minh,totalh,maxb,minb,totalb);
        YAxis yAxis = hbChart.getAxisLeft();
        // 设置y轴数据的位置
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(maxh+50);
        yAxis.setLabelCount(5,true);
        if(hvalues.size()<=0){
            hvalues.add(new Entry(0,0));
        }
        LineDataSet dataSet = new LineDataSet(hvalues, "");
        // 设置曲线颜色
        dataSet.setColor(R.color.more_bh_color);
        dataSet.setCircleColor(R.color.more_bh_color);// 圆形折点的颜色
        // 设置平滑曲线
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        // 不显示坐标点的数据
        dataSet.setDrawValues(false);
        // 不显示定位线
        dataSet.setHighlightEnabled(false);

        dataSet.setLineWidth(1.75f); // 线宽

        LineData lineData = new LineData(dataSet);
        if(bvalues.size()>0){
            LineDataSet dataSet2 = new LineDataSet(bvalues, "");
            // 设置曲线颜色
            dataSet2.setColor(Color.RED);
            dataSet2.setCircleColor(Color.BLACK);// 圆形折点的颜色
            // 设置平滑曲线
            dataSet2.setMode(LineDataSet.Mode.LINEAR);
            // 不显示坐标点的数据
            dataSet2.setDrawValues(false);
            // 不显示定位线
            dataSet2.setHighlightEnabled(false);
            dataSet2.setLineWidth(1.75f); // 线宽
            lineData.addDataSet(dataSet2);
        }
        hbChart.setData(lineData);
        hbChart.invalidate();

    }

    public String getChartXval(int value){
        String res = returnshi(value);
        if(current_title_btn.equals("week")){
            final String [] weeks = new String[]{getString(R.string.sun_txt),getString(R.string.mon_txt),getString(R.string.tue_txt),getString(R.string.wed_txt),getString(R.string.thu_txt),getString(R.string.fri_txt),getString(R.string.sat_txt)};
            res = (value>=0 && value<=6) ? weeks[value] : "";
        }
        return res;
    }

    /*
    *
    *
        Collections.reverse(yvals);
        List<Entry> yvalues = new ArrayList<>();
        List<Entry> yvalues2 = new ArrayList<>();
        yvalues.add(new Entry(0,0));
        int ynum = yvals.size();
        for(int j = 0;j<ynum;j++)
        {
            yvalues.add(new Entry((float)(j+1),(float)yvals.get(j)));
        }
    * */

    /**
     * 初始化图表
     *
     * @return 初始化后的图表
     */
    public void setChartView()
    {
        // 不显示数据描述
        hbChart.getDescription().setEnabled(false);
        // 不可以缩放
        hbChart.setScaleEnabled(false);
        // 不显示y轴右边的值
        hbChart.getAxisRight().setEnabled(false);
        // 不显示图例
        hbChart.setExtraOffsets(15,25,20,15);
        hbChart.setNoDataText(getString(R.string.no_more_data));
        //是否显示边
        hbChart.setDrawBorders(true);
        hbChart.setBorderColor(R.color.gray);
        Legend legend = hbChart.getLegend();
        legend.setEnabled(false);
        XAxis xAxis = hbChart.getXAxis();
        // 不显示x轴
        xAxis.setDrawAxisLine(true);
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
