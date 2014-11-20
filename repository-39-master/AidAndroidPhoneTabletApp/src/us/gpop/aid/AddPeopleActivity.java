package us.gpop.aid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import dreamforce.gpop.us.dreamforce.Data.DFDataSync;

/**
 * Created by Michael Yoon Huh on 10/11/2014.
 */
public class AddPeopleActivity extends Activity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LAYOUT VARIABLES

    // LIST VARIABLES
    private ArrayList<String> people;
    private ArrayAdapter<String> peopleAdapter;
    private ListView peopleItems;

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only runs
    // when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpLayout(); // Sets up the layout.
        setUpButtons(); // Sets up the button
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the activity is resumed from an onPause() state.
    @Override
    protected void onResume() {
        super.onResume();
    }

    // onPause(): This function is called whenever the current activity is suspended or another
    // activity is launched.
    @Override
    public void onPause() {
        super.onPause();
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

    /** ADDITIONAL FUNCTIONALITY _______________________________________________________________ **/

    // setUpLayout(): Sets up the layout.
    private void setUpLayout() {
        setContentView(R.layout.activity_addpeople); // Sets the XML layout file.
    }

    // setUpButtons(): Sets up the buttons.
    private void setUpButtons() {

        ImageButton navButton_1 = (ImageButton) findViewById(R.id.navigation_btn_1);
        ImageButton navButton_2 = (ImageButton) findViewById(R.id.navigation_btn_2);
        ImageButton navButton_3 = (ImageButton) findViewById(R.id.navigation_btn_3);

        // BUTTON LISTENERS:
        navButton_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchActivity(1);
            }
        });

        navButton_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchActivity(2);
            }
        });

        navButton_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchActivity(3);
            }
        });
    }

    // launchActivity(): Launches the specified activity.
    private void launchActivity(int page) {

        switch (page) {

            case 1:
                finish();
                break;
            case 2:
                // Do nothing, we're on this screen.
                break;
            case 3:

				final Intent intent = new Intent(AddPeopleActivity.this, EnterDataActivity.class);
				startActivity(intent);
				
                break;
            default:
                // DO NOTHING.
                break;
        }
    }
}
