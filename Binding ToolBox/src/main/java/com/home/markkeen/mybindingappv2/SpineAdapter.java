package com.home.markkeen.mybindingappv2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

// create a custom adapter to extend - supercede ArrayAdapter defaults?
public class SpineAdapter extends ArrayAdapter<HashMap<String, String>> {


    private final Context context;
    private final ArrayList<HashMap<String, String>> spineList;
    private DBTools dbTools;




    // View lookup cache
    private static class ViewHolder {
        protected TextView spineId;
        protected TextView spineTitle;
        protected TextView history_dateTEXT;
        protected TextView history_pageCountTEXT;
        protected TextView history_pageWidthTEXT;
        protected TextView history_pageHeightTEXT;
        protected TextView history_coverTEXT;
        protected TextView history_textTEXT;
        protected TextView history_spineTEXT;
        protected TextView history_weightTEXT;
        protected Button deleteButton;
        protected Button editButton;
    }

    public SpineAdapter(Context context, ArrayList<HashMap<String, String>> spineList) {
        super(context, R.layout.spine_entry, spineList);

        this.context = context;
        this.spineList = spineList;
        this.dbTools = new DBTools(getContext());


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        HashMap<String, String> spine = spineList.get(position);

        final ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view

        //viewHolder is used to reuse views that are cache rather than redrawing - offers better performance


        // do if the view has never been viewed/seen before
        if (convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.spine_entry, parent, false);

            // hold the information in the viewHolder
            viewHolder.spineId = (TextView) convertView.findViewById(R.id.spineId);
            viewHolder.spineTitle = (TextView) convertView.findViewById(R.id.history_spine_title);
            viewHolder.history_dateTEXT = (TextView) convertView.findViewById(R.id.history_dateTEXT);
            viewHolder.history_pageCountTEXT = (TextView) convertView.findViewById(R.id.history_pageCountTEXT);
            viewHolder.history_pageWidthTEXT = (TextView) convertView.findViewById(R.id.history_pageWidthTEXT);
            viewHolder.history_pageHeightTEXT = (TextView) convertView.findViewById(R.id.history_pageHeightTEXT);
            viewHolder.history_coverTEXT = (TextView) convertView.findViewById(R.id.history_coverTEXT);
            viewHolder.history_textTEXT = (TextView) convertView.findViewById(R.id.history_textTEXT);
            viewHolder.history_spineTEXT = (TextView) convertView.findViewById(R.id.history_spineTEXT);
            viewHolder.history_weightTEXT = (TextView) convertView.findViewById(R.id.history_weightTEXT);
            viewHolder.deleteButton = (Button) convertView.findViewById(R.id.delete_entry);
            viewHolder.editButton = (Button) convertView.findViewById(R.id.edit_entry);

            // store the information in a tag
            convertView.setTag(viewHolder);

        } else {
            // if the view has been seen before use view lookup cache stored in tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.spineId.setText(spine.get("widthId"));
        viewHolder.spineTitle.setText(spine.get("name"));
        viewHolder.history_dateTEXT.setText(spine.get("date"));
        viewHolder.history_pageCountTEXT.setText(spine.get("pageCount"));
        viewHolder.history_pageWidthTEXT.setText(spine.get("pageWidth"));
        viewHolder.history_pageHeightTEXT.setText(spine.get("pageHeight"));
        viewHolder.history_coverTEXT.setText(spine.get("coverWeight"));
        viewHolder.history_textTEXT.setText(spine.get("textWeight"));
        viewHolder.history_spineTEXT.setText(spine.get("spineWidth"));
        viewHolder.history_weightTEXT.setText(spine.get("bookWeight"));



        // set onClickListener for edit button
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // Convert that contactId into a String

                String spineIdValue = viewHolder.spineId.getText().toString();

                Fragment myFragment = new BindingCalculatorFragment();
                Bundle bundle = new Bundle();
                bundle.putString("widthId", spineIdValue);
                myFragment.setArguments(bundle);
                FragmentManager fm = ((FragmentActivity)context).getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, myFragment).addToBackStack(null).commit();
            }
        });

        // set onClickListener for delete button

        final View finalConvertView = convertView;
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String spineIdValue = viewHolder.spineId.getText().toString();
                dbTools.deleteSingleSpine(spineIdValue);


                //animation for listItem deletion
                PropertyValuesHolder pvhW = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -1000);
            //    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
            //   PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);


                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(finalConvertView, pvhW, pvhX);
                finalConvertView.setHasTransientState(true);
                anim.setDuration(500);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        int firstPosition = spineList.indexOf(0) + spineList.indexOf(position);

                        spineList.remove(position);

                        finalConvertView.setAlpha(1);
                        finalConvertView.setTranslationX(0);
                    //    finalConvertView.setScaleX(1);
                    //    finalConvertView.setScaleY(1);

                        for (int i = firstPosition; i < spineList.size(); i++){

                            spineList.indexOf(i);
                            int moveUp = finalConvertView.getHeight();
                            finalConvertView.setTranslationY(moveUp);
                            finalConvertView.animate().setDuration(150).translationY(0);

                        }

                        finalConvertView.setHasTransientState(false);

                        notifyDataSetChanged();
                    }
                });
                anim.start();


            }
        });



        // Return the completed view to render on screen
        return convertView;


    }

}

