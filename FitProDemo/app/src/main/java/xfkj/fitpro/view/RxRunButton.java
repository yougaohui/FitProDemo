package xfkj.fitpro.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;


public class RxRunButton extends androidx.appcompat.widget.AppCompatButton {
    public RxRunButton(@NonNull  Context context) {
        super(context);
    }

    public RxRunButton(@NonNull  Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public RxRunButton(@NonNull  Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
