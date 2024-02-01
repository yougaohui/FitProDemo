package xfkj.fitpro.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.legend.bluetooth.fitprolib.model.WatchThemeResponse;

import java.util.List;

import xfkj.fitpro.R;
import xfkj.fitpro.adapter.adapter.BaseHolder;
import xfkj.fitpro.adapter.adapter.DefaultAdapter;
import xfkj.fitpro.utils.glide.GlideUitls;

/**
 * 表盘列表适配器
 * Created by gaohui.you on 2020/6/12 0012
 * Email:839939978@qq.com
 */
public class ClockDialListAdapter extends DefaultAdapter<WatchThemeResponse> {

    public ClockDialListAdapter(List<WatchThemeResponse> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<WatchThemeResponse> getHolder(View v, int viewType) {
        return new HolderView(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_item_watch_theme_list;
    }

    public class HolderView extends BaseHolder<WatchThemeResponse> {

        ImageView mImg1;
        Context mContext;

        public HolderView(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
            mImg1 =  itemView.findViewById(R.id.img1);
        }

        @Override
        public void setData(WatchThemeResponse data, int position) {
            WatchThemeResponse.MaterialListBean preview = findPreview(data);
            GlideUitls.loadImgFromSever(preview.getUrl(),mImg1);
        }
    }

    private WatchThemeResponse.MaterialListBean findPreview(WatchThemeResponse response) {
        List<WatchThemeResponse.MaterialListBean> datas = response.getMaterialList();
        for (WatchThemeResponse.MaterialListBean data : datas) {
            if (StringUtils.equalsIgnoreCase(data.getName(), "PREVIEW.png")) {
                return data;
            }
        }
        return null;
    }
}
