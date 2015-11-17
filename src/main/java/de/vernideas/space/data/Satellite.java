package de.vernideas.space.data;

import de.vernideas.space.data.planetaryclass.PlanetaryClass;
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
	public final double blackbodyTemperature;
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
		this.escapeVelocity = Math.sqrt(this.surfaceGravity * this.diameter);
		this.equatorialSpeed = Math.PI * this.diameter / Math.abs(rotationPeriod * Constant.TIME_UNIT);
		this.density = mass * Constant.MASS_UNIT / (Math.PI * diameter * diameter * diameter / 6.0);
		 
		// Determine the main star and what its luminosity is, and derived values
		Satellite mainBody = this;
		while( mainBody.parent instanceof Satellite )
		{
			mainBody = (Satellite)mainBody.parent;
		}
		if( mainBody.parent instanceof Star )
		{
			Star mainStar = (Star)mainBody.parent;

			// Calculate the (blackbody, albedo = 0) temperature of the planet
			this.blackbodyTemperature = Math.max(Math.pow(mainStar.luminosity / (16 * Constant.STEFAN_BOLTZMANN_PI * mainBody.orbit.radius * mainBody.orbit.radius), 0.25), Constant.UNIVERSE_TEMPERATURE);
			this.criticalMass = 1.2e-5 * Constant.SOLAR_MASS * Math.pow(mainBody.orbit.pericenter / Constant.AU * Math.sqrt(mainStar.originalLuminosity / Constant.SOLAR_LUM), -0.75);
		}
		else
		{
			// We're some kind of a deep-space object?
			this.blackbodyTemperature = Constant.UNIVERSE_TEMPERATURE;
			this.criticalMass = 0.0;
		}
		this.uncompressedDensity = estimateUncompressedDensity(this.mass / Constant.EARTH_MASS, Math.sqrt(this.density));

		this.molecularLimit = 1000 * 3.0 * Constant.MOLAR_GAS * this.blackbodyTemperature / Math.pow(this.escapeVelocity / 9.15, 2.0);
		
		this.planetaryClass = PlanetaryClass.classify(this);
	}
	
	private double[] densParam = {
		1.075223888e-2, 1.033854725e-3, -2.440327804e-1,
		-2.385275401e-4, 1.007597897e-1, -6.690967381,
		7.934910194e-1, -7.675943516e-1, 100.1634514, -354.1767288
	};
	
	/**
	 * Estimate uncompressed density
	 * 
	 * Polynomial regression from following data (maxx, density, temp, uncompressed density)
	 * <pre>
	 * 0.0553 5427 448 5400
	 * 0.815 5243 327.8 3970
	 * 1 5503 278.7 4030
	 * 0.0123 3340 278.7 3300 # Moon
	 * 0.107 3933 226.1 3710
	 * 317.83 1326 122.2 300
	 * 95.159 687 90 300
	 * 14.536 1271 63.6 500
	 * 17.147 1638 50.8 500
	 * 0.0022 1869 44.4 1800 # Pluto
	 * 0.00016 2160 167.5 2100 # Ceres
	 * </pre>
	 */
	private double estimateUncompressedDensity(double mass, double density) {
		return Math.pow(-1.522468415 * mass * mass + 6.550424608e-1 * mass * density - 4.301092033e-4 * density * density
				- 58.14803175 * mass + 1.087590418 * density - 3.748494515
				, 2.0);
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
				&& blackbodyTemperature >= 220 && blackbodyTemperature <= 330
				// Surface gravitation should be between 0.5 and 1.5g
				&& surfaceGravity >= Constant.EARTH_SURFACE_GRAVITY * 0.5 && surfaceGravity <= Constant.EARTH_SURFACE_GRAVITY * 1.5 );
	}
	
	/**
	 * Assumes a uniform distribution (not really true for most planets, but ok for planetoids).
	 * <p>
	 * For Earth-like planets, it underestimates the pressure by about a factor of 2
	 * <p>
	 * Formula: πGρ^2d^2/6, ρ = density, d = diameter
	 * 
	 * @return in Pa
	 */
	public double corePressure() {
		return Math.PI * Constant.G * density * density * diameter * diameter / 6.0;
	}
	
	/**
	 * Assumes a distribution between innerD at the center and outerD on the surface
	 */
	public double corePressure(double innerD, double outerD) {
		double r = diameter / 2;
		double slope = (outerD - innerD) / r;
		double intercept = outerD - r * slope;
		
		// innerIntegral would be 0 anyway
		double outerIntegral = Math.PI / 36.0 * Constant.G * r * r *
				(9.0 * slope * slope * r * r + 28.0 * slope * intercept * r + 24.0 * intercept * intercept);
		return outerIntegral /* - innerIntegral */;
	}

	/**
	 * Given a min radius, max radius, inner density and outer density, calculate the total mass
	 * of the spheric shell.
	 * <p>
	 * Integral of 4 * pi * r^2 * (a * r + b) for r, with a = slope and b = intercept of the linear
	 * interpolation between inner and outer density and r the radius.
	 * 
	 * @param minRadius in m
	 * @param maxRadius in m
	 * @param innerD in kg/m^3
	 * @param outerD in kg/m^3
	 * @return in kg
	 */
	public static double shellMass(double minRadius, double maxRadius, double innerD, double outerD) {
		double slope = (outerD - innerD) / (maxRadius - minRadius);
		double intercept = outerD - maxRadius * slope;
		
		double outerIntegral = Math.PI / 3.0 * Math.pow(maxRadius, 3.0) * (3.0 * slope * maxRadius + 4.0 * intercept);
		double innerIntegral = Math.PI / 3.0 * Math.pow(minRadius, 3.0) * (3.0 * slope * minRadius + 4.0 * intercept);
		
		return outerIntegral - innerIntegral;
	}
	
	/**
	 * Given a min radius, max radius, inner pressure, outer pressure (typically 0) and material,
	 * calculate the total mass of the spheric shell.
	 * <p>
	 * Integral of 4 * pi * r^2 * u * e^(c * ((o - i) / (b - a) * r + o - (o - i) / (b - a) * b)) for r between a and b
	 * <p>
	 * Simplified to : V * r^2 * e^(C * r) with<br>
	 * C = c * (o - i) / (b - a)<br>
	 * V = 4 * pi * u * e^(c * (o - (o - i) * b / (b - a))) 
	 *   = 4 * pi * u * e^(c * o - C * b)
	 * 
	 * @param minRadius in m
	 * @param maxRadius in m
	 * @param innerP in N/m^2
	 * @param outerP in N/m^2
	 * @param mat Material instance
	 * @return
	 */
	public static double shellMass(double minRadius, double maxRadius, double innerP, double outerP, Material mat) {
		// value type: m^-1
		double c = mat.compressibility * (outerP - innerP) / (maxRadius - minRadius);
		// value type: kg/m^-3
		double v = 4 * Math.PI * mat.uncompressedDensity * Math.exp(mat.compressibility * outerP - c * maxRadius);
		
		double outerVal = (maxRadius * c * (maxRadius * c - 2) + 2) * Math.exp(maxRadius * c);
		double innerVal = (minRadius * c * (minRadius * c - 2) + 2) * Math.exp(minRadius * c);
		return v * (outerVal - innerVal) / c / c / c;
	}
}