package com.example.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * DatabaseHelper class hold the functions to access database
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Declare some constants
    private static final String DATABASE_NAME = "WeightRecords.db";
    private static final int DATABASE_VERSION = 1;
    //Columns common to both settings and records tables
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_HEIGHT = "height";
    //Settings table columns
    private static final String TABLE_SETTINGS = "table_settings";
    private static final String COLUMN_BIRTHDATE = "birthdate";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_GOAL_WEIGHT = "goal_weight";
    private static final String COLUMN_BEST_FIT_TYPE = "best_fit_type";
    private static final String COLUMN_BEST_FIT_ORDER = "best_fit_order";
    //Records table columns
    private static final String TABLE_RECORDS = "table_records";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_TIME = "timestamp";
    private static final String COLUMN_COMMENT = "comment";

    //Declare some private fields
    private Context context; //Hold the context


    /**
     * Default constructor for the DatabaseHelper class
     * @param context Activity context
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Save the context
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Format the query string to create settings table
        String settingsTableQuery = "CREATE TABLE "+TABLE_SETTINGS+
                " ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_BIRTHDATE+" INTEGER, "+
                COLUMN_HEIGHT + " INTEGER, "+
                COLUMN_GENDER + " TEXT, "+
                COLUMN_GOAL_WEIGHT+ " REAL, "+
                COLUMN_BEST_FIT_TYPE + " TEXT, "
                +COLUMN_BEST_FIT_ORDER + " INTEGER);";
        //Execute the query to create settings table
        db.execSQL(settingsTableQuery);

        //Format the query string to create record table
        String recordsTableQuery = "CREATE TABLE "+TABLE_RECORDS+
                " ("+COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_WEIGHT + " REAL, "+
                COLUMN_HEIGHT + " INTEGER, "+
                COLUMN_TIME + " INTEGER, "+
                COLUMN_COMMENT + " TEXT);";
        //Execute the query to create record table
        db.execSQL(recordsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the settings and records tables if necessary
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECORDS);
        //Call the on create method to create settings and records tables
        onCreate(db);
    }

    /**
     * Retrieve the settings information from the settings table
     * @return RecordSettings object contains the information from the settings table, null if no information is stored in the settings table
     */
    public RecordSettings retrieveSettings() {
        //Hold the record settings object
        RecordSettings recordSettings = null;
        //Get the database
        SQLiteDatabase db = this.getReadableDatabase();
        //Format the query string
        String query = "SELECT * FROM "+TABLE_SETTINGS;
        //Query the database
        Cursor cursor = db.rawQuery(query, null);
        //Check the cursor
        if (cursor.getCount() > 0) {
            //Make sure the cursor is at the start
            cursor.moveToFirst();
            //Initialize the record settings with settings information retrieved from the settings table
            recordSettings = new RecordSettings(
                    cursor.getInt(1) * ((long) 1000), //Get the birthdate
                    cursor.getInt(2), //Get the height
                    cursor.getString(3), //Get the gender
                    cursor.getDouble(4), //Get the goal weight
                    cursor.getString(5), //Get the best fit type
                    cursor.getInt(6) //Get the best fit order
            );
        }
        //Close the cursor
        cursor.close();
        //Return the record settings object
        return recordSettings;
    }

    /**
     * Save the settings information to the settings table
     * @param recordSettings RecordSettings object containing the information to be saved to the settings table
     * @return True if the record are saved successfully, false otherwise
     */
    public boolean saveSettings(RecordSettings recordSettings) {
        //Get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Hold the query result
        long result = -1;
        //Setup the content values
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BIRTHDATE, recordSettings.getBirthdate()/1000);
        cv.put(COLUMN_HEIGHT, recordSettings.getHeight());
        cv.put(COLUMN_GENDER, recordSettings.getGender());
        cv.put(COLUMN_GOAL_WEIGHT, recordSettings.getGoal_weight());
        cv.put(COLUMN_BEST_FIT_TYPE, recordSettings.getBest_fit_type());
        cv.put(COLUMN_BEST_FIT_ORDER, recordSettings.getBest_fit_order());

        //Format the query string to retrieve settings table information
        String queryRetrieve = "SELECT * FROM " + TABLE_SETTINGS;
        //Query the database
        Cursor cursor = db.rawQuery(queryRetrieve, null);
        //Check the cursor
        if (cursor.getCount() == 0) {
            //If no data existed, add a new data row
            result = db.insert(TABLE_SETTINGS, null, cv);
        } else {
            //Move the cursor to the first row
            cursor.moveToFirst();
            //If data existed, update the data row
            result = db.update(TABLE_SETTINGS, cv, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
        }
        //Close the cursor
        cursor.close();
        //Return the function
        return (result != -1);
    }

    /**
     * Add a weight record to the records table
     * @param weightRecord weight record object
     * @return True if the weight record is added successfully, false otherwise
     */
    public boolean addRecord(WeightRecord weightRecord) {
        //Get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Setup the content values
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WEIGHT, weightRecord.getWeight());
        cv.put(COLUMN_HEIGHT, weightRecord.getHeight());
        cv.put(COLUMN_TIME, weightRecord.getTimestamp()/1000);
        cv.put(COLUMN_COMMENT, weightRecord.getComment());
        //Insert to the database
        long result = db.insert(TABLE_RECORDS, null, cv);
        //Return the function
        return (result != -1);
    }

    /**
     * Update a row in the weight record table
     * @param weightRecord weight record object
     * @return True if the weight record is updated successfully, false otherwise
     */
    public boolean updateRecord(WeightRecord weightRecord) {
        //Get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Setup the content values
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WEIGHT, weightRecord.getWeight());
        cv.put(COLUMN_HEIGHT, weightRecord.getHeight());
        cv.put(COLUMN_TIME, weightRecord.getTimestamp()/1000);
        cv.put(COLUMN_COMMENT, weightRecord.getComment());
        //Update the database
        int result = db.update(TABLE_RECORDS, cv, "_id=?", new String[]{ String.valueOf(weightRecord.getId())});
        //Return the function
        return (result != -1);
    }

    /**
     * Read all the weight record from the weight record table
     * @return An array list of weight record
     */
    public ArrayList<WeightRecord> readAllRecords(){
        //Initialize the weight record array list
        ArrayList<WeightRecord> weightRecords = new ArrayList<>();

        //Get the database
        SQLiteDatabase db = this.getReadableDatabase();
        //Format the query string
        String query = "SELECT * FROM "+TABLE_RECORDS + " ORDER BY "+COLUMN_TIME + " DESC";
        //Query the database
        Cursor cursor = db.rawQuery(query, null);
        //Check the cursor
        if (cursor.getCount() > 0) {
            //Loop through the cursor
            while (cursor.moveToNext()) {
                //Retrieve the record row information and create the weight record object
                WeightRecord weightRecord = new WeightRecord(
                        cursor.getInt(0), //Get the ID
                        cursor.getDouble(1), //Get the weight
                        cursor.getInt(2), //Get the height
                        cursor.getInt(3) * ((long) 1000), //Get the time
                        cursor.getString(4) //Get the comment
                );
                //Add the weight record to the array list
                weightRecords.add(weightRecord);
            }
        }
        //Return the weight records array list
        return weightRecords;
    }

    /**
     * Delete all the weight records in the weight records table
     */
    public void deleteAllRecords() {
        //Get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Execute the query to delete all database
        db.execSQL("DELETE FROM "+TABLE_RECORDS);
    }


    /**
     * Delete one weight record from the database where its ID matched the input parameter
     * @param recordID Weight record ID for the record to be deleted
     * @return True if the record is deleted successfully, false otherwise
     */
    public boolean deleteOneRecord(String recordID) {
        //Get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //Delete the record row
        long result = db.delete(TABLE_RECORDS, "_id=?", new String[]{recordID});
        //Return the result
        return (result != -1);
    }
}
