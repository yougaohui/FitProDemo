package xfkj.fitpro.view.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;

import xfkj.fitpro.R;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.utils.UIHelper;

public class WatchThemePosPickerDialog extends BaseDialogFragment implements View.OnClickListener {

    private RadioGroup mRadGroup;
    private int mSelectedId = 1;

    @Override
    public void create(Bundle savedInstanceState, View view) {
        mRadGroup = view.findViewById(R.id.rad_group);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        for (int i = 0; i < CacheHelper.getClockDialInfo().getPictureNums(); i++) {
            RadioButton radButton = new RadioButton(view.getContext());
            styleRadButton(view, radButton, i + 1);
            mRadGroup.addView(radButton);
        }

        mRadGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.i("WatchThemePos", radioGroup + ";i:" + i);
                mSelectedId = i;
            }
        });
    }

    private void styleRadButton(View view, RadioButton radioButton, int index) {
        radioButton.setButtonDrawable(null);
        radioButton.setBackground(UIHelper.getDrawable(R.drawable.selector_watch_theme_picker_choise));
        radioButton.setText(String.valueOf(index));
        radioButton.setTextColor(view.getContext().getResources().getColorStateList(R.color.selector_watch_theme_picker_color));
        radioButton.setTextSize(ConvertUtils.sp2px(15));
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setId(index);

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(view.getContext(), null);
        params.weight = 1;
        params.leftMargin = ConvertUtils.dp2px(10);
        params.rightMargin = ConvertUtils.dp2px(10);
        params.bottomMargin = ConvertUtils.dp2px(20);
        radioButton.setLayoutParams(params);
        if (index == 0) {
            radioButton.setChecked(true);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_dialog_watch_theme_pos_picker;
    }

    @Override
    protected Builder builder() {
        return new Builder().canceledOnTouchOutside(true).gravity(Gravity.BOTTOM).width(ScreenUtils.getAppScreenWidth() - 40);
    }

    public void onClickCancelOk(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                if (null != watchThemeSelectListener) {
                    watchThemeSelectListener.onCancel();
                }
                break;
            case R.id.btn_ok:
                if (null != watchThemeSelectListener) {
                    watchThemeSelectListener.onConfirm(mSelectedId);
                }
                break;
        }
        dismiss();
    }

    WatchThemeSelectListener watchThemeSelectListener;

    @Override
    public void onClick(View v) {
        onClickCancelOk(v);
    }

    public interface WatchThemeSelectListener {
        void onConfirm(int index);

        void onCancel();
    }

    public void setWatchThemeSelectListener(WatchThemeSelectListener watchThemeSelectListener) {
        this.watchThemeSelectListener = watchThemeSelectListener;
    }
}
