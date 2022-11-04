package com.home.treefrogapps.bindingtoolbox;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.HashMap;


public class HistoryFragment extends ListFragment {


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static final String ORDERBY = "ORDERBY";
    public int i = 0;

    public static final String MyPREFERENCES = "MyPrefs";
    // Initialise sharedPreferences
    SharedPreferences sharedPreferences;

    private DBTools dbTools;
    private TextView spineId;
    private ArrayList<HashMap<String, String>> spineList;
    private SpineAdapter adapter;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        setHasOptionsMenu(true);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(true);

        MenuItem orderByDate = menu.findItem(R.id.order_by_date);
        orderByDate.setVisible(true);

        MenuItem orderByName = menu.findItem(R.id.order_by_name);
        orderByName.setVisible(true);

        MenuItem orderBySpineWidth = menu.findItem(R.id.order_by_width);
        orderBySpineWidth.setVisible(true);

        MenuItem orderByBookWeight = menu.findItem(R.id.order_by_weight);
        orderByBookWeight.setVisible(true);

        MenuItem help = menu.findItem(R.id.action_help);
        help.setVisible(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbTools = new DBTools(this.getActivity());

        sharedPreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getInt(ORDERBY, 0) >= 1) {

            // get a ArrayList<HashMap String, String>> returned from DBTools.java getAllSpines function
            // using the saved int from shared preferences to order the list exactly the same way the fragment was left
            spineList = dbTools.getAllSpines(sharedPreferences.getInt(ORDERBY, 0));

        } else {
            // get a ArrayList<HashMap String, String>> returned from DBTools.java getAllSpines function
            spineList = dbTools.getAllSpines(0);
        }

        // create instance of customAdapter which extends ArrayAdapter (SpineAdapter.java)
        adapter = new SpineAdapter(getActivity(), spineList);

        listView = (ListView) getListView().findViewById(android.R.id.list);

        listView.setAdapter(adapter);

    }


    public void saveOrderByPreference(int i) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Put the information into SharedPreferences as key value pairs (Static String, String)
        editor.putInt(ORDERBY, i);

        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click


        switch (item.getItemId()) {
            case R.id.action_delete:

                if (spineList.size() > 0) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Confirm Delete")
                            .setMessage("Do you want to clear history?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbTools.deleteSpine();
                                    spineList.clear();
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("No", null).show();
                }
                return true;

            case R.id.order_by_date:

                if (spineList.size() > 0) {
                    i = 0;
                    spineList = dbTools.getAllSpines(i);
                    adapter = new SpineAdapter(getActivity(), spineList);
                    listView.setAdapter(adapter);
                    saveOrderByPreference(i);
                }
                return true;

            case R.id.order_by_name:
                if (spineList.size() > 0) {
                    i = 1;
                    spineList = dbTools.getAllSpines(i);
                    adapter = new SpineAdapter(getActivity(), spineList);
                    listView.setAdapter(adapter);
                    saveOrderByPreference(i);
                }
                return true;

            case R.id.order_by_width:
                if (spineList.size() > 0) {
                    i = 2;
                    spineList = dbTools.getAllSpines(i);
                    adapter = new SpineAdapter(getActivity(), spineList);
                    listView.setAdapter(adapter);
                    saveOrderByPreference(i);
                }
                return true;

            case R.id.order_by_weight:
                if (spineList.size() > 0) {
                    i = 3;
                    spineList = dbTools.getAllSpines(i);
                    adapter = new SpineAdapter(getActivity(), spineList);
                    listView.setAdapter(adapter);
                    saveOrderByPreference(i);
                }
                return true;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}






