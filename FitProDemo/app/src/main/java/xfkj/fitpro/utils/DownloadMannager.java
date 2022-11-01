package xfkj.fitpro.utils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ViewUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xfkj.fitpro.api.NetWorkManager;

/**
 * Created by gaohui.you on 2020/6/12 0012
 * Email:839939978@qq.com
 */
public class DownloadMannager {

    public synchronized void startDownLoad(String url, String path) {
        new Thread(() -> {
            try {
                start();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                OkHttpClient okhttp = NetWorkManager.getInstance().getOkHttpClient();
                Response response = okhttp.newCall(request).execute();
                //开始下载文件
                InputStream is = response.body().byteStream();

                FileUtils.createOrExistsFile(path);
                File file = FileUtils.getFileByPath(path);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                bis.close();
                is.close();
                success(path);
            } catch (Exception e) {
                e.printStackTrace();
                //下载失败删缺省OTA文件
                FileUtils.delete(path);
                failed(e.toString());
            }
        }).start();
    }

    private void start() {
        if (null != mDownLoadListener) {
            ViewUtils.runOnUiThread(() -> mDownLoadListener.onStartDownload());
        }
    }

    private void failed(String info) {
        if (null != mDownLoadListener) {
            ViewUtils.runOnUiThread(() -> mDownLoadListener.onFailed(info));
        }
    }

    private void success(String path) {
        if (null != mDownLoadListener) {
            ViewUtils.runOnUiThread(() -> mDownLoadListener.onSuccess(path));
        }
    }

    DownLoadListener mDownLoadListener;

    public void setDownLoadListener(DownLoadListener downLoadListener) {
        mDownLoadListener = downLoadListener;
    }

   public interface DownLoadListener {

        void onStartDownload();

        void onSuccess(String filePath);

        void onFailed(String info);
    }
}
