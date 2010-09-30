package net.tevp.tubejourney;

import android.os.AsyncTask;
import android.util.Log;
import java.util.Vector;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.tevp.journeyplannerparser.*;

public class TubeJourneyTask extends AsyncTask<JourneyQuery, String, Vector<Journey>>
{
	public static final String TAG = "TubeJourneyTask";

	private JourneyTaskHandler jth;
	
	public TubeJourneyTask(JourneyTaskHandler parent)
	{
		jth = parent;
	}

	protected Vector<Journey> doInBackground(JourneyQuery... jq)
	{
		try
		{
			Log.d(TAG, "Doing TFL lookup");
			Vector<Journey> js = jq[0].run();
			Log.d(TAG, "TFL lookup complete, got "+Integer.toString(js.size())+" results");
			assert js.size()!=0;
			return js;
		}
		catch (AmbiguousLocationException e)
		{
			publishProgress(e.original.toString());
			publishProgress(e.options.toString());
			publishProgress(stack2string(e));
		}
		catch (ParseException e)
		{
			publishProgress(stack2string(e));
		}
		return null;
	}

	private static String stack2string(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return "------\n" + sw.toString() + "------\n";
	}

	protected void onProgressUpdate(String... values)
	{
		for (String s:values)
			jth.addProgressText(s);
	}

	protected void onPostExecute (Vector<Journey> result)
	{
		jth.journeyComplete(result);
	}
}
