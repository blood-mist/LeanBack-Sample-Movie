package maxxtv.movies.stb.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.MovieCategoryActivity;
import maxxtv.movies.stb.MoviePlayCustomController;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.ShowMoreMovies;


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Movie> movieList;
    private int focusedItem = 0;
    private RecyclerView listHozizontal;
    private ArrayList<Movie> fixedArrayList;
    private String title_string = "";

    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movieList, ArrayList<Movie> fixedArrayList, RecyclerView listHozizontal, String title_string) {
        this.context = context;
        this.movieList = movieList;
        this.fixedArrayList = fixedArrayList;
        this.listHozizontal = listHozizontal;
        this.title_string = title_string;
        assignArrayListToAdapter();
    }

    private void assignArrayListToAdapter() {
        if (this.fixedArrayList == null) {
            this.fixedArrayList = movieList;
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                recyclerView.requestFocus(5);
                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_2) {
                        return tryMoveSelection(lm, 1);
                    } else if (keyCode == KeyEvent.KEYCODE_1) {
                        return tryMoveSelection(lm, -1);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        Toast.makeText(context, "Inside", Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int tryFocusItem = focusedItem + direction;


        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            notifyItemChanged(focusedItem);
            focusedItem = tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);

            return true;
        }

        return false;
    }


    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movieList, RecyclerView listHorizontal) {
        this.context = context;
        this.movieList = movieList;
        this.listHozizontal = listHorizontal;
        assignArrayListToAdapter();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_layout, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Movie movie = fixedArrayList.get(position);

        String thumbUrl = movie.getMovie_logo();
        holder.title.setText(movie.getMovie_name());
    /*    if(movie.getIsFav()==1){
            holder.favImage.setVisibility(View.VISIBLE);
        }else{
            holder.favImage.setVisibility(View.GONE);
        }*/
        Glide.with(context)
                .load(thumbUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(300, 270)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageItems);
        if (movie.getMovie_url().equals("")) {
            holder.showMore.setVisibility(View.VISIBLE);
            holder.movieItem.setVisibility(View.GONE);
        } else {
            holder.showMore.setVisibility(View.GONE);
            holder.movieItem.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return fixedArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        /*
                private ImageView imageItems, reflectingImage, reflectingText;
        */
        private FrameLayout itemlayout;
        private View borderView;
        private TextView title, refTitle;
        private ImageView imageItems, plusImage,favImage;
        private LinearLayout showMore, movieItem;


        ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            plusImage = (ImageView) itemView.findViewById(R.id.more_image);
//          borderView = itemView.findViewById(R.id.white_border);
            showMore = (LinearLayout) itemView.findViewById(R.id.show_more);
            movieItem = (LinearLayout) itemView.findViewById(R.id.movie_item);
            favImage= (ImageView) itemView.findViewById(R.id.fav_image);

            imageItems = (ImageView) itemView.findViewById(R.id.appimage);


//            reflectingImage = (ImageView) itemView.findViewById(R.id.reflectiveImageView);
            itemlayout = (FrameLayout) itemView.findViewById(R.id.movie_item_layout);
//            reflectingText = (ImageView) itemView.findViewById(R.id.reflectiveImageText);;
            //    itemlayout.setNextFocusRightId(itemView.getNextFocusRightId());

            itemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Movie clickItems = fixedArrayList.get(getAdapterPosition());
                    if (hasFocus) {
                        if (clickItems.getMovie_url().equals("")) {
                            showMore.setBackgroundColor(ContextCompat.getColor(context, R.color.textColor));
                            plusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.more));
                            showMore.setScaleX(1.06f);
                            showMore.setScaleY(1.03f);

                        } else {
                            ViewCompat.setElevation(itemView, 1);
                            movieItem.setScaleX(1.06f);
                            movieItem.setScaleY(1.03f);
                            title.setBackgroundColor(ContextCompat.getColor(context, R.color.text_bg_color));
                        }
//                       borderView.setVisibility(View.VISIBLE);
                    } else {
                        if (clickItems.getMovie_url().equals("")) {
                            showMore.setBackgroundColor(ContextCompat.getColor(context, R.color.layout_background));
                            plusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.plus_corrected));
                            showMore.setScaleX(1.0f);
                            showMore.setScaleY(1.0f);
                        } else {
                            ViewCompat.setElevation(itemView, 0);
                            movieItem.setScaleX(1.0f);
                            movieItem.setScaleY(1.0f);
                            title.setBackgroundColor(ContextCompat.getColor(context, R.color.item_unselectedd));
//                        borderView.setVisibility(View.GONE);

//
                        }
                    }
                }
            });
            itemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Movie clickItems = fixedArrayList.get(pos);
                    if (!clickItems.getMovie_url().equals("")) {
                        Intent moviePlayIntent = new Intent(context, MoviePlayCustomController.class);
                        moviePlayIntent.putExtra("currentMovieId", clickItems.getMovie_id());
                        moviePlayIntent.putParcelableArrayListExtra("movie_list", movieList);
                        (context).startActivity(moviePlayIntent);
                    } else {
                        Intent showActivity = new Intent(context, ShowMoreMovies.class);
                        showActivity.putParcelableArrayListExtra("movie_list", movieList);
                        showActivity.putExtra("activity_title", title_string);
                        (context).startActivity(showActivity);
                    }
                }
            });

        }


    }

}
