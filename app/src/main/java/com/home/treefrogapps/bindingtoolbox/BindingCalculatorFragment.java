package com.home.treefrogapps.bindingtoolbox;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


// fragment opener - implementing a onclicklistener (method below) for the calculate spine button

public class BindingCalculatorFragment extends Fragment implements View.OnClickListener {

    //declare all variable  - doubles, Spinners, Buttons, EditTexts & TextViews

    public static final String NAME = "NAME";
    public static final String PAGE_WIDTH = "PAGE_WIDTH";
    public static final String PAGE_HEIGHT = "PAGE_HEIGHT";
    public static final String PAGE_COUNT = "PAGE_COUNT";
    public static final String SPINE_WIDTH = "SPINE_WIDTH";
    public static final String BOOK_WEIGHT = "BOOK_WEIGHT";
    public static final String COVER_WEIGHT = "COVER_WEIGHT";
    public static final String TEXT_WEIGHT = "TEXT_WEIGHT";

    public double pageCount;
    public double spineWidthAmount;
    public double spineWidthAmountPercentage;
    public double spineWidthFinal;
    public double pageWidth;
    public double pageHeight;
    public double bookWeight;
    public double coverWeight;
    public double textWeight;

    public View rootView;

    EditText nameEditText;
    EditText pageWidthEditText;
    EditText pageHeightEditText;
    EditText pageCountET;
    TextView spineWidthAmountTV;
    TextView bookWeightTextView;
    Spinner coverPageWeightSpinner;
    Spinner textPageWeightSpinner;
    String widthId;


    double[] paperWeightValuesCover = new double[16];

    double[] paperWeightValuesText = new double[25];

    // Container for my shared preferences
    public static final String MyPREFERENCES = "MyPrefs";
    // Initialise sharedPreferences
    SharedPreferences sharedPreferences;

    private DBTools dbTools;


    // get index from text brought from history fragment for cover and text weight spinners
    // loop through the spinner positions to match up the spinner position with text
    int getIndex(Spinner spinner, String string) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {

            if (spinner.getItemAtPosition(i).equals(string)) {
                index = i;
            }

        }
        return index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_binding_calculator, container, false);

        Button calculateButton = (Button) rootView.findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(this);

        Button resetButton = (Button) rootView.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my, menu);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);

        MenuItem help = menu.findItem(R.id.action_help);
        help.setVisible(true);
    }


    // set Values for variables on Activity Created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
        pageWidthEditText = (EditText) rootView.findViewById(R.id.pageWidthEditText);
        pageHeightEditText = (EditText) rootView.findViewById(R.id.pageHeightEditText);
        bookWeightTextView = (TextView) rootView.findViewById(R.id.bookWeightTextView);

        pageCountET = (EditText) rootView.findViewById(R.id.pageCountEditText);
        spineWidthAmountTV = (TextView) rootView.findViewById(R.id.spineWidthAmountTextView);

        coverPageWeightSpinner = (Spinner) rootView.findViewById(R.id.coverPaperWeightSpinner);
        textPageWeightSpinner = (Spinner) rootView.findViewById(R.id.textPaperWeightSpinner);

        // get fragment preferences - use this.getActivity() within a fragment
        sharedPreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        dbTools = new DBTools(getActivity());

        // get the arguments (spineId/widthId) sent from  the history fragment when list item
        // use if statement to stop null pointer exception

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            widthId = bundle.getString("widthId", "-1");
        } else {
            widthId = "-1";
        }

        if (!"-1".equals(widthId)) {

            HashMap<String, String> widthInfo = dbTools.getWidthInfo(widthId);

            nameEditText.setText(widthInfo.get("name"));
            pageWidthEditText.setText(widthInfo.get("pageWidth"));
            pageHeightEditText.setText(widthInfo.get("pageHeight"));
            pageCountET.setText(widthInfo.get("pageCount"));
            coverPageWeightSpinner.setSelection(getIndex(coverPageWeightSpinner, (widthInfo.get("coverWeight"))));
            textPageWeightSpinner.setSelection(getIndex(textPageWeightSpinner, (widthInfo.get("textWeight"))));
            spineWidthAmountTV.setText(widthInfo.get("spineWidth"));
            bookWeightTextView.setText(widthInfo.get("bookWeight"));

            SavePreferences();


        } else if (sharedPreferences.contains(NAME)
                || sharedPreferences.contains(PAGE_WIDTH)
                || sharedPreferences.contains(PAGE_HEIGHT)
                || sharedPreferences.contains(PAGE_COUNT)
                || sharedPreferences.contains(SPINE_WIDTH)
                || sharedPreferences.contains(BOOK_WEIGHT)
                || sharedPreferences.contains(COVER_WEIGHT)
                || sharedPreferences.contains(TEXT_WEIGHT)) {

            nameEditText.setText(sharedPreferences.getString(NAME, ""));
            pageWidthEditText.setText(sharedPreferences.getString(PAGE_WIDTH, ""));
            pageHeightEditText.setText(sharedPreferences.getString(PAGE_HEIGHT, ""));
            pageCountET.setText(sharedPreferences.getString(PAGE_COUNT, ""));
            spineWidthAmountTV.setText(sharedPreferences.getString(SPINE_WIDTH, ""));
            bookWeightTextView.setText(sharedPreferences.getString(BOOK_WEIGHT, ""));
            coverPageWeightSpinner.setSelection(sharedPreferences.getInt(COVER_WEIGHT, 0));
            textPageWeightSpinner.setSelection(sharedPreferences.getInt(TEXT_WEIGHT, 0));
        }

        addItemSelectedFromCoverSpinner();

        addItemSelectedFromTextSpinner();

    }


    public void addItemSelectedFromCoverSpinner() {

        coverPageWeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pageCountET.getWindowToken(), 0);

                paperWeightValuesCover[0] = (coverPageWeightSpinner.getSelectedItem().equals("130 (coated)")) ? 0.01 : 0;
                paperWeightValuesCover[1] = (coverPageWeightSpinner.getSelectedItem().equals("150 (coated)")) ? 0.02 : 0;
                paperWeightValuesCover[2] = (coverPageWeightSpinner.getSelectedItem().equals("170 (coated)")) ? 0.03 : 0;
                paperWeightValuesCover[3] = (coverPageWeightSpinner.getSelectedItem().equals("200 (coated)")) ? 0.16 : 0;
                paperWeightValuesCover[4] = (coverPageWeightSpinner.getSelectedItem().equals("250 (coated)")) ? 0.2 : 0;
                paperWeightValuesCover[5] = (coverPageWeightSpinner.getSelectedItem().equals("300 (coated)")) ? 0.23 : 0;
                paperWeightValuesCover[6] = (coverPageWeightSpinner.getSelectedItem().equals("350 (coated)")) ? 0.29 : 0;
                paperWeightValuesCover[7] = (coverPageWeightSpinner.getSelectedItem().equals("400 (coated)")) ? 0.34 : 0;
                paperWeightValuesCover[8] = (coverPageWeightSpinner.getSelectedItem().equals("120 (uncoated)")) ? 0.16 : 0;
                paperWeightValuesCover[9] = (coverPageWeightSpinner.getSelectedItem().equals("140 (uncoated)")) ? 0.17 : 0;
                paperWeightValuesCover[10] = (coverPageWeightSpinner.getSelectedItem().equals("150 (uncoated)")) ? 0.18 : 0;
                paperWeightValuesCover[11] = (coverPageWeightSpinner.getSelectedItem().equals("170 (uncoated)")) ? 0.2 : 0;
                paperWeightValuesCover[12] = (coverPageWeightSpinner.getSelectedItem().equals("190 (uncoated)")) ? 0.23 : 0;
                paperWeightValuesCover[13] = (coverPageWeightSpinner.getSelectedItem().equals("250 (uncoated)")) ? 0.3 : 0;
                paperWeightValuesCover[14] = (coverPageWeightSpinner.getSelectedItem().equals("300 (uncoated)")) ? 0.35 : 0;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                paperWeightValuesCover[15] = 0;
            }
        });


    }

    public void addItemSelectedFromTextSpinner() {

        textPageWeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pageCountET.getWindowToken(), 0);


                paperWeightValuesText[0] = (textPageWeightSpinner.getSelectedItem().equals("80 (coated)")) ? 0.068 : 0;
                paperWeightValuesText[1] = (textPageWeightSpinner.getSelectedItem().equals("90 (coated)")) ? 0.07 : 0;
                paperWeightValuesText[2] = (textPageWeightSpinner.getSelectedItem().equals("100 (coated)")) ? 0.08 : 0;
                paperWeightValuesText[3] = (textPageWeightSpinner.getSelectedItem().equals("115 (coated)")) ? 0.09 : 0;
                paperWeightValuesText[4] = (textPageWeightSpinner.getSelectedItem().equals("130 (coated)")) ? 0.1 : 0;
                paperWeightValuesText[5] = (textPageWeightSpinner.getSelectedItem().equals("150 (coated)")) ? 0.12 : 0;
                paperWeightValuesText[6] = (textPageWeightSpinner.getSelectedItem().equals("170 (coated)")) ? 0.13 : 0;
                paperWeightValuesText[7] = (textPageWeightSpinner.getSelectedItem().equals("200 (coated)")) ? 0.16 : 0;
                paperWeightValuesText[8] = (textPageWeightSpinner.getSelectedItem().equals("220 (coated)")) ? 0.18 : 0;
                paperWeightValuesText[9] = (textPageWeightSpinner.getSelectedItem().equals("250 (coated)")) ? 0.2 : 0;
                paperWeightValuesText[10] = (textPageWeightSpinner.getSelectedItem().equals("300 (coated)")) ? 0.23 : 0;
                paperWeightValuesText[11] = (textPageWeightSpinner.getSelectedItem().equals("80 (uncoated)")) ? 0.11 : 0;
                paperWeightValuesText[12] = (textPageWeightSpinner.getSelectedItem().equals("90 (uncoated)")) ? 0.12 : 0;
                paperWeightValuesText[13] = (textPageWeightSpinner.getSelectedItem().equals("100 (uncoated)")) ? 0.13 : 0;
                paperWeightValuesText[14] = (textPageWeightSpinner.getSelectedItem().equals("110 (uncoated)")) ? 0.14 : 0;
                paperWeightValuesText[15] = (textPageWeightSpinner.getSelectedItem().equals("120 (uncoated)")) ? 0.15 : 0;
                paperWeightValuesText[16] = (textPageWeightSpinner.getSelectedItem().equals("140 (uncoated)")) ? 0.17 : 0;
                paperWeightValuesText[17] = (textPageWeightSpinner.getSelectedItem().equals("150 (uncoated)")) ? 0.18 : 0;
                paperWeightValuesText[18] = (textPageWeightSpinner.getSelectedItem().equals("170 (uncoated)")) ? 0.2 : 0;
                paperWeightValuesText[19] = (textPageWeightSpinner.getSelectedItem().equals("190 (uncoated)")) ? 0.23 : 0;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                paperWeightValuesText[20] = 0;

            }
        });
    }


    public void calculateWeight() {

        //Validate a page width has been entered

        try {

            pageWidth = Double.parseDouble(pageWidthEditText.getText().toString());
            pageHeight = Double.parseDouble(pageHeightEditText.getText().toString());

        } catch (NumberFormatException e) {

            pageWidth = 0;
            pageHeight = 0;
        }

        try {

            // Get Item Text from spinner array
            String coverWeightItem = coverPageWeightSpinner.getSelectedItem().toString();
            coverWeight = Double.parseDouble(coverWeightItem.replaceAll("[^0-9]", ""));

        } catch (NumberFormatException e) {

            coverWeight = 0;

        }


        try {

            // Get Item Text from spinner array
            String textWeightItem = textPageWeightSpinner.getSelectedItem().toString();
            textWeight = Double.parseDouble(textWeightItem.replaceAll("[^0-9]", ""));

        } catch (NumberFormatException e) {


            textWeight = 0;
        }


        if (pageWidth == 0 && pageHeight == 0) {

            bookWeight = 0;

            bookWeightTextView.setText(String.format("%.01f", bookWeight) + " grams");


        } else if (pageWidth > 0 && pageHeight > 0) {

            double pagesPerMetre = (1000 / pageWidth) * (1000 / pageHeight);

            double squareMetresText = (pageCount / 2) / pagesPerMetre;
            double squareMetresCover = 2 / pagesPerMetre;

            double textGrammage = squareMetresText * textWeight;
            double coverGrammage = squareMetresCover * coverWeight;

            bookWeight = textGrammage + coverGrammage + 0.7;

            bookWeightTextView.setText(String.format("%.01f", bookWeight) + " grams");


        }

        addNewSpine();
    }


    public void addNewSpine() {


        if (bookWeight != 0 || spineWidthFinal != 0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date());

            HashMap<String, String> queryValuesMap = new HashMap<String, String>();

            queryValuesMap.put("date", date);
            queryValuesMap.put("name", nameEditText.getText().toString());
            queryValuesMap.put("pageWidth", pageWidthEditText.getText().toString());
            queryValuesMap.put("pageHeight", pageHeightEditText.getText().toString());
            queryValuesMap.put("pageCount", pageCountET.getText().toString());
            queryValuesMap.put("coverWeight", coverPageWeightSpinner.getSelectedItem().toString());
            queryValuesMap.put("textWeight", textPageWeightSpinner.getSelectedItem().toString());
            queryValuesMap.put("spineWidth", spineWidthAmountTV.getText().toString());
            queryValuesMap.put("bookWeight", bookWeightTextView.getText().toString());


            dbTools.insertSpine(queryValuesMap);
        }
    }

    // whenever called all information in input boxes will be saved using SharedPreferences
    public void SavePreferences() {

        // Store current information in strings
        String savedNameEditText = nameEditText.getText().toString();
        String savedPageWidthEditText = pageWidthEditText.getText().toString();
        String savedPageHeightEditText = pageHeightEditText.getText().toString();
        String savedPageCountEditText = pageCountET.getText().toString();
        String savedSpineWidthTextView = spineWidthAmountTV.getText().toString();
        String savedWeightTextView = bookWeightTextView.getText().toString();
        int savedCoverWeightSpinnerItem = coverPageWeightSpinner.getSelectedItemPosition();
        int savedTextWeightSpinnerItem = textPageWeightSpinner.getSelectedItemPosition();

        // Get SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Put the information into SharedPreferences as key value pairs (Static String, String)
        editor.putString(NAME, savedNameEditText);
        editor.putString(PAGE_WIDTH, savedPageWidthEditText);
        editor.putString(PAGE_HEIGHT, savedPageHeightEditText);
        editor.putString(PAGE_COUNT, savedPageCountEditText);
        editor.putString(SPINE_WIDTH, savedSpineWidthTextView);
        editor.putString(BOOK_WEIGHT, savedWeightTextView);
        editor.putInt(COVER_WEIGHT, savedCoverWeightSpinnerItem);
        editor.putInt(TEXT_WEIGHT, savedTextWeightSpinnerItem);

        editor.apply();

    }

    public void resetPreferences() {

        // Get SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // clear and apply
        editor.clear();
        editor.apply();

        // Reset all the current values
        nameEditText.setText("");
        pageWidthEditText.setText("");
        pageHeightEditText.setText("");
        pageCountET.setText("");
        spineWidthAmountTV.setText("0.0 mm");
        bookWeightTextView.setText("0.0 grams");
        coverPageWeightSpinner.setSelection(0);
        textPageWeightSpinner.setSelection(0);
    }


    public void CustomToast() {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup)
                rootView.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_view);
        text.setText("Error - Not all options checked or spine below  3 mm minimum");

        Toast toast = new Toast(getActivity());
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }


    @Override
    public void onClick(View rootView) {
        switch (rootView.getId()) {
            case R.id.calculateButton:

                calculateSpine();
                break;

            case R.id.resetButton:

                resetPreferences();
                break;

        }

    }


    public void calculateSpine() {

        // Validate a page count has been entered, if not return a value of 0

        try {
            pageCount = Double.parseDouble(pageCountET.getText().toString());
        } catch (NumberFormatException e) {

            pageCount = 0;
        }

        // get values from array / spinner selected items

        double paperWeightCover = 0.00;

        for (double item : paperWeightValuesCover) {

            paperWeightCover += item;

        }

        double paperWeightText = 0.00;

        for (double item : paperWeightValuesText) {

            paperWeightText += item;
        }

        // if no pages entered fill Text view with 0, else do the spine calculation

        if (pageCount == 0) {

            spineWidthFinal = 0;

            spineWidthAmountTV.setText(String.format("%.01f", spineWidthFinal) + " mm");

        } else if (pageCount > 0) {

            spineWidthAmount = pageCount / 2 * paperWeightText + (paperWeightCover * 2);
            spineWidthAmountPercentage = spineWidthAmount * 0.95;

            spineWidthFinal = Math.ceil(spineWidthAmountPercentage * 2) / 2 + 0.5;

            if (spineWidthFinal < 3) {

                spineWidthFinal = 0;

                CustomToast();

                spineWidthAmountTV.setText(String.format("%.01f", spineWidthFinal) + " mm");

                bookWeightTextView.setText("0.0 grams");


            } else if (spineWidthFinal >= 3) {

                spineWidthAmountTV.setText(String.format(Locale.getDefault(),"%.01f", spineWidthFinal) + " mm");


                calculateWeight();
                SavePreferences();

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        final ActionBar actionBar = ((AppCompatActivity) requireActivity() ).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.binding_calculator);
    }


}
