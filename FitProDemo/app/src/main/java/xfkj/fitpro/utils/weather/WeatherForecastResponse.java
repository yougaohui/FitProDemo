package xfkj.fitpro.utils.weather;

import com.blankj.utilcode.util.TimeUtils;

import java.util.Date;

/**
 * 天气预报响应
 * Created by gaohui.you on 2018/11/27 0027
 * Email:839939978@qq.com
 */
public class WeatherForecastResponse {

    /**
     * tmpMin : 19
     * tmpMax : 31
     * condCodeDay : 300
     * condCodeNight : 300
     * condTextDay : 阵雨
     * condTextNight : 阵雨
     */
    private String tmpMin;
    private String tmpMax;
    private String condCodeDay;
    private String condCodeNight;
    private String condTextDay;
    private String condTextNight;
    String date;

    public String getTmpMin() {
        return tmpMin;
    }

    public void setTmpMin(String tmpMin) {
        this.tmpMin = tmpMin;
    }

    public String getTmpMax() {
        return tmpMax;
    }

    public void setTmpMax(String tmpMax) {
        this.tmpMax = tmpMax;
    }

    public String getCondCodeDay() {
        return condCodeDay;
    }

    public void setCondCodeDay(String condCodeDay) {
        this.condCodeDay = condCodeDay;
    }

    public String getCondCodeNight() {
        return condCodeNight;
    }

    public void setCondCodeNight(String condCodeNight) {
        this.condCodeNight = condCodeNight;
    }

    public String getCondTextDay() {
        return condTextDay;
    }

    public void setCondTextDay(String condTextDay) {
        this.condTextDay = condTextDay;
    }

    public String getCondTextNight() {
        return condTextNight;
    }

    public void setCondTextNight(String condTextNight) {
        this.condTextNight = condTextNight;
    }

    @Override
    public String toString() {
        return "WeatherForecastResponse{" +
                "tmpMin='" + tmpMin + '\'' +
                ", tmpMax='" + tmpMax + '\'' +
                ", condCodeDay='" + condCodeDay + '\'' +
                ", condCodeNight='" + condCodeNight + '\'' +
                ", condTextDay='" + condTextDay + '\'' +
                ", condTextNight='" + condTextNight + '\'' +
                ", date=" + date +
                '}';
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
