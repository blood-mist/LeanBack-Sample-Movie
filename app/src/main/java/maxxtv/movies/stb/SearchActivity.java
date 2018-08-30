package maxxtv.movies.stb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import maxxtv.movies.stb.Adapters.MovieRecyclerViewAdapter;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Utils.Logger;

public class SearchActivity extends AppCompatActivity {
private RecyclerView searchRecyclerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
        ArrayList<Movie> searchMovieList=getIntent().getParcelableArrayListExtra("search_movie_list");
        addDataToRecyclerList(searchMovieList);

    }

    private void addDataToRecyclerList(ArrayList<Movie> searchMovieList) {
        MovieRecyclerViewAdapter searchAdapter=new MovieRecyclerViewAdapter(SearchActivity.this,searchMovieList,searchRecyclerList);
        searchRecyclerList.setLayoutManager(new GridLayoutManager(SearchActivity.this,7, LinearLayoutManager.VERTICAL,false));
        searchRecyclerList.setAdapter(searchAdapter);
    }

    private void findViews() {
        searchRecyclerList= (RecyclerView) findViewById(R.id.search_movie_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public ApplicationMain getApp() {
        return (ApplicationMain) this.getApplication();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Logger.d("onUserInteraction", "User status changed");
        getApp().active();

    }

    private long mLastKeyDownTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long current = System. currentTimeMillis();
        boolean res = false;
        if (current - mLastKeyDownTime < 300 ) {
            res = true;
        } else {
            res = super.onKeyDown(keyCode, event);
            mLastKeyDownTime = current;
        }
        return res;
    }
}
