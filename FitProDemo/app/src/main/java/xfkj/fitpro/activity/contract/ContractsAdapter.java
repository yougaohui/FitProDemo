package xfkj.fitpro.activity.contract;

import android.view.View;

import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.adapter.adapter.BaseHolder;
import xfkj.fitpro.adapter.adapter.DefaultAdapter;
import xfkj.fitpro.model.ContractModel;

/**
 * 同步联系人适配器
 * Created by gaohui.you on 2019/12/30 0030
 * Email:839939978@qq.com
 */
public class ContractsAdapter extends DefaultAdapter<ContractModel> {

    public ContractsAdapter(List<ContractModel> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<ContractModel> getHolder(View v, int viewType) {
        return new ContractItemHolder(v,getInfos());
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_item_contract;
    }
}
