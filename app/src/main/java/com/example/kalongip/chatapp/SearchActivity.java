package com.example.kalongip.chatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class SearchActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener {
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
        toolbar.setTitle("Search by EXACT name");
        toolbar.setTitleTextColor(Color.WHITE);
//    if (toolbar != null) {
//      setSupportActionBar(toolbar);
//      ActionBar actionBar = getSupportActionBar();
//      if (actionBar != null) {
//        actionBar.setTitle("Search by name ->");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setCustomView(toolbar);
//      }
//    }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setIcon(new IconicsDrawable(getApplicationContext()).icon(GoogleMaterial.Icon.gmd_search).actionBar().color(Color.WHITE));
        if (searchView != null)
            searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView != null) {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ConversationListFragment conversationListFragment = ConversationListFragment.newInstance(query);
        if (conversationListFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container1, conversationListFragment).commit();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
