package xfkj.fitpro.event;


import com.legend.bluetooth.fitprolib.model.ClockDialInfoBody;

/**
 * 表盘信息事件
 * Created by gaohui.you on 2020/6/12 0012
 * Email:839939978@qq.com
 */
public class ClockDialInfoEvent {
    ClockDialInfoBody mBody;
    String mErrorInfo;

    public ClockDialInfoEvent(ClockDialInfoBody body, String errorInfo) {
        mBody = body;
        mErrorInfo = errorInfo;
    }

    public ClockDialInfoBody getBody() {
        return mBody;
    }

    public String getErrorInfo() {
        return mErrorInfo;
    }
}
