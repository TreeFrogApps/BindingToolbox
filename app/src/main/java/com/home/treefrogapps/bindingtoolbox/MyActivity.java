package com.home.treefrogapps.bindingtoolbox;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends AppCompatActivity {


    String[] menutitles;


    // nav drawer title
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public List<RowItem> rowItems;
    public CustomAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Check saved state, if not saved set values to 0

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mTitle = mDrawerTitle = getTitle();

        menutitles = getResources().getStringArray(R.array.nav_draw_items);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.slider_menu);

        rowItems = new ArrayList<>();

        for (String menuTitle : menutitles) {
            RowItem items = new RowItem(menuTitle);
            rowItems.add(items);
        }


        adapter = new CustomAdapter(getApplicationContext(), rowItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideItemListener());

        // enabling action bar app icon and behaving it as toggle button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            updateDisplay(0);
        }
    }


    class SlideItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.postDelayed(() -> updateDisplay(position), 300);
        }
    }

    private void updateDisplay(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new BindingCalculatorFragment();
                break;
            case 1:
                fragment = new PaperFragment();
                break;
            case 2:
                fragment = new BindingTypesFragment();
                break;
            case 3:
                fragment = new HistoryFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            // update selected item and title, then close the drawer
            setTitle(menutitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click

        int id = item.getItemId();
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, MyActivityHelp.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
