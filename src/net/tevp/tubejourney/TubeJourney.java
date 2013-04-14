package net.tevp.tubejourney;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.Context;
import android.widget.Toast;

import net.tevp.journeyplannerparser.*;

public class TubeJourney extends Activity {
	public static final String TAG = "TubeJourney";

	private LocationChooser locationStart, locationDest;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		locationStart = (LocationChooser) findViewById(R.id.locationStart);
		locationDest = (LocationChooser) findViewById(R.id.locationDest);

		final TubeJourney self = this;
		Button button = (Button) findViewById(R.id.btnDoJourney);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!locationStart.locationReady())
				{
					toastText("Start location isn't available yet");
					return;
				}
				if (!locationDest.locationReady())
				{
					toastText("Destination location isn't available yet");
					return;
				}
				JourneyParameters jp = new JourneyParameters();
				jp.speed = Speed.fast;
				Log.d(TAG, "Doing TFL lookup");
				JourneyQuery jq = JourneyPlannerParser.doAsyncJourney(locationStart.createLocation(),locationDest.createLocation(), jp);
				Intent intent = new Intent(self, JourneyResults.class);
				intent.putExtra("query", jq);
				startActivity(intent);
			 }
		 });

	}

	@Override
	public void onResume()
	{
		super.onResume();
		locationStart.setUpdating(true);
		locationDest.setUpdating(true);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		locationStart.setUpdating(false);
		locationDest.setUpdating(false);
	}

	private void toastText(String text)
	{
		Context context = getApplicationContext();
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
			case R.id.locations_menu:
				startActivity(new Intent(this, NamedLocations.class));
				return true;
			case R.id.departure_alarm:
				startActivity(new Intent(this, DepartureAlarm.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
