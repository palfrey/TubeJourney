package net.tevp.tubejourney;

import java.util.LinkedHashMap;

import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;

import net.tevp.journeyplannerparser.LocationType;

class TypesSetter implements AdapterView.OnItemSelectedListener
{
	private LinkedHashMap<String,Pair<LocationType, String>> types;
	private EditText et;

	TypesSetter(LinkedHashMap<String,Pair<LocationType, String>> types, EditText et)
	{
		this.types = types;
		this.et = et;
	}

	public void onItemSelected(AdapterView av, View v, int position, long id)
	{
		Pair<LocationType,String> data = types.get((String)av.getItemAtPosition(position));
		if (data.second() == null)
		{
			et.setEnabled(true);
		}
		else
		{
			et.setText(data.second());
			et.setEnabled(false);
		}
	}

	public void onNothingSelected(AdapterView av)
	{
		et.setEnabled(true);
	}
}
