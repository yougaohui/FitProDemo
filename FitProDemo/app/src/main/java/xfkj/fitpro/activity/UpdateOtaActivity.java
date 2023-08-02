package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_DATA_AVAILABLE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_GATT_CHARACTER_NOTIFY;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_GATT_CONNECTED;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_GATT_DISCONNECTED;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_GATT_SERVICES_DISCOVERED;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ACTION_GATT_STATUS_133;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ARRAY_BYTE_DATA;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.BluetoothGattServices;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.CHARAC_CHANGED;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.EXTRA_DATA;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG1_BLE_ERROR;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG1_NO_FILE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_BURN_APP_SUCCESS;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_BURN_CFG_SUCCESS;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_BURN_PATCH_SUCCESS;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_DISCONNECT_BLE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_FLASH_EMPTY;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_HANDS_UP_FAILED;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_OTA_COMPLETE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.MSG_OTA_RESEPONSE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.OTA_REPLY_ACK_CMD;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.OTA_RX_CMD_ACTION;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.OTA_RX_DAT_ACTION;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.OTA_RX_ISP_CMD_ACTION;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.SendFileRseponse;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.bleManager;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.do_work_on_boads;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.initalerpaly;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.mBLE;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.mConnected;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.makeGattUpdateIntentFilter;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ota_data_cmd_charac;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ota_rx_cmd_charac;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ota_rx_dat_charac;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ota_tx_cmd_charac;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.ota_tx_dat_charac;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_rx_cmd_uuid;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_rx_dat_uuid;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_rx_ips_cmd_uuid;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_tx_cmd_uuid;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_tx_dat_uuid;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.otas_tx_ips_cmd_uuid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.example.otalib.boads.Utils;
import com.example.otalib.boads.WorkOnBoads;
import com.legend.bluetooth.fitprolib.api.DownloadMannager;
import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.bluetooth.BluetoothLeService;
import com.legend.bluetooth.fitprolib.bluetooth.ByteUtil;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SendData;
import com.legend.bluetooth.fitprolib.utils.BleUtils;
import com.legend.bluetooth.fitprolib.utils.CountDownTimerUtils;
import com.legend.bluetooth.fitprolib.utils.DeleteFileUtil;
import com.legend.bluetooth.fitprolib.utils.NumberUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import xfkj.fitpro.R;
import xfkj.fitpro.api.NetAPITools;
import xfkj.fitpro.base.BaseActivity;
import xfkj.fitpro.model.OTAUpgradeInfo;
import xfkj.fitpro.utils.OTADialogHelper;
import xfkj.fitpro.utils.PathUtils;

//这个OTA仅仅针对汉天下平台进行升级，其他平台的OTA请联系开发人员
public class UpdateOtaActivity extends BaseActivity {

    private static final String TAG = UpdateOtaActivity.class.getSimpleName();
    private String Version = "";
    private TextView tv_versionName;
    private Button checkVersion;
    private static Thread mTransThread = null;
    private static String base_path = PathUtils.getOTADir();
    private static String user_path = "";
    private static String app_path ="";
    private static String cfg_path = "";
    private static String patch_path = "";
    private boolean isUpdate = false;
    private static Context context;
    private static ProgressBar pb;
    private static TextView mTvProgress;

    private DownloadMannager mDownloadMannager;
    private static CountDownTimerUtils mCountDownTimer;


    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle("OTA", UpdateOtaActivity.this);
        context = UpdateOtaActivity.this;
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_update_ota);
    }

    @Override
    protected void initValues() {
        mDownloadMannager = new DownloadMannager();
        mCountDownTimer = CountDownTimerUtils.getCountDownTimer();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        PermissionUtils.permission(PermissionConstants.LOCATION).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                Version = SaveKeyValues.getStringValues("deviceVersion", "");
                bleManager = BleManager.getInstance();
                Intent i = new Intent(UpdateOtaActivity.this, BluetoothLeService.class);
                startService(i);
                boolean res = bindService(i, mServiceConnection, BIND_AUTO_CREATE);
                Logdebug("DEBUG_OTA", "bindService---startService---:" + res);
                initalerpaly();

                if (isLocServiceEnable(UpdateOtaActivity.this)) {
                    checkUpgrade();
                }
            }

            @Override
            public void onDenied() {
                ToastUtils.showLong("权限被拒绝");
                ActivityUtils.startActivity(IntentUtils.getLaunchAppDetailsSettingsIntent(AppUtils.getAppPackageName()));
                onBackPressed();
            }
        }).request();
    }


    @Override
    protected void initViews() {
        tv_versionName = findViewById(R.id.tv_versionName);
        mTvProgress = findViewById(R.id.tv_progress);
        checkVersion = findViewById(R.id.checkVersion);
        tv_versionName.setText(getString(R.string.device_version_txt)+":"+Version);
        do_work_on_boads = new WorkOnBoads(UpdateOtaActivity.this, handler);
        pb = findViewById(R.id.progressBar);
    }

    /**
     * 检测更新
     */
    private void checkUpgrade() {
        OTADialogHelper.showDialog(this,getString(R.string.check_upgrade));
        NetAPITools.getInstance().getOTAUpgradeInfo(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog("checkUpgrade onFailure");
                ToastUtils.showShort(e.toString());
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) {
                hideDialog("checkUpgrade onResponse");
                OTAUpgradeInfo data = parseBody(response);
                if (data != null) {
                    if (data.isSuccess()) {
                        OTAUpgradeInfo.DataBean info = data.getData();
                        if (null != info) {
                            ViewUtils.runOnUiThread(() -> showDownloadDialog(info));
                        } else {
                            ToastUtils.showShort(R.string.last_version_not_upgrade);
                            finish();
                        }
                    } else {
                        ToastUtils.showShort(R.string.loading_failed);
                        finish();
                    }
                } else {
                    ToastUtils.showShort(R.string.loading_failed);
                    finish();
                }
            }
        });
    }

    @Override
    protected void setViewsListener() {
        mDownloadMannager.setDownLoadListener(new DownloadMannager.DownLoadListener() {
            @Override
            public void onStartDownload(String tag) {
                OTADialogHelper.showLoadDialog(context);
            }

            @Override
            public void onSuccess(String filePath, String tag) {
                hideDialog("setViewsListener onSuccess");
                try {
                    ZipUtils.unzipFile(filePath, PathUtils.getOTADir());
                    FileUtils.delete(filePath);
                    matchName();

                    List<File> files = FileUtils.listFilesInDir(PathUtils.getOTADir());
                    for (File file : files) {
                        Log.e(TAG, file.getAbsolutePath() + ";md5:" + ConvertUtils.bytes2HexString(FileUtils.getFileMD5(file)));
                    }

                    String tmpAddress = FileUtils.getFileNameNoExtension(user_path);
                    if (StringUtils.length(tmpAddress) > 5) {
                        String addr = tmpAddress.substring(5);
                        Log.e(TAG, "address:" + addr);
                        if (isOutMaxMemory(FileUtils.getLength(base_path + user_path), addr)) {
                            showOTAWarnDialog();
                            return;
                        }
                    }
                    startMatchDevice();
                } catch (IOException e) {
                    ToastUtils.showLong(e.toString());
                    finish();
                }
            }

            @Override
            public void onFailed(String info, String tag) {
                hideDialog("setViewsListener onFailed");
                ToastUtils.showShort(info);
                finish();
            }
        });

        mCountDownTimer.setMillisInFuture(60 * 1000);
        mCountDownTimer.setFinishDelegate(() -> {
            ToastUtils.showLong(R.string.upgrade_ota_tips);
            hideDialog("mCountDownTimer");
            finish();
        });
    }

    /**
     * 开始匹配设备
     */
    private void startMatchDevice() {
        SDKTools.otaState = 1;
        SDKTools.waiting = 5;
        if (SDKTools.mService != null) {
            SDKTools.mService.close();
            SDKTools.BleState = 0;
        }
        ViewUtils.runOnUiThread(() -> {
            OTADialogHelper.showDialog(context, getString(R.string.matching_device), 60 * 1000, false);
            mCountDownTimer.start();
            startOtaUpdate();
        });
    }

    private void matchName() {
        List<File> files = FileUtils.listFilesInDir(base_path);
        for (File file : files) {
            String name = file.getName();
            if (name.contains("app")) {
                app_path = name;
            } else if (name.contains("cfg")) {
                cfg_path = name;
            } else if (name.contains("patch")) {
                patch_path = name;
            } else if (name.contains("ui")) {
                user_path = name;
            }
        }
    }

    private OTAUpgradeInfo parseBody(Response response) {
        try {
            ResponseBody body = response.body();
            OTAUpgradeInfo data = OTAUpgradeInfo.objectFromData(body.string());
            body.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showDownloadDialog(OTAUpgradeInfo.DataBean info)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateOtaActivity.this);
        dialog.setTitle(R.string.upgrade_txt);
        dialog.setMessage(R.string.ota_upgrade_message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.update, (dialogInterface, i) -> mDownloadMannager.startDownLoad(info.getApp_down_url(), PathUtils.getOTADir() + File.separator + info.getDisplay_name() + ".zip"));
        dialog.setNegativeButton(R.string.cancel, (dialog1, i) -> {
            dialog1.dismiss();
            checkVersion.setEnabled(true);
            finish();
        });
        dialog.show();
    }


    @Override
    protected void setViewsFunction() {

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBLE = ((BluetoothLeService.LocalBinder) service).getService();
            Logdebug(TAG, "in onServiceConnected!!!--------------");
            if (!mBLE.initialize()) {
                Logdebug(TAG, "Unable to initialize Bluetooth---------------");
                (UpdateOtaActivity.this).finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBLE = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals("find")){
                Map<String, Object> map = (Map<String, Object>) intent.getExtras().getSerializable("Datas");
                BluetoothDevice device = (BluetoothDevice) map.get("device");
                final String deviceAddr = device.getAddress();
                String address = SaveKeyValues.getStringValues("bluetooth_address", "");
                String ota_addr = SaveKeyValues.getStringValues("ota_address", "");
                Logdebug(TAG, "---device---"+device.getName()+"---"+device.getAddress()+"---address---"+address+"---ota_addr---"+ota_addr);
                if ((address.equals(deviceAddr) || ota_addr.equals(deviceAddr) )&& mBLE != null && SDKTools.waiting == 0)
                {
                    bleManager.scanLeDevice(false);
                    handler.postDelayed(new Runnable() {//延迟连接
                        @Override
                        public void run() {
                            boolean res = mBLE.connect(deviceAddr);
                            if(res == false){
                                bleManager.scanLeDevice(true);
                            }
                        }
                    }, 3000);
                    return;
                }
            }else if (ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                setOTAProgress(0);
            } else if (ACTION_GATT_STATUS_133.equals(action)) {
                bleManager.scanLeDevice(true);
                Logdebug(TAG, "Bluetooth connection status is 133,reset the bluetooth now,please wait");
                mBLE.close();
            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothGattServices = mBLE.getSupportedGattServices();
                if (BluetoothGattServices == null) return;
                final Message msg = Message.obtain();
                for (BluetoothGattService gattService : BluetoothGattServices) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        if (Profile.uartWriteCharacteristicUUID.toString().equals(gattCharacteristic.getUuid().toString())) {
                            ota_data_cmd_charac = gattCharacteristic;
                            continue;
                        } else if (gattCharacteristic.getUuid().equals(Profile.uartNotifyCharacteristicUUID)) {
                            handler.postDelayed(() -> {
                                boolean res = mBLE.setCharacNotification(gattCharacteristic, true);
                                if (res) {
                                    Log.e(TAG, "开启通知成功");
                                    //获取升级地址
                                    byte[] data = SendData.getSetInfoByKey((byte) 0xe);
                                    Message message = Message.obtain();
                                    message.arg2 = data.length;
                                    message.obj = data;
                                    sendDataToBle(message,ota_data_cmd_charac);
                                } else {
                                    Log.e(TAG, "开启通知失败");
                                    msg.arg1 = MSG1_BLE_ERROR;
                                    handler.sendMessage(msg);
                                    return;
                                }
                            }, 1000);
                        } else if (otas_tx_dat_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_tx_dat_charac = gattCharacteristic;
                            isUpdate = true;
                            if(isUpdate){
                                handler.postDelayed(() -> startDownload(), 1000);
                            }
                        } else if (otas_rx_dat_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_rx_dat_charac = gattCharacteristic;
                            if ((ota_rx_dat_charac.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean res = mBLE.setCharacteristicNotification(gattCharacteristic, true);
                                        if (res) {
                                            Logdebug(TAG, "Set Notify Success");
                                        } else {
                                            msg.arg1 = MSG1_BLE_ERROR;
                                            handler.sendMessage(msg);
                                            return;
                                        }
                                    }
                                }, 1500);
                            }
                        } else if (otas_rx_cmd_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_rx_cmd_charac = gattCharacteristic;
                            if ((ota_rx_cmd_charac.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                boolean res = mBLE.setCharacteristicNotification(gattCharacteristic, true);
                                if (res) {
                                    Logdebug(TAG, "Set Notify Success");
                                } else {
                                    msg.arg1 = MSG1_BLE_ERROR;
                                    handler.sendMessage(msg);
                                    Logdebug(TAG, "Notify failed!!");
                                    return;
                                }
                            }
                        } else if (otas_tx_cmd_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_tx_cmd_charac = gattCharacteristic;
                        } else if (otas_tx_ips_cmd_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_tx_cmd_charac = gattCharacteristic;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    do_work_on_boads.entryIspModel(com.example.otalib.boads.Constant.MSG_ARG1_ISP_ADDR);
                                }
                            }, 1500);
                        } else if (otas_rx_ips_cmd_uuid.equals(gattCharacteristic.getUuid().toString())) {
                            ota_rx_cmd_charac = gattCharacteristic;
                            if (ota_rx_cmd_charac.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                                boolean res = mBLE.setCharacteristicNotification(gattCharacteristic, true);
                                if (res) {
                                    Logdebug(TAG, "Set Notify Success");
                                } else {
                                    msg.arg1 = MSG1_BLE_ERROR;
                                    handler.sendMessage(msg);
                                    Logdebug(TAG, "Notify failed!!");
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                intent.getStringExtra(EXTRA_DATA);
            } else if (ACTION_GATT_CHARACTER_NOTIFY.equals(action)) {
                intent.getStringExtra(EXTRA_DATA);
            } else if (OTA_RX_DAT_ACTION.equals(action)) {
                byte[] data = intent.getByteArrayExtra(ARRAY_BYTE_DATA);
                if (data != null) {
                    do_work_on_boads.setBluetoothNotifyData(data, com.example.otalib.boads.Constant.DATACHARACTER);
                }
            } else if (OTA_RX_CMD_ACTION.equals(action)) {
                byte[] data = intent.getByteArrayExtra(ARRAY_BYTE_DATA);
                if (data != null) {
                    do_work_on_boads.setBluetoothNotifyData(data, com.example.otalib.boads.Constant.CMDCHARACTER);
                }
            } else if (OTA_RX_ISP_CMD_ACTION.equals(action)) {
                byte[] data = intent.getByteArrayExtra(ARRAY_BYTE_DATA);
                Logdebug(TAG, "ISP notify:" + ByteUtil.bytesToHexString(data));
                String addr = ByteUtil.StringRevers(ByteUtil.bytesToHexString(data).replace(" ", "").substring(2));
                SaveKeyValues.putStringValues("ota_address", addr);
                do_work_on_boads.entryIspModel(com.example.otalib.boads.Constant.MSG_ARG1_ENTRY_ISP);
                SDKTools.otaState = 2;
                SDKTools.waiting = 5;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBLE != null) {
                            mBLE.disconnect();
                        }
                    }
                }, 500);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bleManager.scanLeDevice(true);
                        SDKTools.waiting = 0;
                    }
                }, 5000);
            }else if (OTA_REPLY_ACK_CMD.equals(action)) {
                byte[] data = intent.getByteArrayExtra(ARRAY_BYTE_DATA);
                Message message = Message.obtain();
                message.arg2 = data.length;
                message.obj = data;
                sendDataToBle(message,ota_data_cmd_charac);
            } else if (CHARAC_CHANGED.equals(action)) {
                Map<String, Object> map = (Map<String, Object>) intent.getExtras().getSerializable("Datas");
                Integer what = (Integer) map.get("what");
                if (what == Profile.MsgWhat.what14) {
                    byte[] data = (byte[]) map.get(ARRAY_BYTE_DATA);
                    if (data == null) {
                        Log.e(TAG, "data is null");
                        return;
                    }
                    Logdebug(TAG, "new ISP notify:" + ByteUtil.bytesToHexString(data));
                    String addr = ByteUtil.StringRevers(ByteUtil.bytesToHexString(data).replace(" ", "").substring(2));
                    SaveKeyValues.putStringValues("ota_address", addr);
                    // 请求进入ota升级模式
                    byte[] enterOtaBytes = SendData.getEnterOtaMode();
                    Message message = Message.obtain();
                    message.arg2 = enterOtaBytes.length;
                    message.obj = enterOtaBytes;
                    sendDataToBle(message,ota_data_cmd_charac);

                    SDKTools.otaState = 2;
                    SDKTools.waiting = 5;
                    handler.postDelayed(() -> {
                        if (mBLE != null) {
                            mBLE.disconnect();
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bleManager.scanLeDevice(true);
                            SDKTools.waiting = 0;
                        }
                    }, 5000);
                }
            }
        }
    };

    private static void setOTAProgress(int i) {
        pb.setProgress(i);
        mTvProgress.setText(NumberUtils.keepPrecision((pb.getProgress() / (pb.getMax() * 1.0f)) * 100, 1) + "%");
    }


    @Override
    protected void onPause() {
        super.onPause();
        do_work_on_boads.resetDevice();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mConnected && mBLE != null) {
                    mBLE.disconnect();
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy()
    {
        SDKTools.otaState = 0;
        UpdateOtaActivity.this.unregisterReceiver(mGattUpdateReceiver);
        UpdateOtaActivity.this.unbindService(mServiceConnection);
        if (mConnected) {
            mBLE.disconnect();
        }
        mBLE.close();
        Intent stopIntent = new Intent(UpdateOtaActivity.this,BluetoothLeService.class);
        stopService(stopIntent);//停止服务
        super.onDestroy();
        SDKTools.waiting = 0;
        DeleteFileUtil.deleteFile(base_path + user_path);
        DeleteFileUtil.deleteFile(base_path + app_path);
        DeleteFileUtil.deleteFile(base_path + cfg_path);
        DeleteFileUtil.deleteFile(base_path + patch_path);
        mCountDownTimer.cancel();
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null)
                return;
            Message msg2 = msg;
            if (msg2.what == com.example.otalib.boads.Constant.MSG_WHAT_READ_PART) {
                int addr = msg2.arg1;
                int length = msg2.arg2;
                switch ((Integer) msg2.obj) {
                    case com.example.otalib.boads.Constant.APPTYPE:
                        //    appaddr.setText("0x" + Integer.toHexString(addr));
                        break;
                    case com.example.otalib.boads.Constant.CONFGTYPE:
                        //    confgaddr.setText("0x" + Integer.toHexString(addr));
                        break;
                    case com.example.otalib.boads.Constant.PATCHTYPE:
                        //    patchaddr.setText("0x" + Integer.toHexString(addr));
                        break;
                }
                Logdebug(TAG, "length==="+Integer.toHexString(addr));

//                Logdebug("huntersun", "hand addr:" + addr + " length:" + length);
            } else if (msg2.what == 10) {
                if (msg2.obj != null) {
                    String str = (String) msg2.obj;
                    Logdebug(TAG, "msg2.what == 10==="+str);
                }
            }


            switch (msg2.arg1) {
                case MSG_OTA_RESEPONSE:
                    if (msg.obj != null) {
                        String toaststr = (String) msg.obj;
                        Logdebug(TAG, "MSG_OTA_RESEPONSE==="+toaststr);
                        setOTAProgress(0);
                    }
                    break;
                case MSG_BURN_APP_SUCCESS:

                    break;
                case MSG1_NO_FILE:
                    Logdebug(TAG, "MSG1_NO_FILE===请选择一个文件进行升级");
                    setOTAProgress(0);
                    break;
                case com.example.otalib.boads.Constant.MSG_ARG1_KBS:
                    float kbs = (Float) msg2.obj;
                    Logdebug(TAG, "MSG_ARG1_KBS==="+kbs+"kB/s");
                    break;
                case MSG_DISCONNECT_BLE:
                    Logdebug(TAG, "MSG_DISCONNECT_BLE===0kB/s");
                    break;
                case MSG_BURN_CFG_SUCCESS:

                    break;
                case MSG1_BLE_ERROR:
                    Logdebug(TAG, "MSG1_BLE_ERROR===Bluetooth connection failed,Please scan bluetooth again");
                    break;
                case MSG_BURN_PATCH_SUCCESS:

                    break;
                case MSG_FLASH_EMPTY:
                    Logdebug(TAG, "MSG_FLASH_EMPTY===Bluetooth connection failed,Please scan bluetooth again");
                    break;
                case com.example.otalib.boads.Constant.MSG_ARG1_SEND_OTA_DATA:
                    int pos = 0;
                    int len = msg2.arg2;
                    int tmp = len % 20;
                    byte[] senddat = (byte[]) msg2.obj;
                    boolean res;
                    if (ota_tx_dat_charac == null) {
                        Logdebug("DEBUG_OTA",  "OTA has not discover the right character!");
                        return;
                    }
                    for (int i = 0; i < len / 20; i++) {
                        byte[] packet_data = new byte[20];
                        System.arraycopy(senddat, pos, packet_data, 0, 20);
                        if (mConnected == true && mBLE != null) {
                            res = ota_tx_dat_charac.setValue(packet_data);
                            ota_tx_dat_charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                            res = mBLE.writeCharacteristic(ota_tx_dat_charac);
                            if (!res) {
                                Logdebug(TAG, "writeCharacteristic() failed!!!");
                                return;
                            }
                            pos = pos + 20;
                        } else {
                            return;
                        }
                    }
                    if (tmp != 0) {
                        byte[] packet_data = new byte[tmp];
                        System.arraycopy(senddat, pos, packet_data, 0, tmp);
                        if (mConnected == true && mBLE != null) {
                            res = ota_tx_dat_charac.setValue(packet_data);
                            if (!res) {
                                Logdebug(TAG, "setValue() failed!!!");
                            }
                            ota_tx_dat_charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                            res = mBLE.writeCharacteristic(ota_tx_dat_charac);
                            if (!res) {
                                Logdebug(TAG, "writeCharacteristic() failed!!!");
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    break;
                case MSG_OTA_COMPLETE:
                    Logdebug(TAG, "OTA has done and success!");
                    Toast toast = Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    setOTAProgress(0);
                    break;
                case com.example.otalib.boads.Constant.MSG_ARG1_PROGRESS_BAR_MAX:
                    int len1 = msg2.arg2;
                    pb.setMax(len1);
                    break;

                case com.example.otalib.boads.Constant.MSG_ARG1_PROGRESS_BAR_UPDATA:
                    int len2 = msg2.arg2;
                    setOTAProgress(len2);
                    break;
                case MSG_HANDS_UP_FAILED:
                    Logdebug(TAG, "Hands up to the boads failed before OTA!");
                    setOTAProgress(0);
                    ToastUtils.showLong(R.string.upgrade_ota_tips);
                    hideDialog("MSG_HANDS_UP_FAILED");
                    ((Activity)context).finish();
                    return;
                case com.example.otalib.boads.Constant.MSG_ARG1_OTA_ENCRPT_KEY_FAILED:
                    Logdebug(TAG, "OTA exchange key please try again!");
                    setOTAProgress(0);
                    break;
                case com.example.otalib.boads.Constant.MSG_ARG1_SEND_OTA_CMD:
                {
                    int pos1 = 0;
                    int len3 = msg2.arg2;
                    int tmp1 = len3 % 20;
                    byte[] sendcmd = (byte[]) msg2.obj;
                    boolean res1 = false;
                    if (ota_tx_cmd_charac == null) {
                        Logdebug("DEBUG_OTA", "OTA has not discover the right character!");
                        return;
                    }
                    for (int i = 0; i < len3 / 20; i++) {
                        byte[] packet_data = new byte[20];
                        System.arraycopy(sendcmd, pos1, packet_data, 0, 20);
                        if (mConnected == true && mBLE != null) {
                            ota_tx_cmd_charac.setValue(packet_data);
                            res1 = mBLE.writeCharacteristic(ota_tx_cmd_charac);
                            if (!res1) {
                                Logdebug(TAG, "writeCharacteristic() failed!!!");
                                return;
                            }
                            pos1 = pos1 + 20;
                            Logdebug(TAG, packet_data.toString());
                        } else {
                            Logdebug("DEBUG_OTA", "--------mConnected----mBLE----");
                            return;
                        }
                    }

                    if (tmp1 != 0) {
                        byte[] packet_data = new byte[tmp1];
                        System.arraycopy(sendcmd, pos1, packet_data, 0, tmp1);
                        if (mConnected == true && mBLE != null) {
                            boolean b = ota_tx_cmd_charac.setValue(packet_data);
                            if (!b) {
                                Logdebug(TAG, "writeCharacteristic() failed!!!");
                                return;
                            }
                            res1 = mBLE.writeCharacteristic(ota_tx_cmd_charac);
                            if (!res1) {
                                Logdebug(TAG, "writeCharacteristic() failed!!!");
                                return;
                            }
                            Logdebug(TAG, packet_data.toString());
                        } else {
                            Logdebug("DEBUG_OTA", "--------mConnected----mBLE----");
                            return;
                        }
                    }
                }

                break;
            }
        }
    };

    private static void hideDialog(String label) {
        Log.e(TAG, "hideDialog:"+label);
        ViewUtils.runOnUiThread(() -> OTADialogHelper.hideDialog());
    }

    private Runnable runnable;
    public boolean startOtaUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mConnected)
                    bleManager.scanLeDevice(true);
                SDKTools.waiting = 0;
            }
        }, 2000);

        //定时任务检测蓝牙连接状态
        runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                SDKTools.mHandler.postDelayed(this, 50000);
                if(!mConnected && !isUpdate)
                    bleManager.scanLeDevice(true);
            }
        };
        SDKTools.mHandler.postDelayed(runnable, 50000);
        return true;
    }

    public static void startDownload(){
        OTADialogHelper.showDialog(context, R.string.upgradding, 5 * 60 * 1000, false);
        mCountDownTimer.cancel();
        mTransThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do_work_on_boads.setEncrypt(false);
                //burn app
                int response = -1;
                ByteUtil op = new ByteUtil();
                String addr = "29000";//getStringData(context, SP_USER_ADDR);

                Logdebug(TAG, "-------------startDownload------------>");
                if (!user_path.equals("")) {
                    String tmpAddress = FileUtils.getFileNameNoExtension(user_path);
                    //解析UI地址
                    if (tmpAddress.length() > 5) {
                        addr = tmpAddress.substring(5);
                        Log.e(TAG, "address:" + addr);
                    }
                    byte[] tmp_user = new byte[0];
                    try {
                        tmp_user = op.readSDFile(base_path + user_path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (tmp_user.length>0){
                        response = do_work_on_boads.writeUserData(tmp_user, addr);
                        SendFileRseponse(response,handler);
                    }
                }

                if(!app_path.equals("")){
                    byte[] tmp_app = new byte[0];
                    try {
                        tmp_app = op.readSDFile(base_path+app_path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (tmp_app.length>0) {
                        Logdebug(TAG, "-------------startDownload------------LoadBinary--------tmp_app");
                        response = do_work_on_boads.loadBinary(tmp_app, com.example.otalib.boads.Constant.APPTYPE);
                        SendFileRseponse(response, handler);
                    }
                }


                //burn configuration
                if(!cfg_path.equals("")){
                    byte[] cfg_read = new byte[0];
                    try {
                        cfg_read = op.readSDFile(base_path+cfg_path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (cfg_read.length>0) {
                        response = do_work_on_boads.loadBinary(cfg_read, com.example.otalib.boads.Constant.CONFGTYPE);
                        SendFileRseponse(response,handler);
                    }
                }


                //burn patch
                if(!patch_path.equals("")){
                    byte[] patch_read = new byte[0];
                    try {
                        patch_read = op.readSDFile(base_path+patch_path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (patch_read.length > 0) {
                        response = do_work_on_boads.loadBinary(patch_read, com.example.otalib.boads.Constant.PATCHTYPE);
                        SendFileRseponse(response,handler);
                    }
                }
                Log.e(TAG,"ota upgrade end");
                // 复位硬件
                do_work_on_boads.resetDevice();
                if (response == 0) {
                    SDKTools.BleState = 0;
                    SaveKeyValues.deleteAllValues();
                    ToastUtils.showLong(R.string.oat_upgrade_success);
                }else {
                    ToastUtils.showLong(R.string.upgrade_ota_tips);
                }
                SDKTools.otaState = 0;
                SDKTools.mService.close();
                hideDialog("ota upgrade end");
                ((Activity) context).finish();
            }
        });
        mTransThread.start();
    }

    /**
     * 按两次退出按钮退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            do_work_on_boads.resetDevice();
            checkVersion.setEnabled(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    private void sendDataToBle(Message msg, BluetoothGattCharacteristic characteristic) {
        int pos = 0;
        int len = msg.arg2;
        int tmp = len % 20;
        byte[] sendData = (byte[]) msg.obj;
        boolean res = false;
        if (characteristic == null) {
            Log.e(TAG, "OTA has not discover the right character!");
            return;
        }
        for (int i = 0; i < len / 20; i++) {
            byte[] packet_data = new byte[20];
            System.arraycopy(sendData, pos, packet_data, 0, 20);
            if (mConnected == true && mBLE != null) {
                Log.i("DEBUG_OTA", "ota send lenth:" + packet_data.length);
                characteristic.setValue(packet_data);
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                res = mBLE.writeCharacteristic(characteristic);
                if (!res) {
                    Log.i(TAG, "writeCharacteristic() failed!!!");
                    return;
                }
                pos = pos + 20;
            } else {
                return;
            }
        }
        if (tmp != 0) {
            byte[] packet_data = new byte[tmp];
            System.arraycopy(sendData, pos, packet_data, 0, tmp);
            if (mConnected == true && mBLE != null) {
                Log.e(TAG, "send data:" + Utils.bytesToHexString(packet_data));
                res = characteristic.setValue(packet_data);
                if (!res) {
                    Log.i(TAG, "setValue() failed!!!");
                }
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                res = mBLE.writeCharacteristic2(characteristic);
                if (!res) {
                    Log.i(TAG, "writeCharacteristic() failed!!!");
                    reSendData(characteristic);
                    return;
                }
            }
        }
    }

    int maxTimes = 0;
    private void reSendData(BluetoothGattCharacteristic characteristic) {
        handler.postDelayed(() -> {
            maxTimes++;
            boolean res = mBLE.writeCharacteristic2(characteristic);
            if (!res) {
                if (maxTimes < 5) {
                    reSendData(characteristic);
                    Log.e(TAG, "正在重发:" + maxTimes);
                } else {
                    Log.e(TAG, "重发完成");
                    mBLE.disconnect();
                    maxTimes = 0;
                }
            } else {
                maxTimes = 0;
                Log.e(TAG, "write success");
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        ToastUtils.showShort(R.string.upgradding);
    }


    public boolean isLocServiceEnable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!BleUtils.isEnableGps()) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(R.string.open_location_tips);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    openGpsSettings();
                    dialogInterface.dismiss();
                    finish();
                });
                dialog.show();
                return false;
            }
        }
        return true;
    }

    /**
     * 打开GPS设置页面
     */
    public void openGpsSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showOTAWarnDialog() {
        ViewUtils.runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateOtaActivity.this);
            dialog.setTitle(getString(R.string.tips_txt));
            dialog.setMessage(R.string.ota_out_memory_tips);
            dialog.setCancelable(false);
            dialog.setNegativeButton(getString(R.string.cancel_txt), (dialogInterface, i) -> {
                finish();
            });
            dialog.setPositiveButton(getString(R.string.continuea), (dialogInterface, i) -> {
                startMatchDevice();
            });
            dialog.show();
        });
    }

    /**
     * 判断是否超过最大设备最大内存大小
     * @return
     */
    public boolean isOutMaxMemory(long fileSize, String hexAddress) {
        try {
            int memorySize = 512 * MemoryConstants.KB;
            int usedPace;
            if (fileSize > 512 * MemoryConstants.KB) {
                memorySize = 1024 * MemoryConstants.KB;
                usedPace = (memorySize - Integer.parseInt(hexAddress, 16));
                Log.e(TAG, "fileSize:" + fileSize + ";usedPace:" + usedPace);
                return usedPace > (800 * MemoryConstants.KB);
            } else {
                usedPace = (memorySize - Integer.parseInt(hexAddress, 16));
                Log.e(TAG, "fileSize:" + fileSize + ";usedPace:" + usedPace);
                return usedPace > (352 * MemoryConstants.KB);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
