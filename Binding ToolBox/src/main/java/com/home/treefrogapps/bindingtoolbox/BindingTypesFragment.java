package com.home.treefrogapps.bindingtoolbox;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class BindingTypesFragment extends Fragment {


    public BindingTypesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_binding_types, container, false);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.my, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);

        MenuItem help = menu.findItem(R.id.action_help);
        help.setVisible(true);
    }


}
