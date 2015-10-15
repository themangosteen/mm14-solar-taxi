package com.ikarus.solartaxi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Activity for the game
 * @author Nikolaus Leopold, Sebastian Kirchner
 */
public class GameActivity extends Activity {

	private GameView gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameView = new GameView(this);
		setContentView(gameView);

		// initialize and load sounds
		SoundPlayer.instance();
		SoundPlayer.init(this);
		SoundPlayer.loadSound(R.raw.taxi_explosion);
		SoundPlayer.loadSound(R.raw.taxi_shield);
		SoundPlayer.loadSound(R.raw.credits_earned);
		//SoundPlayer.loadSound(R.raw.passenger_greeting);
		SoundPlayer.loadSound(R.raw.taxi_thrust);

		// note: background music is handled in GameView
	}

	/**
	 * Added to avoid Game turning on automatically because of change in screen orientation, see also 
	 * Android Manifest file for this Activity.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)	{
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Pause the {@link com.ikarus.solartaxi.GameView}
	 */
	@Override
	protected void onPause() {
		super.onPause();

		gameView.setPaused(true);
	}

	/**
	 * Resume the {@link com.ikarus.solartaxi.GameView}
	 */
	@Override
	protected void onResume() {
		super.onResume();

		gameView.setPaused(gameView.isPaused());
	}

	/**
	 * Ensure that it is only possible to get to Menu from Game (destroys the Game State, meaning game starts from
	 * beginning)
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		this.startActivity(new Intent(this, MenuActivity.class));
	}

}