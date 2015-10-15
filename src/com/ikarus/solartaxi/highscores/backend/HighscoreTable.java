package com.ikarus.solartaxi.highscores.backend;

import android.database.sqlite.SQLiteDatabase;

/**
 * provides static structure and commands for SQLite highscore table
 * @author Nikolaus Leopold
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html#todo_layout
 */
public class HighscoreTable {
	
	public static final String HIGHSCORE_TABLE_NAME = "highscore";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SCORE = "score";
	
	// sqlite create command agglutination
	private static final String HIGHSCORE_TABLE_CREATE =
            "CREATE TABLE " + HIGHSCORE_TABLE_NAME + " (" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " TEXT not null, " +
            COLUMN_SCORE + " integer);";
	
	public static void onCreate(SQLiteDatabase db) {
		// create highscore table
		db.execSQL(HIGHSCORE_TABLE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// delete table if already exists
		db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORE_TABLE_NAME);
		// create new table
		onCreate(db);
	}
	
	
}