package stb.androidtv.moviesleanback.cards.presenters;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

public class GridListRow extends ListRow {
    private static final String TAG = GridListRow.class.getSimpleName();
    private int mNumRows = 1;
    public GridListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public GridListRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public GridListRow(ObjectAdapter adapter) {
        super(adapter);
    }

    public void setNumRows(int numRows) {
        mNumRows = numRows;
    }

    public int getNumRows() {
        return mNumRows;
    }
}
