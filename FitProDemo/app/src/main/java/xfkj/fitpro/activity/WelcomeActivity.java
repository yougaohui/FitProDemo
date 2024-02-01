package xfkj.fitpro.activity;

import static xfkj.fitpro.application.MyApplication.setWindowStatusBarColor;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import xfkj.fitpro.R;
import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.db.SqliteDBAcces;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WelcomeActivity extends BaseActivity {

    //TAG
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                String address = SaveKeyValues.getStringValues("bluetooth_address", "");
                if (!address.equals("")) {
                    startActivity(new Intent(WelcomeActivity.this, MenusActivity.class));
                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
                finish();
            }
            return false;
        }
    });

    @Override
    protected void setActivityTitle() {

    }

    @Override
    protected void getLayoutToView() {
        setWindowStatusBarColor(this, getResources().getColor(R.color.white));
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void initValues() {

    }


    @Override
    protected void initViews() {
    }

    @Override
    protected void setViewsListener() {

    }

    @Override
    protected void setViewsFunction() {
        //生成数据库
        OpenDataBase();
        handler.sendEmptyMessageDelayed(1, 2000);
        //启动短信服务
        if (SDKTools.mService != null) {
            SDKTools.mService.startSmsService();
        }
    }

    /**
     * 打开数据库
     */
    private void OpenDataBase() {
        //生成数据库
        SqliteDBAcces DBAccess;
        try {
            SQLiteDatabase db = this.openOrCreateDatabase(SDKTools.DBNAME, Context.MODE_PRIVATE, null);
            if (db == null) {
                Toast.makeText(this, "异常1", Toast.LENGTH_SHORT).show();
                return;
            }
            DBAccess = new SqliteDBAcces(db);
            MyApplication.DBAcces = DBAccess;
        } catch (Exception e) {
            Toast.makeText(this, "异常2：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
