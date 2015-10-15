package com.ikarus.solartaxi.level;

import android.view.View;

import com.ikarus.solartaxi.R;
import com.ikarus.solartaxi.celestialbodies.Planet;
import com.ikarus.solartaxi.celestialbodies.Sun;

/**
 * Storage class for references
 * defining a level/world in the game
 * 
 * @author Nikolaus Leopold
 */
public final class Level2 extends Level {

	/**
	 * CONSTRUCTOR
	 * this is just a storage class
	 * for now, the whole level is defined here, hardcoded!
	 * storage variables have protected access in superclass
	 */
	public Level2(View view) {

		// name of this level, displayed at the beginning
		levelName = "GALAXY II";
		
		// adjust these such that no planets or suns are visible 
		// as spaceship reaches the edge to have nicer warping
		worldMinX = -8000.0f;
		worldMinY = -8000.0f;
		worldMaxX = 8000.0f;
		worldMaxY = 8000.0f;		
		
		// setStars => has to be set after world boundaries are set
		setStars();
		
		// the amount of credits required to finish this level
		creditsRequired = 20000;

		// the level to be played after this one, if null, the game will end (victory)
		nextLevel = null;

		// taxi orientation must be in range [0,2pi[
		taxiStartOrientation = (float) (Math.PI / 3.0f);
		
		celestialBodies.add(new Planet(500, -200, view.getResources().getDimension(R.dimen.m_Planet)));
		celestialBodies.add(new Planet(0, -700, view.getResources().getDimension(R.dimen.s_Planet)));
		celestialBodies.add(new Planet(-1000, 300, view.getResources().getDimension(R.dimen.l_Planet)));
		celestialBodies.add(new Sun(0, -1900, view.getResources().getDimension(R.dimen.l_Planet)));
		celestialBodies.add(new Sun(-300, 800, view.getResources().getDimension(R.dimen.m_Planet)));
		celestialBodies.add(new Sun(-500, -300, view.getResources().getDimension(R.dimen.s_Planet)));
		celestialBodies.add(new Sun(-500, -300, view.getResources().getDimension(R.dimen.s_Planet)));
		celestialBodies.add(new Sun(6000, 4800, view.getResources().getDimension(R.dimen.l_Planet)));
		celestialBodies.add(new Sun(3000, -4000, view.getResources().getDimension(R.dimen.s_Planet)));

		Planet planet0 = new Planet(0, 400, view.getResources().getDimension(R.dimen.m_Planet));
		Planet planet1 = new Planet(1000, -400, view.getResources().getDimension(R.dimen.s_Planet));
		Planet planet2 = new Planet(2200, -800, view.getResources().getDimension(R.dimen.s_Planet));
		Planet planet3 = new Planet(-3000, -4000, view.getResources().getDimension(R.dimen.l_Planet));
		Planet planet4 = new Planet(7000, 5000, view.getResources().getDimension(R.dimen.m_Planet));
		Planet planet5 = new Planet(5000, -6000, view.getResources().getDimension(R.dimen.l_Planet));
		planet0.addWaitingPassenger(planet1);
		planet0.addWaitingPassenger(planet2);
		planet0.addWaitingPassenger(planet1);
		planet1.addWaitingPassenger(planet1);
		planet1.addWaitingPassenger(planet2);
		planet1.addWaitingPassenger(planet4);
		planet2.addWaitingPassenger(planet3);
		planet2.addWaitingPassenger(planet0);
		planet3.addWaitingPassenger(planet4);
		planet3.addWaitingPassenger(planet5);
		planet3.addWaitingPassenger(planet0);
		planet4.addWaitingPassenger(planet3);
		planet4.addWaitingPassenger(planet2);
		planet4.addWaitingPassenger(planet1);
		planet5.addWaitingPassenger(planet3);
		planet5.addWaitingPassenger(planet1);
		celestialBodies.add(planet0);
		celestialBodies.add(planet1);
		celestialBodies.add(planet2);
		celestialBodies.add(planet3);
		celestialBodies.add(planet4);
		celestialBodies.add(planet5);
	}

}