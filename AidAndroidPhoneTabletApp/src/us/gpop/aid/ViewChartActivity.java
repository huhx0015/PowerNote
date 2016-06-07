package us.gpop.aid;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.QuerySpec.Order;

public class ViewChartActivity extends Activity {

	public static final float GREEN_STATUS_STEP_THRESHOLD = 10000f;

	private static final String LOG_TAG = ViewChartActivity.class.getSimpleName();

	private static class Stats {
		private Float min;
		private Float max;
		private Float start;
		private Float end;

		private void update(Float value) {
			if (null == min || min < value) {
				min = value;
			}

			if (null == max || max > value) {
				max = value;
			}

			if (null == start) {
				start = value;
			}

			if (null != value) {
				end = value;
			}
		}

		private String getDescription(String name, String units) {
			return name + " started at " + start + " " + units + " and ended at " + end + " " + units
					+ ". The maximum was " + max + ".";
		}
	}

	private AidApp app;

	private ViewGroup content;

	private ViewGroup graphContent;
	private View settingButton;
	private View myaidButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = AidApp.getInstance();

		setContentView(R.layout.activity_view_chart);
		content = (ViewGroup) findViewById(R.id.content);
		graphContent = (ViewGroup) findViewById(R.id.graph_content);

		settingButton = findViewById(R.id.settingButton);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();

				final Intent intent = new Intent(ViewChartActivity.this, EnterDataActivity.class);
				startActivity(intent);
			}
		});

		myaidButton = findViewById(R.id.myaidButton);
		myaidButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.mynetworkButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();

				final Intent intent = new Intent(ViewChartActivity.this, AddPeopleActivity.class);
				startActivity(intent);
			}
		});


	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(LOG_TAG, "onReceive");
			
			refreshData();
		}
	};

	public void onResume() {
		Log.i(LOG_TAG, "onResume");
		
		super.onResume();
		refreshData();
		
		final IntentFilter filter = new IntentFilter();
		filter.addAction(AidApp.STEPS_RECEIVED_INTENT_ACTION);
		registerReceiver(receiver, filter);
		Log.i(LOG_TAG, "registered for steps received");
	}
	
	public void onPause() {
		Log.i(LOG_TAG, "onPause");
		
		super.onPause();
		unregisterReceiver(receiver);
		Log.i(LOG_TAG, "unregistered");
	}

	public synchronized void refreshData() {
		Log.i(LOG_TAG, "refreshData");
		
		int totalSteps = 0;

		try {
			JSONArray readings = app.store.query(
					QuerySpec.buildAllQuerySpec(
							app.getCurrentChartName(), "timestamp", Order.ascending, 1000), 0);

			// TODO don't enable graph button until we have readings?
			if (readings.length() == 0) {
				Toast.makeText(this, "Not enough readings to graph. Sorry.", Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			GraphViewData[] steps = new GraphViewData[readings.length()];
			Stats stepsStats = new Stats();

			long now = new Date().getTime();

			// Float previousSecondsAgo;
			for (int i = 0; i < readings.length(); i++) {
				final JSONObject row = readings.getJSONObject(i);

				final long timestamp = row.getLong("timestamp");
				final long stepsValue = row.getLong("steps");

				Log.i("ViewDataActivity", "timestamp: " + timestamp);
				Log.i("ViewDataActivity", "stepsValue: " + stepsValue);

				totalSteps += stepsValue;

				float secondsAgo = (now - timestamp) / 1000;
				// if (null == previousSecondsAgo ||
				// previousSecondsAgo.equals(secondsAgo)) {
				// previousSecondsAgo = secondsAgo;
				steps[i] = new GraphViewData(secondsAgo, stepsValue);
				// i++;
				// }

			}

			GraphViewSeries stepsSeries = new GraphViewSeries(
					"Steps (count)",
					new GraphViewStyle(Color.RED, 5), steps);

			GraphView graphView = new LineGraphView(this, "");

			graphContent.removeAllViews();
			graphContent.addView(graphView);
			graphContent.setContentDescription("Recent Health Stats: "
					+ stepsStats.getDescription("Steps ", " (count) "));

			// ((LineGraphView) graphView).setDrawBackground(true);

			// custom static labels
			graphView.setHorizontalLabels(new String[] {"S", "M", "T", "W", "R", "F", "U"});
			// graphView.setVerticalLabels(new String[] {"high", "middle",
			// "low"});
			graphView.addSeries(stepsSeries);
			graphView.setShowLegend(false);
			graphView.setScalable(true);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
