package net.tevp.tubejourney;

import java.util.LinkedHashMap;

import android.view.View;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.util.TypedValue;
import android.util.AttributeSet;

import net.tevp.journeyplannerparser.*;

public class LocationChooser extends TableRow
{
	private EditText edit;
	private Spinner spin;

	private static LinkedHashMap<String,Pair<LocationType, String>> types = null;
	private static ArrayAdapter<String> adapter = null;

	public LocationChooser(Context ctx)
	{
		super(ctx);
		setup(ctx);
	}

	public LocationChooser(Context ctx, AttributeSet as)
	{
		super(ctx,as);
		setup(ctx);
	}

	private void setup(Context ctx)
	{
		if (types == null)
		{
			types = new LinkedHashMap<String,Pair<LocationType, String>>();

			types.put("Postcode", new Pair<LocationType, String>(LocationType.Postcode, null));
			types.put("Stop", new Pair<LocationType, String>(LocationType.Stop, null));
			types.put("Address", new Pair<LocationType, String>(LocationType.Address, null));
			types.put("Place of Interest", new Pair<LocationType, String>(LocationType.PlaceOfInterest, null));
			// FIXME: remove hard code
			types.put("Home", new Pair<LocationType, String>(LocationType.Postcode, "E3 4AE"));

			adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, types.keySet().toArray(new String[1]));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		spin = new Spinner(ctx);
		addView(spin, new TableRow.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		edit = new EditText(ctx);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		lp.width = 0;
		edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		addView(edit, lp);

		spin.setOnItemSelectedListener(new TypesSetter(types, edit));
		spin.setAdapter(adapter);
	}

	public String text()
	{
		return edit.getText().toString();
	}

	public LocationType location()
	{
		return types.get((String)spin.getSelectedItem()).first();
	}
}
