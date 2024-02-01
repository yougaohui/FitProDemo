package xfkj.fitpro.activity;

import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetStepValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getSetUinfoValue;
import static com.legend.bluetooth.fitprolib.utils.BleUtils.setLeServiceEnable;
import static xfkj.fitpro.application.MyApplication.removeActivity_;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.SDKTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.base.BaseActivity;

@SuppressLint("NewApi")
public class MiBandReaderActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private String TAG = "MiBandReaderActivity";
    private List<Map<String, Object>> dlist = null;
    private ListView drives_lists;
    private LeReceiver leReceiver;
    private LinearLayout bbtns_box, title_chunk;
    private RelativeLayout blist_box;
    private TextView connection_stocks, time_stocks, uinfo_stocks, step_stocks;
    private BleManager mBle;
    /**
     * 扫描动画
     */
    private LottieAnimationView lottieanimation;


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            switch (msg.what) {
                case Profile.MsgWhat.what1:
                    //获取扫描成功的设备
                    BluetoothDevice device = (BluetoothDevice) map.get("device");
                    if (StringUtils.isTrimEmpty(device.getName())) {
                        return;
                    }
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("address", device.getAddress());
                    map2.put("name", device.getName());
                    map2.put("rssi", map.get("rssi"));

                    if (dlist == null) {
                        dlist.add(map2);
                    }
                    int is_add = 0;
                    for (int i = 0; i < dlist.size(); i++) {
                        if (((Map) dlist.get(i)).get("address").equals(map2.get("address"))) {
                            dlist.set(i, map2);
                            is_add++;
                            break;
                        }
                    }
                    if (is_add == 0) {
                        dlist.add(map2);
                    }
                    if (!isFastRefresh() && dlist.size() > 1) {
                        Collections.sort(dlist, new ComparatorValues());
                    }
                    MyAdapter ListAdapter = new MyAdapter(dlist);
                    drives_lists.setAdapter(ListAdapter);
                    break;
                case Profile.MsgWhat.what2:
                    if (map.get("state").equals("0")) {//断开连接
                        setStateUI();
                    } else if (map.get("state").equals("1")) {//连接成功
                        connection_stocks.setText(getString(R.string.completed_txt));
                        connection_stocks.setTextColor(getResources().getColor(R.color.white));
                        showView(true);
                    }
                    break;
                case Profile.MsgWhat.what30:
                    time_stocks.setText(getString(R.string.completed_txt));
                    time_stocks.setTextColor(getResources().getColor(R.color.white));
                    SDKTools.mService.commandPoolWrite(getSetUinfoValue(), "设置个人信息");//设置个人信息
                    break;
                case Profile.MsgWhat.what31:
                    uinfo_stocks.setText(getString(R.string.completed_txt));
                    uinfo_stocks.setTextColor(getResources().getColor(R.color.white));
                    SDKTools.mService.commandPoolWrite(getSetStepValue(), "设置目标步数信息");//设置目标步数信息
                    break;
                case Profile.MsgWhat.what32:
                    step_stocks.setText(getString(R.string.completed_txt));
                    step_stocks.setTextColor(getResources().getColor(R.color.white));
                    //    Constant.mService.commandPoolWrite(getDeviceSetInfo(2),"请求个人设置信息和提醒命令");// 请求个人设置信息和提醒命令
                    startActivity(new Intent(MiBandReaderActivity.this, MenusActivity.class));
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeActivity_(MiBandReaderActivity.this);
                        }
                    }, 10000);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 按信号强度排序
     */
    private final class ComparatorValues implements Comparator<Map> {
        @Override
        public int compare(Map object1, Map object2) {
            int m1 = (int) object1.get("rssi");
            int m2 = (int) object2.get("rssi");
            int result = 0;
            if (m1 < m2) {
                result = 1;
            }
            if (m1 > m2) {
                result = -1;
            }
            return result;
        }
    }

    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle(this.getString(R.string.find_fitpro));
    }

    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_bluetooth);
    }

    @Override
    protected void initValues() {
        leReceiver = new LeReceiver(MiBandReaderActivity.this, mHandler);
        mBle = BleManager.getInstance();
        setLeServiceEnable(false);
        isLocServiceEnable(this);
    }

    public void isLocServiceEnable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!network) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(R.string.open_location_tips);
                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    protected void initViews() {
        dlist = new ArrayList<>();
        drives_lists = findViewById(R.id.drives_lists);

        bbtns_box = findViewById(R.id.bbtns_box);
        blist_box = findViewById(R.id.blist_box);
        title_chunk = findViewById(R.id.title_chunk);

        connection_stocks = findViewById(R.id.connection_stocks);
        time_stocks = findViewById(R.id.time_stocks);
        uinfo_stocks = findViewById(R.id.uinfo_stocks);
        step_stocks = findViewById(R.id.step_stocks);

        lottieanimation = findViewById(R.id.lottieanimation);

        showView(SDKTools.BleState == 1 ? true : false);
    }

    @Override
    protected void setViewsListener() {
        drives_lists.setOnItemClickListener(this);
        ImageView title_left = (ImageView) findViewById(R.id.left_btn);
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBle.scanLeDevice(false);
                startActivity(new Intent(MiBandReaderActivity.this, MainActivity.class));
                finish();
            }
        });

    }


    @Override
    protected void setViewsFunction() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtils.permission(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE).request();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String addr = dlist.get(i).get("address").toString();
        Logdebug(TAG, "点击连接蓝牙的地址是:" + addr);
        if (SDKTools.mService != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SDKTools.mService.connect2(addr);
                }
            }, 1000);
            setStateUI();
            dlist.clear();
            showView(true);
        } else {
            Toast.makeText(MiBandReaderActivity.this, getString(R.string.connecting_txt), Toast.LENGTH_SHORT).show();
        }
    }

    public void setStateUI() {
        connection_stocks.setText(getString(R.string.reinfecta_txt));
        connection_stocks.setTextColor(getResources().getColor(R.color.gray));
        time_stocks.setText(getString(R.string.reinfecta_txt));
        time_stocks.setTextColor(getResources().getColor(R.color.gray));
        uinfo_stocks.setText(getString(R.string.reinfecta_txt));
        uinfo_stocks.setTextColor(getResources().getColor(R.color.gray));
        step_stocks.setText(getString(R.string.reinfecta_txt));
        step_stocks.setTextColor(getResources().getColor(R.color.gray));
    }

    public void showView(boolean is_show) {
        if (is_show) {
            bbtns_box.setVisibility(View.VISIBLE);
            blist_box.setVisibility(View.GONE);
            lottieanimation.cancelAnimation();
            lottieanimation.setVisibility(View.GONE);
            title_chunk.setVisibility(View.INVISIBLE);
        } else {
            blist_box.setVisibility(View.VISIBLE);
            bbtns_box.setVisibility(View.GONE);
            title_chunk.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (leReceiver != null)
            leReceiver.unregisterLeReceiver();
        if (mBle != null)
            mBle.scanLeDevice(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (leReceiver != null)
            leReceiver.registerLeReceiver();
        if (mBle != null)
            mBle.scanLeDevice(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBle.scanLeDevice(false);
            finish();
            return true;
        }
        return false;
    }

    public class MyAdapter extends BaseAdapter {

        private List<Map<String, Object>> list;

        public MyAdapter(List<Map<String, Object>> data) {
            this.list = data;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setData(List<Map<String, Object>> data) {
            this.list = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(MiBandReaderActivity.this, R.layout.drives_item, null);
            Map<String, Object> map = list.get(i);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView address = (TextView) v.findViewById(R.id.adress);
            TextView rssi = (TextView) v.findViewById(R.id.rssi);
            name.setText((String) map.get("name"));
            address.setText((String) map.get("address"));
            rssi.setText(map.get("rssi") + "");
            return v;
        }
    }

    //检测刷新快慢
    private long lastClickTime = 0;//上次点击的时间
    private int spaceTime = 3000;//时间间隔

    public boolean isFastRefresh() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isFastClick;//是否允许点击
        if (currentTime - lastClickTime > spaceTime) {
            isFastClick = false;
            lastClickTime = currentTime;
        } else {
            isFastClick = true;
        }
        return isFastClick;
    }
}
