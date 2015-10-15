package com.ikarus.solartaxi.celestialbodies;

import android.graphics.Paint;

/**
 * This class represents a CelestialBody, either a 'Sun' or a 'Planet' object. Each 
 * object has a center point represented by x and y and a radius, as well as a Paint
 * Object for the surface color and a value for the gravitational pull of the object.
 * 
 * @author Sebastian Kirchner
 */
public abstract class CelestialBody {
	private final static float MIN_RADIUS = 80; // minimum Radius for objects
	private final static float MAX_RADIUS = 800;  // maximum Radius for objects
	
	private float x, y; // center coordinates
	private float radius;	
	
	protected float gravity; 
	protected Paint surfaceColor;
	
	/**
	 * Constructor 
	 * @param x x-coordinate of the center
	 * @param y y-coordinate of the center
	 * @param radius radius of this CelestialBody
	 */
	public CelestialBody(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		
		// if radius is larger than maxRadi it is set to maxRadi and if it is smaller than minRadi it 
		// is set to minRadi
		this.radius = radius > MAX_RADIUS ? MAX_RADIUS : radius < MIN_RADIUS ? MIN_RADIUS : radius;
		this.gravity = interpolate(1.0f, 0.5f);
		this.surfaceColor = new Paint();
		surfaceColor.setStrokeWidth(10);
		surfaceColor.setAntiAlias(true);
	}
	
	/**
	 * @return x-coordinate of the center of this CelestialBody
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * @return y-coordinate of the center of this CelestialBody
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * @return radius of this CelestialBody
	 */
	public float getRadius() {
		return radius;
	}
	
	/**
	 * @return the gravity pull value for this Object
	 */
	public float getGravity() {
		return this.gravity;
	}
	
	public Paint getSurfaceColor() {
		return this.surfaceColor;
	}
	/**
	 * Get interpolated value in given range according to the size of the radius.
	 * 
	 * @param rangeMax upper border of range
	 * @param rangeMin lower border of range
	 * @return interpolated value in range according to radius
	 */
	protected float interpolate(float rangeMin, float rangeMax) {
		return (this.getRadius()-MIN_RADIUS)/(MAX_RADIUS-MIN_RADIUS) * rangeMax + rangeMin;
	}
	
	/**
	 * Get interpolated RGB value in given range according to the size of the radius.
	 * Allowed values for maxRGB and minRGB are [0, 255], invalid values
	 * will be set to 0 or 255.
	 * 
	 * @param maxRGB the maxiumum RGB value (upper border)
	 * @param minRGB the minimum RGB value (lower border)
	 * @return a value between minRGB and maxRGB
	 */
	protected int interpolateRGB(float minRGB, float maxRGB) {
		maxRGB = maxRGB > 255 ? 255: maxRGB;
		minRGB = minRGB < 0 ? 0 : minRGB;
		
		return (int)interpolate(minRGB, maxRGB);
	}
	
	public boolean equals(Object o) {
		return o instanceof CelestialBody ? ((CelestialBody) o).getX() == this.getX() && 
				((CelestialBody) o).getY() == this.getY() && 
				((CelestialBody) o).getRadius() == this.getRadius() : false;
	}
}