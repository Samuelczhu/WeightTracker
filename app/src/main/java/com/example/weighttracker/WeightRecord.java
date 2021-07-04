package com.example.weighttracker;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The WeightRecord class hold the information for one weight record
 */
public class WeightRecord implements Serializable {

    //Declare some private fields
    private int id;    //Weight record ID
    private double weight;    //Record weight in KG
    private int height;    //Record height in CM
    private long timestamp;    //Record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
    private String comment;    //Record comment

    /**
     * Construct a weight record
     * @param id Weight record id
     * @param weight record weight in KG
     * @param height record height in CM
     * @param timestamp record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @param comment record comment
     */
    public WeightRecord(int id, double weight, int height, long timestamp, String comment) {
        //Set the fields
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    /**
     * Get the weight record id
     * @return integer for the weight record id
     */
    public int getId() {
        return id;
    }

    /**
     * Get record weight in KG
     * @return record weight in KG
     */
    public double getWeight() {
        //Return the record weight
        return weight;
    }

    /**
     * Get record height in CM
     * @return record height in CM
     */
    public int getHeight() {
        //Return the record height
        return height;
    }

    /**
     * Get record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @return record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     */
    public long getTimestamp() {
        //Return the record timestamp
        return timestamp;
    }

    /**
     * Get record comment
     * @return record comment
     */
    public String getComment() {
        //Return the record comment
        return comment;
    }

    /**
     * Set the record weight in KG
     * @param weight record weight in KG
     */
    public void setWeight(double weight) {
        //Set the record weight
        this.weight = weight;
    }

    /**
     * Set the record height in CM
     * @param height record height in CM
     */
    public void setHeight(int height) {
        //Set the record height
        this.height = height;
    }

    /**
     * Set the record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     * @param timestamp record timestamp as Unix Time, the number of milliseconds since 1970-01-01 00:00:00 UTC.
     */
    public void setTimestamp(long timestamp) {
        //Set the record timestamp
        this.timestamp = timestamp;
    }

    /**
     * Set the record comment
     * @param comment record comment
     */
    public void setComment(String comment) {
        //Set the record comment
        this.comment = comment;
    }

    /**
     * Get the timestamp string in MM/dd/yyyy hh:mm format
     * @return string for record timestamp in MM/dd/yyyy hh:mm format
     */
    public String getTimestampString() {
        //Create a helper date object
        Date dateTime = new java.util.Date(this.timestamp);
        //Create a date formatter
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //Format and return the date string
        return dateFormat.format(dateTime);
    }
}
