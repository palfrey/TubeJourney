package net.tevp.tubejourney;

import java.util.LinkedHashMap;

import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TableRow;
import android.content.Context;
import android.util.TypedValue;
import android.util.AttributeSet;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.content.SharedPreferences;
import android.util.Log;
import android.database.DataSetObserver;
import android.widget.ArrayAdapter;
import android.view.View;

import net.tevp.journeyplannerparser.*;
import net.tevp.postcode.*;

public class LocationChooser extends TableRow implements PostcodeListener
{
	public static final String TAG = "LocationChooser";
	private EditText edit;
	private Spinner spin;
	private static String postcode;

	private static LinkedHashMap<String,Pair<LocationType, String>> coreTypes = null;
	private static LinkedHashMap<String,Pair<LocationType, String>> types = null;
	private static ProxyAdapter<String> adapter = null;
	private static ArrayAdapter<String> coreAdapter = null;
	private static SharedPreferences sp;

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

	private static void updateAdapter()
	{
		Log.d(TAG, "Updating types");
		types.clear();
		for(String s: coreTypes.keySet())
			types.put(s, coreTypes.get(s));
		types.put("<Here>", new Pair<LocationType, String>(null, postcode));
		for(String name: sp.getAll().keySet())
		{
			String value = sp.getString(name, null);
			if (value != null)
			{
				LocationType lt = Enum.valueOf(LocationType.class, value.substring(0,value.indexOf(",")));
				String val = value.substring(value.indexOf(",")+1);
				types.put(name, new Pair<LocationType, String>(lt, val));
				Log.d(TAG, "Added "+name+" "+val);
			}
		}

		adapter.setData(types.keySet());
		TypesSetter.setTypes(types);
		Log.d(TAG, "Types set");
	}

	private void setup(final Context ctx)
	{
		if (coreTypes == null)
		{
			types = new LinkedHashMap<String,Pair<LocationType, String>>();
			coreTypes = new LinkedHashMap<String,Pair<LocationType, String>>();

			coreTypes.put("Postcode", new Pair<LocationType, String>(LocationType.Postcode, null));
			coreTypes.put("Stop", new Pair<LocationType, String>(LocationType.Stop, null));
			coreTypes.put("Address", new Pair<LocationType, String>(LocationType.Address, null));
			coreTypes.put("Place of Interest", new Pair<LocationType, String>(LocationType.PlaceOfInterest, null));
			
			adapter = new ProxyAdapter<String>(ctx, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			coreAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, coreTypes.keySet().toArray(new String[]{}));
			coreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			sp = ctx.getSharedPreferences("locations", Context.MODE_PRIVATE);
			sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener () {
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
				{
					LocationChooser.updateAdapter();
				}
			});
			updateAdapter();
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

		final TypesSetter ts = new TypesSetter(edit);
		spin.setAdapter(adapter);
		adapter.registerDataSetObserver(new DataSetObserver() {
			public void onChanged()
			{
				ts.updateFromSelected(spin, spin.getSelectedItemPosition());
			}
		});

		final LocationChooser self = this;
		setSpinnerSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				if (location() == null) // postcode
				{
					postcode = null;
					edit.setEnabled(false);
					setPostcodeLookupEntry("Updating...");
					new PostcodeBackend().getPostcode(ctx,self);
				}
				else
					ts.onItemSelected(parentView, selectedItemView, position, id);
			}
			public void onNothingSelected(AdapterView<?> parentView) {}
		});

	}

	public String text()
	{
		return edit.getText().toString();
	}

	public boolean locationReady()
	{
		if (location() != null) // not the postcode lookup
			return true;
		else
			return postcode != null;
	}

	private void setPostcodeLookupEntry(String text)
	{
		for(String s: types.keySet())
		{
			Pair<LocationType, String> p = types.get(s);
			if (p.first() == null) // the lookup
			{
				p.setSecond(text);
				adapter.setData(types.keySet());
				TypesSetter.setTypes(types);
				if (location() == null)
					setText(text);
				break;
			}
		}
	}

	@Override
	public void postcodeChange(final String newPostcode)
	{
		postcode = newPostcode;
		Log.d(TAG, "Postcode change to "+postcode);
		post(new Runnable()
		{
			public void run()
			{
				setPostcodeLookupEntry(postcode);
			}
		});
	}

	@Override
	public void updatedLocation(android.location.Location l) {} // ignore location data, we want postcode

	public LocationType location()
	{
		return types.get((String)spin.getSelectedItem()).first();
	}

	public JourneyLocation createLocation()
	{
		if (!locationReady())
			return null;

		LocationType lt = location();
		if (lt == null)
			return LocationType.Postcode.create(postcode);
		else
			return lt.create(text());
	}

	public void setLocation(LocationType lt)
	{
		int i = 0;
		for (String s: types.keySet())
		{
			if (types.get(s).first() == lt)
			{
				spin.setSelection(i);
				break;
			}
			else
				i++;
		}
	}

	public void setText(String text)
	{
		edit.setText(text);
	}

	public void addTextChangedListener(TextWatcher tw)
	{
		edit.addTextChangedListener(tw);
	}

	public void setSpinnerSelectedListener(AdapterView.OnItemSelectedListener ols)
	{
		spin.setOnItemSelectedListener(ols);
	}

	public void setCoreOnly(boolean core)
	{	
		if (core)
			spin.setAdapter(coreAdapter);
		else
			spin.setAdapter(adapter);
	}
}
