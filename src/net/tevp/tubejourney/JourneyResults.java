package net.tevp.tubejourney;

import android.app.Activity;
import android.util.Log;
import java.util.Vector;
import android.os.Bundle;
import android.widget.TextView;

import net.tevp.journeyplannerparser.*;

public class JourneyResults extends Activity implements JourneyTaskHandler {
	public static final String TAG = "JourneyResults";

	@Override
	public void onCreate(Bundle inState)
	{
		super.onCreate(inState);
		setContentView(R.layout.results);
		Log.d(TAG, "Doing TFL lookup");

		Bundle extras =	getIntent().getExtras();
		if (extras != null)
		{
			JourneyQuery jq = extras.getParcelable("query");
			addProgressText(jq.params.when.toString()+"\n");
			new TubeJourneyTask(this).execute(jq);
		}
		else
			addProgressText("Can't find query");
		if (inState!=null && inState.containsKey("tflOutput"))
		{
			TextView tv = (TextView) findViewById(R.id.textLog);
			tv.setText(inState.getString("tflOutput"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		TextView tv = (TextView) findViewById(R.id.textLog);
		outState.putString("tflOutput", tv.getText().toString());
	}

	protected void clearText()
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText("");
	}

	public void journeyComplete(Vector<Journey> js)
	{
		if (js == null)
			return;
		for (int i=0;i<js.size();i++)
		{
			String text;
			text = Integer.toString(i) + "\n";
			text += js.get(i)+ "\n";
			text += "\n";
			addProgressText(text);
		}
	}

	public void addProgressText(final String text)
	{
		TextView tv = (TextView) findViewById(R.id.textLog);
		tv.setText(tv.getText().toString()+text);
	}
}
