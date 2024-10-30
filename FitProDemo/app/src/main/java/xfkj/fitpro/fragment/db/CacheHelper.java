package xfkj.fitpro.fragment.db;

import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;


/**
 * 数据库助手
 * Created by gaohui.you on 2019/5/28 0028
 * Email:839939978@qq.com
 */
public class CacheHelper {

    private static ClockDialInfoBody watchInfo;

    /**
     * 获取表盘信息
     *
     * @return
     */
    public static ClockDialInfoBody getClockDialInfo() {
        return watchInfo;
    }

    public static void setWatchInfo(ClockDialInfoBody watchInfo) {
        CacheHelper.watchInfo = watchInfo;
    }
}
