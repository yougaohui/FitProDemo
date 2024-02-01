package xfkj.fitpro.view.dialog;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.CommandPool;
import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;
import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.WatchThemeTools;

import java.util.List;
import java.util.Locale;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.watchTheme1.WatchThemeHelper;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.utils.glide.GlideUitls;

public class WatchThemeDialog extends BaseDialogFragment {

    private final String TAG = "WatchThemeDialog";

    ImageView mImgPreview1;
    ImageView mImgPreview2;
    ProgressBar mProgressBar;
    TextView mTvTitle;
    View mFrmPreview;

    private final WatchThemeDetailsResponse mWatchThemeData;

    //升级状态
    final int MSG_START = 0x00;
    final int MSG_SUCCESS = 0x01;
    final int MSG_FAILED = 0x02;
    final int MSG_UPGRADDING = 0x03;

    private Handler mHandler = new Handler(msg -> {
        if (!(isRemoving() || isDetached())) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    dismiss();
                    break;
                case MSG_FAILED:
                    dismiss();
                    break;
                case MSG_START:
                case MSG_UPGRADDING:
                    showProgressDialog(msg.arg1);
                    break;
            }
        }
        return false;
    });

    private boolean isSuccess = false;

    public WatchThemeDialog(WatchThemeDetailsResponse watchThemeData) {
        this.mWatchThemeData = watchThemeData;
    }

    @Override
    public void create(Bundle savedInstanceState, View view) {
        mImgPreview1 =  view.findViewById(R.id.img_preview1);
        mImgPreview2 =  view.findViewById(R.id.img_preview2);
        mProgressBar =  view.findViewById(R.id.progressBar);
        mTvTitle =  view.findViewById(R.id.tv_title);
        mFrmPreview =  view.findViewById(R.id.frm_preview);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFrmPreview.getLayoutParams();
        params.height = (int) WatchThemeHelper.getConvertHeight(params.width);
        mFrmPreview.setLayoutParams(params);

        String previewPath = mWatchThemeData.getPreviewImgPath();
        ClockDialInfoBody infos = CacheHelper.getClockDialInfo();
        if (StringUtils.isTrimEmpty(previewPath)) {
            WatchThemeDetailsResponse.MaterialListBean item = findBgImgUrlData(mWatchThemeData.getMaterialList());
            GlideUitls.loadLocal(getContext(), item.getUrl(), mImgPreview1, infos.getScreenType() == 1);
        } else {
            GlideUitls.loadLocal(getContext(), previewPath, mImgPreview1, infos.getScreenType() == 1);
        }

        WatchThemeDetailsResponse.MaterialListBean positionBean = getRotationBeanByCode(mWatchThemeData.getFontPosition());
        if (positionBean != null) {
            mImgPreview2.setVisibility(View.VISIBLE);
            GlideUitls.loadLocal(getContext(), positionBean.getUrl(), mImgPreview2);
        } else {
            mImgPreview2.setVisibility(View.INVISIBLE);
        }

        WatchThemeTools.getInstance().addStatusChangeListener(mUpdateStatusChangeListener);
        WatchThemeTools.getInstance().startFile(CacheHelper.getClockDialInfo(),mWatchThemeData);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_dialog_watchtheme_update;
    }

    @Override
    protected Builder builder() {
        return new Builder().canceledOnTouchOutside(false).isCancel(false).gravity(Gravity.CENTER).width(ScreenUtils.getAppScreenWidth() - 40);
    }

    private WatchThemeDetailsResponse.MaterialListBean findBgImgUrlData(List<WatchThemeDetailsResponse.MaterialListBean> datas) {
        for (WatchThemeDetailsResponse.MaterialListBean data : datas) {
            String name = data.getName().toLowerCase(Locale.ROOT);
            if (name.contains("gif")) {
                return data;
            }
        }

        for (WatchThemeDetailsResponse.MaterialListBean data : datas) {
            String name = data.getName().toLowerCase(Locale.ROOT);
            if (StringUtils.equalsIgnoreCase(name, "BG_APP.png")) {
                return data;
            }
        }

        for (WatchThemeDetailsResponse.MaterialListBean data : datas) {
            String name = data.getName().toLowerCase(Locale.ROOT);
            if (StringUtils.equalsIgnoreCase(name, "PREVIEW.png")) {
                return data;
            }
        }
        return datas.get(0);
    }


    UpdateStatusChangeListener mUpdateStatusChangeListener = new UpdateStatusChangeListener();

    class UpdateStatusChangeListener implements WatchThemeTools.UpdateStatusChangeListener {

        @Override
        public void onStartUpgrade() {
            Log.e(TAG, "onStartUpgrade");
            //设置发送属性和速度，增加发送速度
            CommandPool.setSendSpaceDuraion(3);
            BluetoothGattCharacteristic writeChar = SDKTools.mService.getWriteChar();
            if (writeChar != null) {
                writeChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            }

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

            Message message = new Message();
            message.what = MSG_SUCCESS;
            mHandler.sendMessage(message);
            ToastUtils.showShort(R.string.upgrade_success);

            isSuccess = true;
        }

        @Override
        public void onUpgradeFailed(int errorCode) {
            Log.e(TAG, "errorCode:" + errorCode);
            //还原写属性
            CommandPool.setSendSpaceDuraion(100);
            //Constant.mService.getWriteChar().setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);

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

    public void showProgressDialog(int progress) {
        float progress1 = NumberUtils.keepPrecision(progress / 10f, 1);
        mTvTitle.setText(progress1 + "%");
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSuccess) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WatchThemeTools.getInstance().removeListener(mUpdateStatusChangeListener);
        mUpdateStatusChangeListener = null;
    }

    final String[] DERECTION_LABELS = {"TL.png", "TR.png", "BR.png", "BL.png"};

    private WatchThemeDetailsResponse.MaterialListBean getRotationBeanByCode(int position) {
        if (position > 0) {
            List<WatchThemeDetailsResponse.MaterialListBean> items = mWatchThemeData.getMaterialList();
            for (WatchThemeDetailsResponse.MaterialListBean item : items) {
                if (StringUtils.equalsIgnoreCase(item.getName(), DERECTION_LABELS[position - 1])) {
                    return item;
                }
            }
        }
        return null;
    }
}
