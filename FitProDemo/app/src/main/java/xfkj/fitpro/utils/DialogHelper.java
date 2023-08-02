package xfkj.fitpro.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ScreenUtils;

import xfkj.fitpro.Constants;
import xfkj.fitpro.R;

/**
 * dialog助手
 * Created by gaohui.you on 2019/6/6 0006
 * Email:839939978@qq.com
 */
public class DialogHelper {

    public static void showDialog(Context context, int resid, int showTime, boolean isCancel) {
        if (context != null) {
            showDialog(context, context.getString(resid), showTime, isCancel);
        }
    }

    public static void showDialog(Context context, int resid, boolean isCancel) {
        if (context != null) {
            showDialog(context, context.getString(resid), 8000, isCancel);
        }
    }

    public static void showDialog(Context context, String message, boolean isCancel) {
        if (context != null) {
            showDialog(context, message, 8000, isCancel);
        }
    }

    public static void showDialog(Context context, int resid) {
        if (context != null) {
            showDialog(context, context.getString(resid), 30 * 1000, false);
        }
    }

    public static void showDialog(Context context, String message) {
        if (context != null) {
            showDialog(context, message, 30 * 1000, false);
        }
    }

    public static void showLoadDialog(Context context) {
        if (context != null) {
            showDialog(context, context.getString(R.string.loadding_data), 30 * 1000, false);
        }
    }

    public static void showLoadDialog(Context context, int loadTime) {
        if (context != null) {
            showDialog(context, context.getString(R.string.loadding_data), loadTime, false);
        }
    }

    /**
     * 显示dialog
     *
     * @param context
     * @param message
     */
    public static void showDialog(Context context, String message, int showTime, boolean isCancel) {
        if ((context instanceof Activity) && !((Activity) context).isDestroyed()) {
            hideDialog();
            LoadingDailog.Builder mBuilder = new LoadingDailog.Builder(context)
                    .setMessage(message)
                    .setCancelable(isCancel);
            Constants.dialog = mBuilder.create(true, showTime);
            Constants.dialog.show();
        }
    }

    /**
     * 显示状态
     *
     * @return
     */
    public static boolean isShown() {
        if (Constants.dialog != null && Constants.dialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * 隐藏dialog
     */
    public static void hideDialog() {
        if (isShown()) {
            Constants.dialog.dismiss();
            Constants.dialog = null;
        }
    }


    /**
     * 矫正dialog显示问题
     *
     * @param dialog
     */
    public static void correctDialog(AlertDialog dialog) {
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // 设置宽度
        p.width = (int) (ScreenUtils.getScreenWidth() * 0.95); // 宽度设置为屏幕的0.95
        p.gravity = Gravity.CENTER;//设置位置
        dialogWindow.setAttributes(p);
    }
}
