package net.tevp.tubejourney;

import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;
import java.util.HashSet;

public class ProxyAdapter<T> extends DataSetObserver implements Adapter, SpinnerAdapter
{
	ArrayAdapter<T> adapter = null;
	int resource = -1;
	int dropDownResource = -1;
	Context ctx;
	HashSet<DataSetObserver> observers;

	public ProxyAdapter(Context ctx, int resource)
	{
		this.resource = resource;
		this.ctx = ctx;
		observers = new HashSet<DataSetObserver>();
	}

	public ProxyAdapter(Context ctx, int resource, Set<T> data)
	{
		this(ctx,resource);
		setData(data);
	}

	public void setDropDownViewResource(int resource)
	{
		if (adapter != null)
			adapter.setDropDownViewResource(resource);
		dropDownResource = resource;
	}

	@Override
	public boolean isEmpty()
	{
		if (adapter != null)
			return adapter.isEmpty();
		else
			return true;
	}

	@SuppressWarnings("unchecked")
	public void setData(Set<T> newData)
	{
		adapter = new ArrayAdapter<T>(ctx, resource, (T[])newData.toArray());
		adapter.registerDataSetObserver(this);
		if (dropDownResource != -1);
			adapter.setDropDownViewResource(dropDownResource);
		onChanged();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		observers.remove(observer);
	}

	@Override
	public int getCount()
	{
		return adapter.getCount();
	}

	@Override
	public Object getItem(int position)
	{
		return adapter.getItem(position);
	}

	@Override
	public long getItemId(int position)
	{
		return adapter.getItemId(position);
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return adapter.getView(position, convertView, parent);
	}

	@Override
	public int getItemViewType(int position)
	{
		return adapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount()
	{
		return adapter.getViewTypeCount();
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return adapter.getDropDownView(position, convertView, parent);
	}

	@Override
	public void onChanged()
	{
		for (DataSetObserver observer: observers)
			observer.onChanged();
	}

	@Override
	public void onInvalidated()
	{
		for (DataSetObserver observer: observers)
			observer.onInvalidated();
	}
}
