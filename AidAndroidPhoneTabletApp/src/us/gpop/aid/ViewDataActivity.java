package us.gpop.aid;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.QuerySpec.Order;

public class ViewDataActivity extends Activity {

	public static final float GREEN_STATUS_STEP_THRESHOLD = 10000f;

	private static final String LOG_TAG = ViewDataActivity.class.getSimpleName();

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

	private ArcView stepsDot;
	private ArcView heartDot;
	private ArcView sleepDot;

	private Button checkinButton;
	private View settingButton;
	private TextView commentField;

	private ViewGroup commentsContainer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = AidApp.getInstance();

		setContentView(R.layout.activity_view_data);
		commentsContainer = (ViewGroup) findViewById(R.id.commentsContainer);

		stepsDot = (ArcView) findViewById(R.id.stepsDot);
		stepsDot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(ViewDataActivity.this, ViewChartActivity.class);
				startActivity(intent);
			}
		});

		heartDot = (ArcView) findViewById(R.id.heartDot);
		heartDot.setAngle(320);
		heartDot.setText("95bpm");
		heartDot.listener = new ArcView.ArcTouchListener() {
			@Override
			public boolean onArcTouch(float newAngle) {
				Log.i("ViewDataActivity", "onArcTouch: " + newAngle);
				
				heartDot.setAngle(newAngle);
				
				if ( newAngle < 120 ) {
					showWarningNotification();
				}
				
				//final float newRateInverse = 360f * 200 / 360f;
				//float nweRate = 200 - newRateInverse;
				float newRate = newAngle / 2;
				
				newRate = Math.max(newRate, 20f);
				//newRate = Math.min(newRate, 300f);

				final int newRateInt = (int) newRate;
				
				heartDot.setText(newRateInt + "bpm");
				
				return false;
			}
			
		};

		sleepDot = (ArcView) findViewById(R.id.sleepDot);
		sleepDot.setAngle(60);
		sleepDot.setText("4hr");

		settingButton = findViewById(R.id.settingButton);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(ViewDataActivity.this, EnterDataActivity.class);
				startActivity(intent);
			}
		});

		commentField = (TextView) findViewById(R.id.commentField);

		checkinButton = (Button) findViewById(R.id.checkinButton);
		checkinButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String newCommentBody = commentField.getText().toString();
				addComment(newCommentBody);
				commentField.setText("");
			}
		});

		findViewById(R.id.mynetworkButton).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(ViewDataActivity.this, AddPeopleActivity.class);
				startActivity(intent);
			}
		});
		
		applyCustomFont(); // Applies the custom font family to the layout.
	}
	
	private static final int NOTIFICATION_ID = 100;
	
	public void showWarningNotification() {
		Log.i("ViewDataActivity", "showWarningNotification");
		

	      // Invoking the default notification service
	      NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);	
	 
	      mBuilder.setContentTitle("Power Note Health Warning");
	      mBuilder.setContentText("Your heart rate is dangerously low. If symptoms persist for several days, please see a doctor.");
	      mBuilder.setTicker("Heart health warning!");
	      mBuilder.setSmallIcon(R.drawable.ic_launcher);

	      // Increase notification number every time a new notification arrives 
	      //mBuilder.setNumber(++numMessagesOne);
	      
	      // Creates an explicit intent for an Activity in your app 
	      Intent resultIntent = new Intent(this, LoginActivity.class);
	      //resultIntent.putExtra("notificationId", notificationIdOne);

	      //This ensures that navigating backward from the Activity leads out of the app to Home page
	      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

	      // Adds the back stack for the Intent
	      stackBuilder.addParentStack(LoginActivity.class);

	      // Adds the Intent that starts the Activity to the top of the stack
	      stackBuilder.addNextIntent(resultIntent);
	      PendingIntent resultPendingIntent =
	         stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_ONE_SHOT //can only be used once
	         );
	      // start the activity when the user clicks the notification text
	      mBuilder.setContentIntent(resultPendingIntent);

	      NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	      // pass the Notification object to the system 
	      myNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

	// applyCustomFont(): Applies the custom font family to the TextView and Button objects.
	private void applyCustomFont() {
	
		// TextView & Button references.
		TextView personText = (TextView) findViewById(R.id.person_text);
		TextView stepsLabelText = (TextView) findViewById(R.id.stepsDotLabel);
		TextView sleepLabelText = (TextView) findViewById(R.id.sleepDotLabel);
		TextView heartLabelText = (TextView) findViewById(R.id.heartDotLabel); 
		
		// Sets the custom font to the layout objects.
		personText.setTypeface(CustomFont.getInstance(this).getTypeFace());
		stepsLabelText.setTypeface(CustomFont.getInstance(this).getTypeFace());
		sleepLabelText.setTypeface(CustomFont.getInstance(this).getTypeFace());
		heartLabelText.setTypeface(CustomFont.getInstance(this).getTypeFace());
		commentField.setTypeface(CustomFont.getInstance(this).getTypeFace());
		checkinButton.setTypeface(CustomFont.getInstance(this).getTypeFace());
	}

	public void addComment(final String newMessageBody) {
		try {

			final long currentTime = new Date().getTime();

			{
				JSONObject newMessage = new JSONObject();
				newMessage.put("stepsDot", stepsDot.getAngle());
				newMessage.put("sleepDot", sleepDot.getAngle());
				newMessage.put("heartDot", heartDot.getAngle());
				newMessage.put("message", newMessageBody);
				newMessage.put("timestamp", currentTime);
				JSONObject createdRecord = app.store.create(AidApp.COMMENTS_TABLE_NAME, newMessage);
			}

		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		refreshData();
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
		
		displayCommentsFromDb();
		
		try {

			// Display circle graph for steps
			int totalSteps = 0;

			Log.i(LOG_TAG, "refreshData querying chart: " + app.getCurrentChartName());
			
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

			// GraphViewSeries stepsSeries = new GraphViewSeries(
			// "Steps (count)",
			// new GraphViewStyle(Color.RED, 5), steps);

			// GraphView graphView = new LineGraphView(this, "");

			// graphContent.removeAllViews();
			// graphContent.addView(graphView);
			// graphContent.setContentDescription("Recent Health Stats: "
			// + stepsStats.getDescription("Steps ", " (count) "));

			// ((LineGraphView) graphView).setDrawBackground(true);

			// custom static labels
			// graphView.setHorizontalLabels(new String[] {"2 days ago",
			// "yesterday", "today", "tomorrow"});
			// graphView.setVerticalLabels(new String[] {"high", "middle",
			// "low"});
			// graphView.addSeries(stepsSeries);
			// graphView.setShowLegend(false);
			// graphView.setScalable(true);

			final float stepsTowardGoalPercent = totalSteps / GREEN_STATUS_STEP_THRESHOLD;
			Log.i(LOG_TAG, "stepsTowardGoalPercent = " + stepsTowardGoalPercent);

			final float angle = stepsTowardGoalPercent * 360;
			Log.i(LOG_TAG, "angle = " + angle);

			stepsDot.setAngle(angle);
			stepsDot.setText("" + totalSteps);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	public void displayCommentsFromDb() {
		if (!app.store.hasSoup(AidApp.COMMENTS_TABLE_NAME)) {
			Log.e(LOG_TAG, "displayCommentsFromDb - no comments table found");
			return;
		}
		
		try {

			// Display comments
			commentsContainer.removeAllViews();

			JSONArray commentsData = app.store.query(
					QuerySpec.buildAllQuerySpec(
							AidApp.COMMENTS_TABLE_NAME, "timestamp", Order.descending, 1000), 0);

			final LayoutInflater inflator = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			for (int i = 0; i < commentsData.length(); i++) {
				final JSONObject row = commentsData.getJSONObject(i);

				final ViewGroup commentLayout = (ViewGroup) inflator.inflate(R.layout.activity_view_data_comment_row,
						null);
				commentsContainer.addView(commentLayout);

				final String message = row.getString("message");
				final TextView messageTextView = (TextView) commentLayout.findViewById(R.id.commentBody);
				messageTextView.setTypeface(CustomFont.getInstance(this).getTypeFace()); // CUSTOM FONT.
				messageTextView.setText(message);

				final long timestamp = row.getLong("timestamp");
				final Date date = new Date(timestamp);
				SimpleDateFormat simpleDateFormat =
						new SimpleDateFormat("MM.dd.yyyy");
				String dateAsString = simpleDateFormat.format(date);
				final TextView commentDate = (TextView) commentLayout.findViewById(R.id.commentDate);
				commentDate.setTypeface(CustomFont.getInstance(this).getTypeFace()); // CUSTOM FONT.
				commentDate.setText(dateAsString);

				{
					final Double stepsDotAngle = row.getDouble("stepsDot");
					final TextView commentDot1 = (TextView) commentLayout.findViewById(R.id.commentDot1);
					if (stepsDotAngle < 120) {
						commentDot1.setTextColor(Color.parseColor("#C12239"));
					} else if (stepsDotAngle < 240) {
						commentDot1.setTextColor(Color.parseColor("#EDE30F"));
					} else {
						commentDot1.setTextColor(Color.parseColor("#61B719"));
					}
				}
				{
					final Double sleepDotAngel = row.getDouble("sleepDot");
					final TextView commentDot2 = (TextView) commentLayout.findViewById(R.id.commentDot2);
					if (sleepDotAngel < 120) {
						commentDot2.setTextColor(Color.parseColor("#C12239"));
					} else if (sleepDotAngel < 240) {
						commentDot2.setTextColor(Color.parseColor("#EDE30F"));
					} else {
						commentDot2.setTextColor(Color.parseColor("#61B719"));
					}
				}
				{
					final Double heartDotAngel = row.getDouble("heartDot");
					final TextView commentDot3 = (TextView) commentLayout.findViewById(R.id.commentDot3);
					if (heartDotAngel < 120) {
						commentDot3.setTextColor(Color.parseColor("#C12239"));
					} else if (heartDotAngel < 240) {
						commentDot3.setTextColor(Color.parseColor("#EDE30F"));
					} else {
						commentDot3.setTextColor(Color.parseColor("#61B719"));
					}
				}
			}

			// Display circle graph for steps
			int totalSteps = 0;

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

			// GraphViewSeries stepsSeries = new GraphViewSeries(
			// "Steps (count)",
			// new GraphViewStyle(Color.RED, 5), steps);

			// GraphView graphView = new LineGraphView(this, "");

			// graphContent.removeAllViews();
			// graphContent.addView(graphView);
			// graphContent.setContentDescription("Recent Health Stats: "
			// + stepsStats.getDescription("Steps ", " (count) "));

			// ((LineGraphView) graphView).setDrawBackground(true);

			// custom static labels
			// graphView.setHorizontalLabels(new String[] {"2 days ago",
			// "yesterday", "today", "tomorrow"});
			// graphView.setVerticalLabels(new String[] {"high", "middle",
			// "low"});
			// graphView.addSeries(stepsSeries);
			// graphView.setShowLegend(false);
			// graphView.setScalable(true);

			final float stepsTowardGoalPercent = totalSteps / GREEN_STATUS_STEP_THRESHOLD;
			Log.i(LOG_TAG, "stepsTowardGoalPercent = " + stepsTowardGoalPercent);

			final float angle = stepsTowardGoalPercent * 360;
			Log.i(LOG_TAG, "angle = " + angle);

			stepsDot.setAngle(angle);
			stepsDot.setText("" + totalSteps);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
