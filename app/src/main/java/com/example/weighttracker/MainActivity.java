package com.example.weighttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Declare some public constants for key
    public static final String KEY_SETTINGS = "Settings";
    public static final String KEY_RECORD = "Record";

    //Hold the animations
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;

    //Hold the floating action buttons
    private FloatingActionButton fabExpand, fabAnalyze, fabAdd, fabSettings;

    //Recycler view for weight records
    private RecyclerView rvWeightRecords;
    private WeightRecordsAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    //Hold the view when no record existed
    private LinearLayoutCompat llNoRecord;

    //Hold the views for most recent record
    private ConstraintLayout clMostRecentRecord;
    private TextView tvRecordDateTime, tvBMI, tvWeight, tvHeight, tvBMICategory;


    //Database helper
    private DatabaseHelper databaseHelper;
    //Record settings
    private RecordSettings recordSettings;
    //Array list of weight records
    private ArrayList<WeightRecord> weightRecords;

    //Hold the clicked status for the expand floating action button. True if expanded, false otherwise
    private boolean fabExpandClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find all the animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        //Find all the floating action buttons
        fabExpand = findViewById(R.id.fab_expand);
        fabAnalyze = findViewById(R.id.fab_analyze);
        fabAdd = findViewById(R.id.fab_add);
        fabSettings = findViewById(R.id.fab_settings);

        //Find the view for no record
        llNoRecord = findViewById(R.id.ll_no_record);

        //Find the views for most recent record
        clMostRecentRecord = findViewById(R.id.cl_mostRecentRecord);
        tvRecordDateTime = findViewById(R.id.tv_recordDateTime);
        tvBMI = findViewById(R.id.tv_BMI);
        tvWeight = findViewById(R.id.tv_weight);
        tvHeight = findViewById(R.id.tv_height);
        tvBMICategory = findViewById(R.id.tv_BMI_category);

        //Find the recycler view
        rvWeightRecords = findViewById(R.id.rv_weight_records);

        //Setup onclick listener for expand floating action button
        fabExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call the function to handle expand floating action button clicked event
                onExpandFabClicked();
            }
        });

        //Setup onclick listener for analyze floating action button
        fabAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Analyze button clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //Setup onclick listener for add record floating action button
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to the record activity
                toRecordActivity(null);
            }
        });

        //Setup onclick listener for settings floating action button
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to the settings activity
                toSettingsActivity();
            }
        });

        //Initialize the database helper
        databaseHelper = new DatabaseHelper(MainActivity.this);
        //Retrieve the record settings
        recordSettings = databaseHelper.retrieveSettings();
        //Retrieve the weight records
        weightRecords = databaseHelper.readAllRecords();

        //Check for the weight records
        if (weightRecords.isEmpty()) {
            //Hide the most recent record and record recycler views
            clMostRecentRecord.setVisibility(View.GONE);
            rvWeightRecords.setVisibility(View.GONE);
            //Show the no record view
            llNoRecord.setVisibility(View.VISIBLE);
        } else {
            //Hide the no record view
            llNoRecord.setVisibility(View.GONE);
            //Show the most recent record and record recycler views
            clMostRecentRecord.setVisibility(View.VISIBLE);
            rvWeightRecords.setVisibility(View.VISIBLE);

            //Get the most recent record
            WeightRecord mostRecentRecord = weightRecords.remove(0);

            //Create the BMI object for the most recent record
            BMI mostRecentBMI = new BMI(mostRecentRecord.getHeight(), mostRecentRecord.getWeight());

            //Display the data for the most recent record
            tvRecordDateTime.setText(mostRecentRecord.getTimestampString());
            tvBMI.setText("BMI: "+ mostRecentBMI.getBMIValueString());
            tvWeight.setText(String.format("%.2f KG", mostRecentRecord.getWeight()));
            tvHeight.setText(String.format("%d cm", mostRecentRecord.getHeight()));
            //Check for the BMI category
            switch (mostRecentBMI.getBMICategory()) {
                case BMI.UNDERWEIGHT:
                    tvBMICategory.setText(getResources().getString(R.string.underweight));
                    break;
                case BMI.NORMAL_WEIGHT:
                    tvBMICategory.setText(getResources().getString(R.string.normal_weight));
                    break;
                case BMI.OVERWEIGHT:
                    tvBMICategory.setText(getResources().getString(R.string.overweight));
                    break;
                case BMI.OBESITY:
                    tvBMICategory.setText(getResources().getString(R.string.obesity));
                    break;
            }

            //Setup the on click listener for the most recent record view
            clMostRecentRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to the record activity
                    toRecordActivity(mostRecentRecord);
                }
            });

            //Setup the recycler view for weight records
            rvWeightRecords.setHasFixedSize(true);
            //Initialize the layout manager
            rvLayoutManager = new LinearLayoutManager(this);
            //Initialize the custom adapter
            rvAdapter = new WeightRecordsAdapter(weightRecords);
            //Set the layout manager to the recycler view for weight records
            rvWeightRecords.setLayoutManager(rvLayoutManager);
            //Set the custom adapter
            rvWeightRecords.setAdapter(rvAdapter);
            //Add a divider decoration
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvWeightRecords.getContext(), LinearLayoutManager.VERTICAL);
            rvWeightRecords.addItemDecoration(dividerItemDecoration);

            //Setup on item click listener for the weight records recycler view
            rvAdapter.setOnItemClickListener(new WeightRecordsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //Go to the record activity
                    toRecordActivity(MainActivity.this.weightRecords.get(position));
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check for the request code
        if (requestCode == 1) {
            //Recreate the activity
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get the menu inflater object
        MenuInflater inflater = getMenuInflater();
        //Inflate the main menu
        inflater.inflate(R.menu.main_menu, menu);
        //Return true
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Check for the menu item id
        switch (item.getItemId()) {
            //Deal with settings menu clicked
            case R.id.menu_settings:
                //Go to the settings activity
                toSettingsActivity();
                return true;

            //Deal with add record menu clicked
            case R.id.menu_add_record:
                //Go to the record activity
                toRecordActivity(null);
                return true;

            //Deal with analyze record menu clicked
            case R.id.menu_analyze_record:
                Toast.makeText(this, "Analyze record menu clicked!", Toast.LENGTH_SHORT).show();
                return true;

            //Deal with clear all menu clicked
            case R.id.menu_clear_all:
                //Create a alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //Set the dialog title
                builder.setTitle(getString(R.string.delete_all_title));
                //Set the dialog message
                builder.setMessage(getString(R.string.delete_all_message));
                //Setup the positive button
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete all the weight records in the database
                        databaseHelper.deleteAllRecords();
                        //Refresh page
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        //Kill the current page
                        finish();
                    }
                });
                //Setup the negative button
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                //Create and show the dialog
                builder.create().show();
                //Return the case statement
                return true;

            //Deal with help menu clicked
            case R.id.menu_help:
                Toast.makeText(this, "Help menu clicked!", Toast.LENGTH_SHORT).show();
                return true;

            //Default case
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This function handles the expand floating action button clicked event.
     */
    private void onExpandFabClicked() {
        //Update the clicked flag
        fabExpandClicked = !fabExpandClicked;
        //Set the visibility for the floating action buttons
        setFabVisibility(fabExpandClicked);
        //Set the animation for the floating action buttons
        setFabAnimation(fabExpandClicked);
        //Set the clickable status for the floating action buttons
        setFabClickable(fabExpandClicked);
    }

    /**
     * This function set the visibility for the floating action buttons
     * @param fabExpandClicked Flag for whether the expand floating action buttons was clicked to expand
     */
    private void setFabVisibility(boolean fabExpandClicked) {
        //Check the clicked status
        if (fabExpandClicked) {
            //Show the floating action buttons
            fabAnalyze.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
            fabSettings.setVisibility(View.VISIBLE);
        } else {
            //Hide the floating action buttons
            fabAnalyze.setVisibility(View.INVISIBLE);
            fabAdd.setVisibility(View.INVISIBLE);
            fabSettings.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This function set the animation for the floating action buttons
     * @param fabExpandClicked Flag for whether the expand floating action buttons was clicked to expand
     */
    private void setFabAnimation(boolean fabExpandClicked) {
        //Check the clicked status
        if (fabExpandClicked) {
            //Start the animation for expanding floating action buttons
            fabAnalyze.startAnimation(fromBottom);
            fabAdd.startAnimation(fromBottom);
            fabSettings.startAnimation(fromBottom);
            fabExpand.startAnimation(rotateOpen);
        } else {
            //Start the animation for closing floating action buttons
            fabAnalyze.startAnimation(toBottom);
            fabAdd.startAnimation(toBottom);
            fabSettings.startAnimation(toBottom);
            fabExpand.startAnimation(rotateClose);
        }
    }

    /**
     * This function set the clickable status for the floating action buttons
     * @param fabExpandClicked Flag for whether the expand floating action buttons was clicked to expand
     */
    private void setFabClickable(boolean fabExpandClicked) {
        //Set the clickable status for expanding/closing floating action buttons
        fabAnalyze.setClickable(fabExpandClicked);
        fabAdd.setClickable(fabExpandClicked);
        fabSettings.setClickable(fabExpandClicked);
    }

    /**
     * Helper function to go to the settings activity
     */
    private void toSettingsActivity() {
        //Create an intent to go to the settings activity
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        //Put the record settings object to the intent
        intent.putExtra(KEY_SETTINGS, this.recordSettings);
        //Start the activity
        startActivityForResult(intent, 1);
    }

    /**
     * Helper function to go to the record activity
     * @param weightRecord Weight record to sent to the record activity, null if not applicable
     */
    private void toRecordActivity(WeightRecord weightRecord) {
        //Create an intent to go to the record activity
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        //Put the record settings object to the intent
        intent.putExtra(KEY_SETTINGS, this.recordSettings);
        //Check for the position
        if (weightRecord != null) {
            //Put the weight record object to the intent
            intent.putExtra(KEY_RECORD, weightRecord);
        }
        //Start the activity
        startActivityForResult(intent, 1);
    }


}