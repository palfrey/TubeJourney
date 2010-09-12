package net.tevp.tubejourney;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import java.util.Vector;

import net.tevp.journeyplannerparser.*;
import net.tevp.postcode.*;

public class TubeJourney extends Activity implements PostcodeListener, JourneyTaskHandler {
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
				clearText();
				JourneyPlannerParser jpp = new JourneyPlannerParser(false);
				JourneyParameters jp = new JourneyParameters();
				jp.speed = Speed.fast;
				Log.d(TAG, "Doing TFL lookup");
				addProgressText(jp.when.toString()+"\n");
				JourneyQuery jq = jpp.doAsyncJourney(locationStart.createLocation(),locationDest.createLocation(), jp);
				new TubeJourneyTask(self).execute(jq);
			 }
		 });

		if (inState!=null && inState.containsKey("tflOutput"))
		{
			TextView tv = (TextView) findViewById(R.id.textLog);
			tv.setText(inState.getString("tflOutput"));
		}
	}

	public void addProgressText(final String text)
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText(tv.getText().toString()+text);
	}

	public void journeyComplete(Vector<Journey> js)
	{
	}

	protected void clearText()
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText("");
	}

	public void postcodeChange(final String postcode)
	{
		Log.d(TAG, "Postcode change to "+postcode);
		addProgressText("Got postcode " + postcode + "\n");
		JourneyPlannerParser jpp = new JourneyPlannerParser(false);
		JourneyParameters jp = new JourneyParameters();
		jp.speed = Speed.fast;
		Log.d(TAG, "Doing TFL lookup");
		JourneyQuery jq = jpp.doAsyncJourney(LocationType.Postcode.create(postcode),LocationType.Postcode.create("E3 4AE"), jp);
		new TubeJourneyTask(this).execute(jq);
	}

	@Override
	public void updatedLocation(android.location.Location l) {} // ignore location data, we want postcode

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		TextView tv = (TextView) findViewById(R.id.textLog);
		outState.putString("tflOutput", tv.getText().toString());
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
		}
		return super.onOptionsItemSelected(item);
	}
}
