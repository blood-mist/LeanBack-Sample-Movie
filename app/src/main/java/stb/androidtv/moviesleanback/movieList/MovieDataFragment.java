package stb.androidtv.moviesleanback.movieList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Objects;

import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.cards.presenters.CardPresenterSelector;
import stb.androidtv.moviesleanback.cards.presenters.GridListRow;
import stb.androidtv.moviesleanback.cards.presenters.GridRowItemPresenter;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.enitities.TopmoviesItem;
import stb.androidtv.moviesleanback.models.Card;
import stb.androidtv.moviesleanback.utils.MovieEnum;

public class MovieDataFragment extends BrowseSupportFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private ArrayList<MovieItem> submovieList;
    private ArrayList<TopmoviesItem> topmoviesList;
    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private ArrayObjectAdapter mHeaderAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MovieEnum.hasSubListData())
            submovieList = MovieEnum.getMovieSublist();
        if (MovieEnum.hasTopMovieData())
            topmoviesList = MovieEnum.getTopMovielist();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();

        setupUIElements();

        setUpRows();

    }

    private void setUpRows() {
        mHeaderAdapter=new ArrayObjectAdapter(new GridRowItemPresenter());
        mRowsAdapter = new ArrayObjectAdapter(new GridRowItemPresenter());
        PresenterSelector cardPresenterSelector = new CardPresenterSelector(getContext());

        for (int i = 0; i < submovieList.size(); i++) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenterSelector);
            for (MoviesItem movies : submovieList.get(i).getMovies()) {
                Card card = new Card();
                card.setId(movies.getId());
                card.setDescription(movies.getDescription());
                card.setTitle(movies.getName());
                card.setImageUrl(movies.getMovieLogo());
                card.setType(Card.Type.GRID_SQUARE);
                listRowAdapter.add(card);
            }

                HeaderItem header = new HeaderItem(i, submovieList.get(i).getName());
                GridListRow headersRow = new GridListRow(header, listRowAdapter);
                GridListRow gridListRow=new GridListRow(i,null, listRowAdapter);
                gridListRow.setNumRows(2);
                mRowsAdapter.add(gridListRow);
                mHeaderAdapter.add(headersRow);

        }
        setAdapter(mRowsAdapter);
        getHeadersSupportFragment().setAdapter(mHeaderAdapter);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.movie)); // Badge, when set, takes precedent
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.search_opaque));
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(Objects.requireNonNull(getActivity()));
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }
}
