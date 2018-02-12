package maxxtv.movies.stb.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Async.AddToFav;
import maxxtv.movies.stb.Entity.AddDataToFav;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Interface.IsFavCallback;
import maxxtv.movies.stb.MoviePlayCustomController;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.EnterPasswordDialog;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.ParentalLockUtils;
import maxxtv.movies.stb.Utils.common.LinkConfig;


public class OptionsFragment extends Fragment implements IsFavCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageButton info, parental_lock, fav_movie;
    private Realm realm;
    // TODO: Rename and change types of parameters
    private int mMovieId;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String authToken;

    public OptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Parameter 1.
     * @param param2  Parameter 2.
     * @return A new instance of fragment OptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionsFragment newInstance(int movieId, String param2) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, movieId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        authToken= LoginFileUtils.getAuthTokenFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View optionView = inflater.inflate(R.layout.fragment_options, container, false);
        realm = Realm.getDefaultInstance();
        info = (ImageButton) optionView.findViewById(R.id.info_button);
        parental_lock = (ImageButton) optionView.findViewById(R.id.parental_button);
        fav_movie = (ImageButton) optionView.findViewById(R.id.fav_button);

        info.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mListener.onOptionFragmentInteraction();
                }
            }
        });
        fav_movie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mListener.onOptionFragmentInteraction();
                }
            }
        });
        parental_lock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mListener.onOptionFragmentInteraction();
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MoviePlayCustomController) getActivity()).showInfoFragment();


            }
        });
        fav_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddToFav(getActivity(),mMovieId,OptionsFragment.this,authToken).execute(LinkConfig.getString(getActivity(), LinkConfig.MOVIE_FAV_LINK));


            }
        });

        parental_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((MoviePlayCustomController)getActivity()).checkPinStatus()) {
                    EnterPasswordDialog.showParentalControlPasswordDialogToPlayMovie(getActivity(), mMovieId, false, authToken);
                }else{
                    ParentalLockUtils.changeMovieParentalStatus(getActivity(),
                            mMovieId,authToken);
                }
            }
        });
        return optionView;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setMovieAsFav(String s) {
        try{
            JSONObject obj=new JSONObject(s);
            boolean isFav=obj.getBoolean("status");
            realm.beginTransaction();
            RealmQuery<Movie> movieQuery = realm.where(Movie.class).equalTo("movie_id", mMovieId);
            Movie movie = movieQuery.findFirst();
            if(isFav){
            Log.d("movie",movie+"");
                movie.setIsFav(1);
                Toast.makeText(getActivity(), "Movie successfully added as favorite", Toast.LENGTH_SHORT).show();
            }
            else {
                movie.setIsFav(0);
                Toast.makeText(getActivity(), "Movie successfully removed from favorite", Toast.LENGTH_SHORT).show();
            }
            realm.insertOrUpdate(movie);
            realm.commitTransaction();
            EventBus.getDefault().post(new AddDataToFav(movie.getMovie_id()));

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Requested operation couldn't be completed.", Toast.LENGTH_SHORT).show();
            try {
                JSONObject root = new JSONObject(s);
                if (root.getString("error_code").equals("405")) {
                    LinkConfig.deleteAuthCodeFile();
                    final CustomDialogManager invalidTokenDialog = new CustomDialogManager(getActivity(), CustomDialogManager.ALERT);
                    invalidTokenDialog.build();
                    invalidTokenDialog.setTitle("Invalid Token");
                    invalidTokenDialog.setMessage("", root.getString("message")+",please re-login");
                    invalidTokenDialog.getInnerObject().setCancelable(false);
                    invalidTokenDialog.exitApponBackPress();
                    invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent entryPointIntent = new Intent(getActivity(), EntryPoint.class);
                            entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            invalidTokenDialog.dismiss();
                            getActivity().startActivity(entryPointIntent);


                        }
                    });
                    invalidTokenDialog.show();


                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(getActivity());
            }

        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOptionFragmentInteraction();
    }

}
