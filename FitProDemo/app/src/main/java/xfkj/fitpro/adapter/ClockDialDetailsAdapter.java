package xfkj.fitpro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.legend.bluetooth.fitprolib.model.WatchThemeDetailsResponse;

import java.util.List;

import butterknife.BindView;
import xfkj.fitpro.R;
import xfkj.fitpro.adapter.adapter.BaseHolder;
import xfkj.fitpro.adapter.adapter.DefaultAdapter;
import xfkj.fitpro.utils.glide.GlideUitls;

/**
 * 表盘列表适配器
 * Created by gaohui.you on 2020/6/12 0012
 * Email:839939978@qq.com
 */
public class ClockDialDetailsAdapter extends DefaultAdapter<WatchThemeDetailsResponse.MaterialListBean> {

    String mBgUrl;

   public HolderView mHolderView;

    public ClockDialDetailsAdapter(List<WatchThemeDetailsResponse.MaterialListBean> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<WatchThemeDetailsResponse.MaterialListBean> getHolder(View v, int viewType) {
        mHolderView = new HolderView(v);
        return mHolderView;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_clock_dial_details;
    }

    public void setBgUrl(String bgUrl) {
        mBgUrl = bgUrl;
    }

    public String getBgUrl() {
        return mBgUrl;
    }

    public class HolderView extends BaseHolder<WatchThemeDetailsResponse.MaterialListBean> {

        private final View mItemView;

        @BindView(R.id.img_bg)
        ImageView mImgBg;
        @BindView(R.id.img_front)
        ImageView mImgFront;

        Context mContext;

        public HolderView(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mContext = itemView.getContext();
        }

        @Override
        public void setData(WatchThemeDetailsResponse.MaterialListBean data, int position) {
            GlideUitls.loadImgFromSever(mBgUrl,mImgBg);
            GlideUitls.loadImgFromSever(data.getUrl(), mImgFront);
            Log.e(TAG, "mImgBg w:" + mImgBg.getWidth() + ";h:" + mImgBg.getHeight());
        }

        public int[] getWH(){
            int[] wh = new int[2];
            wh[0] = mItemView.getWidth();
            wh[1] = mItemView.getHeight();
            return wh;
        }
    }
}
