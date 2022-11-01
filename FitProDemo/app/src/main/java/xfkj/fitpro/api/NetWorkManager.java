/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xfkj.fitpro.api;


import static xfkj.fitpro.api.Api.APP_FITPRO_CHINA_DOMAIN;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求管理类
 * Created by gaohui.you on 2019/5/28 0028
 * Email:839939978@qq.com
 */
public class NetWorkManager {
    private final String TAG = NetWorkManager.class.getSimpleName();
    private final CommonService mCommonService;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }

    public static final NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
    }

    private NetWorkManager() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyyMMddHHmmss");
        builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            String dateStr = json.getAsJsonPrimitive().getAsString();
            Log.i(TAG, "date AsString:" + dateStr);
            if (dateStr.length() == 8) {
                return TimeUtils.string2Date(dateStr, new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
            } else {
                return TimeUtils.string2Date(dateStr, new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
            }
        });
        Gson gson = builder.create();

        this.mOkHttpClient = getClient();

        String baseUrl = APP_FITPRO_CHINA_DOMAIN;

        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create(gson))//使用Gson
                .client(mOkHttpClient)
                .build();

        this.mCommonService = mRetrofit.create(CommonService.class);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public CommonService getCommonService() {
        return mCommonService;
    }


    private synchronized  OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(15,TimeUnit.SECONDS);
            builder.readTimeout(15,TimeUnit.SECONDS);
            builder.writeTimeout(15,TimeUnit.SECONDS);
            builder.connectionPool(new ConnectionPool(1, 1, TimeUnit.MINUTES));
            // 自定义一个信任所有证书的TrustManager，添加SSLSocketFactory的时候要用到
            final X509TrustManager trustAllCert = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };
            final SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);
            builder.sslSocketFactory(sslSocketFactory, trustAllCert);
            builder.addInterceptor(chain -> {
                Request request = chain.request()
                        .newBuilder()
                        .header("accept-language", Locale.getDefault().getLanguage())
                        .addHeader("app-type","1")
                        .addHeader("app-name",AppUtils.getAppName())
                        .addHeader("app-version", AppUtils.getAppVersionName())
                        .addHeader("country","china")
                        .build();

                return chain.proceed(request);
            });

            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }
}
