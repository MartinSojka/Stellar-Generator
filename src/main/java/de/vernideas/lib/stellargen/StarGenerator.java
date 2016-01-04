package de.vernideas.lib.stellargen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.VectorD3D;
import de.vernideas.space.data.starclass.StarClass;
import de.vernideas.space.data.starclass.StarClassHelper;

public final class StarGenerator {
	/**
	 * Generate a random star in the specified universe
	 * 
	 * @param u
	 */
	public static Star star(Universe u) {
		return star(u, spectralDistribution.lowerEntry(u.random.nextInt(maxSpectralVal) + 1).getValue(), u.seed + u.random.nextInt());
	}
	
	public static Star star(Universe u, long seed) {
		return star(u, spectralDistribution.lowerEntry(u.random.nextInt(maxSpectralVal) + 1).getValue(), seed);
	}
	
	public static Star star(Universe u, String scDef) {
		return star(u, scDef, u.seed + u.random.nextInt());
	}
	
	public static Star star(Universe u, String scDef, long seed) {
		Random starRnd = new Random(seed);
		String scClass = scDef.substring(0, 1);
		StarClass sc = StarClassHelper.parse(scDef);
		String name = starName(starRnd, scClass);
		Star star = new Star(name, sc);
		
		star.seed(seed);
		star.random(starRnd);
		star.position(newPosition(u));
		
		double effTemp = StarClassHelper.randomTemp(sc, starRnd);
		double luminosity = StarClassHelper.randomLuminosity(sc, starRnd);
		// Calculate diameter out of temperature and luminosity
		double diameter = Math.sqrt(luminosity) / effTemp / effTemp * Constant.SOLAR_TEMPERATURE * Constant.SOLAR_TEMPERATURE;
		
		star.temperature(effTemp);
		star.diameter(diameter * Constant.SOLAR_DIAMETER);
		star.luminosity(luminosity * Constant.SOLAR_LUM);
		star.originalLuminosity(StarClassHelper.randomOriginalLuminosity(sc, luminosity * Constant.SOLAR_LUM, starRnd));
		star.mass(StarClassHelper.randomMass(sc, starRnd) * Constant.SOLAR_MASS);

		return star;
	}
	
	// Constant tables
	private static final List<Pair<String, Integer>> scList = new ArrayList<Pair<String,Integer>>(100);
	private static final TreeMap<Integer, String> spectralDistribution = new TreeMap<Integer, String>();
	private static final int maxSpectralVal;

	private static final List<String> constellationNames;
	
	private static final List<String> starPrefixes = Arrays.<String>asList(new String[]{
			"α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω",
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "W", "X", "Y", "Z"
	});
	
	private static final List<String> durchmusterungNames;
	
	private static VectorD3D newPosition(Universe u) {
		return new VectorD3D(u.random.nextDouble()*256000.0, u.random.nextDouble()*256000, u.random.nextDouble()*256000);
	}
	
	/**
	 * Generate a random star name
	 */
	public static String starName(Random rnd, String specClass)
	{
		if( specClass.equals("O") || specClass.equals("B") || specClass.equals("A") || specClass.equals("F") || (specClass.equals("G") && rnd.nextBoolean()) )
		{
			int maxPrefix = starPrefixes.size();
			int prefixNum = rnd.nextInt(maxPrefix);
			// Shift to the lower numbers if a bigger star
			switch( specClass )
			{
				case "O":
					prefixNum = Math.min(rnd.nextInt(maxPrefix), prefixNum);
				case "B":
					prefixNum = Math.min(rnd.nextInt(maxPrefix), prefixNum);
				case "A":
					prefixNum = Math.min(rnd.nextInt(maxPrefix), prefixNum);
				default:
					break;
			}
			return starPrefixes.get(prefixNum) + " " + constellationNames.get(rnd.nextInt(constellationNames.size()));
		}
		
		// Flamsteed-like designations
		if( specClass.equals("G") || (specClass.equals("K") && rnd.nextInt(4) > 0) || (specClass.equals("M") && rnd.nextInt(5) == 0) )
		{
			return (Math.max(rnd.nextInt(99), rnd.nextInt(99)) + 1) + " " + constellationNames.get(rnd.nextInt(constellationNames.size()));
		}
		
		// Random catalogue name
		int catalogueMax = durchmusterungNames.size();
		
		// TODO: First number should depend on position relative to the origin
		return durchmusterungNames.get(Math.min(rnd.nextInt(catalogueMax), rnd.nextInt(catalogueMax)))
				+ (rnd.nextBoolean() ? "+" : "-") + String.format("%02d", rnd.nextInt(90)) + "°" + (rnd.nextInt(19900) + 100);
	}
	
	private static void addSC(String base, int minDist, int maxDist)
	{
		for( int i = 0; i <= 9; ++ i )
		{
			scList.add(Pair.<String,Integer>of(base + String.valueOf(i), GenUtil.lerp(minDist, maxDist, i / 9.0)));
		}
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
		
		constellationNames = new ArrayList<String>();
		try(
			InputStream in = StarGenerator.class.getResourceAsStream("/de/vernideas/lib/stellargen/constellations.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))
		) {
			String line = reader.readLine();
			while( null != line ) {
				if( !line.startsWith("#") ) {
					constellationNames.add(line);
				}
				line = reader.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		durchmusterungNames = new ArrayList<String>();
		try(
				InputStream in = StarGenerator.class.getResourceAsStream("/de/vernideas/lib/stellargen/durchmusterungs.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))
			) {
				String line = reader.readLine();
				while( null != line ) {
					if( !line.startsWith("#") ) {
						durchmusterungNames.add(line);
					}
					line = reader.readLine();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
	}

}
