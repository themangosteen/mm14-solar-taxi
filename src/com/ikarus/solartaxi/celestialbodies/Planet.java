package com.ikarus.solartaxi.celestialbodies;

import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import android.graphics.Matrix;

import com.ikarus.solartaxi.Passenger;

/**
 * Represents a Planet object with a center, a radius and a gravitation.<br />
 * Planets also have passengers waiting on the surface that interact with the taxi
 * 
 * @author Sebastian Kirchner, Nikolaus Leopold
 */
public class Planet extends CelestialBody {
	
	private LinkedList<Passenger> waitingPassengers = new LinkedList<Passenger>();

	/**
	 * Constructor of Planet object, the surfaceColor is interpolated using the radius, for 
	 * more information see method interpolate() in parent class CelestialBody.
	 * @param x x-coordinate of the center
	 * @param y y-coordinate of the center
	 * @param radius radius of this planet object
	 * @param waitingPassengerCount number of passengers waiting for taxi
	 */
	public Planet(float x, float y, float radius) {
		super(x, y, radius);
		
		Random rand = new Random();
		this.surfaceColor.setARGB(255, rand.nextInt(100)+100, rand.nextInt(100)+100, rand.nextInt(100)+100);
	}
	
	/**
	 * @return list of waiting passenger transformation matrices
	 */
	public Collection<Matrix> getPassengerMatrices() {
		LinkedList<Matrix> matrices = new LinkedList<Matrix>();
		for (Passenger p : waitingPassengers) {
			matrices.add(p.getMatrix());
		}
		return matrices;
	}
	
	/**
	 * remove a passenger from planet to be picked up by taxi
	 * @return passenger to be picked up by taxi
	 */
	public Passenger pickUpPassenger() throws NoSuchElementException {
		return waitingPassengers.pop();
	}
	
	/**
	 * add a passenger on this planet waiting for taxi to target planet
	 * @param targetPlanet passenger intends to travel to (mustnt be this)
	 */
	public void addWaitingPassenger(Planet targetPlanet) {
		if (targetPlanet != null && targetPlanet != this) {
			waitingPassengers.add(new Passenger(this, targetPlanet));
		}
	}
	
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Planet;
	}
	
	public int getPassengerCredits() {
		int sum = 0;
		
		for (Passenger p : waitingPassengers) {
			sum += p.getCredits();
		}
		return sum;
	}

}