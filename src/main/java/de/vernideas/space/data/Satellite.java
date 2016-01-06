package de.vernideas.space.data;

import java.util.Comparator;
import java.util.Random;

import de.vernideas.space.data.planetaryclass.PlanetaryClass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(of={"orbit", "rotationPeriod", "siderealPeriod", "dayLength", "hillsRadius", "surfaceGravity"}, callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public abstract class Satellite extends StellarObject {
	public static final Comparator<Satellite> ORBITAL_COMPARATOR = new Comparator<Satellite>() {
		@Override public int compare(Satellite o1, Satellite o2) {
			return (o1.orbit.radius < o2.orbit.radius ? -1 : o1.orbit.radius > o2.orbit.radius ? 1 : 0);
		}
	};
	public static final Comparator<Satellite> MASS_COMPARATOR = new Comparator<Satellite>() {
		@Override public int compare(Satellite o1, Satellite o2) {
			return (o1.mass < o2.mass ? -1 : o1.mass > o2.mass ? 1 : 0);
		}
	};
	public static final Comparator<Satellite> REVERSE_MASS_COMPARATOR = new Comparator<Satellite>() {
		@Override public int compare(Satellite o1, Satellite o2) {
			return (o1.mass < o2.mass ? 1 : o1.mass > o2.mass ? -1 : 0);
		}
	};


	/** Generate a random planetary mass */
	public static double newMass(Random random) {
		double rnd = random.nextDouble();
		double mass = 0.0001814813990910743 * Math.exp(25.647952850461436 * rnd) + 19765.338232060116 * rnd;
		return mass * Constant.YOTTAGRAM;
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
	
	@NonNull @Getter protected StellarObject parent;
	@NonNull @Getter protected Orbit orbit;
	/** In minutes. Negative numbers mean the parent planet goes up in the west. */
	@Getter protected double rotationPeriod;
	/** Rotation time around the parent. In minutes, calculated field */
	private double siderealPeriod;
	private boolean siderealPeriodCalculated = false;
	
	/** Day length in minutes, rounded to be useful in game terms. For comparison, Moon = 42524. Can be negative. */
	private double dayLength;
	private boolean dayLengthCalculated = false;
	
	/** Hill's sphere radius of this satellite in m. Will be almost too small for moons. */
	private double hillsRadius;
	private boolean hillsRadiusCalculated = false;
	
	/** Planetary exclusion zone (important for major planets) in mAU. */
	private double exclusionZone;
	private boolean exclusionZoneCalculated = false;
	
	/** in m/s^2 */
	private double surfaceGravity;
	private boolean surfaceGravityCalculated = false;
	
	/** Escape velocity at the equator, in m/s. Doesn't take into account the centrifugal force. */
	private double escapeVelocity;
	private boolean escapeVelocityCalculated = false;
	
	/** Speed of the surface at equator, in m/s. Important for delta-V calculations */
	private double equatorialSpeed;
	private boolean equatorialSpeedCalculated = false;
	
	/** In kg/m^3 */
	private double density;
	private boolean densityCalculated = false;
	
	/** Estimate of overall compressibility, in Pa^-1, normal range 1e-12 to 30e-12 for planets,
	 * 20e-12 to 200e-12 for planetoids, see Material class for more examples */
	@Getter protected double compressibility;
	/** In kg/m^3, rough estimate (only really valid for terrestial planets) */
	@Getter protected double uncompressedDensity;
	/** Mass at which this planet or moon would start to accrete gas as well as dust */
	
	private double criticalMass;
	private boolean criticalMassCalculated = false;
	
	/** In Kelvin, assuming blackbody planet (albedo = 0). Multiply by Math.pow(1 - albedo, 0.25) (Earth: 0.91) to get the "real" value */
	private double blackbodyTemperature;
	private boolean blackbodyTemperatureCalculated = false;
	
	/** Smallest molecular weight still retained by the satellite */
	private double molecularLimit;
	private boolean molecularLimitCalculated = false;
	
	private PlanetaryClass planetaryClass;
	private boolean planetaryClassValidated = false;
	
	/** Main satellite body of the system (can be this object if it's a planet) */
	private Satellite mainBody;
	private boolean mainBodyCalculated = false;

	/** Main star of the system (TODO: Multi-star systems) */
	private Star mainStar;
	private boolean mainStarCalculated = false;
	
	protected Satellite(String name)
	{
		super(name);
	}
	
	public double blackbodyTemperature() {
		if( !blackbodyTemperatureCalculated ) {
			Star star = mainStar();
			if( null != star ) {
				blackbodyTemperature = mainBody().orbit.blackbodyTemp(star);
			} else {
				blackbodyTemperature = Constant.UNIVERSE_TEMPERATURE;
			}
			blackbodyTemperatureCalculated = true;
		}
		return blackbodyTemperature;
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
		return Math.PI * Constant.G * density() * density() * diameter * diameter / 6.0;
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
	
	public double criticalMass() {
		if( !criticalMassCalculated ) {
			Star star = mainStar();
			if( null != star ) {
				criticalMass = 1.2e-5 * Constant.SOLAR_MASS * Math.pow(mainBody().orbit.pericenter / Constant.AU * Math.sqrt(star.originalLuminosity / Constant.SOLAR_LUM), -0.75);
			} else {
				criticalMass = 0.0;
			}
			criticalMassCalculated = true;
		}
		return criticalMass;
	};
	
	public double dayLength() {
		if( !dayLengthCalculated ) {
			if( mainBody().siderealPeriod() > 0.0 ) {
				dayLength = (rotationPeriod / (1.0 - rotationPeriod / mainBody().siderealPeriod()));
				dayLengthCalculated = true;
			} else {
				dayLength = 0.0;
			}
		}
		return dayLength;
	}
	
	public double density() {
		if( !densityCalculated ) {
			if( mass > 0.0 && diameter > 0.0 ) {
				density = mass * Constant.MASS_UNIT / (Math.PI * diameter * diameter * diameter / 6.0);
				densityCalculated = true;
			} else {
				density = 0.0;
			}
		}
		return density;
	}
	
	@Override public StellarObject diameter(double diameter) {
		super.diameter(diameter);
		invalidateSurfaceParameters();
		equatorialSpeedCalculated = false;
		return this;
	}
	
	public double equatorialSpeed() {
		if( !equatorialSpeedCalculated ) {
			if( rotationPeriod > 0.0 ) {
				equatorialSpeed = Math.PI * diameter / Math.abs(rotationPeriod * Constant.TIME_UNIT);
				equatorialSpeedCalculated = true;
			} else {
				equatorialSpeed = 0.0;
			}
		}
		return equatorialSpeed;
	}
	
	public double escapeVelocity() {
		if( !escapeVelocityCalculated ) {
			if( mass > 0.0 && diameter > 0.0 ) {
				escapeVelocity = Math.sqrt(surfaceGravity() * this.diameter);
				escapeVelocityCalculated = true;
			} else {
				escapeVelocity = 0.0;
			}
		}
		return escapeVelocity;
	}
	
	public double exclusionZone() {
		if( !exclusionZoneCalculated ) {
			if( null != orbit && null != parent ) {
				double hillsRadiusCircle = orbit.radius * Math.pow(mass / 3.0 / parent.mass, 1.0 / 3.0);
				exclusionZone = hillsRadiusCircle * 5;
				exclusionZoneCalculated = true;
			} else {
				exclusionZone = 0.0;
			}
		}
		return exclusionZone;
	}
	
	/**
	 * Check the habitable parameters and return true if people could live on it without specific technology
	 * @return
	 */
	public boolean habitable()
	{
		return(
				// The planet needs to retain water vapour (18.02), but not helium (4.00)
				molecularLimit() < 18.02 && molecularLimit() > 4.00
				// Effective temperature should be between 220 and 330 K
				&& blackbodyTemperature() >= 220 && blackbodyTemperature() <= 330
				// Surface gravitation should be between 0.5 and 1.5g
				&& surfaceGravity() >= Constant.EARTH_SURFACE_GRAVITY * 0.5 && surfaceGravity() <= Constant.EARTH_SURFACE_GRAVITY * 1.5 );
	}
	
	public double hillsRadius() {
		if( !hillsRadiusCalculated ) {
			if( null != orbit && null != parent ) {
				double hillsRadiusCircle = orbit.radius * Math.pow(mass / 3.0 / parent.mass, 1.0 / 3.0);
				hillsRadius = ((1.0 - orbit.eccentricity) * hillsRadiusCircle);
				hillsRadiusCalculated = true;
			} else {
				hillsRadius = 0.0;
			}
		}
		return hillsRadius;
	}
	
	// Depends on orbital parameters and own mass
	private void invalidateHillsRadius() {
		hillsRadiusCalculated = false;
		exclusionZoneCalculated = false;
	}
	
	private void invalidateOrbitalParameters() {
		blackbodyTemperatureCalculated = false;
		criticalMassCalculated = false;
	}
	
	// Depends on mass and diameter
	private void invalidateSurfaceParameters() {
		surfaceGravityCalculated = false;
		escapeVelocityCalculated = false;
		densityCalculated = false;
		molecularLimitCalculated = false;
	}
	
	public Satellite mainBody() {
		if( !mainBodyCalculated ) {
			mainBody = this;
			while( mainBody.parent instanceof Satellite ) {
				mainBody = (Satellite)mainBody.parent;
			}
			mainBodyCalculated = true;
		}
		return mainBody;
	}
	
	public Star mainStar() {
		if( !mainStarCalculated ) {
			if( mainBody().parent instanceof Star ) {
				mainStar = (Star)mainBody().parent;
			} else {
				mainStar = null;
			}
			mainStarCalculated = true;
		}
		return mainStar;
	}

	@Override public StellarObject mass(double mass) {
		super.mass(mass);
		invalidateHillsRadius();
		invalidateSurfaceParameters();
		return this;
	}

	public Satellite material(Material material) {
		this.compressibility = material.compressibility;
		this.uncompressedDensity = material.uncompressedDensity;
		return this;
	}
	
	public double molecularLimit() {
		if( !molecularLimitCalculated ) {
			if( mass > 0.0 && diameter > 0.0 ) {
				molecularLimit = 1000 * 3.0 * Constant.MOLAR_GAS * blackbodyTemperature() / Math.pow(escapeVelocity() / 9.15, 2.0);
				molecularLimitCalculated = true;
			} else {
				molecularLimit = 0.0;
			}
		}
		return molecularLimit;
	}
	
	public Satellite orbit(@NonNull Orbit orbit) {
		this.orbit = orbit;
		invalidateHillsRadius();
		invalidateOrbitalParameters();
		molecularLimitCalculated = false;
		siderealPeriodCalculated = false;
		dayLengthCalculated = false;
		return this;
	}
	
	public Satellite orbit(@NonNull StellarObject parent, @NonNull Orbit orbit) {
		this.parent = parent;
		this.orbit = orbit;
		invalidateHillsRadius();
		invalidateOrbitalParameters();
		molecularLimitCalculated = false;
		siderealPeriodCalculated = false;
		dayLengthCalculated = false;
		return this;
	}
	
	public Satellite parent(@NonNull StellarObject parent) {
		this.parent = parent;
		invalidateHillsRadius();
		invalidateOrbitalParameters();
		molecularLimitCalculated = false;
		siderealPeriodCalculated = false;
		dayLengthCalculated = false;
		return this;
	}
	
	public PlanetaryClass planetaryClass() {
		if( !planetaryClassValidated ) {
			planetaryClass = (null != planetaryClass && planetaryClass.validClass(this)
					? planetaryClass : PlanetaryClass.classify(this));
			planetaryClassValidated = true;
		}
		return planetaryClass;
	}
	
	public Satellite planetaryClass(PlanetaryClass planetaryClass) {
		this.planetaryClass = planetaryClass;
		this.planetaryClassValidated = false;
		return this;
	}

	public Satellite rotationPeriod(double rotationPeriod) {
		this.rotationPeriod = rotationPeriod;
		dayLengthCalculated = false;
		equatorialSpeedCalculated = false;
		return this;
	}
	
	public double siderealPeriod() {
		if( !siderealPeriodCalculated ) {
			if( null != orbit && null != parent ) {
				siderealPeriod = Constant.TWO_PI_SQRT_INV_G * Math.sqrt(Math.pow(orbit.radius, 3) / parent.mass);
				siderealPeriodCalculated = true;
			} else {
				siderealPeriod = 0.0;
			}
		}
		return siderealPeriod;
	}
	
	public double surfaceGravity() {
		if( !surfaceGravityCalculated ) {
			if( mass > 0.0 && diameter > 0.0 ) {
				surfaceGravity = Constant.G * 4 * mass / diameter / diameter * Math.pow(Constant.DISTANCE_UNIT, 3.0) / Constant.TIME_UNIT / Constant.TIME_UNIT;
				surfaceGravityCalculated = true;
			} else {
				surfaceGravity = 0.0;
			}
		}
		return surfaceGravity;
	}
}