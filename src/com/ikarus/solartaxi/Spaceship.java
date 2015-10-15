package com.ikarus.solartaxi;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.ikarus.solartaxi.celestialbodies.CelestialBody;
import com.ikarus.solartaxi.celestialbodies.Planet;
import com.ikarus.solartaxi.celestialbodies.Sun;

/**
 * Spaceship class for player controlled Taxi. 
 * 
 * @author Nikolaus Leopold<br>
 * Battery charging related functionality by Sebastian Kirchner
 * 
 */
public class Spaceship {

	// VARIABLES TO CONTROL MOVEMENT BEHAVIOUR
	private static final float  THRUST_TRANSLATION_FACTOR  	= 0.4f;
	private static final float  THRUST_ROTATION_FACTOR     	= 0.015f;
	private static final float  SPEED_DAMPING              	= 0.99f;
	private static final double ANGULAR_SPEED_DAMPING      	= 0.95;
	private static final int 	DISTANCE_COEFF_GRAVITY		= 3;
	private static final float  MAX_LANDING_SPEED          	= 5.0f;
	private static final double MAX_LANDING_TILT           	= Math.PI/8;
	private static final float  SPACESHIP_COLLISION_RADIUS 	= 42; //should be determined from bitmap
	private static float  		GRAVITY_FACTOR             	= 120.0f;

	// current bitmap to draw (see updateBitmapState() for encoding)
	private int bitmapState; 

	// spaceship center in world coordinates
	private float x, y; 

	// coordinates in previous frame (used to calculate actual speed)
	private float lastX, lastY; 

	// speed in look direction
	private float speed; 

	private double orientation, angularSpeed;

	// closest Planet or Sun
	private CelestialBody closestSurface;

	// distance to the closest Planet/Sun
	private float distToClosestSurface;

	// Battery of the taxi
	private TaxiBattery battery;

	// boolean values for the various states of the taxi
	private boolean landed, crashed, shieldOn, outOfBatt, charging, ikarused;

	private int chargingSide, shields, credits, maxPassengerCount;

	// List of Passengers currently on board
	private LinkedList<Passenger> passengers = new LinkedList<Passenger>();

	/**
	 * Constructor
	 * @param x x-coordinate of spaceship center
	 * @param y y-coordinate of spaceship center
	 * @param orientation direction in which taxi is looking, range [0,2pi[
	 */
	public Spaceship(float x, float y, double orientation) {
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
		this.speed = 0;
		this.orientation = orientation;
		this.angularSpeed = 0;

		this.bitmapState = 00;

		this.closestSurface = null;
		this.distToClosestSurface = Float.POSITIVE_INFINITY;
		this.landed = false;
		this.crashed = false;
		this.outOfBatt = false;
		this.charging = false;
		this.ikarused = false;

		this.chargingSide = 0;
		this.shields = 3;
		this.battery = new TaxiBattery();

		this.maxPassengerCount = 3;
		this.credits = 0;
	}

	/**
	 * @return x-coordinate of spaceship center
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return y-coordinate of spaceship center
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return direction in which taxi is looking, range [0,2pi[
	 */
	public double getOrientation() {
		return orientation;
	}

	/**
	 * @return speed value used for translation in look direction
	 */
	public float getLookSpeed() {
		return speed;
	}

	/**
	 * @return angular speed value used for orientation change
	 */
	public double getAngularSpeed() {
		return angularSpeed;
	}

	/**
	 * design note: we need this since in order to have responsive controls
	 * we don't have realistic momentum but always put our speed in look direction
	 * and thus can't put the effects of gravitation into that speed
	 * since then it would be possible to just rotate the momentum of gravity
	 * by looking somewhere else. so gravity translates independently.
	 * this implies that the speed value is not the actual displacement rate
	 * since it doesn't include the gravtiational effect. 
	 * thus we calculate it from actual displacement per frame
	 * @return the actual speed determined as displacement per frame
	 */
	public float getSpeed() {
		return GeometricCalc.dist(x, y, lastX, lastY);
	}

	/**
	 * @return current bitmap to draw
	 * <br>00 no thrust
	 * <br>01 right thrust
	 * <br>10 left thrust
	 * <br>11 both thrusters
	 * <br>100 legs out
	 * <br>-1 crashed
	 * <br>1100 shield and legs
	 */
	public int getBitmapState() {
		return bitmapState;
	}

	/**
	 * @return credits currently available
	 */
	public int getCredits() {
		return credits;
	}

	/**
	 * @return number of shields left
	 */
	public int getShields() {
		return shields;
	}

	/**
	 * @return number of passengers currently in taxi
	 */
	public int getPassengerCount() {
		return passengers.size();
	}

	/**
	 * @return the current battery charge in percentage
	 */
	public double getBatteryState() {
		return battery.getLoadPercentage();
	}

	/**
	 * @return list of target planets passengers intend to travel to
	 */
	public LinkedList<Planet> getTargetPlanets() {
		LinkedList<Planet> targetPlanets = new LinkedList<Planet>();
		for (Passenger p : passengers) {
			targetPlanets.add(p.getTargetPlanet());
		}

		return targetPlanets;
	}

	/**
	 * @return distance of Spaceship boundary to closest {@link com.ikarus.solartaxi.celestialbodies.CelestialBody} surface
	 */
	public float getDistToClosest() {
		return distToClosestSurface;
	}

	/**
	 * @return whether we have landed safely
	 */
	public boolean isLanded() {
		return landed;
	}

	/**
	 * @return whether we have crashed
	 */
	public boolean isCrashed() {
		return crashed;
	}

	/**
	 * @return whether angle allows for safe landing
	 */
	public boolean isAngleOk() {
		if (closestSurface == null)
			return false;
		return Math.abs(GeometricCalc.angle(x, y, closestSurface.getX(), closestSurface.getY()) - orientation) <= MAX_LANDING_TILT;
	}

	/**
	 * @return whether or not we still have battery left
	 */
	public boolean isOutOfBattery() {
		return outOfBatt;
	}

	/**
	 * @return true if taxi is currently charging battery
	 */
	public boolean isCharging() {
		return charging;
	}

	/**
	 * GameOver by crash with sun.
	 * @return true if crashed with sun
	 */
	public boolean isIkarused() {
		return ikarused;
	}

	/**
	 * @return <br>0 ... not charging
	 * <br>1 ... right side charging
	 * <br>2 ... left side charging
	 */
	public int getChargingSide() {
		return chargingSide;
	}

	/**
	 * Apply linear translation to taxi position.
	 * @param deltaX translation in x dimension
	 * @param deltaY translation in y dimension
	 */
	private void translate(float deltaX, float deltaY) {
		x += deltaX;
		y += deltaY;
	}

	/**
	 * Change orientation.<br>
	 * Orientation automatically stays in range [0,2pi[
	 * @param delta rotation angle in radians
	 */
	private void rotate(double delta) {
		orientation += delta;

		if (orientation >= 2*Math.PI) {
			orientation %= 2*Math.PI;
		}
		else if (orientation < 0) {
			orientation += 2*Math.PI;
		}
	}

	/**
	 * Change speed in orientation direction.
	 * @param delta speed increment/decrement
	 */
	private void accelerate(float delta) {
		speed += delta;
	}

	/**
	 * Change angular speed.
	 * @param delta angular speed increment/decrement
	 */
	private void accelerateRotation(double delta) {
		angularSpeed += delta;		
	}

	/**
	 * Apply thrust to Spaceship, right and left thrusters can be set independently to
	 * control translation/rotation. 
	 * <br>Sets BitmapState for moving Spaceship.
	 * @param thrust of left thruster
	 * @param thrust of right thruster
	 */
	public void applyThrust(float thrustLeft, float thrustRight) {

		if (landed) {
			// extra thrust to overcome gravitation
			thrustLeft += 0.5f;
			thrustRight += 0.5f;

			// extra battery drain for extra thrust
			battery.drain(0.02); 
		}

		// right thruster on
		if (thrustLeft == 0) {
			this.bitmapState = 01;
		} 
		// left thruster on
		else if (thrustRight == 0) {
			this.bitmapState = 10;
		} 
		// both thrusters on
		else {
			this.bitmapState = 11;
		}

		float thrustTranslation = (thrustLeft + thrustRight)*THRUST_TRANSLATION_FACTOR;
		float thrustRotation = (thrustLeft - thrustRight)*THRUST_ROTATION_FACTOR;

		// translate and rotate taxi
		accelerate(thrustTranslation);
		accelerateRotation(thrustRotation);

		// drain battery when taxi uses thrusters
		battery.drain();
	}

	/**
	 * Update the Spaceship, handles battery recharge, crash situation etc.
	 * @param celestialBodies collection of all {@link com.ikarus.solartaxi.celestialbodies.CelestialBody} in the level
	 */
	public void update(Collection<CelestialBody> celestialBodies) {

		if (crashed) {
			return;
		}

		if (battery.getLoadPercentage() < 0.05) {
			// handle empty battery situation
			outOfBattery();
			if (outOfBatt) {
				return;
			}
		}

		if (landed) {
			// enable to get away from planet before gravity sets back in
			if (distToClosestSurface > SPACESHIP_COLLISION_RADIUS/2)
				landed = false;
		}

		if (speed >= 0) {
			shieldOn = false;
		}

		lastX = x;
		lastY = y;

		distToClosestSurface = Float.POSITIVE_INFINITY;

		// setting this value here has the outcome, that you can always only be charged
		// by using one sun, necessary so charging panels are shown correctly
		charging = false;

		closestSurface = null;
		// go through all CelestialBodies, find the closest
		for (CelestialBody c : celestialBodies) {
			float distance = GeometricCalc.dist(c.getX(), c.getY(), x, y);

			// ignore if out of gravity and charge range
			if (distance > c.getRadius()*DISTANCE_COEFF_GRAVITY) {
				continue;
			}

			float distToSurface = distance - c.getRadius() - SPACESHIP_COLLISION_RADIUS;

			if (distToSurface < distToClosestSurface) {
				distToClosestSurface = distToSurface;
				closestSurface = c;
			}

			// BATTERY CHARGING
			if (c instanceof Sun && !charging) {
				batteryCharge((Sun) c, distToSurface); 
			}
			applyGravity(c, distance);
		}

		// only if we found a closestSurface
		if (closestSurface != null) {
			checkCollision(closestSurface);
		}

		//update spaceship world coordinates
		//design note: a change in orienation rotates the whole momentum without having to 
		//accelerate again in the new direction. this is unrealistic for space, but more responsive
		//and essential for playability
		translate((float)Math.cos(orientation) * speed, (float)Math.sin(orientation) * speed);

		if (distToClosestSurface > SPACESHIP_COLLISION_RADIUS/4) {
			rotate(angularSpeed);
		}

		//simulate vacuum friction :)
		speed *= SPEED_DAMPING;
		angularSpeed *= ANGULAR_SPEED_DAMPING;

		// update BitmapState to draw correct Bitmap of Taxi
		updateBitmapState();
	}

	/**
	 * Handles the case that the battery is out of energy, if there is a shield left, 
	 * energy will be taken from the shield to recharge the battery, frameCount is added 
	 * for a bit of lag inbetween. 
	 */
	private void outOfBattery() {
		outOfBatt = true;

		if (getShields() > 0) {

			--shields;
			battery.recharge(0.3);
			shieldOn = true;
			outOfBatt = false;
		} 
		// no more shields, no more battery ==> GameOver
		else {
			outOfBatt = true;
			return;
		}

	}

	/**
	 * Called in update(), checks whether angle and distance are right for Taxi to be recharging
	 * its battery. Can always only be charged from one sun.
	 * @param s the Sun that is to be checked
	 * @param dist distance to the Sun s
	 */
	private void batteryCharge(Sun s, float dist) {
		// in correct distance to the sun
		if (dist < s.getRadius()*2 && !isOutOfBattery()) {
			// calculate angle to the sun
			double deviationCos = Math.cos(Math.abs(GeometricCalc.angle(x, y, s.getX(), s.getY()) - (orientation-Math.PI/2)));

			// if Taxi is in ~correct angle to sun and not crashed, landed nor shieldon, recharge
			if (Math.abs(deviationCos) > 0.8 && !(crashed)) {
				battery.recharge();
				charging = true;

				// set BitMap so correct ChargingPanel is drawn
				if (deviationCos > 0) {
					chargingSide = 1;	
				} else {
					chargingSide = 2;
				}
			} else {
				charging = false;
			} 
		}
	}

	/**
	 * Applies gravity of a @CelestialBody to Taxi if it is close enough.<br>
	 * Design note: this uses a different acceleration model than the spaceship.
	 * Since we want responsive controls the spaceship momentum is totally rotated
	 * when the orientation changes, such that we dont fly in the same direction
	 * but always in look direction, without having to accelerate after rotating.
	 * however it should not be able to influence the momentum built by gravity by a
	 * simple change of orientation. an approach using shared momentum for both didnt work
	 * so now for gravitation we dont use the accelerate function but translate independently
	 * @param c celestial body for which to apply gravity
	 * @param distance the distance to the celestial body center
	 */
	private void applyGravity(CelestialBody c, float distance) {

		if (!landed && distance < c.getRadius()*DISTANCE_COEFF_GRAVITY) {

			double normalOrientation = GeometricCalc.angle(x, y, c.getX(), c.getY());
			float grav = c.getGravity()*GRAVITY_FACTOR / (distance - c.getRadius());
			translate((float)Math.cos(normalOrientation)*-grav, (float)Math.sin(normalOrientation)*-grav);
		}
	}

	/**
	 * Check for Collision with a CelestialBody. Depending on type (Planet or Sun),
	 * speed and orientation of Taxi, this can be a landing or a crash. 
	 * @param c CelestialBody to check, should only be checked for the closest
	 */
	private void checkCollision(CelestialBody c) {

		float distance = GeometricCalc.dist(c.getX(), c.getY(), x, y);
		double normalOrientation = GeometricCalc.angle(x, y, c.getX(), c.getY());

		if (distance < c.getRadius() + SPACESHIP_COLLISION_RADIUS) {

			// CelestialBody is a sun, immediate crash and GameOver
			if (c instanceof Sun) {
				SoundPlayer.playSound(R.raw.taxi_explosion, 0, 0.5f);
				// set right SplashScreen
				ikarused = true;
				return;
			}

			// Conditions for safe Landing are met
			if (getSpeed() <= MAX_LANDING_SPEED && Math.abs(normalOrientation - orientation) <= MAX_LANDING_TILT) {
				landed = true;
				speed = 0;
				//translate outwards a bit so taxi doesn't get stuck
				translate((float)Math.cos(normalOrientation), (float)Math.sin(normalOrientation));
				orientation = normalOrientation; // align to planet surface normal

				// Exchange Passengers if possible
				exchangePassengers((Planet)c);
			} 
			// Taxi crashed
			else {
				// Still some Shields left
				if (shields > 0) {
					SoundPlayer.playSound(R.raw.taxi_shield, 0, 1.75f);
					--shields;
					speed = -1;
					translate((float)Math.cos(normalOrientation)*SPACESHIP_COLLISION_RADIUS, (float)Math.sin(normalOrientation)*SPACESHIP_COLLISION_RADIUS);
					orientation = normalOrientation; // align to planet surface normal
					battery.drain(0.15); // shield drains more energy
					shieldOn = true;
				} 
				// no more shields, GameOver 
				else {
					SoundPlayer.playSound(R.raw.taxi_explosion, 0, 0.5f);
					crashed = true;
					return;
				}
			}
		}
	}

	/**
	 * Checks whether world boundaries have been surpassed and takes an according action.
	 * In this case we warp around (one end to the other end).
	 * @param xMin world min x
	 * @param yMin world min y
	 * @param xMax world max x
	 * @param yMax world max y
	 */
	public void checkWorldBoundaryCollision(float xMin, float yMin, float xMax, float yMax) {

		// over the left boundary
		if (x < xMin) {
			x = xMax;
		} 
		// over the right boundary
		else if (x > xMax) {
			x = xMin;
		}

		// over the upper boundary
		if (y < yMin) {
			y = yMax;
		} 
		// over the lower boundary
		else if (y > yMax) {
			y = yMin;
		}
	}

	/**
	 * Pick up as many passengers on a planet as possible and drop off 
	 * all passengers for whom this planet is the destination.
	 * @param p planet landed on to exchange passengers
	 */
	private void exchangePassengers(Planet planet) {

		Iterator<Passenger> iter = passengers.iterator();
		Passenger p;
		boolean playedSoundCredits = false;
		boolean playedSoundGreeting = false;

		// check if planet is target planet and remove those passengers
		while (iter.hasNext()) {
			p = iter.next();

			if (planet == p.getTargetPlanet()) {
				iter.remove();
				credits += p.getCredits();

				if (!playedSoundCredits)
					SoundPlayer.playSound(R.raw.credits_earned, 0, 1.75f);
				playedSoundCredits = true;
			}
		}

		// take up as many new passengers as possible
		while (passengers.size() < maxPassengerCount) {
			try {
				boolean passengerPickedUp = passengers.add(planet.pickUpPassenger());
				if (passengerPickedUp && !playedSoundGreeting) {
					//playedSoundGreeting = true;
				}
			} catch (NoSuchElementException e) {
				break;
			}
		}
	}

	/**
	 * Update current BitmapState, thruster states are set in applyThrust for accuracy.<br>
	 * Encoding: 
	 * 		<br>1100 ... legs & shield 
	 * 		<br>-1 ... exploded 	
	 * 		<br>100 ... legs extended
	 * 		<br>00 ... no thrusters
	 */
	private void updateBitmapState() {

		if (shieldOn) {
			// legs extended & shield activated
			bitmapState = 1100; 
		} 
		else if (crashed) {
			// crashed
			bitmapState = -1; 
		} 
		else if (landed) { 
			// legs extended
			bitmapState = 100; 
		} 
		else if (getSpeed() < 0.99) {
			// no thrust
			bitmapState = 00; 
		}
	}

	
	public void setGravityFactor(float g) {
		GRAVITY_FACTOR = g;
	}

}