package maxxtv.movies.stb.Adapters;

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

;import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.MovieCategoryParent;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.Logger;


public class CategoryAdapter extends ArrayAdapter<Movie> {
    private final Context context;

    private int selectedPosition = -1;


    private ArrayList<MovieCategoryParent> itemList;

    public CategoryAdapter(Context context, ArrayList<MovieCategoryParent> itemList) {
        super(context,R.layout.categories_item_layout);
        this.context = context;
        this.itemList = itemList;
        Logger.d("parent_cat",itemList+"");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.categories_item_layout, null);

            holder = new ViewHolder();
            holder.appImage = (ImageView) convertView.findViewById(R.id.category_image);
            holder.category_title = (TextView) convertView.findViewById(R.id.title_text);
            holder.dim_view=convertView.findViewById(R.id.dim_view1);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==selectedPosition){
            holder.category_title.setBackgroundColor(ContextCompat.getColor(context,R.color.text_bg_color));
            holder.dim_view.setVisibility(View.GONE);
        }else{
            holder.category_title.setBackgroundColor(ContextCompat.getColor(context,R.color.item_unselectedd));
            holder.dim_view.setVisibility(View.VISIBLE);
        }
        MovieCategoryParent movie=itemList.get(position);
        String thumbUrl=movie.getCategoryImageLink();
        Glide.with(context)
                .load(thumbUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.youtube)
                .into(holder.appImage);
        holder.category_title.setText(movie.getCategoryName());

        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    static class ViewHolder  {
        ImageView appImage;
        TextView category_title;
        View dim_view;
    }
}