package com.ikarus.solartaxi;

import com.ikarus.solartaxi.highscores.frontend.AddHighscoreActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Activity for SplashScreen when all levels have been completed or Taxi has crashed.
 * 
 * @author Nikolaus Leopold, Sebastian Kirchner
 *
 */
public class EndOfGameActivity extends Activity {

	private int credits, causeOfEnd;
	private static boolean clicked;
	private Handler x;
	private Runnable runner;


	/**
	 * Sets all ImageViews to Invisible and according to getExtras the corresponding
	 * Splashscreen is shown. Implements a Handler so it terminates automatically after
	 * 4.5 seconds.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endofgame);

		credits = getIntent().getExtras().getInt("CREDITS", 0);
		causeOfEnd = getIntent().getExtras().getInt("ENDSTATE", 0);
		clicked = false;

		// GameOver by Planet Crash
		ImageView crashed_text 	= (ImageView) findViewById(R.id.splash_crashtext);
		ImageView crashed_anim	= (ImageView) findViewById(R.id.splash_explosion);

		// GameOver by Sun Crash
		ImageView ikarused_text	= (ImageView) findViewById(R.id.splash_ikarustext);
		ImageView ikarused_anim = (ImageView) findViewById(R.id.splash_ikarusnova);
		
		// GameOver by Battery Dead
		ImageView battery_text = (ImageView) findViewById(R.id.splash_oobatterytext);
		ImageView battery_anim = (ImageView) findViewById(R.id.splash_oobatteryimg);
		
		// Game Cleared by getting max HighScore
		ImageView finished_text	= (ImageView) findViewById(R.id.splash_finished);

		// Set all ImageViews to Invisible
		crashed_text.setVisibility(View.INVISIBLE);
		crashed_anim.setVisibility(View.INVISIBLE);
		ikarused_text.setVisibility(View.INVISIBLE);
		ikarused_anim.setVisibility(View.INVISIBLE);
		battery_text.setVisibility(View.INVISIBLE);
		battery_anim.setVisibility(View.INVISIBLE);
		finished_text.setVisibility(View.INVISIBLE);

		// create a new Handler to start the next activity after 4.5 seconds
		x = new Handler();
		runner = new Runnable() {

			@Override
			public void run() {
				if (!clicked) {
					Intent intent = new Intent(EndOfGameActivity.this, AddHighscoreActivity.class);
					intent.putExtra("CREDITS", credits);
					startActivity(intent);
					finish();
				}
			} 
		};
		
		x.postDelayed(runner, 4500);

		// Crash with planet
		if (causeOfEnd == 0) {
			Animation explosionAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_crash_explosion);
			crashed_anim.startAnimation(explosionAnim);

			Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_crash_text);
			crashed_text.startAnimation(splashAnim);
			crashed_text.setVisibility(View.VISIBLE);
			
		} 
		
		// Crash with sun
		else if (causeOfEnd == 1) {
			Animation novaAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_ikarus_nova);
			ikarused_anim.startAnimation(novaAnim);
			
			Animation ikarusAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_ikarus);
			ikarused_text.startAnimation(ikarusAnim);
			ikarused_text.setVisibility(View.VISIBLE);
			
		} 
		
		// Battery dead
		else if (causeOfEnd == 2) {
			Animation battAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_battery_img);
			battery_anim.startAnimation(battAnim);
			
			Animation oobattAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_battery_text);
			battery_text.startAnimation(oobattAnim);
			battery_text.setVisibility(View.VISIBLE);
			
		} 
		
		// Cleared Game
		else if (causeOfEnd == 3) {

			Animation ikarusAnim = AnimationUtils.loadAnimation(this, R.anim.splash_end_cleared);
			finished_text.startAnimation(ikarusAnim);
			
		}

	}

	/**
	 * Remove the Callback for correct behaviour if activity is skipped
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		x.removeCallbacksAndMessages(null);
		
		SoundPlayer.release(); 
	}

	/**
	 * Skip ahead on touch.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		clicked = true;
		x.removeCallbacksAndMessages(null);
		Intent intent = new Intent(this, AddHighscoreActivity.class);
		intent.putExtra("CREDITS", this.credits);
		startActivity(intent);
		this.finish();

		return false;
	}

	/**
	 * Ensure you can't get back to Game from here by setting next activity manually
	 */
	@Override
	public void onBackPressed() {

		clicked = true;
		x.removeCallbacksAndMessages(null);
		this.startActivity(new Intent(this, AddHighscoreActivity.class));
		this.finish();
	}

}