package xfkj.fitpro.model;


/**
 * 联系人
 * Created by gaohui.you on 2019/12/30 0030
 * Email:839939978@qq.com
 */
public class ContractModel {
    public static final String TAB_NAME = "ContractModel";

    int id;

    /**
     * 设备mac地址
     */
    String deviceId;

    /**
     * 联系人姓名
     */
    String contractName="";

    /**
     * 联系人号码
     */
    String phoneNumber;


    public ContractModel(String deviceId, String contractName, String phoneNumber) {
        this.deviceId = deviceId;
        this.contractName = contractName;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return this.id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getDeviceId() {
        return this.deviceId;
    }


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getContractName() {
        return this.contractName;
    }


    public void setContractName(String contractName) {
        this.contractName = contractName;
    }


    public String getPhoneNumber() {
        return this.phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "ContractModel{" +
                "deviceId='" + deviceId + '\'' +
                ", contractName='" + contractName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
