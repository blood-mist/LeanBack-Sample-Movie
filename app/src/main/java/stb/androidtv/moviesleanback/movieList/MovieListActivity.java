package stb.androidtv.moviesleanback.movieList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import stb.androidtv.moviesleanback.R;

public class MovieListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        if (savedInstanceState == null) {
            Fragment fragment = new MovieDataFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movieDataContainer, fragment)
                    .commit();
        }
    }
}
