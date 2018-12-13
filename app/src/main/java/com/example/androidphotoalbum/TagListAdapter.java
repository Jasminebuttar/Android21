package com.example.androidphotoalbum;

import java.util.List;

import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author Jasmine Buttar
 * @author Zalak Shingala
 */
public class TagListAdapter extends BaseAdapter implements View.OnCreateContextMenuListener {

	LayoutInflater inflater;
	private Context con;
	private List<PhotoTag> tags;
	List<PhotoTag> selectedTags;
	Controller control;
	String holder;
	String filename;
	String album;

	public TagListAdapter(Context c, String a, String p)
	{
		con = c;
		control = Controller.getInstance(con);
		this.album = a;
		this.filename = p;
		tags = control.getTagsForPhoto(album, filename);
		selectedTags = tags;

		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount()
	{
		return tags.size();
	}

	@Override
	public Object getItem(int position)
	{
		return tags.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	static class TagHolder
	{
		TextView title;
		CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.checkable_picker_row, parent, false);
		final CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkbox);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(tags.get(position).toString());

		final PhotoTag s = tags.get(position);

		cb.setChecked(true);
		cb.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (cb.isChecked())
				{
					selectedTags.add(s);
				} else
				{
					selectedTags.remove(s);
				}
			}
		});

		rowView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (cb.isChecked())
				{
					cb.setChecked(false);
					selectedTags.remove(s);
				} else {
					cb.setChecked(true);
					selectedTags.add(s);
				}
			}
		});

		return rowView;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{

	}

}