package maxxtv.movies.stb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import maxxtv.movies.stb.Entity.MovieCategoryParent;
import maxxtv.movies.stb.GlideApp;
import maxxtv.movies.stb.MovieListActivity;
import maxxtv.movies.stb.R;


public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MovieCategoryParent> itemList;
    private int focusedItem = 0;
    private RecyclerView listHozizontal;

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


    public CategoryRecyclerViewAdapter(Context context, ArrayList<MovieCategoryParent> itemList,RecyclerView listHorizontal) {
        this.context = context;
        this.itemList = itemList;
        this.listHozizontal = listHorizontal;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item_layout, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       MovieCategoryParent itemData = itemList.get(position);

        String thumbUrl = itemData.getCategoryImageLink();
//        holder.reflectingText.setImageBitmap(reflectImage(context, itemImage.getId()));
        holder.title.setText(itemData.getCategoryName());
        // Here you apply the animation when the view is bound
        //       UrlImageViewHelper.setUrlDrawable(holder.imageItems, thumbUrl, R.drawable.placeholder, 3000000);
        GlideApp.with(context)
                .load(thumbUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(240,330)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageItems);
        GradientDrawable drawable;

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        /*
                private ImageView imageItems, reflectingImage, reflectingText;
        */
        private FrameLayout itemlayout;
        private View imageShadeView,textShadeView;
        private TextView title;
        private ImageView imageItems;


        ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
            imageShadeView = itemView.findViewById(R.id.dim_view1);
            textShadeView = itemView.findViewById(R.id.dim_view2);

            imageItems = (ImageView) itemView.findViewById(R.id.category_image);


//            reflectingImage = (ImageView) itemView.findViewById(R.id.reflectiveImageView);
            itemlayout = (FrameLayout) itemView.findViewById(R.id.item_layout);

            itemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ViewCompat.setElevation(itemView, 1);
                        title.setBackground(ContextCompat.getDrawable(context,R.drawable.main_gradient));
                        imageShadeView.setVisibility(View.INVISIBLE);
                        textShadeView.setVisibility(View.INVISIBLE);

                    } else {
                        ViewCompat.setElevation(itemView, 0);
                        imageShadeView.setVisibility(View.VISIBLE);
                        textShadeView.setVisibility(View.VISIBLE);
                        title.setBackground(ContextCompat.getDrawable(context,R.color.item_unselectedd));
//

                    }
                }
            });
            itemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    MovieCategoryParent clickItems = itemList.get(pos);
                    int parent_id=clickItems.getParentId();
                    Intent movieListIntent=new Intent(context,MovieListActivity.class);
                    movieListIntent.putExtra("parent_id",parent_id);
                    (context).startActivity(movieListIntent);
                }

            });

        }


    }

}
