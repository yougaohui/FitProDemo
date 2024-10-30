package xfkj.fitpro.db;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;

import java.util.ArrayList;
import java.util.List;

import xfkj.fitpro.application.MyApplication;
import xfkj.fitpro.model.ContractModel;

public class DBHelper {
    public static void saveContract(ContractModel mCurAddContractModel) {
        String deviceId = mCurAddContractModel.getDeviceId();
        String contractName = mCurAddContractModel.getContractName();
        String phoneNumber = mCurAddContractModel.getPhoneNumber();
        String insertData ="INSERT INTO ContractModel (deviceId, contractName, phoneNumber) "
                + "VALUES ('" + deviceId + "', '" + contractName + "', '" + phoneNumber + "');";
        MyApplication.DBAcces.Execute(String.format(insertData,deviceId, contractName, phoneNumber));
    }

    @SuppressLint("Range")
    public static List<ContractModel> getContracts() {
        Cursor cursor = MyApplication.DBAcces.Query("SELECT * FROM ContractModel WHERE deviceId = '" + FitProSpUtils.getBluetoothAddress() + "';");
        List<ContractModel> contractModelList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String deviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                String contractName = cursor.getString(cursor.getColumnIndex("contractName"));
                String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                contractModelList.add(new ContractModel(deviceId, contractName, phoneNumber));
            }
        }
        return contractModelList;
    }

    //根据phoneNumber删除ContractModel
    public static void deleteContract(ContractModel model) {
        MyApplication.DBAcces.Execute("DELETE FROM ContractModel WHERE phoneNumber = " + model.getPhoneNumber());
    }

    //删除所有ContractModel
    public static void deleteAllContract() {
        MyApplication.DBAcces.Execute("DELETE FROM ContractModel");
    }
}
