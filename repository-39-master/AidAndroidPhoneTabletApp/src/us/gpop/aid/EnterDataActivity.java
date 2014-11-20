
package us.gpop.aid;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.salesforce.androidsdk.smartstore.store.DBHelper;
import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.QuerySpec.Order;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartstore.store.SmartStore.Type;
import us.gpop.aid.R;

/**
 * Allows creating / removing / viewing charts.
 *
 */
public class EnterDataActivity extends Activity {

	private AidApp app;
	private TextView textView;
	private EditText editTextView;
	private Button createChartButton;
	private Button deleteChartButton;
	private Button addDataButton;
	private Button readDataButton;
	private Button resetDataButton;
	
	private EditText editTextViewSteps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = AidApp.getInstance();

		setContentView(R.layout.activity_enter_data);
		textView = (TextView) findViewById(R.id.textView);
		editTextView = (EditText) findViewById(R.id.editTextView);
		editTextViewSteps = (EditText) findViewById(R.id.editTextViewSteps);
		createChartButton = (Button) findViewById(R.id.createChartButton);
		deleteChartButton = (Button) findViewById(R.id.deleteChartButton);
		addDataButton = (Button) findViewById(R.id.addDataButton);
		readDataButton = (Button) findViewById(R.id.readDataButton);
		resetDataButton = (Button) findViewById(R.id.resetDataButton);

		createChartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chartName = editTextView.getText().toString();
				app.store.registerSoup(chartName,
						new IndexSpec[] {
								new IndexSpec("timestamp", Type.integer),
//								new IndexSpec("value", Type.string)
						});
				app.setCurrentChartName(chartName);
				listCharts();
			}
		});

		deleteChartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chartName = editTextView.getText().toString();
				app.setCurrentChartName(null);
				app.store.dropSoup(chartName);
				listCharts();
			}
		});

		resetDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				app.resetData();
				
			}
		});

				
		addDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chartName = editTextView.getText().toString();
				
				final String stepCountText = editTextViewSteps.getText().toString();
				try {
					final int stepCountInt = Integer.parseInt(stepCountText);
				
					addExampleData(app.store, chartName, stepCountInt);
				} catch(Exception e) {
					addExampleData(app.store, chartName, null);
				}
			}
		});

		readDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chartName = editTextView.getText().toString();
				readData(chartName);
			}
		});

		listCharts();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		final String currentChartName = app.getCurrentChartName();
		if ( null != currentChartName ) {
			editTextView.setText(currentChartName);
		}
		
	}



	private void readData(final String chartName) {
		try {

			listCharts();
			
			// Query all - large page
			JSONArray result = app.store.query(
					QuerySpec.buildAllQuerySpec(
							chartName, "timestamp", Order.ascending, 1000), 0);

			textView.append("\n\nQuery results:\n");
			
			for (int i = 0; i < result.length(); i++ ) {
				final JSONObject row = result.getJSONObject(i);
				
				textView.append("\tdata row: " + row + "\n");
			}			
			
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void addExampleData(final SmartStore store, final String chartName,
			final Integer stepCount) {
		try {

			final long currentTime = new Date().getTime();
			final long halfMinuteAgo = currentTime - (30 * 1000);
			final long aMinuteAgo = currentTime - (60 * 1000);
			
			if ( null != stepCount ) {

				JSONObject chartDataPoint = new JSONObject();
				chartDataPoint.put("steps", stepCount);
				chartDataPoint.put("timestamp", currentTime);
				JSONObject createdRecord = store.create(chartName, chartDataPoint);
			} else {
			
			JSONObject chartDataPoint = new JSONObject();
			chartDataPoint.put("steps", 100);
			chartDataPoint.put("timestamp", aMinuteAgo);
			JSONObject createdRecord = store.create(chartName, chartDataPoint);

			chartDataPoint = new JSONObject();
			chartDataPoint.put("steps", 50);
			chartDataPoint.put("timestamp", halfMinuteAgo);
			createdRecord = store.create(chartName, chartDataPoint);

			chartDataPoint = new JSONObject();
			chartDataPoint.put("steps", 16);
			chartDataPoint.put("timestamp", currentTime);
			createdRecord = store.create(chartName, chartDataPoint);
			}

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private void listCharts() {
		textView.setText("Existing charts:\n");

		for (final String soupName : app.store.getAllSoupNames()) {
			textView.append("\tChart name: " + soupName + "\n");
		}

		textView.append("\n\n");
	}

	/**
	 * Helper method to check that a table exists in the database
	 * 
	 * @param tableName
	 * @return
	 */
	protected boolean hasTable(String tableName) {
		Cursor c = null;
		try {
			c = DBHelper.getInstance(app.db).query(app.db, "sqlite_master", null, null, null, "type = ? and name = ?", "table",
					tableName);
			return c.getCount() == 1;
		} finally {
			safeClose(c);
		}
	}

	/**
	 * Close cursor if not null
	 * 
	 * @param c
	 */
	protected void safeClose(Cursor c) {
		if (c != null) {
			c.close();
		}
	}

	/**
	 * @param soupElt
	 * @return _soupEntryId field value
	 * @throws JSONException
	 */
	protected long idOf(JSONObject soupElt) throws JSONException {
		return soupElt.getLong(SmartStore.SOUP_ENTRY_ID);
	}

	/**
	 * Compare two JSON
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @throws JSONException
	 */
	protected void assertSameJSON(String message, Object expected, Object actual) throws JSONException {
		// At least one null
		if (expected == null || actual == null) {
			// Both null
			if (expected == null && actual == null) {
				return;
			}
			// One null, not the other
			else {
				assertTrue(message, false);
			}
		}
		// Both arrays
		else if (expected instanceof JSONArray && actual instanceof JSONArray) {
			assertSameJSONArray(message, (JSONArray) expected, (JSONArray) actual);
		}
		// Both maps
		else if (expected instanceof JSONObject && actual instanceof JSONObject) {
			assertSameJSONObject(message, (JSONObject) expected, (JSONObject) actual);
		}
		// Atomic types
		else {
			// Comparing string representations, to avoid things like new
			// Long(n) != new Integer(n)
			assertEquals(message, expected.toString(), actual.toString());
		}
	}

	/**
	 * Compare two JSON arrays
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @throws JSONException
	 */
	protected void assertSameJSONArray(String message, JSONArray expected, JSONArray actual) throws JSONException {
		// First compare length
		assertEquals(message, expected.length(), actual.length());

		// If string value match we are done
		if (expected.toString().equals(actual.toString())) {
			// Done
			return;
		}
		// If string values don't match, it might still be the same object
		// (toString does not sort fields of maps)
		else {
			// Compare values
			for (int i = 0; i < expected.length(); i++) {
				assertSameJSON(message, expected.get(i), actual.get(i));
			}
		}
	}

	/**
	 * Compare two JSON maps
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @throws JSONException
	 */
	protected void assertSameJSONObject(String message, JSONObject expected, JSONObject actual) throws JSONException {
		// First compare length
		assertEquals(message, expected.length(), actual.length());

		// If string value match we are done
		if (expected.toString().equals(actual.toString())) {
			// Done
			return;
		}
		// If string values don't match, it might still be the same object
		// (toString does not sort fields of maps)
		else {
			// Compare keys / values
			JSONArray expectedNames = expected.names();
			JSONArray actualNames = actual.names();
			assertEquals(message, expectedNames.length(), actualNames.length());
			JSONArray expectedValues = expected.toJSONArray(expectedNames);
			JSONArray actualValues = actual.toJSONArray(expectedNames);
			assertSameJSONArray(message, expectedValues, actualValues);
		}
	}

	/**
	 * @param soupName
	 * @return table name for soup
	 */
	protected String getSoupTableName(String soupName) {
		return DBHelper.getInstance(app.db).getSoupTableName(app.db, soupName);
	}
}
