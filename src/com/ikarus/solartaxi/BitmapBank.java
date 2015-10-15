package com.ikarus.solartaxi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Static class to load bitmaps and manage references to them
 * 
 * @author Nikolaus Leopold
 */
public class BitmapBank {
		
	private static Bitmap taxi_sprites, taxi_explode, taxi_shield;
	private static Bitmap battery, chargingPanelLeft, chargingPanelRight;
	private static Bitmap targetIndicator;
	private static Bitmap btn_pauseBM, btn_unpauseBM, btn_musicOnBM, btn_musicOffBM;
	private static Bitmap passenger_moognu, passenger_weirdo;
	private static Bitmap help_arrow;

	public static void init(Resources res) {
		taxi_sprites = BitmapFactory.decodeResource(res, R.drawable.taxi_sprites);
		taxi_explode = BitmapFactory.decodeResource(res, R.drawable.taxi_explode);
		taxi_shield  = BitmapFactory.decodeResource(res, R.drawable.taxi_shield);

		chargingPanelLeft 	= BitmapFactory.decodeResource(res, R.drawable.charging_panel_left);
		chargingPanelRight 	= BitmapFactory.decodeResource(res, R.drawable.charging_panel_right);
		targetIndicator 	= BitmapFactory.decodeResource(res, R.drawable.target_indicator);
		
		battery 		= BitmapFactory.decodeResource(res, R.drawable.battery_sprite_8);

		btn_pauseBM 	= BitmapFactory.decodeResource(res, R.drawable.button_pause);
		btn_unpauseBM 	= BitmapFactory.decodeResource(res, R.drawable.button_unpause);
		btn_musicOnBM 	= BitmapFactory.decodeResource(res, R.drawable.button_music_on);
		btn_musicOffBM 	= BitmapFactory.decodeResource(res, R.drawable.button_music_off);
		
		passenger_moognu = BitmapFactory.decodeResource(res, R.drawable.passenger_moognu);
		passenger_weirdo = BitmapFactory.decodeResource(res, R.drawable.passenger_weirdo);
		
		help_arrow = BitmapFactory.decodeResource(res, R.drawable.help_arrow);
		
	}

	/**
	 * Returns Taxi Bitmap corresponding to bitmapState code<br>
	 * accepted codes:<br>
	 * 00   ... no thrust<br>
	 * 01   ... right thruster enabled<br>
	 * 10   ... left thruster enabled<br>
	 * 11   ... both thrusters enabled<br>
	 * 100  ... no thrust, legs extended<br>
	 * -1   ... taxi explode
	 * 
	 * @param bitmapState current bitmap state code
	 * @return the corresponding bitmap
	 */
	public static Bitmap getTaxiBitmap(int bitmapState) {
		
		int frame_wid = (int)Math.floor(taxi_sprites.getWidth()/5.0);
		int frame_hgt = taxi_sprites.getHeight();
		
		if (bitmapState == 00) {
			return Bitmap.createBitmap(taxi_sprites, 0*frame_wid, 0, frame_wid, frame_hgt);
		} else if (bitmapState == 01) {
			return Bitmap.createBitmap(taxi_sprites, 1*frame_wid, 0, frame_wid, frame_hgt);
		} else if (bitmapState == 10) {
			return Bitmap.createBitmap(taxi_sprites, 2*frame_wid, 0, frame_wid, frame_hgt);
		} else if (bitmapState == 11) {
			return Bitmap.createBitmap(taxi_sprites, 3*frame_wid, 0, frame_wid, frame_hgt);
		} else if (bitmapState == 100) {
			return Bitmap.createBitmap(taxi_sprites, 4*frame_wid, 0, frame_wid, frame_hgt);
		} else if (bitmapState == 1100) {
			return taxi_shield;
		} else if (bitmapState == -1) {
			return taxi_explode;
		} else {
			return Bitmap.createBitmap(taxi_sprites, 0*frame_wid, 0, frame_wid, frame_hgt);
		}
	}

	/**
	 * @return Bitmap used as overlay to indicate target planets
	 */
	public static Bitmap getTargetIndicatorBitmap() {
		return targetIndicator;
	}
	
	/**
	 * @return Bitmap used as overlay when charging from left solar panel
	 */
	public static Bitmap getPanelChargeLeftBitmap() {
		return chargingPanelLeft;
	}
	
	/**
	 * @return Bitmap used as overlay when charging from right solar panel
	 */
	public static Bitmap getPanelChargeRightBitmap() {
		return chargingPanelRight;
	}

	/**
	 * @param d the charge amount of the battery
	 * @return Bitmap used for taxi battery
	 */
	public static Bitmap getBatteryBitmap(double d) {
		
		int frame_wid = (int)Math.floor(battery.getWidth()/4.0);
		int frame_hgt = (int)Math.floor(battery.getHeight()/2.0);

		if (d > 0.87) {
			return Bitmap.createBitmap(battery, 3*frame_wid, 0, frame_wid, frame_hgt);
		} else if (d > 0.75) {
			return Bitmap.createBitmap(battery, 3*frame_wid, frame_hgt, frame_wid, frame_hgt);
		} else if (d > 0.63) {
			return Bitmap.createBitmap(battery, 2*frame_wid, 0, frame_wid, frame_hgt);
		} else if (d > 0.5) {
			return Bitmap.createBitmap(battery, 2*frame_wid, frame_hgt, frame_wid, frame_hgt);
		} else if (d > 0.37) {
			return Bitmap.createBitmap(battery, 1*frame_wid, 0, frame_wid, frame_hgt);
		} else if (d > 0.25) {
			return Bitmap.createBitmap(battery, 1*frame_wid, frame_hgt, frame_wid, frame_hgt);
		} else if (d > 0.12) {
			return Bitmap.createBitmap(battery, 0*frame_wid, 0, frame_wid, frame_hgt);
		} else {
			return Bitmap.createBitmap(battery, 0*frame_wid, frame_hgt, frame_wid, frame_hgt);
		}
	}
	
	public static Bitmap getPauseBtnBitmap(int on) {
		return on == 0 ? btn_pauseBM : btn_unpauseBM;
	}

	public static Bitmap getMusicBtnBitmap(int on) {
		return on == 0 ? btn_musicOnBM : btn_musicOffBM;
	}
	
	public static Bitmap getPassengerBitmap(int lvl) {
		return lvl == 1 ? passenger_weirdo : passenger_moognu;
	}
	
	public static Bitmap getHelpArrow() {
		return help_arrow;
	}
}