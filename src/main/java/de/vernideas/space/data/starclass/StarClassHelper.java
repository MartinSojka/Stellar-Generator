package de.vernideas.space.data.starclass;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	private static final Map<String, Double> minTempTable = new HashMap<String, Double>();
	private static final Map<String, Double> maxTempTable = new HashMap<String, Double>();
	
	private static void addTemp(String type, double min, double max)
	{
		minTempTable.put(type, min);
		maxTempTable.put(type, max);
	}
	
	// Star type minimum safe jump distance (for BattleTech) - only for main sequence
	private static final Map<String, Double> safeJumpDistance = new HashMap<String, Double>();
	
	// Star type solar masses limits
	private static final Map<String, Double> minMassTable = new HashMap<String, Double>();
	private static final Map<String, Double> maxMassTable = new HashMap<String, Double>();
	
	private static void addMass(String type, double min, double max)
	{
		minMassTable.put(type, min);
		maxMassTable.put(type, max);
	}
	
	static {
		starClassParsers.add(new SubdwarfParser());
		starClassParsers.add(new WhiteDwarfParser());
		starClassParsers.add(new MainParser());

		 // Probably too hot, but those are so rare you never know.
		addTemp("O0", 57500, 65000); safeJumpDistance.put("O0", 3823342301741.0);
		addTemp("O1", 53500, 57500); safeJumpDistance.put("O1", 2703511268359.0);
		addTemp("O2", 50000, 53500); safeJumpDistance.put("O2", 2207407707114.0);
		addTemp("O3", 46500, 50000); safeJumpDistance.put("O3", 1745109019773.0);
		addTemp("O4", 43500, 46500); safeJumpDistance.put("O4", 1439053763348.0);
		addTemp("O5", 40500, 43500); safeJumpDistance.put("O5", 1130959904760.0);
		addTemp("O6", 37500, 40500); safeJumpDistance.put("O6", 903420539704.0);
		addTemp("O7", 34500, 37500); safeJumpDistance.put("O7", 706715289566.0);
		addTemp("O8", 31500, 34500); safeJumpDistance.put("O8", 551851926779.0);
		addTemp("O9", 30000, 34500); safeJumpDistance.put("O9", 427462664397.0);
		addTemp("B0", 27000, 30000); safeJumpDistance.put("B0", 347840984769.0);
		addTemp("B1", 25000, 27000); safeJumpDistance.put("B1", 282066836091.0);
		addTemp("B2", 23000, 25000); safeJumpDistance.put("B2", 229405969325.0);
		addTemp("B3", 21000, 23000); safeJumpDistance.put("B3", 187115967958.0);
		addTemp("B4", 19500, 21000); safeJumpDistance.put("B4", 153067686150.0);
		addTemp("B5", 18000, 19500); safeJumpDistance.put("B5", 125563499718.0);
		addTemp("B6", 16500, 18000); safeJumpDistance.put("B6", 103286041300.0);
		addTemp("B7", 14000, 16000); safeJumpDistance.put("B7", 85203218902.0);
		addTemp("B8", 12000, 14000); safeJumpDistance.put("B8", 70474451635.0);
		addTemp("B9", 10000, 12000); safeJumpDistance.put("B9", 58430461862.0);
		addTemp("A0", 9700, 10000); safeJumpDistance.put("A0", 48582277772.0);
		addTemp("A1", 9450, 9700); safeJumpDistance.put("A1", 40498150645.0);
		addTemp("A2", 9300, 9450); safeJumpDistance.put("A2", 33849108637.0);
		addTemp("A3", 9000, 9300); safeJumpDistance.put("A3", 28381605649.0);
		addTemp("A4", 8800, 9000); safeJumpDistance.put("A4", 23844066419.0);
		addTemp("A5", 8500, 8800); safeJumpDistance.put("A5", 20061644606.0);
		addTemp("A6", 8250, 8500); safeJumpDistance.put("A6", 16931308504.0);
		addTemp("A7", 8000, 8250); safeJumpDistance.put("A7", 14324662716.0);
		addTemp("A8", 7750, 8000); safeJumpDistance.put("A8", 12147011068.0);
		addTemp("A9", 7500, 7750); safeJumpDistance.put("A9", 10324169238.0);
		addTemp("F0", 7350, 7500); safeJumpDistance.put("F0", 8782563721.0);
		addTemp("F1", 7200, 7350); safeJumpDistance.put("F1", 7509968038.0);
		addTemp("F2", 7050, 7200); safeJumpDistance.put("F2", 6426026992.0);
		addTemp("F3", 6900, 7050); safeJumpDistance.put("F3", 5510895632.0);
		addTemp("F4", 6800, 6900); safeJumpDistance.put("F4", 4736187040.0);
		addTemp("F5", 6650, 6800); safeJumpDistance.put("F5", 4079039960.0);
		addTemp("F6", 6500, 6650); safeJumpDistance.put("F6", 3520358039.0);
		addTemp("F7", 6350, 6500); safeJumpDistance.put("F7", 3044526612.0);
		addTemp("F8", 6200, 6350); safeJumpDistance.put("F8", 2638513835.0);
		addTemp("F9", 6000, 6200); safeJumpDistance.put("F9", 2290901666.0);
		addTemp("G0", 5900, 6000); safeJumpDistance.put("G0", 1993326049.0);
		addTemp("G1", 5800, 5900); safeJumpDistance.put("G1", 1737804380.0);
		addTemp("G2", 5700, 5800); safeJumpDistance.put("G2", 1517879732.0);
		addTemp("G3", 5600, 5700); safeJumpDistance.put("G3", 1328301833.0);
		addTemp("G4", 5500, 5600); safeJumpDistance.put("G4", 1164589626.0);
		addTemp("G5", 5400, 5500); safeJumpDistance.put("G5", 1023057406.0);
		addTemp("G6", 5350, 5400); safeJumpDistance.put("G6", 900260278.0);
		addTemp("G7", 5300, 5350); safeJumpDistance.put("G7", 793654769.0);
		addTemp("G8", 5250, 5300); safeJumpDistance.put("G8", 700990216.0);
		addTemp("G9", 5200, 5250); safeJumpDistance.put("G9", 620061930.0);
		addTemp("K0", 5100, 5200); safeJumpDistance.put("K0", 549564113.0);
		addTemp("K1", 5000, 5100); safeJumpDistance.put("K1", 487899662.0);
		addTemp("K2", 4900, 5000); safeJumpDistance.put("K2", 433890326.0);
		addTemp("K3", 4800, 4900); safeJumpDistance.put("K3", 386486041.0);
		addTemp("K4", 4650, 4800); safeJumpDistance.put("K4", 344841863.0);
		addTemp("K5", 4500, 4650); safeJumpDistance.put("K5", 308167706.0);
		addTemp("K6", 4300, 4500); safeJumpDistance.put("K6", 275861999.0);
		addTemp("K7", 4100, 4300); safeJumpDistance.put("K7", 247343861.0);
		addTemp("K8", 3900, 4100); safeJumpDistance.put("K8", 222081144.0);
		addTemp("K9", 3700, 3900); safeJumpDistance.put("K9", 199737005.0);
		addTemp("M0", 3600, 3700); safeJumpDistance.put("M0", 179917035.0);
		addTemp("M1", 3400, 3550); safeJumpDistance.put("M1", 162304787.0);
		addTemp("M2", 3250, 3400); safeJumpDistance.put("M2", 146630886.0);
		addTemp("M3", 3100, 3250); safeJumpDistance.put("M3", 132669349.0);
		addTemp("M4", 2950, 3100); safeJumpDistance.put("M4", 120212270.0);
		addTemp("M5", 2800, 2950); safeJumpDistance.put("M5", 109082750.0);
		addTemp("M6", 2700, 2800); safeJumpDistance.put("M6", 99120198.0);
		addTemp("M7", 2600, 2700); safeJumpDistance.put("M7", 90202821.0);
		addTemp("M8", 2500, 2600); safeJumpDistance.put("M8", 82196817.0);
		addTemp("M9", 2400, 2500); safeJumpDistance.put("M9", 75004186.0);
		// Not likely to be the central mass of a solar system of their own,
		// but often "dark" partners in multi-star systems
		addTemp("L0", 2290, 2400); safeJumpDistance.put("L0", 64303323.0);
		addTemp("L1", 2180, 2290); safeJumpDistance.put("L1", 58164544.0);
		addTemp("L2", 2070, 2180); safeJumpDistance.put("L2", 52741556.0);
		addTemp("L3", 1960, 2070); safeJumpDistance.put("L3", 48276182.0);
		addTemp("L4", 1850, 1960); safeJumpDistance.put("L4", 45054062.0);
		addTemp("L5", 1740, 1850); safeJumpDistance.put("L5", 40668992.0);
		addTemp("L6", 1630, 1740); safeJumpDistance.put("L6", 37794523.0);
		addTemp("L7", 1520, 1630); safeJumpDistance.put("L7", 33581315.0);
		addTemp("L8", 1410, 1520); safeJumpDistance.put("L8", 30036042.0);
		addTemp("L9", 1300, 1410); safeJumpDistance.put("L9", 27419029.0);
		addTemp("T0", 1200, 1300); safeJumpDistance.put("T0", 24524325.0);
		addTemp("T1", 1100, 1200); safeJumpDistance.put("T1", 22105928.0);
		addTemp("T2", 1000, 1100); safeJumpDistance.put("T2", 19388181.0);
		addTemp("T3", 900, 1000); safeJumpDistance.put("T3", 17341317.0);
		addTemp("T4", 800, 900); safeJumpDistance.put("T4", 15018021.0);
		addTemp("T5", 700, 800); safeJumpDistance.put("T5", 12860665.0);
		addTemp("T6", 650, 700); safeJumpDistance.put("T6", 11632909.0);
		addTemp("T7", 600, 650); safeJumpDistance.put("T7", 10619344.0);
		addTemp("T8", 550, 600); safeJumpDistance.put("T8", 9498230.0);
		addTemp("T9", 500, 550); safeJumpDistance.put("T9", 8225709.0);
		// White Dwarves are ... different.
		// Specifically, the subtype number is equal to Math.round(50400/T_eff)
		addTemp("D0", 75600, 100800);
		addTemp("D1", 33600, 75600);
		addTemp("D2", 20160, 33600);
		addTemp("D3", 14400, 20160);
		addTemp("D4", 11200, 14400);
		addTemp("D5", 9163, 11200);
		addTemp("D6", 7754, 9163);
		addTemp("D7", 6720, 7754);
		addTemp("D8", 5929, 6720);
		addTemp("D9", 5000, 5929); // Technically, 50400 / 9.5 = 5305 is the lower limit
		// Mass limits (in solar masses, rough values)
		addMass("T9", 0.009504, 0.009696); // About 10x Jupiter mass
		addMass("T8", 0.009702, 0.009898);
		addMass("T7", 0.0099, 0.0101);
		addMass("T6", 0.009996, 0.010404);
		addMass("T5", 0.010088, 0.010712);
		addMass("T4", 0.010176, 0.011024);
		addMass("T3", 0.01026, 0.01134);
		addMass("T2", 0.01034, 0.01166);
		addMass("T1", 0.010695, 0.012305);
		addMass("T0", 0.01104, 0.01296);
		addMass("L9", 0.01183, 0.01417); // About 13x Jupiter mass, Lowest cut-off point for nuclear fusion
		addMass("L8", 0.0126, 0.0154);
		addMass("L7", 0.01275, 0.01725);
		addMass("L6", 0.0152, 0.0228);
		addMass("L5", 0.0176, 0.0264);
		addMass("L4", 0.0216, 0.0324);
		addMass("L3", 0.0248, 0.0372);
		addMass("L2", 0.0296, 0.0444);
		addMass("L1", 0.036, 0.054);
		addMass("L0", 0.044, 0.066);
		addMass("M9", 0.06, 0.09);
		addMass("M8", 0.072, 0.108);
		addMass("M7", 0.081, 0.119);
		addMass("M6", 0.1215, 0.1785);
		addMass("M5", 0.164, 0.236);
		addMass("M4", 0.2255, 0.3245);
		addMass("M3", 0.2905, 0.4095);
		addMass("M2", 0.332, 0.468);
		addMass("M1", 0.378, 0.522);
		addMass("M0", 0.42, 0.58);
		addMass("K9", 0.44625, 0.60375);
		addMass("K8", 0.4675, 0.6325);
		addMass("K7", 0.4945, 0.6555);
		addMass("K6", 0.516, 0.684);
		addMass("K5", 0.5655, 0.7345);
		addMass("K4", 0.609, 0.791);
		addMass("K3", 0.638, 0.812);
		addMass("K2", 0.66, 0.84);
		addMass("K1", 0.68975, 0.86025);
		addMass("K0", 0.712, 0.888);
		addMass("G9", 0.7875, 0.9625);
		addMass("G8", 0.765, 0.935);
		addMass("G7", 0.7875, 0.9625);
		addMass("G6", 0.81, 0.99);
		addMass("G5", 0.8325, 1.0175);
		addMass("G4", 0.855, 1.045);
		addMass("G3", 0.8775, 1.0725);
		addMass("G2", 0.9, 1.1);
		addMass("G1", 0.945, 1.155);
		addMass("G0", 0.99, 1.21);
		addMass("F9", 1.035, 1.265);
		addMass("F8", 1.08, 1.32);
		addMass("F7", 1.125, 1.375);
		addMass("F6", 1.17, 1.43);
		addMass("F5", 1.215, 1.485);
		addMass("F4", 1.26, 1.54);
		addMass("F3", 1.305, 1.595);
		addMass("F2", 1.35, 1.65);
		addMass("F1", 1.395, 1.705);
		addMass("F0", 1.44, 1.76);
		addMass("A9", 1.513, 1.887);
		addMass("A8", 1.5575, 1.9425);
		addMass("A7", 1.584, 2.016);
		addMass("A6", 1.672, 2.128);
		addMass("A5", 1.6965, 2.2035);
		addMass("A4", 1.74, 2.26);
		addMass("A3", 1.7802, 2.3598);
		addMass("A2", 1.8662, 2.4738);
		addMass("A1", 1.938, 2.622);
		addMass("A0", 2.091, 2.829);
		addMass("B9", 2.6628, 3.6772);
		addMass("B8", 3.3012, 4.5588);
		addMass("B7", 4.0338, 5.6862);
		addMass("B6", 4.7891, 6.7509);
		addMass("B5", 5.494, 7.906);
		addMass("B4", 6.561, 9.639);
		addMass("B3", 8.08, 12.12);
		addMass("B2", 11.85, 18.15);
		addMass("B1", 15.6, 24.4);
		addMass("B0", 23.1, 36.9);
		addMass("O9", 35.25, 58.85);
		addMass("O8", 57.72, 98.28);
		addMass("O7", 94.9, 165.1);
		addMass("O6", 151.2, 268.8);
		addMass("O5", 234.3, 425.7);
		addMass("O4", 371.0, 689.0);
		addMass("O3", 538.2, 1021.8);
		addMass("O2", 837.5, 1662.5);
		addMass("O1", 1235.0, 2565.0);
		addMass("O0", 2437.5, 5000.0);
		// White dwarves don't vary by mass much
		addMass("D0", 1.0, 1.5);
		addMass("D1", 0.97, 1.47);
		addMass("D2", 0.94, 1.44);
		addMass("D3", 0.92, 1.42);
		addMass("D4", 0.9, 1.4);
		addMass("D5", 0.88, 1.38);
		addMass("D6", 0.86, 1.36);
		addMass("D7", 0.84, 1.34);
		addMass("D8", 0.82, 1.32);
		addMass("D9", 0.8, 1.3);
	}
	
	// Static-only class; no need for instances of it
	private StarClassHelper() { }
}
