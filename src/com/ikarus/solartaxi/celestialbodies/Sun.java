package com.ikarus.solartaxi.celestialbodies;

/**
 * Represents a Sun object with a center, a radius, a gravitation value 
 * and a value for emmitted energy.
 * 
 * @author Sebastian Kirchner
 */
public class Sun extends CelestialBody {
	
	/**
	 * Constructor of Sun object, the surfaceColor is interpolated using the radius, for 
	 * more information see method interpolate() in parent class CelestialBody.
	 * @param x x-coordinate of the center
	 * @param y y-coordinate of the center
	 * @param radius radius of this sun object
	 */
	public Sun(float x, float y, float radius) {
		super(x, y, radius);
		this.surfaceColor.setARGB(255, 255, interpolateRGB(200, 255), 0);
	}
	
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Sun;
	}
	
}