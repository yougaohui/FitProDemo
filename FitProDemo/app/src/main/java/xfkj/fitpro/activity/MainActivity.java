package xfkj.fitpro.activity;

import static xfkj.fitpro.service.NotifyService.showNotifyPermissionDialog;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
    private LinearLayout sexbox, agebox, weightbox, heightbox, stepbox;//布局
    private TextView sex, age, weight, height, step;//生日、身高
    private Button next_one, next_two;//下一步

    /**
     * 初始化窗口
     */
    @Override
    protected void getLayoutToView()
    {
        setContentView(R.layout.activity_main);
    }

    /**
     * 初始化标题
     */
    @Override
    protected void setActivityTitle()
    {
        initTitle();
        setTitle(getString(R.string.app_name));
        setTitleTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 设置初始化的值和变量
     */
    @Override
    protected void initValues()
    {
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews()
    {
        //======================================= 完善资料1/2 =======================================
        sexbox = findViewById(R.id.sexbox);
        agebox = findViewById(R.id.agebox);
        weightbox = findViewById(R.id.weightbox);
        heightbox = findViewById(R.id.heightbox);
        stepbox = findViewById(R.id.stepbox);
        sex = findViewById(R.id.sex);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        step = findViewById(R.id.step);
        next_one = findViewById(R.id.next_one);
        next_two = findViewById(R.id.next_two);

        setValues();

        viewDrawables(age);
        viewDrawables(sex);
        viewDrawables(height);
        viewDrawables(weight);
        viewDrawables(step);
    }

    public void viewDrawables(TextView view)
    {
        Drawable rights=getResources().getDrawable(R.drawable.xfkj_right);
        rights.setBounds(0,0,getResources().getDimensionPixelSize(R.dimen.dp_20),getResources().getDimensionPixelSize(R.dimen.dp_20));//必须设置图片的大小否则没有作用
        view.setCompoundDrawables(null,null ,rights,null);
    }

    private void setValues()
    {
        age.setText(SaveKeyValues.getIntValues("age", 25) + " "+getString(R.string.age_unit));
        sex.setText(SaveKeyValues.getIntValues("gender", 1) == 0 ? getString(R.string.girl) : getString(R.string.boy));
        height.setText(SaveKeyValues.getIntValues("height", 170) + " cm");
        weight.setText(SaveKeyValues.getIntValues("weight", 65) + " kg");
        step.setText(SaveKeyValues.getIntValues("step", 5000) + " "+getString(R.string.step));
    }

    /**
     * 初始化控件的监听
     */
    @Override
    protected void setViewsListener()
    {
        sexbox.setOnClickListener(this);
        agebox.setOnClickListener(this);
        weightbox.setOnClickListener(this);
        heightbox.setOnClickListener(this);
        stepbox.setOnClickListener(this);
        next_one.setOnClickListener(this);
        next_two.setOnClickListener(this);
    }

    /**
     * 设置相关管功能
     */
    @Override
    protected void setViewsFunction()
    {
        showNotifyPermissionDialog(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK){
            setValues();
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        int vid = v.getId();
        Integer val = 0;
        String[] vals;
        int mType = 0;
        Intent intent = new Intent();
        if (vid == R.id.agebox || vid == R.id.sexbox || vid == R.id.weightbox || vid == R.id.heightbox || vid == R.id.stepbox)
        {
            if (vid == R.id.agebox)
            {
                vals = age.getText().toString().split("\\s+");
                val = Integer.parseInt(vals[0]);
                mType = R.string.age;
            } else if (vid == R.id.sexbox)
            {
                String gender = sex.getText().toString();
                if (gender.equals(this.getString(R.string.boy)) == true)
                {
                    val = 1;
                } else
                {
                    val = 0;
                }
                mType = R.string.sex;
            } else if (vid == R.id.weightbox)
            {
                vals = weight.getText().toString().split("\\s+");
                val = Integer.parseInt(vals[0]);
                mType = R.string.weight;
            } else if (vid == R.id.heightbox)
            {
                vals = height.getText().toString().split("\\s+");
                val = Integer.parseInt(vals[0]);
                mType = R.string.height;
            } else if (vid == R.id.stepbox)
            {
                vals = step.getText().toString().split("\\s+");
                val = Integer.parseInt(vals[0]);
                mType = R.string.target_txt;
            }

            intent.setClass(this, SetInfoActivity.class);
            intent.putExtra("type", mType);
            intent.putExtra("value", val);
			intent.putExtra("showbtn", 0);

            startActivityForResult(intent, 1);
        } else
        {
            boolean is_Connected = vid == R.id.next_one ? false : true;
            if(is_Connected){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    final String[] permission = new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN};
                    if(!PermissionUtils.isGranted(permission)){
                        PermissionUtils.permission(permission).callback(new PermissionUtils.SimpleCallback() {
                            @Override
                            public void onGranted() {
                                ActivityUtils.startActivity(MiBandReaderActivity.class);
                            }

                            @Override
                            public void onDenied() {
                                ToastUtils.showShort("请通过蓝牙权限再试");
                            }
                        }).request();
                        return;
                    }
                    intent.setClass(this, MiBandReaderActivity.class);
                }else {
                    intent.setClass(this, MiBandReaderActivity.class);
                }
            }else{
                intent.setClass(this, MenusActivity.class);
            }
            startActivity(intent);
        }

    }

}
