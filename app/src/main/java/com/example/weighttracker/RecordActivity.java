package com.example.weighttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class RecordActivity extends AppCompatActivity {

    //Declare some private fields to hold views
    private Button btnDate, btnTime, btnSave, btnCancel, btnDelete;
    private EditText etHeight, etWeight, etComment;
    private TextView tvDescription1, tvDescription2, tvDescription3;

    //Date and time picker dialog
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    //Hold the calendar for date and time picker dialog
    private Calendar calendar;

    //Hold the record settings
    private RecordSettings recordSettings;

    //Hold the weight record
    private WeightRecord weightRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //Get the intent
        Intent intent = getIntent();
        //Check if the intent contains record settings
        if (intent.hasExtra(MainActivity.KEY_SETTINGS)) {
            //Retrieve the record settings
            recordSettings = (RecordSettings) intent.getExtras().getSerializable(MainActivity.KEY_SETTINGS);
        }

        //Check if the intent contains weight record
        if (intent.hasExtra(MainActivity.KEY_RECORD)) {
            //Retrieve the weight record
            weightRecord = (WeightRecord) intent.getExtras().getSerializable(MainActivity.KEY_RECORD);
        }

        //Find the views
        btnDate = findViewById(R.id.btn_record_date);
        btnTime = findViewById(R.id.btn_record_time);
        etHeight = findViewById(R.id.et_record_height);
        etWeight = findViewById(R.id.et_record_weight);
        etComment = findViewById(R.id.et_record_comment);
        tvDescription1 = findViewById(R.id.tv_record_description1);
        tvDescription2 = findViewById(R.id.tv_record_description2);
        tvDescription3 = findViewById(R.id.tv_record_description3);
        btnSave = findViewById(R.id.btn_record_save);
        btnCancel = findViewById(R.id.btn_record_cancel);
        btnDelete = findViewById(R.id.btn_record_delete);

        //Initialize the date and time picker
        initDateTimePicker();

        //Deal with date button click event
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show the date picker dialog
                datePickerDialog.show();
            }
        });

        //Deal with time button click event
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show the time picker dialog
                timePickerDialog.show();
            }
        });

        //Deal with save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start of TRY/CATCH block
                try {
                    //Retrieve the height and weight inputs
                    int height = Integer.valueOf(etHeight.getText().toString().trim());
                    double weight = Double.valueOf(etWeight.getText().toString().trim());
                    //Check for height and weight inputs
                    if (height <= 0 || weight <= 0) {
                        throw new Exception("Invalid height and weight inputs!");
                    }

                    //Retrieve the user inputs and create the weight record
                    WeightRecord newRecord = new WeightRecord(
                            (weightRecord == null) ? (-1) : (weightRecord.getId()), //Record ID
                            weight, //Record weight
                            height, //Record height
                            calendar.getTimeInMillis(), //Record timestamp
                            etComment.getText().toString().trim() //Record comment
                    );

                    //Create the database helper
                    DatabaseHelper databaseHelper = new DatabaseHelper(RecordActivity.this);

                    //Check which mode we are in
                    if (weightRecord == null) {
                        //Deal with add mode, add the record to database
                        if (databaseHelper.addRecord(newRecord)) {
                            //Inform user add new record successfully
                            Toast.makeText(RecordActivity.this, R.string.record_added_success, Toast.LENGTH_SHORT).show();
                            //Finish this activity
                            finish();
                        } else {
                            //Inform user failed to add new record
                            Toast.makeText(RecordActivity.this, R.string.record_added_failed, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Deal with edit mode, update the record in database
                        if (databaseHelper.updateRecord(newRecord)) {
                            //Inform user updated record successfully
                            Toast.makeText(RecordActivity.this, R.string.record_updated_success, Toast.LENGTH_SHORT).show();
                            //Finish this activity
                            finish();
                        } else {
                            //Inform user failed to update record
                            Toast.makeText(RecordActivity.this, R.string.record_updated_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    //Show a Toast message to inform user to input valid values
                    Toast.makeText(RecordActivity.this, R.string.record_input_error_message, Toast.LENGTH_SHORT).show();
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

        //Deal with delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if in edit mode
                if (weightRecord == null) {
                    return; //Do nothing if not in edit mode
                }
                //Build a alert dialog to confirm delete
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                //Setup the title
                builder.setTitle(String.format("%s: %.2f kg?", getResources().getString(R.string.delete_record), weightRecord.getWeight()));
                //Setup the message
                builder.setMessage(String.format("%s: %.2f kg?", getResources().getString(R.string.delete_record_question), weightRecord.getWeight()));
                //Deal with yes button click
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Create a database helper
                        DatabaseHelper databaseHelper = new DatabaseHelper(RecordActivity.this);
                        //Delete the weight record
                        if (databaseHelper.deleteOneRecord(String.valueOf(weightRecord.getId()))) {
                            //Inform user that the record is deleted
                            Toast.makeText(RecordActivity.this, R.string.record_deleted_success, Toast.LENGTH_SHORT).show();
                            //Finish the activity
                            finish();
                        } else {
                            //Inform user failed to delete the weight record
                            Toast.makeText(RecordActivity.this, R.string.record_deleted_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //Deal with no button click
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //Show the alert dialog
                builder.create().show();
            }
        });

        //Setup the text change listener for the height input
        etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Display the BMI description to user if applicable
                displayDescription(etHeight.getText().toString().trim(), etWeight.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Display the BMI description to user if applicable
                displayDescription(etHeight.getText().toString(), etWeight.getText().toString());
            }
        });

        //Setup the text change listener for the weight input
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Display the BMI description to user if applicable
                displayDescription(etHeight.getText().toString().trim(), etWeight.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Display the BMI description to user if applicable
                displayDescription(etHeight.getText().toString().trim(), etWeight.getText().toString().trim());
            }
        });

        //Check if the weight record existed
        if (weightRecord != null) {
            //Populate the views with weight record information
            etHeight.setText(String.valueOf(weightRecord.getHeight()));
            etWeight.setText(String.format("%.2f", weightRecord.getWeight()));
            etComment.setText(weightRecord.getComment());
            //Show the delete button for edit mode
            btnDelete.setVisibility(View.VISIBLE);
        } else if (recordSettings != null) {
            //If no weight record existed, retrieved the height from the settings record if it existed
            etHeight.setText(String.valueOf(recordSettings.getHeight()));
            //Hide the delete button for add mode
            btnDelete.setVisibility(View.GONE);
        } else {
            //Hide the delete button for add mode
            btnDelete.setVisibility(View.GONE);
        }
    }

    /**
     * Helper function to initialize the date picker and time picker
     */
    private void initDateTimePicker() {
        //Setup the on date set listener
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Update the calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Format the date string in format MM/dd/yyyy
                String txtDate = String.format("%02d/%02d/%04d", month+1, dayOfMonth, year);
                //Set the text to the date button
                btnDate.setText(txtDate);
            }
        };

        //Setup the on time set listener
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Update the calender
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                //Format the time string in format hh:mm
                String txtTime = String.format("%02d:%02d", hourOfDay, minute);
                //Set the text to the time button
                btnTime.setText(txtTime);
            }
        };

        //Get the calendar instance
        calendar = Calendar.getInstance();
        //Check if the weight record existed
        if (weightRecord != null) {
            //Update the calendar
            calendar.setTimeInMillis(weightRecord.getTimestamp());
        }
        //Get the year, month, day of month, hour of day, and minute
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //Instantiate the date picker
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, dayOfMonth);
        //Instantiate the time picker
        timePickerDialog = new TimePickerDialog(this, timeSetListener, hourOfDay, minute, true);
        //Display the date on the date button
        btnDate.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
        //Display the time on the time button
        btnTime.setText(String.format("%02d:%02d", hourOfDay, minute));
    }

    /**
     * Display the description for the user BMI
     * @param strHeight string height in cm retrieved from the user input
     * @param strWeight string weight in kg retrieved from the user input
     */
    private void displayDescription(String strHeight, String strWeight) {
        //Start of the TRY/CATCH block
        try {
            //Cast the height and weight
            int height = Integer.valueOf(strHeight);
            double weight = Double.valueOf(strWeight);

            //Create a BMI object
            BMI bmi = new BMI(height, weight);
            //Setup the description text for record weight
            tvDescription2.setText(getResources().getString(R.string.record_description2) + " "+ bmi.getBMIValueString());
            String description3 = getResources().getString(R.string.record_description3);
            //Check for the BMI category
            switch (bmi.getBMICategory()) {
                case BMI.UNDERWEIGHT:
                    description3 += " "+getResources().getString(R.string.underweight);
                    break;
                case BMI.NORMAL_WEIGHT:
                    description3 += " "+ getResources().getString(R.string.normal_weight);
                    break;
                case BMI.OVERWEIGHT:
                    description3 += " " + getResources().getString(R.string.overweight);
                    break;
                case BMI.OBESITY:
                    description3 += " " + getResources().getString(R.string.obesity);
                    break;
            }
            tvDescription3.setText(description3);

            //Show the BMI description text views
            tvDescription1.setVisibility(View.VISIBLE);
            tvDescription2.setVisibility(View.VISIBLE);
            tvDescription3.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            //Hide the BMI description text views
            tvDescription1.setVisibility(View.INVISIBLE);
            tvDescription2.setVisibility(View.INVISIBLE);
            tvDescription3.setVisibility(View.INVISIBLE);
        }
    }

}