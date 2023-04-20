# FitProDemo
# fitproSDK

## 1,
### （1）将fitprolib-release.aar、otalib.aar放入lib目录下,并在应用build.gradle下引入.
### （2）build.gradle下引入 implementation 'com.blankj:utilcodex:1.31.0' 

## 2,在Application初始化
    public class MyApplication extends Application {
      @Override
      public void onCreate() {
         super.onCreate();
         FitProSDK.getFitProSDK().init(this);
       }
    }

## 3、接口管理类 SDKCmdMannager

	/**
     * 连接状态
     *
     * @return true = 已连接
     */
    public static boolean isConnected();

    /**
     * 找手表
     *
     * @return 发送状态
     */
    public static boolean findWatch();


    /**
     * 获取设备设置信息
     *
     * @return
     */
    public static boolean getDeviceInfo();

    /**
     * 设置语言
     *
     * @return
     */
    public static boolean setLanguage(int enable);

    /**
     * 解除绑定
     *
     * @return
     */
    public static boolean unbondWatch();

    /**
     * 重置手环
     *
     * @return
     */
    public static boolean resetWatch();

    /**
     * 获取目标步数
     *
     * @return
     */
    public static boolean getTargetSteps();

    /**
     * 获取闹钟
     *
     * @return
     */
    public static boolean getAlarms();

    /**
     * 设置闹钟
     *
     * @param alarms
     */
    public static boolean setAlarms(byte[] alarms);

    /**
     * 获取测试数据
     */
    public static boolean getTestData();

    /**
     * 关闭测试数据
     */
    public static boolean closeTestData();

    /**
     * 自定义命令
     *
     * @param val
     */
    public static boolean sendCustomOrder(byte[] val) ;

    /**
     * 设置抬腕亮屏
     *
     * @param data
     */
    public static boolean setHandLight(byte[] data);

    /**
     * 获取翻腕亮屏信息
     */
    public static boolean GetInfoOfWrist();

    /**
     * 开启拍照功能
     */
    public static boolean openCamera();

    /**
     * 关闭拍照功能
     */
    public static boolean closeCamera();

    /**
     * 设置务扰模式
     *
     * @param longSit
     */
    public static boolean setDistuMode(byte[] data);

    /**
     * 获取勿扰模式信息
     */
    public static boolean getDistuModeInfo();

    /**
     * 设置心率自动测量
     *
     * @param longSit
     */
    public static boolean setHeartRateAutoMeas(byte[] data);

    /**
     * 获取心率自动测量信息
     */
    public static boolean getHearRateAutoMeInfo();

    /**
     * 获取久坐提醒
     */
    public static boolean getLongSitWarnInfo();

    /**
     * 开始测量心率
     */
    public static boolean startMeasureHeatRate();

    /**
     * 获取个人信息
     *
     * @param data
     */
    public static boolean getMessagesInfo(byte[] data);

    /**
     * 设置睡眠开关
     */
    public static boolean switchSleep();

    /**
     * 获取睡眠开关信息
     */
    public static boolean getSleepSwitchInfo();

    /**
     * app请求获取天总步数实时数据
     */
    public static boolean getTotalSportData();

    /**
     * 获取个人信息
     */
    public static boolean getPersonalInfo();


## 4、SDK工具类SDKTools

     /**
     * 数据库名字
     */
    public final static String DBNAME = "fitpro";

     /**
     * 应用程序根目录
     */
    public static String RootPath = "";

     /**
     * 数据库操作对象
     */
    public static SqliteDBAcces DBAcces = null;

    /**
     * 蓝牙服务通道对象
     */
    public static LeService mService = null;

    /**
     * 媒体播放器
     */
    public static MediaPlayer mediaPlayer = null;;

    public static boolean isdialog = false;

    /**
     * 蓝牙连接状态 -2设备不支持蓝牙, -1蓝牙未打开, 0未连接, 1 已连接
     */
    public static int BleState = 0;

    public static boolean hearting = false,blooding = false;//是否正在测试心率，血压


    public static boolean isDeubg = true;

    /**
     * app是否在前台 0否, 1 是
     */
    public static int IsAppOnForeground = 0;


    /**
     * 全局Handler
     */
    public static Handler mHandler = null;


    /**
     * 升级状态
     */
    public static int otaState = 0;//0 无，1 进入升级，2 DUF

    /**
     * 是否执行命令
     */
    public static boolean isExecute = true;


    /**
     * 是否展示获取数据loading
     */
    public static boolean isGetDataLoading = false;

    /**
     * 等待时间
     */
    public static int waiting = 0;



## 5、连接
    SDKTools.mService.connect2(addr);


## 6、关于本地通信指令解释 Profile
    public static final int what1 = 1; // 发现蓝牙
    public static final int what10 = 10; // 解绑手环
    public static final int what11 = 11; // 接收历史数据中
    public static final int what12 = 12; // 接收历史数据完成
    public static final int what13 = 13; // 重置
    public static final int what14 = 14; // 请求设置信息(取代 0x09 命令，具体 key 参考后面)
    public static final int what2 = 2; // 蓝牙状态
    public static final int what30 = 30; // 蓝牙初始设置显示 时间
    public static final int what31 = 31; // 蓝牙初始设置显示 个人信息
    public static final int what32 = 32; // 蓝牙初始设置显示 目标步数
    public static final int what33 = 33; //设置消息推送开关（来电、短信、微信、QQ）
    public static final int what34 = 34; //闹铃列表
    public static final int what35 = 35; //设置闹铃
    public static final int what36 = 36; //设置久坐提醒
    public static final int what37 = 37; // 安卓手机来电推送（来电、挂断电话、电话号码和姓名）安卓手机消息推销（来电、短信、微信、QQ）
    public static final int what38 = 38; //语言设置（value：中文：0x00 英文：0x01）
    public static final int what39 = 39; //抬腕亮屏设置
    public static final int what300 = 300; //勿扰模式
    public static final int what301 = 301; //睡眠开关设置和睡眠有效时间段设置
    public static final int what302 = 302; //心率自动测量
    public static final int what4 = 4; // 蓝牙电量
    public static final int what40 = 40; //左右手设置
    public static final int what5 = 5; // 蓝牙运动实时步数
    public static final int what51 = 51; //蓝牙运动数据15分钟返回一次
    
    public static final int what60 = 60; //测量心率返回值
    public static final int what61 = 61; //测量心率退出(手环主动发起)
    public static final int what62 = 62; //测量血压返回值
    public static final int what63 = 63; //测量血压退出(手环主动发起)
    public static final int what64 = 64; //测量心率开始/关闭(APP发起)
    public static final int what65 = 65; //测量血压开始/关闭(APP发起)
    
    public static final int what70 = 70; //拍照
    public static final int what71 = 71; //退出拍照
    public static final int what72 = 72; //打开拍照
    public static final int what80 = 80; //查找手机
    public static final int what90 = 90; //睡眠数据返回

## 7.关于蓝牙扫描注意事项
### .SDK提供有扫描接口以及蓝牙相关工具类，扫描示例代码MiBandReaderActivity.java
    BleManager.getInstance().scanLeDevice(true)表示扫描设备
    BleManager.getInstance().scanLeDevice(false)表示停止扫描设备
### .扫描结果获取需要注册监听广播
    LeReceiver是SDK广播，可以监听所有SDK返回的数据,具体使用请参考示例代码MiBandReaderActivity.java
    另外，Android 7 到Android11 需要打开定位并获取定位权限才能扫描到这边。
    Android12及以上需要动态申请Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE权限

## 数据发送
### SDKCmdMannager
    SDKCmdMannager 封装了常用的指令发送接口，直接调用就可以。
### 自定义发送数据
    调用SDKTools.mService.commandPoolWrite可以发送自定义指令。例如
    SDKTools.mService.commandPoolWrite(new byte[]{0,0,1}, "测试用例");
