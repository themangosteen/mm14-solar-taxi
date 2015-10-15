package com.ikarus.solartaxi.level;

import android.view.View;

import com.ikarus.solartaxi.R;
import com.ikarus.solartaxi.celestialbodies.Planet;
import com.ikarus.solartaxi.celestialbodies.Sun;

/**
 * Storage class for references
 * defining a level/world in the game
 * 
 * @author Sebastian Kirchner
 */
public final class Level1 extends Level {

	/**
	 * CONSTRUCTOR
	 * this is just a storage class
	 * for now, the whole level is defined here, hardcoded!
	 * storage variables have protected access in superclass
	 */
	public Level1(View view) {

		// name of this level, displayed at the beginning
		levelName = "GALAXY I";

		// adjust these such that no planets or suns are visible 
		// as spaceship reaches the edge to have nicer warping
		worldMinX = -view.getResources().getDimension(R.dimen.level_1_boundary);
		worldMinY = -view.getResources().getDimension(R.dimen.level_1_boundary);
		worldMaxX = view.getResources().getDimension(R.dimen.level_1_boundary);
		worldMaxY = view.getResources().getDimension(R.dimen.level_1_boundary);

		// setStars => has to be set after world boundaries are set
		setStars();

		// could be used for goal based on max credit in level
		int creditSum = 0;
	
		// planet radi and distance in dp, uniform dist equals ~1000p on Samsung Galaxy S3 mini
		float s_radi = view.getResources().getDimension(R.dimen.s_Planet),
				m_radi = view.getResources().getDimension(R.dimen.m_Planet),
				l_radi = view.getResources().getDimension(R.dimen.l_Planet),
				s_dist = view.getResources().getDimension(R.dimen.uniform_dist);

		// the level to be played after this one, if null, the game will end (victory)
		nextLevel = new Level2(view);

		// taxi orientation must be in range [0,2pi[
		taxiStartOrientation = 3*(float)Math.PI/2;
		
		celestialBodies.add(new Sun(-s_dist*4.6f,  -s_dist*0.75f, 	m_radi));
		celestialBodies.add(new Sun(-s_dist*3.3f,  -s_dist*3.6f, 	l_radi));
		celestialBodies.add(new Sun(-s_dist*3.0f, 	s_dist*1.9f, 	l_radi*0.75f));
		celestialBodies.add(new Sun(-s_dist*1.2f,  -s_dist*1.2f, 	m_radi*0.9f));
		celestialBodies.add(new Sun(-s_dist, 		s_dist*2.4f, 	m_radi*1.1f));
		celestialBodies.add(new Sun(s_dist, 	   -s_dist*3.0f, 	m_radi*1.3f));
		celestialBodies.add(new Sun(s_dist*2.4f,	s_dist*1.7f, 	m_radi*1.7f));

		Planet planet0 = new Planet(0, 				l_radi*0.75f, 	l_radi*0.5f);
		Planet planet1 = new Planet(-s_dist*4.5f,	s_dist*2.0f, 	s_radi*1.2f);
		Planet planet2 = new Planet(-s_dist*4.2f,  -s_dist*3.4f,	m_radi*1.1f);
		Planet planet3 = new Planet(-s_dist*3.5f, 	s_dist*1.4f, 	m_radi*0.9f);
		Planet planet4 = new Planet(-s_dist*2.8f,  -s_dist*2.7f, 	m_radi*1.2f);
		Planet planet5 = new Planet(-s_dist*27.f,  -s_dist*0.8f, 	m_radi*1.1f);
		Planet planet6 = new Planet(-s_dist*3.0f+l_radi+s_radi, s_dist*1.6f, s_radi*1.2f);
		Planet planet7 = new Planet(-s_dist*1.7f,  -s_dist*3.4f, 	l_radi*0.75f);
		Planet planet8 = new Planet(-s_dist*1.4f, 	s_dist*3.1f, 	l_radi*0.6f);
		Planet planet9 = new Planet(-s_dist*0.7f,  -s_dist*0.8f, 	s_radi*1.1f);
		Planet planet10= new Planet(s_dist*0.55f,	s_dist*1.8f, 	m_radi*1.2f);
		Planet planet11= new Planet(s_dist*1.3f, 	s_dist*0.3f, 	s_radi*1.3f);
		Planet planet12= new Planet(s_dist*1.4f,   -s_dist*1.2f, 	m_radi*1.5f);
		Planet planet13= new Planet(s_dist*1.2f, 	s_dist*3.75f, 	l_radi*0.9f);
		Planet planet14= new Planet(s_dist*3.5f,	s_dist*3.0f, 	l_radi);

		Planet[] planets = new Planet[] {planet0, planet1, planet2, planet3, planet4,
				planet5, planet6, planet7, planet8, planet9, planet10, planet11, planet12,
				planet13, planet14};

		// randomize passengers on planets 
		for (Planet p : planets) {
			int passengers = p.getRadius() >= l_radi ? (int) (Math.random()*4 + 2) : 
				(p.getRadius() >= m_radi ? (int) (Math.random()*3+1) :
					(p.getRadius() >= s_radi ? (int) (Math.random()*2+1) :(int) (Math.random()*2)));

			// first planet set manually
			if (p.equals((Object) planet0)) {
				continue;
			}
			
			// randomize target planets of passengers
			for (int i=0; i<passengers; i++) {
				p.addWaitingPassenger(getRandomPlanet(planets));
			}
			creditSum += p.getPassengerCredits();
			celestialBodies.add(p);
		}

		planet0.addWaitingPassenger(planet2);
		planet0.addWaitingPassenger(planet14);
		planet0.addWaitingPassenger(planet1);

		celestialBodies.add(planet0);
		creditSum += planet0.getPassengerCredits();
		
		// the amount of credits required to finish this level
		creditsRequired =  50000; //(int) (creditSum/100*50/1000) * 1000;

	}

}