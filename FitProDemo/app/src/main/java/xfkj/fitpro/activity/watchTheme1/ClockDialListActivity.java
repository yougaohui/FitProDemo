package xfkj.fitpro.activity.watchTheme1;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.legend.bluetooth.fitprolib.api.DownloadMannager;
import com.legend.bluetooth.fitprolib.api.HttpHelper;
import com.legend.bluetooth.fitprolib.bluetooth.CommandPool;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.BaseResponse;
import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;
import com.legend.bluetooth.fitprolib.model.WatchThemeResponse;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.WatchThemeTools;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.activity.watchTheme1.fragment.ClockDialMoRenFragment;
import xfkj.fitpro.activity.watchTheme1.fragment.ClockDialTuiJianFragment;
import xfkj.fitpro.base.BaseFragmentAdapter;
import xfkj.fitpro.base.NewBaseFragment;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.PathUtils;

public class ClockDialListActivity extends BaseClockDialActivity {

    //升级状态
    final int MSG_START = 0x00;
    final int MSG_SUCCESS = 0x01;
    final int MSG_FAILED = 0x02;
    final int MSG_UPGRADDING = 0x03;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    private BaseFragmentAdapter mAdapterViewPager;

    List<Fragment> mFragments = new ArrayList<>();
    private DownloadMannager mImgDownloadMannager;//图片固件下载器
    private WatchThemeDetailsResponse mCurData;

    private String mImgBinLocalPath;

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
        switch (msg.what) {
            case MSG_SUCCESS:
            case MSG_FAILED:
                hideDialog();
                break;
            case MSG_START:
            case MSG_UPGRADDING:
                showProgressDialog(msg.arg1);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_clock_dial_list;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTabLayout = findViewById(R.id.TabLayout);
        mViewPager = findViewById(R.id.ViewPager);
        setTitle(R.string.clock_dial_settings);
        //设置替换表盘图片选择模式
        Constants.isDeviceChoicePic = getIntent().getBooleanExtra("isDeviceChoicePic", false);
        //删除表盘主题文件夹里面的所有文件
        FileUtils.deleteAllInDir(PathUtils.getWatchThemePath());
        //从服务器获取数据
        httpData();
    }

    private void httpData() {
        HttpHelper.getInstance().queryWatchThemeList(CacheHelper.getClockDialInfo(),new Observer<BaseResponse<List<WatchThemeResponse>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                DialogHelper.showLoadDialog(mContext);
            }

            @Override
            public void onNext(BaseResponse<List<WatchThemeResponse>> response) {
                if (response.isSuccess()) {
                    List<WatchThemeResponse> defaultThemes = diffOriginOfCutsom(response.getData(), true);
                    List<WatchThemeResponse> customThemes = diffOriginOfCutsom(response.getData(), false);
                    if (!CollectionUtils.isEmpty(defaultThemes)) {
                        mTabLayout.setVisibility(View.VISIBLE);
                        mFragments.add(ClockDialMoRenFragment.newInstance());
                        mFragments.add(ClockDialTuiJianFragment.newInstance());
                        ((NewBaseFragment) mFragments.get(0)).setData(defaultThemes);
                        ((NewBaseFragment) mFragments.get(1)).setData(customThemes);

                        mAdapterViewPager = new BaseFragmentAdapter(getSupportFragmentManager(), mFragments);
                        mViewPager.setAdapter(mAdapterViewPager);
                        String[] titls = getResources().getStringArray(R.array.clock_dial_titles);
                        for (int i = 0; i < titls.length; i++) {
                            mTabLayout.addTab(mTabLayout.newTab().setText(titls[i]));
                        }
                        mTabLayout.setupWithViewPager(mViewPager);//这个必须介于tab属性中间，不然显示不了tab
                        for (int i = 0; i < titls.length; i++) {
                            mTabLayout.getTabAt(i).setText(titls[i]);
                        }
                    } else {
                        mFragments.add(ClockDialTuiJianFragment.newInstance());
                        ((NewBaseFragment) mFragments.get(0)).setData(customThemes);
                        mAdapterViewPager = new BaseFragmentAdapter(getSupportFragmentManager(), mFragments);
                        mViewPager.setAdapter(mAdapterViewPager);
                    }
                } else {
                    ToastUtils.showShort(response.getError().toString());
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort(e.toString());
                DialogHelper.hideDialog();
            }

            @Override
            public void onComplete() {
                DialogHelper.hideDialog();
            }
        });
    }


    @Override
    public void initListener() {
        //图片固件下载
        mImgDownloadMannager = new DownloadMannager();
        mImgDownloadMannager.setDownLoadListener(new DownloadMannager.DownLoadListener() {
            @Override
            public void onStartDownload(String tag) {
                DialogHelper.showDialog(mContext, getString(R.string.loadding_data));
            }

            @Override
            public void onSuccess(String path,String tag) {
                DialogHelper.hideDialog();
                WatchThemeHelper.handleDownloadWatchThmeFile(ClockDialListActivity.this, mCurData, mImgDownloadMannager, path, tag,false);
            }

            @Override
            public void onFailed(String info,String tag) {
                ToastUtils.showShort(getString(R.string.loading_failed) + ":" + info);
                DialogHelper.hideDialog();
            }
        });

        //数据升级监听
        WatchThemeTools.getInstance().addStatusChangeListener(mUpdateStatusChangeListener);
    }

    /**
     * 区分默认表盘和自定义表盘
     *
     * @param datas
     * @param isOrigin true表示默认表盘
     * @return
     */
    private List<WatchThemeResponse> diffOriginOfCutsom(List<WatchThemeResponse> datas, boolean isOrigin) {
        List<WatchThemeResponse> origin = new ArrayList<>();
        for (WatchThemeResponse data : datas) {
            if (data.isOriginal() == isOrigin) {
                origin.add(data);
            }
        }
        return origin;
    }

    /**
     * 加载详细数据
     *
     * @param themeId
     */
    public void loadDetailsData(long themeId, boolean isCustomTheme) {
        mImgBinLocalPath = PathUtils.getWatchThemePath() + "IMG_" + themeId + ".bin";
        if (mCurData != null && themeId == mCurData.getId()) {
            Log.e(TAG, "文件已存在，不需要再次加载");
            enterDetailsPageOrUpgrade();
            return;
        }
        HttpHelper.getInstance().queryWatchThemeDetails(themeId, new Observer<BaseResponse<WatchThemeDetailsResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                DialogHelper.showLoadDialog(mContext);
            }

            @Override
            public void onNext(BaseResponse<WatchThemeDetailsResponse> response) {
                if (response.isSuccess()) {
                    mCurData = response.getData();
                    mCurData.setCutomTheme(isCustomTheme);
                    Log.e(TAG, mCurData.toString());
                    enterDetailsPageOrUpgrade();
                } else {
                    ToastUtils.showShort(response.getError().toString());
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort(e.toString());
                DialogHelper.hideDialog();
            }

            @Override
            public void onComplete() {
                DialogHelper.hideDialog();
            }
        });
    }

    /**
     * 进入升级模式还是详情页面
     */
    private void enterDetailsPageOrUpgrade() {
        List<WatchThemeDetailsResponse.MaterialListBean> materialList = mCurData.getMaterialList();
        if (CollectionUtils.size(materialList) > 2) {
            Intent intent = new Intent(mContext, ClockDialDetailsActivity.class);
            intent.putExtra("data", mCurData);
            startActivity(intent);
        } else {
            if (SDKCmdMannager.isConnected()) {
                mCurData.setPicBinpath(mImgBinLocalPath);
                WatchThemeHelper.handleClickInstallWatchTheme(this,mCurData,mImgDownloadMannager, CacheHelper.getClockDialInfo(),false);
            } else {
                ToastUtils.showShort(R.string.unconnected);
            }
        }
    }


    ProgressBar mDialogProgressbar;
    private AlertDialog mUpgradeDialog;
    private TextView mTitleTextView;

    public void showProgressDialog(int progress) {
        if (mUpgradeDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false);
            mUpgradeDialog = builder.create();
        }

        if (!mUpgradeDialog.isShowing()) {
            mUpgradeDialog.show();
        }

        if (mDialogProgressbar == null || mTitleTextView == null) {
            mUpgradeDialog.addContentView(LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_progress, null), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            mDialogProgressbar = mUpgradeDialog.findViewById(R.id.progressBar);
            mTitleTextView = mUpgradeDialog.findViewById(R.id.tv_title);
        }

        float progress1 = NumberUtils.keepPrecision(progress / 10f, 1);
        mTitleTextView.setText(getString(R.string.upgradding) + "(" + progress1 + "%" + ")");
        mDialogProgressbar.setProgress(progress);
    }

    private void hideDialog() {
        if (mUpgradeDialog != null) {
            mUpgradeDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        WatchThemeTools.getInstance().removeListener(mUpdateStatusChangeListener);
        mUpdateStatusChangeListener = null;
    }

    UpdateStatusChangeListener mUpdateStatusChangeListener = new UpdateStatusChangeListener();

    class UpdateStatusChangeListener implements WatchThemeTools.UpdateStatusChangeListener {

        @Override
        public void onStartUpgrade() {
            Log.e(TAG, "onStartUpgrade");
            //设置发送属性和速度，增加发送速度
            CommandPool.setSendSpaceDuraion(3);
            SDKTools.mService.getWriteChar().setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

            Message message = new Message();
            message.what = MSG_START;
            message.arg1 = 0;
            mHandler.sendMessage(message);
        }

        @Override
        public void onStatusChange(int progress) {
            Log.e(TAG, "onStatusChange:" + progress);
            Message message = new Message();
            message.what = MSG_UPGRADDING;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }

        @Override
        public void onUpgradeSuccess() {
            Log.e(TAG, "upgradeSuccess");
            //还原写属性
            CommandPool.setSendSpaceDuraion(100);
            //Constant.mService.getWriteChar().setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            Message message = new Message();
            message.what = MSG_SUCCESS;
            mHandler.sendMessage(message);
            ToastUtils.showShort(R.string.upgrade_success);
        }

        @Override
        public void onUpgradeFailed(int errorCode) {
            Log.e(TAG, "errorCode:" + errorCode);
            //还原写属性
            CommandPool.setSendSpaceDuraion(100);
            //Constant.mService.getWriteChar().setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            Message message = new Message();
            message.what = MSG_FAILED;
            mHandler.sendMessage(message);

            if (WatchThemeTools.ERROR_BLE_DISCONNECTED == errorCode) {
                ToastUtils.showShort(R.string.unconnected);
            } else if (WatchThemeTools.ERROR_BATTERY_LOW == errorCode) {
                ToastUtils.showShort(R.string.battery_low_not_dial_clock);
            } else if (WatchThemeTools.ERROR_CHARGE_BATTERY == errorCode) {
                ToastUtils.showShort(R.string.charge_battery_not_dial_clock);
            } else {
                ToastUtils.showShort(getString(R.string.upgrade_failed, errorCode));
            }
        }
    }
}
