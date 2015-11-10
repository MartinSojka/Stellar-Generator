package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(of={"orbit", "rotationPeriod", "siderealPeriod", "dayLength", "hillsRadius", "surfaceGravity"}, callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public abstract class Satellite extends StellarObject {

	@NonNull public final StellarObject parent;
	@NonNull public final Orbit orbit;
	/** In minutes. Negative numbers mean the parent planet goes up in the west. */
	public final float rotationPeriod;
	/** Rotation time around the parent. In minutes, calculated field */
	public final float siderealPeriod;
	/** Day length in minutes, rounded to be useful in game terms. For comparison, Moon = 42524. Can be negative. */
	public final double dayLength;
	/** Hill's sphere radius of this satellite in m. Will be almost too small for moons. */
	public final double hillsRadius;
	/** Planetary exclusion zone (important for major planets) in mAU. */
	public final double exclusionZone;
	/** in m/s^2 */
	public final double surfaceGravity;
	/** Escape velocity at the equator, in m/s. Doesn't take into account the centrifugal force. */
	public final double escapeVelocity;
	/** Speed of the surface at equator, in m/s. Important for delta-V calculations */
	public final double equatorialSpeed;
	/** In kg/m^3 */
	public final double density;
	/** In kg/m^3, rough estimate (only really valid for terrestial planets) */
	public final double uncompressedDensity;
	/** Mass at which this planet or moon would start to accrete gas as well as dust */
	public final double criticalMass;
	/** In Kelvin, assuming blackbody planet (albedo = 0). Multiply by Math.pow(1 - albedo, 0.25) (Earth: 0.91) to get the "real" value */
	public final double effectiveTemperature;
	/** Smallest molecular weight still retained by the satellite */
	public final double molecularLimit;
	
	public final PlanetaryClass planetaryClass;

	protected Satellite(String name, double mass, double diameter, StellarObject parent, Orbit orbit, float rotationPeriod)
	{
		super(name, mass, diameter, Math.round(parent.seed + 79L * orbit.radius));
		
		this.parent = parent;
		this.orbit = orbit;
		this.rotationPeriod = rotationPeriod;
		
		// Calculate other values
		this.siderealPeriod = (float)(Constant.TWO_PI_SQRT_INV_G * Math.sqrt(Math.pow(orbit.radius, 3) / parent.mass));
		// Day length depends on if our parent is a satellite or not
		if( parent instanceof Satellite )
		{
			// We're orbiting a planet (moon, space station, ...)
			this.dayLength = (rotationPeriod / (1.0 - rotationPeriod / ((Satellite)parent).siderealPeriod));
		}
		else
		{
			// We're a planet or planetoid, orbiting a sun
			this.dayLength = (rotationPeriod / (1.0 - rotationPeriod / siderealPeriod));
		}
		// Hills radius of a circular orbit
		double hillsRadiusCircle = orbit.radius * Math.pow(mass / 3.0 / parent.mass, 1.0 / 3.0);
		this.hillsRadius = ((1.0 - orbit.eccentricity) * hillsRadiusCircle);
		// Estimated
		this.exclusionZone = hillsRadiusCircle * 5;
		
		this.surfaceGravity = Constant.G * 4 * mass / diameter / diameter * Math.pow(Constant.DISTANCE_UNIT, 3.0) / Constant.TIME_UNIT / Constant.TIME_UNIT;
		this.escapeVelocity = Math.sqrt(4.0 * this.surfaceGravity * this.diameter);
		this.equatorialSpeed = Math.PI * this.diameter / Math.abs(rotationPeriod * Constant.TIME_UNIT);
		this.density = mass * Constant.MASS_UNIT / (Math.PI * diameter * diameter * diameter / 6.0);
		// Estimate uncompressed density
		this.uncompressedDensity = this.density * 1.488560167 * Math.pow(this.mass, -0.06880048664);

		// Determine the main star and what its luminosity is, and derived values
		Satellite mainBody = this;
		while( mainBody.parent instanceof Satellite )
		{
			mainBody = (Satellite)mainBody.parent;
		}
		if( mainBody.parent instanceof Star )
		{
			Star mainStar = (Star)mainBody.parent;
			double starLuminosity = mainStar.luminosity;
	
			// Calculate the (blackbody, albedo = 0) temperature of the planet
			this.effectiveTemperature = Math.max(Math.pow(mainStar.luminosity / (16 * Math.PI * Constant.STEFAN_BOLTZMANN * mainBody.orbit.radius * mainBody.orbit.radius), 0.25), Constant.UNIVERSE_TEMPERATURE);
			this.criticalMass = 3.95e16 * Math.pow(mainBody.orbit.pericenter * Math.sqrt(starLuminosity), -0.75);
		}
		else
		{
			// We're some kind of a deep-space object?
			this.effectiveTemperature = Constant.UNIVERSE_TEMPERATURE;
			this.criticalMass = 0.0;
		}
		
		this.molecularLimit = 3.0 * Constant.MOLAR_GAS * this.effectiveTemperature / Math.pow(this.escapeVelocity / 6.0, 2.0);
		
		this.planetaryClass = PlanetaryClass.classify(this);
	}
	
	/**
	 * Check the habitable parameters and return true if people could live on it without specific technology
	 * @return
	 */
	public boolean habitable()
	{
		return(
				// The planet needs to retain water vapour (18.02), but not helium (4.00)
				molecularLimit < 18.02 && molecularLimit > 4.00
				// Effective temperature should be between 220 and 330 K
				&& effectiveTemperature >= 220 && effectiveTemperature <= 330
				// Surface gravitation should be between 0.5 and 1.5g
				&& surfaceGravity >= Constant.EARTH_SURFACE_GRAVITY * 0.5 && surfaceGravity <= Constant.EARTH_SURFACE_GRAVITY * 1.5 );
	}
}