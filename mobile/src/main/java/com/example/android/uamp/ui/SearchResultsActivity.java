package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.uamp.R;

public class SearchResultsActivity extends ActionBarActivity {
    public String searchresults = "a";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        handleIntent(getIntent());
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
*/
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Bundle extras = ActivityOptions.makeCustomAnimation(
                    SearchResultsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
            searchresults = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(SearchResultsActivity.this, MainCatalogActivity.class);
            searchIntent.putExtra("SEARCHVALUE", searchresults);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(searchIntent, extras);
            //use the query to search
        }
    }
    public String getSearchResults() {
        return searchresults;
    }
}
