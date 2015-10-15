package com.ikarus.solartaxi;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

/**
 * Represents the HelpDialogue. All Methods are static, Methods are called in @GameView and 
 * draw() in @GameEngine. All Strings can be found in res/values/strings.xml. Call init() 
 * first to avoid NullPointerExceptions() and use it as reset. <br>The Order in draw(), prev() 
 * and next() has to be kept, rather if the order in one method is changed, the others have
 * to be changed accordingly.
 * 
 * @author Sebastian Kirchner
 *
 */
public class HelpDialogue {
	
	// these booleans serve as the main logic for correct draing and for next()/prev() functionality
	private static boolean draw_help, draw_btns, draw_battery, draw_stats, draw_controls, draw_shields, draw_aim;
	
	// paints for text background, texts, arrows, etc.
	private static Paint textPaint, fieldPaint, highlightPaint, titlePaint, controlPaint, arrowPaint, bitmapPaint;
	
	// rectangle for the text field and one for the highlighting
	private static Rect textField, highlight;
	
	// float array to draw prev/skip arrows, needs 12 floats (4 per line)
	private static float[] rightArrow, leftArrow;
		
	/**
	 * Draws the Help Dialogue directly on the canvas, uses R.dimen and 
	 * canvas width/height for independent positioning. 
	 * 
	 * <br>IMPORTANTE NOTICE:
	 * If the order should be changed, skip and prev have to be changed 
	 * accordingly to ensure correct behaviour.
	 * 
	 * @param canvas canvas to draw on
	 * @param res Resources to access like dimen, color or strings
	 */
	public static void draw(Canvas canvas, Resources res) {
		// used as spaceing between lines and for corners on Rect
		float spaceing 	= res.getDimension(R.dimen.help_text_size);
		
		// start position of first line of help text
		float textX = canvas.getWidth()*0.24f, 
					textY = canvas.getHeight()*0.31f;
		
		// start position of the help headline
		float titleX = canvas.getWidth()*0.24f, 
					titleY = canvas.getHeight()*0.22f;
		
		// set the color and alpha value for text field
		fieldPaint.setColor(Color.BLACK);
		fieldPaint.setAlpha(240);
		
		// set the color and alpha value for the highlighting box
		highlightPaint.setColor(res.getColor(R.color.main_blue));
		highlightPaint.setAlpha(130);
		
		// set the color and size for the help texts
		textPaint.setColor(res.getColor(R.color.help_text));
		textPaint.setTextSize(res.getDimension(R.dimen.help_text_size));
		
		// set the color and size for the help headlines
		titlePaint.setColor(res.getColor(R.color.help_text));
		titlePaint.setTextSize(res.getDimension(R.dimen.help_heading_size));
		
		// set the color and alpha value for the next/prev arrows
		arrowPaint.setColor(res.getColor(R.color.main_theme));
		arrowPaint.setAlpha(255);
		
		// previous arrow drawn by 3 lines equals 12 float values
		leftArrow = new float[] {
				canvas.getWidth()*0.21f, canvas.getHeight()*0.47f,
				canvas.getWidth()*0.21f, canvas.getHeight()*0.35f,
				canvas.getWidth()*0.13f, canvas.getHeight()*0.41f,
				canvas.getWidth()*0.21f, canvas.getHeight()*0.35f,
				canvas.getWidth()*0.13f, canvas.getHeight()*0.41f,
				canvas.getWidth()*0.21f, canvas.getHeight()*0.47f,
		};
		
		// next arrow drawn by 3 lines equals 12 float values
		rightArrow = new float[] {
				canvas.getWidth()*0.79f, canvas.getHeight()*0.47f,
				canvas.getWidth()*0.79f, canvas.getHeight()*0.35f,
				canvas.getWidth()*0.87f, canvas.getHeight()*0.41f,
				canvas.getWidth()*0.79f, canvas.getHeight()*0.35f,
				canvas.getWidth()*0.87f, canvas.getHeight()*0.41f,
				canvas.getWidth()*0.79f, canvas.getHeight()*0.47f,
		};

		// DRAW FIRST HELP PAGE, info about help mode
		if (draw_help) {
			// draw 'next' arrow
			canvas.drawLines(rightArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.78));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// set text as not bold ==> has to be done in case we come back form controls
			textPaint.setFakeBoldText(false);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_help), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.help_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.help_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_3), textX, textY+3*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_4), textX, textY+5.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_5), textX, textY+6.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_6), textX, textY+8.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.help_7), textX, textY+9.5f*spaceing, textPaint);
		}
		
		// DRAW INFO ABOUT HOW TO CONTROL SPACECRAFT (-> where to touch)
		else if (draw_controls) {
			// draw 'prev'/'next' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			canvas.drawLines(rightArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.4));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// set color and alpha value for bounding box of control touch area 
			controlPaint.setColor(res.getColor(R.color.main_theme));
			controlPaint.setAlpha(255);
				
			// set text as bold ==> reset to not bold in next and previous help pages
			textPaint.setFakeBoldText(true);
			
			// set rectangles for control touch areas
			Rect right	= new Rect((int) (canvas.getWidth()*0.5), (int) (canvas.getHeight()*0.75), canvas.getWidth(), (int) (canvas.getHeight()));
			Rect left 	= new Rect(0, (int) (canvas.getHeight()*0.75), (int) (canvas.getWidth()*0.5), (int) (canvas.getHeight()));
			Rect straight = new Rect(0, (int) (canvas.getHeight()*0.5), canvas.getWidth(), (int) (canvas.getHeight()*0.75));
			
			// set bounding box rectangle for touch areas, no fill
			highlight = new Rect((int)(canvas.getWidth()*0.48), 0,(int) canvas.getWidth(),(int) (canvas.getHeight()*0.1));
			
			// draw rectangles for touch areas with round corners
			canvas.drawRoundRect(new RectF(left), spaceing, spaceing, highlightPaint);
			canvas.drawRoundRect(new RectF(right), spaceing, spaceing, highlightPaint);
			canvas.drawRoundRect(new RectF(straight), spaceing, spaceing, highlightPaint);
			
			canvas.drawRoundRect(new RectF(left), spaceing, spaceing, controlPaint);
			canvas.drawRoundRect(new RectF(right), spaceing, spaceing, controlPaint);
			canvas.drawRoundRect(new RectF(straight), spaceing, spaceing, controlPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_contols), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.control_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.control_left), canvas.getWidth()*0.1f, canvas.getHeight()*0.9f, textPaint);
			canvas.drawText(res.getString(R.string.control_right), canvas.getWidth()*0.6f, canvas.getHeight()*0.9f, textPaint);
			canvas.drawText(res.getString(R.string.control_straight), canvas.getWidth()*0.35f, canvas.getHeight()*0.63f, textPaint);
		}
		
		// DRAW INFORMATION ABOUT THE AIM OF THE GAME
		else if (draw_aim) {
			// draw 'prev'/'next' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			canvas.drawLines(rightArrow, arrowPaint);
			
			// set text as not bold ==> has to be done because it is set to bold in controls
			textPaint.setFakeBoldText(false);

			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.78));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_aim), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.aim_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.aim_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_3), textX, textY+3*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_4), textX, textY+4f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_5), textX, textY+7.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_6), textX, textY+8.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.aim_7), textX, textY+9.5f*spaceing, textPaint);
		}
		
		// DRAW INFORMATION ABOUT THE SHIELDS
		else if (draw_shields) {
			// draw 'prev'/'next' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			canvas.drawLines(rightArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.78));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// draw highlighting box around shields
			highlight = new Rect((int)(canvas.getWidth()*0.4), 0,(int) (canvas.getWidth()*0.6),(int) (canvas.getHeight()*0.12));
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,highlightPaint);
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,controlPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_shields), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.shields_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.shields_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_3), textX, textY+5*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_4), textX, textY+6*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_5), textX, textY+7.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_6), textX, textY+8.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.shields_7), textX, textY+9.5f*spaceing, textPaint);
		}
		
		// DRAW INFORMATION ABOUT THE BATTERY
		else if (draw_battery) {
			// draw 'prev'/'next' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			canvas.drawLines(rightArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.78));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// draw highlighting box around battery
			highlight = new Rect(0, 0, (int)(canvas.getWidth()*0.14),(int) (canvas.getHeight()*0.1));
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,highlightPaint);
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,controlPaint);

			// draw taxi with loading panel for clarity
			Bitmap taxi = BitmapBank.getTaxiBitmap(00);
			Bitmap chargingPanelRight = BitmapBank.getPanelChargeRightBitmap();
			canvas.drawBitmap(taxi, canvas.getWidth()/2 - taxi.getWidth()/2, textY+3.3f*spaceing, bitmapPaint);
			canvas.drawBitmap(chargingPanelRight, canvas.getWidth()/2 - chargingPanelRight.getWidth()/2, textY+3.3f*spaceing, bitmapPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_battery), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.battery_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.battery_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_3), textX, textY+3*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_4), textX, textY+6.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_5), textX, textY+7.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_6), textX, textY+8.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.battery_7), textX, textY+9.5f*spaceing, textPaint);
		}
		
		// DRAW INFORMATION ABOUT CREDITS AND PASSENGERS
		else if (draw_stats) {
			// draw 'prev'/'next' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			canvas.drawLines(rightArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.85));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);

			// draw highlighting rectangle around credits and passenger count
			highlight = new Rect(0, (int) (canvas.getHeight()*0.12), (int)(canvas.getWidth()*0.2),(int) (canvas.getHeight()*0.35));
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,highlightPaint);
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,controlPaint);
			
			// draw the target planet indicator for clarity
			Bitmap indicator = BitmapBank.getTargetIndicatorBitmap();
			canvas.drawBitmap(indicator, canvas.getWidth()*0.72f, textY+11*spaceing, bitmapPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_stats), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.stats_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.stats_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_3), textX, textY+3*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_4), textX, textY+4*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_5), textX, textY+6*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_6), textX, textY+7*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_7), textX, textY+8*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_8), textX, textY+9.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_9), textX, textY+10.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.stats_10), textX, textY+11.5f*spaceing, textPaint);
		}
		
		// DRAW INFORMATION ABOUT BUTTONS
		else if (draw_btns) {
			// draw 'prev' arrow
			canvas.drawLines(leftArrow, arrowPaint);
			
			// draw text field as round rectangle
			textField = new Rect((int) (canvas.getWidth()*0.22), (int) (titleY + 0.5*spaceing),(int) ( canvas.getWidth()*0.78f), (int) (canvas.getHeight()*0.78));
			canvas.drawRoundRect(new RectF(textField), spaceing, spaceing, fieldPaint);
			
			// draw highlighting rectangle around buttons
			highlight = new Rect((int)(canvas.getWidth()*0.8), 0,(int) canvas.getWidth(),(int) (canvas.getHeight()*0.1));
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,highlightPaint);
			canvas.drawRoundRect(new RectF(highlight), spaceing, spaceing,controlPaint);
			
			// draw heading and text
			canvas.drawText(res.getString(R.string.help_title_buttons), titleX, titleY, titlePaint);
			canvas.drawText(res.getString(R.string.buttons_0), textX, textY, textPaint);
			canvas.drawText(res.getString(R.string.buttons_1), textX, textY+1*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_2), textX, textY+2*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_3), textX, textY+3*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_4), textX, textY+5.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_5), textX, textY+6.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_6), textX, textY+7.5f*spaceing, textPaint);
			canvas.drawText(res.getString(R.string.buttons_7), textX, textY+9.5f*spaceing, textPaint);
		}
	}
	
	/**
	 * Skips to the next help page, if called on last page nothing happens.
	 * For this to work the order has to be the same as in draw() method.
	 */
	public static void next() {
		if (draw_help) {
			draw_help = false;			
		}
		else if (draw_controls) {
			draw_controls = false;
		}
		else if (draw_aim) {
			draw_aim = false;
		}
		else if (draw_shields) {
			draw_shields = false;
		} 
		else if (draw_battery) {
			draw_battery = false;
		}
		else if (draw_stats) {
			draw_stats = false;
		}
	}
	
	/**
	 * Goes back to the previous help page, if called on first page nothing happens.
	 * For this to work it has to be in the opposite order as in draw() and next().
	 */
	public static void prev() {
		if (!draw_stats) {
			draw_stats = true;			
		}
		else if (!draw_battery) {
			draw_battery = true;
		}
		else if (!draw_shields) {
			draw_shields = true;
		} 
		else if (!draw_aim) {
			draw_aim = true;
		}
		else if (!draw_controls) {
			draw_controls = true;
		}
		else if (!draw_help) {
			draw_help = true;
		}
	}
	
	/**
	 * Initializes all necessary values. Must be called once before 
	 * any other method to avoid NullPointerException.
	 */
	public static void init() {
		
		// initialize all booleans to true
		draw_help 		= true;
		draw_aim 		= true;
		draw_shields 	= true;
		draw_battery 	= true;
		draw_controls 	= true;
		draw_stats 		= true;
		draw_btns		= true;
		
		// Initialize all Paints and set as much information as possible without
		// access to resources
		titlePaint = new Paint();
		titlePaint.setTextAlign(Align.LEFT);
		
		textPaint = new Paint();
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
		textPaint.setAntiAlias(true);
		
		fieldPaint = new Paint();
		
		highlightPaint = new Paint();
		highlightPaint.setStyle(Paint.Style.FILL);
		highlightPaint.setAntiAlias(true);
		
		arrowPaint = new Paint();
		arrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		arrowPaint.setStrokeWidth(3);
		arrowPaint.setAntiAlias(true);
		
		controlPaint = new Paint();
		controlPaint.setStyle(Paint.Style.STROKE);
		controlPaint.setStrokeWidth(5);
		
		bitmapPaint = new Paint();
		bitmapPaint = new Paint();
		bitmapPaint.setStrokeWidth(10);
		bitmapPaint.setFilterBitmap(true);
		bitmapPaint.setAntiAlias(true);
	}

}
