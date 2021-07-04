package com.example.weighttracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The RecordSettings class hold the general settings information
 */
public class RecordSettings implements Serializable {

    //Declare public constants for gender
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";

    //Declare public constants for best fit type
    public static final String LINEAR = "Linear";
    public static final String LOGARITHMIC = "Logarithmic";
    public static final String POLYNOMIAL = "Polynomial";
    public static final String POWER = "Power";
    public static final String EXPONENTIAL = "Exponential";


    //Declare some private fields
    private long birthdate; //User birth date timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
    private int height; //User height in CM
    private String gender; //User gender
    private double goal_weight; //User goal weight in KG
    private String best_fit_type; //Best fit type for analysis
    private int best_fit_order; //Order of equation for polynomial best fit type

    /**
     * Construct the record settings
     * @param birthdate User birth date timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @param height User height in CM
     * @param gender User gender
     * @param goal_weight User goal weight in KG
     * @param best_fit_type Best fit type for analysis
     * @param best_fit_order //Order of equation for polynomial best fit type
     */
    public RecordSettings(long birthdate, int height, String gender, double goal_weight, String best_fit_type, int best_fit_order) {
        //Set the fields
        this.birthdate = birthdate;
        this.height = height;
        this.gender = gender;
        this.goal_weight = goal_weight;
        this.best_fit_type = best_fit_type;
        this.best_fit_order = best_fit_order;
    }

    /**
     * Get the birthdate timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @return Birthdate timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     */
    public long getBirthdate() {
        return birthdate;
    }

    /**
     * Set the birthdate timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @param birthdate Birthdate timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     */
    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Get the user height in CM
     * @return User height in CM
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the user height in CM
     * @param height User height in CM
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the user gender
     * @return User gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set the user gender
     * @param gender User gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get the user goal weight in KG
     * @return User goal weight in KG
     */
    public double getGoal_weight() {
        return goal_weight;
    }

    /**
     * Set the user goal weight in KG
     * @param goal_weight User goal weight in KG
     */
    public void setGoal_weight(double goal_weight) {
        this.goal_weight = goal_weight;
    }

    /**
     * Get the best fit type for analysis
     * @return Best fit type for analysis
     */
    public String getBest_fit_type() {
        return best_fit_type;
    }

    /**
     * Set the best fit type for analysis
     * @param best_fit_type Best fit type for analysis
     */
    public void setBest_fit_type(String best_fit_type) {
        this.best_fit_type = best_fit_type;
    }

    /**
     * Get the order of the equation for polynomial best fit type
     * @return Order of the equation for polynomial best fit type
     */
    public int getBest_fit_order() {
        return best_fit_order;
    }

    /**
     * Set the order of the equation for polynomial best fit type
     * @param best_fit_order Order of the equation for polynomial best fit type
     */
    public void setBest_fit_order(int best_fit_order) {
        this.best_fit_order = best_fit_order;
    }

    /**
     * Helper function to get the birthdate string in MM/dd/yyyy format
     * @return Birthdate string in MM/dd/yyyy format
     */
    public String getBirthdateString() {
        //Create a helper date object
        Date date = new java.util.Date(this.birthdate);
        //Create a date formatter
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        //Format and return the date string
        return dateFormat.format(date);
    }
}
