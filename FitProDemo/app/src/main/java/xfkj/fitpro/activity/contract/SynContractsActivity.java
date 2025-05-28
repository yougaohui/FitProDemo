package xfkj.fitpro.activity.contract;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.ContractNumEvent;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;
import com.legend.bluetooth.fitprolib.utils.LanguageUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.Map;

import xfkj.fitpro.R;
import xfkj.fitpro.base.NewBaseActivity;
import xfkj.fitpro.db.DBHelper;
import xfkj.fitpro.model.ContractModel;
import xfkj.fitpro.utils.DialogHelper;
import xfkj.fitpro.utils.MySPUtils;
import xfkj.fitpro.view.SpaceItemDecoration;


/**
 * 同步联系人
 * Created by gaohui.you on 2019/12/30 0030
 * Email:839939978@qq.com
 */
public class SynContractsActivity extends NewBaseActivity {

    ImageButton mImgBtnRight;
    SwipeMenuRecyclerView mContractList;

    ContractsAdapter mAdapter;

    private LeReceiver leReceiver;

    private final int DIALOG_WAIT_TIME = 15 * 1000;

    private final int DEFALUT_MAX_NUM = 8;
    private int mMaxNum = DEFALUT_MAX_NUM;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
            //添加联系人回调
            if (msg.what == Profile.MsgWhat.what19) {
                if (map.get("is_ok") != null && map.get("is_ok").equals("0")) {
                    ToastUtils.showShort(R.string.set_err);
                } else {//设置成功后一定要把联系人保存到本地，设备端不会回传联系人
                    ToastUtils.showShort(R.string.set);
                    if (mCurAddContractModel != null) {
                        //紧急联系人不存在自动默认一个
                        if (StringUtils.isTrimEmpty(MySPUtils.getSOSContract())) {
                            MySPUtils.saveSOSContract(mCurAddContractModel.getPhoneNumber());
                        }
                        mAdapter.getInfos().add(mCurAddContractModel);
                        mAdapter.notifyDataSetChanged();
                        DBHelper.saveContract(mCurAddContractModel);
                        mCurAddContractModel = null;
                    }
                }
                DialogHelper.hideDialog();
            } else if (msg.what == Profile.MsgWhat.what23) {//删除联系人回调
                if (map.get("is_ok") != null && map.get("is_ok").equals("0")) {
                    ToastUtils.showShort(R.string.set_err);
                } else {
                    ToastUtils.showShort(R.string.set);
                    if (mCurDeleteContract != null) {//删除成功后把本地联系人移除
                        mAdapter.getInfos().remove(mCurDeleteContract);
                        DBHelper.deleteContract(mCurDeleteContract);
                        //删除的sos的联系人，要移除数据库标记
                        if (StringUtils.equalsIgnoreCase(MySPUtils.getSOSContract(), mCurDeleteContract.getPhoneNumber())) {
                            if (!CollectionUtils.isEmpty(mAdapter.getInfos())) {//自动切换成第一个联系人号码
                                MySPUtils.saveSOSContract(mAdapter.getInfos().get(0).getPhoneNumber());
                            } else {//清除
                                MySPUtils.saveSOSContract("");
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        mCurDeleteContract = null;
                    }
                }
                DialogHelper.hideDialog();
            } else if (msg.what == Profile.MsgWhat.what24) {  //设置紧急联系人
                if (map.get("is_ok") != null && map.get("is_ok").equals("0")) {
                    ToastUtils.showShort(R.string.set_err);
                } else {
                    ToastUtils.showShort(R.string.set);
                    if (!StringUtils.isTrimEmpty(mSOSPhoneNumber)) {
                        MySPUtils.saveSOSContract(mSOSPhoneNumber);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                DialogHelper.hideDialog();
            }
        }
    };

    private ContractModel mCurAddContractModel;
    private ContractModel mCurDeleteContract;

    private Dialog mDialog;
    private EditText mEdtName, mEdtPhone;
    private String mSOSPhoneNumber = "";


    @Override
    public int getLayoutId() {
        return R.layout.activity_syn_contracts;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle("常用联系人");
        mImgBtnRight = findViewById(R.id.img_btn_right);
        mContractList = findViewById(R.id.contract_list);
        //初始化通信广播
        leReceiver = new LeReceiver(mContext, mHandler);
        mAdapter = new ContractsAdapter(DBHelper.getContracts());
        mContractList.setSwipeMenuCreator(swipeMenuCreator);
        mContractList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mContractList.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mContractList.addItemDecoration(new SpaceItemDecoration(5));
        mContractList.setAdapter(mAdapter);
        initBottomDialog();
        syncContractStatus();
    }

    @Override
    public void initListener() {
        mImgBtnRight.setVisibility(View.VISIBLE);
        mImgBtnRight.setImageResource(R.mipmap.add_alarm);
        mImgBtnRight.setOnClickListener(v -> {
            if (!isOutLimit()) {
                showBottomDialog();
            } else {
                ToastUtils.showShort(R.string.contract_outlimited);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (leReceiver != null) leReceiver.registerLeReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (leReceiver != null) leReceiver.unregisterLeReceiver();
    }

    public static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_PHONE_NUMBER:
                if (data != null) {
                    Uri contactUri = data.getData();
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                    Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String name = cursor.getString(nameIndex);      //联系人姓名
                        String number = cursor.getString(numberIndex);  //联系人号码
                        cursor.close();
                        mEdtName.setText(StringUtils.isEmpty(name) ? name : name.replaceAll("_", ""));
                        mEdtPhone.setText(StringUtils.isEmpty(number) ? number : number.replaceAll("_", "").replaceAll(" ", ""));
                        break;
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean addPhoneNum(String name, String number) {

        if (StringUtils.isEmpty(name)) {
            name = "";
        }

        if (StringUtils.isEmpty(number)) {
            ToastUtils.showShort(R.string.please_input_coreect_num);
            return false;
        }

        //姓名和号码里面不能包含下划线
        name = name.replaceAll("_", "");
        number = number.replaceAll("_", "").replaceAll(" ", "");

        if (isOutForNameBytes(name.getBytes())) {
            ToastUtils.showShort(R.string.input_content_name_out_limit);
            return false;
        }

        if (isOutForNumberBytes(number.getBytes())) {
            ToastUtils.showShort(R.string.input_content_phone_out_limit);
            return false;
        }


        if (isContainsPhoneNumber(number)) {
            ToastUtils.showShort(R.string.already_exsit_contract);
            return false;
        }

        if (SDKCmdMannager.isConnected()) {
            DialogHelper.showDialog(mContext, R.string.setting, DIALOG_WAIT_TIME, false);
            mCurAddContractModel = new ContractModel(FitProSpUtils.getBluetoothAddress(), name, number);
            SDKCmdMannager.addContact(mCurAddContractModel.getContractName(), mCurAddContractModel.getPhoneNumber());
        } else {
            ToastUtils.showShort(R.string.unconnected);
        }
        return true;
    }

    /**
     * 判断名字长度是否超
     *
     * @param nameBytes
     * @return
     */
    private boolean isOutForNameBytes(byte[] nameBytes) {
        int length = nameBytes.length;
        Log.i(TAG, "nameBytes Length:" + length);
        return length > 20;
    }

    /**
     * 号码是否超长度
     *
     * @param numberBytes
     * @return
     */
    private boolean isOutForNumberBytes(byte[] numberBytes) {
        int length = numberBytes.length;
        Log.i(TAG, "numberBytes Length:" + length);
        return length > 20;
    }

    /**
     * 删除电话号码
     *
     * @param phoneNumber
     */
    private void deletePhoneNum(String phoneNumber) {
        if (SDKCmdMannager.isConnected()) {
            DialogHelper.showDialog(mContext, "正在删除...", DIALOG_WAIT_TIME, false);
            SDKCmdMannager.deleteContact(phoneNumber);
        } else {
            ToastUtils.showShort(R.string.unconnected);
        }
    }

    public void setSOSContract(String phoneNumber) {
        if (SDKCmdMannager.isConnected()) {
            mSOSPhoneNumber = phoneNumber;
            DialogHelper.showDialog(mContext, R.string.setting, DIALOG_WAIT_TIME, false);
            SDKCmdMannager.setSOS(phoneNumber);
        } else {
            ToastUtils.showShort(R.string.unconnected);
        }
    }

    /**
     * 获取联系人状态
     */
    private void syncContractStatus() {
        if (SDKCmdMannager.isConnected()) {
            SDKCmdMannager.syncContractStatus();
        } else {
            ToastUtils.showShort(R.string.unconnected);
        }
    }

    /**
     * 最多存10个联系人
     *
     * @return
     */
    private boolean isOutLimit() {
        return mAdapter.getInfos().size() >= mMaxNum;
    }

    /**
     * 检查号码是否已经存在
     *
     * @return
     */
    private boolean isContainsPhoneNumber(String phoneNumber) {
        for (ContractModel info : mAdapter.getInfos()) {
            if (StringUtils.equalsIgnoreCase(info.getPhoneNumber(), phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = SizeUtils.dp2px(70);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (LanguageUtils.isArabic()) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setBackground(R.drawable.selector_red).setImage(R.mipmap.equipment_fc_delete_icon).setWidth(width).setHeight(height);
            swipeLeftMenu.addMenuItem(deleteItem);// 添加菜单到左侧。
        } else {
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setBackground(R.drawable.selector_red).setImage(R.mipmap.equipment_fc_delete_icon).setWidth(width).setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int position = menuBridge.getAdapterPosition();
            mCurDeleteContract = mAdapter.getItem(position);
            deletePhoneNum(mCurDeleteContract.getPhoneNumber());
        }
    };

    private void initBottomDialog() {
        if (mDialog == null) {
            //1、使用Dialog、设置style
            mDialog = new Dialog(this, R.style.DialogTheme);
            //2、设置布局
            View view = View.inflate(this, R.layout.layout_dialog_add_contract, null);

            mDialog.setContentView(view);

            mEdtName = mDialog.findViewById(R.id.edt_name);
            mEdtPhone = mDialog.findViewById(R.id.edt_phone);

            Window window = mDialog.getWindow();

            //设置弹出位置
            window.setGravity(Gravity.BOTTOM);
            //设置对话框大小
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mDialog.findViewById(R.id.import_contract).setOnClickListener(v -> {
                if (PermissionUtils.isGranted(Manifest.permission.READ_CONTACTS)) {
                    selectContact();
                } else {
                    PermissionUtils.permission(PermissionConstants.CONTACTS).callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            selectContact();
                        }

                        @Override
                        public void onDenied() {
                            ToastUtils.showShort("权限被拒");
                        }
                    }).request();
                }
            });

            mDialog.findViewById(R.id.btn_cancel).setOnClickListener(view1 -> mDialog.dismiss());

            mDialog.findViewById(R.id.btn_ok).setOnClickListener(view12 -> {
                if (addPhoneNum(mEdtName.getText().toString(), mEdtPhone.getText().toString())) {
                    mDialog.dismiss();
                }
            });
        }
    }

    private void showBottomDialog() {
        if (mDialog != null && !mDialog.isShowing()) {
            mEdtName.setText("");
            mEdtPhone.setText("");
            mDialog.show();
        }
    }

    @Override
    public void onMessageEvent(Object event) {
        super.onMessageEvent(event);
        if (event instanceof ContractNumEvent) {
            ContractNumEvent contractEvent = ((ContractNumEvent) event);
            int num = contractEvent.getNum();//设备端已有的联系人个数
            int maxNum = contractEvent.getMaxNum();//最大联系人个数
            if (num <= 0) {
                if (!CollectionUtils.isEmpty(DBHelper.getContracts())) {
                    mAdapter.getInfos().clear();
                    mAdapter.notifyDataSetChanged();
                    MySPUtils.saveSOSContract("");
                    DBHelper.deleteAllContract();
                }
            }

            if (maxNum > 0) {
                mMaxNum = maxNum;
            } else {//等于0的时候默认8个联系人
                mMaxNum = DEFALUT_MAX_NUM;
            }
            Log.e(TAG, "===============>>Contract num:" + num);
        }
    }
}
