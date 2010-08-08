package net.tevp.tubejourney;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.AdapterView;

import java.util.Set;

public class NamedLocations extends Activity
{
	ListView listLocations;
	ArrayAdapter<String> locationsAdapter;
	SharedPreferences sp;
	String[] keys;

	void setLocations()
	{
		keys = sp.getAll().keySet().toArray(new String[]{});
		locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keys);
		listLocations.setAdapter(locationsAdapter);
	}

	@Override
	public void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.named_locations);

		final NamedLocations self = this;

		listLocations = (ListView)findViewById(R.id.listLocations);
		sp = getSharedPreferences("locations", MODE_PRIVATE);
		setLocations();
		sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener () {
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
			{
				self.setLocations();
			}
		});
		
		((Button)findViewById(R.id.btnNewLocation)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(self, NamedLocationEditor.class));
			}
		});

		listLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent intent = new Intent(self, NamedLocationEditor.class);
				intent.putExtra("location", keys[(int)id]);
				startActivity(intent);
			}
		});
	}
}
