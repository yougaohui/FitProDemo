package xfkj.fitpro.activity;

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
import androidx.annotation.RequiresApi;
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

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static xfkj.fitpro.application.MyApplication.removeActivity_;

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

    //???????????????
    private int FontCameraIndex = 1;
    //???????????????
    private int BackCameraIndex = 0;

    //????????????????????????
    private int CAMERA_ID = 0; //???????????????

    private boolean isDevicesCloseCamera;//????????????????????????????????????


    public Handler mHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//??????msg?????????????????????
            Logdebug(TAG, TAG + "----state-------[" + map.get("state") + "]-----msg.what----" + msg.what);
            switch (msg.what) {
                case Profile.MsgWhat.what70:
                    Logdebug(TAG, "???????????????app????????????");
                    if (!mIsTimerRunning) {
                        mIsTimerRunning = true;
                        mHandler2.post(timerRunnable);
                    }
                    break;
                case Profile.MsgWhat.what71:
                    Logdebug(TAG, "??????????????????????????????--" + map.get("is_ok"));
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

        //????????????????????????
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

        //????????????????????????
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
                //????????????
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
     * ???????????????
     */
    private void setCamera() {
        if (null == mCamera) {
            mCamera = Camera.open(CAMERA_ID);//????????????
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
        //??????????????????
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    initCamera();//??????????????????????????????
                    camera.cancelAutoFocus();//????????????????????????????????????????????????
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


    //??????????????????????????????
    private void initCamera() {
        parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // ???????????????????????????????????????????????????
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//?????????
        if (CAMERA_ID == BackCameraIndex) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1????????????
        }
        setDispaly(parameters, mCamera);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2????????????????????????????????????????????????????????????

    }

    //?????????????????????????????????
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //??????????????????????????????
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Logdebug("Came_e", "????????????");
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
                removeActivity_(CameraActivity.this);//??????
                break;
            case R.id.change_camera_id:
                //?????????????????????
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
                //?????????????????????
                CAMERA_ID = CAMERA_ID == BackCameraIndex ? FontCameraIndex : BackCameraIndex;
                setCamera();
                break;
        }
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // ?????????????????????????????????????????????????????????????????????"image/jpeg ??? image/png????????????" ?????????????????? "image/*"
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
            //???????????????????????????????????????????????????
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(CAMERA_ID == BackCameraIndex ? 90 : 270);//?????????????????????270?? //?????????????????????90??
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //?????????????????????
        // ????????????????????????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bigName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ??????????????????????????????????????????
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        mCamera.startPreview();
    }

    /**
     * ????????????????????????
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

    //???????????????
    private void BindData() {
        final ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
        for (int n = 0; n <= 15; n++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("labTime", n + getString(R.string.second_txt));//??????
            int isCheck = (mCurrentTimer == n) ? R.drawable.checked : R.drawable.unchecked;
            map.put("labCheck", isCheck);//????????????
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
        if (!isDevicesCloseCamera && SDKTools.BleState == 1) {//???????????????????????????????????????
            SDKCmdMannager.closeCamera();
        }
    }
}
