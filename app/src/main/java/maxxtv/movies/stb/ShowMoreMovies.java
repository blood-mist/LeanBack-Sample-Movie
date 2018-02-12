package maxxtv.movies.stb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Adapters.MovieRecyclerViewAdapter;
import maxxtv.movies.stb.Entity.AddDataToFav;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Utils.Logger;

public class ShowMoreMovies extends AppCompatActivity {

    private RecyclerView moreRecyclerList;
    private TextView title;
    private ArrayList<Movie> moreMovieList;
    private Realm realm;
    private  MovieRecyclerViewAdapter showMoreAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm=Realm.getDefaultInstance();
        setContentView(R.layout.activity_show_more_movies);
        EventBus.getDefault().register(this);
        findViews();
        moreMovieList =getIntent().getParcelableArrayListExtra("movie_list");
        title.setText(getIntent().getStringExtra("activity_title"));
        addDataToRecyclerList(moreMovieList);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddDataToFav addData) {
        int movie_id=addData.getMovie_id();
        RealmQuery<Movie> movieQuery=realm.where(Movie.class).equalTo("movie_id",movie_id);
        Movie movie=movieQuery.findFirst();
        Log.d("movieSearch",movie+"");
        if(checkIfAlreadyExists(movie_id)){
            for(int i=0;i<moreMovieList.size();i++){
                if(moreMovieList.get(i).getMovie_id()==movie.getMovie_id()){
                    moreMovieList.remove(i);
                    break;
                }
            }
            Logger.d("fav_sizeRemoved",moreMovieList.size()+"");
        }else{
            moreMovieList.add(movie);
            Logger.d("fav_sizeadd",moreMovieList.size()+"");
        }
        showMoreAdapter=null;
        addDataToRecyclerList(moreMovieList);
    }

    private boolean checkIfAlreadyExists(int movie_id) {
        for(Movie movieItem:moreMovieList){
            if(movieItem.getMovie_id()==movie_id){
                return true;
            }
        }
        return false;
    }
    private void addDataToRecyclerList(ArrayList<Movie> searchMovieList) {
        showMoreAdapter =new MovieRecyclerViewAdapter(ShowMoreMovies.this,searchMovieList,moreRecyclerList);
        moreRecyclerList.setLayoutManager(new GridLayoutManager(ShowMoreMovies.this,7, LinearLayoutManager.VERTICAL,false));
        moreRecyclerList.setAdapter(showMoreAdapter);
    }

    private void findViews() {
        moreRecyclerList= (RecyclerView) findViewById(R.id.more_movie_list);
        title= (TextView) findViewById(R.id.movies_list_type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
}
