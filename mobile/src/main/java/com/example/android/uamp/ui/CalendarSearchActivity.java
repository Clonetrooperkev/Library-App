package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.uamp.R;

public class CalendarSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

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
                    CalendarSearchActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
            String searchresults = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(CalendarSearchActivity.this, NewCalendarActivity.class);
            searchIntent.putExtra("SEARCHVALUE", searchresults);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(searchIntent, extras);
            //use the query to search
        }
    }

}
