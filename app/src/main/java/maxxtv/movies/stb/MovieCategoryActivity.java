package maxxtv.movies.stb;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Adapters.CategoryRecyclerViewAdapter;
import maxxtv.movies.stb.Adapters.MovieRecyclerViewAdapter;
import maxxtv.movies.stb.Async.SearchAsync;
import maxxtv.movies.stb.Entity.AddDataToFav;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.MovieCategoryParent;
import maxxtv.movies.stb.Interface.SearchCallback;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.MovieEnum;
import maxxtv.movies.stb.Utils.common.LinkConfig;

public class MovieCategoryActivity extends AppCompatActivity implements SearchCallback {
    private RecyclerView category_list, top_list, playlist_list;
    private TextView noTopMovie, noFavMovie;
    private EditText search_text;
    private Button searchBtn;
    private Realm realm;
    ArrayList<Movie> myList;
    private MovieRecyclerViewAdapter playlist_adapter;
    private String searched_movie;
    private String authToken;
    private SearchAsync searchAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        authToken = LoginFileUtils.getAuthTokenFromFile();
        findViews();
        myList = getIntent().getParcelableArrayListExtra("watchList");
        addItemtoCategoryList();
        addItemtoTopList();
        addItemtoPlaylist();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchAsync != null) {
            searchAsync.cancel(true);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddDataToFav addData) {
        int movie_id = addData.getMovie_id();
        RealmQuery<Movie> movieQuery = realm.where(Movie.class).equalTo("movie_id", movie_id);
        Movie movie = movieQuery.findFirst();
        Log.d("movieSearch", movie + "");
        Logger.d("fav_size", myList.size() + "");
        if (checkIfAlreadyExists(movie_id)) {
            for (int i = 0; i < myList.size(); i++) {
                if (myList.get(i).getMovie_id() == movie.getMovie_id()) {
                    myList.remove(i);
                    break;
                }
            }
            Logger.d("fav_sizeRemoved", myList.size() + "");
        } else {
            myList.add(movie);
            Logger.d("fav_sizeadd", myList.size() + "");
        }
        playlist_adapter = null;
        addItemtoPlaylist();
    }

    private boolean checkIfAlreadyExists(int movie_id) {
        for (Movie movieItem : myList) {
            if (movieItem.getMovie_id() == movie_id) {
                return true;
            }
        }
        return false;
    }

    private void addItemtoPlaylist() {
        ArrayList<Movie> fixedFavArrayList = new ArrayList<>();
        if (myList.size() == 0) {
            noFavMovie.setVisibility(View.VISIBLE);
            playlist_list.setVisibility(View.GONE);
        } else {
            int addObjectCount = 0;
            Iterator<Movie> favListsIterator = myList.listIterator(0);
            while (favListsIterator.hasNext() && addObjectCount < 4) {
                Movie movie = favListsIterator.next();
                fixedFavArrayList.add(movie);
                addObjectCount++;
            }
            if (myList.size() > 4) {
                Movie plusmovie = new Movie();
                fixedFavArrayList.remove(3);
                plusmovie.setMovie_url("");
                fixedFavArrayList.add(plusmovie);
            }

            playlist_adapter = new MovieRecyclerViewAdapter(this, myList, fixedFavArrayList, playlist_list, "My Watchlist");
            playlist_list.setLayoutManager(new LinearLayoutManager(MovieCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false));
            playlist_list.setAdapter(playlist_adapter);
            playlist_adapter.notifyDataSetChanged();
            noFavMovie.setVisibility(View.GONE);
            playlist_list.setVisibility(View.VISIBLE);
        }
     /*   playlist_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int h1 = playlist_list.getWidth();
                int h2 = view.getWidth();
                playlist_list.smoothScrollToPositionFromOffset(i, h1 / 2 - h2 / 2, 2000);
                if(playlist_list.hasFocus()) {
                    playlist_adapter.setSelectedPosition(i);
                    playlist_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        playlist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent favMoviePlayIntent=new Intent(MovieCategoryActivity.this,MoviePlayCustomController.class);
                favMoviePlayIntent.putExtra("currentMovieId",myList.get(i).getMovie_id());
                favMoviePlayIntent.putParcelableArrayListExtra("movie_list",myList);
                startActivity(favMoviePlayIntent);
            }
        });
        playlist_list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    playlist_adapter.setSelectedPosition(-1);
                    playlist_adapter.notifyDataSetChanged();
                }
            }
        });*/


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

    @Override
    public void onBackPressed() {

        if (searchAsync != null && searchAsync.getStatus() == android.os.AsyncTask.Status.RUNNING) {
            searchAsync.cancel(true);
        } else
            super.onBackPressed();
    }

    private void addItemtoTopList() {
        final ArrayList<Movie> topArrayList = getIntent().getParcelableArrayListExtra("topMovieList");
        if (topArrayList.size() == 0) {
            top_list.setVisibility(View.GONE);
            noTopMovie.setVisibility(View.VISIBLE);

        } else {
            ArrayList<Movie> fixedtopArrayList = new ArrayList<>();
            int addObjectCount = 0;
            Iterator<Movie> topListsIterator = topArrayList.listIterator(0);
            while (topListsIterator.hasNext() && addObjectCount < 4) {
                Movie movie = topListsIterator.next();
                fixedtopArrayList.add(movie);
                addObjectCount++;
            }
            if (topArrayList.size() > 4) {
                Movie plusmovie = new Movie();
                plusmovie.setMovie_url("");
                fixedtopArrayList.add(plusmovie);
            }

            final MovieRecyclerViewAdapter toplist_adapter = new MovieRecyclerViewAdapter(this, topArrayList, fixedtopArrayList, top_list, getString(R.string.new_movies));
            top_list.setLayoutManager(new LinearLayoutManager(MovieCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false));
            top_list.setAdapter(toplist_adapter);
            noTopMovie.setVisibility(View.GONE);
            top_list.setVisibility(View.VISIBLE);
        }
       /* top_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topMoviePlayIntent=new Intent(MovieCategoryActivity.this,MoviePlayCustomController.class);
                topMoviePlayIntent.putExtra("currentMovieId",topArrayList.get(i).getMovie_id());
                topMoviePlayIntent.putParcelableArrayListExtra("movie_list",topArrayList);
                startActivity(topMoviePlayIntent);
            }
        });
        top_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int h1 = top_list.getWidth();
                int h2 = view.getWidth();
                top_list.smoothScrollToPositionFromOffset(i, h1 / 2 - h2 / 2, 2000);
                if(top_list.hasFocus()) {
                    toplist_adapter.setSelectedPosition(i);
                    toplist_adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        top_list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    toplist_adapter.setSelectedPosition(-1);
                    toplist_adapter.notifyDataSetChanged();
                }
            }
        });*/
    }

    private void addItemtoCategoryList() {
        final ArrayList<MovieCategoryParent> movieList = getIntent().getParcelableArrayListExtra("movieParentList");
        category_list.setLayoutManager(new LinearLayoutManager(MovieCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false));
        final CategoryRecyclerViewAdapter category_adapter = new CategoryRecyclerViewAdapter(this, movieList, category_list);
        category_list.setAdapter(category_adapter);
        category_list.requestFocus();
    }

    private void findViews() {
        category_list = (RecyclerView) findViewById(R.id.movie_categoriies_list);
        top_list = (RecyclerView) findViewById(R.id.top_movies_list);
        playlist_list = (RecyclerView) findViewById(R.id.user_playlist_list);
        noTopMovie = (TextView) findViewById(R.id.no_topMovies);
        noFavMovie = (TextView) findViewById(R.id.no_favMovies);
        search_text = (EditText) findViewById(R.id.search_text);
        searchBtn = (Button) findViewById(R.id.btn_search);
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;
                    //Perform your Actions here.
                    if (search_text.getText().toString().trim().equals("".trim())) {
                        Toast.makeText(MovieCategoryActivity.this, "Please enter text to search", Toast.LENGTH_SHORT).show();
                        search_text.requestFocus();
                    } else {
                        loadSearchAsyn();
                    }

                }
                return handled;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_text.getText().toString().trim().equals("".trim())) {
                    Toast.makeText(MovieCategoryActivity.this, "Please enter text to search", Toast.LENGTH_SHORT).show();
                    search_text.requestFocus();
                } else {
                    loadSearchAsyn();
                }
            }

        });

    }

    private void loadSearchAsyn() {
        searched_movie = search_text.getText().toString();
        if (searchAsync != null)
            searchAsync.cancel(true);
        searchAsync = new SearchAsync(MovieCategoryActivity.this, MovieCategoryActivity.this, authToken);
        searchAsync.execute(LinkConfig.getString(MovieCategoryActivity.this, R.string.search_url), searched_movie);


    }


    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.giec.settings");
                    startActivity(LaunchIntent);
                    finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }


        } catch (Exception a) {
            startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void getSearchMovies(String s, final CustomDialogManager loading) {
        Log.d("movies", s);
        if (s.equalsIgnoreCase(DownloadUtil.NotOnline) || s.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
            loading.dismiss();
            final CustomDialogManager noInternet = new CustomDialogManager(this, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.setTitle(getString(R.string.no_internet_title));
            noInternet.setMessage("", getString(R.string.no_internet_body));
            noInternet.dismissDialogOnBackPressed();
            noInternet.getInnerObject().setCancelable(false);
            noInternet.setExtraButton(this.getString(R.string.btn_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noInternet.dismiss();
                }
            });
            noInternet.setPositiveButton("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternet.dismiss();
                    loadSearchAsyn();
                }
            });
            noInternet.setNegativeButton("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSetting();
                }
            });
            noInternet.show();
        } else {
            if (s.equals("")) {
                loading.dismiss();
                final CustomDialogManager manager = new CustomDialogManager(MovieCategoryActivity.this, CustomDialogManager.MESSAGE);
                manager.build();
                manager.setTitle("Movie Not Found");
                manager.setMessage("Movie Not Found ", "Requested movie couldn\'t be found.");
                manager.addDissmissButtonToDialog();
                manager.dismissDialogOnBackPressed();
                manager.setExtraButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manager.dismiss();
                    }
                });
                manager.show();
                search_text.requestFocus();
            } else {
                final ArrayList<Movie> searchmovieList = new ArrayList<>();
                try {
                    JSONObject searchMoiveJobj = new JSONObject(s);
                    final JSONArray searchJArray = searchMoiveJobj.getJSONArray("movies");

                    if (searchJArray.length() == 0) {
                        loading.dismiss();
                        final CustomDialogManager manager = new CustomDialogManager(MovieCategoryActivity.this, CustomDialogManager.MESSAGE);
                        manager.build();
                        manager.setTitle("Movie Not Found");
                        manager.dismissDialogOnBackPressed();
                        manager.setMessage("Movie Not Found ", " Requested movie couldn\'t be found.");
                        manager.addDissmissButtonToDialog();
                        manager.setExtraButton("", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                manager.dismiss();
                            }
                        });
                        manager.show();
                        search_text.requestFocus();
                    } else {
                        Thread searchThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final Realm searchRealm = Realm.getDefaultInstance();
                                    for (int i = 0; i < searchJArray.length(); i++) {
                                        JSONObject insideObj = searchJArray.getJSONObject(i);
                                        final Movie searchMovie = new Movie();
                                        searchMovie.setMovie_id(insideObj.getInt("id"));
                                        searchMovie.setMovie_name(insideObj.getString("name"));
                                        try {
                                            searchMovie.setIsFav(Integer.parseInt(insideObj.getString("isFav")));
                                        } catch (Exception e) {
                                            searchMovie.setIsFav(0);
                                        }
                                        searchMovie.setIs_Imdb(Integer.parseInt(insideObj.getString("imdbID")));
                                        searchMovie.setImdb_id(String.valueOf(insideObj.getInt("movie_id")));
                                        searchMovie.setMovie_url(insideObj.getString("movie_url"));
                                        searchMovie.setParental_lock(Integer.parseInt(insideObj.getString("parental_lock")));
                                        Movie movie = searchRealm.where(Movie.class).equalTo("movie_id", insideObj.getInt("id")).findFirst();
                                        if (movie != null) {
                                            if (movie.getParental_lock() == 1)
                                                searchMovie.setParental_lock(1);
                                        }
                                        searchMovie.setMovie_description(insideObj.getString("description"));
                                        searchMovie.setMovie_logo(insideObj.getString("movie_logo"));
                                        searchMovie.setIs_youtube(Integer.parseInt(insideObj.getString("is_youtube")));
                                        searchMovie.setPreview_url(insideObj.getString("preview_url"));
                                        searchMovie.setMovie_category_id(Integer.parseInt(insideObj.getString("movie_category_id")));
                                        searchmovieList.add(searchMovie);

                                        searchRealm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(@NonNull Realm realm) {
                                                realm.insertOrUpdate(searchMovie);
                                            }
                                        });
                                    }
                                    searchRealm.close();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showMoviesinActivity(searchmovieList,loading);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        searchThread.start();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        JSONObject root = new JSONObject(s);
                        if (root.getString("error_code").equals("405")) {
                            LinkConfig.deleteAuthCodeFile();
                            final CustomDialogManager invalidTokenDialog = new CustomDialogManager(MovieCategoryActivity.this, CustomDialogManager.ALERT);
                            invalidTokenDialog.build();
                            invalidTokenDialog.setTitle("Invalid Token");
                            invalidTokenDialog.setMessage("", root.getString("message") + ",please re-login");
                            invalidTokenDialog.getInnerObject().setCancelable(false);
                            invalidTokenDialog.exitApponBackPress();
                            invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent entryPointIntent = new Intent(MovieCategoryActivity.this, EntryPoint.class);
                                    entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    invalidTokenDialog.dismiss();
                                    startActivity(entryPointIntent);


                                }
                            });
                            invalidTokenDialog.show();


                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(MovieCategoryActivity.this);
                    }
                }
            }
        }
    }

    private void showMoviesinActivity(ArrayList<Movie> searchmovieList, CustomDialogManager loading) {
        Intent searchintent = new Intent(MovieCategoryActivity.this, SearchActivity.class);
        MovieEnum.setData(searchmovieList);
        startActivity(searchintent);
        loading.dismiss();
    }
}
