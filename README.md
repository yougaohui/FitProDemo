# fitproSDK
# V1.2.4更新说明:  
  a.兼容Android14


## 1、集成
### （1）将fitprolib-release.aar、otalib.aar放入lib目录下,并在应用build.gradle下引入.
### （2）build.gradle下引入
    implementation 'com.blankj:utilcodex:1.31.1' 
    implementation 'androidx.work:work-runtime:2.7.1'


## 2、在Application初始化
    public class MyApplication extends Application {
      @Override
      public void onCreate() {
            FitProSDK.getFitProSDK()
                .setConfig(new Config()
                .setNotificationImportance(NotificationUtils.IMPORTANCE_DEFAULT)//设置前台消息通知级别
                .setNotificationTitle("你自己的标题")
                .setNotificationContent("您自己的通知内容")
                .setLogListener((tag, content) -> {//监听SDK返回的log信息，tag是用于定位日志页面的标记，content是log信息
                  Log.i(tag, content);//SDK log information
                }))
                .init(this);
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
     * 应用程序根目录
     */
    public static String RootPath = "";

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
     * 蓝牙连接状态 -2设备不支持蓝牙, -1蓝牙未打开, 0未连接, 1 已连接，3 连接失败
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

## 7、关于蓝牙扫描注意事项
### SDK提供有扫描接口以及蓝牙相关工具类，扫描示例代码MiBandReaderActivity.java
    BleManager.getInstance().scanLeDevice(true)表示扫描设备
    BleManager.getInstance().scanLeDevice(false)表示停止扫描设备
### 扫描结果获取需要注册监听广播
    LeReceiver是SDK广播，可以监听所有SDK返回的数据,具体使用请参考示例代码MiBandReaderActivity.java
    另外，Android 7 到Android11 需要打开定位并获取定位权限才能扫描到设备。
    Android12及以上需要动态申请Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE权限

## 8、数据发送
### SDKCmdMannager
    SDKCmdMannager 封装了常用的指令发送接口，直接调用就可以。
### 自定义发送数据
    调用SDKTools.mService.commandPoolWrite可以发送自定义指令。例如
    SDKTools.mService.commandPoolWrite(new byte[]{0,0,1}, "测试用例");

## 9、数据接收
    需要注册LeReceiver广播才能接收到设备的相关数据，具体示例代码请参考Demo中的MyApplication.java,里面有各种数据的接收示例.
    说明:这个类是Android自身的广播,请按照普通广播的方式进行注册和取消注册

## 10、天气
    DEMO里Utils->weather->WeatherProxy是天气的业务代理实现。天气需要客户请求自己的天气接口，
    并按照WeatherProxy的示例发送天气数据。
    DEMO示例位于BluetoothCommandActivity页面
    
### 天气类型
    协议支持多天气和单个天气。
    FitProSpUtils.isSupport3DaysWeather()表示true支持多天气
    FitProSpUtils.isSupportWeather()表示true支持一天的天气

## 11、消息通知
    消息通知拦截在NotifyService.java有实现
    
## 12、语言设置
    默认SDK刚连上会同步手机本地语言到设备端。如果开发者需要根据自己的情况同步语言到设备端，请调用SDKCmdMannager.setLanguage(lang).
    lang对应国家的编码:
    中文：0x00 英文：0x01 繁体：0x02 阿拉伯语：0x03 捷克语：0x04 德语:0x05  西班牙语:0x06  法语:0x07  日语:0x08  马来西亚语:0x09  荷兰语:0xA  波兰语:0xB  葡萄牙语:0xC  俄语:0xD  斯洛伐克语:0xE  泰语:0xF  土耳其语:0x10 越南语:0x11 意大利语:0x12 菲律宾语:0x13 印尼语:0x14
    乌克兰语:0x15 印度语:0x16 芬兰语:0x17 克罗地亚语:0x18 挪威语:0x19  丹麦语:0x1A 瑞典语:0x1B  韩语:0x1C 匈牙利语:0x1D 希腊语:0x1E 波斯语:0x1F 罗马尼亚语:0x20

## 13、表盘升级错误码
    public static final int ERROR_WAIT_TIMEOUT = 1001;//等待设备返回数据超时
    public static final int ERROR_RESEND_TIMEOUT = 1002;//重发等待设备返回数据超时
    public static final int ERROR_CHECK = 1003;//校验错误，设备端返回的
    public static final int ERROR_IMG_FILE_NO_EXIST = 1004;//图片固件文件不存在
    public static final int ERROR_FONT_FILE_NO_EXIST = 1005;//字体固件文件不存在
    public static final int ERROR_BLE_DISCONNECTED = 1006;//app和设备已断开
    public static final int ERROR_UNKNOWN = 1007;//未知错误，设备端返回0过来导致的
    public static final int ERROR_BATTERY_LOW = 1008;//电量低
    public static final int ERROR_CHARGE_BATTERY = 1009;//正在充电

## 14、前台服务通知
     com.legend.bluetooth.fitprolib.utils.NotificationUtil.startServiceNotification(title,content);
     title是通知标题，content是通知内容。开发人员可以通过这个方法修改通知里面的内容。

## 15、本地ProfilePlus解释
    ProfilePlus.what1 //表盘信息获取
    ProfilePlus.what2 //设备信息返回
    ProfilePlus.what3 //产品信息返回

## 16、个人信息
### (1) 获取个人信息
        Integer gender = FitProSpUtils.getGender();//获取性别，1表示男，0表示女，默认1
        Integer age = FitProSpUtils.getAge();//获取年龄，默认25岁
        Integer height = FitProSpUtils.getHeight();//获取身高，默认170cm
        Integer weight = FitProSpUtils.getWeight();//获取体重,默认65kg
        Integer weight = FitProSpUtils.getTargetSteps();//获取目标步数,默认5000
### (2) 设置个人信息
        FitProSpUtils.setGender(gender);//设置性别，gender 1表示男性，0表示女性
        FitProSpUtils.setAge(age);//设置年龄，age > 0
        FitProSpUtils.setHeight(height);//设置身高 单位cm
        FitProSpUtils.setWeight(weight);//设置体重 单位kg
        FitProSpUtils.setTargetSteps(targetSteps);//设置目标步数，单位步，不设置默认5000
        