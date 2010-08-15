package net.tevp.tubejourney;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AdapterView;
import android.view.View;
import android.util.Log;

import net.tevp.journeyplannerparser.LocationType;

public class NamedLocationEditor extends Activity implements TextWatcher
{
	public static final String TAG = "NamedLocationEditor";
	SharedPreferences sp;
	EditText editName;
	LocationChooser lc;
	String oldName;

	@Override
	public void onCreate(Bundle inState)
	{
		super.onCreate(inState);
		setContentView(R.layout.named_location_edit);
		sp = getSharedPreferences("locations", MODE_PRIVATE);
		editName = (EditText)findViewById(R.id.editLocationName);
		lc = (LocationChooser)findViewById(R.id.lcEdit);

		Bundle extras =	getIntent().getExtras();
		if (extras != null)
		{
			String name = extras.getString("location");
			oldName = name;
			editName.setText(name);
			String value = sp.getString(name, null);
			if (value != null)
			{
				lc.setLocation(Enum.valueOf(LocationType.class, value.substring(0,value.indexOf(","))));
				lc.setText(value.substring(value.indexOf(",")+1));
			}
		}
		else
			oldName = null;

		editName.addTextChangedListener(this);
		lc.addTextChangedListener(this);
		lc.addSpinnerSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				storeChange();
			}
			public void onNothingSelected(AdapterView<?> parentView) {}
		});
		lc.setCoreOnly(true);
	}

	void storeChange()
	{
		SharedPreferences.Editor edit = sp.edit();
		if (oldName != null)
		{
			edit.remove(oldName);
			Log.d(TAG, "Removing "+oldName);
		}
		String newName = editName.getText().toString();
		String toStore = lc.location().name()+","+lc.text();
		Log.d(TAG, String.format("Storing '%s':'%s'", newName,toStore));
		edit.putString(newName, toStore);
		edit.commit();
		oldName = newName;
	}

	public void afterTextChanged(Editable s)
	{
		assert s == editName;
		storeChange();
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
