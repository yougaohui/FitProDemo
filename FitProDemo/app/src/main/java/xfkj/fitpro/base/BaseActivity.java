package xfkj.fitpro.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import xfkj.fitpro.R;
import xfkj.fitpro.activity.WelcomeActivity;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.utils.PermissionUtil;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static xfkj.fitpro.application.MyApplication.clearChatMsg;

public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{

    private TextView title_center;//标题的中间部分
    private ImageView title_left, title_right;//标题的左边和右边
    private RelativeLayout title_relRelativeLayout;
    private MyApplication application;
    private PermissionUtil mPermission;

    /**
     * 当前的Activity
     */
    public static AppCompatActivity CurrentActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        if (application == null)
        {
            // 得到Application对象
            application = MyApplication.getContext();
        }
        application.addActivity_(this);
        // 调用添加方法
        mPermission = PermissionUtil.getInstance();
        mPermission.init(this);
        String activityName = this.getClass().getSimpleName();
        String[] needPermissions = mPermission.getNeedPermissions(activityName);
        if (needPermissions != null && needPermissions.length > 0)
        {
            boolean pass = mPermission.checkPermissions(needPermissions);
            if (pass)
            {
                DoWork();
            }
        } else
        {
            DoWork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] paramArrayOfInt)
    {
        String activityName = this.getClass().getSimpleName();
        Logdebug("activity", "onRequestPermissionsResult-------false------" + activityName + "----requestCode--" + requestCode + "---" + mPermission.getRequestCode());
        if(requestCode == 10101){
            if (!(paramArrayOfInt.length > 0 && paramArrayOfInt[0] == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, 2);
            }
        }else{
            if (requestCode == mPermission.getRequestCode())
            {
                if (!mPermission.verifyPermissions(paramArrayOfInt))
                {
                    application.removeActivity_(this);
                    if (this instanceof WelcomeActivity)
                    {
                        Process.killProcess(Process.myPid()); //杀死当前进程
                    }
                } else
                {
                    DoWork();
                }
            }
        }
    }


    private void DoWork()
    {
        getLayoutToView();
        initValues();
        setActivityTitle();
        initViews();
        setViewsListener();
        setViewsFunction();
    }


    @Override
    protected void onStart()
    {
        CurrentActivity = this;
        super.onStart();
    }

    /**
     * 初始化标题
     */
    public void initTitle()
    {
        title_center = (TextView) findViewById(R.id.titles);
        title_left = (ImageView) findViewById(R.id.left_btn);
        title_right = (ImageView) findViewById(R.id.right_btn);
        title_relRelativeLayout = (RelativeLayout) findViewById(R.id.title_back);
    }

    public void setMyBackGround(int color)
    {
        title_relRelativeLayout.setBackgroundResource(color);
    }

    /**
     * 设置TextView的下滑线
     *
     * @param view
     */
    public void setTextViewUnderLine(TextView view)
    {
        Paint paint = view.getPaint();
        paint.setColor(getResources().getColor(R.color.btn_gray));//设置画笔颜色
        paint.setAntiAlias(true);//设置抗锯齿
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下滑线
        view.invalidate();
    }

    /**
     * 初始化标题
     */
    protected abstract void setActivityTitle();

    /**
     * 初始化窗口
     */
    protected abstract void getLayoutToView();

    /**
     * 设置初始化的值和变量
     */
    protected abstract void initValues();

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 初始化控件的监听
     */
    protected abstract void setViewsListener();

    /**
     * 设置相关管功能
     */
    protected abstract void setViewsFunction();

    /**
     * 设置标题的名称
     *
     * @param name
     */
    public void setTitle(String name)
    {
        title_relRelativeLayout.setVisibility(View.VISIBLE);
        title_center.setText(name);
        title_left.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置标题有返回键功能-->可以改变返回键的图片
     *
     * @param name
     * @param activity
     */
    public void setTitle(String name, final Activity activity)
    {
        title_relRelativeLayout.setVisibility(View.VISIBLE);
        title_center.setText(name);
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                application.removeActivity_(activity);
            }
        });
    }

    public void hideTitle()
    {
        title_relRelativeLayout.setVisibility(View.GONE);
    }

    /**
     * 获取标题左边的按钮
     *
     * @param name
     * @return
     */
    public ImageView setTitleLeft(String name)
    {
        title_relRelativeLayout.setVisibility(View.VISIBLE);
        title_center.setText(name);
        title_left.setVisibility(View.VISIBLE);
        return title_left;
    }

    /**
     * 设置标题左 中 右 全部显示
     *
     * @param name
     * @param activity
     * @param picID
     */
    public ImageView setTitle(String name, final Activity activity, int picID)
    {
        title_relRelativeLayout.setVisibility(View.VISIBLE);
        title_center.setText(name);
        title_left.setVisibility(View.VISIBLE);
        title_right.setVisibility(View.VISIBLE);
        if (picID != 0)
        {
            title_right.setImageResource(picID);
        }
        title_left.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                application.removeActivity_(activity);
            }
        });
        return title_right;
    }

    /**
     * 设置标题的文字颜色
     *
     * @param colorID
     */
    public void setTitleTextColor(int colorID)
    {
        title_center.setTextColor(colorID);
    }

    /**
     * 设置标题左侧图片按钮的图片
     *
     * @param picID
     */
    public void setTitleLeftImage(int picID)
    {
        title_left.setImageResource(picID);
    }

    /**
     * 设置标题右侧图片按钮的图片
     *
     * @param picID
     */
    public void setTitleRightImage(int picID)
    {
        title_right.setImageResource(picID);
    }

    @Override
    protected void onResume()
    {
        //    checkPermissions(needPermissions);
        if (SDKTools.mediaPlayer != null && SDKTools.mediaPlayer.isPlaying())
        {
            SDKTools.mediaPlayer.pause();
            SDKTools.mediaPlayer.pause();
        }
        clearChatMsg(-1);//清除所有通知
        BleManager.getInstance().getConnetedBleState();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            application.removeActivity_(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if (application != null)
        {
            application.removeActivity_(this);
        }
    }

}
