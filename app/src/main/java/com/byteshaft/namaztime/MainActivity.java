package com.byteshaft.namaztime;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener , View.OnClickListener {


    final static String sFileName = "namaztimes.txt";
    public static int CITY_NAME;
    static LinearLayout layout;
    static LinearLayout linearLayout;
    private final String SELECTED_CITY = "city";
    private Spinner mSpinner;
    private SharedPreferences setting;
    private String FILE_NAME = "cities";
    private SharedPreferences.OnSharedPreferenceChangeListener listen;
    public static TextView textView, text , textTime;
    Button btnPrevious, btnNext;
    Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, NamazTimeService.class));
        initializationOfXmlReferences();
        helpers = new Helpers(this);


        String location = getFilesDir().getAbsoluteFile().getAbsolutePath() + "/" + sFileName;
        File file = new File(location);
        if (!file.exists()) {
            if (Helpers.checkNetworkStatus() != null) {
                new SystemManagement(this).execute();
            } else {
                Helpers.refreshDialoge(this);
            }
        } else {
            helpers.setTimesFromDatabase();
        }
    }


    private void citiesSpinner() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Karachi");
        categories.add("Lahore");
        categories.add("Multan");
        categories.add("Islamabad");
        categories.add("Peshawar");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setting = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        int previousPosition = setting.getInt(SELECTED_CITY, 0);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(previousPosition);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setSharedPrefrenceForCities(mSpinner.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void setSharedPrefrenceForCities(int value) {
        setting = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = setting.edit();
        editor.putInt(SELECTED_CITY, value);
        editor.apply();
        CITY_NAME = setting.getInt(SELECTED_CITY, 0);
    }

    private void refreshOnChangeSharedPrefrence(final Activity context) {
        listen = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (Helpers.checkNetworkStatus() == null) {
                    Helpers.refreshDialoge(context);
                }else{
                    new SystemManagement(context).execute();
                }
            }
        };
    }

    private void initializationOfXmlReferences() {
        mSpinner = (Spinner) findViewById(R.id.FirstSpinner);
        layout = (LinearLayout) findViewById(R.id.layout);
        mSpinner.setOnItemSelectedListener(this);
        citiesSpinner();
        refreshOnChangeSharedPrefrence(this);
        setting.registerOnSharedPreferenceChangeListener(listen);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setBackgroundResource(R.drawable.back);
        textView = (TextView) findViewById(R.id.textView);
        text = (TextView) findViewById(R.id.text);
        textTime = (TextView) findViewById(R.id.textTime);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                helpers.addOneToDate();
                if (!Helpers.mPresentDate.matches(Helpers.sDATE)) {
                    btnNext.setBackgroundResource(R.drawable.onclicknext);
                    helpers.setTimesFromDatabase();
                } else {
                    btnNext.setBackgroundResource(R.drawable.next);
                }

                break;
            case R.id.btnPrevious:
                helpers.subOneToDate();
                if (!Helpers.mPresentDate.matches(Helpers.sDATE)) {
                    btnPrevious.setBackgroundResource(R.drawable.onclickprevious);
                    helpers.setTimesFromDatabase();
                } else {
                    btnPrevious.setBackgroundResource(R.drawable.previous);
                }



                break;
        }

    }
}
