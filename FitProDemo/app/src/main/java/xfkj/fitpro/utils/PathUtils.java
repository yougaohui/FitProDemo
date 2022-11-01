package xfkj.fitpro.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 写文件的工具类
 */
public class PathUtils {

    public static final String ROOT_DIR = "Android/data/"
            + AppUtils.getAppPackageName();
    public static final String DOWNLOAD_DIR = "download";
    public static final String CACHE_DIR = "cache";
    public static final String ICON_DIR = "icon";
    public static final String FILE_DIR = "file";
    public static final String LOG = "log";
    public static final String DB = "db";
    public static final String OTHER = "other";
    public static final String CAMERA = "camera";
    public static final String PATCH = "patch";
    public static final String OTA = "OTA";
    public static final String APK = "apk";
    public static final String WATCH_THEME = "WatchTheme";

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取下载目录
     */
    public static String getDownloadDir() {
        return getDir(DOWNLOAD_DIR);
    }

    /**
     * 获取缓存目录
     */
    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }

    /**
     * 获取文件目录
     */
    public static String getFileDir() {
        return getDir(FILE_DIR);
    }

    /**
     * 获取icon目录
     */
    public static String getIconDir() {
        return getDir(ICON_DIR);
    }

    /**
     * 获取日志目录
     */
    public static String getLogDir() {
        return getDir(LOG);
    }

    /**
     * 获取数据库目录
     */
    public static String getDbDir() {
        return getDir(DB);
    }

    /**
     * 获取其他的文件目录
     */
    public static String getOtherDir() {
        return getDir(OTHER);
    }

    /**
     * 表盘主题
     * @return
     */
    public static String getWatchThemePath() {
        return getDir(OTHER) + WATCH_THEME + File.separator;
    }

    /**
     * 获取相机文件目录
     *
     * @return
     */
    public static String getCameraDir() {
        return com.blankj.utilcode.util.PathUtils.getExternalAppPicturesPath();
    }

    /**
     * 获取补丁目录
     *
     * @return
     */
    public static String getPatchDir() {
        return getDir(PATCH);
    }

    /**
     * 获取OTA目录
     *
     * @return
     */
    public static String getOTADir() {
        return getDir(OTA);
    }

    /**
     * 获取apk目录
     *
     * @return
     */
    public static String getAPKDir() {
        return getDir(APK);
    }

    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        return path;
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = Utils.getApp().getCacheDir();
        if (null == f) {
            return Utils.getApp().getDatabasePath("fitPro").getAbsolutePath();
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 获取文件目录
     */
    public static String getFilePath() {
        File f = Utils.getApp().getFilesDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    public static List<String> getAssetPicFontPath(Context context){
        return getAssertPath(context, "fontota","FONT_OTA");
    }

    public static List<String> getAssertPath(Context context, String assertDir,String saveDirName) {
        AssetManager am = context.getAssets();
        String[] path = null;
        try {
            path = am.list(assertDir);  // ""获取所有,填入目录获取该目录下所有资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> pciPaths = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            if ((path[i].endsWith(".bin"))) {
                pciPaths.add(copyAssetAndWrite(context,path[i],PathUtils.getOtherDir() + saveDirName,assertDir));
            }
        }
        return pciPaths;
    }

    private static String copyAssetAndWrite(Context context, String fileName,String cachePath,String assetDirName) {
        try {
            String path = cachePath + File.separator + fileName;
            if (FileUtils.isFileExists(path)) {
                Log.e("aaa", "file already exist");
                return path;
            }
            File cacheDir = FileUtils.getFileByPath(cachePath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return "";
                }
            }
            InputStream is = context.getAssets().open(assetDirName + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}