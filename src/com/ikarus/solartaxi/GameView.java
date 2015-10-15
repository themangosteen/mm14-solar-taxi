package com.ikarus.solartaxi;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.ikarus.solartaxi.level.*;

/**
 * The View the user interacts with (provides events and drawing)
 * 
 * @author Sebastian Kirchner, Nikolaus Leopold<br />
 * Touch-based input by Sebastian Kirchner
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	//calls update and draw
	private GameLoopThread thread;
	
	//provides main game logic
	private GameEngine engine; 
	
	//object that manages this surface
	private SurfaceHolder surfaceHolder; 

	// MediaPlayer for background music
	private MediaPlayer mediaPlayer;

	private boolean paused, helpMode;

	private float soundVol, taxiSpeed;

	public GameView(Context context) {
		super(context);

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this); //callback for surface events (created, changed, destroyed)

		//to handle events
		setFocusable(true); 

		paused = false;
		helpMode = false;
		
		taxiSpeed = this.getResources().getDimension(R.dimen.taxi_speed);

		// prepare background music
		mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.background_music);
		mediaPlayer.setLooping(true);

		engine = new GameEngine(this, new Level1(this));
	}

	/**
	 * Pauses the game or resumes it. Keeps Screen from turning off if game is on and allows it 
	 * to turn off in Pause mode.
	 * @param paused true if game is to be paused.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
		Activity gameActivity = (Activity) this.getContext();

		if (paused == true) {
			mediaPlayer.pause();
			gameActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		else {
			mediaPlayer.start();
			gameActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	/**
	 * @return true if game is paused, else false
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @return true if background music is playing, else false
	 */
	public boolean isMusicOn() {
		return mediaPlayer.isPlaying(); 
	}

	/**
	 * @return true if currently in help mode, else false
	 */
	public boolean inHelpMode() {
		return helpMode;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start background music
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.background_music);
			mediaPlayer.setVolume(SoundPlayer.volume*0.4f, SoundPlayer.volume*0.4f);
			mediaPlayer.start();
		}

		// start gameloop
		thread = new GameLoopThread(getHolder(), this, engine);
		thread.setFinished(false);
		thread.start(); //calls thread constructor and run()
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mediaPlayer.pause();

		thread.setFinished(true);
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} 
			catch (InterruptedException e) {
				//try again and again
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int ratio, int width, int height) {
	}

	/**
	 * Handles the Touch Events for controlling the SpaceShip, Buttons and Help Mode.  
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int maskedAction = event.getActionMasked();

		switch (maskedAction) {

		case MotionEvent.ACTION_DOWN:
		{
			float Y = event.getY();
			float X = event.getX();

			// toggle game pause button
			if (Y < getHeight()*0.15 && X > getWidth() * 0.92)  {
				if (inHelpMode()) {

				} else {
					setPaused(!paused);
					break;					
				}
			}

			// toggle background music button
			if (Y < getHeight()*0.15 && X > getWidth() * 0.84 && X <= getWidth() * 0.92)  {
				if (inHelpMode()) {

				} else {

					if (mediaPlayer.isPlaying()) {
						soundVol = SoundPlayer.volume;
						SoundPlayer.volume = -1;
						mediaPlayer.pause();
					}
					else {
						SoundPlayer.volume = soundVol;
						mediaPlayer.start();
					}
					break;
				}
			}

			// Toggle Help Mode
			if (Y < getHeight()*0.15 && X > getWidth() * 0.76 && X <= getWidth() * 0.84)  {
				helpMode = !helpMode;
				if (helpMode) {
					setPaused(true);
				} else {
					HelpDialogue.init();
					setPaused(false);
				}
				break;
			}

			// skip and prev buttons HelpMode
			if (inHelpMode()) {
				if (Y > getHeight()*0.33f && Y < getHeight()*0.49f) {
					// next help page
					if (X > getWidth()*0.8f && X < getWidth() * 0.85f) {
						HelpDialogue.next();
					} 
					// previous help page
					else if (X > getWidth()*0.15f && X < getWidth()*0.2f) {
						HelpDialogue.prev();
					}
				}
			}

			if (paused) {
				break;
			}

			// SpaceShip Movement controls
			if (Y > getHeight()*0.75)  {

				if (X < getWidth() * 0.5) {
					engine.taxiApplyThrust(0, taxiSpeed);
				} 
				else if (X > getWidth() * 0.5) {
					engine.taxiApplyThrust(taxiSpeed, 0);
				}
			}
			else if (Y > getHeight() * 0.2){
				engine.taxiApplyThrust(taxiSpeed, taxiSpeed);
			}

			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN:
		{	
			if (paused) {
				break;
			}

			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		{
			break;
		}

		}

		// return true, otherwise MotionEvent.ACTION_MOVE will not be detected
		return true;
	}

}