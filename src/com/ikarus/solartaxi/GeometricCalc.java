package com.ikarus.solartaxi;

/**
 * Helper Class for geometrical Calculations
 * @author Nikolaus Leopold
 *
 */
public class GeometricCalc {

	/**
	 * @return euclidean distance between two points
	 */
	public static float dist(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	/**
	 * @return angle of a point p on a circle with center c in range [0, 2pi[
	 */
	public static float angle(double px, double py, double cx, double cy) {
		double angle = Math.atan2(py - cy, px - cx);
		if (angle < 0)
			angle += 2*Math.PI;
		return (float) angle;
	} 
}
