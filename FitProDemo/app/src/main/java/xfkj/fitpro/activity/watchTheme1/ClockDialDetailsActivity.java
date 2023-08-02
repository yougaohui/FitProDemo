package xfkj.fitpro.activity.watchTheme1;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.CommandPool;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.WatchThemeTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import xfkj.fitpro.R;
import xfkj.fitpro.adapter.ClockDialDetailsAdapter;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.PictureSelectorUtils;
import xfkj.fitpro.utils.glide.GlideUitls;
import xfkj.fitpro.view.RxRunTextView;
import xfkj.fitpro.view.SpaceItemDecoration;

public class ClockDialDetailsActivity extends WatchThemeDetailsBaseActivity {
    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.toolbar_back)
    RelativeLayout mToolbarBack;
    @BindView(R.id.toolbar_title)
    RxRunTextView mToolbarTitle;
    @BindView(R.id.img_left)
    ImageView mImgLeft;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.btn_right)
    Button mBtnRight;
    @BindView(R.id.img_btn_right)
    ImageButton mImgBtnRight;
    @BindView(R.id.tv_finish)
    TextView mTvFinish;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.img_cur_clock_dial_1)
    ImageView mImgCurClockDial1;
    @BindView(R.id.img_cur_clock_dial_2)
    ImageView mImgCurClockDial2;
    @BindView(R.id.btn_switch_bg)
    AppCompatButton mBtnSwitchBg;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_upgrade)
    Button mBtnUpgrade;
    @BindView(R.id.ll_custom_pic)
    LinearLayout mLlCustomPic;
    @BindView(R.id.frm_preview)
    View mFrmPreview;

    ClockDialDetailsAdapter mAdapter;

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
        return R.layout.activity_clock_dial_details;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Log.e(TAG, "data:" + mCurData.toString());
        initAdapter();
        showOrHideCustomPic();
    }

    private void showOrHideCustomPic() {
        if (mClockInfo.getGrade() == 1 || !mCurData.isCutomTheme() || mCurData.getFontFile() == null) {
            mBtnSwitchBg.setVisibility(View.GONE);
        } else {
            mBtnSwitchBg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置适配器数据
     */
    private void initAdapter() {
        this.mAdapter = new ClockDialDetailsAdapter(new ArrayList<>());
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        this.mRecyclerView.addItemDecoration(new SpaceItemDecoration(1, 1, 1, 1));
        this.mRecyclerView.setAdapter(mAdapter);

        List<WatchThemeDetailsResponse.MaterialListBean> datas;
        List<WatchThemeDetailsResponse.MaterialListBean> infos = null;
        try {
            datas = mCurData.getMaterialList();
            WatchThemeDetailsResponse.MaterialListBean bgUrlImgData = findBgImgUrlData();
            mAdapter.setBgUrl(bgUrlImgData.getUrl());
            infos = mAdapter.getInfos();
            infos.clear();
            infos.addAll(findDerectionDatas(datas));
        } catch (Exception e) {
            ToastUtils.showShort(e.toString());
            finish();
        }
        mCurBean = infos.get(0);
        mAdapter.notifyDataSetChanged();

        convertDirection();
        GlideUitls.loadImgFromSever(mAdapter.getBgUrl(), mImgCurClockDial1);
        GlideUitls.loadImgFromSever(mCurBean.getUrl(), mImgCurClockDial2);
    }

    /**
     * 获取方向集合
     *
     * @param datas
     * @return
     */
    private List<WatchThemeDetailsResponse.MaterialListBean> findDerectionDatas(List<WatchThemeDetailsResponse.MaterialListBean> datas) {
        List<WatchThemeDetailsResponse.MaterialListBean> derectionDatas = new ArrayList<>();
        List<String> labels = Arrays.asList(DERECTION_LABELS);
        for (WatchThemeDetailsResponse.MaterialListBean data : datas) {
            if (!derectionDatas.contains(data) && labels.contains(data.getName())) {
                derectionDatas.add(data);
            }
        }
        return derectionDatas;
    }

    @Override
    public void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener((view, viewType, data, position) -> {
            mCurBean = (WatchThemeDetailsResponse.MaterialListBean) data;
            showImgPreView(view);
        });
        WatchThemeTools.getInstance().addStatusChangeListener(mUpdateStatusChangeListener);
    }

    protected void showImgPreView(View view) {
        String previewPath = mCurData.getPreviewImgPath();
        if (StringUtils.isTrimEmpty(previewPath) && view != null) {
            mImgCurClockDial1.setImageBitmap(ImageUtils.view2Bitmap(view));
            mImgCurClockDial2.setVisibility(View.INVISIBLE);
        } else {
            mImgCurClockDial1.setImageBitmap(ImageUtils.getBitmap(previewPath));
            mImgCurClockDial2.setVisibility(View.VISIBLE);
            GlideUitls.loadImgFromSever(mCurBean.getUrl(), mImgCurClockDial2);
        }
        convertDirection();
    }

    @OnClick(R.id.btn_upgrade)
    public void onMBtnUpgradeClicked() {
        if (!SDKCmdMannager.isConnected()) {
            mAdapter.notifyDataSetChanged();
            ToastUtils.showShort(R.string.unconnected);
            return;
        }
        handleClickInstallWatchTheme();
    }

    @OnClick(R.id.btn_switch_bg)
    public void onMBtnSwitchBgClicked() {
        PictureSelectorUtils.startBiaoPanPictureSelector(ClockDialDetailsActivity.this, mClockInfo.getWidth(), mClockInfo.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            DialogHelper.correctDialog(mUpgradeDialog);
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
            //Constant.mService.getWriteChar().setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);

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

    @Override
    protected boolean isShowDialog() {
        return false;
    }

    @Override
    protected Bitmap getThumbSrcBitmap() {
        return ImageUtils.view2Bitmap(mFrmPreview);
    }
}
