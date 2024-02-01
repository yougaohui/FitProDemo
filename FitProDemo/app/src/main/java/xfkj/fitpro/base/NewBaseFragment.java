package xfkj.fitpro.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 新的Fragment基类
 * Created by gaohui.you on 2019/6/5 0005
 * Email:839939978@qq.com
 */
public abstract class NewBaseFragment extends Fragment  {
    protected String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    private boolean isCreate = false;
    private List<Integer> mDelayWhats = new ArrayList<>();
    private boolean isImmersionBar = false;

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), container, false);
        createView(inflate);
        mContext = getActivity();
        initData(savedInstanceState);
        initListener();
        isCreate = true;
        return inflate;
    }

    protected void createView(View view) {
        
    }

    public abstract int getLayoutId();

    public abstract void initData(Bundle savedInstanceState);

    public abstract void initListener();

    /**
     * 设置数据
     *
     * @param object
     */
    public void setData(Object object) {

    }

    /**
     * 通知数据有变化
     */
    public void notifyDataChange() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.isCreate = false;
        stopAllTimeOut();
    }

    public boolean isCreate() {
        return !(!isCreate || (getActivity() == null || getActivity().isFinishing()));
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
