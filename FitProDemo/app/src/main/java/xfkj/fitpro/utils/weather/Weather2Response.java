package xfkj.fitpro.utils.weather;

import java.util.List;

public class Weather2Response {

    private String loc;
    private List<ListDTO> list;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        private String day;  // 预报日期  yyyy-MM-dd
        private String tmpMin; // 预报当天最低温度
        private String tmpMax;  // 预报当天最高温度
        private String condCodeDay; // 预报白天天气状况的图标代码
        private String condCodeNight; // 预报夜间天气状况的图标代码
        private String condTextDay; // 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
        private String condTextNight; // 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
        private String sunrise; // 日出时间，在高纬度地区可能为空  HH:mm
        private String sunset;  // 日落时间，在高纬度地区可能为空  HH:mm
        private String windSpeedDay; // 预报白天风速，公里/小时
        private String windSpeedNight; // 预报夜间风速，公里/小时
        private String precip;  // 预报当天总降水量，默认单位：毫米
        private String uvIndex;  // 紫外线强度指数
        private String humidity;  // 相对湿度，百分比数值
        private String vis;  // 能见度，默认单位：公里

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

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

        public String getSunrise() {
            return sunrise;
        }

        public void setSunrise(String sunrise) {
            this.sunrise = sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public void setSunset(String sunset) {
            this.sunset = sunset;
        }

        public String getWindSpeedDay() {
            return windSpeedDay;
        }

        public void setWindSpeedDay(String windSpeedDay) {
            this.windSpeedDay = windSpeedDay;
        }

        public String getWindSpeedNight() {
            return windSpeedNight;
        }

        public void setWindSpeedNight(String windSpeedNight) {
            this.windSpeedNight = windSpeedNight;
        }

        public String getPrecip() {
            return precip;
        }

        public void setPrecip(String precip) {
            this.precip = precip;
        }

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getVis() {
            return vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
        }

        @Override
        public String toString() {
            return "ListDTO{" +
                    "day='" + day + '\'' +
                    ", tmpMin='" + tmpMin + '\'' +
                    ", tmpMax='" + tmpMax + '\'' +
                    ", condCodeDay='" + condCodeDay + '\'' +
                    ", condCodeNight='" + condCodeNight + '\'' +
                    ", condTextDay='" + condTextDay + '\'' +
                    ", condTextNight='" + condTextNight + '\'' +
                    ", sunrise='" + sunrise + '\'' +
                    ", sunset='" + sunset + '\'' +
                    ", windSpeedDay='" + windSpeedDay + '\'' +
                    ", windSpeedNight='" + windSpeedNight + '\'' +
                    ", precip='" + precip + '\'' +
                    ", uvIndex='" + uvIndex + '\'' +
                    ", humidity='" + humidity + '\'' +
                    ", vis='" + vis + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Weather2Response{" +
                "loc='" + loc + '\'' +
                ", list=" + list +
                '}';
    }
}
