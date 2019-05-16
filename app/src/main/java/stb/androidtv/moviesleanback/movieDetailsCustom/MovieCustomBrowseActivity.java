package stb.androidtv.moviesleanback.movieDetailsCustom;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.SearchOrbView;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.customFragments.CustomFrameLayout;
import stb.androidtv.moviesleanback.customFragments.CustomHeadersFragment;
import stb.androidtv.moviesleanback.customFragments.CustomRowsFragment;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.TopmoviesItem;
import stb.androidtv.moviesleanback.search.SearchActivity;
import stb.androidtv.moviesleanback.utils.MovieEnum;

public class MovieCustomBrowseActivity extends FragmentActivity {
    private CustomHeadersFragment headersFragment;
    private CustomRowsFragment rowsFragment;
    private SearchOrbView orbView;
    private CustomFrameLayout customFrameLayout;
    private ArrayList<MovieItem> submovieList;
    private ArrayList<TopmoviesItem> topmoviesList;

    private boolean navigationDrawerOpen;
    private static final float NAVIGATION_DRAWER_SCALE_FACTOR = 0.9f;

    private final int CATEGORIES_NUMBER = 5;
    private LinkedHashMap<Integer, CustomRowsFragment> fragments;
    private int rowsPosition=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_movie_list_activity);
        orbView = findViewById(R.id.custom_search_orb);
        orbView.getRootView().setElevation(0f);
        orbView.setZ(0f);
        orbView.setOnOrbClickedListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        });

        if (MovieEnum.hasSubListData())
            submovieList = MovieEnum.getMovieSublist();
        if (MovieEnum.hasTopMovieData())
            topmoviesList = MovieEnum.getTopMovielist();
        fragments = new LinkedHashMap<>();

        for (int i = 0; i < submovieList.size(); i++) {
            CustomRowsFragment fragment = new CustomRowsFragment();
            fragments.put(i, fragment);
        }

        headersFragment = new CustomHeadersFragment();
        rowsFragment = fragments.get(0);

        customFrameLayout = (CustomFrameLayout) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        setupCustomFrameLayout();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(R.id.header_container, headersFragment, "CustomHeadersFragment")
                .replace(R.id.rows_container, rowsFragment, "CustomRowsFragment");
        transaction.commit();
    }
    public ArrayList<MovieItem> getSubCatMovieList(){
        return submovieList;
    }

    public ArrayList<TopmoviesItem> getTopmovieList() {
        return topmoviesList;
    }

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }

    public LinkedHashMap<Integer, CustomRowsFragment> getFragments() {
        return fragments;
    }
    private void setupCustomFrameLayout() {
        customFrameLayout.setOnChildFocusListener(new CustomFrameLayout.OnChildFocusListener() {
            @Override
            public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                if (headersFragment.getView() != null && headersFragment.getView().requestFocus(direction, previouslyFocusedRect)) {
                    return true;
                }
                return rowsFragment.getView() != null && rowsFragment.getView().requestFocus(direction, previouslyFocusedRect);
            }

            @Override
            public void onRequestChildFocus(View child, View focused) {
                int childId = child.getId();
                if (childId == R.id.rows_container) {
                    toggleHeadersFragment(false);
                } else if (childId == R.id.header_container) {
                    toggleHeadersFragment(true);
                }
            }
        });

        customFrameLayout.setOnFocusSearchListener((focused, direction) -> {
            if (direction == View.FOCUS_LEFT) {
                if (isVerticalScrolling() || navigationDrawerOpen) {
                    return focused;
                }
                return getVerticalGridView(headersFragment);
            } else if (direction == View.FOCUS_RIGHT) {
                if (isVerticalScrolling() || !navigationDrawerOpen) {
                    return focused;
                }
                return getVerticalGridView(rowsFragment);
            } else if (focused == orbView && direction == View.FOCUS_DOWN) {
                return navigationDrawerOpen ? getVerticalGridView(headersFragment) : getVerticalGridView(rowsFragment);
            } else if (focused != orbView && orbView.getVisibility() == View.VISIBLE && direction == View.FOCUS_UP) {
                return orbView;
            } else {
                return null;
            }
        });
    }
    public int dipToPixels(int dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public synchronized void toggleHeadersFragment(final boolean doOpen) {
        boolean condition = (doOpen != isNavigationDrawerOpen());
        if (condition) {
            final View headersContainer = (View) Objects.requireNonNull(headersFragment.getView()).getParent();
            final View rowsContainer = (View) Objects.requireNonNull(rowsFragment.getView()).getParent();

            final float delta = headersContainer.getWidth() * NAVIGATION_DRAWER_SCALE_FACTOR;

            // get current margin (a previous animation might have been interrupted)
            final int currentHeadersMargin = (((ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams()).leftMargin);
            final int currentRowsMargin = (((ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams()).leftMargin);

            // calculate destination
            final int headersDestination = (doOpen ? 0 : (int) (0 - delta));
            final int rowsDestination = (doOpen ? (dipToPixels(300)) : (int) (dipToPixels(300) - delta));

            // calculate the delta (destination - current)
            final int headersDelta = headersDestination - currentHeadersMargin;
            final int rowsDelta = rowsDestination - currentRowsMargin;

            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.MarginLayoutParams headersParams = (ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams();
                    headersParams.leftMargin = (int) (currentHeadersMargin + headersDelta * interpolatedTime);
                    headersContainer.setLayoutParams(headersParams);

                    ViewGroup.MarginLayoutParams rowsParams = (ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams();
                    rowsParams.leftMargin = (int) (currentRowsMargin + rowsDelta * interpolatedTime);
                    rowsContainer.setLayoutParams(rowsParams);
                }
            };

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    navigationDrawerOpen = doOpen;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!doOpen) {
                        rowsFragment.refresh();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}

            });

            animation.setDuration(200);
            ((View) rowsContainer.getParent()).startAnimation(animation);
        }
    }

    private boolean isVerticalScrolling() {
        try {
            // don't run transition
            return getVerticalGridView(headersFragment).getScrollState()
                    != HorizontalGridView.SCROLL_STATE_IDLE
                    || getVerticalGridView(rowsFragment).getScrollState()
                    != HorizontalGridView.SCROLL_STATE_IDLE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public VerticalGridView getVerticalGridView(Fragment fragment) {
        try {
            Class baseRowFragmentClass = getClassLoader().loadClass("android/support/v17/leanback/app/BaseRowSupportFragment");
            Method getVerticalGridViewMethod = baseRowFragmentClass.getDeclaredMethod("getVerticalGridView", null);
            getVerticalGridViewMethod.setAccessible(true);
            return (VerticalGridView) getVerticalGridViewMethod.invoke(fragment, null);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized boolean isNavigationDrawerOpen() {
        return navigationDrawerOpen;
    }

    public void updateCurrentRowsFragment(CustomRowsFragment fragment,int rowPosition) {
        rowsFragment = fragment;
        this.rowsPosition=rowPosition;
    }

    public int getCurrentRowPosition(){
        return rowsPosition;
    }
}
