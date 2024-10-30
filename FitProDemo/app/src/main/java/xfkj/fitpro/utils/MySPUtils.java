package xfkj.fitpro.utils;


import static com.legend.bluetooth.fitprolib.utils.FitProSpUtils.getBluetoothAddress;

import com.blankj.utilcode.util.SPUtils;

/**
 * 简单数据库工具类
 * Created by gaohui.you on 2019/6/18 0018
 * Email:839939978@qq.com
 */
public class MySPUtils {

    private static SPUtils getSP() {
        return SPUtils.getInstance("default");
    }

    public static void saveSOSContract(String phoneNum) {
        getSP().put("SOSContract" + getBluetoothAddress(), phoneNum);
    }

    public static String getSOSContract() {
        return getSP().getString("SOSContract" + getBluetoothAddress(), "");
    }
}
