package dreamforce.gpop.us.dreamforce.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import us.gpop.aid.R;

public class DFMainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // PEDOMETER VARIABLES
    private int pedometerData = 0;

    // DATA LAYER VARIABLES
    GoogleApiClient wearGoogleApiClient; // Used to setup the data connection between wear and phone.
    private String PEDOMETERKEY = "df_pedometer";

    // SYSTEM VARIABLES
    private static String TAG = "DFLOGINACTIVITY";

    // THREADING
    private Handler pedHandler = new Handler();

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only runs
    // when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfmain);

        // WEAR SYNCHRONIZATION:
        setUpDataConnection(); // Set up the connection.
    }

    @Override
    protected void onStart() {
        super.onStart();
        wearGoogleApiClient.connect();
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
        Wearable.DataApi.removeListener(wearGoogleApiClient, this);
        Wearable.MessageApi.removeListener(wearGoogleApiClient, this);
        Wearable.NodeApi.removeListener(wearGoogleApiClient, this);
        wearGoogleApiClient.disconnect();
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

    // Send Data to the Wear.
    private void setWearData(int value, String dataType) {

        String path = getFilesDir() + "/dreamforcedata"; // Stores the path location to the data.

        // Sync Data with a Data Map:
        PutDataMapRequest wearDataMap = PutDataMapRequest.create(path); // Setting the path of the data item.

        // Sets the data to send to the wearable.
        wearDataMap.getDataMap().putInt(dataType, value); // Places the data

        // Call PutDataMapRequest.asPutDataRequest() to obtain a PutDataRequest object.
        PutDataRequest request = wearDataMap.asPutDataRequest();

        // Call DataApi.putDataItem() to request the system to create the data item.
        // Note: If the handset and wearable devices are disconnected, the data is buffered and and
        // synced when the connection is re-established.
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(wearGoogleApiClient, request);

        Toast.makeText(this, "SENDING DATA TO WEAR", Toast.LENGTH_SHORT).show();
    }

    // Get Data from the Wear.
    private void getWearData(final String dataType) {

        //Toast.makeText(this, "ATTEMPTING TO GET DATA FROM WEAR", Toast.LENGTH_SHORT).show();

        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(wearGoogleApiClient);
        results.setResultCallback(new ResultCallback<DataItemBuffer>() {

            @Override
            public void onResult(DataItemBuffer dataItems) {

                //Toast.makeText(DFLoginActivity.this, "ATTEMPT SUCCESSFUL!", Toast.LENGTH_SHORT).show();

                //int pedometerData = 0;

                if (dataItems.getCount() != 0) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItems.get(0));

                    // This should read the correct value.
                    pedometerData = dataMapItem.getDataMap().getInt(dataType);
                }

                displayPedometer(pedometerData); // Updates the TextView object with the pedometer data.

                dataItems.release();
            }
        });
    }

    // Sets up the data connection between the wear and phone.
    /*
    private void setUpDataConnection() {

        wearGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
*/

    // Sets up the data connection between the wear and phone.
    private void setUpDataConnection() {

        wearGoogleApiClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle connectionHint) {

                        //Toast.makeText(DFLoginActivity.this, "CONNECTED", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API

                        getWearData(PEDOMETERKEY); // Gets pedometer data from the wear.
                        //setWearData(); // Send data to the wear.
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {

                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })

                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        // wearGoogleApiClient.connect(); // Connect to the api.
    }


    /** ADDITIONAL FUNCTIONALITY _______________________________________________________________ **/

    private void displayPedometer(int pedometerData) {

        TextView stepCount = (TextView) findViewById(R.id.df_main_step_count);
        stepCount.setText(String.valueOf(pedometerData)); // Sets the total steps.
    }

    // startStopAllThreads(): Resumes or stops all threads.
    private void startStopAllThreads(Boolean isStart) {

        // Starts all threads.
        if (isStart) {
            pedHandler.postDelayed(pedometerUpdates, 2500); // Updates the thread.
        }

        // Stops all threads.
        else {
            pedHandler.removeCallbacks(pedometerUpdates);
        }
    }

    // pedometerUpdates(): A separate thread for handling the update of the walking counter.
    private Runnable pedometerUpdates = new Runnable() {

        public void run() {

            getWearData(PEDOMETERKEY);
            displayPedometer(pedometerData); // Updates the TextView object with the pedometer data.
            pedHandler.postDelayed(this, 2500); // Updates the thread for 1000 ms.
        }
    };

    /** IMPLEMENTATION METHODS _______________________________________________________ **/

    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(this, "CONNECTED", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(wearGoogleApiClient, this);
        Wearable.MessageApi.addListener(wearGoogleApiClient, this);
        Wearable.NodeApi.addListener(wearGoogleApiClient, this);

        getWearData(PEDOMETERKEY); // Gets data from the wear.
        //setWearData(totalSteps, PEDOMETERKEY); // Send data to the wear.
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            }

            else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(this, "CONNECTION FAILED", Toast.LENGTH_SHORT).show();
    }
}
