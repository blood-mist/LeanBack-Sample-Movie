package stb.androidtv.moviesleanback.customFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.app.HeadersSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.movieDetailsCustom.MovieCustomBrowseActivity;

/**
 * Created by Sebastiano Gottardo on 08/11/14.
 */
public class CustomHeadersFragment extends HeadersSupportFragment {

    private ArrayObjectAdapter adapter;
    private ArrayList<MovieItem> subCatmovieList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subCatmovieList=((MovieCustomBrowseActivity)getActivity()).getSubCatMovieList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        customSetBackground(R.color.fastlane_background);
        setOnHeaderViewSelectedListener(getDefaultItemSelectedListener());
        setHeaderAdapter();
        setCustomPadding();

    }


    private void setCustomPadding() {
        getVerticalGridView().setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams params = Objects.requireNonNull(getVerticalGridView()).getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height =ViewGroup.LayoutParams.WRAP_CONTENT;
        getVerticalGridView().requestLayout();
        Objects.requireNonNull(getVerticalGridView()).setPadding(0, dipToPixels(128), 0, 0);
    }
        public int dipToPixels(int dipValue) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
        }
    private void setHeaderAdapter() {
        adapter = new ArrayObjectAdapter();

        LinkedHashMap<Integer, CustomRowsFragment> fragments = ((MovieCustomBrowseActivity) getActivity()).getFragments();

        for (int i = 0; i < fragments.size(); i++) {
            MovieItem movieItem=subCatmovieList.get(i);
            HeaderItem header = new HeaderItem(movieItem.getId(), movieItem.getName());
            ArrayObjectAdapter innerAdapter = new ArrayObjectAdapter();
            innerAdapter.add(fragments.get(i));
            adapter.add(i, new ListRow(header, innerAdapter));
        }

        setAdapter(adapter);
    }

    private OnHeaderViewSelectedListener getDefaultItemSelectedListener() {
        return (viewHolder, row) -> {
            Object obj = ((ListRow) row).getAdapter().get(0);
            getFragmentManager().beginTransaction().replace(R.id.rows_container, (Fragment) obj).commit();
            ((MovieCustomBrowseActivity) getActivity()).updateCurrentRowsFragment((CustomRowsFragment) obj,getSelectedPosition());
        };
    }

    /**
     * Since the original setBackgroundColor is private, we need to
     * access it via reflection
     *
     * @param colorResource The colour resource
     */
    private void customSetBackground(int colorResource) {
        try {
            Class clazz = HeadersSupportFragment.class;
            Method m = clazz.getDeclaredMethod("setBackgroundColor", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(this, getResources().getColor(colorResource,null));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}