package xfkj.fitpro.activity;


import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.test.SleepDebugActivity;
import xfkj.fitpro.base.BaseActivity;

public class PlusCmdActivity extends BaseActivity {


    @Override
    protected void setActivityTitle() {

    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_plus_cmd);
    }

    @Override
    protected void initValues() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {

    }

    public void onClick1(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.getHardInfo();
    }

    public void onClick2(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.getProductInfo();
    }

    public void onClick3(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.getClockDialInfo();
    }

    public void onClick4(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.startMeasureHeart(true);//false表示停止测量
    }

    public void onClick5(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.startMeasureBlood(true);//false表示停止测量
    }

    public void onClick6(View view) {
        if (!SDKCmdMannager.isConnected()) {
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        SDKCmdMannager.startMeasureSpo(true);//false表示停止测量
    }

    public void onClick7(View view) {
        ActivityUtils.startActivity(SleepDebugActivity.class);
    }
}