package maxxtv.movies.stb.Utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import javax.annotation.Nullable;

public class FocusFixFrameLayout extends FrameLayout {

    public FocusFixFrameLayout(@NonNull Context context) {
        super(context);
    }

    public FocusFixFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusFixFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FocusFixFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void clearFocus() {
        if (this.getParent() != null)
            super.clearFocus();
    }
}