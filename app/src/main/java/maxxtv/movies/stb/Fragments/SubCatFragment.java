package maxxtv.movies.stb.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import maxxtv.movies.stb.Adapters.MovieRecyclerViewAdapter;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.MovieListActivity;
import maxxtv.movies.stb.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubCatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubCatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean mParam1;
    private String mParam2;

//    private RecyclerView subCatGrid;
    private RecyclerView subCatGrid;
    Realm realm;
    private OnFragmentInteractionListener mListener;

    public SubCatFragment() {
        // Required empty public constructor
    }

    public static SubCatFragment newInstance(Boolean isClicked, JSONArray param2) {
        SubCatFragment fragment = new SubCatFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isClicked);
        args.putString(ARG_PARAM2, String.valueOf(param2));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View movieView=inflater.inflate(R.layout.fragment_sub_cat, container, false);
        subCatGrid= (RecyclerView) movieView.findViewById(R.id.subcat_movie_grid);
        realm = Realm.getDefaultInstance();
        parseMovieItems();
        return movieView;
    }

    private void parseMovieItems() {
        Log.d("jsonArray",mParam2);
        ArrayList<Movie>subCatMoveList=new ArrayList<>();
        JSONArray movieArray= null;
        try {
            movieArray = new JSONArray(mParam2);

            for(int i=0;i<movieArray.length();i++){
                realm.beginTransaction();
                Movie subCatMovie  = new Movie();
                JSONObject movieObj=movieArray.getJSONObject(i);
                subCatMovie.setMovie_id(movieObj.getInt("id"));
                subCatMovie.setIs_Imdb(Integer.parseInt(movieObj.getString("imdbID")));
                subCatMovie.setImdb_id(String.valueOf(movieObj.getInt("movie_id")));
                subCatMovie.setMovie_name(movieObj.getString("name"));
                subCatMovie.setMovie_description(movieObj.getString("description"));
                subCatMovie.setMovie_category_id(Integer.parseInt(movieObj.getString("movie_category_id")));
                subCatMovie.setMovie_url(movieObj.getString("movie_url"));
                subCatMovie.setIs_youtube(Integer.parseInt(movieObj.getString("is_youtube")));
                subCatMovie.setPreview_url(movieObj.getString("preview_url"));
                subCatMovie.setParental_lock(Integer.parseInt(movieObj.getString("parental_lock")));
                subCatMovie.setMovie_logo(movieObj.getString("movie_logo"));
                subCatMoveList.add(subCatMovie);
                realm.insertOrUpdate(subCatMovie);
                realm.commitTransaction();

            }


            displayMovieList(subCatMoveList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayMovieList(final ArrayList<Movie> subCatMoveList) {
        ((MovieListActivity)getActivity()).removeErrorMessage();
        final MovieRecyclerViewAdapter movieadapter=new MovieRecyclerViewAdapter(getActivity(),subCatMoveList,subCatGrid);
        subCatGrid.setLayoutManager(new GridLayoutManager(getActivity(),7));
        subCatGrid.setAdapter(movieadapter);
        if(subCatMoveList.size()>0 && mParam1){
            subCatGrid.requestFocus();
        }




       /* subCatGrid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                movieadapter.setSelectedPosition(i);
                movieadapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        subCatGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(), MoviePlayCustomController.class);
                intent.putExtra("currentMovieId",subCatMoveList.get(i).getMovie_id());
                intent.putParcelableArrayListExtra("movie_list",subCatMoveList);
                startActivity(intent);

            }
        });*/



    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
