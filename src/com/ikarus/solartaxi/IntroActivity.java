package com.ikarus.solartaxi;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.VideoView;

/**
 * Activity for the Intro-Video, skips to MenuActivity after video finishes or by touching the screen. 
 * Has a no-history flag.
 * 
 * @author Sebastian Kirchner<br>
 * videos by Nikolaus Leopold (intro) and Sebastian Kirchner (teaser)
 *
 */
public class IntroActivity extends Activity implements OnTouchListener, OnCompletionListener {

	private VideoView vw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		vw = (VideoView) findViewById(R.id.introvideoview);
		Uri pathToVideo = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.solartaxi_intro);
		vw.setVideoURI(pathToVideo);
		vw.setOnTouchListener(this);
		vw.setOnCompletionListener(this);
		
		vw.start();
		vw.requestFocus();
	}

	/**
	 * Skip to MenuActivity upon touching the screen
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Intent myIntent = new Intent(v.getContext(), MenuActivity.class);
			startActivity(myIntent);
			finish();
			break;
		}
		
		return false;
	}

	/**
	 * Start MenuActivity upon completion of the Intro-Clip.
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		Intent myIntent = new Intent(this, MenuActivity.class);
		startActivity(myIntent);
		finish();
	}
}