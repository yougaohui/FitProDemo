package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.application.FitProSDK.getContext;
import static xfkj.fitpro.application.MyApplication.getRequset;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.utils.LoadingDailog;
import xfkj.fitpro.utils.qrcode.QRCodeUtil;

public class WxSportActivity extends BaseActivity {

    private ImageView mImageView;
    private String addr;
    private LinearLayout qrcodeBox;

    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(getString(R.string.wx_sport), WxSportActivity.this);
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_wx_sport);
    }

    @Override
    protected void initValues() {
        addr = SaveKeyValues.getStringValues("bluetooth_address", "");
    }

    @Override
    protected void initViews() {
        mImageView = findViewById(R.id.wxqrcode);
        qrcodeBox = findViewById(R.id.wxqrcodebox);
    }

    @Override
    protected void setViewsListener() {

    }

    public void  getQrcode(){
        String url = SDKTools.wx_sport_url + "?dtype=getbind&addr=" + addr;
        String res = getRequset(url);
        Logdebug("wxSport", res);
        try {
            if(Constants.dialog != null){
                SDKTools.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Constants.dialog.dismiss();
                        qrcodeBox.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
            JSONObject obj = new JSONObject(res);
            if (obj.getInt("errcode") == 0 && obj.getString("errmsg").equals("ok")) {
                final JSONObject data = obj.getJSONObject("data");
                if(data != null){
                    String ticket = data.getString("ticket");
                    if(!ticket.equals("")){
                        Bitmap logo = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.xfkj);
                        Bitmap qrcode = QRCodeUtil.createQRCodeBitmap(ticket, 400,logo,0.2f);
                        mImageView.setImageBitmap(qrcode);
                        saveImage(qrcode, SDKTools.RootPath+"/qrcode.JPEG");
                    }
                }
            }else{
                Toast toast = Toast.makeText(WxSportActivity.this, obj.getString("errmsg"), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //保存图片到本地路径
    public File saveImage(Bitmap bmp, String fileName) {
        File file = new File(fileName);
        if (file.exists())
        {
            file.delete();
            //https://www.cnblogs.com/plokmju/p/android_mediastore.html
        }
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //对相册进行刷新
        // 把刚保存的图片文件插入到系统相册
        try
        {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), "qrcode.JPEG", null);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        SDKTools.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(WxSportActivity.this, getString(R.string.wx_save_tips), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }, 3000);

        return file;
    }

    @Override
    protected void setViewsFunction() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(WxSportActivity.this)
                .setMessage(getString(R.string.ws_wait_qrcode))
                .setCancelable(true);
        Constants.dialog = mBuilder.create(false, 0);
        Constants.dialog.show();
        getQrcode();
    }
}
