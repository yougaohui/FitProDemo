package xfkj.fitpro.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.activity.WelcomeActivity;

public class PermissionUtil {
    private static final int mRequestCode = 10010;//权限请求码
    private static Activity context = null;
    private static Map<String, String[]> reqPermission;

    private static PermissionUtil permissionUtil;

    public static PermissionUtil getInstance() {
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil();
        }
        reqPermission = new HashMap<>();
        return permissionUtil;
    }

    public void init(@NonNull Activity context) {
        // 根据Android版本动态配置权限

        String[] storagePermissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android 13+ 使用新的媒体权限
            storagePermissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO};
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //Android 10-12 只需要读权限
            storagePermissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,};
        } else {
            //Android 9及以下需要读写权限
            storagePermissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
        }
        String[] WelcomeActivity = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG
        };
        //        String[] WelcomeActivity = {Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.PROCESS_OUTGOING_CALLS,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.READ_CALL_LOG};

        //合并存储权限+
        String[] mergeWelcomePermissions = new String[WelcomeActivity.length + storagePermissions.length];
        //合并权限 长度为WelcomeActivity.length
        System.arraycopy(WelcomeActivity, 0, mergeWelcomePermissions, 0, WelcomeActivity.length);
        //合并权限 长度为storagePermissions.length
        System.arraycopy(storagePermissions, 0, mergeWelcomePermissions, WelcomeActivity.length, storagePermissions.length);
        reqPermission.put("WelcomeActivity", mergeWelcomePermissions);
//        reqPermission.put("WelcomeActivity", WelcomeActivity);

        String[] MiBandReaderActivity = {Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION};
        reqPermission.put("MiBandReaderActivity", MiBandReaderActivity);

        String[] UpgradeActivity = {Manifest.permission.ACCESS_COARSE_LOCATION};
        reqPermission.put("UpgradeActivity", UpgradeActivity);

        String[] CameraActivity = {Manifest.permission.CAMERA};
        reqPermission.put("CameraActivity", CameraActivity);
        this.context = context;
    }

    public String[] getNeedPermissions(String activityName) {
        return reqPermission.get(activityName);
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    /**
     * 检测权限数组
     *
     * @param permissions 权限列
     * @since 2.5.0
     */
    public static boolean checkPermissions(String... permissions) {
        //判断Android版本是否大于23
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> needRequestPermissonList = findDeniedPermissions(permissions);
//            if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
//                ActivityCompat.requestPermissions(context, needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]), mRequestCode);
//                return false;
//            }
//        }

        if (context == null) return false;
        List<String> needRequestPermissonsList = findDeniedPermissions(permissions);
        //没有权限需要申请
        if (!needRequestPermissonsList.isEmpty()){
            ActivityCompat.requestPermissions(
                    context,
                    needRequestPermissonsList.toArray(new String[0]),
                    mRequestCode
            );
            return false;
        }
        return true;
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions 权限列
     * @return 全向列表
     * @since 2.5.0
     */
    private static List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonsList = new ArrayList<>();
        for (String perm : permissions) {
            //过滤掉在特定版本不需要的权限
            if (shouldSkipPermission(perm)) continue;

            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonsList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, perm)) {
                    needRequestPermissonsList.add(perm);
                }
            }
        }
        return needRequestPermissonsList;
    }

    /**
     * 检查是否需要跳过某些权限请求
     */
    private static boolean shouldSkipPermission(String permission) {
        // Android 13+ 跳过 WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults 权限结果
     * @return 是否授权
     * @since 2.5.0
     */
    public boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    public void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("设置权限信息");
        builder.setMessage("是否设置？");
        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                });
        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(intent, mRequestCode);
    }


}