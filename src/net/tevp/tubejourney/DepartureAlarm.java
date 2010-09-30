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
import android.widget.TimePicker;
import android.os.Handler;

import java.util.Vector;
import java.util.Calendar;
import java.util.Date;

import net.tevp.journeyplannerparser.*;
import net.tevp.postcode.*;

public class DepartureAlarm extends Activity implements PostcodeListener, JourneyTaskHandler {
	public static final String TAG = "DepartureAlarm";

	private LocationChooser chooserDest;
	private JourneyLocation locationDest;
	private Date timeDest;
	private TimePicker pickerDest;
	private Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.departure_alarm);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		chooserDest = (LocationChooser) findViewById(R.id.locationDest);
		pickerDest = (TimePicker) findViewById(R.id.timeDest);

		final DepartureAlarm self = this;
		Button button = (Button) findViewById(R.id.btnSetDestination);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				locationDest = chooserDest.createLocation();
				Calendar now = Calendar.getInstance();
				Calendar calDest = Calendar.getInstance();
				if (now.get(Calendar.HOUR_OF_DAY)> pickerDest.getCurrentHour()+1 || (now.get(Calendar.HOUR_OF_DAY) == pickerDest.getCurrentHour()+1 && now.get(Calendar.MINUTE) > pickerDest.getCurrentMinute()))
					calDest.set(Calendar.DATE,calDest.get(Calendar.DATE) + 1); // time must be tomorrow
				calDest.set(Calendar.MINUTE,pickerDest.getCurrentMinute());
				calDest.set(Calendar.HOUR_OF_DAY,pickerDest.getCurrentHour());

				timeDest = calDest.getTime();

				new PostcodeBackend().getPostcode(self, self);
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

	protected void clearText()
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText("");
	}

	public void postcodeChange(final String postcode)
	{
		Log.d(TAG, "Postcode change to "+postcode);
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				clearText();
				addProgressText("Got postcode " + postcode + "\n");
			}
		});
		JourneyPlannerParser jpp = new JourneyPlannerParser(false);
		JourneyParameters jp = new JourneyParameters();
		jp.speed = Speed.fast;
		jp.when = timeDest;
		jp.timeType = TimeType.Arrive;
		Log.d(TAG, "Doing TFL lookup");
		final JourneyQuery jq = jpp.doAsyncJourney(LocationType.Postcode.create(postcode),locationDest, jp);
		final DepartureAlarm self = this;
		mHandler.post(new Runnable(){
			public void run()
			{
				new TubeJourneyTask(self).execute(jq);
			}
		});
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

	public void journeyComplete(Vector<Journey> js)
	{
		Journey latest = null;
		if (js == null)
			return;

		for (Journey j: js)
		{
			long newEnd = j.last().time_end.getTime();
			if (newEnd > timeDest.getTime())
				continue;
			else if (latest == null || newEnd > latest.last().time_end.getTime())
				latest = j;
		}
		if (latest == null)
			return;
		addProgressText(latest.toString());
	}
}
