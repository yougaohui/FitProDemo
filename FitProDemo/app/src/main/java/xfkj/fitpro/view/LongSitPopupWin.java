package xfkj.fitpro.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.R;

public class LongSitPopupWin extends PopupWindow
{
    private Context mContext;
    private View view;
    private TextView longsit_title;
    private LoopView longsit_item;
    private ImageView cancel_set_btn, confirm_set_btn;
    private Map<String, Object> map;
    private ArrayList listItems;
    private int itemIndex = 0;

    // 数据接口
    OnGetData mOnGetData;

    public LongSitPopupWin(Context mContext)
    {
        super(mContext);
        InitData(mContext);
        InitUI();
    }


    private void InitData(Context context)
    {
        this.view = LayoutInflater.from(context).inflate(R.layout.long_sit_set, null);
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
        longsit_title = (TextView) view.findViewById(R.id.longsit_title);
        longsit_item = (LoopView) view.findViewById(R.id.longsit_item);
        cancel_set_btn = (ImageView) view.findViewById(R.id.cancel_set_btn);
        confirm_set_btn = (ImageView) view.findViewById(R.id.confirm_set_btn);
        listItems = new ArrayList<>();

        cancel_set_btn.setOnClickListener(new Button.OnClickListener()
        {//创建监听
            public void onClick(View v)
            {
                doCancelBtn(v);
            }
        });


        longsit_item.setTextSize(25);
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

        itemIndex = longsit_item.getSelectedItem();
        mOnGetData.onDataCallBack(itemIndex, map);
        dismiss();
    }

    public void updateUI()
    {
        longsit_title.setText(map.get("sit_title") + "");
        longsit_item.setItems(listItems);
        longsit_item.setCurrentPosition(itemIndex);
    }

    // 数据接口设置,数据源接口传入
    public void setOnData(OnGetData sd)
    {
        mOnGetData = sd;
        map = new HashMap<>();
        if (mOnGetData != null)
        {
            map = mOnGetData.onMaps();
            listItems = mOnGetData.onListItems();
            itemIndex = mOnGetData.onSeclectItem();
        }
        updateUI();
    }

    // 数据接口抽象方法
    public interface OnGetData
    {
        abstract Map<String, Object> onMaps();

        abstract ArrayList onListItems();

        abstract int onSeclectItem();

        abstract void onDataCallBack(int nSectlect, Map<String, Object> map);
    }


}