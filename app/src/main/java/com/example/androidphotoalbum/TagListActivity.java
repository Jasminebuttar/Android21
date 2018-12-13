package com.example.androidphotoalbum;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Jasmine Buttar
 * @author Zalak Shingala
 */
public class TagListActivity extends Activity
{

	String album;
	String photo;
	private Controller control;
	Context con;
	private ListView lv;
	private TagListAdapter tla;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkable_picker);
		con = this;
		control = Controller.getInstance(con);
		this.album = getIntent().getExtras().getString(AppConstants.ALBUM_TITLE);
		this.photo = getIntent().getExtras().getString(AppConstants.PHOTO_FILENAME);

		lv = (ListView) findViewById(R.id.tagList);

		tla = new TagListAdapter(this, album, photo);

		lv.setAdapter(tla);

		Button cancel = (Button) findViewById(R.id.cancel);
		Button save = (Button) findViewById(R.id.save);

		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				saveTags();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.tag_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		switch (id)
		{
			case android.R.id.home :
				finish();
				break;
			case R.id.ok :
				saveTags();
				break;
			case R.id.cancel :
				finish();
				break;

		}

		return true;
	}

	private void saveTags()
	{
		control.getAlbums().get(this.album).getPhotos().get(this.photo).setTags(tla.selectedTags);
		Toast.makeText(con, "Saved tags " + StringDisplayUtil.formatTagList(tla.selectedTags), Toast.LENGTH_SHORT)
				.show();
		control.write();
		finish();
	}

}
