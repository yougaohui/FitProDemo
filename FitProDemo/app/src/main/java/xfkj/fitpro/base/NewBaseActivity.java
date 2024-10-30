package xfkj.fitpro.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import xfkj.fitpro.utils.EventBusUtils;


/**
 * 新的Activity基类
 * Created by gaohui.you on 2019/6/5 0005
 * Email:839939978@qq.com
 */
public abstract class NewBaseActivity<T extends ViewBinding> extends AppCompatActivity  {
    protected String TAG = (this == null ? "debug" : this.getClass().getSimpleName());
    protected boolean isActive = true; //是否活跃

    private InputMethodManager imm;
    private Toast mToast = null;

    protected ProgressDialog mProgressDialog;

    protected InputMethodManager inputMethodManager;

    protected Context mContext;

    private boolean isImmersionBar = false;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMsg(msg);
        }
    };

    public T binding;

    protected void handleMsg(Message msg) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        mContext = this;
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            try {
                Class<T> clazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
                //反射
                Method method = clazz.getMethod("inflate", LayoutInflater.class);
                binding = (T) method.invoke(null, getLayoutInflater());
            } catch (Exception e) {
                e.printStackTrace();
            }
            setContentView(binding.getRoot());
        } else {
            setContentView(getLayoutId());
        }

        setTitle("");
        initData(savedInstanceState);
        initListener();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public int getLayoutId() {
        return 0;
    }

    public abstract void initData(Bundle savedInstanceState);

    public abstract void initListener();


    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isRegisterEventBus()) {
            EventBusUtils.register(this);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
        mProgressDialog = null;
        if (!AppUtils.isAppForeground()) {
            //app 进入后台
            //全局变量 记录当前已经进入后台
            isActive = false;
            LogUtils.i(TAG, "进入后台");
        }
        if (isRegisterEventBus()) {
            EventBusUtils.unregister(this);
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public void onResume() {
        super.onResume();
        if (mToast == null)
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            LogUtils.i(TAG, "进入前台");
        }
    }


    @Override
    public void onPause() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        hideSoftKeyBoard();
        super.onPause();
    }


    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return isImmersionBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    protected boolean isRegisterEventBus() {
        return true;
    }

    /**
     * 事件广播入口
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {/* Do something */}
}
