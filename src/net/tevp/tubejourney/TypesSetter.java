package net.tevp.tubejourney;

import java.util.LinkedHashMap;

import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;
import android.util.Log;

import net.tevp.journeyplannerparser.LocationType;

class TypesSetter implements AdapterView.OnItemSelectedListener
{
	public static final String TAG = "TypesSetter";
	private static LinkedHashMap<String,Pair<LocationType, String>> types;
	private EditText et;

	TypesSetter(EditText et)
	{
		this.et = et;
	}

	public static void setTypes(LinkedHashMap<String,Pair<LocationType, String>> types)
	{
		TypesSetter.types = types;
		Log.d(TAG, "Types set");
	}

	public void onItemSelected(AdapterView av, View v, int position, long id)
	{
		updateFromSelected(av, position);
	}

	public void updateFromSelected(AdapterView av, int position)
	{
		Pair<LocationType,String> data = types.get((String)av.getItemAtPosition(position));
		if (data.second() == null)
		{
			if (et.isEnabled() == false)
				et.setText("");
			et.setEnabled(true);
		}
		else
		{
			Log.d(TAG, String.format("Type %s selected, with value %s", data.first(), data.second()));
			et.setText(data.second());
			et.setEnabled(false);
		}
	}

	public void onNothingSelected(AdapterView av)
	{
		et.setEnabled(true);
	}
}
