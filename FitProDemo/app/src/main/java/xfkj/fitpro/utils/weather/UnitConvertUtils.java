package xfkj.fitpro.utils.weather;

import com.legend.bluetooth.fitprolib.utils.NumberUtils;

/**
 * 单位转换
 * Created by gaohui.you on 2018/10/17 0017
 * Email:839939978@qq.com
 */
public class UnitConvertUtils {
    /**
     * 摄氏转华氏
     * @param sheshi
     * @return
     */
    public static int sheshiConvertHuashi(int sheshi) {
        return (int) NumberUtils.keepPrecision((sheshi * 1.8f + 32),0);
    }

    public static float sheshiConvertHuashiFloat(float sheshi) {
        return NumberUtils.keepPrecision((sheshi * 1.8f + 32), 1);
    }
}
