package xfkj.fitpro.jni;

import android.graphics.Bitmap;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;

import xfkj.fitpro.utils.PathUtils;
import xfkj.fitpro.utils.bmp.BitmapConverter;

/**
 * Created by gaohui.you on 2020/5/13 0013
 * Email:839939978@qq.com
 */
public class BmpConvertTools {

    private static final String TAG = "BmpConvertTools";

    /**
     * 24位图转16位图
     *
     * @param inputPath
     * @param outputPath
     * @return
     */
    private static native int Bmp24ConvertBmp16(String inputPath, String outputPath, int bin);

    /**
     * 针对BK平台修改的转Bin算法
     *
     * @param sourcePath
     * @param destPath
     * @return
     */
    public static native int convertBKBin(String sourcePath, String destPath);

    static {
        try {
            System.loadLibrary("bmp-lib");
            Log.i("loadlibrary", "load library success");
        } catch (UnsatisfiedLinkError ule) {
            Log.i("loadlibrary", "load library failed : " + ule.getMessage());
        }
    }

    /**
     * 将24位图转换成16位图bin文件
     *
     * @param bitmap
     * @param isReverseBmp 是否需要翻转位图显示
     * @return
     */
    public static String convert24To16Bin(Bitmap bitmap, boolean isReverseBmp) {
        if (null == bitmap) {
            Log.e(TAG, "bitmap is no exist");
            return "";
        }
        String binPath = "";
        String srcPath = PathUtils.getOtherDir() + "BMP" + File.separator + TimeUtils.getNowMills() + "bmp24.bmp";
        String imtOTAPath = PathUtils.getOtherDir() + "IMG_OTA";
        if (FileUtils.createOrExistsDir(imtOTAPath)) {
            String destPath = imtOTAPath + File.separator + TimeUtils.getNowMills() + "bmp16.bin";
            byte[] bytes = new BitmapConverter().convert(bitmap);
            if (FileIOUtils.writeFileFromBytesByStream(srcPath, bytes)) {
                if (Bmp24ConvertBmp16(srcPath, destPath, isReverseBmp ? 1 : 0) == 0) {
                    binPath = destPath;
                } else {
                    Log.e(TAG, "convert bin failed");
                }
            } else {
                Log.e(TAG, "write bmp24 failed");
            }
        } else {
            Log.e(TAG, "img ota Path set failed");
        }
        return binPath;
    }

    public static String convertBKBin(Bitmap bitmap) {
        String sourcePath = convert24To16Bin(bitmap, false);
        String bkBinPath = PathUtils.getOtherDir() + "IMG_OTA" + File.separator + TimeUtils.getNowMills() + "BK.bin";
        if (convertBKBin(sourcePath, bkBinPath) == 1) {
            return bkBinPath;
        }
        return null;
    }
}
