package stb.androidtv.moviesleanback.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import stb.androidtv.moviesleanback.R;

public class SearchActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }
}
