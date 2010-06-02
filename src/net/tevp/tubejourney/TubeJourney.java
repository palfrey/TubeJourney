package net.tevp.tubejourney;

import java.util.GregorianCalendar;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.util.Log;

import net.tevp.JourneyPlannerParser.*;
import net.tevp.postcode.*;

public class TubeJourney extends Activity implements PostcodeListener {
	public static final String TAG = "TubeJourney";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttons);

		final TubeJourney buttonThis = this;
        final Button button = (Button) findViewById(R.id.btnHome);
        button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
				setContentView(R.layout.log);
				appendText("Acquiring postcode...\n");
				new PostcodeBackend().getPostcode(buttonThis, buttonThis);
             }
         });
	}

	protected void appendText(final String text)
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText(tv.getText().toString()+text);
	}
	public void postcodeChange(final String postcode)
	{
		Log.d(TAG, "Postcode change to "+postcode);
		appendText("Got postcode " + postcode + "\n");
		JourneyPlannerParser jpp = new JourneyPlannerParser(false);
		JourneyParameters jp = new JourneyParameters();
		//jp.when = new GregorianCalendar(2010, 5, 10, 0, 23).getTime();
		jp.speed = Speed.fast;
		Log.d(TAG, "Doing TFL lookup");
		JourneyQuery jq = jpp.doAsyncJourney(LocationType.Postcode.create(postcode),LocationType.Postcode.create("E3 4AE"), jp);
		new TubeJourneyTask(this).execute(jq);
	}
}
