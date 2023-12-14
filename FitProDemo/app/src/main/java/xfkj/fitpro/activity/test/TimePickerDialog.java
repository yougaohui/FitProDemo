package xfkj.fitpro.activity.test;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.blankj.utilcode.util.ScreenUtils;

import butterknife.BindView;
import xfkj.fitpro.R;
import xfkj.fitpro.view.dialog.BaseDialogFragment;

public class TimePickerDialog extends BaseDialogFragment {

    @BindView(R.id.timePicker)
    TimePicker timePicker;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;

    int hourOfDay, min;

    @Override
    public void create(Bundle savedInstanceState, View view) {
        hourOfDay = timePicker.getCurrentHour();
        min = timePicker.getCurrentMinute();

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnOk.setOnClickListener(v -> {
            if (null != listener) {
                listener.onDateCallback(hourOfDay, min);
            }
            dismiss();
        });

        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            this.hourOfDay = hourOfDay;
            this.min = minute;
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_dialog_time_picker;
    }

    @Override
    protected Builder builder() {
        return new Builder().canceledOnTouchOutside(true).gravity(Gravity.CENTER).width(ScreenUtils.getAppScreenWidth() - 40);
    }

    ConfirmListener listener;

    public interface ConfirmListener {
        void onDateCallback(int hour, int min);
    }

    public void setListener(ConfirmListener listener) {
        this.listener = listener;
    }
}
