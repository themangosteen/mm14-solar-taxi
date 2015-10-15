package com.ikarus.solartaxi;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

/**
 * Static Singleton class which stores a SoundPool
 * can be accessed from anywhere to play/stop sounds.
 * @author Nikolaus Leopold
 */
public class SoundPlayer {

	public static float volume;
	private static SoundPlayer instance; // only instance of SoundPlayer (Singleton) 

	private static SoundPool soundPool;
	private static SparseIntArray soundIDs; // SparseIntArray is an efficient int to int map provided by Android API
	private static AudioManager  audioManager;
	private static Context context;

	private SoundPlayer() {		
		// do nothing
	}

	/**
	 * @return only instance of SoundPlayer (Singleton)
	 */
	public static synchronized SoundPlayer instance() {

		if (instance == null) {
			instance = new SoundPlayer();
		}
		return instance;
	}

	/**
	 * initialize soundplayer
	 * @param context application context
	 */
	public static void init(Context context) {

		SoundPlayer.context = context;
		soundIDs = new SparseIntArray();
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

	}

	/**
	 * load a new sound into the sound pool
	 * @param rawID Android resource ID of the raw sound file
	 */
	public static void loadSound(int rawID) {
		soundIDs.put(rawID, soundPool.load(context, rawID, 0));
	}


	/**
	 * deallocates resources and SoundPlayer instance
	 */
	public static void release() {

		soundPool.release();
		soundPool = null;
		soundIDs.clear();
		audioManager.unloadSoundEffects();
		instance = null;
	}

	/**
	 * @param rawID Android resource ID of the raw sound file
	 * @param pitch playback speed/pitch, between 0.5 and 2.0
	 * @param repetitions how many times to loop (0 to play once, -1 for infinite loop)
	 */
	public static void playSound(int rawID, int repetitions, float pitch) {
		// hack for turning off the sound effects when sound is off (button) 
		if (volume != -1) {
			volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			soundPool.play(soundIDs.get(rawID), volume, volume, 0, repetitions, pitch);
		} else if (volume == -1) {
			soundPool.play(soundIDs.get(rawID), 0, 0, 0, repetitions, pitch);	
		}
	}

	/**
	 * @param rawID Android resource ID of the raw sound file
	 */
	public static void stopSound(int rawID) {
		soundPool.stop(soundIDs.get(rawID));
	}



}
