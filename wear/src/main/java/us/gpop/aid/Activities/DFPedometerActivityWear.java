package us.gpop.aid.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import us.gpop.aid.Data.DFDataSyncWear;
import us.gpop.aid.R;
import us.gpop.aid.Sensors.DFPedometerSensorWear;

/**
 * Created by Michael Yoon Huh on 10/10/2014.
 */
public class DFPedometerActivityWear extends Activity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LAYOUT VARIABLES
    private Boolean isTransmitting = true;

    // SENSOR VARIABLES
    private DFPedometerSensorWear pedSensor;
    private DFDataSyncWear dataSync;
    private int totalSteps = 0; // Stores the total number of steps read from the device.
    private String PEDOMETERKEY = "df_pedometer";

    // THREADING VARIABLES
    private Handler pedHandler = new Handler();

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only runs
    // when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LAYOUT INITIALIZATION
        setUpLayout();

        // PEDOMETER INITIALIZATION
        setUpSensors(); // Starts the pedometer sensors.
        displayPedometer(); // Gets the steps.

        // WEAR SYNCHRONIZATION:
        setUpDataSync(); // Set up the connection.
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the activity is resumed from an onPause() state.
    @Override
    protected void onResume() {
        super.onResume();
        pedSensor.getSensorData(); // Registers the listener.
        startStopAllThreads(true); // Starts all threads.
    }

    // onStart(): This function is called when the activity is started.
    @Override
    protected void onStart() {
        super.onStart();
        dataSync.wearGoogleApiClient.connect();
    }

    // onPause(): This function is called whenever the current activity is suspended or another
    // activity is launched.
    @Override
    public void onPause() {
        super.onPause();
        dataSync.wearGoogleApiClient.disconnect();
    }

    // onStop(): This function runs when screen is no longer visible and the activity is in a
    // state prior to destruction.
    @Override
    protected void onStop() {
        super.onStop();
        startStopAllThreads(false);
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    @Override
    protected void onDestroy() { super.onDestroy(); }

    /** LAYOUT FUNCTIONALITY ___________________________________________________________________ **/

    private void setUpLayout() {

        setContentView(R.layout.activity_dfpedometer_wear); // Sets the XML layout file for the activity class.
        setUpButtons(); // Sets up button listeners.
    }

    private void setUpButtons() {

        final ImageButton pedometer_toggle_btn = (ImageButton) findViewById(R.id.df_pedometer_transmit_btn);
        pedometer_toggle_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Is transmitting...
                if (isTransmitting) {
                    isTransmitting = false;
                    pedometer_toggle_btn.setImageResource(R.drawable.my_network);
                }

                // If not transmitting...
                else {
                    isTransmitting = true;
                    pedometer_toggle_btn.setImageResource(R.drawable.my_network_on);
                }
            }
        });
    }

    /** DATA SYNC FUNCTIONALITY ________________________________________________________________ **/

    // setUpDataSync(): Sets up the data syncing functionality.
    private void setUpDataSync() {

        // INITIALIZATION:
        dataSync = new DFDataSyncWear();
        dataSync.setUpDataConnection(this); // Sets up the Google API connection.
    }

    /** PEDOMETER SENSOR _______________________________________________________________________ **/

    // setUpSensors(): Sets up the sensors.
    private void setUpSensors() {

        // INITIALIZATION
        pedSensor = new DFPedometerSensorWear();
        pedSensor.setUpSensors(this); // Initiates pedometer sensors.
    }

    /** THREADING FUNCTIONALITY ________________________________________________________________ **/

    // pedometerUpdates(): A separate thread for handling the update of the walking counter.
    private Runnable pedometerUpdates = new Runnable() {

        public void run() {

            displayPedometer();

            // If transmitting button is enabled, pedometer data will be constantly sent to the mobile device.
            if (isTransmitting) {
                dataSync.setWearData(DFPedometerActivityWear.this, totalSteps, PEDOMETERKEY); // Send data to the wear.
            }

            pedHandler.postDelayed(this, 1000); // Updates the thread for 1000 ms.
        }
    };

    // startStopAllThreads(): Resumes or stops all threads.
    private void startStopAllThreads(Boolean isStart) {

        // Starts all threads.
        if (isStart) { pedHandler.postDelayed(pedometerUpdates, 1000); } // Updates the thread.

        // Stops all threads.
        else { pedHandler.removeCallbacks(pedometerUpdates); }
    }

    /** ADDITIONAL FUNCTIONALITY _______________________________________________________________ **/

    // displayPedometer(): Retrieves total number of steps.
    private void displayPedometer() {

        totalSteps = pedSensor.getCurrentSteps(); // Returns number of steps from sensor.

        // Sets the text for the TextView object.
        TextView pedText = (TextView) findViewById(R.id.df_pedometer_count);
        pedText.setText(String.valueOf(totalSteps));
    }
}