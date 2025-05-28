package xfkj.fitpro.api;

import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import xfkj.fitpro.BuildConfig;

/**
 * http 助手
 * Created by gaohui.you on 2019/6/3 0003
 * Email:839939978@qq.com
 */
public class HttpHelper {

    private final String TAG = HttpHelper.class.getSimpleName();

    private static HttpHelper instance = null;

    public static HttpHelper getInstance() {
        if (null == instance) {
            instance = new HttpHelper();
        }
        return instance;
    }

    private <T> ObservableTransformer<T, T> getDefaultTransformer() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> Log.i(TAG, "start accept....."))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(t -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "原始数据:" + t.toString());
                    }
                })
                .doAfterTerminate((Action) () -> {
                    Log.i(TAG, "run.....");
                });
    }

    /**
     * 获取ota升级信息
     *
     * @param callback
     */
    public void getOTAUpgradeInfo(Callback callback) {
        OkHttpClient okhttp = NetWorkManager.getInstance().getOkHttpClient();
        Request.Builder builder = new Request.Builder();
        //请求头token添加你们公司自己的，这个只做演示用
        builder.addHeader("authorization", Uri.decode("Bearer 6fcb7f58475b4e5aad8f0f1cadce235e"));

        String bluetoothName = "Watch-TEST";//蓝牙名称，用于测试，实际情况根据你们设备进行配置
        String softVersion = "V116";//固件版本号，用于测试，实际情况根据你们设备进行配置
        if (StringUtils.isTrimEmpty(bluetoothName)) {
            Log.e(TAG, "name is empty");
            return;
        }
        String softVersionUrl = String.format("https://tomato.gulaike.com/api/v1/config/app?name=%1$s&type=1&version=%2$s&platform=%3$d", bluetoothName, softVersion, 0);
        Log.e(TAG, "softVersionUrl:" + softVersionUrl);
        if (!StringUtils.isTrimEmpty(softVersionUrl)) {
            builder.url(softVersionUrl);
            okhttp.newCall(builder.build()).enqueue(callback);
        }
    }
}
