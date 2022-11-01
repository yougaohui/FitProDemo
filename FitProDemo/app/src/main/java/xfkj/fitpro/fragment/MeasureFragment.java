package xfkj.fitpro.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseFragment;
import xfkj.fitpro.db.SqliteDBAcces;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.view.MyBloodView;
import xfkj.fitpro.view.MyHeartView;

import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSportBloodRateRecive;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSportHeartRateRecive;

public class MeasureFragment extends BaseFragment implements View.OnClickListener
{
    private static final String TAG = MeasureFragment.class.getSimpleName();
    private View view;//界面的布局
	
    private Button do_test_btn;
    private ScrollView scrollView;

  //  private ImageView blood_icon,heart_icon;

    private LeReceiver leReceiver;
    boolean hasBooldVal = false;//第一次切换加载数据

    private Context context;
    private ListView hblist;
    private MyHeartView heartView;
    private MyBloodView bloodView;
    private LinearLayout show_instView,show_testing,nb_tip_box;
    private LottieAnimationView blood_pressure,heart_rate;
	private SimpleAdapter mAdapter;
    private String isHbType = "heart";

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
                    setHBData("heart");
                    SDKTools.hearting = false;
                    showUiView();
                    break;
                case Profile.MsgWhat.what61://心率测量停止(手环主动发起)
                    SDKTools.hearting = false;
                    showUiView();
                    break;
                case Profile.MsgWhat.what62://血压测量返回
                    setHBData("blood");
                    SDKTools.blooding = false;
                    showUiView2();
                case Profile.MsgWhat.what63://血压测量停止(手环主动发起)
                    SDKTools.blooding = false;
                    showUiView2();
                    break;
                case Profile.MsgWhat.what64://心率测量开始/停止(APP发起)
                    if(SDKTools.hearting && map.get("is_ok") != null && map.get("is_ok").equals("0")){
                        SDKTools.hearting = false;//停止测量
                    }
                    showUiView();
                    break;
                case Profile.MsgWhat.what65://血压测量开始/停止(APP发起)
                    if(SDKTools.blooding && map.get("is_ok") != null && map.get("is_ok").equals("0")){
                        SDKTools.blooding = false;//停止测量
                    }
                    showUiView2();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_measure, null);
        initView();
        return view;
    }    

    private void showUiView(){
        if(SDKTools.hearting){
            show_testing.setVisibility(View.VISIBLE);
            show_instView.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.stop_test_hb));
            heart_rate.setVisibility(View.VISIBLE);
            blood_pressure.setVisibility(View.GONE);
        }else{
            show_instView.setVisibility(View.VISIBLE);
            show_testing.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.start_test_hb));
            if(!isHbType.equals("heart")){
                return;
            }
            heartView.setVisibility(View.VISIBLE);
            bloodView.setVisibility(View.GONE);
        }
    }

    private void showUiView2(){
        if(SDKTools.blooding){
            show_testing.setVisibility(View.VISIBLE);
            show_instView.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.stop_test_hb));
            blood_pressure.setVisibility(View.VISIBLE);
            heart_rate.setVisibility(View.GONE);
        }else{
            show_instView.setVisibility(View.VISIBLE);
            show_testing.setVisibility(View.GONE);
            do_test_btn.setText(getString(R.string.start_test_hb));
            if(!isHbType.equals("blood")){
                return;
            }
            bloodView.setVisibility(View.VISIBLE);
            heartView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化控件
     */
    private void initView()
    {
        scrollView = view.findViewById(R.id.hbScroll);
        hblist = view.findViewById(R.id.hb_list_item);
        show_testing = view.findViewById(R.id.show_testing);
        show_instView = view.findViewById(R.id.show_instView);
        heart_rate = view.findViewById(R.id.heart_rate);

        heartView = view.findViewById(R.id.heartView);

        nb_tip_box = view.findViewById(R.id.no_hb_tip_box);

        do_test_btn = view.findViewById(R.id.test_hb_btn);

        do_test_btn.setOnClickListener(this);

        setHBData(isHbType);

        leReceiver = new LeReceiver(context, handler);
       
        scrollView.smoothScrollTo(0, 0);
		/*
		 Cursor cursor =  Constant.DBAcces.Query("select p.*,s.seed from Pwds as p left join Pwdseeds as s on p.s_id = s.id where p.id = "+Integer.valueOf(pwd_id));

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
            {
               int s_id = cursor.getInt(cursor.getColumnIndex("s_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));                
            }
		*/
    }

    //数据测量的血压数据
    private void setHBData(final String mType)
    {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        int[] Image_a={R.drawable.day_icon,R.drawable.time_icon};
    //    Map dates = getDate();
    //    String today = dates.get("date").toString();//year+"-"+month+"-"+day;
        SqliteDBAcces DBAccess =  MyApplication.DBAcces;
        Cursor cursor = DBAccess.Query("select * from Measure where Types = '"+mType+"' group by LongDate order by LongDate desc limit 0,150");
        int i = 0;
        int lastProgress = 0,hblood=0,dblood=0;
        String day_title="no";
        while (cursor.moveToNext()) {
            String revDate = cursor.getString(cursor.getColumnIndex("RevDate"));
            int hdata = cursor.getInt(cursor.getColumnIndex("Data"));
            int ddata = cursor.getInt(cursor.getColumnIndex("Blood2"));
            if(i == 0){
                lastProgress = hdata;
				hblood=hdata;
				dblood=ddata;
            }
            String SysDate = cursor.getString(cursor.getColumnIndex("SysDate"));
            HashMap<String, Object> map = new HashMap<>();
            if(!day_title.equals(SysDate)){
                map.put("day_icon", Image_a[0]);
                map.put("day_val", SysDate);
                listItem.add(map);
                map = new HashMap<>();
            }
            day_title = SysDate;
            map.put("time_icon", Image_a[1]);// 图像资源的ID
            map.put("time_val", revDate);
            if(mType == "blood"){
                map.put("hb_val", ddata+"/"+hdata);//测量数据
            }else{
                map.put("hb_val", hdata+"");//测量数据
            }
            listItem.add(map);
            i++;
        }
        cursor.close();
        if(listItem.size()>0){//
            nb_tip_box.setVisibility(View.GONE);
        }else{
            nb_tip_box.setVisibility(View.VISIBLE);
        }
		if(mType == "heart"){
            HeartAnimator(lastProgress);
		}else{
            bloodView.setScoreText(hblood,dblood);
		}
        mAdapter = new SimpleAdapter(context, listItem,R.layout.layout_heat_item,new String[] {"day_icon", "day_val", "time_icon" ,"time_val","hb_val"},new int[] { R.id.day_icon,R.id.day_val, R.id.time_icon , R.id.time_val , R.id.hb_val });
        hblist.setAdapter(mAdapter);
        hblist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                for (int i = 0; i < listItem.size(); i++) {
                    if (arg2 == i && listItem.get(arg2) != null && listItem.get(arg2).get("hb_val") != null) {
                        if(mType == "blood"){
                            String hb_val =  listItem.get(arg2).get("hb_val").toString();
                            String [] hbs = hb_val.split("\\/");
                            int hblood = Integer.valueOf(hbs[0]);
                            int dblood = Integer.valueOf(hbs[1]);
                            bloodView.setScoreText(dblood,hblood);
                        }else{
                            int testData = Integer.valueOf(listItem.get(arg2).get("hb_val").toString());
                            HeartAnimator(testData);
                        }
                    }
                }
            }
        });
    }

    public void HeartAnimator(int heartValue){
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, heartValue);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float)animation.getAnimatedValue();
                heartView.setProgress((int) animatedValue,getString(R.string.newly_test));
            }
        });
        valueAnimator.start();
    }

    @Override
    public void onResume()
    {
        super.onResume();
		if(leReceiver != null)
        leReceiver.registerLeReceiver();
        hasBooldVal = false;
        if(SDKTools.hearting){
            showUiView();
        }else if(SDKTools.blooding){
            showHeartBlood(false);
            showUiView2();
            return;
        }
        showHeartBlood(true);
    }

    public void onPause() {
        super.onPause();
		if(leReceiver != null)
        leReceiver.unregisterLeReceiver();
    }

    /**
     * 释放资源
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context).setCancelable(false);
        switch (v.getId())
        {
            case R.id.test_hb_btn:
                if(SDKTools.BleState != 1){
                    Toast.makeText(context, getString(R.string.unconnected), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isHbType.equals("heart")){
                    SDKTools.hearting = !SDKTools.hearting;
                    SDKTools.mService.commandPoolWrite(getSportHeartRateRecive(SDKTools.hearting),"心率测试:"+(SDKTools.hearting?"开始":"停止"));
                }else{
                    SDKTools.blooding = !SDKTools.blooding;
                    SDKTools.mService.commandPoolWrite(getSportBloodRateRecive(SDKTools.blooding),"血压测试:"+(SDKTools.blooding?"开始":"停止"));
                }
                break;
            default:
                break;
        }
    }

    public void showHeartBlood(boolean heart){
        show_instView.setVisibility(View.VISIBLE);
        show_testing.setVisibility(View.GONE);            
        scrollView.smoothScrollTo(0, 0);
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        scrollView.smoothScrollTo(0, 0);
        SDKTools.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, 0);
            }
        }, 100);
    }

}
