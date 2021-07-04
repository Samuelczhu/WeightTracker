package com.example.weighttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    //Declare some private fields for views
    private Button btnBirthDate, btnSave, btnCancel;
    private Spinner spGender, spBestFitType, spBestFitOrder;
    private EditText etHeight, etGoalWeight;
    private TextView tvBMILabel, tvBMI, tvBestFitOrder;

    //Date picker dialog
    private DatePickerDialog datePickerDialog;

    //Hold the calendar for date picker dialog
    private Calendar calendar;

    //Hold the record settings object
    private RecordSettings recordSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Get the intent
        Intent intent = getIntent();
        //Check if the intent contains record settings
        if (intent.hasExtra(MainActivity.KEY_SETTINGS)) {
            //Retrieve the record settings
            recordSettings = (RecordSettings) intent.getExtras().getSerializable(MainActivity.KEY_SETTINGS);
        }

        //Find the views
        btnBirthDate = findViewById(R.id.btn_settings_birthDate);
        btnSave = findViewById(R.id.btn_settings_save);
        btnCancel = findViewById(R.id.btn_settings_cancel);
        spGender = findViewById(R.id.spinner_settings_gender);
        etHeight = findViewById(R.id.et_settings_height);
        etGoalWeight = findViewById(R.id.et_settings_goalWeight);
        tvBMILabel = findViewById(R.id.tv_settings_BMI_label);
        tvBMI = findViewById(R.id.tv_settings_BMI);
        spBestFitType = findViewById(R.id.spinner_settings_best_fit_type);
        spBestFitOrder = findViewById(R.id.spinner_settings_best_fit_order);
        tvBestFitOrder = findViewById(R.id.tv_best_fit_order);

        //Setup the gender spinner
        ArrayAdapter<String> spGenderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.gender_array));
        spGender.setAdapter(spGenderAdapter);

        //Setup the best fit type spinner
        ArrayAdapter<String> spBestFitTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.best_fit_type_array));
        spBestFitType.setAdapter(spBestFitTypeAdapter);

        //Setup the best fit order spinner
        ArrayAdapter<String> spBestFitOrderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.best_fit_order_array));
        spBestFitOrder.setAdapter(spBestFitOrderAdapter);

        //Init the date picker
        initDatePicker();

        //Deal with best fit type spinner item click event
        spBestFitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Check if the polynomial type is selected
                if (position == 2) {
                    //Show the views for order selection
                    tvBestFitOrder.setVisibility(View.VISIBLE);
                    spBestFitOrder.setVisibility(View.VISIBLE);
                    spBestFitOrder.setClickable(true);
                } else {
                    //Hide the views for order selection
                    tvBestFitOrder.setVisibility(View.INVISIBLE);
                    spBestFitOrder.setVisibility(View.INVISIBLE);
                    spBestFitOrder.setClickable(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Deal with birthdate button click event
        btnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show the date picker dialog
                datePickerDialog.show();
            }
        });

        //Deal with save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start of TRY/CATCH block
                try {
                    //Retrieve the height and goal weight inputs
                    int height = Integer.valueOf(etHeight.getText().toString().trim());
                    double goalWeight = Double.valueOf(etGoalWeight.getText().toString().trim());
                    //Check for height and goal weight inputs
                    if (height <= 0 || goalWeight <= 0) {
                        throw new Exception("Invalid height and goal weight inputs!");
                    }
                    //Retrieve the gender inputs
                    String gender = (spGender.getSelectedItemPosition() == 0) ? (RecordSettings.MALE) : (RecordSettings.FEMALE);

                    //Retrieve the best fit type inputs
                    String bestFitType = "";
                    switch (spBestFitType.getSelectedItemPosition()) {
                        case 0: //Linear
                            bestFitType = RecordSettings.LINEAR;
                            break;
                        case 1: //Logarithmic
                            bestFitType = RecordSettings.LOGARITHMIC;
                            break;
                        case 2: //Polynomial
                            bestFitType = RecordSettings.POLYNOMIAL;
                            break;
                        case 3: //Power
                            bestFitType = RecordSettings.POWER;
                            break;
                        case 4: //Exponential
                            bestFitType = RecordSettings.EXPONENTIAL;
                            break;
                        default: //Not valid
                            throw new Exception("Invalid best fit type input!");
                    }

                    //Retrieve the best fit type order
                    int bestFitOrder = Integer.valueOf(spBestFitOrder.getSelectedItem().toString());

                    //Retrieve the settings information and create the record settings object
                    RecordSettings recordSettings = new RecordSettings(
                            calendar.getTimeInMillis(), //User birthdate
                            height, //User height
                            gender, //User gender
                            goalWeight, //User goal weight
                            bestFitType, //Best fit type
                            bestFitOrder //Best fit order
                    );

                    //Create the database helper object
                    DatabaseHelper databaseHelper = new DatabaseHelper(SettingsActivity.this);
                    //Save the settings to database
                    if (databaseHelper.saveSettings(recordSettings)) {
                        //Make a Toast message to inform user saved settings successfully
                        Toast.makeText(SettingsActivity.this, R.string.success_saved_settings, Toast.LENGTH_SHORT).show();
                        //Finish this activity
                        finish();
                    } else {
                        //Make a Toast message to inform user failed to save settings
                        Toast.makeText(SettingsActivity.this, R.string.failed_saved_settings, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    //Show a Toast message to inform user to input valid values
                    Toast.makeText(SettingsActivity.this, R.string.settings_input_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Deal with cancel button click event
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish this activity
                finish();
            }
        });

        //Setup the text change listener for the height input
        etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Display the BMI to user if applicable
                displayBMI(etHeight.getText().toString().trim(), etGoalWeight.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Display the BMI to user if applicable
                displayBMI(etHeight.getText().toString().trim(), etGoalWeight.getText().toString().trim());
            }
        });

        //Setup the text change listener for the goal weight input
        etGoalWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Display the BMI to user if applicable
                displayBMI(etHeight.getText().toString().trim(), etGoalWeight.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Display the BMI to user if applicable
                displayBMI(etHeight.getText().toString().trim(), etGoalWeight.getText().toString().trim());
            }
        });

        //Check if the record settings existed
        if (recordSettings != null) {
            //Populate the views with settings information
            etHeight.setText(String.valueOf(recordSettings.getHeight()));
            spGender.setSelection((recordSettings.getGender().equals(RecordSettings.MALE)) ? (0) : (1));
            etGoalWeight.setText(String.format("%.2f", recordSettings.getGoal_weight()));
            //Get the best fit types array
            String[] arrBestFitTypes = getResources().getStringArray(R.array.best_fit_type_array);
            //Set the selection for best fit types
            spBestFitType.setSelection(findIndexInArray(arrBestFitTypes, recordSettings.getBest_fit_type()));
            //Check for the best fit type
            if (recordSettings.getBest_fit_type().equals(RecordSettings.POLYNOMIAL)) {
                //Get the best fit order array
                String[] arrBestFitOrder = getResources().getStringArray(R.array.best_fit_order_array);
                //Populate the best fit order information
                spBestFitOrder.setSelection(findIndexInArray(arrBestFitOrder, String.valueOf(recordSettings.getBest_fit_order())));
            }
        }
    }

    /**
     * Helper function to initialize the date picker
     */
    private void initDatePicker() {
        //Setup the on date set listener
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Update the calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Format the date string in format MM/dd/yyyy
                String txtDate = String.format("%02d/%02d/%04d",month+1, dayOfMonth, year);
                //Set the text to the birthdate button
                btnBirthDate.setText(txtDate);
            }
        };

        //Get the calendar instance
        calendar = Calendar.getInstance();
        //Check if the record settings existed
        if (recordSettings != null) {
            //Update the birthdate
            calendar.setTimeInMillis(recordSettings.getBirthdate());
        }
        //Get the year, month, and day of month
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //Instantiate the date picker
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, dayOfMonth);
        //Set the maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        //Display the date on the btnBirthDate
        btnBirthDate.setText(String.format("%02d/%02d/%04d",month+1, dayOfMonth, year));
    }

    /**
     * Display the BMI text view if applicable
     * @param strHeight string height in cm retrieved from the user input
     * @param strWeight string weight in kg retrieved from the user input
     */
    private void displayBMI(String strHeight, String strWeight) {
        //Start of the TRY/CATCH block
        try {
            //Cast the height and weight
            int height = Integer.valueOf(strHeight);
            double weight = Double.valueOf(strWeight);

            //Create a BMI object
            BMI bmi = new BMI(height, weight);
            //Show the BMI text views
            tvBMILabel.setVisibility(View.VISIBLE);
            tvBMI.setVisibility(View.VISIBLE);
            //Display the BMI to user
            tvBMI.setText(bmi.getBMIValueString());
        } catch (Exception ex) {
            //Hide the BMI text views
            tvBMILabel.setVisibility(View.INVISIBLE);
            tvBMI.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Helper function to find the index in a string array
     * @param arr The string array to search
     * @param str The string element to search for
     * @return index of the item in the string array, -1 if not found
     */
    private int findIndexInArray(String[] arr, String str) {
        //Loop through the string array
        for (int i=0; i<arr.length; i++) {
            //Check for the array item
            if (arr[i].equals(str)) {
                //If matched, return the index
                return i;
            }
        }
        //Default return is -1
        return -1;
    }

}