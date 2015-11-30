package de.vernideas.space.data;

public final class Constant {
	// Units used inside the game in respect to SI units
	/** Interstellar distance: 1 m */
	public static final double DISTANCE_UNIT = 1.0;
	/** Time tick: 1 s */
	public static final double TIME_UNIT = 1.0;
	/** Planetary mass unit: 1 kg */
	public static final double MASS_UNIT = 1.0;
	/** In m/s */
	public static final double SPEED_UNIT = DISTANCE_UNIT / TIME_UNIT;
	/** In m/s^2 */
	public static final double ACCELERATION_UNIT = SPEED_UNIT / TIME_UNIT;
	/** In s/m */
	public static final double PACE_UNIT = TIME_UNIT / DISTANCE_UNIT;
	/** In N = kg * m/s^2 */
	public static final double FORCE_UNIT = ACCELERATION_UNIT * MASS_UNIT;
	/** In J = N * m = kg * m^2 / s^2 */
	public static final double ENERGY_UNIT = FORCE_UNIT * DISTANCE_UNIT;
	/** In W = J / s = kg * m^2 / s^3 */
	public static final double LUM_UNIT = ENERGY_UNIT / TIME_UNIT;
	/** in kg/m^3 */
	public static final double DENSITY_UNIT = MASS_UNIT / DISTANCE_UNIT / DISTANCE_UNIT / DISTANCE_UNIT;
	/** In Pa = N / m^2 = kg / (m * s^2) */
	public static final double PRESSURE_UNIT = FORCE_UNIT / DISTANCE_UNIT / DISTANCE_UNIT;
	
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
	 * Stefan-Boltzmann constant times PI, for blackbody temperature calculations
	 */
	public static final double STEFAN_BOLTZMANN_PI = STEFAN_BOLTZMANN * Math.PI;
	
	/**
	 * Escape velocity multiplier - sqrt(2 * G) in m^1.5 kg^-0.5 s^-1
	 */
	public static final double ESCAPE_MULTIPLIER = Math.sqrt(2.0 * G);
	
	/** Solar radius (in distance units) */
	public static final double SOLAR_DIAMETER = 1.391e9 / DISTANCE_UNIT;
	/** Solar luminosity (in W) */
	public static final double SOLAR_LUM = 3.846e26 / LUM_UNIT;
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
	public static final double UNIVERSE_TEMPERATURE = 2.73;
	public static final double UNIVERSE_AGE = 1.3799e9 * 31557600.0 / TIME_UNIT; // Julian years for astronomy values
	
	/** Earth surface gravity (acceleration) in m/s^2 */
	public static final double EARTH_SURFACE_GRAVITY = 9.807 / ACCELERATION_UNIT;
	/** Earth diameter, calculated from gravity (9.807 m/s^2) and mass */
	public static final double EARTH_DIAMETER = 6375098 * 2 / DISTANCE_UNIT; 
	/** Earth's standard atmospheric pressure */
	public static final double EARTH_ATM_PRESSURE = 101325.0 / PRESSURE_UNIT;
	
	// Tidal locking constants: 6x10^10 years times 3x10^10 kg m^-1 s^-2 for rocky,
	// times 4x10^9 N m^2 for icy worlds, and result in galactic years.
	public static final double TIDAL_LOCKING_ROCKY = 6e10 * 3e10 / GALACTIC_YEAR;
	public static final double TIDAL_LOCKING_ICY = 6e10 * 4e9 / GALACTIC_YEAR;
	
	/** 8.3144621 m^2 kg s^-2 K^-1 mol^-1  */
	public static final double MOLAR_GAS = 8.314459848 / ENERGY_UNIT;
	
	/** Rough upper limit of terrestrial planets, in Yg */
	public static final double MAX_TERRESTRIAL_MASS = 5e25 / MASS_UNIT;
	public static final double MIN_TERRESTRIAL_MASS = 1e23 / MASS_UNIT;
	public static final double MIN_MOON_MASS = 1e19 / MASS_UNIT;
	public static final double EARTH_MASS = 5.97219e24 / MASS_UNIT;
	
	public static final double YOTTAGRAM = 1e21 / MASS_UNIT;
	
	// Earth - estimated density limits for core, mantle and crust.
	// Used with linear interpolation
	public static final double EARTH_CORE_MAX_DENSITY = 13300.0 / DENSITY_UNIT;
	public static final double EARTH_CORE_MIN_DENSITY = 10000.0 / DENSITY_UNIT;
	public static final double EARTH_MANTLE_MAX_DENSITY = 5226.0 / DENSITY_UNIT;
	public static final double EARTH_MANTLE_MIN_DENSITY = 4234.0 / DENSITY_UNIT;
	public static final double EARTH_CRUST_MAX_DENSITY = 3538.0 / DENSITY_UNIT;
	public static final double EARTH_CRUST_MIN_DENSITY = 2300.0 / DENSITY_UNIT;
	// Earth - estimated radius of core and mantle (crust = planet radius) in m
	public static final double EARTH_CORE_RADIUS = 3400000.0 / DISTANCE_UNIT;
	public static final double EARTH_MANTLE_RADIUS = 6335000.0 / DISTANCE_UNIT;
	
	/** For calculation of the Stern-Levison parameter */
	public static final double STERN_LEVISON_CONSTANT = 1.53e5 * Math.pow(AU, 1.5) / EARTH_MASS / EARTH_MASS / Math.sqrt(SOLAR_MASS);
	
}
