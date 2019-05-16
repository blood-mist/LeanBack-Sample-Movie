package stb.androidtv.moviesleanback.cards.presenters;

import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;

public class GridRowItemPresenter extends ListRowPresenter {
    private static final String TAG = GridRowItemPresenter.class.getSimpleName();

    public GridRowItemPresenter() {
        super();
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        /* This two line codes changes the number of rows of ListRow */
        int numRows = ((GridListRow) item).getNumRows();
        ((ListRowPresenter.ViewHolder) holder).getGridView().setNumRows(numRows);

        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);

        /* Disable Shadow */
        // setShadowEnabled(false);
    }

    @Override
    protected void onSelectLevelChanged(RowPresenter.ViewHolder holder) {
        super.onSelectLevelChanged(holder);
        if (holder.isSelected()){
            ((ListRowPresenter.ViewHolder) holder).getGridView().setVisibility(View.VISIBLE);
        } else {
            ((ListRowPresenter.ViewHolder) holder).getGridView().setVisibility(View.GONE);
        }
    }

}
