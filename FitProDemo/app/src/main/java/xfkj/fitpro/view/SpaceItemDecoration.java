package xfkj.fitpro.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * recycerView行间距
 * Created by gaohui.you on 2018/4/14 0014
 * Email:839939978@qq.com
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final String TAG = SpaceItemDecoration.class.getSimpleName();
    private int mBottomSpace;
    private int mLeftSpace;
    private int mRightSpace;
    private int mTopSpace;

    public SpaceItemDecoration(int bottomSpace) {
        mBottomSpace = ConvertUtils.dp2px(bottomSpace);
    }

    public SpaceItemDecoration(int bottomSpace, int leftSpace) {
        this.mBottomSpace = ConvertUtils.dp2px(bottomSpace);
        this.mLeftSpace = ConvertUtils.dp2px(leftSpace);
    }

    public SpaceItemDecoration(int bottomSpace, int leftSpace, int rightSpace) {
        this.mBottomSpace = ConvertUtils.dp2px(bottomSpace);
        this.mLeftSpace = ConvertUtils.dp2px(leftSpace);
        this.mRightSpace = ConvertUtils.dp2px(rightSpace);
    }

    public SpaceItemDecoration(int bottomSpace, int leftSpace, int rightSpace, int topSpace) {
        this.mBottomSpace = ConvertUtils.dp2px( bottomSpace);
        this.mLeftSpace = ConvertUtils.dp2px(leftSpace);
        this.mRightSpace = ConvertUtils.dp2px(rightSpace);
        this.mTopSpace = ConvertUtils.dp2px(topSpace);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (itemPosition < 0)
            return;
        outRect.left = mLeftSpace;
        outRect.right = mRightSpace;
        outRect.bottom = mBottomSpace;
        outRect.top = mTopSpace;

    }
}
