package dreamforce.gpop.us.dreamforce.Data;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Created by Michael Yoon Huh on 10/10/2014.
 */
public class DFDataSync implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    // GOOGLE API VARIABLES
    public GoogleApiClient wearGoogleApiClient; // Used to setup the data connection between wear and phone.

    // LOGGING VARIABLES
    private final static String TAG = "DFDATASYNC";

    /** DATA SYNCING PROTOTYPE _________________________________________________________________ **/

    // Send Data to the Wear.
    public void setWearData(Context con, int value, String dataType) {

        String path = con.getFilesDir() + "/dreamforcedata"; // Stores the path location to the data.

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
    }

    // Get Data from the Wear.
    public int getWearData(final String dataType) {

        final int[] pedometerData = {0};

        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(wearGoogleApiClient);
        results.setResultCallback(new ResultCallback<DataItemBuffer>() {

            @Override
            public void onResult(DataItemBuffer dataItems) {

                if (dataItems.getCount() != 0) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItems.get(0));

                    // This should read the correct value.
                    pedometerData[0] = dataMapItem.getDataMap().getInt(dataType);
                }

                dataItems.release();
            }
        });

        return pedometerData[0];
    }

    // Sets up the data connection between the wear and phone.
    public void setUpDataConnection(Context con) {

        wearGoogleApiClient = new GoogleApiClient.Builder(con)

                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
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
    }

    /** IMPLEMENTATION METHODS _______________________________________________________ **/

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(wearGoogleApiClient, this);
        Wearable.MessageApi.addListener(wearGoogleApiClient, this);
        Wearable.NodeApi.addListener(wearGoogleApiClient, this);
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
        Log.e(TAG, "onConnectionFailed(): Failed to connect");
    }
}
