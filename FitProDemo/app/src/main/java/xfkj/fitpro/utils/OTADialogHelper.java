package xfkj.fitpro.utils;

import android.app.Activity;
import android.content.Context;

import xfkj.fitpro.R;

/**
 * dialog助手
 * Created by gaohui.you on 2019/6/6 0006
 * Email:839939978@qq.com
 */
public class OTADialogHelper {

    private static LoadingDailog dialog;
    
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

    public static void showLoadDialog(Context context,int loadTime) {
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
            dialog = mBuilder.create(true, showTime);
            dialog.show();
        }
    }

    /**
     * 显示状态
     *
     * @return
     */
    public static boolean isShown() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * 隐藏dialog
     */
    public static void hideDialog() {
        if (isShown()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
