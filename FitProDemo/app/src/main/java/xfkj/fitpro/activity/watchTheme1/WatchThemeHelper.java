package xfkj.fitpro.activity.watchTheme1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.api.DownloadMannager;
import com.legend.bluetooth.fitprolib.api.HttpHelper;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;
import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.WatchThemeTools;

import java.io.File;
import java.util.List;
import java.util.Locale;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.db.CacheHelper;
import xfkj.fitpro.jni.BmpConvertTools;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.PathUtils;
import xfkj.fitpro.utils.bmp.BitmapConverter;
import xfkj.fitpro.view.dialog.WatchThemeDialog;
import xfkj.fitpro.view.dialog.WatchThemePosPickerDialog;

public class WatchThemeHelper {

    public final static String DEFAULT_WATCHTHME_BIN_TAG = "default_WatchTheme";
    public final static String WATCHTHME_THUMB_TAG = "thumBin";
    public final static String WATCHTHME_PREVIEW_TAG = "preview";

    private static final String TAG = "WatchThemeHelper";

    public static float getConvertHeight(float width) {
        ClockDialInfoBody info = CacheHelper.getClockDialInfo();
        if (info != null) {
            float bili = (info.getHeight() * 1.0f) / (info.getWidth() * 1.0f);
            return width * bili;
        }
        return width;
    }

    /**
     * 升级表盘
     *
     * @param context
     * @param data
     */
    public static void startWatchTheme(AppCompatActivity context, WatchThemeDetailsResponse data, boolean showWatchThemeDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tips_txt);
        builder.setMessage(R.string.watch_theme_upgrade_tips_content);
        builder.setPositiveButton(R.string.known, (dialog1, which) -> {
            dialog1.dismiss();
            if (!Constants.isDeviceChoicePic && CacheHelper.getClockDialInfo().getPictureNums() > 1) {//弹出框选择位置
                WatchThemePosPickerDialog dialog = new WatchThemePosPickerDialog();
                dialog.setWatchThemeSelectListener(new WatchThemePosPickerDialog.WatchThemeSelectListener() {
                    @Override
                    public void onConfirm(int index) {
                        data.setReplacePicPos(index);
                        if (showWatchThemeDialog) {
                            WatchThemeDialog dialog = new WatchThemeDialog(data);
                            dialog.show(context.getSupportFragmentManager(), "showWatchThemeDialog");
                        } else {
                            WatchThemeTools.getInstance().startFile(CacheHelper.getClockDialInfo(),data);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                dialog.show(context.getSupportFragmentManager(), "显示选择表盘dialog");
            } else {
                if (showWatchThemeDialog) {
                    WatchThemeDialog dialog = new WatchThemeDialog(data);
                    dialog.show(context.getSupportFragmentManager(), "showWatchThemeDialog");
                } else {
                    WatchThemeTools.getInstance().startFile(CacheHelper.getClockDialInfo(),data);
                }
            }
        });
        AlertDialog tipsDialog = builder.create();
        tipsDialog.show();
        DialogHelper.correctDialog(tipsDialog);
    }

    /**
     * 转换表盘文件
     *
     * @param bitmap
     * @return bin文件按路径
     */
    public static String convertWatchThemeBin(Bitmap bitmap) {
        ClockDialInfoBody clockInfo = CacheHelper.getClockDialInfo();
        String path;
        byte algorithm = clockInfo.getAlgorithm();
        if (algorithm == 1) {
            path = BmpConvertTools.convertBKBin(bitmap);
        } else if (algorithm == 2) {
            path = BmpConvertTools.convert24To16Bin(bitmap, false);
        } else {
            byte[] config = NumberUtils.intToBinary(clockInfo.getConfig());
            path = BmpConvertTools.convert24To16Bin(bitmap, config[0] == 0);
        }
        return path;
    }

    public static void adjustThumbSupport(Bitmap srcBitmap, WatchThemeDetailsResponse watchInfo, ThumbThemeConvertCallback callback) {
        ClockDialInfoBody info = CacheHelper.getClockDialInfo();
        if (isSupportThumb(info)) {
            int perInt = info.getThumbPercent();
            if (perInt < 10 || perInt > 100) {
                ToastUtils.showShort(R.string.thumb_not_normal);
                return;
            }
            short scaleWith = info.getThumbWidth();
            short scaleHeight = info.getThumbHeight();
            Bitmap thumBitmap = ImageUtils.scale(srcBitmap, scaleWith, scaleHeight);
            int angle = info.getThumbRoundAngle();
            if (angle > 0 && angle < 90) {
                thumBitmap = ImageUtils.toRoundCorner(thumBitmap, info.getThumbRoundAngle());
            }
            if (info.getAlgorithm() == 3) {
                handleNetThumBin(watchInfo, thumBitmap, callback);
            } else {
                handleLocalThumBin(watchInfo, thumBitmap, callback);
            }
        }
    }

    /**
     * 通过网络转换成成bin
     */
    private static void handleNetThumBin(WatchThemeDetailsResponse watchInfo, Bitmap thumBitmap, ThumbThemeConvertCallback callback) {
        BitmapConverter bitmapConverter = new BitmapConverter();
        byte[] bitmap24 = bitmapConverter.convert(thumBitmap);
        String destPath = PathUtils.getWatchThemePath() + File.separator + TimeUtils.getNowMills() + "_thumb_bmp24.bmp";
        boolean success = FileIOUtils.writeFileFromBytesByStream(destPath, bitmap24);
        if (success) {
            httpConvert8Bit(destPath, false, new DownloadMannager.DownLoadListener() {
                @Override
                public void onStartDownload(String tag) {
                }

                @Override
                public void onSuccess(String filePath, String tag) {
                    watchInfo.setThumbBinPath(filePath);
                    DialogHelper.hideDialog();
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailed(String info, String tag) {
                    ToastUtils.showShort(info);
                    DialogHelper.hideDialog();
                    if (callback != null) {
                        callback.onFailed();
                    }
                }
            });
        }
    }

    public static void handleNetSrcBin(Bitmap bitmap, DownloadMannager.DownLoadListener listener) {
        BitmapConverter bitmapConverter = new BitmapConverter();
        byte[] bitmap24 = bitmapConverter.convert(bitmap);
        String destPath = PathUtils.getWatchThemePath() + File.separator + TimeUtils.getNowMills() + "_src_bmp24.bmp";
        boolean success = FileIOUtils.writeFileFromBytesByStream(destPath, bitmap24);
        if (success) {
            httpConvert8Bit(destPath, true, listener);
        }
    }

    private static void httpConvert8Bit(String destPath, boolean isFont, DownloadMannager.DownLoadListener listener) {
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity != null && !(topActivity.isFinishing() || topActivity.isDestroyed())) {
            DialogHelper.showDialog(topActivity, topActivity.getString(R.string.conver_image));
        }
        HttpHelper.getInstance().bmp16Convert8BitByNetwork(FileUtils.getFileByPath(destPath), isFont, listener);
    }

    /**
     * 同构本地生成
     */
    private static void handleLocalThumBin(WatchThemeDetailsResponse watchInfo, Bitmap thumBitmap, ThumbThemeConvertCallback callback) {
        String thumbPath = WatchThemeHelper.convertWatchThemeBin(thumBitmap);
        watchInfo.setThumbBinPath(thumbPath);
        if (callback != null) {
            callback.onSuccess();
        }
    }

    /**
     * 是否支持缩略图
     *
     * @return
     */
    public static boolean isSupportThumb() {
        return isSupportThumb(CacheHelper.getClockDialInfo());
    }

    public static boolean isSupportThumb(ClockDialInfoBody info) {
        if (info == null) return false;
        byte[] configs = NumberUtils.intToBinary(info.getConfig());
        return configs[5] == 1;
    }

    public static void handleClickInstallWatchTheme(AppCompatActivity activity, WatchThemeDetailsResponse watchData, DownloadMannager downloadMannager, ClockDialInfoBody clockInfo, boolean isShowDialog) {
        if (SDKCmdMannager.isConnected()) {
            if (StringUtils.isTrimEmpty(watchData.getFonBinPath())) {
                Log.e(TAG, "升级固定表盘");
                if (!FileUtils.isFileExists(watchData.getPicBinpath())) {
                    Log.i(TAG, "isSupportThumb and not find thumbData222");
                    downloadMannager.startDownLoad(watchData.getBinFile().getUrl(), watchData.getPicBinpath(), null, DEFAULT_WATCHTHME_BIN_TAG);
                } else if (isSupportThumb() && !FileUtils.isFileExists(watchData.getThumbBinPath())) {
                    Log.i(TAG, "isSupportThumb and find thumbData");
                    checkAndDownloadThum(watchData, downloadMannager);
                } else {
                    Log.i(TAG, "isSupportThumb and not find thumbData333");
                    startWatchTheme(activity, watchData, isShowDialog);
                }
            } else {
                Log.e(TAG, "升级自定义表盘");
                if (WatchThemeHelper.isSupportThumb(clockInfo) && StringUtils.isTrimEmpty(watchData.getThumbBinPath())) {
                    ToastUtils.showShort(R.string.thumb_not_have);
                    return;
                }
                //易兆微8位图不需要字体文件
                if ((clockInfo.getAlgorithm() != 3) && !FileUtils.isFileExists(watchData.getFonBinPath())) {
                    downloadMannager.startDownLoad(watchData.getFontFile().getUrl(), watchData.getFonBinPath());
                } else {
                    startWatchTheme(activity, watchData, isShowDialog);
                }
            }
        } else {
            ToastUtils.showShort(R.string.unconnected);
        }
    }

    public static WatchThemeDetailsResponse.MaterialListBean findThumbData(WatchThemeDetailsResponse response) {
        for (WatchThemeDetailsResponse.MaterialListBean data : response.getMaterialList()) {
            if (StringUtils.equalsIgnoreCase(data.getName(), "THU8.bin")) {
                return data;
            }
        }
        return null;
    }

    public static void handleDownloadWatchThmeFile(AppCompatActivity context, WatchThemeDetailsResponse response, DownloadMannager downloadMannager, String path, String tag, boolean isShowDialog) {
        //默认表盘
        if (StringUtils.equalsIgnoreCase(tag, DEFAULT_WATCHTHME_BIN_TAG)) {
            if (WatchThemeHelper.isSupportThumb()) {
                checkAndDownloadThum(response, downloadMannager);
            }else {
                WatchThemeHelper.startWatchTheme(context, response, isShowDialog);
            }
        } else if (tag.equalsIgnoreCase(WATCHTHME_THUMB_TAG)) {
            response.setThumbBinPath(path);
            WatchThemeHelper.startWatchTheme(context, response, isShowDialog);
        } else if (tag.equalsIgnoreCase(WATCHTHME_PREVIEW_TAG)) {
            handleHardWatchThemeThumb(context, response, downloadMannager, path, isShowDialog);
        }
    }

    private static void handleHardWatchThemeThumb(AppCompatActivity context, WatchThemeDetailsResponse response, DownloadMannager downloadMannager, String path, boolean isShowDialog) {
        adjustThumbSupport(ImageUtils.getBitmap(path), response, new ThumbThemeConvertCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "convert success");
                ClockDialInfoBody info = CacheHelper.getClockDialInfo();
                WatchThemeHelper.handleClickInstallWatchTheme(context, response, downloadMannager, info, isShowDialog);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private static void checkAndDownloadThum(WatchThemeDetailsResponse response, DownloadMannager downloadMannager) {
        if (WatchThemeHelper.isSupportThumb()) {
            WatchThemeDetailsResponse.MaterialListBean thumData = WatchThemeHelper.findThumbData(response);
            if (thumData != null) {
                Log.i(TAG, "support thumb image1");
                final String THUM_BIN_PATH = getPreviewLocalPath();
                downloadMannager.startDownLoad(thumData.getUrl(), THUM_BIN_PATH, null, WATCHTHME_THUMB_TAG);
            } else {
                Log.i(TAG, "support thumb image2");
                WatchThemeDetailsResponse.MaterialListBean previewData = findPreviewBg(response.getMaterialList());
                downloadMannager.startDownLoad(previewData.getUrl(), getPreviewLocalPath(), null, WATCHTHME_PREVIEW_TAG);
            }
        }
    }

    public static String getPreviewLocalPath() {
        return PathUtils.getWatchThemePath() + TimeUtils.getNowMills() + "_PREVIEW" + ".png";
    }

    public static WatchThemeDetailsResponse.MaterialListBean findPreviewBg(List<WatchThemeDetailsResponse.MaterialListBean> datas) {
        for (WatchThemeDetailsResponse.MaterialListBean data : datas) {
            String name = data.getName().toLowerCase(Locale.ROOT);
            if (StringUtils.equalsIgnoreCase(name, "PREVIEW.png")) {
                return data;
            }
        }
        return null;
    }

    /**
     * 缩略图转换状态
     */
    public interface ThumbThemeConvertCallback {
        void onSuccess();

        void onFailed();
    }
}
