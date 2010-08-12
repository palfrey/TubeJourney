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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

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
		registerForContextMenu(listLocations);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listLocations) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("Actions");
			menu.add(Menu.NONE, 0, 0, "Open");
			menu.add(Menu.NONE, 1, 1, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		switch(menuItemIndex)
		{
			case 0: // open
				Intent intent = new Intent(this, NamedLocationEditor.class);
				intent.putExtra("location", keys[(int)info.id]);
				startActivity(intent);
				break;
			case 1:
				SharedPreferences.Editor edit = sp.edit();
				edit.remove(keys[(int)info.id]);
				edit.commit();
				break;
			default:
				assert false;
		}
		return true;
	}
}
