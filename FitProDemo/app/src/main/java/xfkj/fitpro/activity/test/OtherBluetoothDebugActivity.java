package xfkj.fitpro.activity.test;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.bluetooth.SendData;

import xfkj.fitpro.R;

public class OtherBluetoothDebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_bluetooth_debug);
    }

    public void onClickSetStartTimeOfSleep(View view) {
        TimePickerDialog dialog = new TimePickerDialog();
        dialog.setListener((hour, min) -> {
            short time = (short) (hour * 60 + min);
            LogUtils.i("onClickSetStartTimeOfSleep", "onClickSetStartTimeOfSleep:" + time);
            if (SDKCmdMannager.isConnected()) {
                SDKCmdMannager.sendCustomOrder(SendData.getSleepOfBegin(time));
                ToastUtils.showShort("设置成功");
            } else {
                ToastUtils.showShort("设备已断开");
            }
        });
        dialog.show(getSupportFragmentManager(), "showTimePiker");
    }
}