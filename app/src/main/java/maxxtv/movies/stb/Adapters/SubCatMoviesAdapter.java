package maxxtv.movies.stb.Adapters;

import android.content.Context;
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
import maxxtv.movies.stb.Utils.Logger;

public class SubCatMoviesAdapter extends ArrayAdapter<Movie> {

	private Context context;
	private ArrayList<Movie> movieArrayList;
	private int rowLayoutResourceId;

	public SubCatMoviesAdapter(Context context, int rowLayoutResourceId,
                               ArrayList<Movie> movieArrayList) {
		super(context, rowLayoutResourceId, movieArrayList);

		this.context = context;
		this.movieArrayList = movieArrayList;
		this.rowLayoutResourceId = rowLayoutResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(rowLayoutResourceId, null);

			holder = new ViewHolder();
			holder.movieImage= (ImageView) convertView.findViewById(R.id.appimage);
			holder.movieTitle= (TextView) convertView.findViewById(R.id.tv_title);
			Movie movie = movieArrayList.get(position);

			String thumbUrl = movie.getMovie_logo();
//        holder.reflectingText.setImageBitmap(reflectImage(context, itemImage.getId()));
			holder.movieTitle.setText(movie.getMovie_name());
			// Here you apply the animation when the view is bound
			//       UrlImageViewHelper.setUrlDrawable(holder.imageItems, thumbUrl, R.drawable.placeholder, 3000000);
			Glide.with(context)
					.load(thumbUrl)
					.diskCacheStrategy(DiskCacheStrategy.RESULT)
					.override(300,270)
					.placeholder(R.drawable.placeholder)
					.into(holder.movieImage);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView movieImage;
		TextView movieTitle;
	}
}
