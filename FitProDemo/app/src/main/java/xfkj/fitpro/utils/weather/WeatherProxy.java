package xfkj.fitpro.utils.weather;

import android.util.Log;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.legend.bluetooth.fitprolib.bluetooth.ByteUtil;
import com.legend.bluetooth.fitprolib.bluetooth.SendData;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 处理天气的代理者
 * Created by gaohui.you on 2018/11/28 0028
 * Email:839939978@qq.com
 */
public class WeatherProxy {
    private static final String TAG = WeatherProxy.class.getSimpleName();

    private static byte[] getBleWeatherProtocol(WeatherForecastResponse body, boolean isTest) {
        //温度单位;0为摄氏，1为华氏
        int symBol = 0;
        //温度
        int minTemp = Integer.valueOf(body.getTmpMin());
        int maxTemp = Integer.valueOf(body.getTmpMax());
        //根据温度单位转换温度
        minTemp = (symBol == 0 ? minTemp : UnitConvertUtils.sheshiConvertHuashi(minTemp));
        maxTemp = (symBol == 0 ? maxTemp : UnitConvertUtils.sheshiConvertHuashi(maxTemp));
        //天气类型
        int conCode = Integer.valueOf(body.getCondCodeDay());
        int type = isTest ? conCode : getWeatherStatusByCode(conCode);
        byte[] value = {(byte) minTemp, (byte) maxTemp, (byte) type, (byte) symBol};
        return SendData.getWeatherInfoValue(value);
    }

    /**
     * 获取天气状态码
     */
    private static int getWeatherStatusByCode(int code) {
        if (100 == code) {//晴天
            return 0x01;
        } else if (104 == code) {//阴天
            return 0x2;
        } else if (101 == code || 102 == code || 103 == code) {//多云
            return 0x03;
        } else if (305 == code || 309 == code) {//小雨
            return 0x04;
        } else if (306 == code || 314 == code || 399 == code) {//中雨
            return 0x5;
        } else if (307 == code || 308 == code
                || 310 == code || 311 == code
                || 312 == code || 315 == code
                || 316 == code || 317 == code
                || 318 == code) {//大雨
            return 0x6;
        } else if (300 == code || 301 == code || 302 == code || 303 == code) {//雷阵雨
            return 0x7;
        } else if (400 == code || 407 == code) {//小雪
            return 0x8;
        } else if (401 == code || 408 == code || 499 == code) {//中雪
            return 0x9;
        } else if (402 == code || 403 == code || 409 == code || 410 == code) {//大雪
            return 0xA;
        } else if (404 == code || 405 == code || 406 == code) {//雨夹雪
            return 0xB;
        } else if (500 == code || 501 == code
                || 502 == code || 509 == code
                || 510 == code || 511 == code
                || 512 == code || 513 == code
                || 514 == code || 515 == code) {//雾霾
            return 0x0C;
        } else if (304 == code || 313 == code) {//雨加冰雹
            return 0x0D;
        } else if (503 == code || 504 == code || 507 == code || 508 == code) {//尘埃，沙尘暴
            return 0x0E;
        } else if (200 == code || 201 == code || 202 == code || 203 == code || 204 == code) {//风
            return 0xF;
        } else if (205 == code || 206 == code || 207 == code || 208 == code) {//大风
            return 0x10;
        } else if (209 == code || 210 == code || 211 == code) {//狂风
            return 0x11;
        } else if (212 == code) {//龙卷风
            return 0x12;
        } else if (231 == code) {//雷暴
            return 0x13;
        }
        return 0x03;
    }

    /**
     * 同步多天气
     * @param isTest 用于测试天气类型，release版本一定用false
     */
    public static void sync3DaysWeather(Weather2Response body,boolean isTest) {
        byte[] value = WeatherProxy.getBleWeatherProtocol2(body, isTest);
        if (value != null) {
            SDKTools.mService.commandPoolWrite(value, "同步多天气");
        }
    }

    /**
     * 同步单天气
     * @param weather
     * @param isTest 用于测试天气类型，release版本一定用false
     */
    public static void syncWeather(WeatherForecastResponse weather,boolean isTest) {
        byte[] value = WeatherProxy.getBleWeatherProtocol(weather,isTest);
        if (value != null) {
            SDKTools.mService.commandPoolWrite(value, "同步单天气");
        }
    }


    private static byte[] getBleWeatherProtocol2(Weather2Response body) {
        return getBleWeatherProtocol2(body, false);
    }

    /**
     * @param body    数据体
     * @param isTest 是否为测试模式，正式版本一定为false
     * @return
     */
    private static byte[] getBleWeatherProtocol2(Weather2Response body, boolean isTest) {
        try {
            if (null != body) {
                byte[] value = new byte[0];
                String loc = body.getLoc();
                //温度单位;0为摄氏，1为华氏
                int symBol = 0;
                List<Weather2Response.ListDTO> datas = body.getList();
                int days = CollectionUtils.size(datas);
                if (days > 0) {
                    byte[] locBytes = loc.getBytes(StandardCharsets.UTF_8);
                    value = NumberUtils.combineBytes(value, new byte[]{(byte) locBytes.length}, locBytes, new byte[]{(byte) days});
                    for (Weather2Response.ListDTO data : datas) {
                        Date date = TimeUtils.string2Date(data.getDay(), new SimpleDateFormat("yyyy-MM-dd"));
                        String dayStr = TimeUtils.date2String(date, new SimpleDateFormat("yyyyMMdd"));
                        int weekIndex = TimeUtils.getValueByCalendarField(date, Calendar.DAY_OF_WEEK);
                        //温度
                        int minTemp = Integer.valueOf(data.getTmpMin());
                        int maxTemp = Integer.valueOf(data.getTmpMax());
                        //根据温度单位转换温度
                        minTemp = (symBol == 0 ? minTemp : UnitConvertUtils.sheshiConvertHuashi(minTemp));
                        maxTemp = (symBol == 0 ? maxTemp : UnitConvertUtils.sheshiConvertHuashi(maxTemp));
                        //白天晚上天气状况
                        int dayWeatherType = isTest ? Integer.valueOf(data.getCondCodeDay()) : getWeatherStatusByCode(Integer.valueOf(data.getCondCodeDay()));
                        int nightWeatherType = isTest ? Integer.valueOf(data.getCondCodeDay()) : getWeatherStatusByCode(Integer.valueOf(data.getCondCodeNight()));
                        //紫外线
                        int uvIndex = Integer.valueOf(data.getUvIndex());
                        //空气湿度
                        int humidity = Integer.valueOf(data.getHumidity());
                        //能见度
                        int vis = Integer.valueOf(data.getVis());
                        //风力
                        int dayWindSpeed = Integer.valueOf(data.getWindSpeedDay());
                        int nightWindSpeed = Integer.valueOf(data.getWindSpeedNight());
                        //降雨量
                        float precip = Float.valueOf(data.getPrecip());
                        //日出日落时间
                        String[] sunRises = data.getSunrise().split(":");
                        int sunRise = Integer.valueOf(sunRises[0]) * 60 + Integer.valueOf(sunRises[1]);
                        String[] sunSets = data.getSunset().split(":");
                        int sunSet = Integer.valueOf(sunSets[0]) * 60 + Integer.valueOf(sunSets[1]);
                        value = NumberUtils.combineBytes(value, ByteUtil.intToBytes(Integer.valueOf(dayStr)),
                                new byte[]{(byte) weekIndex, (byte) minTemp, (byte) maxTemp, (byte) dayWeatherType,
                                        (byte) nightWeatherType, (byte) symBol, (byte) uvIndex, (byte) humidity,
                                        (byte) vis, (byte) dayWindSpeed, (byte) nightWindSpeed, (byte) precip},
                                ByteUtil.shortToByte((short) sunRise),
                                ByteUtil.shortToByte((short) sunSet)
                        );
                    }
                }
                return SendData.getWeatherInfoValue2(value);
            } else {
                Log.e(TAG, "不存在缓存天气!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "天气错误:" + e);
        }
        return null;
    }

}
