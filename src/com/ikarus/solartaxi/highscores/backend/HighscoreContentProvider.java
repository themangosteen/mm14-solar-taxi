package com.ikarus.solartaxi.highscores.backend;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


/**
 * provides database access for application logic
 * @author Nikolaus Leopold
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html#todo_layout
 */
public class HighscoreContentProvider extends ContentProvider {
	
	// database helper
	HighscoreOpenHelper dbHelper;
	
	private static final String AUTHORITY = "com.ikarus.solartaxi.contentprovider";
	private static final String BASE_PATH = "highscore";
	
	// used for the UriMacher
	private static final int HIGHSCORE = 10;
	private static final int HIGHSCORE_ID = 20;
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/highscores";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/highscores";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, HIGHSCORE);
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", HIGHSCORE_ID);
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new HighscoreOpenHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection); // check if requested column exists
		queryBuilder.setTables(HighscoreTable.HIGHSCORE_TABLE_NAME);
		
		switch (sURIMatcher.match(uri)) {
			case HIGHSCORE:
				// do nothing
				break;
			case HIGHSCORE_ID:
				// add ID to query
				queryBuilder.appendWhere(HighscoreTable.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		// open connection to database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		// make sure listeners are being notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int uriCode = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long rowId = 0;

		switch (uriCode) {
		case HIGHSCORE:
			rowId = db.insert(HighscoreTable.HIGHSCORE_TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH + "/" + rowId);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriCode = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		
		switch (uriCode) {
			case (HIGHSCORE):
				rowsDeleted = db.delete(HighscoreTable.HIGHSCORE_TABLE_NAME, selection, selectionArgs);
				break;
			case (HIGHSCORE_ID):
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = db.delete(HighscoreTable.HIGHSCORE_TABLE_NAME,
							                HighscoreTable.COLUMN_ID + "=" + id, null);
				} else {
					rowsDeleted = db.delete(HighscoreTable.HIGHSCORE_TABLE_NAME,
							HighscoreTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsDeleted;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int uriCode = sURIMatcher.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int rowsUpdated = 0;

		switch (uriCode) {
		case HIGHSCORE:
			rowsUpdated = db.update(HighscoreTable.HIGHSCORE_TABLE_NAME, values, selection, selectionArgs);
			break;
		case HIGHSCORE_ID:
			String id = uri.getLastPathSegment();
			
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(HighscoreTable.HIGHSCORE_TABLE_NAME, values,
						HighscoreTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(HighscoreTable.HIGHSCORE_TABLE_NAME, values,
						HighscoreTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	// check if requested columns exist
	private void checkColumns(String[] projection) {
		String[] columns = { HighscoreTable.COLUMN_ID, HighscoreTable.COLUMN_NAME, HighscoreTable.COLUMN_SCORE };
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> existingColumns = new HashSet<String>(Arrays.asList(columns));
			
			// check if requested columns exist
			if (!existingColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Projection includes unknown columns");
			}
		}
	}
	
}