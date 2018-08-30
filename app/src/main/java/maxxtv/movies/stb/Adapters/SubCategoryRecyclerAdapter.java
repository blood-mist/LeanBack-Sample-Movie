package maxxtv.movies.stb.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.SubCategoryName;
import maxxtv.movies.stb.Fragments.SubCatFragment;
import maxxtv.movies.stb.MovieListActivity;
import maxxtv.movies.stb.R;


public class SubCategoryRecyclerAdapter extends RecyclerView.Adapter<SubCategoryRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SubCategoryName> subcategoryList;
    private int focusedItem = -1;
    private RecyclerView listHozizontal;
    private boolean flag;
    private int selected_item1 = -1;

    public void setSelected_item2(int selected_item2) {
        this.selected_item2 = selected_item2;
    }

    private int selected_item2 = -1;

    public SubCategoryRecyclerAdapter(Context context, ArrayList<SubCategoryName> subcategoryList, RecyclerView listHorizontal) {
        this.context = context;
        this.subcategoryList = subcategoryList;
        this.listHozizontal = listHorizontal;
        this.flag = true;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SubCategoryName itemName = subcategoryList.get(position);
        selected_item1 = 0;




        if (flag) {
            if (selected_item1 == position) {
                holder.title.setSelected(true);
                flag = false;
            }
        } else {
            if (selected_item2 == position) {
                holder.title.setSelected(true);
            } else {
                holder.title.setSelected(false);
            }
        }

        if(focusedItem == position){
            holder.title.requestFocus();
        }
//        holder.reflectingText.setImageBitmap(reflectImage(context, itemImage.getId()));
        holder.title.setText(itemName.getSubCategory());

    }




    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return subcategoryList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        /*
                private ImageView imageItems, reflectingImage, reflectingText;
        */
        private TextView title;
        private View selected_view;


        ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.subcategory_name);
//            reflectingImage = (ImageView) itemView.findViewById(R.id.reflectiveImageView);
            selected_view = itemView.findViewById(R.id.selected_view);
//            reflectingText = (ImageView) itemView.findViewById(R.id.reflectiveImageText);;
            //    itemlayout.setNextFocusRightId(itemView.getNextFocusRightId());

            title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        selected_view.setVisibility(View.VISIBLE);
                        listHozizontal.getLayoutManager().scrollToPosition(getAdapterPosition());

                    } else {
                        selected_view.setVisibility(View.GONE);
//

                    }
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.requestFocus();
                    int pos = getAdapterPosition();
                    if (selected_item2 == pos) {

                    } else {
                        SubCategoryName clickedName = subcategoryList.get(pos);
                        if (clickedName.getMovie_details().length() > 0) {
                            SubCatFragment subMovies = SubCatFragment.newInstance(true, clickedName.getMovie_details());
                            ((MovieListActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.movie_list_container, subMovies).commit();
                            focusedItem = -1;
                        } else {
                            ((MovieListActivity) context).hideFragment();
                            view.requestFocus();
                            focusedItem = pos;
                        }

                        selected_item2 = pos;
                        notifyDataSetChanged();

                    }

                   /* SubCategoryName clickedName = subcategoryList.get(pos);
                    SubCatFragment subMovies = SubCatFragment.newInstance("hello", clickedName.getMovie_details());
                    ((MovieListActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.movie_list_container, subMovies).commit();
*/

                }
            });


        }


    }

}
