package stb.androidtv.moviesleanback.loadingSpinner;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import stb.androidtv.moviesleanback.R;

public class LoadingFragment extends Fragment {
    private static final String TAG = LoadingFragment.class.getSimpleName();

    private static final int SPINNER_WIDTH = 100;
    private static final int SPINNER_HEIGHT = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProgressBar progressBar = new ProgressBar(container.getContext());
        if (container instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER);
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress_drawable));
            progressBar.setLayoutParams(layoutParams);
        }
        return progressBar;
    }
}
