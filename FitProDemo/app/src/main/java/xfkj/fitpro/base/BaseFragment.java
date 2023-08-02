package xfkj.fitpro.base;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;

import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import static xfkj.fitpro.application.MyApplication.clearChatMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import xfkj.fitpro.utils.EventBusUtils;

public class BaseFragment extends Fragment {

    public LayoutInflater inflater;//布局填充器
    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(getContext());
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SDKTools.mediaPlayer != null && SDKTools.mediaPlayer.isPlaying()){
            SDKTools.mediaPlayer.pause();
        }
        clearChatMsg(-1);//清除所有通知
        BleManager.getInstance().getConnetedBleState();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            //Fragment隐藏时调用
        }else {
            if (SDKTools.mediaPlayer != null && SDKTools.mediaPlayer.isPlaying()){
                SDKTools.mediaPlayer.pause();
            }
            BleManager.getInstance().getConnetedBleState();
            clearChatMsg(-1);//清除所有通知
        }
    }

    private List<Integer> mDelayWhats = new ArrayList<>();

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            localHandleMessage(msg);
            //移除超时缓存key
            if (mDelayWhats.contains(Integer.valueOf(msg.what))) {
                mDelayWhats.remove(Integer.valueOf(msg.what));
            }
        }
    };

    protected void localHandleMessage(Message msg) {

    }

    protected void startTimeOut(int what, long time) {
        mDelayWhats.add(what);
        mHandler.sendEmptyMessageDelayed(what, time);
    }

    protected void stopTimeOut(int what) {
        mDelayWhats.remove(Integer.valueOf(what));
        mHandler.removeMessages(what);
    }

    protected void stopAllTimeOut() {
        if (!mDelayWhats.isEmpty()) {
            for (Integer what : mDelayWhats) {
                stopTimeOut(what);
            }
        }
    }

    public List<Integer> getDelayWhats() {
        return mDelayWhats;
    }

    /**
     * 事件广播入口
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {/* Do something */}
}
