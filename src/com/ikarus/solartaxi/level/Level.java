package com.ikarus.solartaxi.level;

import java.util.ArrayList;
import java.util.Collection;

import com.ikarus.solartaxi.celestialbodies.CelestialBody;
import com.ikarus.solartaxi.celestialbodies.Planet;

/**
 * Storage class for references
 * defining a level/world in the game
 * NOTE: for now, levels should be defined in subclass constructors (hardcoded)
 * by setting all protected class fields accordingly
 * this is just a quick solution, if a lot of levels were to be included, 
 * it might be nicer to use a custom XML definition and parser
 * 
 * @author Nikolaus Leopold, Sebastian Kirchner
 */
public abstract class Level {

	protected ArrayList<CelestialBody> celestialBodies = new ArrayList<CelestialBody>();
	protected float[] stars;

	// adjust the world boundary such that no planets or suns are visible 
	// when spaceship reaches the edge, in order to have less noticable warping
	protected float worldMinX, worldMinY, worldMaxX, worldMaxY;

	// taxi start position must be within world boundary
	protected float taxiStartPosX, taxiStartPosY;

	// taxi orientation in range [0,2pi[
	protected float taxiStartOrientation;

	// the amount of credits required to finish this level
	protected int creditsRequired;

	// level to be played after this one
	// if null, the game will end (victory)
	protected Level nextLevel;
	
	// name of the level, displayed at the beginning
	protected String levelName;

	/**
	 * CONSTRUCTOR
	 * NOTE: levels should be defined in subclass constructors (hardcoded)
	 * by modifying the protected class fields
	 */
	public Level() {
		
		// taxi start position must be within world boundary
		taxiStartPosX = 0;
		taxiStartPosY = 0;

	}
	
	protected void setStars() {
		// stars creation
		ArrayList<Float> tempList = new ArrayList<Float>();  

		float x = worldMinX-200, y;

		while (x < worldMaxX + 200) {
			y = worldMinY-200;
			while (y < worldMaxY + 200) {
				tempList.add(x);
				tempList.add(y);

				// use these to make bigger stars, uses more computational power 				
//				tempList.add(x+1);
//				tempList.add(y);
//				
//				tempList.add(x-1);
//				tempList.add(y);
//				
//				tempList.add(x);
//				tempList.add(y-1);
//				
//				tempList.add(x);
//				tempList.add(y+1);

				y += Math.random()*210 + 40;
			}
			x += Math.random()*170 + 20;
		}

		Object[] tempArr = tempList.toArray();
		stars = new float[tempArr.length];

		for (int i=0;i<stars.length;i++) {
			stars[i] = (Float) tempArr[i];
		}
	}

	/**
	 * @return shallow copy of list of all celestial bodies (planets & suns)
	 * this is ok since we only load once at level start
	 */
	public Collection<CelestialBody> getCelestialBodies() {
		return celestialBodies;
	}

	/**
	 * @return world boundary min x
	 */
	public float getMinX() {
		return worldMinX;
	}

	/**
	 * @return world boundary min y
	 */
	public float getMinY() {
		return worldMinY;
	}

	/**
	 * @return world boundary max x
	 */
	public float getMaxX() {
		return worldMaxX;
	}

	/**
	 * @return world boundary max y
	 */
	public float getMaxY() {
		return worldMaxY;
	}

	/**
	 * @return taxi starting position x value
	 */
	public float getTaxiStartPosX() {
		return taxiStartPosX;
	}

	/**
	 * @return taxi starting position y value
	 */
	public float getTaxiStartPosY() {
		return taxiStartPosY;
	}

	/**
	 * @return taxi starting orientation in range [0,2pi[
	 */
	public float getTaxiStartOrientation() {
		return taxiStartOrientation;
	}

	/**
	 * @return the level to be played after this one. if null, the game will end (victory)
		nextLevel = new Level2(view);
	 */
	public Level getNextLevel() {
		return nextLevel;
	}

	/**
	 * @return required amount of credits to finish this level
	 */
	public int getRequiredCredits() {
		return creditsRequired;
	}

	/**
	 * @return a float array listing x,y coordinates used for drawing stars
	 */
	public float[] getStars() {
		return stars;
	}
	
	protected Planet getRandomPlanet(Planet[] planets) {
		int ind = (int) (Math.random()*(planets.length-1));
		return planets[ind];
	}
	
	@Override
	public String toString() {
		return this.levelName;
	}

}