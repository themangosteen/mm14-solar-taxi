package com.ikarus.solartaxi.highscores.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for database creation and version management
 * @author Nikolaus Leopold
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html#todo_layout
 */
public class HighscoreOpenHelper extends SQLiteOpenHelper {
	
	private static final int 	DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "highscore.db";
	
	public HighscoreOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		HighscoreTable.onCreate(db);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		HighscoreTable.onUpgrade(db, oldVersion, newVersion);
	}
	
}