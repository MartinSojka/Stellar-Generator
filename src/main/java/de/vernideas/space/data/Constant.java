package de.vernideas.space.data;

public final class Constant {
	// Units used inside the game in respect to SI units
	/** Interstellar distance: 1 m */
	public static final double DISTANCE_UNIT = 1.0;
	/** Time tick: 1 s */
	public static final double TIME_UNIT = 1.0;
	/** Planetary mass unit: 1 kg */
	public static final double MASS_UNIT = 1.0;
	/** Interplanetary speed unit; mAU/minute. This is a very large speed, about 125 units = light speed */
	public static final double SPEED_UNIT = DISTANCE_UNIT / TIME_UNIT;
	/** Interplanetary acceleration unit: mAU/minute². Very large unit and not usable for display */
	public static final double ACCELERATION_UNIT = SPEED_UNIT / TIME_UNIT;
	/** Minutes per mAU; usable for display of interplanetary travel information */
	public static final double PACE_UNIT = TIME_UNIT / DISTANCE_UNIT;
	
	/**
	 * Gravitational constant (in m^3 kg^-1 s^-2)
	 */
	public static final double G = 6.673848e-11;
	/**
	 * For calculating sidereal periods: 2 * PI * sqrt(1/G) = about 5.32269e-5
	 */
	public static final double TWO_PI_SQRT_INV_G = 2 * Math.PI * Math.sqrt(1.0 / G);
	
	/**
	 * Stefan-Boltzmann constant, for calculating luminosity out of radius and effective temperature (in W m^-2 K^-4)
	 */
	public static final double STEFAN_BOLTZMANN = 5.67036713e-8;
	
	/**
	 * Escape velocity multiplier - sqrt(2 * G) in m^1.5 kg^-0.5 s^-1
	 */
	public static final double ESCAPE_MULTIPLIER = Math.sqrt(2.0 * G);
	
	/** Solar radius (in distance units) */
	public static final double SOLAR_DIAMETER = 1.391e9 / DISTANCE_UNIT;
	/** Solar luminosity (in W) */
	public static final double SOLAR_LUM = 3.846e26;
	/** Solar mass (in mass units) */
	public static final double SOLAR_MASS = 1.98855e30 / MASS_UNIT;
	public static final double SOLAR_TEMPERATURE = 5778.0;
	
	/** One milli-lightyear in distance units */
	public static final double MILLI_LIGHTYEAR = 9.4607304725808e15 / DISTANCE_UNIT;
	public static final double AU = 149597870700.0 / DISTANCE_UNIT;
	
	/** One galactic day (25 hours of 60 minutes each) in s */
	public static final int GALACTIC_DAY = 25 * 3600;
	/** One galactic month (30 days of 25 hours of 60 minutes each) in s */
	public static final int GALACTIC_MONTH = 30 * GALACTIC_DAY;
	/** One galactic year (360 days of 25 hours of 60 minutes each) in s */
	public static final int GALACTIC_YEAR = 12 * GALACTIC_MONTH;
	
	/** Average temperature of the universe, in Kelvin */
	public static final double UNIVERSE_TEMPERATURE = 3.0;
	
	/** Earth surface gravity in m/s^2 */
	public static final double EARTH_SURFACE_GRAVITY = 9.807;
	
	// Tidal locking constants: 6x10^10 years times 3x10^10 kg m^-1 s^-2 for rocky,
	// times 4x10^9 N m^2 for icy worlds, and result in galactic years.
	public static final double TIDAL_LOCKING_ROCKY = 6e10 * 3e10 / GALACTIC_YEAR;
	public static final double TIDAL_LOCKING_ICY = 6e10 * 4e9 / GALACTIC_YEAR;
	
	/** 8.3144621 m^2 kg s^-2 K^-1 mol^-1 with the base time unit */
	public static final double MOLAR_GAS = 8.3144621 * TIME_UNIT * TIME_UNIT;
	
	/** Rough upper limit of terrestrial planets, in Yg */
	public static final double MAX_TERRESTRIAL_MASS = 5e25;
	public static final double MIN_TERRESTRIAL_MASS = 1e23;
	public static final double MIN_MOON_MASS = 1e19;
	public static final double EARTH_MASS = 5.97219e24;
	
	public static final double YOTTAGRAM = 1e21 / MASS_UNIT;
}
