package xfkj.fitpro.utils;

import static com.legend.bluetooth.fitprolib.bluetooth.SendData.returnBeforeValue;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import xfkj.fitpro.R;

public class LoadingDailog extends Dialog{


    public LoadingDailog(Context context) {
        super(context);
    }

    public LoadingDailog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder{

        private Context context;
        private String message="";
        private boolean isCancelable=false;
        private boolean isCancelOutside=false;


        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置提示信息
         * @param message
         * @return
         */

        public Builder setMessage(String message){
            this.message=message;
            return this;
        }


        /**
         * 设置是否可以按返回键取消
         * @param isCancelable
         * @return
         */

        public Builder setCancelable(boolean isCancelable){
            this.isCancelable=isCancelable;
            return this;
        }

        /**
         * 设置是否可以取消
         * @param isCancelOutside
         * @return
         */
        public Builder setCancelOutside(boolean isCancelOutside){
            this.isCancelOutside=isCancelOutside;
            return this;
        }

        public LoadingDailog create(boolean isShow, int showTime){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.dialog_loading,null);
            final LoadingDailog loadingDailog=new LoadingDailog(context, R.style.MyDialogStyle);
            TextView msgText= (TextView) view.findViewById(R.id.tipTextView);
            if(message != "" && message != null){
                msgText.setText(message);
            }else{
                msgText.setVisibility(View.GONE);
            }
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            if(isShow){
                loadingDailog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!loadingDailog.isShowing()){
                            return;
                        }
                        loadingDailog.dismiss();
                        String key = SaveKeyValues.getStringValues("p_keys","none");
                        if (!key.equals("none")){
                            Toast toast = Toast.makeText(context, context.getString(R.string.timeout_txt), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        returnBeforeValue();
                    }
                },showTime);
            }
            return  loadingDailog;
        }
/*
*
*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.style1_btn:
                LoadingDialog.Builder builder1=new LoadingDialog.Builder(MainActivity.this)
                        .setMessage("加载中...")
                        .setCancelable(false);
                final LoadingDialog dialog1=builder1.create();
                dialog1.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },2000);
                break;
            case R.id.style2_btn:
                LoadingDialog.Builder builder2=new LoadingDialog.Builder(MainActivity.this)
                        .setShowMessage(false)
                        .setCancelable(false);
                final LoadingDialog dialog2=builder2.create();
                dialog2.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog2.dismiss();
                    }
                },2000);
                break;
        }
    }
*
* */

    }
}
