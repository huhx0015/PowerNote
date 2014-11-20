package us.gpop.us.aid.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Michael Yoon Huh on 10/10/2014.
 */
public class DFPedometerSensorWear implements SensorEventListener {

    public Context context;

    // PEDOMETER VARIABLES
    int totalSteps = 0; // Total number of steps.
    public Sensor mStepCounterSensor;
    public Sensor mStepDetectorSensor;
    public SensorManager sensorManager;
    private Boolean isFirstDetection = true; // Used to determine if first sensor action has occurred or not.

    // LOGGING
    private static String TAG = "DFPEDOMETERSENSOR";

    /** SENSOR EXTENSIONS ______________________________________________________________________ **/

    // onSensorChanged(): When the sensor detects an update, this function is run.
    @Override
    public void onSensorChanged(SensorEvent event) {

        int newStepsSinceLast = 0;

        switch (event.sensor.getType()) {

            case Sensor.TYPE_STEP_DETECTOR:

                totalSteps = totalSteps + 1; // Increments the steps.
                break;

            case Sensor.TYPE_STEP_COUNTER:

                // If this is the first sensor detection, retrieve the step counter.
                if (isFirstDetection) {
                    totalSteps = (int) event.values[0]; // Sets the total number of steps at the initialization of sensor.
                    Log.d(TAG, "STEPS UPDATED."); // LOG
                }
                break;
        }
    }

    // onAccuracyChanged(): When the accuracy of the sensor has been changed, this function is run.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /** SENSOR FUNCTIONALITY ___________________________________________________________________ **/

    // getCurrentSteps(): Returns the current number of steps from the pedometer sensor.
    public int getCurrentSteps() {
        return totalSteps;
    }

    // setUpSensors(): Sets up the sensors.
    public void setUpSensors(Context con) {

        // Set up the sensors.
        sensorManager = (SensorManager) con.getSystemService(con.SENSOR_SERVICE);
        mStepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    // getSensorData(): This function retrieves the sensor data.
    public void getSensorData() {

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) { sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL); } // Experimental
        else {
            Log.d(TAG, "SENSOR NOT AVAILABLE!"); // LOG
        }
    }

    // unregisterSensors(): Turns off pedometer sensor. (NOTE: DON'T DO THIS FOR MOBILE, AS IT TURNS
    // SENSOR OFF COMPLETELY).
    public void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }

}
