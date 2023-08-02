package xfkj.fitpro.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xfkj.fitpro.R;

/**
 * 使用 BaseDialogFragment 自定义自己需要的对话框
 */
public abstract class BaseDialogFragment extends DialogFragment {

    // ButterKnife Unbinder
    private Unbinder unbinder = null;

    // 参数配置
    private Builder builder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        builder = builder();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
            dialog.setCancelable(builder.isCancel);

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(builder.width, builder.height);
                window.setGravity(builder.gravity);
                window.setWindowAnimations(builder.animation);
                window.setAttributes(getLayoutParams(window));
            }
        }
    }

    private WindowManager.LayoutParams getLayoutParams(Window window) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = builder.dimAmount;
        layoutParams.x = builder.x;
        layoutParams.y = builder.y;
        return layoutParams;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(builder.style, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        // 绑定 ButterKnife
        unbinder = ButterKnife.bind(this, view);

        // View 的初始化可以放到这里执行
        this.create(savedInstanceState, view);

        return view;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null && unbinder != Unbinder.EMPTY) {
            try {
                unbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        super.onDestroyView();
    }

    /**
     * 拆分onCreateView，提供一个create方法，View的初始化可以放到这里执行，必须在子类中实现此方法
     */
    public abstract void create(Bundle savedInstanceState, View view);

    /**
     * 获取布局文件，必须在子类中实现此方法
     */
    public abstract int getLayoutId();

    /**
     * 参数配置，可重写此方法添加参数配置
     */
    protected Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /**
         * STYLE_NORMAL：会显示一个普通的dialog
         * STYLE_NO_TITLE：不带标题的dialog
         * STYLE_NO_FRAME：无框的dialog
         * STYLE_NO_INPUT：无法输入内容的dialog，即不接收输入的焦点，而且触摸无效。
         */
        private int style = DialogFragment.STYLE_NO_FRAME;

        private int width = LinearLayout.LayoutParams.MATCH_PARENT;
        private int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // 窗体相对位置
        private int gravity = Gravity.TOP;

        // 进入退出动画
        private int animation = R.style.DialogEmptyAnimation;

        // 点击外部是否关闭对话框，默认关闭
        private boolean canceledOnTouchOutside = false;

        // 背景明暗
        private float dimAmount = 0.5f;

        // 坐标位置
        public int x;
        public int y;

        public boolean isCancel = true;

        public Builder() {
        }

        public Builder style(int style) {
            this.style = style;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder animation(int animation) {
            this.animation = animation;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder dimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder isCancel(boolean isCancel) {
            this.isCancel = isCancel;
            return this;
        }
    }

    @Override
    public void dismiss() {
        FragmentManager manager = getParentFragmentManager();
        if (manager.isStateSaved()) {
            return;
        }
        super.dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed())
                return;
        }
        if (manager.isStateSaved()) {
            return;
        }
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commit();
            super.show(manager, tag);
        } catch (Exception e) {
            //同一实例使用不同的tag会异常，这里捕获一下
            e.printStackTrace();
        }
    }
}