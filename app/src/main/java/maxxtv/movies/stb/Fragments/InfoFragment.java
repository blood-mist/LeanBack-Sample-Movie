package maxxtv.movies.stb.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import maxxtv.movies.stb.Entity.ImdbPojo;
import maxxtv.movies.stb.R;


public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static ImdbPojo imdbPojo = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView title, overview, release_date, writers, directors, casts;
    private ImageView star1, star2, star3, star4, star5;


    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, ImdbPojo param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        imdbPojo = param2;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        title = (TextView) v.findViewById(R.id.info_title);
        overview = (TextView) v.findViewById(R.id.info_description);
        release_date = (TextView) v.findViewById(R.id.release_date);
        directors = (TextView) v.findViewById(R.id.director_name);
        writers = (TextView) v.findViewById(R.id.movie_writers);
        casts = (TextView) v.findViewById(R.id.movie_stars);
        star1 = (ImageView) v.findViewById(R.id.star_1);
        star2 = (ImageView) v.findViewById(R.id.star_2);
        star3 = (ImageView) v.findViewById(R.id.star_3);
        star4 = (ImageView) v.findViewById(R.id.star_4);
        star5 = (ImageView) v.findViewById(R.id.star_5);
        setDetails();
        return v;
    }

    private void setDetails() {
        title.setText(imdbPojo.getMovie_name());
        overview.setText(imdbPojo.getOverview());
        String date = imdbPojo.getRelease_date();
        if (date.length() > 3)
            date = date.substring(0, 4);
        release_date.setText(date);
        directors.setText(imdbPojo.getDirector());
        writers.setText(imdbPojo.getWriters());
        casts.setText(imdbPojo.getCasts());

        double rating = imdbPojo.getPopularity();
        rating = (rating / 10) * 5;
        int ratingInt = (int) Math.round(rating);
        switch (ratingInt) {
            case 1:
                star1.setImageResource(R.drawable.rate_org);
                break;
            case 2:
                star1.setImageResource(R.drawable.rate_org);
                star2.setImageResource(R.drawable.rate_org);
                break;
            case 3:
                star1.setImageResource(R.drawable.rate_org);
                star2.setImageResource(R.drawable.rate_org);
                star3.setImageResource(R.drawable.rate_org);
                break;
            case 4:
                star1.setImageResource(R.drawable.rate_org);
                star2.setImageResource(R.drawable.rate_org);
                star3.setImageResource(R.drawable.rate_org);
                star4.setImageResource(R.drawable.rate_org);
            case 5:
                star1.setImageResource(R.drawable.rate_org);
                star2.setImageResource(R.drawable.rate_org);
                star3.setImageResource(R.drawable.rate_org);
                star4.setImageResource(R.drawable.rate_org);
                star5.setImageResource(R.drawable.rate_org);
                break;
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
