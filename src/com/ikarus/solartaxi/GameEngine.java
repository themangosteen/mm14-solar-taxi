package com.ikarus.solartaxi;

import java.util.Collection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;

import com.ikarus.solartaxi.celestialbodies.CelestialBody;
import com.ikarus.solartaxi.celestialbodies.Planet;
import com.ikarus.solartaxi.level.Level;

/**
 * Main game logic class, stores relevant object references and provides 
 * update and draw routines. 
 * 
 * @author Nikolaus Leopold, Sebastian Kirchner<br>
 * ViewMatrix Transformations by Nikolaus Leopold<br>
 * Spaceship Controls related methods by Sebastian
 */
public class GameEngine {
	
	Matrix identityMatrix = new Matrix();
	
	private GameView view;
	private Paint canvasPaint, starPaint, bitmapPaint, textPaint, helpPaint, levelPaint;
	private Bitmap batteryBM, shieldsBM, btn_pauseBM, btn_unpauseBM, btn_musicOnBM, btn_musicOffBM,
					targetIndicatorBM, panelChargeLeftBM, panelChargeRightBM, taxiBM, 
					passengerBM,
					help_arrowBM;
	
	private int lastLevelsScore, width_s_Margin, height_s_Margin, height_l_margin, frame_count, alpha_level; 
	private String credits, credits_required;
		
	private Level level;
	private Spaceship taxi;
	
	/**
	 * Constructor
	 * 
	 * @param view the view the game should run on
	 * @param level the level/world the engine should run
	 */
	public GameEngine(GameView view, Level firstLevel) {

		this.view = view;
		
		// necessary to initialize static classes once
		BitmapBank.init(view.getResources());
		HelpDialogue.init();
		
		// showing credits
		credits = view.getResources().getString(R.string.credits_sign) + " 0";
		credits_required = view.getResources().getString(R.string.required_credits) + ": " + firstLevel.getRequiredCredits();

		level = firstLevel;
		lastLevelsScore = 0;
		frame_count = 0;
		alpha_level = 255;
		
		// initialize Constant Bitmaps
		initConstantBMs();
		
		// initialize Paints
		initPaints();
		
		// Initialize new SpaceTaxi
		taxi = new Spaceship(level.getTaxiStartPosX(), level.getTaxiStartPosY(), level.getTaxiStartOrientation());
		
		taxi.setGravityFactor(view.getResources().getDimension(R.dimen.gravity_factor));
	}

	/**
	 * Update the Game state, calls {@link com.ikarus.solartaxi.SpaceShip} update method
	 */
	public void update() {

		taxi.update(level.getCelestialBodies());
		taxi.checkWorldBoundaryCollision(level.getMinX(), level.getMinY(), level.getMaxX(), level.getMaxY());
		
		Log.d("speed", "speed: " + taxi.getSpeed());
		// would have to be changed if credits could go past one million :D
		int score = taxi.getCredits();
		credits = view.getResources().getString(R.string.credits_sign) + String.format(" %,d", score);
		credits_required = view.getResources().getString(R.string.required_credits) + ": " + String.format(" %,d", level.getRequiredCredits());

		// Crash with Planets
		if (taxi.isCrashed()) {
			startEndOfGame(0);
		}
		
		// Crash with Sun
		if (taxi.isIkarused()) {
			startEndOfGame(1);
		}

		// Battery Dead
		if (taxi.isOutOfBattery()) {
			startEndOfGame(2);
		}
		
		// first level cleared
		if (taxi.getCredits() > level.getRequiredCredits()) {
			nextLevel();
		}
	}

	/**
	 * Main draw Routine, calls private draw methods.
	 * @param canvas canvas to draw on
	 */
	public void draw(Canvas canvas) {
		
		// draw black background
		canvas.drawPaint(canvasPaint);

		// draw game stats like battery, score, nr. of passengers, 
		drawRelativeToTaxi(canvas);

		// draw game objects like planets, suns, passengers or stars
		drawRelativeToCanvasOrigin(canvas);
		
	}

	/**
	 * Draw with canvas matrix set to Taxi view Matrix
	 * @param canvas canvas to draw on
	 */
	private void drawRelativeToTaxi(Canvas canvas) {

		canvas.setMatrix(getViewMatrix(canvas));

		// draw stars on background, randomly created in Level class
		canvas.drawPoints(level.getStars(), starPaint);

		// draw celestial bodies as circles
		for (CelestialBody cb : level.getCelestialBodies()) {
			canvas.drawCircle(cb.getX(), cb.getY(), cb.getRadius(), cb.getSurfaceColor());

			// if it is a planet, draw passengers
			if (cb instanceof Planet) {
				Collection<Matrix> passengersMatrices = ((Planet)cb).getPassengerMatrices();

				for (Matrix passengerMatrix : passengersMatrices) {
					passengerMatrix.preTranslate(-passengerBM.getWidth()/2, -passengerBM.getHeight()/2);
					canvas.drawBitmap(passengerBM, passengerMatrix, canvasPaint);
				}
			}
		}

		// draw passenger target planet indicators
		for (Planet p : taxi.getTargetPlanets()) {

			double targetAngle = GeometricCalc.angle(p.getX(), p.getY(), taxi.getX(), taxi.getY());

			Matrix targetMatrix = new Matrix();
			targetMatrix.postRotate(90);
			targetMatrix.postTranslate(canvas.getHeight()/5 - targetIndicatorBM.getWidth()/2, - targetIndicatorBM.getHeight()/2);
			targetMatrix.postRotate((float)(targetAngle*180/Math.PI));
			targetMatrix.postTranslate(taxi.getX(), taxi.getY());

			canvas.drawBitmap(targetIndicatorBM, targetMatrix, canvasPaint);
		}
	}
	
	/**
	 * Draws with canvas matrix set to identity matrix (everything that is constantly on the screen like 
	 * credits, taxi, battery, etc)
	 * @param canvas canvas to draw on
	 */
	private void drawRelativeToCanvasOrigin(Canvas canvas) {

		canvas.setMatrix(null);
		
		// update taxi bitmap state
		taxiBM = BitmapBank.getTaxiBitmap(taxi.getBitmapState());
		
		// set taxi position here
		float taxiXPosition = canvas.getWidth()/2 - taxiBM.getWidth()/2;
		float taxiYPosition = canvas.getHeight()*0.7f - taxiBM.getHeight()/2;
		
		// set margin values based on view stats
		width_s_Margin		= (int) Math.ceil(this.view.getWidth() * 0.01);
		height_s_Margin 	= (int) Math.ceil(this.view.getHeight() * 0.01);
		height_l_margin		= (int) Math.ceil(this.view.getHeight() * 0.1);

		// draw battery (correct Bitmap is chosen depending on charge percentage)
		batteryBM = BitmapBank.getBatteryBitmap(taxi.getBatteryState());
		canvas.drawBitmap(batteryBM, width_s_Margin, height_s_Margin, bitmapPaint);

		// draw credits 
		canvas.drawText(credits_required, width_s_Margin, 1.7f*height_l_margin, textPaint);
		canvas.drawText(credits, width_s_Margin, (float) (2.4f*height_l_margin), textPaint);
		
		// DRAW DEBUG FOR LEVEL TESTING
		//canvas.drawText("x: " + Math.round(taxi.getX()) + ", y: " + Math.round(taxi.getY()), width_s_Margin, 4f*height_l_margin, textPaint);
		
		// draw galaxy/level title at start of level
		if (frame_count < 255) {
			levelPaint.setAlpha(alpha_level);
			alpha_level--;
			frame_count++;
			canvas.drawText("HELP MODE", canvas.getWidth()*0.6f, 2*height_l_margin, textPaint);
			canvas.drawText(level.toString(), canvas.getWidth()*0.1f, 5.5f*height_l_margin, levelPaint);
			canvas.drawBitmap(help_arrowBM, canvas.getWidth()*0.75f, 0.9f*height_l_margin, bitmapPaint);
		}

		// draw shields (small taxis with shield on)
		float scaleFactor = 0.5f;
		float startPos = view.getWidth()/2 - shieldsBM.getWidth()*scaleFactor/2;
		float offset = (height_s_Margin+shieldsBM.getWidth()*scaleFactor);
		Matrix shieldsMatrix = new Matrix();
		shieldsMatrix.postScale(scaleFactor, scaleFactor);
		shieldsMatrix.postTranslate(startPos - taxi.getShields()/2 * offset, height_s_Margin);
		for (int i = 0; i < taxi.getShields(); ++i) {
			canvas.drawBitmap(shieldsBM, shieldsMatrix, canvasPaint);
			shieldsMatrix.postTranslate(offset, 0);
		}
	
		// draw passengers currently onboard
		scaleFactor = 0.75f;
		startPos = width_s_Margin;
		offset = (passengerBM.getWidth() + passengerBM.getWidth()/6)*scaleFactor;
		Matrix passengerMatrix = new Matrix();
		passengerMatrix.postScale(scaleFactor, scaleFactor);
		passengerMatrix.postTranslate(startPos, 2.5f*height_l_margin);
		for (int i = 0; i < taxi.getPassengerCount(); ++i) {
			canvas.drawBitmap(passengerBM, passengerMatrix, bitmapPaint);
			passengerMatrix.postTranslate(offset, 0);
		}

		// draw taxi (taxi is always in canvas center looking in -y)
		canvas.drawBitmap(taxiBM, taxiXPosition, taxiYPosition, bitmapPaint);

		// draw overlay reflection on panel if sun is left or right to taxi
		// note: two bitmaps are used instead of mirroring one, since taxi panels are not exactly symmetrical
		// also only draw if taxi is not currently exploding
		if (taxi.getBitmapState() != -1 && taxi.isCharging()) {
			if (taxi.getChargingSide() == 1) {
				canvas.drawBitmap(panelChargeRightBM, canvas.getWidth()/2 - panelChargeRightBM.getWidth()/2, canvas.getHeight()*0.7f - panelChargeRightBM.getHeight()/2, bitmapPaint);
			} else if (taxi.getChargingSide() == 2) {
				canvas.drawBitmap(panelChargeLeftBM, canvas.getWidth()/2 - panelChargeLeftBM.getWidth()/2, canvas.getHeight()*0.7f - panelChargeLeftBM.getHeight()/2, bitmapPaint);
			}
		}

		// draw Buttons
		drawButtons(canvas);
		
		// draw HelpDialogue if in HelpMode
		if (view.inHelpMode()) {
			HelpDialogue.draw(canvas, this.view.getResources());
		}
	}

	/**
	 * Draw all Buttons on Canvas in correct State (pressed or not).
	 * @param canvas canvas to draw on 
	 */
	public void drawButtons(Canvas canvas) {

		canvas.setMatrix(null);

		// draw pause/unpause button
		if (view.isPaused()) {
			canvas.drawBitmap(btn_unpauseBM, view.getWidth()*0.99f - btn_unpauseBM.getWidth(), view.getHeight() * 0.02f, null);
		} else {
			canvas.drawBitmap(btn_pauseBM, view.getWidth()*0.99f - btn_pauseBM.getWidth(), view.getHeight() * 0.02f, null);
		}

		// draw background music toggle button
		if (view.isMusicOn()) {
			canvas.drawBitmap(btn_musicOnBM, view.getWidth()*0.92f - btn_musicOnBM.getWidth(), view.getHeight() * 0.02f, null);
		} else {
			canvas.drawBitmap(btn_musicOffBM, view.getWidth()*0.92f - btn_musicOffBM.getWidth(), view.getHeight() * 0.02f, null);
		}
		
		// draw helpMode button
		if (view.inHelpMode()) {
			helpPaint.setARGB(255, 255, 0, 0);
		} else {
			helpPaint.setARGB(255, 255, 255, 255);
		}
		canvas.drawText("?", (float) (view.getWidth()*0.85f - btn_musicOnBM.getWidth()*0.63f), view.getHeight() * 0.067f, helpPaint);
	}

	/**
	 * If the current level points to a next level, continue with that one,
	 * if there is no more level, the game ends victoriously. 
	 */
	private void nextLevel() {

		Level nextLevel = level.getNextLevel();
		
		if (nextLevel != null) {
			alpha_level = 255;
			frame_count = 0;
			level = nextLevel;
			lastLevelsScore += taxi.getCredits();
			passengerBM = BitmapBank.getPassengerBitmap(2);
			taxi = new Spaceship(level.getTaxiStartPosX(), level.getTaxiStartPosY(), level.getTaxiStartOrientation());

		} else {
			// Game Cleared
			startEndOfGame(3);
		}
	}

	/**
	 * Apply thrust to taxi, each thruster can be set independently to 
	 * control translation/rotation.
	 * @param thrust of left thruster
	 * @param thrust of right thruster
	 */
	public void taxiApplyThrust(float thrustLeft, float thrustRight) {
		if (!taxi.isOutOfBattery()) {
			taxi.applyThrust(thrustLeft, thrustRight);	
		}
	}

	/**
	 * this matrix transforms canvas center and orientation to Taxi
	 * such that taxi appears at screen center and looks in -y<br>
	 * make sure to apply to canvas identity matrix only
	 * @param canvas needed to center taxi center at canvas/screen center (not origin)
	 * @return the Taxi view transformation Matrix
	 */
	private Matrix getViewMatrix(Canvas canvas) {

		//transform world coordinates to taxi view space (taxi appears at screen center, looking in -y)
		//matrix could be set in fewer steps, done like this for clarity
		Matrix viewMatrix = new Matrix();

		viewMatrix.set(null);
		viewMatrix.postTranslate(-taxi.getX(), -taxi.getY()); //translate such that taxi center is at 0 (canvas origin)
		viewMatrix.postRotate((float)((-taxi.getOrientation())/Math.PI*180)); //rotate such that taxi looks at canvas 0 orientation
		//viewMatrix.postScale(0.2f, 0.2f); //could be scaled for minimap or similar
		viewMatrix.postRotate((float)((-Math.PI/2)/Math.PI*180)); //rotate such that taxi looks at -pi/2 (= up, -y)
		viewMatrix.postTranslate(canvas.getWidth()/2, canvas.getHeight()*0.7f); //translate such that taxi is at screen center

		return viewMatrix;
	}
	
	/**
	 * Start the next Activity, pass Credits on and the Cause of Termination
	 * @param endState <br>
	 * 				0 ... crashed (Planet) 	<br>
	 * 				1 ... crashed (Sun)		<br>
	 * 				2 ... out of battery	<br>
	 * 				3 ... cleared Game		
	 */
	private void startEndOfGame(int endState) {
		Activity gameActivity = (Activity)view.getContext();
		Intent intent = new Intent(gameActivity, EndOfGameActivity.class);

		// make value of credits available for next Activity
		intent.putExtra("CREDITS", lastLevelsScore + taxi.getCredits());
		intent.putExtra("ENDSTATE", endState);
		gameActivity.startActivity(intent);

		// make sure we cant get here again using the back key
		gameActivity.finish();
	}
	
	/**
	 * Initialize all Paints, created method for better clarity
	 */
	private void initPaints() {
		
		// Paint for drawing a black background
		canvasPaint = new Paint();
		canvasPaint.setColor(Color.argb(255, 0, 0, 0));

		// Paint for drawing bitmaps
		bitmapPaint = new Paint();
		bitmapPaint.setStrokeWidth(10);
		bitmapPaint.setFilterBitmap(true); //Bitmap Antialiasing
		bitmapPaint.setAntiAlias(true);

		// Paint for drawing stars in the background
		starPaint = new Paint();
		starPaint.setAntiAlias(true);
		starPaint.setARGB(255, 255, 255, 255);

		// Paint for text
		textPaint = new Paint();
		textPaint.setARGB(255, 100, 255, 255);
		textPaint.setTextSize(view.getResources().getDimension(R.dimen.statusFontSize));
		textPaint.setFakeBoldText(true);
		textPaint.setTextAlign(Align.LEFT);
		
		// Paint for help button
		helpPaint = new Paint();
		helpPaint.setARGB(255, 255, 255, 255);
		helpPaint.setTextSize(view.getResources().getDimension(R.dimen.help_button_font_size));
		helpPaint.setTextAlign(Align.CENTER);
		helpPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
		helpPaint.setFakeBoldText(true);
		helpPaint.setAntiAlias(true);
		
		// Paint for level name display
		levelPaint = new Paint();
		levelPaint.setColor(view.getResources().getColor(R.color.main_theme));
		levelPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
		levelPaint.setTextSize(view.getResources().getDimension(R.dimen.s_Planet));
		levelPaint.setFakeBoldText(true);
		levelPaint.setAntiAlias(true);
	}
	
	/**
	 * Initialize all constant bitmaps such as buttons, shields, passengers
	 */
	private void initConstantBMs() {
		
		// Game relevant Bitmaps
		taxiBM 				= BitmapBank.getTaxiBitmap(00);
		passengerBM 		= BitmapBank.getPassengerBitmap(1);
		targetIndicatorBM 	= BitmapBank.getTargetIndicatorBitmap();
		panelChargeLeftBM 	= BitmapBank.getPanelChargeLeftBitmap();
		panelChargeRightBM 	= BitmapBank.getPanelChargeRightBitmap();

		// Bitmaps of buttons for music and pause
		btn_pauseBM 	= BitmapBank.getPauseBtnBitmap(0);
		btn_unpauseBM 	= BitmapBank.getPauseBtnBitmap(1);
		btn_musicOnBM 	= BitmapBank.getMusicBtnBitmap(0);
		btn_musicOffBM 	= BitmapBank.getMusicBtnBitmap(1);
		
		// Bitmap for shields
		shieldsBM = BitmapBank.getTaxiBitmap(1100);
		
		// Bitmap for help_arrow
		help_arrowBM = BitmapBank.getHelpArrow();
	}

}