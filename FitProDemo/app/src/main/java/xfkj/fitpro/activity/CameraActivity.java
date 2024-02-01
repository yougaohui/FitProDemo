package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static xfkj.fitpro.application.MyApplication.removeActivity_;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback {
    private SurfaceView mSurfaceView;
    private ImageView camera_back, showpic, time_set, change_camera_id;
    private Button btnTakePic;
    private TextView mTvCountDown;

    private SurfaceHolder mHolder;

    private Camera mCamera;
    private int mCurrentTimer = 3;

    private boolean mIsTimerRunning = false;
    private RelativeLayout camera_box, camera_time_box;

    private static final String TAG = CameraActivity.class.getSimpleName();

    private ListView time_item;
    private LeReceiver leReceiver;
    Camera.Parameters parameters;

    //前置摄像头
    private int FontCameraIndex = 1;
    //后置摄像头
    private int BackCameraIndex = 0;

    //当前使用的摄像头
    private int CAMERA_ID = 0; //后置摄像头

    private boolean isDevicesCloseCamera;//是否是设备端发出退出指令


    public Handler mHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            Logdebug(TAG, TAG + "----state-------[" + map.get("state") + "]-----msg.what----" + msg.what);
            switch (msg.what) {
                case Profile.MsgWhat.what70:
                    Logdebug(TAG, "接收到控制app拍照命令");
                    if (!mIsTimerRunning) {
                        mIsTimerRunning = true;
                        mHandler2.post(timerRunnable);
                    }
                    break;
                case Profile.MsgWhat.what71:
                    Logdebug(TAG, "接收关闭拍照页面命令--" + map.get("is_ok"));
                    isDevicesCloseCamera = true;
                    removeActivity_(CameraActivity.this);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(getString(R.string.camera_time_run));
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_camera);

        //获取前后置摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int iCameraCnt = Camera.getNumberOfCameras();
        for (int i = 0; i < iCameraCnt; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                FontCameraIndex = i;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                BackCameraIndex = i;
            }
        }

        //默认为后置摄像头
        CAMERA_ID = BackCameraIndex;
    }

    @Override
    protected void initValues() {
        isDevicesCloseCamera = false;
        mCurrentTimer = SaveKeyValues.getIntValues("take_pic_time", 3);
        leReceiver = new LeReceiver(CameraActivity.this, mHandler2);
    }

    @Override
    protected void initViews() {
        mSurfaceView = findViewById(R.id.surface_view);
        btnTakePic = findViewById(R.id.btnTakePic);
        mTvCountDown = findViewById(R.id.count_down);
        time_item = findViewById(R.id.time_list_item);

        camera_back = findViewById(R.id.camera_back);
        showpic = findViewById(R.id.showpic);
        time_set = findViewById(R.id.time_set);
        change_camera_id = findViewById(R.id.change_camera_id);

        camera_box = findViewById(R.id.camera_box);
        camera_time_box = findViewById(R.id.camera_time_box);

        btnTakePic.setOnClickListener(this);
        camera_back.setOnClickListener(this);
        time_set.setOnClickListener(this);
        showpic.setOnClickListener(this);
        change_camera_id.setOnClickListener(this);
    }

    @Override
    protected void setViewsListener() {
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        ImageView title_left = (ImageView) findViewById(R.id.left_btn);
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_time_box.setVisibility(View.GONE);
                camera_box.setVisibility(View.VISIBLE);
                //时间选择
//                startPreview();
            }
        });

    }

    @Override
    protected void setViewsFunction() {
    }


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (null == mCamera || !mIsTimerRunning) {
                return;
            }
            Logdebug(TAG, "---mCurrentTimer---" + mCurrentTimer + "--------" + time_item.getTag(R.id.time_list_item));

            if (mCurrentTimer > 0) {
                mTvCountDown.setText(mCurrentTimer + "");
                mCurrentTimer--;
                mHandler2.postDelayed(timerRunnable, 1000);
            } else {
                mTvCountDown.setText("");
                mCamera.takePicture(null, null, null, CameraActivity.this);
                playSound();
                mIsTimerRunning = false;
                mCurrentTimer = SaveKeyValues.getIntValues("take_pic_time", 3);
            }
        }
    };


    /**
     * 设置摄像头
     */
    private void setCamera() {
        if (null == mCamera) {
            mCamera = Camera.open(CAMERA_ID);//打开相机
            try {
                initCamera();
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //实现自动对焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    initCamera();//实现相机的参数初始化
                    camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }


    //相机参数的初始化设置
    private void initCamera() {
        parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//闪光灯
        if (CAMERA_ID == BackCameraIndex) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        }
        setDispaly(parameters, mCamera);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Logdebug("Came_e", "图像出错");
        }
    }

    @Override
    public void onClick(View v) {
        if (mIsTimerRunning) {
            return;
        }
        switch (v.getId()) {
            case R.id.time_set:
                camera_box.setVisibility(View.GONE);
                BindData();
                camera_time_box.setVisibility(View.VISIBLE);
                break;
            case R.id.showpic:
                choosePhoto();
                break;
            case R.id.btnTakePic:
                if (!mIsTimerRunning) {
                    mIsTimerRunning = true;
                    mHandler2.post(timerRunnable);
                }
                break;
            case R.id.camera_back:
                removeActivity_(CameraActivity.this);//返回
                break;
            case R.id.change_camera_id:
                //切换前后摄像头
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
                //前后摄像头切换
                CAMERA_ID = CAMERA_ID == BackCameraIndex ? FontCameraIndex : BackCameraIndex;
                setCamera();
                break;
        }
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        startActivityForResult(intentToPickPic, 1);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String bigName = System.currentTimeMillis() + ".JPEG";
        String fileName = SDKTools.RootPath + "/picture/";

        File folder = new File(fileName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        fileName += bigName;
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            //旋转角度，保证保存的图片方向是对的
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(CAMERA_ID == BackCameraIndex ? 90 : 270);//前置摄像头旋转270° //后置摄像头旋转90°
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对相册进行刷新
        // 把刚保存的图片文件插入到系统相册
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bigName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        mCamera.startPreview();
    }

    /**
     * 播放系统拍照声音
     */
    public void playSound() {
        MediaPlayer mediaPlayer = null;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer.create(this,
                        Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }

    //选择倒计时
    private void BindData() {
        final ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
        for (int n = 0; n <= 15; n++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("labTime", n + getString(R.string.second_txt));//序号
            int isCheck = (mCurrentTimer == n) ? R.drawable.checked : R.drawable.unchecked;
            map.put("labCheck", isCheck);//测量时间
            mData.add(map);
        }

        String[] from = new String[]{"labTime", "labCheck"};
        int[] to = new int[]{R.id.labTime, R.id.labCheck};
        final SimpleAdapter adapter = new SimpleAdapter(CameraActivity.this, mData, R.layout.layout_camera_time_item, from, to);
        time_item.setAdapter(adapter);
        time_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentTimer = i;
                SaveKeyValues.putIntValues("take_pic_time", i);
                BindData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leReceiver != null)
            leReceiver.registerLeReceiver();
        if (SDKTools.BleState == 1) {
            SDKCmdMannager.openCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (leReceiver != null)
            leReceiver.unregisterLeReceiver();
        super.onDestroy();
        if (!isDevicesCloseCamera && SDKTools.BleState == 1) {//前提不是设备端发出指令退出
            SDKCmdMannager.closeCamera();
        }
    }
}
