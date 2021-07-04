package com.example.weighttracker;

/**
* The BMI class help to calculate the body mass information and categorization
 */
public class BMI {

    //Declare some public constants for upper limits of BMI categories
    public static final double ULIM_UNDERWEIGHT = 18.5;
    public static final double ULIM_NORMAL_WEIGHT = 25;
    public static final double ULIM_OVERWEIGHT = 30;

    //Declare some public constants for BMI categories
    public static final String UNDERWEIGHT = "Underweight";
    public static final String NORMAL_WEIGHT = "Normal weight";
    public static final String OVERWEIGHT = "Overweight";
    public static final String OBESITY = "Obesity";

    //Declare some private fields
    private double height; //Height in meter
    private double mass; //Mass in KG

    /**
     * Construct a BMI object
     * @param height Height in CM
     * @param mass mass in KG
     */
    public BMI(int height, double mass) {
        //Set the height and mass
        this.height = height / 100.0;
        this.mass = mass;
    }

    /**
     * Get the BMI value
     * @return BMI value
     */
    public double getBMIValue() {
        //Return the BMI value
        return mass / (height * height);
    }

    /**
     * Get the BMI value string in two decimal place
     * @return BMI value string in two decimal place
     */
    public String getBMIValueString() {
        //Calculate the BMI
        Double result = getBMIValue();
        //Return the formatted string to two decimal place
        return String.format("%.2f", result);
    }

    /**
     * Get the BMI category
     * @return String for the BMI category
     */
    public String getBMICategory() {
        //Calculate the BMI
        Double result = getBMIValue();
        //Check for the range
        if (result < ULIM_UNDERWEIGHT) {
            //Return underweight
            return UNDERWEIGHT;
        } else if (result < ULIM_NORMAL_WEIGHT) {
            //Return normal weight
            return NORMAL_WEIGHT;
        } else if (result < ULIM_OVERWEIGHT) {
            //Return overweight
            return OVERWEIGHT;
        } else {
            //Return obesity
            return OBESITY;
        }
    }
}
