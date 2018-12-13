package com.example.androidphotoalbum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Jasmine Buttar
 * @author Zalak Shingala
 */
public class MainActivity extends Activity
{

	private Controller control;
	private GridView gv;
	private AlbumGridAdapter albumAdapter;
	Context con;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		init();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		control.write();
	}

	public void init()
	{
		setContentView(R.layout.grid_fragment);
		con = this;
		control = Controller.getInstance(con);
		gv = (GridView) findViewById(R.id.grid);

		int placeholderWidth = (int) (1.1 * this.getResources().getDrawable(R.drawable.no_image).getIntrinsicWidth());
		gv.setColumnWidth(placeholderWidth);
		showAlbums();
	}

	private void showAlbums()
	{
		TextView tv = (TextView) findViewById(R.id.label);
		if (!control.getAlbums().isEmpty())
		{
			tv.setVisibility(View.GONE);
			albumAdapter = new AlbumGridAdapter(con);
			gv.setAdapter(albumAdapter);
		} else
		{
			tv.setVisibility(View.VISIBLE);
			tv.setSelectAllOnFocus(false);
			tv.setGravity(Gravity.CENTER);
			tv.setText("No Albums");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		final MenuItem searchItem = menu.findItem(R.id.search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQuery("", false);
		searchItem.collapseActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener()
		{

			@Override
			public boolean onQueryTextSubmit(String query)
			{
				Intent i = new Intent(con, Searchable.class);
				i.putExtra(AppConstants.QUERY, searchView.getQuery().toString());

				searchView.setQuery("", false);
				searchItem.collapseActionView();

				startActivity(i);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText)
			{
				return false;
			}
		});

		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					searchView.setQuery("", false);
					searchItem.collapseActionView();
				}
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getItemId();

		if (id == R.id.add_album)
		{
			final EditText name = new EditText(this);
			name.setHint("Album Name");

			final InputMethodManager imgr = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);

			final AlertDialog d = new AlertDialog.Builder(this).setView(name)
					.setTitle(R.string.new_album)
					.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface d, int which)
						{
						}
					}).setNegativeButton(android.R.string.cancel, null).create();
			d.setOnShowListener(new DialogInterface.OnShowListener()
			{

				@Override
				public void onShow(DialogInterface dialog)
				{

					imgr.showSoftInput(name, 0);
					name.requestFocus();

					Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick(View view)
						{
							String t = name.getText().toString();
							if (t.isEmpty())
							{
								Toast.makeText(con, "Album name cannot be empty.", Toast.LENGTH_SHORT)
										.show();
							} else
							{
								if (control.createAlbum(t))
								{
									Toast.makeText(con, "Created album " + t, Toast.LENGTH_SHORT)
											.show();
									refresh();
									d.dismiss();
								} else
								{
									Toast.makeText(con, "Album already " + t + " exists", Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					});
				}
			});
			d.show();
		} else if (id == android.R.id.home)
		{
			finish();
		} else if (id == R.id.add_photo)
		{
		} else if (id == R.id.take_photo)
		{
		} else
		{
			Toast.makeText(this, "Option not recognized: " + id, Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public void refresh()
	{
		showAlbums();
		gv.invalidate();
	}

	public void deleteAlbum(final String album) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
		alertDialogBuilder.setTitle("Delete Album");
		alertDialogBuilder.setMessage("Are you sure you want to delete this album?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						if (control.deleteAlbum(album))
						{
							Log.e("methods", this.getClass().getName());
							MainActivity.this.refresh();
						}
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}