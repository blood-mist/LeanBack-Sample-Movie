package maxxtv.movies.stb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.SubCategoryName;
import maxxtv.movies.stb.R;

;


public class SubCategoryListAdapter extends ArrayAdapter<Movie> {
    private final Activity context;
    private int selectedPosition = -1;
    private int clickedPosition=0;


    private ArrayList<SubCategoryName> itemList;

    public SubCategoryListAdapter(Activity context, ArrayList<SubCategoryName> itemList) {
        super(context, R.layout.item_movie_sucategory);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.test, null);

            holder = new ViewHolder();
            holder.subcategory_name = (TextView) convertView.findViewById(R.id.subcategory_name);
            holder.selected_view = convertView.findViewById(R.id.selected_view);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SubCategoryName name = itemList.get(position);
        holder.subcategory_name.setText(name.getSubCategory());
        if (position == selectedPosition) {
            holder.selected_view.setVisibility(View.VISIBLE);
        } else {
            holder.selected_view.setVisibility(View.GONE);
        }

        if(position==clickedPosition){
            holder.subcategory_name.setTextColor(Color.parseColor("#ffffff"));
        }else{
            holder.subcategory_name.setTextColor(Color.parseColor("#607D8B"));
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }
    public void setClickedPossition(int position){
        this.clickedPosition=position;
    }

    private static class ViewHolder {
        private TextView subcategory_name;
        private View selected_view;
    }
}