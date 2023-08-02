package xfkj.fitpro.view;

import android.content.Context;
import android.util.AttributeSet;


/**
 * @author vondear
 * @date 2016/6/28
 */
public class RxRunTextView extends androidx.appcompat.widget.AppCompatTextView {
    public RxRunTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RxRunTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxRunTextView(Context context) {
        super(context);
    }

    /**
     * 当前并没有焦点，我只是欺骗了Android系统
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
