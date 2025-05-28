package xfkj.fitpro.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.sql.Timestamp;
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

import static xfkj.fitpro.application.MyApplication.removeActivity_;
import static xfkj.fitpro.application.MyApplication.returnshi;
import static xfkj.fitpro.application.MyApplication.setWindowStatusBarColor;

public class MoreSleepActivity extends BaseActivity {

    private static String TAG = "MoreSleepActivity";
    private Map<String, Map> dsleep;
    private Map<String, List> sleepData;
    private Map<String, ArrayList<HashMap<String, Object>>> daySleeps;

    private Map<String, List> titleList;
    private HorizontalScrollView sleep_tabbar;
    private RadioGroup tabbarRadio;

    private ImageView msleep_back_btn;
    private String current_title_btn, current_tabber_btn;

    private LinearLayout dayBox;
    private TextView msleep_title;
    private int pre_select_id = -1;

    private int[] sTypeBg;
    private TextView sleep_qk_txt, start_sleep_time, end_sleep_time, total_sleep_time, deep_sleep_time, somnolence_sleep_time, sober_time, deep_sleep_bfb,
            somnolence_sleep_bfb, deep_sleep_bgview, somnolence_sleep_bgview, m_sober_sleep_bgview, sleep_title, dsleep_icon, ssleep_icon, sosleep_icon, tv_sober_times_percent;
    private LinearLayout sChartLinear;


    @Override
    protected void setActivityTitle() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void getLayoutToView() {
        setWindowStatusBarColor(this, getResources().getColor(R.color.sleep_background));
        setContentView(R.layout.activity_more_sleep);
    }

    @Override
    protected void initValues() {
        current_title_btn = "day";
        dsleep = new HashMap<>();
        sleepData = new HashMap<>();
        daySleeps = new HashMap<>();
        titleList = new HashMap<>();
        getSleepDatas();
    }

    public void initTabbar() {
        //   sleep_tabbar.scrollTo(0,0);
        List<String> tabbarList = titleList.get(current_title_btn);
        tabbarRadio.clearCheck();
        tabbarRadio.removeAllViews();
        final int totalList = tabbarList.size();
        for (int i = 0; i < totalList; i++) {
            String channel = tabbarList.get(i);
            String[] channels = channel.split("\\*");
            RadioButton radio = new RadioButton(this);
            radio.setId(i);
            radio.setButtonDrawable(null);
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            l.setMargins(20, 0, 20, 0);
            radio.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/IMPACT_0.TTF"));
            radio.setLayoutParams(l);
            radio.setTextSize(17);
            radio.setGravity(Gravity.CENTER);
            radio.setText(channels[1]);
            radio.setTag(channel);
            radio.setPadding(30, 0, 30, 5);
            radio.setTextColor(getResources().getColor(R.color.white));
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
                sleep_tabbar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);

        tabbarRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                if (rb == null || !rb.isChecked()) {
                    return;
                }
                Drawable drawable_news = getResources().getDrawable(R.drawable.ui_mstept_tabber_selector);
                drawable_news.setBounds(0, 0, rb.getMeasuredWidth() - rb.getPaddingLeft() - rb.getPaddingRight(), 5);
                rb.setCompoundDrawables(null, null, null, drawable_news);
                rb.setPadding(30, 0, 30, 5);
                current_tabber_btn = rb.getTag() + "";
                setChartValue();
                pre_select_id = i;
            }

        });
    }

    @Override
    protected void initViews() {
        sleep_qk_txt = findViewById(R.id.m_sleep_qk_txt);
        start_sleep_time = findViewById(R.id.m_start_sleep_time);
        end_sleep_time = findViewById(R.id.m_end_sleep_time);
        total_sleep_time = findViewById(R.id.m_total_sleep_time);
        deep_sleep_time = findViewById(R.id.m_deep_sleep_time);
        somnolence_sleep_time = findViewById(R.id.m_somnolence_sleep_time);
        sober_time = findViewById(R.id.m_sober_time);
        tv_sober_times_percent = findViewById(R.id.tv_sober_times_percent);
        deep_sleep_bfb = findViewById(R.id.m_deep_sleep_bfb);
        somnolence_sleep_bfb = findViewById(R.id.m_somnolence_sleep_bfb);
        deep_sleep_bgview = findViewById(R.id.m_deep_sleep_bgview);
        somnolence_sleep_bgview = findViewById(R.id.m_somnolence_sleep_bgview);
        m_sober_sleep_bgview = findViewById(R.id.m_sober_sleep_bgview);
        sChartLinear = findViewById(R.id.m_sChartLinear);
        dsleep_icon = findViewById(R.id.m_dsleep_icon);
        ssleep_icon = findViewById(R.id.m_ssleep_icon);
        sosleep_icon = findViewById(R.id.m_sosleep_icon);
        sTypeBg = new int[]{R.color.deep_sleep_background, R.color.somnolence_sleep_background, R.color.sober_sleep_background};

        dayBox = findViewById(R.id.sleep_day_box);

        msleep_title = findViewById(R.id.msleep_title);
        msleep_title.setText(getString(R.string.more_sleep));
        sleep_tabbar = (HorizontalScrollView) this.findViewById(R.id.hpv_menu);
        tabbarRadio = findViewById(R.id.tabbar_p_items);
        msleep_back_btn = findViewById(R.id.msleep_back_btn);
        getDsleeps();
        if (sleepData.size() > 0) {
            setChartValue();
        }
    }

    public void setChartValue() {
        Map<String, String> mSleep = dsleep.get(current_title_btn);
        String key = current_tabber_btn + "_" + current_title_btn + "_";
        dayBox.setVisibility(View.VISIBLE);
        float deep_sleep_times = 0;
        float somnolence_times = 0;
        float sober_times = 0;
        String sleep_times = "";
        String wake_times = "";
        ArrayList<HashMap<String, Object>> sleepItem = new ArrayList<HashMap<String, Object>>();
        if (mSleep.get("deep_t" + key) != null) {
            deep_sleep_times = Float.parseFloat(mSleep.get("deep_t" + key));
            somnolence_times = Float.parseFloat(mSleep.get("somnolence_t" + key));
            sober_times = Float.parseFloat(mSleep.get("sober_t" + key));
            sleep_times = mSleep.get("sleep_t" + key).toString();
            wake_times = mSleep.get("wake_t" + key).toString();
        }
        if (daySleeps.get(key) != null) {
            sleepItem = (ArrayList<HashMap<String, Object>>) daySleeps.get(key);
        }
        showView(deep_sleep_times, somnolence_times, sober_times, sleep_times, wake_times, sleepItem);
    }

    ;

    //获取数据
    private void getSleepDatas() {
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        int start = 18;
        int end = 12;
        String current = "none";
        List<Map> days = null;
        String sleep_times = "", wake_times = "";
        Cursor cursor = DBAccess.Query("select * from Sleep group by LongDate order by LongDate asc");
        if (cursor != null && cursor.getCount() > 0) {
            int total = cursor.getCount();
            while (cursor.moveToNext()) {
                total--;
                int stype = Integer.valueOf(cursor.getString(cursor.getColumnIndex("SleepTypes")));
                //    int id = Integer.valueOf(cursor.getInt(cursor.getColumnIndex("ID")));
                //    int sleepdata = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Data")));
                //    String stimes = cursor.getString(cursor.getColumnIndex("RevDate"));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(longDate);
                int year = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH) + 1;
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                String sday = "";
                if (h >= start) {
                    sday = year + "-" + returnshi(m) + "-" + returnshi(d);
                } else if (h < end) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天
                    year = calendar.get(Calendar.YEAR);
                    m = calendar.get(Calendar.MONTH) + 1;
                    d = calendar.get(Calendar.DAY_OF_MONTH);
                    sday = year + "-" + returnshi(m) + "-" + returnshi(d);
                }
                if (sday == "" || sday.isEmpty()) {
                    continue;
                }
                String pattern = sday + " 00:00:00";
                Timestamp stime = Timestamp.valueOf(pattern);
                Map<String, String> ds = new HashMap<>();
                sleep_times = returnshi(h) + ":" + returnshi(min);
                if (!current.equals(sday)) {
                    if (current != "none" && !current.equals("none")) {
                        days.get(0).put("wake_times", wake_times + "");
                        if (days.size() >= 6) {
                            sleepData.put(current, days);
                        }
                        ds.clear();
                    }
                    days = new ArrayList<Map>();
                    ds.put("year", year + "");
                    ds.put("month", m + "");
                    ds.put("day", d + "");
                    ds.put("sleep_times", sleep_times + "");
                    ds.put("slongDate", stime.getTime() + "");
                }
                wake_times = sleep_times;
                ds.put("stype", stype + "");
                ds.put("longDate", longDate + "");
                days.add(ds);
                current = sday;
                if (total <= 0) {
                    days.get(0).put("wake_times", wake_times + "");
                    if (days.size() >= 6) {
                        sleepData.put(current, days);
                    }
                }
            }
            cursor.close();
        } else {
            Toast toast = Toast.makeText(MoreSleepActivity.this, getString(R.string.no_more_data), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    protected void setViewsListener() {
        msleep_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeActivity_(MoreSleepActivity.this);
            }
        });

    }

    @Override
    protected void setViewsFunction() {

    }

    //按 日 周 月 区分数据
    public void getDsleeps() {
        List<String> tabbarList = new ArrayList<String>();
        Map<String, String> mSleep = new HashMap<>();
        String year = "";
        for (String dk : sleepData.keySet()) {
            List<Map> days = sleepData.get(dk);
            int m = 0, d = 0, h = 0;
            int deep_t = 0, somnolence_t = 0, sober_t = 0, wake_n = 0;
            long start_t = 0;
            int pstype = 0;
            String sleep_times = "", wake_times = "";
            long slongDate = 0;
            ArrayList<HashMap<String, Object>> sleepItem = new ArrayList<HashMap<String, Object>>();
            for (Map sleep : days) {
                HashMap<String, Object> sleeps = new HashMap<>();
                if (sleep.get("year") != null) {
                    year = sleep.get("year").toString();
                    m = Integer.parseInt(sleep.get("month").toString());
                    d = Integer.parseInt(sleep.get("day").toString());
                    slongDate = Long.parseLong(sleep.get("slongDate").toString());
                    sleep_times = sleep.get("sleep_times").toString();
                }
                long longDate = Long.parseLong(sleep.get("longDate").toString());
                int stype = Integer.parseInt(sleep.get("stype").toString());
                if (sleep.get("wake_times") != null) {
                    wake_times = sleep.get("wake_times").toString();
                }
                if (start_t == 0) {
                    start_t = longDate;
                    pstype = stype;
                } else if (stype == 3) {
                    wake_n++;
                }
                float t = longDate - start_t;
                if (pstype == 2 && (stype == 1 || stype == 3)) {//2到1是或者2到3都是浅睡时间
                    sleeps.put("stype", 2);
                    somnolence_t += t;
                } else if (pstype == 1 && stype == 2) {//1到2是深睡时间
                    sleeps.put("stype", 1);
                    deep_t += t;
                } else if (pstype == 3 && stype == 2) {//3到2是清醒时间
                    sleeps.put("stype", 3);
                    sober_t += t;
                }
                sleeps.put("stime", t);
                start_t = longDate;
                pstype = stype;
                if (t > 0) {
                    sleepItem.add(sleeps);
                }
            }
            //    Logdebug("getDsleeps","days--"+sleepItem);
            String title2 = year + "*";//年份区分
            String key2 = "";
            int nm = 0;
            if (current_title_btn.equals("day")) {
                title2 += returnshi(m) + "-" + returnshi(d);
                key2 = "";
                daySleeps.put(title2 + "_" + current_title_btn + "_" + key2, sleepItem);
            }
            if (tabbarList != null && !tabbarList.contains(title2)) {
                tabbarList.add(title2);
            }
            int min = mSleep.get(title2 + "_" + current_title_btn + "_min") != null ? Integer.valueOf(mSleep.get(title2 + "_" + current_title_btn + "_min")) : 31;
            int max = mSleep.get(title2 + "_" + current_title_btn + "_max") != null ? Integer.valueOf(mSleep.get(title2 + "_" + current_title_btn + "_max")) : 0;
            if (nm < min) {//当前月有数据的最小天数
                mSleep.put(title2 + "_" + current_title_btn + "_min", nm + "");
            }
            if (nm > max) {//当前月有数据的最大天数
                mSleep.put(title2 + "_" + current_title_btn + "_max", nm + "");
            }
            String key = title2 + "_" + current_title_btn + "_" + key2;
            deep_t = deep_t / 1000 / 60;
            somnolence_t = somnolence_t / 1000 / 60;
            sober_t = sober_t / 1000 / 60;

            mSleep.put("deep_t" + key, deep_t + "");
            mSleep.put("somnolence_t" + key, somnolence_t + "");
            mSleep.put("sober_t" + key, sober_t + "");

            mSleep.put("sleep_t" + key, sleep_times + "");
            mSleep.put("wake_t" + key, wake_times + "");
            mSleep.put("wake_n" + key, wake_n + "");
        }
        dsleep.put(current_title_btn, mSleep);
        Collections.sort(tabbarList);
        titleList.put(current_title_btn, tabbarList);
        initTabbar();
    }


    public void showView(float deep_sleep_times, float somnolence_times, float sober_times, String sleep_t, String wake_t, ArrayList<HashMap<String, Object>> sleepItem) {
        if (deep_sleep_times > somnolence_times) {//深睡时间大于浅睡时间不展示当天睡眠
            deep_sleep_times = somnolence_times = sober_times = 0;
            sleep_t = wake_t = "";
            sleepItem.clear();
        }
        String sleep_quality = getSleepQuality(deep_sleep_times);
        sleep_qk_txt.setText(sleep_quality);
        setSleepChart(sleepItem);
        start_sleep_time.setText(sleep_t);
        end_sleep_time.setText(wake_t);
        float total = deep_sleep_times + somnolence_times + sober_times;
        total_sleep_time.setText(resetTextSize(getStringTime(total), 34));
        total = (total <= 0) ? 1 : total;
        deep_sleep_bfb.setText(Math.round((deep_sleep_times / total * 100)) + "%");
        somnolence_sleep_bfb.setText(Math.round((somnolence_times / total * 100)) + "%");
        tv_sober_times_percent.setText(Math.round((sober_times / total * 100)) + "%");
        deep_sleep_time.setText(resetTextSize(getStringTime(deep_sleep_times), 22));
        somnolence_sleep_time.setText(resetTextSize(getStringTime(somnolence_times), 22));
        sober_time.setText(resetTextSize(getStringTime(sober_times), 22));

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.weight = Math.abs(deep_sleep_times);
        deep_sleep_bgview.setLayoutParams(layoutParams1);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.weight = Math.abs(somnolence_times);
        somnolence_sleep_bgview.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams3.weight = Math.abs(sober_times);
        m_sober_sleep_bgview.setLayoutParams(layoutParams3);
    }

    public void setSleepChart(ArrayList<HashMap<String, Object>> sleepItem) {
        sChartLinear.removeAllViews();
        int totalItem = sleepItem.size();
        for (int i = 0; i < totalItem; i++) {
            HashMap<String, Object> sleeps = sleepItem.get(i);
            TextView textView = new TextView(MoreSleepActivity.this);
            if (sleeps.get("stype") == null) {
                continue;
            }
            float stime = (float) sleeps.get("stime");
            int stype = (int) sleeps.get("stype");
            Log.i(TAG, "=====sleep type:" + stype);
            stime = stime / 1000 / 60;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = stime;
            textView.setBackgroundResource(sTypeBg[stype - 1]); //设置背景
            textView.setLayoutParams(layoutParams);
            sChartLinear.addView(textView);
        }
    }

    public Spannable resetTextSize(String txt, int size) {
        Spannable sp = new SpannableString(txt);
        sp.setSpan(new AbsoluteSizeSpan(size, true), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(size, true), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.BLACK), 3, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    public String getStringTime(float value) {
        int hour = (int) Math.floor(value / 60);
        int minute = (int) value % 60;
        return returnshi(hour) + getString(R.string.hours_txt) + returnshi(minute) + getString(R.string.minute_txt);
    }

    public String getSleepQuality(float deep_sleep_times) {
        if (deep_sleep_times <= 0) {
            return getString(R.string.none);
        }
        if (deep_sleep_times > 2 * 60) {
            return getString(R.string.good_txt);//"良";
        } else if (deep_sleep_times > 1 * 60) {
            return getString(R.string.excellent_txt);//"优";
        }
        return getString(R.string.commonly_txt);//"一般";
    }

}
