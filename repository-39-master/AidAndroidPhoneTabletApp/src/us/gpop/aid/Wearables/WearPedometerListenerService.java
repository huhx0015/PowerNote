package us.gpop.aid.Wearables;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael Yoon Huh on 10/11/2014.
 */

public class WearPedometerListenerService extends WearableListenerService {

    private static final String TAG = "WearPedometerListenerService";
    
    // PREFERENCES
    private SharedPreferences AID_prefs; // SharedPreferences objects that store settings for the application.
    private SharedPreferences.Editor AID_prefs_editor; // SharedPreferences.Editor objects that are used for editing the temporary preferences.
    private static final String AID_OPTIONS = "aid_pref"; // Used to reference the name of the preference XML file.
        
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        
        Log.d(TAG, "onDataChanged: " + dataEvents);
        
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        
        // Establish a connection using Google API.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
        	.addApi(Wearable.API)
        	.build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
        	Log.e(TAG, "Failed to connect to GoogleApiClient.");
        	return;
        }

        // Attempts to retrieve the data from Android Wear device.
        for (DataEvent event : events) {
            
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            
            if ("/dreamforcedata".equals(path)) {
                
                String PEDOMETERKEY = "df_pedometer";
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                int pedValues = map.getInt(PEDOMETERKEY);
                
                // Stores the value in preferences.
                AID_prefs = getSharedPreferences(AID_OPTIONS, MODE_PRIVATE); // Main preferences variable.
                AID_prefs_editor = AID_prefs.edit();
                AID_prefs_editor.putInt(PEDOMETERKEY, 0); // Stores the retrieved integer value.
                AID_prefs_editor.putBoolean("df_isWearDataSent", true);
                Log.d(TAG, "PEDOMETER VALUE: " + pedValues); // LOGGING
            }
        }
    }
}