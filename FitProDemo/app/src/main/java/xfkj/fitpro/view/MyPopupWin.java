package xfkj.fitpro.view;

import static com.legend.bluetooth.fitprolib.utils.DateUtils.getDate;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.Map;

import xfkj.fitpro.R;

public class MyPopupWin extends PopupWindow
{
    private Context mContext;
    private View view;
    private ArrayList<String> minute_list, hours_list;//集合
    private LoopView time_minute, time_hours;
    private String minute = "00";
    private String hours = "00";

    private Button cancel_set_btn, confirm_set_btn;
    private CheckBox Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
    private Integer[] weeks;


    // 数据接口
    OnGetAlarmData mAlarmData;

    public MyPopupWin(Context mContext)
    {
        super(mContext);
        this.mContext = mContext;
        InitData(mContext);
        InitUI();
    }


    private void InitData(Context context)
    {
        this.view = LayoutInflater.from(context).inflate(R.layout.alarm_edit, null);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        dismiss();
                    }
                }
                return true;
            }
        });
        weeks = new Integer[]{0,0, 0, 1, 1, 1, 1, 1};

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);
        update();

    }

    private void InitUI()
    {

        time_minute = view.findViewById(R.id.time_minute);
        time_hours = view.findViewById(R.id.time_hours);

        cancel_set_btn = view.findViewById(R.id.cancel_id);
        confirm_set_btn = view.findViewById(R.id.confirm_id);

        Monday = view.findViewById(R.id.mon_id);
        Tuesday = view.findViewById(R.id.tue_id);
        Wednesday = view.findViewById(R.id.wed_id);
        Thursday = view.findViewById(R.id.thu_id);
        Friday = view.findViewById(R.id.fri_id);
        Saturday = view.findViewById(R.id.sat_id);
        Sunday = view.findViewById(R.id.sun_id);


        Monday.setOnCheckedChangeListener(changeListener);
        Tuesday.setOnCheckedChangeListener(changeListener);
        Wednesday.setOnCheckedChangeListener(changeListener);
        Thursday.setOnCheckedChangeListener(changeListener);
        Friday.setOnCheckedChangeListener(changeListener);
        Saturday.setOnCheckedChangeListener(changeListener);
        Sunday.setOnCheckedChangeListener(changeListener);


        minute_list = new ArrayList<>();
        hours_list = new ArrayList<>();
        for (int i = 0; i <= 23; i++)
        {
            String item = (i < 10) ? ("0" + i) : i + "";
            hours_list.add(item);
        }
        for (int i = 0; i <= 59; i++)
        {
            String item = (i < 10) ? ("0" + i) : i + "";
            minute_list.add(item);
        }
        time_hours.setItems(hours_list);
        time_minute.setItems(minute_list);

/*
        time_hours.setListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int index)
            {
       //         hours = hours_list.get(index).toString();
            }
        });

        time_minute.setListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(int index)
            {
       //         minute = minute_list.get(index).toString();
            }
        });
*/
        cancel_set_btn.setOnClickListener(new Button.OnClickListener()
        {//创建监听
            public void onClick(View v)
            {
                doCancelBtn(v);
            }
        });

        confirm_set_btn.setOnClickListener(new Button.OnClickListener()
        {//创建监听
            public void onClick(View v)
            {
                doConfirmBtn(v);
            }
        });

    }

    public void doCancelBtn(View view)
    {
        dismiss();
    }

    public void doConfirmBtn(View view)
    {

        hours = hours_list.get(time_hours.getSelectedItem()).toString();
        minute = minute_list.get(time_minute.getSelectedItem()).toString();
        mAlarmData.onDataCallBack(hours, minute, weeks);
        dismiss();
    }

    // 数据接口设置,数据源接口传入
    public void setOnData(OnGetAlarmData sd)
    {
        mAlarmData = sd;
        resetView();
    }

    public void resetView(){
        Monday.setChecked(true);
        Tuesday.setChecked(true);
        Wednesday.setChecked(true);
        Thursday.setChecked(true);
        Friday.setChecked(true);
        Saturday.setChecked(false);
        Sunday.setChecked(false);
        Map dates = getDate();
        int h = Integer.valueOf(dates.get("hour").toString());
        int m = Integer.valueOf(dates.get("minute").toString());
        time_hours.setCurrentPosition(h);
        time_minute.setCurrentPosition(m);
    }



    // 数据接口抽象方法
    public interface OnGetAlarmData
    {
        abstract void onDataCallBack(String hours, String minute, Integer[] weeks);
    }

    public CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            Integer val = isChecked ? 1 : 0;
            switch (buttonView.getId())
            {
                case R.id.mon_id:
                    weeks[7] = val;
                    break;
                case R.id.tue_id:
                    weeks[6] = val;
                    break;
                case R.id.wed_id:
                    weeks[5] = val;
                    break;
                case R.id.thu_id:
                    weeks[4] = val;
                    break;
                case R.id.fri_id:
                    weeks[3] = val;
                    break;
                case R.id.sat_id:
                    weeks[2] = val;
                    break;
                case R.id.sun_id:
                    weeks[1] = val;
                    break;
                default:
                    break;
            }
        }
    };


}