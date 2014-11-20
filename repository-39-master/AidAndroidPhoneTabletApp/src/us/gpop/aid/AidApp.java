package us.gpop.aid;

import java.util.Date;

import org.json.JSONObject;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.smartstore.app.SalesforceSDKManagerWithSmartStore;
import com.salesforce.androidsdk.smartstore.store.DBHelper;
import com.salesforce.androidsdk.smartstore.store.DBOpenHelper;
import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartstore.store.SmartStore.Type;

/**
 * Holds app singleton resources and settings.
 * 
 */
public class AidApp extends Application {
	
	public static final String STEPS_RECEIVED_INTENT_ACTION = 
			AidApp.class.getName() + ".STEPS_RECEIVED_INTENT_ACTION";

	public static final String COMMENTS_TABLE_NAME = "my_comments_table";

	private static final String EXAMPLE_CHART_NAME = "my_example_chart";

	private static final String CHART_NAME_PREF_KEY = AidApp.class.getName();

	private static AidApp app;

	public SQLiteDatabase db;

	public SmartStore store;

	private SharedPreferences prefs;

	protected SQLiteDatabase getWritableDatabase() {
		return DBOpenHelper.getOpenHelper(this, null).getWritableDatabase("");
	}

	public static AidApp getInstance() {
		return app;
	}

	public String getCurrentChartName() {
		return prefs.getString(CHART_NAME_PREF_KEY, null);
	}

	public void setCurrentChartName(final String newChartName) {
		final Editor editor = prefs.edit();
		editor.putString(CHART_NAME_PREF_KEY, newChartName);
		editor.commit();
	}

	public void resetData() {
		DBHelper.getInstance(db).reset(this, null); // start clean
		db = getWritableDatabase();
		store = new SmartStore(db);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		app = this;
		SalesforceSDKManagerWithSmartStore.initNative(
				getApplicationContext(), 
				new KeyImpl(), 
				LoginActivity.class);


		super.onCreate();

		// SalesforceSDKManager.getInstance().setIsTestRun(true);
		// DBHelper.getInstance(db).reset(this, null); // start clean

		db = getWritableDatabase();
		store = new SmartStore(db);

		// Add example data if there is nothing
		if (!store.hasSoup(EXAMPLE_CHART_NAME)) {
			// if ( store.getAllSoupNames().isEmpty() ) {
			setCurrentChartName(EXAMPLE_CHART_NAME);

			app.store.registerSoup(EXAMPLE_CHART_NAME,
					new IndexSpec[] {
					new IndexSpec("timestamp", Type.integer),
					// new IndexSpec("value", Type.string)
					});
			EnterDataActivity.addExampleData(store, EXAMPLE_CHART_NAME, null);
		}

		if (!store.hasSoup(COMMENTS_TABLE_NAME)) {
			app.store.registerSoup(COMMENTS_TABLE_NAME,
					new IndexSpec[] {
					new IndexSpec("timestamp", Type.integer),
					});
			addExampleComment(store, COMMENTS_TABLE_NAME);
		}
	}

	public static void addExampleComment(final SmartStore store, final String chartName) {
		try {

			final long currentTime = new Date().getTime();
			final long halfMinuteAgo = currentTime - (30 * 1000);
			final long aMinuteAgo = currentTime - (60 * 1000);

			{
				JSONObject newMessage = new JSONObject();
				newMessage.put("stepsDot", 200f);
				newMessage.put("sleepDot", 340);
				newMessage.put("heartDot", 200f);
				newMessage.put("message",
						"Just another general check up. Glad to see my health getting better every visit!");
				newMessage.put("timestamp", currentTime);
				JSONObject createdRecord = store.create(chartName, newMessage);
			}

			{
				JSONObject newMessage = new JSONObject();
				newMessage.put("stepsDot", 20f);
				newMessage.put("sleepDot", 340);
				newMessage.put("heartDot", 200f);
				newMessage.put("message",
						"Doctor said to get more sleep!");
				newMessage.put("timestamp", aMinuteAgo);
				JSONObject createdRecord = store.create(chartName, newMessage);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public void onTerminate() {
		app.db.close();
		super.onTerminate();
	}

}
