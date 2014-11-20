package dreamforce.gpop.us.dreamforce.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import dreamforce.gpop.us.dreamforce.Data.DFDataSync;
import dreamforce.gpop.us.dreamforce.R;

/**
 * Created by Michael Yoon Huh on 10/10/2014.
 */
public class DFPedometerActivity extends Activity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // DATA LAYER VARIABLES
    private DFDataSync dataSync;
    private String PEDOMETERKEY = "df_pedometer";

    // PEDOMETER VARIABLES
    private int pedometerData = 0;

    // THREADING VARIABLES
    private Handler pedHandler = new Handler();

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only runs
    // when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfmain);

        // WEAR SYNCHRONIZATION:
        setUpDataSync(); // Set up the connection.
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSync.wearGoogleApiClient.connect();
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the activity is resumed from an onPause() state.
    @Override
    protected void onResume() {
        super.onResume();
        startStopAllThreads(true);
    }

    // onPause(): This function is called whenever the current activity is suspended or another
    // activity is launched.
    @Override
    public void onPause() {
        super.onPause();
        dataSync.wearGoogleApiClient.disconnect();
        startStopAllThreads(false);
    }

    // onStop(): This function runs when screen is no longer visible and the activity is in a
    // state prior to destruction.
    @Override
    protected void onStop() {
        super.onStop();
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    @Override
    protected void onDestroy() { super.onDestroy(); }

    /** ACTIVITY EXTENSION FUNCTIONALITY _______________________________________________________ **/

    // onActivityResult(): This method handles end of activity results.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dfmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** DATA SYNC FUNCTIONALITY ________________________________________________________________ **/

    private void setUpDataSync() {

        // INITIALIZATION:
        dataSync = new DFDataSync();
        dataSync.setUpDataConnection(this);
    }

    /** ADDITIONAL FUNCTIONALITY _______________________________________________________________ **/

    private void displayPedometer(int pedometerData) {

        TextView stepCount = (TextView) findViewById(R.id.df_main_step_count);
        stepCount.setText(String.valueOf(pedometerData)); // Sets the total steps.
    }

    // startStopAllThreads(): Resumes or stops all threads.
    private void startStopAllThreads(Boolean isStart) {

        // Starts all threads.
        if (isStart) { pedHandler.postDelayed(pedometerUpdates, 1000); } // Updates the thread.

        // Stops all threads.
        else { pedHandler.removeCallbacks(pedometerUpdates); }
    }

    // pedometerUpdates(): A separate thread for handling the update of the walking counter.
    private Runnable pedometerUpdates = new Runnable() {

        public void run() {

            dataSync.getWearData(PEDOMETERKEY); // Retrieves the pedometer data with Android Wear.
            displayPedometer(pedometerData); // Updates the TextView object with the pedometer data.
            pedHandler.postDelayed(this, 1000); // Updates the thread for 1000 ms.
        }
    };
}
