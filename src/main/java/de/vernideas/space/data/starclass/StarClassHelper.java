package de.vernideas.space.data.starclass;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public final class StarClassHelper {
	/**
	 * Get a random temperature for the given star class
	 */
	public static double randomTemp(StarClass sc, Random rnd) {
		return sc.minTemp() + rnd.nextDouble() * (sc.maxTemp() - sc.minTemp());
	}

	/**
	 * Get the minimum temperature for the given star class
	 */
	public static double minTemp(StarClass sc)
	{
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return minTemp(String.format("D%d", sc.subType));
		default:
			return minTemp(String.format("%s%d", sc.type, sc.subType));
		}
	}

	/**
	 * Get the minimum temperature for the given (string) star class
	 */
	public static double minTemp(String sc)
	{
		return minTempTable.get(sc);
	}

	/**
	 * Get the maximum temperature for the given star class
	 */
	public static double maxTemp(StarClass sc)
	{
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return maxTemp(String.format("D%d", sc.subType));
		default:
			return maxTemp(String.format("%s%d", sc.type, sc.subType));
		}
	}

	/**
	 * Get the maximum temperature for the given (string) star class
	 */
	public static double maxTemp(String sc)
	{
		return maxTempTable.get(sc);
	}

	/**
	 * Get the minimum safe jump distance for the star class, if available
	 */
	public static double safeJumpDistance(StarClass sc)
	{
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return 0.0;
		default:
			return safeJumpDistance(String.format("%s%d", sc.type, sc.subType));
		}
	}

	/**
	 * Get the minimum safe jump distance for the given (string) star class, if available
	 */
	public static double safeJumpDistance(String sc)
	{
		Double result = safeJumpDistance.get(sc);
		return( null != result ? result * 1000 : 0.0 );
	}

	/**
	 * Get a random temperature for the given star class
	 */
	public static double randomMass(StarClass sc, Random rnd) {
		return sc.minMass() + rnd.nextDouble()*(sc.maxMass() - sc.minMass());
	}

	/**
	 * Get the minimum temperature for the given star class
	 */
	public static double minMass(StarClass sc)
	{
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return minMass(String.format("D%d", sc.subType));
		default:
			return minMass(String.format("%s%d", sc.type, sc.subType));
		}
	}

	/**
	 * Get the minimum temperature for the given (string) star class
	 */
	public static double minMass(String sc)
	{
		return minMassTable.get(sc);
	}

	/**
	 * Get the maximum temperature for the given star class
	 */
	public static double maxMass(StarClass sc)
	{
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return maxMass(String.format("D%d", sc.subType));
		default:
			return maxMass(String.format("%s%d", sc.type, sc.subType));
		}
	}

	/**
	 * Get the maximum temperature for the given (string) star class
	 */
	public static double maxMass(String sc)
	{
		return maxMassTable.get(sc);
	}

	public static double randomLuminosity(StarClass sc, Random rnd) {
		return sc.avgLuminosity() * (rnd.nextDouble() * 0.2 + 0.9);
	}


	public static double avgLuminosity(StarClass sc) {
		switch(sc.luminosityClass) {
		case WHITE_DWARF:
			return avgLuminosity(String.format("D%d", sc.subType));
		default:
			return avgLuminosity(String.format("%s%d", sc.type, sc.subType));
		}
	}

	public static double avgLuminosity(String sc)
	{
		return avgLuminosityTable.get(sc);
	}

	/**
	 * Returns the approximate B-V color index of the star
	 * from its effective temperature (in Kelvin)
	 * using Ballesteros' formula.
	 * <p>
	 * Reference: http://arxiv.org/pdf/1201.1809.pdf
	 * <p>
	 * This works to within few percent of the "true" value for everything
	 * from sub-dwarfs (IV) to giants (III). Other stars have different
	 * offsets; in particular white dwarfs are "bluer" (lower B-V color index)
	 * than what's indicated by their effective temperature.
	 */
	public static double temperatureToBV(double temp) {
		if( temp <= 0 ) {
			throw new IllegalArgumentException("Negative or zero temperature supplied");
		}
		return 0.0217391 * (Math.sqrt(729.0 * temp * temp + 52900000000.0) - 58.0 * temp + 230000.0) / temp;
	}

	/**
	 * Approximate B-V color index for the given effective
	 * temperature and luminosity class.
	 */
	public static double temperatureToBV(double temp, LuminosityClass lc) {
		switch( lc ) {
		case BRIGHT_GIANT:
			return temperatureToBV(temp) + 0.1; // Slightly redder
		case SUPERGIANT:
			return temperatureToBV(temp) + 0.2; // Redder
		case HYPERGIANT:
			return temperatureToBV(temp) + 0.4; // Significantly redder
		case WHITE_DWARF:
			return temperatureToBV(temp + temp / 4); // Significantly bluer
		default:
			return temperatureToBV(temp);
		}
	}

	/**
	 * Return the approximate color in sRGB space for the provided temperature
	 * and luminosity class.
	 * 
	 * @see temperatureToBV
	 */
	public static Color temperatureToColor(double temp, LuminosityClass lc) {
		double bv = temperatureToBV(temp, lc);

		// Limit ourselves to B-V index of -0.4 to +2.0, for simplicity.
		if( bv < -0.4 ) { bv = -0.4; }
		if( bv > 2.0 ) { bv = 2.0; }

		float red;
		if( bv < 0.0 ) {
			red = (float) (0.83 + 0.775 * bv + 0.625 * bv * bv); /* 0.62 - 0.83 */
		}
		else if( bv < 0.4 ) {
			red = (float) (0.83 + 0.425 * bv); /* 0.83 - 1.00 */
		}
		else {
			red = 1.00f;
		}

		float green;
		if( bv < 0.0 ) {
			green = (float) (0.87 + 0.675 * bv + 0.625 * bv * bv); /* 0.70 - 0.87 */
		}
		else if( bv < 0.4 ) {
			green = (float) (0.87 + 0.275 * bv); /* 0.87 - 0.98 */
		}
		else if( bv < 1.6 ) {
			green = (float) (( 3.10 - 0.4 * bv ) / 3.0); /* 0.98 - 0.82 */
		}
		else {
			green = (float) (-7.18 + 10.0 * bv - 3.125 * bv * bv); /* 0.82 - 0.32  */
		}

		float blue;
		if( bv < 0.4 ) {
			blue = 1.00f;
		}
		else if( bv < 1.5 ) {
			blue = (float) (( 143.28 - 59.7 * bv + 10.0 * bv * bv ) / 121.0); /* 1.00 - 0.63 */
		}
		else if( bv < 1.94 ){
			blue = (float) (( -767.52 + 1125.0 * bv - 375.0 * bv * bv ) / 121.0);  /* 0.63 - 0.03 */
		}
		else {
			blue = (float) (1.00 - 0.5 * bv); /* 0.03 - 0.00 */
		}

		return new Color(red, green, blue, 1.0f);
	}

	/**
	 * Return the approximate color in sRGB space for the provided temperature.
	 * <p>
	 * The conversion only holds (mostly) for sub-dwarfs (IV) to giants (III). Others
	 * need to be shifted.
	 * 
	 * @see temperatureToBV
	 */
	public static Color temperatureToColor(double temp) {
		return temperatureToColor(temp, LuminosityClass.DWARF);
	}

	/**
	 * List of parsers used to try and turn a string into a StarClass instance, in order.
	 */
	public static final List<StarClassParser> starClassParsers = new ArrayList<StarClassParser>();

	/**
	 * Parse the given star class declaration and return a matching StarClass instance
	 */
	public static StarClass parse(String classDeclaration) {
		if( classDeclaration == null || classDeclaration.length() < 1 ) {
			return null;
		}

		// Run through all the parsers and pick the first one which recognises the string
		for( StarClassParser parser : starClassParsers ) {
			try {
				StarClass result = parser.parse(classDeclaration);
				if( result != null ) {
					return result;
				}
			}
			catch( Exception e ) { /* ignore */ }
		}
		return null;
	}

	/**
	 * Return all valid star classes as a list of strings
	 */
	public static List<String> validStarClasses() {
		List<String> result = new ArrayList<String>();
		for( StarClassParser parser : starClassParsers ) {
			try {
				List<String> addedResults = parser.validStarClasses();
				if( addedResults != null ) {
					result.addAll(addedResults);
				}
			}
			catch( Exception e ) { /* ignore */ }
		}
		Collections.sort(result);
		return result;
	}

	// Star type effective temperature table courtesy of Wikipedia, GURPS4:Space and BT:IO
	private static final Map<String, Double> minTempTable;
	private static final Map<String, Double> maxTempTable;

	// Star type minimum safe jump distance (for BattleTech) - only for main sequence
	private static final Map<String, Double> safeJumpDistance;

	// Star type solar masses limits
	private static final Map<String, Double> minMassTable;
	private static final Map<String, Double> maxMassTable;

	// Average luminosity
	private static final Map<String, Double> avgLuminosityTable;

	// Star generation - modifiers
	private static final Map<String, Integer> habilityModTable;
	private static final Map<String, Integer> gasgiantModTable;

	private static double asDouble(String str) {
		try {
			return Double.valueOf(str);
		} catch( NumberFormatException nfex ) {
			return Double.NaN;
		}
	}

	private static int asInt(String str) {
		try {
			return Integer.valueOf(str);
		} catch( NumberFormatException nfex ) {
			return Integer.MIN_VALUE;
		}
	}


	static {
		starClassParsers.add(new SubdwarfParser());
		starClassParsers.add(new WhiteDwarfParser());
		starClassParsers.add(new MainParser());

		// Read the CSV data
		minTempTable = new HashMap<String, Double>();
		maxTempTable = new HashMap<String, Double>();
		safeJumpDistance = new HashMap<String, Double>();
		minMassTable = new HashMap<String, Double>();
		maxMassTable = new HashMap<String, Double>();
		avgLuminosityTable = new HashMap<String, Double>();
		habilityModTable = new HashMap<String, Integer>();
		gasgiantModTable = new HashMap<String, Integer>();

		try(
				InputStream in = StarClassHelper.class.getResourceAsStream("/de/vernideas/space/data/starclass/starclasses.csv");
				CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(in)), ',', '"')
				) {
			String[] line = reader.readNext();
			while( null != line ) {
				if( line.length == 0 || line[0].startsWith("#") ) {
					line = reader.readNext();
					continue;
				}
				if( line[0].equals("starclass") ) {
					// Header
					line = reader.readNext();
					continue;
				}
				String type = line[0];
				double minTemp = (line.length > 0 ? asDouble(line[1]) : Double.NaN);
				double maxTemp = (line.length > 1 ? asDouble(line[2]) : Double.NaN);
				double jumpDist = (line.length > 2 ? asDouble(line[3]) : Double.NaN);
				double minMass = (line.length > 3 ? asDouble(line[4]) : Double.NaN);
				double maxMass = (line.length > 4 ? asDouble(line[5]) : Double.NaN);
				double avgLum = (line.length > 5 ? asDouble(line[6]) : Double.NaN);
				int habilityMod = (line.length > 6 ? asInt(line[7]) : Integer.MIN_VALUE);
				int gasgiantMod = (line.length > 7 ? asInt(line[8]) : Integer.MIN_VALUE);

				if( !Double.isNaN(minTemp) && !Double.isNaN(maxTemp) ) {
					minTempTable.put(type, minTemp);
					maxTempTable.put(type, maxTemp);
				}
				if( !Double.isNaN(jumpDist) ) {
					safeJumpDistance.put(type, jumpDist);
				}
				if( !Double.isNaN(minMass) && !Double.isNaN(maxMass) ) {
					minMassTable.put(type, minMass);
					maxMassTable.put(type, maxMass);
				}
				if( !Double.isNaN(avgLum) ) {
					avgLuminosityTable.put(type, avgLum);
				}
				if( habilityMod > Integer.MIN_VALUE ) {
					habilityModTable.put(type, habilityMod);
				}
				if( gasgiantMod > Integer.MIN_VALUE ) {
					gasgiantModTable.put(type, gasgiantMod);
				}
				line = reader.readNext();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Static-only class; no need for instances of it
	private StarClassHelper() { }
}
