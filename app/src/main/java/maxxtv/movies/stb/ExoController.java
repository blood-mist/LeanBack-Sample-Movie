package maxxtv.movies.stb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;

public class ExoController extends PlayerControlView {
    public ExoController(Context context) {
        super(context);
    }

    public ExoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExoController(Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr, playbackAttrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
