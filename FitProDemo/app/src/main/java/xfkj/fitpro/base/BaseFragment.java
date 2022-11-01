package xfkj.fitpro.base;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;

import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import static xfkj.fitpro.application.MyApplication.clearChatMsg;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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


}
