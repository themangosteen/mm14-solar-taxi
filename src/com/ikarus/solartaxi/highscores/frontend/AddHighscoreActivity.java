package com.ikarus.solartaxi.highscores.frontend;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ikarus.solartaxi.highscores.backend.*;
import com.ikarus.solartaxi.MenuActivity;
import com.ikarus.solartaxi.R;

/**
 * Activity that shows up after GameOver, asking the user for input of name and then 
 * saving the score in the database.
 * 
 * @author Sebastian Kirchner
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html#todo_layout
 *
 */
public class AddHighscoreActivity extends Activity {

	private int credits;
	private TextView new_score;
	private EditText input_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_highscore);
		
		// get the Credits, if not there set to 0 by default
		credits = getIntent().getIntExtra("CREDITS", 0);
		String creditString = "" + (credits > 999 ? credits/1000 + "," + credits%1000 : credits);
		
		// set the welcoming message including the scored Credits
		String score_msg = getResources().getString(R.string.score_your_score, creditString);
		new_score = (TextView) findViewById(R.id.score_lblNewScore);
		new_score.setText(score_msg);
		
		// set the name of the player
		input_name = (EditText) findViewById(R.id.score_editEnterName);
	}
	
	/**
	 * Pressing this button adds the new score, a valid name has to be entered 
	 * to call the next activity, otherwise an Error Toast will show up.
	 * @param view
	 */
	public void addScoreBtn(View view) {
		String name = input_name.getText().toString();
		
		// if name is empty (not only whitespace)
		if (!name.trim().isEmpty()) {
			
			// set values for name and score
			ContentValues values = new ContentValues();
			values.put(HighscoreTable.COLUMN_NAME, name);
			values.put(HighscoreTable.COLUMN_SCORE, credits);
			
			// add new score with name to database
			getContentResolver().insert(HighscoreContentProvider.CONTENT_URI, values);
			
			// call next activity
			this.startActivity(new Intent(this, HighscoresActivity.class));
			this.finish();
		} 
		else {
			Toast toast = Toast.makeText(this, R.string.score_err_no_name, Toast.LENGTH_SHORT);
			
			// set Gravity so Toast doesn't show up in keyboard
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
		}
	}
	
	/**
	 * Skip to Highscores Table without adding the new score.
	 */
	public void skipBtn(View view) {
		this.startActivity(new Intent(this, HighscoresActivity.class));
		this.finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		// make sure we can't get back to the Game or the GameOver Activity
		this.startActivity(new Intent(this, MenuActivity.class));
	}
}
