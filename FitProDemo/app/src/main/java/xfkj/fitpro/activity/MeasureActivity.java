package xfkj.fitpro.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.MeasureDetailsModel;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.db.SqliteDBAcces;
import xfkj.fitpro.view.MyHeartView;

import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;
import static xfkj.fitpro.application.MyApplication.setWindowStatusBarColor;

public class MeasureActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = MeasureActivity.class.getSimpleName();
    private Button do_test_btn;

    private LeReceiver leReceiver;
    private ListView mlist;
    private MyHeartView heartView;
    private LinearLayout show_instView,show_testing;
    private LottieAnimationView heart_rate;
    private MyAdapter mAdapter;
    private String today;
    private Typeface tf;
    private Map dates;
    private TextView spo_val, blood_val,show_day;
    private ScrollView scrollView;

    //传值
    private Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what)
            {
                case Profile.MsgWhat.what60://心率测量返回
                    MeasureDetailsModel data = (MeasureDetailsModel) map.get("measuredata");
                    SDKTools.hearting = false;
                    setData(data);
                    showUiView();
                    break;
                case Profile.MsgWhat.what61://心率测量停止(手环主动发起)
                    SDKTools.hearting = false;
                    showUiView();
                    break;
                case Profile.MsgWhat.what64://心率测量开始/停止(APP发起)
                    if(SDKTools.hearting && map.get("is_ok") != null && map.get("is_ok").equals("0")){
                        SDKTools.hearting = false;//停止测量
                        showUiView();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(getString(R.string.measure_title), MeasureActivity.this);
        findViewById(R.id.title_chunk).setBackgroundColor(getResources().getColor(R.color.measure_title_bg));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_measure);
        setWindowStatusBarColor(this,getResources().getColor(R.color.measure_title_bg));
    }

    @Override
    protected void initValues() {
        dates = getDate();
        tf = Typeface.createFromAsset(this.getAssets(), "fonts/IMPACT_0.TTF");
        today = dates.get("date").toString();
    }



    @Override
    protected void initViews() {
        scrollView = findViewById(R.id.meaScroll);
        mlist = findViewById(R.id.m_lists);
        show_testing = findViewById(R.id.show_testing);
        show_instView = findViewById(R.id.show_instView);
        heartView = findViewById(R.id.heartView);
        heart_rate = findViewById(R.id.heart_rate);
        do_test_btn = findViewById(R.id.test_btn);
        spo_val = findViewById(R.id.n_spo_val);
        blood_val = findViewById(R.id.n_blood_val);
        spo_val.setTypeface(tf);
        blood_val.setTypeface(tf);
        do_test_btn.setOnClickListener(this);
        mlist.setFocusable(false);

        leReceiver = new LeReceiver(this, handler);
        scrollView.smoothScrollTo(0, 0);
        setData();
    }

    private void showUiView(){
        if(SDKTools.hearting){
            show_testing.setVisibility(View.VISIBLE);
            show_instView.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.stop_test_hb));
            heart_rate.setVisibility(View.VISIBLE);
        }else{
            show_instView.setVisibility(View.VISIBLE);
            show_testing.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.start_test_hb));
            heartView.setVisibility(View.VISIBLE);
        }
    }

    private void setData() {
        setData(null);
    }

    //设置数据
    @SuppressLint("ResourceAsColor")
    private void setData(MeasureDetailsModel data) {
        if ( MyApplication.DBAcces == null) {
            return;
        }
//        int heart = data.getHeart();
//        HeartAnimator(heart);
//
//        int hblood = data.getHblood();
//        int lblood = data.getLblood();
//
//        int spo = data.getSpo();
//        String t_blood = lblood + "/" + hblood;
//        spo_val.setText(spo + "%");
//        blood_val.setText(t_blood);
        Cursor cursor =   MyApplication.DBAcces.Query("select * from Measure order by id desc limit 0,1");
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            String heart = cursor.getString(cursor.getColumnIndex("Heart"));
            HeartAnimator(Integer.valueOf(heart));
            String hblood = cursor.getString(cursor.getColumnIndex("hBlood"));
            String lblood = cursor.getString(cursor.getColumnIndex("lBlood"));
            String spo = cursor.getString(cursor.getColumnIndex("Spo"));
            String t_blood = lblood+"/"+hblood;
            spo_val.setText(spo+"%");
            blood_val.setText(t_blood);
        }

        getMeasureRecord();
    }

    public void HeartAnimator(int heartValue){
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, heartValue);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                heartView.setProgress((int) animatedValue,"bpm");
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }

    public void getMeasureRecord(){
        List<Map<String, Object>> listItem = new ArrayList<>();
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Measure group by LongDate order by LongDate desc limit 0,30");
        int[] Image_a = {R.drawable.sel_day_icon, R.drawable.time_icon};
        String day_title = "no";
        while (cursor.moveToNext()) {
            String revDate = cursor.getString(cursor.getColumnIndex("RevDate"));
            String SysDate = cursor.getString(cursor.getColumnIndex("SysDate"));
            String heart = cursor.getString(cursor.getColumnIndex("Heart"));
            String hblood = cursor.getString(cursor.getColumnIndex("hBlood"));
            String lblood = cursor.getString(cursor.getColumnIndex("lBlood"));
            String spo = cursor.getString(cursor.getColumnIndex("Spo"));
            HashMap<String, Object> map = new HashMap<>();
            if (!day_title.equals(SysDate))
            {
                map.put("day_img", Image_a[0]);
                map.put("time_val", SysDate);
                map.put("heart_val", getString(R.string.measure_heart));
                map.put("blood_val", getString(R.string.measure_blood));
                map.put("spo_val", getString(R.string.measure_spo));
                listItem.add(map);
                map = new HashMap<>();
            }
            day_title = SysDate;
            map.put("time_img", Image_a[1]);
            map.put("time_val", revDate);
            map.put("heart_val", heart);
            map.put("blood_val", lblood+"/"+hblood);
            map.put("spo_val", spo+"%");
            listItem.add(map);
        }
        //@drawable/sel_day_icon
        //@drawable/day_time_icon
        cursor.close();


        mAdapter = new MyAdapter(MeasureActivity.this,listItem, null);

    //    mAdapter = new SimpleAdapter(this, listItem,R.layout.measure_item,new String[] {"day_img","time_img","time_val", "heart_val", "blood_val" ,"spo_val"},new int[] { R.id.day_img,R.id.time_img,R.id.m_time,R.id.m_heart, R.id.m_blood , R.id.m_spo});
        mlist.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.test_btn:
                if(SDKTools.BleState != 1){
                    Toast.makeText(this, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                SDKTools.hearting = !SDKTools.hearting;
                SDKCmdMannager.startMeasureHeatRate();
                showUiView();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(leReceiver != null)
            leReceiver.registerLeReceiver();
        scrollView.smoothScrollTo(0, 0);
        SDKTools.mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.smoothScrollTo(0, 0);
            }
        }, 100);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        if(leReceiver != null)
            leReceiver.unregisterLeReceiver();
        super.onDestroy();
    }

    public class MyAdapter extends BaseAdapter
    {

        private String activity="";
        private View.OnClickListener onClickListener = null;
        private List<Map<String, Object>> list;
        private Context mContext;

        public MyAdapter(Context context,List<Map<String, Object>> data,View.OnClickListener onClickListener)
        {
            this.list = data;
            this.onClickListener = onClickListener;
            this.mContext = context;
        }


        @Override
        public int getCount()
        {
            return list.size();
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

        public void setData(List<Map<String, Object>> data)
        {
            this.list = data;
            notifyDataSetChanged();
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Map<String, Object> map = list.get(i);
            View v = View.inflate(this.mContext, R.layout.measure_item, null);
            TextView time_val = (TextView) v.findViewById(R.id.m_time);
            TextView day_val = (TextView) v.findViewById(R.id.m_day_val);
            TextView heart_val = (TextView) v.findViewById(R.id.m_heart);
            TextView blood_val = (TextView) v.findViewById(R.id.m_blood);
            TextView spo_val = (TextView) v.findViewById(R.id.m_spo);
            ImageView day_icon = v.findViewById(R.id.day_img);
            ImageView time_icon = v.findViewById(R.id.time_img);
            LinearLayout box = v.findViewById(R.id.m_item_box);
            if(map.get("day_img") != null){
                int color = getResources().getColor(R.color.gray_text);
                day_icon.setImageResource((int)map.get("day_img"));
                day_val.setVisibility(View.VISIBLE);
                day_val.setText((String) map.get("time_val")+"");
                time_val.setVisibility(View.GONE);
                heart_val.setTextSize(15);
                blood_val.setTextSize(15);
                spo_val.setTextSize(15);
                heart_val.setTextColor(color);
                blood_val.setTextColor(color);
                spo_val.setTextColor(color);
            }else{
                time_icon.setImageResource((int)map.get("time_img"));
                int color = getResources().getColor(R.color.text1);
                time_val.setVisibility(View.VISIBLE);
                time_val.setText((String) map.get("time_val")+"");
                day_val.setVisibility(View.GONE);
                heart_val.setTextSize(20);
                blood_val.setTextSize(20);
                spo_val.setTextSize(20);
                heart_val.setTextColor(color);
                blood_val.setTextColor(color);
                spo_val.setTextColor(color);
            }
            heart_val.setText((String) map.get("heart_val")+"");
            blood_val.setText((String) map.get("blood_val")+"");
            spo_val.setText((String) map.get("spo_val")+"");
//    mAdapter =  {"day_img","time_img","time_val", "heart_val", "blood_val" ,"spo_val"},new int[] { R.id.day_img,R.id.time_img,R.id.m_time,R.id.m_heart, R.id.m_blood , R.id.m_spo});


            return v;
        }
    }

}
