package us.gpop.aid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.VideoView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;

public class LoginActivity extends SalesforceActivity {

	/** CLASS VARIABLES __________________________________________________________________________ **/
	
	// VIDEO VARIABLES
	private static final int INTRO_TIME_MS = 3 * 1000;
	private Handler uiThreadHanler = new Handler();
	private VideoView videoView;
	
	// LAYOUT VARIABLES
	private View signInButton;
	private View content;
	
	/** ACTIVITY FUNCTIONALITY ___________________________________________________________________ **/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_login);
		
		setUpVideoView(); // Sets up the video background.
		
		signInButton = findViewById(R.id.signInButton);
		signInButton.setVisibility(View.GONE);
		content = findViewById(R.id.content);
	}

	@Override
	public void onResume(RestClient client) {

		// Salesforce login done

		signInButton.setVisibility(View.VISIBLE);
		
		content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


						final Intent intent = new Intent(LoginActivity.this, ViewDataActivity.class);
						startActivity(intent);
						finish();

			}
		});
	}
	
	/** VIEW FUNCTIONALITY ____________________________________________________________________ **/
		
	public void setUpVideoView() {
		
		videoView = (VideoView) findViewById(R.id.activity_login_videoView);

		// Loops video playback.
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoView.start(); //need to make transition seamless.
            }
        });
		
		String uri = "android.resource://" + getPackageName() + "/" + R.raw.loading_video;
		videoView.setVideoURI(Uri.parse(uri));
		videoView.start();
	}
	
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

}