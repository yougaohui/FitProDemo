package xfkj.fitpro.activity.watchTheme1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.api.DownloadMannager;
import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;
import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.Arrays;
import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.base.NewBaseActivity;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.PathUtils;

public abstract class WatchThemeDetailsBaseActivity extends NewBaseActivity {
    protected final String IMG_BIN_LOCAL_PATH = PathUtils.getWatchThemePath() + "IMG_" + TimeUtils.getNowMills() + ".bin";
    protected final String FONT_BIN_LOCAL_PATH = PathUtils.getWatchThemePath() + "FONT_" + TimeUtils.getNowMills() + ".bin";

    protected WatchThemeDetailsResponse mCurData;
    protected WatchThemeDetailsResponse.MaterialListBean mCurBean;

    protected ClockDialInfoBody mClockInfo;

    protected final int[] COLORS = {0xfffce529, 0xfffa855e, 0xffff4a5e, 0xfff7aadf, 0xffabacac, 0xff323232, 0xffadfe65, 0xff03fe8f, 0xff02fbd9, 0xff31c8f9, 0xff1601f5, 0xff8200ff};

    protected DownloadMannager mDownloadMannager;//图片固件下载器
    protected final String[] DERECTION_LABELS = {"TL.png", "TR.png", "BR.png", "BL.png"};

    //升级状态
    protected final int MSG_START = 0x00;
    protected final int MSG_SUCCESS = 0x01;
    protected final int MSG_FAILED = 0x02;
    protected final int MSG_UPGRADDING = 0x03;

    @Override
    public void initData(Bundle savedInstanceState) {
        //删除表盘主题文件夹里面的所有文件
        FileUtils.deleteAllInDir(PathUtils.getWatchThemePath());
        mClockInfo = CacheHelper.getClockDialInfo();
        mCurData = (WatchThemeDetailsResponse) getIntent().getSerializableExtra("data");
        mCurData.setPicBinpath(IMG_BIN_LOCAL_PATH);
        mCurBean = mCurData.getMaterialList().get(0);
    }

    @Override
    public void initListener() {
        //图片固件下载
        mDownloadMannager = new DownloadMannager();
        mDownloadMannager.setDownLoadListener(new DownloadMannager.DownLoadListener() {
            @Override
            public void onStartDownload(String tag) {
                DialogHelper.showDialog(mContext, getString(R.string.loadding_data));
            }

            @Override
            public void onSuccess(String path, String tag) {
                DialogHelper.hideDialog();
                handleDownloadWatchTheme(path, tag);
            }

            @Override
            public void onFailed(String info, String tag) {
                ToastUtils.showShort(getString(R.string.loading_failed) + ":" + info);
                DialogHelper.hideDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            for (LocalMedia localMedia : selectList) {
                String binImgPath = localMedia.getCutPath();
                //判断宽高
                int[] wh = ImageUtils.getSize(binImgPath);
                if (wh[0] != mClockInfo.getWidth() || wh[1] != mClockInfo.getHeight()) {
                    Log.e(TAG, "截图表盘宽高不正确:" + Arrays.toString(wh));
                    Bitmap bitmap = ImageUtils.getBitmap(binImgPath);
                    Bitmap newBitmap = ImageUtils.compressByScale(bitmap, mClockInfo.getWidth(), mClockInfo.getHeight());
                    ImageUtils.save(newBitmap, binImgPath, Bitmap.CompressFormat.PNG);
                }
                handleBin(localMedia, binImgPath);
            }
        }
    }

    protected void handleBin(LocalMedia localMedia, String binImgPath) {
        mCurData.setThumbBinPath("");
        if (mClockInfo.getAlgorithm() == 3) {//通过服务器转换成8bit图
            WatchThemeHelper.handleNetSrcBin(ImageUtils.getBitmap(binImgPath), new DownloadMannager.DownLoadListener() {
                @Override
                public void onStartDownload(String tag) {

                }

                @Override
                public void onSuccess(String filePath, String tag) {
                    DialogHelper.hideDialog();
                    mCurData.setPicBinpath(filePath);
                    mCurData.setFonBinPath(FONT_BIN_LOCAL_PATH);
                    mCurData.setPreviewImgPath(localMedia.getCutPath());
                    showImgPreView(null);
                }

                @Override
                public void onFailed(String info, String tag) {
                    DialogHelper.hideDialog();
                    ToastUtils.showShort(info);
                }
            });
        } else {//本地转换
            String path = WatchThemeHelper.convertWatchThemeBin(ImageUtils.getBitmap(binImgPath));
            if (StringUtils.isTrimEmpty(path)) {
                ToastUtils.showShort(R.string.pic_convert_failed_tips);
                return;
            }
            mCurData.setPicBinpath(path);
            mCurData.setFonBinPath(FONT_BIN_LOCAL_PATH);
            mCurData.setPreviewImgPath(localMedia.getCutPath());
            showImgPreView(null);
        }
    }

    protected void showImgPreView(View view) {
    }

    protected void handleClickInstallWatchTheme() {
        if (isCustomWatchTheme() && isSupportThumb() && !FileUtils.isFileExists(mCurData.getThumbBinPath())) {
            handleThumbClickEvent();
        } else {
            WatchThemeHelper.handleClickInstallWatchTheme(this, mCurData, mDownloadMannager, mClockInfo, isShowDialog());
        }
    }

    protected abstract boolean isShowDialog();

    /**
     * 判断是否为自定义表盘
     *
     * @return
     */
    private boolean isCustomWatchTheme() {
        return !StringUtils.isTrimEmpty(mCurData.getFonBinPath());
    }

    /**
     * 转换方向
     */
    protected void convertDirection() {
        String name = mCurBean.getName();
        int position = 0;
        if (StringUtils.equalsIgnoreCase(name, DERECTION_LABELS[0])) {
            position = 1;
        } else if (StringUtils.equalsIgnoreCase(name, DERECTION_LABELS[1])) {
            position = 2;
        } else if (StringUtils.equalsIgnoreCase(name, DERECTION_LABELS[2])) {
            position = 3;
        } else if (StringUtils.equalsIgnoreCase(name, DERECTION_LABELS[3])) {
            position = 4;
        }
        mCurData.setFontPosition(position);
    }


    protected WatchThemeDetailsResponse.MaterialListBean findBgImgUrlData() {
        for (WatchThemeDetailsResponse.MaterialListBean data : mCurData.getMaterialList()) {
            if (StringUtils.equalsIgnoreCase(data.getName(), "BG_APP.png")) {
                return data;
            }
        }
        return null;
    }

    protected void handleDownloadWatchTheme(String path, String tag) {
        WatchThemeHelper.handleDownloadWatchThmeFile(this, mCurData, mDownloadMannager, path, tag, isShowDialog());
    }

    /**
     * 判断是否支持缩略图，并且缩略图是否存在
     *
     * @return
     */
    protected boolean isSupportThumb() {
        return WatchThemeHelper.isSupportThumb(mClockInfo);
    }

    protected abstract Bitmap getThumbSrcBitmap();

    private void handleThumbClickEvent() {
        WatchThemeHelper.adjustThumbSupport(getThumbSrcBitmap(), mCurData, new WatchThemeHelper.ThumbThemeConvertCallback() {
            @Override
            public void onSuccess() {
                WatchThemeHelper.handleClickInstallWatchTheme(WatchThemeDetailsBaseActivity.this, mCurData, mDownloadMannager, mClockInfo, isShowDialog());
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除表盘主题文件夹里面的所有文件
        FileUtils.deleteAllInDir(PathUtils.getWatchThemePath());
    }
}
