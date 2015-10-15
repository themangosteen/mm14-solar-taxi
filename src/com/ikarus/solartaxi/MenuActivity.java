package com.ikarus.solartaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;

import com.ikarus.solartaxi.highscores.frontend.HighscoresActivity;

/**
 * Activity for the Menu, holds two Buttons (starting the Game and showing the High-Score Table).
 * OnClick for the buttons is set in the activity_menu.xml file.
 * 
 * @author Nikolaus Leopold
 *
 */
public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
	}

	/**
	 * Starts the GameActivity.
	 * @param view
	 */
	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		this.startActivity(intent);
		this.finish();

	}

	/**
	 * Starts HighscoresActivity
	 * @param view
	 */
	public void showHighscores(View view) {
		Intent intent = new Intent(this, HighscoresActivity.class);
		startActivity(intent);
		this.finish();
	}

}