package maxxtv.movies.stb;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import maxxtv.movies.stb.Adapters.MovieRecyclerViewAdapter;
import maxxtv.movies.stb.Adapters.SubCategoryRecyclerAdapter;
import maxxtv.movies.stb.Async.LoadMovieAsync;
import maxxtv.movies.stb.Async.SearchAsync;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.SubCategoryName;
import maxxtv.movies.stb.Fragments.SubCatFragment;
import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Interface.SearchCallback;
import maxxtv.movies.stb.Parser.MovieSubCategoryParser;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.MovieEnum;
import maxxtv.movies.stb.Utils.common.DataUtils;
import maxxtv.movies.stb.Utils.common.LinkConfig;

public class MovieListActivity extends AppCompatActivity implements AsyncBack, SubCatFragment.OnFragmentInteractionListener, SearchCallback {
    private TextView top_movies, noMovies, noCategory, noTopMovie;
    private RecyclerView top_movielist, subcategory_list;
    private LinearLayout subcategorylayout, contentlayout;
    private FrameLayout loadingLayout;
    private LoadMovieAsync loadMovies;
    private SearchView movieSearchView;
    private EditText search_text;
    private Button searchBtn;
    SubCategoryRecyclerAdapter subcatAdapter;
    private Realm realm;
    private String searchedMovie;
    private String authToken;
    private boolean hasTopMovie = false;
    private FrameLayout subcategory_container;
    private SearchAsync searchMoviesTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        authToken = LoginFileUtils.getAuthTokenFromFile();
        findViews();
        search_text.setNextFocusRightId(searchBtn.getId());
        realm = Realm.getDefaultInstance();
        loadingLayout.setVisibility(View.VISIBLE);
        contentlayout.setVisibility(View.GONE);
        loadMovies = new LoadMovieAsync(this, this, authToken);
        loadMovies.execute(LinkConfig.getString(this, LinkConfig.MOVIE_CATEGORY_DETAIL) + "?parentId=" + getIntent().getIntExtra("parent_id", 0));
    }


    private void findViews() {
        contentlayout = (LinearLayout) findViewById(R.id.content_layout);
        loadingLayout = (FrameLayout) findViewById(R.id.progressBarLayout);
        subcategorylayout = (LinearLayout) findViewById(R.id.subcategory_layout);
        noTopMovie = (TextView) findViewById(R.id.topmovie_error);
        noCategory = (TextView) findViewById(R.id.category_error_text);
        subcategory_container = (FrameLayout) findViewById(R.id.movie_list_container);


        noMovies = (TextView) findViewById(R.id.movie_error);
        top_movies = (TextView) findViewById(R.id.top_movies);
        top_movielist = (RecyclerView) findViewById(R.id.top_movies_list);
        subcategory_list = (RecyclerView) findViewById(R.id.subcategory_list);
//        subcategory_list.setNextFocusLeftId(subcategory_list.getId());
////        subcategory_list.setNextFocusRightId(subcategory_list.getId());

        searchBtn = (Button) findViewById(R.id.btn_search_sub);
        search_text = (EditText) findViewById(R.id.search_text_sub);
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (search_text.getText().toString().trim().equals("".trim())) {
                    Toast.makeText(MovieListActivity.this, "Please enter text to search", Toast.LENGTH_SHORT).show();
                    search_text.requestFocus();
                } else {
                    handled = true;
                    searchedMovie = search_text.getText().toString();
                    if (searchMoviesTask != null)
                        searchMoviesTask.cancel(true);
                    searchMoviesTask = new SearchAsync(MovieListActivity.this, MovieListActivity.this, authToken);
                    searchMoviesTask.execute(LinkConfig.getString(MovieListActivity.this, R.string.search_url), searchedMovie);
                }
                return handled;
            }
        });
        search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    // Just ignore the [Enter] key
                    return true;
                }
                return false;
            }
        });
        search_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !searchBtn.hasFocus()) {
                    search_text.setVisibility(View.GONE);

                }
            }
        });
        searchBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && search_text.getVisibility() == View.GONE) {
                    search_text.setVisibility(View.VISIBLE);
                    search_text.requestFocus();
                } else if (!hasFocus && !search_text.hasFocus()) {
                    search_text.setVisibility(View.GONE);
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_text.getText().toString().trim().equals("".trim())) {
                    Toast.makeText(MovieListActivity.this, "Please enter text to search", Toast.LENGTH_SHORT).show();
                    search_text.requestFocus();
                } else {
                    searchedMovie = search_text.getText().toString();
                    if (searchMoviesTask != null)
                        searchMoviesTask.cancel(true);
                    searchMoviesTask = new SearchAsync(MovieListActivity.this, MovieListActivity.this, authToken);
                    searchMoviesTask.execute(LinkConfig.getString(MovieListActivity.this, R.string.search_url), searchedMovie);
                }
            }
        });
        top_movielist.setNextFocusRightId(searchBtn.getId());
        searchBtn.setNextFocusDownId(top_movielist.getId());
        search_text.setNextFocusDownId(top_movielist.getId());
    }


    @Override
    public void onBackPressed() {
        if (loadMovies != null && loadMovies.getStatus() == android.os.AsyncTask.Status.RUNNING) {
            loadMovies.cancel(true);
        }
        if (searchMoviesTask != null && searchMoviesTask.getStatus() == android.os.AsyncTask.Status.RUNNING) {
            searchMoviesTask.cancel(true);
        }
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void getResults(String s) {
        loadingLayout.setVisibility(View.GONE);
        contentlayout.setVisibility(View.VISIBLE);
        if (getTopMoviesData(s)) {
            MovieSubCategoryParser parser = new MovieSubCategoryParser(s);
            try {
                boolean success = parser.parse();
                if (success) {
                    ArrayList<SubCategoryName> subcategoryList = parser.getSubCategoryList();
                    if (subcategoryList.size() > 0)
                        setUpAllAdapters(subcategoryList);
                    else {
                        subcategorylayout.setVisibility(View.GONE);
                        noCategory.setVisibility(View.VISIBLE);
                    }
                } else {
                    CustomDialogManager noMoviesAlert = new CustomDialogManager(this, CustomDialogManager.MESSAGE);
                    noMoviesAlert.build();
                    noMoviesAlert.setMessage("", this.getString(R.string.msg_no_movies));
                    noMoviesAlert.addDissmissButtonToDialog();
                    noMoviesAlert.dismissDialogOnBackPressed();
                    noMoviesAlert.finishActivityOnBackPressed(this);
                    noMoviesAlert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    JSONObject root = new JSONObject(s);
                    if (root.getString("error_code").equals("405")) {
                        LinkConfig.deleteAuthCodeFile();
                        final CustomDialogManager invalidTokenDialog = new CustomDialogManager(MovieListActivity.this, CustomDialogManager.ALERT);
                        invalidTokenDialog.build();
                        invalidTokenDialog.setTitle("Invalid Token");
                        invalidTokenDialog.setMessage("", root.getString("message") + ",please re-login");
                        invalidTokenDialog.getInnerObject().setCancelable(false);
                        invalidTokenDialog.exitApponBackPress();
                        invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent entryPointIntent = new Intent(MovieListActivity.this, EntryPoint.class);
                                entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                invalidTokenDialog.dismiss();
                                startActivity(entryPointIntent);


                            }
                        });
                        invalidTokenDialog.show();


                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(MovieListActivity.this);
                }
            }
        }

    }

    private boolean getTopMoviesData(String s) {
        if (s.equals(DownloadUtil.NotOnline) || s.equals(DownloadUtil.ServerUnrechable)) {
            final CustomDialogManager noInternet = new CustomDialogManager(MovieListActivity.this, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.setTitle(getString(R.string.no_internet_title));
            noInternet.setMessage("", getString(R.string.no_internet_body));
            noInternet.dismissDialogOnBackPressed(MovieListActivity.this);
            noInternet.getInnerObject().setCancelable(false);
            noInternet.setExtraButton(getString(R.string.btn_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noInternet.dismiss();
                    finish();
                }
            });
            noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    noInternet.dismiss();
                    startActivity(i);


                }
            });
            noInternet.setNegativeButton("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSetting();
                    noInternet.dismiss();
                }
            });
            noInternet.show();
            return false;
        } else {
            ArrayList<Movie> topMoveList = new ArrayList<>();
            JSONArray movieArray = null;
            try {
                JSONObject topmovieObj = new JSONObject(s);
                movieArray = topmovieObj.getJSONArray("topmovies");
                if (movieArray.length() > 0) {
                    for (int i = 0; i < movieArray.length(); i++) {
                        realm.beginTransaction();
//                Movie subCatMovie  = realm.createObject(Movie.class);
                        Movie topMovie = new Movie();
                        JSONObject movieObj = movieArray.getJSONObject(i);
                        topMovie.setMovie_id(movieObj.getInt("id"));
                        topMovie.setMovie_name(movieObj.getString("name"));
                        topMovie.setIs_Imdb(Integer.parseInt(movieObj.getString("imdbID")));
                        topMovie.setImdb_id(movieObj.getString("movie_id"));
                        topMovie.setMovie_description(movieObj.getString("description"));
                        topMovie.setMovie_category_id(Integer.parseInt(movieObj.getString("movie_category_id")));
                        topMovie.setMovie_url(movieObj.getString("movie_url"));
                        topMovie.setIs_youtube(Integer.parseInt(movieObj.getString("is_youtube")));
                        topMovie.setPreview_url(movieObj.getString("preview_url"));
                        topMovie.setParental_lock(Integer.parseInt(movieObj.getString("parental_lock")));
                        Movie movie = realm.where(Movie.class).equalTo("movie_id", movieObj.getInt("id")).findFirst();
                        if (movie != null) {
                            if (movie.getParental_lock() == 1)
                                topMovie.setParental_lock(1);
                        }
                        topMovie.setMovie_logo(movieObj.getString("movie_logo"));
                        topMovie.setIsFav(Integer.parseInt(movieObj.getString("isFav")));
                        topMoveList.add(topMovie);
                        realm.insertOrUpdate(topMovie);
                        realm.commitTransaction();

                    }
                    hasTopMovie = true;
                    displayTopMovieList(topMoveList);
                } else {
                    hasTopMovie = false;
                    top_movielist.setVisibility(View.GONE);
                    noTopMovie.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
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

    private void displayTopMovieList(final ArrayList<Movie> topMoveList) {
        final MovieRecyclerViewAdapter topAdapter = new MovieRecyclerViewAdapter(MovieListActivity.this, topMoveList, top_movielist);
        top_movielist.setLayoutManager(new LinearLayoutManager(MovieListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        top_movielist.setAdapter(topAdapter);
        top_movielist.requestFocus();

       /* top_movielist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int h1 = top_movielist.getWidth();
                int h2 = view.getWidth();
                top_movielist.smoothScrollToPositionFromOffset(i, h1 / 2 - h2 / 2, 2000);
                topAdapter.setSelectedPosition(i);
                topAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        top_movielist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topMoviePlayIntent=new Intent(MovieListActivity.this,MoviePlayCustomController.class);
                topMoviePlayIntent.putExtra("currentMovieId",topMoveList.get(i).getMovie_id());
                topMoviePlayIntent.putParcelableArrayListExtra("movie_list",topMoveList);
                startActivity(topMoviePlayIntent);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpAllAdapters(final ArrayList<SubCategoryName> subcategoryList) {
        ArrayList<SubCategoryName> newSubCategoryList = DataUtils.getFilteredSubCatList(subcategoryList);
        subcatAdapter = new SubCategoryRecyclerAdapter(this, newSubCategoryList, subcategory_list);
        subcategory_list.setLayoutManager(new LinearLayoutManager(MovieListActivity.this, LinearLayoutManager.HORIZONTAL, false));
        subcategory_list.setAdapter(subcatAdapter);


        SubCategoryName clickedName = newSubCategoryList.get(0);
        if (clickedName.getMovie_details().length() == 0) {
            if (hasTopMovie)
                top_movielist.requestFocus();
            noMovies.setVisibility(View.VISIBLE);
            subcategory_container.setVisibility(View.GONE);
        } else {
            subcatAdapter.setSelected_item2(0);
            SubCatFragment subMovies = SubCatFragment.newInstance(false, clickedName.getMovie_details());
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_list_container, subMovies).commit();
        }
      /*  subcategory_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subcatAdapter.setClickedPossition(i);
                subcatAdapter.notifyDataSetChanged();
                SubCatFragment subMovies = SubCatFragment.newInstance("hello", subcategoryList.get(i).getMovie_details());
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_list_container, subMovies).commit();

            }
        });*/
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
                    if (searchMoviesTask != null)
                        searchMoviesTask.cancel(true);
                    searchMoviesTask = new SearchAsync(MovieListActivity.this, MovieListActivity.this, authToken);
                    searchMoviesTask.execute(LinkConfig.getString(MovieListActivity.this, R.string.search_url), searchedMovie);
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
                final CustomDialogManager manager = new CustomDialogManager(MovieListActivity.this, CustomDialogManager.MESSAGE);
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
                        final CustomDialogManager manager = new CustomDialogManager(MovieListActivity.this, CustomDialogManager.MESSAGE);
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
                                            showMoviesinActivity(searchmovieList, loading);
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
                            final CustomDialogManager invalidTokenDialog = new CustomDialogManager(MovieListActivity.this, CustomDialogManager.ALERT);
                            invalidTokenDialog.build();
                            invalidTokenDialog.setTitle("Invalid Token");
                            invalidTokenDialog.setMessage("", root.getString("message") + ",please re-login");
                            invalidTokenDialog.getInnerObject().setCancelable(false);
                            invalidTokenDialog.exitApponBackPress();
                            invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent entryPointIntent = new Intent(MovieListActivity.this, EntryPoint.class);
                                    entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    invalidTokenDialog.dismiss();
                                    startActivity(entryPointIntent);


                                }
                            });
                            invalidTokenDialog.show();


                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(MovieListActivity.this);
                    }
                }
            }
        }
    }

    private void showMoviesinActivity(ArrayList<Movie> searchmovieList, CustomDialogManager loading) {
        Intent searchintent = new Intent(MovieListActivity.this, SearchActivity.class);
        MovieEnum.setData(searchmovieList);
        startActivity(searchintent);
        loading.dismiss();
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

    public void hideFragment() {
        if (hasTopMovie) {
            top_movielist.requestFocus();
        } else {
            subcategory_list.requestFocus();
        }
        noMovies.setVisibility(View.VISIBLE);
        subcategory_container.setVisibility(View.GONE);

    }

    public void removeErrorMessage() {
        noMovies.setVisibility(View.GONE);
        subcategory_container.setVisibility(View.VISIBLE);
    }

    private long mLastKeyDownTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long current = System.currentTimeMillis();
        boolean res = false;
        if (current - mLastKeyDownTime < 300) {
            res = true;
        } else {
            res = super.onKeyDown(keyCode, event);
            mLastKeyDownTime = current;
        }
        return res;
    }
}
