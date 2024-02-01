package xfkj.fitpro.fragment;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.utils.DateUtils.getCalendars;
import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;
import static xfkj.fitpro.application.MyApplication.returnshi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.LogUtils;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.MeasureActivity;
import xfkj.fitpro.activity.MoreSleepActivity;
import xfkj.fitpro.activity.StepNumberMoreActivity;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseFragment;
import xfkj.fitpro.db.SqliteDBAcces;

@SuppressLint("ValidFragment")
public class SportFragment extends BaseFragment {
    private String TAG = "SportFragment";
    private View view;//界面的布局
    private Context context;
    private TextView cary, day, step, s_mb, s_wcl, km_val, hs_hour, hs_min, hs_zl, hxl_val, hx_time, hx_val;


    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;//sport_scrollview

    private int custom_steps;//用户的步数
    private Double distance_values;// 路程：米
    private int steps_values;//步数
    private int calory_values;//热量
    private String today;
    private LeReceiver leReceiver;
    private Typeface tf;
    private Map dates;

    private LinearLayout sleep_item, step_box, measure_box;
    private RelativeLayout sleep_box;
    private ArrayList<HashMap<String, Object>> sleepItem;
    private int[] sTypeBg;
    private String t_daytime, t_heart, t_blood, s_hour, s_min;

    TextView mTvTemp;
    TextView mTvTempLabel;

    //传值
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what) {
                case Profile.MsgWhat.what5://步数跟新后会调至这里
                    steps_values = (Integer) map.get("step");//获取计步的步数
                    distance_values = Double.valueOf(map.get("distance").toString());
                    calory_values = (Integer) map.get("calory");
                    //设置显示值
                    updateViewData();
                    break;
                case Profile.MsgWhat.what60://心率测量返回
                case Profile.MsgWhat.what51://步数跟新后会调至这里
                case Profile.MsgWhat.what90://睡眠更新会在这里回调
                    updateViewData();
                    break;
                case Profile.MsgWhat.what6://温度返回
                    setTempData((float) map.get("temps"));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void setTempData(float temps) {
        //这里显示的都是摄氏，需要转换成华氏的话，你们自己处理
        mTvTemp.setText(temps + "");
        //这个单位和上面的无关
        mTvTempLabel.setText((FitProSpUtils.getTemptUnit() == 0 ? "摄氏" : "华氏"));
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


    /**
     * 创建视图
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sport, container, false);
        initView();//初始化控件
        return view;
    }

    /**
     * 初始化相关的属性
     */
    private void initValues() {
        sleepItem = new ArrayList<>();
        dates = getDate();
        today = dates.get("date").toString();
        String today1 = dates.get("month").toString() + dates.get("day").toString();
        distance_values = Double.valueOf(SaveKeyValues.getStringValues("distance_values" + today1, "0"));
        calory_values = SaveKeyValues.getIntValues("calory_values" + today1, 0);
        steps_values = SaveKeyValues.getIntValues("steps_values" + today1, 0);
        t_daytime = " -:-";
        t_heart = s_hour = s_min = "0";
        t_blood = "00/00";
    }

    private void updateViewData() {
        //重新获取
        initValues();
        LogUtils.i("==================>>updateViewData!");
        day.setText(today);
        custom_steps = SaveKeyValues.getIntValues("step", 5000);//用户的步数
        step.setText(steps_values + "");
        s_mb.setText(getString(R.string.trget_txt) + ":" + custom_steps + "");
        String wcl = (formatDouble((double) (((float) steps_values / (float) custom_steps)) * 100));
        if (steps_values > custom_steps) {
            wcl = 100 + "";
        }
        s_wcl.setText(getString(R.string.wcl_txt) + ":" + wcl + "%");
        km_val.setText(formatDouble(distance_values) + "");
        cary.setText(calory_values + "");

        Logdebug(TAG, "updateViewData" + today + "---distance_values---" + distance_values + "---calory_values---" + calory_values + "---steps_values---" + steps_values);
        setData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        swipeRefreshLayout = view.findViewById(R.id.id_swipe);
        scrollView = view.findViewById(R.id.sport_scrollview);
        cary = view.findViewById(R.id.cary_val);
        day = view.findViewById(R.id.day_val);
        step = view.findViewById(R.id.step_val);
        s_mb = view.findViewById(R.id.step_mb);
        s_wcl = view.findViewById(R.id.step_wcl);
        km_val = view.findViewById(R.id.km_val);
        hs_hour = view.findViewById(R.id.hsleep_hour);
        hs_min = view.findViewById(R.id.hsleep_min);
        hs_zl = view.findViewById(R.id.hsleep_zl);
        hxl_val = view.findViewById(R.id.hxl_val);
        hx_time = view.findViewById(R.id.hxlxy_time);
        hx_val = view.findViewById(R.id.hxy_val);
        sleep_item = view.findViewById(R.id.sleep_item);
        step_box = view.findViewById(R.id.stept_box);
        sleep_box = view.findViewById(R.id.sleep_box);
        measure_box = view.findViewById(R.id.measure_box);

        mTvTemp = view.findViewById(R.id.tv_temp);
        mTvTempLabel = view.findViewById(R.id.tv_temp_label);

        step_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, StepNumberMoreActivity.class));
            }
        });

        sleep_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MoreSleepActivity.class));
            }
        });

        measure_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MeasureActivity.class));
            }
        });
        leReceiver = new LeReceiver(context, handler);
        //初始化字体
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/IMPACT_0.TTF");
        cary.setTypeface(tf);
        day.setTypeface(tf);
        step.setTypeface(tf);
        km_val.setTypeface(tf);
        hxl_val.setTypeface(tf);
        hx_val.setTypeface(tf);
        hs_hour.setTypeface(tf);
        hs_min.setTypeface(tf);
        sTypeBg = new int[]{R.color.deep_sleep_background, R.color.somnolence_sleep_background, R.color.sober_sleep_background};
        //设置进度条的颜色主题，最多能设置四种 加载颜色是循环播放的，只要没有完成刷新就会一直循环，
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);

        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipeRefreshLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        // 设置圆圈的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        //设置下拉刷新的监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);//设置不刷新
                            updateViewData();
                            if (SDKTools.BleState == 1) {
                                SDKCmdMannager.getTotalSportData();
                            }
                        }
                    }
                }, 1000);
            }
        });
    }


    //设置数据
    @SuppressLint("ResourceAsColor")
    private void setData() {
        LogUtils.i("==================>>setData!");
        if ( MyApplication.DBAcces == null) {
            return;
        }
        SleepData();
        Cursor cursor =  MyApplication.DBAcces.Query("select * from Measure order by LongDate desc limit 0,1");
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            t_heart = cursor.getString(cursor.getColumnIndex("Heart"));
            String hblood = cursor.getString(cursor.getColumnIndex("hBlood"));
            String lblood = cursor.getString(cursor.getColumnIndex("lBlood"));
            t_blood = lblood + "/" + hblood;
            long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            t_daytime = format2.format(new Date(longDate));
        }
        hxl_val.setText(t_heart);
        hx_time.setText(t_daytime);
        hx_val.setText(t_blood);
    }

    private void SleepData() {
        LogUtils.i("初始化睡眠数据!");
        sleepItem.clear();
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
        float deep_sleep_times = 0, somnolence_times = 0, wakeup_times = 0;
        long start_sleep_data = 0;
        int pstype = 0;
        LogUtils.i("查看睡眠数据数据库:cursor count:" + cursor == null ? "0" : cursor.getCount());
        if (cursor != null && cursor.getCount() >= 6) {
            while (cursor.moveToNext()) {
                LogUtils.i("debug睡眠 Num:1");
                HashMap<String, Object> sleeps = new HashMap<>();
                int stype = Integer.valueOf(cursor.getString(cursor.getColumnIndex("SleepTypes")));
                long longDate = cursor.getLong(cursor.getColumnIndex("LongDate"));
                if (start_sleep_data == 0) {
                    start_sleep_data = longDate;
                    pstype = stype;
                }
                LogUtils.i("debug睡眠 Num:2");
                float t = longDate - start_sleep_data;
                if (pstype == 2 && (stype == 1 || stype == 3)) {//2到1是或者2到3都是浅睡时间
                    sleeps.put("stype", 2);
                    somnolence_times += t;
                } else if (pstype == 1 && stype == 2) {//1到2是深睡时间
                    sleeps.put("stype", 1);
                    deep_sleep_times += t;
                } else if (pstype == 3 && stype == 2) {//3到2是清醒时间
                    sleeps.put("stype", 3);
                    wakeup_times += t;
                }
                LogUtils.i("debug睡眠 Num:3");
                sleeps.put("stime", t);
                start_sleep_data = longDate;
                pstype = stype;
                if (t > 0) {
                    sleepItem.add(sleeps);
                }
                LogUtils.i("debug睡眠 Num:4");
            }
            cursor.close();
            deep_sleep_times = deep_sleep_times / 1000 / 60;
            somnolence_times = somnolence_times / 1000 / 60;
            wakeup_times = wakeup_times / 1000 / 60;
            LogUtils.i("debug睡眠 Num:5");
        }
        showView(deep_sleep_times, somnolence_times, wakeup_times);
    }

    public void showView(float deep_sleep_times, float somnolence_times, float wakeup_times) {
        if (deep_sleep_times > somnolence_times) {//深睡时间大于浅睡时间不展示当天睡眠
            deep_sleep_times = somnolence_times = 0;
            sleepItem.clear();
        }
        float total = deep_sleep_times + somnolence_times + wakeup_times;
        int hour = (int) Math.floor(total / 60);
        int minute = (int) total % 60;
        hs_hour.setText(hour + "");
        hs_min.setText(returnshi(minute));
        String sleep_quality = getSleepQuality(deep_sleep_times);
        hs_zl.setText(sleep_quality);
        setSleepChart();
    }

    public void setSleepChart() {
        sleep_item.removeAllViews();
        int totalItem = sleepItem.size();
        for (int i = 0; i < totalItem; i++) {
            HashMap<String, Object> sleeps = sleepItem.get(i);
            TextView textView = new TextView(context);
            if (sleeps.get("stype") == null) {
                continue;
            }
            float stime = (float) sleeps.get("stime");
            int stype = (int) sleeps.get("stype");
            stime = stime / 1000 / 60;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = stime;
            textView.setBackgroundResource(sTypeBg[stype - 1]); //设置背景
            textView.setLayoutParams(layoutParams);
            sleep_item.addView(textView);
        }
    }


    public String getSleepQuality(float deep_sleep_times) {
        String smqk = getString(R.string.none);
        if (deep_sleep_times <= 0) {
            return smqk;
        }
        smqk = getString(R.string.sleep_quality);
        if (deep_sleep_times > 2 * 60) {
            return smqk + getString(R.string.good_txt);//"良";
        } else if (deep_sleep_times > 1 * 60) {
            return smqk + getString(R.string.excellent_txt);//"优";
        }
        return smqk + getString(R.string.commonly_txt);//"一般";
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
     * 在当前Fragment结束之前，销毁一些不需要的变量
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateViewData();
        if (leReceiver != null)
            leReceiver.registerLeReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (leReceiver != null)
            leReceiver.unregisterLeReceiver();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateViewData();
        }
    }
}
