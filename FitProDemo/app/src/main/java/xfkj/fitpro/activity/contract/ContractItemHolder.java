package xfkj.fitpro.activity.contract;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;

import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.adapter.adapter.BaseHolder;
import xfkj.fitpro.model.ContractModel;
import xfkj.fitpro.utils.MySPUtils;

/**
 * 同步联系人容器
 * Created by gaohui.you on 2019/12/30 0030
 * Email:839939978@qq.com
 */
public class ContractItemHolder extends BaseHolder<ContractModel> {

    TextView mTvName;
    TextView mTvNumber;
    TextView mTvSos;

    private final boolean isSupportSOS = FitProSpUtils.isSupportSOSContract();

    public ContractItemHolder(View itemView, List<ContractModel> infos) {
        super(itemView);
        mTvName =  itemView.findViewById(R.id.tv_name);
        mTvNumber =  itemView.findViewById(R.id.tv_number);
        mTvSos =  itemView.findViewById(R.id.tv_sos);
        mTvSos.setOnClickListener(view -> {
            ContractModel data = infos.get(getAbsoluteAdapterPosition());
            Log.i(TAG, "sos phone number:" + data.toString());
            String phoneNumber = data.getPhoneNumber();
            if (!StringUtils.equalsIgnoreCase(phoneNumber, MySPUtils.getSOSContract())) {
                ((SynContractsActivity) (itemView.getContext())).setSOSContract(data.getPhoneNumber());
            }
        });
    }

    @Override
    public void setData(ContractModel data, int position) {
        mTvName.setText(data.getContractName());
        mTvNumber.setText(data.getPhoneNumber());
        if (isSupportSOS) {
            mTvSos.setVisibility(View.VISIBLE);
            mTvSos.setSelected(false);
            mTvSos.setSelected(StringUtils.equalsIgnoreCase(data.getPhoneNumber(), MySPUtils.getSOSContract()));
        } else {
            mTvSos.setVisibility(View.GONE);
        }
    }
}
