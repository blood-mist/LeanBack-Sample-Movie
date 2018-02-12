package maxxtv.movies.stb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.R;

;


public class MoviesListAdapter extends ArrayAdapter<Movie> {
    private final Activity context;

    int selectedPosition = -1;


    private ArrayList<Movie> itemList;

    public MoviesListAdapter(Activity context, ArrayList<Movie> itemList) {
        super(context, R.layout.movie_item_layout, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_item_layout, null);

            holder = new ViewHolder();
            holder.appImage = (ImageView) convertView.findViewById(R.id.appimage);
            holder.category_title = (TextView) convertView.findViewById(R.id.tv_title);
//            holder.border_view=convertView.findViewById(R.id.white_border);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==selectedPosition){
            holder.category_title.setBackgroundColor(ContextCompat.getColor(context,R.color.text_bg_color));
            holder.border_view.setVisibility(View.VISIBLE);
        }else{
            holder.category_title.setBackgroundColor(ContextCompat.getColor(context,R.color.item_unselectedd));
            holder.border_view.setVisibility(View.GONE);
        }

        Movie movie=itemList.get(position);
        holder.category_title.setText(movie.getMovie_name());
        String thumbUrl=movie.getMovie_logo();
        Glide.with(context)
                .load(thumbUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.youtube)
                .into(holder.appImage);
        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    static class ViewHolder {
        ImageView appImage;
        TextView category_title;
        View border_view;

    }
}