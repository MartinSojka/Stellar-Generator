package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.SpectralClass;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Star.StarBuilder;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.VectorI3D;

public final class StarGenerator {
	/**
	 * Generate a random star in the specified universe
	 * 
	 * @param u
	 */
	public static Star star(Universe u) {
		StarBuilder builder = Star.builder();
		
		builder.position(new VectorI3D(u.random.nextInt(256000), u.random.nextInt(256000), u.random.nextInt(256000))).seed(u.seed);
		
		int randomSC = u.random.nextInt(maxSpectralVal) + 1;
		String scDef = spectralDistribution.lowerEntry(randomSC).getValue();
		String scClass = scDef.substring(0, 1);
		SpectralClass sc = new SpectralClass(scDef);
		builder.spectralClass(sc);
		
		double meanTemp = sc.meanEffectiveTemperature;
		double effTemp = u.random.nextGaussian() * 0.1 * meanTemp + meanTemp;
		
		builder.temperature(effTemp);
		
		double solarR = solarRadius(scDef); // Radius in solar units (695500 km)
		double actualR = (1.0 + u.random.nextGaussian() * 0.10) * solarR * Constant.SOLAR_RADIUS; // Actual radius
		
		builder.diameter((int)Math.ceil(actualR * 2));
		
		double luminosity = 4 * Math.PI * Constant.STEFAN_BOLTZMANN * actualR * actualR * effTemp * effTemp * effTemp * effTemp;
		double solarLum = luminosity / Constant.SOLAR_LUM; // Luminosity in solar units
		
		// Mass calculation depends on luminosity ratio
		double mass = Constant.SOLAR_MASS;
		if( solarLum >= 64000.0 )
		{
			// Giants or similar, linear
			mass *= solarLum / 3200.0;
		}
		else if( solarLum > 16.97  )
		{
			// Bigger stars, mostly A and F
			mass *= Math.pow(solarLum / 1.5, 1 / 3.5);
		}
		else if( solarLum > 0.034188 )
		{
			// Around the sol's size, G/K/M
			mass *= Math.pow(solarLum, 0.25);
		}
		else
		{
			// Tiny stars
			mass *= Math.pow(solarLum / 0.23, 1 / 2.3);
		}
		builder.mass(mass);
		builder.name(starName(u, scClass));

		return builder.build();
	}
	
	// Constant tables
	private static final List<Pair<String, Integer>> scList = new ArrayList<Pair<String,Integer>>(100);
	private static final TreeMap<Integer, String> spectralDistribution = new TreeMap<Integer, String>();
	private static final int maxSpectralVal;

	private static final List<String> constellationNames = Arrays.<String>asList(new String[]{
			"Anguillae", "Apium", "Araneae", "Avis", "Bufonis", "Cameli", "Caprae", "Castoris", "Catuli", "Cervi", "Coronae",
			"Erinacei", "Equi", "Felis", "Galli", "Hippocampi", "Hirudinis", "Hystricis", "Leaenae", "Lilii", "Limacis", "Marmoti",
			"Mustelae", "Noctuae", "Patellae", "Pinnae", "Plutei", "Praesepis", "Quadrati", "Pantherae", "Phocae", "Regis",
			"Roboris", "Rosae", "Sciuri", "Sirenis", "Talpae", "Tarandri", "Testudinis", "Tigris", "Trianguli", "Urnae",
			"Vespae", "Vespertilionis", "Vexilli", "Vulpis"
	}); 
	
	private static final List<String> starPrefixes = Arrays.<String>asList(new String[]{
			"α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω",
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "W", "X", "Y", "Z"
	});
	
	private static final List<String> durchmusterungNames = Arrays.<String>asList(new String[]{
			"BD", "CD", "HD", "ED", "KD", "VD", "FD", "MD", "RD", "SD"
	});
	
	private static void addSC(String base, int minDist, int maxDist)
	{
		for( int i = 0; i <= 9; ++ i )
		{
			scList.add(Pair.<String,Integer>of(base + String.valueOf(i), (minDist * (9 - i) + maxDist * i) / 10));
		}
	}

	/**
	 * Estimate the radius via r(x) = 32*exp(-0.08251674051476068*x)
	 */
	private static double solarRadius(String spec)
	{
		return 32.0 * Math.exp(specNum(spec) * -0.08251674051476068);
	}
	
	/**
	 * Generate a random star name
	 */
	private static String starName(Universe u, String specClass)
	{
		if( specClass.equals("O") || specClass.equals("B") || specClass.equals("A") || specClass.equals("F") || (specClass.equals("G") && u.random.nextBoolean()) )
		{
			int maxPrefix = starPrefixes.size();
			int prefixNum = u.random.nextInt(maxPrefix);
			// Shift to the lower numbers if a bigger star
			switch( specClass )
			{
				case "O":
					prefixNum = Math.min(u.random.nextInt(maxPrefix), prefixNum);
				case "B":
					prefixNum = Math.min(u.random.nextInt(maxPrefix), prefixNum);
				case "A":
					prefixNum = Math.min(u.random.nextInt(maxPrefix), prefixNum);
				default:
					break;
			}
			return starPrefixes.get(prefixNum) + " " + constellationNames.get(u.random.nextInt(constellationNames.size()));
		}
		
		// Flamsteed-like designations
		if( specClass.equals("G") || (specClass.equals("K") && u.random.nextInt(4) > 0) || (specClass.equals("M") && u.random.nextInt(5) == 0) )
		{
			return (Math.max(u.random.nextInt(99), u.random.nextInt(99)) + 1) + " " + constellationNames.get(u.random.nextInt(constellationNames.size()));
		}
		
		// Random catalogue name
		int catalogueMax = durchmusterungNames.size();
		
		// TODO: First number should depend on position relative to the origin
		
		return durchmusterungNames.get(Math.min(u.random.nextInt(catalogueMax), u.random.nextInt(catalogueMax)))
				+ (u.random.nextBoolean() ? "+" : "-") + String.format("%02d", u.random.nextInt(90)) + "°" + (u.random.nextInt(19900) + 100);
	}
	
	private static final List<String> specClasses = Arrays.<String>asList(new String[]{"O", "B", "A", "F", "G", "K", "M", "L", "T", "Y"});

	private static int specNum(String spec)
	{
		String specClass = spec.substring(0, 1);
		int classNum = specClasses.indexOf(specClass);
		return classNum * 10 + Integer.valueOf(spec.substring(1, 2));
	}
	
	static {
		addSC("O", 1, 12);
		addSC("B", 13, 20);
		addSC("A", 25, 40);
		addSC("F", 45, 80);
		addSC("G", 100, 150);
		addSC("K", 160, 250);
		addSC("M", 260, 400);
		// These should be more common, but we don't actually care that much for them. Too small, too dark.
		addSC("L", 200, 20);
		// those are just too small (come up with a weight below Jupiter ...)
		// addSC("T", 150, 100);
		// addSC("Y", 90, 50);
		
		int count = 0;
		for( Pair<String, Integer> spectral : scList) {
			spectralDistribution.put(count, spectral.first);
			count += spectral.second;
		}
		maxSpectralVal = count;
	}

}
