# fitproSDK
# V1.2.5更新说明:  
  a.新增常用联系人


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
     * app请求获取天总数据,包括睡眠数据、历史步数、历史心率等
     */
    public static boolean getTotalSportData();

    /**
     * 获取个人信息
     */
    public static boolean getPersonalInfo();

     /**
     * 设置温度单位
     * @param unite 0为摄氏，1为华氏
     */
    public static void setTmpUnite(byte unite)

    /**
     * 设置系统时间
     */
    public static boolean synchronTime()

    /**
     * 获取表盘升级信息
     *
     * @return
     */
    public static boolean getClockDialInfo() 

    /**
     * 停止铃声
     */
    public static void pauseRingtone()

    /**
     * 获取硬件信息
     */
    public static boolean getHardInfo()

    /**
     * 获取产品信息
     */
    public static boolean getProductInfo()

    /**
     * 开始测量血压 单独测试血压，部分设备不支持，以实际设备为准
     * @param measure true表示开始测量，false表示停止测量
     * @return
     */
    public static boolean startMeasureBlood(boolean measure)


    /**
     * 开始测量心率 单独测试心率，部分设备不支持，以实际设备为准
     * @param measure true表示开始测量，false表示停止测量
     * @return
     */
    public static boolean startMeasureHeart(boolean measure)

    /**
     * 开始测量血氧 单独测试血氧，部分设备不支持，以实际设备为准
     * @param measure true表示开始测量，false表示停止测量
     * @return
     */
    public static boolean startMeasureSpo(boolean measure)

    /**
     * 添加联系人 部分设备不支持，以实际设备为准
     * @param name 联系人姓名
     * @param phone 联系人号码
     * @return
     */
    public static boolean addContact(String name, String phone)

    /**
     * 删除联系人 部分设备不支持，以实际设备为准
     * @param phoneNumber 手机号码
     * @return
     */
    public static boolean deleteContact(String phoneNumber)
    /**
     * 设置SOS 部分设备不支持，以实际设备为准
     * @param phoneNumber 手机号码
     * @return
     */
    public static boolean setSOS(String phoneNumber)

    /**
     * 获取常用联系人状态,主要用于获取设备端联系人个数和最大支持多少联系人 部分设备不支持，以实际设备为准
     * @return
     */
    public static boolean syncContractStatus()


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
    public static final int what19 = 19;//添加联系人
    public static final int what23 = 23;//删除联系人
    public static final int what24 = 24;//设置紧急联系人
    public static final int what25 = 25;//同步温度单位        
    public static final int what26 = 26; // 心电开始
    public static final int what27 = 27; // 心电结束
    public static final int what28 = 28; // 支付码返回状态        
    public static final int what91 = 91; // 设置目标运动时间
    public static final int what92 = 92; // 设置目标站立目标
    public static final int what93 = 93; // aplipay设备透传数据
    public static final int what94 = 94; // 设备主动请求APP断开连接并保持不回连1到2分钟

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
###    1、消息通知拦截在demo里面的NotifyService.java有实现
###    2、发送消息通知方法
####   NotifiMsgHelper.sendNotifyPush(packageName, msgs, 0)
#####  packageName指的是app包名，msgs是消息内容，第三个参数默认0（注意:发送消息通知前必须先开启对应app的消息通知开关）
#####  消息通知开关在Demo中的MessageSettingActivity.java有实现
    
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
    ProfilePlus.what4 //通讯录信息返回
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


## 17、SaveKeyValues 
       说明:这个类是缓存临时数据使用的，是基于SharedPreferences的封装
### 使用
        putStringValues（String key,String values）//存入String类型的值
        getStringValues(String key,String defValue）//取出String类型的值
        putIntValues(String key,int values) //存入int类型的值
        getIntValues(String key,int defValue)//取出int类型的值
        putLongValues(String key,long values)//存入long类型的值
        getLongValues(String key,long defValue)//取出long类型的值
        putFloatValues(String key,long values)//存入long类型的值
        getFloatValues(String key,long defValue)//取出long类型的值
        deleteAllValues()//删所有数据
        removeKeyForValues(String key)//删除指定key

## 18、FitProSpUtils
        
         /**
         * 获取性别
         *
         * @return 1表示男性，0表示女性
         */
        public static int getGender();

        /**
         * 设置性别
         *
         * @param gender 1表示男性，0表示女性
         */
        public static void setGender(int gender);

        /**
         * 设置年龄
         *
         * @param age
         */
        public static void setAge(int age);

        /**
         * 获取年龄
         *
         * @return
         */
        public static int getAge();

        /**
         * 设置身高
         *
         * @param height 单位cm
         */
        public static void setHeight(int height);

        /**
         * 获取身高 单位cm
         */
        public static int getHeight();

        /**
         * 设置体重 单位 kg
         */
        public static void setWeight(int weight);

        /**
         * 获取体重
         *
         * @return 单位 kg
         */
        public static int getWeight();

        /**
         * 获取目标步数
         */
        public static int getTargetSteps();

        /**
         * 设置目标步数
         *
         * @param targetSteps
         */
        public static void setTargetSteps(int targetSteps);


        /**
         * 设置目标睡眠
         *
         * @param targetSleep 单位小时
         */
        public static void setTargetSleep(int targetSleep);

        /**
         * 获取目标睡眠
         *
         * @return 睡眠时长，单位小时
         */
        public static int getTargetSleep(); 

        /**
         * 设置距离单位 (1->KM, 2->MILES)
         */
        public static void setDistanceUnit(int distanceUnit);

        /**
         * 获取距离单位
         *
         * @return 距离单位(1 - > KM, 2 - > MILES)
         */
        public static int getDistanceUnit();

        /**
         * @return 体重单位(1 - > KG. 2 - > 磅)
         */
        public static int getWeightUnit();

        /**
         * 设置体重单位
         *
         * @param weightUnit 体重单位(1->KG. 2->磅)
         */
        public static void setWeightUnit(int weightUnit);

        /**
         * @return 身高单位(1 - > CM, 2 - > 英寸)
         */
        public static int getHeightUnit();

        /**
         * 设置身高单位
         *
         * @param heightUnit 身高单位(1->CM,2->英寸)
         */
        public static void setHeightUnit(int heightUnit);


        /**
         * 设置温度单位
         *
         * @param tempUnit 温度单位(0->℃,1->℉)
         */
        public static void setTemptUnit(int tempUnit);

        /**
         * 获取温度单位
         */
        public static int getTemptUnit();

 
        /**
         * 获取mac地址
         *
         * @return
         */
        public static String getBluetoothAddress();

        /**
         * 设置蓝牙mac地址
         *
         * @param bluetoothAddress
         */
        public static void setBluetoothAddress(String bluetoothAddress);

        /**
         * 保存实时步数 
		 * @param dist 距离
		 * @param realSteps 实时步数
		 * @param cal 卡路里  大卡
         */
        public static void saveRealStepsInfo(int dist, int realSteps, int cal);

        /**
         * 获取实时步数
         *
         * @return 实时步数
         */
        public static int getRealSteps();

        /**
         * 获取卡路里
         *
         * @return
         */
        public static int getRealCal();

        /**
         * 获取实时距离
         *
         * @return 单位 m
         */
        public static String getRealDistance();
     

        /**
         * 是否支持心率
         *
         * @return true 支持心率 
         */
        public static boolean isSurpportHeart();

        /**
         * 是否支持血压
         * 利用的是运动位来判断是否支持睡眠，这个标记位没用到
         *
         * @return true 表示支持
         */
        public static boolean isSurpportSleep();

        /**
         * 是否支持距离公英制转换
         *
         * @return true 表示支持
         */
        public static boolean isSurpportDistance() ;

        /**
         * 是否支持微信运动
         *
         * @return true 表示支持
         */
        public static boolean isSurpportWXSport();

        /**
         * 支持语音通话
         *
         * @return true 表示支持
         */
        public static boolean isSurpportVoice();

        /**
         * 更新语音提示状态，true表示显示过
         */
        public static void updateVoiceTipsStatus();

        /**
         * 是否显示过语音提示
         *
         * @return
         */
        public static boolean isShownVoiceTips() ;

        /**
         * 默认支持来电提醒功能
         *
         * @return
         */
        public static boolean isDefalutOpenCall();

        /**
         * 支持同步通讯录
         *
         * @return
         */
        public static boolean isSupportSycContract();


        /**
         * 是否支持表盘设置
         *
         * @return
         */
        public static boolean isSupportClockDialSettings() ;

        /**
         * 是否支持温度显示
         *
         * @return
         */
        public static boolean isSupportTemp() ;

        /**
         * 是否支持天气
         *
         * @return
         */
        public static boolean isSupportWeather() ;

        /**
         * 支持多天气
         *
         * @return
         */
        public static boolean isSupport3DaysWeather();

        /**
         * 是否支持更多消息通知
         *
         * @return
         */
        public static boolean isSupportMoreNotifi();

        /**
         * 是否支持更多消息通知
         *
         * @return
         */
        public static boolean isShowAdv();


        /**
         * 是否支持远程拍照
         *
         * @return
         */
        public static boolean isSupportRemoteCamera() ;


        /**
         * 是否支持找手环
         *
         * @return
         */
        public static boolean isSupportFindDevice() ;


        /**
         * 是否支持心电
         *
         * @return
         */
        public static boolean isSupportHREL() ;

        /**
         * 是否显示久坐时长
         *
         * @return
         */
        public static boolean isShowLongSitTime() ;


        /**
         * 是否显示勿扰模式
         *
         * @return
         */
        public static boolean isShowDisturnMode();

        /**
         * 是否显示二维码支付
         *
         * @return
         */
        public static boolean isShowPay() ;

        /**
         * 是否支持来电语音播报功能
         * @return false不支持，只发送名字或者电话号码。
         * @return true 支持，需要发送名字和号码过去，名字在前，通过"/"进行分割
         */
        public static boolean isSurpportVoicePlay() ;
        /**
         * 是否支持hid
         * @return
         */
        public static boolean isSurpportHid() ;


        /**
         * 公司有比较多的方案商，
         * 为了适配不同的方案商并兼容以前的方案商区别处理
         */
        public static boolean isManufacturer01();

        public static boolean isManufacturer01(String macAddress);


        /**
         * 保存经典蓝牙地址
         */
        public static void saveClassicMac(String addresss) ;


        /**
         * 经典蓝牙名称编码
         *
         * @param code
         */
        public static void saveClassicBleNameCode(int code) ;

        /**
         * 获取 经典蓝牙名称编码
         * @return  0x00:"LH728-Audio";0x01:"WellAudio";0x02:"LH722-Audio";0x03:AQFiT"
         */
        public static int getClassicBleNameCode();

        /**
         * @param type 默认是0,1表示泰凌微平台,2表示LR平台,3表示BK平台,4表示OM平台
         * @return
         */
        public static void setPlarmType(int type;

        /**
         * 获取平台信息
         * @return   默认是0,1表示泰凌微平台,2表示LR平台,3表示BK平台,4表示OM平台
         */
        public static int getPlarmType() ;

        /**
         * 获取不会被解绑后清除的蓝牙mac地址
         *
         * @return
         */
        public static String getLongCacheBleMac();

        /**
         * 缓存因为解绑后的蓝牙地址
         *
         * @param address
         */
        public static void setLongCacheBleMac(String address) ;

        /**
         * 缓存蓝牙名称
         */
        public static void cacheBluetoothName(String key, String bluetoothName) ;

        /**
         * 获取缓存蓝牙
         *
         * @param key
         * @return
         */
        public static String getCacheBluetoothName(String key);

        /**
         * 保存目标运动时间
         * @param targetTime
         */
        public static void saveTargetSportTime(short targetTime) ;
		
        /**
         * 保存经典蓝牙名称
         * @param name
         */
        public static void saveClassicBluetoothName(String name) ;


        /**
         * 获取目标运动时间
         * @return
         */
        public static int getTargetSportTime();

        /**
         * 是否支持震动模式
         * @return true表示支持
         */
        public static boolean isSupportShakeMode() ;

        /**
         * 是否支持联系人SOS
         * @return true表示支持
         */
        public static boolean isSupportSOSContract() ;

        /**
         * 是否支持抬手亮屏
         * @return  true表示支持
         */
        public static boolean isSupportHandLight() ;

        /**
         * 获取经典蓝牙名
         * @return
         */
        public static String getClassicBluetoothName() ;
## 18、睡眠时间计算说明
       (1)、开始结束时间
       睡眠时间是从22:00到第二天08:00，22:00前和08:00后的数据不计入统计。
       (2)、深眠、浅睡、清醒计算方式
        a、睡眠数据是以一个个连续item过来的，每个item包括了当前节点的时间和睡眠状态.
        b、睡眠状态:1表示深睡状态，2表示浅睡状态，3表示清醒状态
        c、计算方式:后面的状态时间 - 前面的状态时间 = 前面状态的睡眠时长
        比如:item1 为清醒，时间是22:00 item2 为浅睡，时间是22:15，那么22:00到22:15这个区间为清醒状态。

## 19、升级用户自定义表盘
###    （1）、需要构造库里面的WatchThemeDetailsResponse
###    （2）、必须传入的参数说明:
        a、setFonBinPath("本地字体文件路径");自定义表盘必须得传入，可选。
        b、setPicBinpath("本地表盘文件路径");表盘文件，必传。
        c、setThumbBinPath("本地缩略图文件路径");缩略图文件路径，支持缩略图的表盘才需要(WatchThemeTools.isSupportThumb()，可以判断是否需要缩略图)，可选。
        d、setFontPosition("表盘字体位置");字体位置，默认0代表没有方向，有1、2、3、4四个方向，分别代表左上，右上，右下，左下，具体实现参照demo，可选。
        e、setCutomTheme("是否是自定义表盘");是否是自定义表盘，必传。
        f、setReplacePicPos("替换表盘位置");替换的表盘位置，clockInfo.getVersionCode()表盘协议是2的支持，可选。
###    （3）、调用方法
####        a、WatchThemeTools.getInstance().startFile("表盘信息","必要参数体");
####        b、表盘信息:ClockDialInfoBody
        /**
         * 主板型号
         */
         String mainModel;
    
        /**
         * 整机型号
         */
         String mchModel;
    
        /**
         * 高低配  0：高配   1：低配
         */
         int grade;
    
        /**
         * 屏幕类型  0：方屏  1：圆屏
         */
         int screenType;
    
        /**
         * 屏幕宽度
         */
         int width;
    
        /**
         * 屏幕高度
         */
         int height;
    
        /**
         * 配置信息
         */
         int config;
    
        /**
         * 算法，0表示默认算法，1表示BK算法,2表示易兆微算法，3表示易兆微8bit图
         */
         int algorithm;
    
        /**
         * 协议版本号。默认0，用于区分不同的表盘协议
         */
         int versionCode;
    
        /**
         * 客户信息
         */
         String customer;
    
        /**
         * 设备端表盘张数，用于弹出选择替换表盘位置框
         */
         int pictureNums;
    
        /**
         * 缩略图百分比
         */
         int thumbPercent;
    
        /**
         * 缩略图圆角
         */
         int thumbRoundAngle;
####      c 、必要参数体 WatchThemeDetailsResponse

## 20、混淆
    -keep class com.legend.bluetooth.fitprolib.model.** { *;}