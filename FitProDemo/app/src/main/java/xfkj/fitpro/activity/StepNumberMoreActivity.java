package xfkj.fitpro.activity;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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

import static xfkj.fitpro.application.MyApplication.returnshi;

public class StepNumberMoreActivity extends BaseActivity {

    private static String TAG = "StepNumberMoreActivity";
    private GridView gv;
    private BarChart sChart;
    private Map<String, Map> dstep;
    private Map<String, List> titleList;
    private Map<Integer, Map> stepData;
    private HorizontalScrollView step_tabbar;
    private RadioGroup tabbarRadio, title_btn_box;
    private ProgressBar mProgressBar;
    private FrameLayout mFrmProbar;

    private String current_title_btn, current_tabber_btn;

    private int pre_select_id = -1;

    private final int MSG_HIDE_LOADVIEW = 0x1;//隐藏加载数据ui

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE_LOADVIEW) {
                mFrmProbar.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mHandler.removeMessages(MSG_HIDE_LOADVIEW);
            }
        }
    };

    @Override
    protected void setActivityTitle() {
        initTitle();
        String title = getString(R.string.more_stept);
        setTitle(title, StepNumberMoreActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_step_number_more);
    }

    @Override
    protected void initValues() {
        dstep = new HashMap<>();
        stepData = new HashMap<>();
        titleList = new HashMap<>();
        getSteptDatas();
        //两秒后隐藏加载数据动画，防止显示没有数据的情况
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_LOADVIEW, 2000);
    }


    public void initTabbar() {
        //step_tabbar.scrollTo(0,0);
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
            l.setMargins(20, 0, 20, 0);
            radio.setLayoutParams(l);
            radio.setTextSize(17);
            radio.setGravity(Gravity.CENTER);
            radio.setText(channels[1]);
            radio.setTag(channel);
            radio.setPadding(5, 0, 5, 5);
            radio.setTextColor(getResources().getColor(R.color.black));
            if (totalList - i == 1) {
                current_tabber_btn = channel;
            }
            tabbarRadio.addView(radio);
        }
        SDKTools.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabbarRadio.clearCheck();
                tabbarRadio.check(totalList - 1);
                step_tabbar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);
    }

    @Override
    protected void initViews() {
        sChart = findViewById(R.id.stepts_chart);
        gv = findViewById(R.id.gv);
        step_tabbar = this.findViewById(R.id.hsv_menu);
        tabbarRadio = findViewById(R.id.tabbar_items);
        title_btn_box = findViewById(R.id.ms_title_btns);
        mProgressBar = findViewById(R.id.progressBar2);
        mFrmProbar = findViewById(R.id.frm_progress);
        RadioButton tbtn = findViewById(title_btn_box.getCheckedRadioButtonId());
        if (tbtn != null) {
            current_title_btn = tbtn.getTag().toString();
        } else {
            current_title_btn = "day";
        }
        getDstepts();
        setChartView();
        if (stepData.size() == 0) {
            showView(0, 0, 0, 0);
            setChartValue();
        }
    }

    @Override
    protected void setViewsListener() {
        title_btn_box.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) findViewById(i);
                if (rb == null || !rb.isChecked()) return;
                rb.setTextColor(getResources().getColor(R.color.white));
                int[] btn_ids = new int[]{R.id.day_btn, R.id.week_btn, R.id.month_btn};
                for (int j = 0; j <= 2; j++) {
                    if (i == btn_ids[j]) {
                        continue;
                    }
                    ((RadioButton) findViewById(btn_ids[j])).setTextColor(getResources().getColor(R.color.text1));
                }

                current_title_btn = rb.getTag().toString();
                if (dstep.get(current_title_btn) == null) {
                    getDstepts();
                } else {
                    initTabbar();
                }
                //    setChartView();
            }
        });
        tabbarRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                if (rb == null || !rb.isChecked()) {
                    setChartValue();
                    return;
                }
                rb.setTextColor(getResources().getColor(R.color.text1));
                Drawable drawable_news = getResources().getDrawable(R.drawable.ui_mstept_tabber_selector);
                drawable_news.setBounds(0, 0, rb.getMeasuredWidth() - rb.getPaddingLeft() - rb.getPaddingRight(), 5);
                rb.setCompoundDrawables(null, null, null, drawable_news);
                rb.setPadding(5, 0, 5, 0);
                current_tabber_btn = rb.getTag() + "";
                setChartValue();
                RadioButton rb2 = (RadioButton) findViewById(pre_select_id);
                if (rb2 != null)
                    rb2.setTextColor(getResources().getColor(R.color.black));
                pre_select_id = i;
            }

        });
    }

    @Override
    protected void setViewsFunction() {

    }


    //获取数据
    private void getSteptDatas() {
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Step" + " group by LongDate order by LongDate desc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                int stept = cursor.getInt(cursor.getColumnIndex("Steps"));
                int calory = cursor.getInt(cursor.getColumnIndex("Calory"));
                int distance = cursor.getInt(cursor.getColumnIndex("Distance"));
                String sportDate = cursor.getString(cursor.getColumnIndex("SportDate"));
                Map<String, String> s = new HashMap<>();
                s.put("stept", stept + "");
                s.put("calory", calory + "");
                s.put("distance", distance + "");
                s.put("sportDate", sportDate + "");
                s.put("longDate", longDate + "");
                stepData.put(id, s);
            }
            cursor.close();
        } else {
            Toast toast = Toast.makeText(StepNumberMoreActivity.this, getString(R.string.no_more_data), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //按 日 周 月 区分数据
    public void getDstepts() {
        List<String> tabbarList = new ArrayList<String>();
        Map<String, Integer> mStep = new HashMap<>();
        for (Map s : stepData.values()) {
            long longDate = Long.parseLong(s.get("longDate").toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(longDate);
            int year = calendar.get(Calendar.YEAR);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            String month = returnshi(calendar.get(Calendar.MONTH) + 1);
            String day = returnshi(d);
            int stept = Integer.parseInt(s.get("stept").toString());
            int calory = Integer.parseInt(s.get("calory").toString());
            int distance = Integer.parseInt(s.get("distance").toString());

            String title2 = year + "*";//年份区分
            String key2 = "";
            int nm = d;
            if (current_title_btn.equals("day")) {
                title2 += month + "-" + day;
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                String hour = returnshi(h);
                key2 = hour + "";
                nm = h;
            } else if (current_title_btn.equals("week")) {
                int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                String week = returnshi(w);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 获取星期日开始时间
                title2 += returnshi(calendar.get(Calendar.MONTH) + 1) + "-" + returnshi(calendar.get(Calendar.DAY_OF_MONTH)) + "/";
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);// 获取星期六结束时间
                title2 += returnshi(calendar.get(Calendar.MONTH) + 1) + "-" + returnshi(calendar.get(Calendar.DAY_OF_MONTH));
                key2 = week + "";
                nm = w + 1;
            } else if (current_title_btn.equals("month")) {
                title2 += year + "-" + month;
                key2 = day + "";
                nm = d;
            }
            if (tabbarList != null && !tabbarList.contains(title2)) {
                tabbarList.add(title2);
            }
            String key = title2 + "_" + current_title_btn + "_" + key2;
            int ms = 0, mc = 0, md = 0;
            if (mStep.get("s_" + key) != null) {
                ms = mStep.get("s_" + key);
                mc = mStep.get("c_" + key);
                md = mStep.get("d_" + key);
            } else {
                int min = mStep.get(title2 + "_" + current_title_btn + "_min") != null ? mStep.get(title2 + "_" + current_title_btn + "_min") : 31;
                int max = mStep.get(title2 + "_" + current_title_btn + "_max") != null ? mStep.get(title2 + "_" + current_title_btn + "_max") : 0;
                if (nm < min) {//当前月有数据的最小天数
                    mStep.put(title2 + "_" + current_title_btn + "_min", nm);
                }
                if (nm > max) {//当前月有数据的最大天数
                    mStep.put(title2 + "_" + current_title_btn + "_max", nm);
                }
            }
            mStep.put("s_" + key, ms + stept);
            mStep.put("c_" + key, mc + calory);
            mStep.put("d_" + key, md + distance);
        }
        dstep.put(current_title_btn, mStep);
        Collections.sort(tabbarList);
        titleList.put(current_title_btn, tabbarList);
        initTabbar();
    }


    public void showView(int stype, int s, int c, int d) {
        int imgs[] = new int[]{R.drawable.total_steps, R.drawable.total_distance, R.drawable.total_calorie};
        String titles[] = new String[]{getString(R.string.m_t_step), getString(R.string.m_t_distance), getString(R.string.m_t_calory)};
        if (stype == 1) {
            titles = new String[]{getString(R.string.m_p_step), getString(R.string.m_p_distance), getString(R.string.m_p_calory)};
        }
        String vals[] = new String[]{(s) + "", formatDouble(Double.valueOf(((float) d / 1000))) + "", formatDouble(Double.valueOf(((float) c / 1000))) + ""};
        String units[] = new String[]{getString(R.string.step), "km", getString(R.string.kcary_txt)};

        ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
        for (int n = 0; n < 3; n++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img", imgs[n]);
            map.put("headvalue", titles[n]);
            map.put("value", vals[n]);
            map.put("bottomvalue", units[n]);
            mData.add(map);
        }
        String[] from = new String[]{"img", "headvalue", "value", "bottomvalue"};
        int[] to = new int[]{R.id.iv, R.id.labHead, R.id.labBottom, R.id.labDanwei};
        SimpleAdapter adapter = new SimpleAdapter(this, mData, R.layout.layout_step_more_item, from, to);
        gv.setAdapter(adapter);
    }

    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    private String formatDouble(Double doubles) {
        DecimalFormat df1 = new DecimalFormat("#.#");
        df1.setRoundingMode(RoundingMode.FLOOR);
        return df1.format(doubles);
    }

    /**
     * 初始化图表
     */
    public void setChartValue() {
        //模拟数据
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        Map<String, Integer> mStep = dstep.get(current_title_btn);
        String key = current_tabber_btn + "_" + current_title_btn + "_";
        XAxis xAxis = sChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getChartXval((int) value); //mList为存有月份的集合
            }
        });
        int barNum = 0;
        int starn = 0;
        if (current_title_btn.equals("day")) {
            barNum = 23;
            starn = 1;
            xAxis.setLabelCount(23);
        } else if (current_title_btn.equals("week")) {
            barNum = 6;
            xAxis.setLabelCount(6);
        } else if (current_title_btn.equals("month")) {
            starn = 1;
            barNum = 31;
            xAxis.setLabelCount(barNum);
        }

        int ms = 0, mc = 0, md = 0;
        for (int i = starn; i <= barNum; i++) {
            String k = key + ((i < 10) ? "0" + i : i);
            int hs = mStep.get("s_" + k) != null ? mStep.get("s_" + k) : 0;
            if (mStep.get("s_" + k) != null) {
                ms += hs;
                mc += mStep.get("c_" + k);
                md += mStep.get("d_" + k);
            }
            yVals1.add(new BarEntry(i, hs));
        }
        int stype = 0;
        if (!current_title_btn.equals("day")) {
            stype = 1;
            starn = mStep.get(key + "min") != null ? mStep.get(key + "min") : 1;
            barNum = mStep.get(key + "max") != null ? mStep.get(key + "max") : 31;
            ms = ms / (barNum - starn + 1);
            mc = mc / (barNum - starn + 1);
            md = md / (barNum - starn + 1);
        }
        showView(stype, ms, mc, md);

        BarDataSet set1 = new BarDataSet(yVals1, "");
        //设置多彩 也可以单一颜色
        int color_id = getResources().getColor(R.color.theme_color);
        set1.setColors(color_id);
        set1.setDrawValues(false);
        set1.setFormLineWidth(0.8f);
        set1.setFormSize(15.f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        sChart.setData(data);
        sChart.setFitBars(true);
        sChart.invalidate();
    }

    public String getChartXval(int value) {
        String res;
        if (current_title_btn.equals("week")) {
            final String[] weeks = new String[]{getString(R.string.sun_txt), getString(R.string.mon_txt), getString(R.string.tue_txt), getString(R.string.wed_txt), getString(R.string.thu_txt), getString(R.string.fri_txt), getString(R.string.sat_txt)};
            res = (value >= 0 && value <= 6) ? weeks[value] : "";
        } else {
            if (0 == value) {//不存在0天的情况
                return "";
            }
            res = returnshi(value);
        }
        return res;
    }


    /**
     * 初始化图表
     */
    public void setChartView() {
        XAxis xAxis = sChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.enableGridDashedLine(1, 2, 1);
        xAxis.setGridColor(R.color.gray);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(6);
        xAxis.setXOffset(0);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = sChart.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisMinimum(0f);

        yAxis.enableGridDashedLine(8, 2, 1);
        yAxis.setGridColor(R.color.gray);
        yAxis.setTextSize(6);
        yAxis.setSpaceBottom(0);

        sChart.setNoDataText(getString(R.string.no_more_data));
        sChart.getDescription().setEnabled(false);
        sChart.getAxisRight().setEnabled(false);
        sChart.getLegend().setEnabled(false);
        sChart.setDrawBorders(false);
        sChart.setExtraOffsets(10, 0, 20, 10);//设置视图窗口大小
        sChart.animateX(1500);//数据显示动画，从左往右依次显示
        sChart.setTouchEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_HIDE_LOADVIEW);
    }
}
