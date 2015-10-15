package com.ikarus.solartaxi.highscores.frontend;

import com.ikarus.solartaxi.MenuActivity;
import com.ikarus.solartaxi.R;
import com.ikarus.solartaxi.highscores.backend.HighscoreContentProvider;
import com.ikarus.solartaxi.highscores.backend.HighscoreTable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Activity representing the Highscores Table.
 * 
 * @author Sebastian Kirchner
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html#todo_layout
 */
public class HighscoresActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int DELETE_ID = Menu.FIRST + 1;
	
	private SimpleCursorAdapter dbAdapter;
	
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscores);
		
		listView = (ListView) findViewById(R.id.highscores_listView);
		listView.setEmptyView(findViewById(R.id.highscores_emptyList));
		showData();
		
		registerForContextMenu(listView);
	}
	
	private void showData() {
		listView.setVisibility(View.GONE);
		
		getLoaderManager().initLoader(0, null, this);
		
		String[] from = new String[] {HighscoreTable.COLUMN_NAME, HighscoreTable.COLUMN_SCORE };
		int[] to = new int[] { R.id.entry_lblName, R.id.entry_lblScore };
		
		dbAdapter = new SimpleCursorAdapter(this, R.layout.highscore_entry_layout, null, from, to, 0);
		
		listView.setAdapter(dbAdapter);
 	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(0, DELETE_ID, 0, R.string.highscores_entryDelete);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info =  (AdapterContextMenuInfo) item.getMenuInfo();
			deleteEntry(info.position);
			
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void deleteEntry(int itemPosition) {
		Cursor itemToDelete = (Cursor) listView.getItemAtPosition(itemPosition);
		long id = itemToDelete.getLong(0);
		
		Uri uri = Uri.parse(HighscoreContentProvider.CONTENT_URI + "/" + id);
		
		getContentResolver().delete(uri, null, null);
		
		showData();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		String[] projection = {HighscoreTable.COLUMN_ID, HighscoreTable.COLUMN_NAME, HighscoreTable.COLUMN_SCORE };
		CursorLoader cursorLoader = new CursorLoader(this, HighscoreContentProvider.CONTENT_URI, projection, null, null, HighscoreTable.COLUMN_SCORE + " DESC");
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		dbAdapter.swapCursor(data);
		listView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		dbAdapter.swapCursor(null);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		this.startActivity(new Intent(this, MenuActivity.class));
	}
}