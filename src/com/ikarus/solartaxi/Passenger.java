package com.ikarus.solartaxi;

import java.util.Random;

import android.graphics.Matrix;

import com.ikarus.solartaxi.celestialbodies.Planet;

/**
 * Passengers wait on a planet for the taxi to take them to their target planet.
 * @author Nikolaus Leopold
 */
public class Passenger {

	private Planet sourcePlanet;
	private double rotation; // angle in [0, 2pi[
	
	private Planet targetPlanet;
	private int credits; // credits the passenger pays for the trip
	
	/**
	 * Instances should be constructed by Planet objects
	 * @param sourcePlanet planet of origin ('spawn planet')
	 * @param targetPlanet passenger destination planet
	 */
	public Passenger(Planet sourcePlanet, Planet targetPlanet) {
		
		this.sourcePlanet = sourcePlanet;
		this.targetPlanet = targetPlanet;
		
		Random rand = new Random();
		rotation = rand.nextDouble() * 2*Math.PI;
		
		// credits paid for trip depend on target planet distance
		credits = (int)Math.sqrt(Math.pow(targetPlanet.getX() - sourcePlanet.getX(), 2) 
				               + Math.pow(targetPlanet.getY() - sourcePlanet.getY(), 2));
		
	}
	
	/**
	 * @return target planet this passenger intends to travel to
	 */
	public Planet getTargetPlanet() {
		return targetPlanet;
	}
	
	/**
	 * @return credits paid for taxi trip when target is reached
	 */
	public int getCredits() {
		return credits;
	}
	
	/**
	 * @return transformation matrix of passenger to world coordinates
	 */
	public Matrix getMatrix() {
		Matrix passengerMatrix = new Matrix();
		
		float dx = sourcePlanet.getX() + (float)Math.cos(rotation) * sourcePlanet.getRadius();
		float dy = sourcePlanet.getY() + (float)Math.sin(rotation) * sourcePlanet.getRadius();
		
		passengerMatrix.postRotate((float)(rotation*180/Math.PI + 90));
		passengerMatrix.postTranslate(dx, dy);
		
		return passengerMatrix;
	}
}