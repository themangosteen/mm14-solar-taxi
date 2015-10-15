package com.ikarus.solartaxi;

/**
 * Class representing a chargeable battery. 
 * 
 * @author Sebastian Kirchner
 *
 */
public class TaxiBattery {
	
	// maximum Capacity and current Charge 
	// currCharge can never be greater than maxCapacity
	private double maxCapacity;
	private double currCharge;
	
	// how fast the battery is drained/recharged
	private final static double DRAINSPEED = 0.998;
	private final static double GAINSPEED = 1.001;
	
	/**
	 * Constructor setting maxCapacity and currCapacity to 100. 
	 * For different maxCapacity use method 'setNewMax(float new_max)'
	 */
	public TaxiBattery() {
		this.maxCapacity = 100;
		this.currCharge = this.maxCapacity;
	}
	
	/**
	 * @return double value representing the current percentage of charging (0-1)
	 */
	public double getLoadPercentage() {
		return this.currCharge/this.maxCapacity;
	}
	
	/**
	 * Drain the battery, checks that current charge can't go under 0
	 */
	public void drain() {
		this.currCharge -= (this.maxCapacity*(1-DRAINSPEED));
		this.currCharge = this.currCharge < 0.0 ? 0.0 : this.currCharge;
	}
	
	/**
	 * Drain the battery off a certain percentage
	 * 
	 * @param percentage of charge that should be drained 
	 * 			(e.g. 0.2 = 20% => - 20%)
	 */
	public void drain(double percentage) {
		this.currCharge -= (this.maxCapacity*percentage);
		this.currCharge = this.currCharge < 0.0 ? 0.0 : this.currCharge;
	}
	
	/**
	 * Recharges the battery, checks that current capacity can't go over maxCapacity
	 */
	public void recharge() {
		this.currCharge += (this.maxCapacity*(GAINSPEED-1));
		this.currCharge = this.currCharge < this.maxCapacity ? this.currCharge : this.maxCapacity;
	}
	
	/**
	 * Recharges the battery, checks that current capacity can't go over maxCapacity
	 */
	public void recharge(double percentage) {
		this.currCharge += (this.maxCapacity*percentage);
		this.currCharge = this.currCharge < this.maxCapacity ? this.currCharge : this.maxCapacity;
	}
	
	/**
	 * Resets the maximum capacity of the battery and also sets the current charge to 
	 * maximum capacity.
	 * 
	 * @param new_max value that will be the new maximum capacity
	 */
	public void setNewMax(double new_max) {
		this.maxCapacity = new_max;
		this.currCharge = this.maxCapacity;
	}

}
