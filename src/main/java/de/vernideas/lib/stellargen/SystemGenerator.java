package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Satellite;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.starclass.StarClassHelper;

/**
 * Random stateless star generator.
 * <p>
 * Seed hierarchy:
 * <dl>
 * <dt>Universe seed</dt>
 * <dd>Used to determine<ul><li>Star positions</li><li>Star seeds</li>
 *     <li>Stellar generation (clusters, ...)</li><li>Star names</li></ul>
 * <dt>Star seed</dt>
 * <dd>Used to determine<ul><li>Stellar class</li><li>Stellar mass</li><li>Stellar luminosity</li>
 *     <li>Stellar diameter</li><li>Planet and planetoid seeds</li><li>Planet and planetoid masses</li>
 *     <li>Planet and planetoid (original) classes</li><li>Planet and planetoid densities</li>
 *     <li>Planet and planetoid orbits</li></ul></dd>
 * <dt>Planet seed</dt>
 * <dd>Used to determine<ul><li>Planet class</li><li>Albedo</li><li>Greenhouse effect</li>
 *     <li>Atmosphere</li>
 *     <li>Moon seeds</li>
 *     <li>Moon masses</li><li>Moon (original) classes</li><li>Moon densities</li>
 *     <li>Moon orbits</li></ul></dd>
 * </dl>
 */
public class SystemGenerator {
	public static Star star(Universe u) {
		return star(u, null);
	}
	
	public static Star star(Universe u, String scDef) {
		// Build the star first
		Star star = (null == scDef ? StarGenerator.star(u) : StarGenerator.star(u, scDef));
		
		addPlanetarySystem(star);
		
		return star;
	}
	
	public static Star star(Universe u, String scDef, long starSeed) {
		// Build the star first
		Star star = (null == scDef ? StarGenerator.star(u, starSeed) : StarGenerator.star(u, scDef, starSeed));
		
		addPlanetarySystem(star);
		
		return star;
	}
	
	private static void addPlanetarySystem(Star star) {
		double maxPlanetaryMass = Math.min(25e28, star.mass() / 25.0);
		
		// Planet building phase
		int gasgiantMod = StarClassHelper.gasgiantMod(star.starClass);
		int planetNum = (int)Math.round(StarClassHelper.randomPlanets(star.starClass, star.random()));
		if( planetNum < 0 ) { planetNum = 0; }
		if( planetNum > 26 ) { planetNum = 26; } // Arbitrary, to not complicate the naming scheme

		double stellarDustLimit = 1e13 * Math.pow(12 + gasgiantMod, 3.0) * Math.pow(star.mass(), 1.0 / 3.0) * (1.0 + star.random().nextGaussian() * 0.1);
		if( stellarDustLimit < 0.0 ) {
			stellarDustLimit = 1e13 *  Math.pow(star.mass(), 1.0 / 3.0);
		}
		stellarDustLimit += planetNum * Constant.MIN_TERRESTRIAL_MASS;
		
		double stellarDust = stellarDustLimit;
		
		List<Planet> planets = null;
		if( planetNum == 0 )
		{
			planets = new ArrayList<Planet>(0);
		}
		else
		{
			planets = new ArrayList<Planet>(planetNum);
			
			int curPlanetNum = 0;
			while( stellarDust > 1000.0 * Constant.YOTTAGRAM && planetNum > curPlanetNum )
			{
				Planet tempPlanet = null;
				if( stellarDust > stellarDustLimit * 0.01 && stellarDust > Constant.MAX_TERRESTRIAL_MASS && star.random().nextInt(10) + gasgiantMod > 3 )
				{
					// Try generating gas giants first
					double minMass = Math.max(Constant.MAX_TERRESTRIAL_MASS, stellarDust / 2.0);
					tempPlanet = PlanetGenerator.newGasgiant(star, null, minMass, Math.min(maxPlanetaryMass, stellarDust));
				}
				else
				{
					double maxMass = Math.min(maxPlanetaryMass, stellarDust * (0.5 + (planetNum - curPlanetNum) / (2.0 * planetNum)));
					tempPlanet = PlanetGenerator.newTerrestialPlanet(star, null, Constant.MIN_TERRESTRIAL_MASS, maxMass);
				}
				if( null != tempPlanet && tempPlanet.mass() <= stellarDust ) {
					planets.add(tempPlanet);
					star.planets.add(tempPlanet);
					stellarDust -= tempPlanet.mass();
					++ curPlanetNum;
				}
			}
			
			planetNum = curPlanetNum;
		}
				
		// Assign orbits
		Collections.sort(planets, Satellite.REVERSE_MASS_COMPARATOR);
		int generatedPlanets = 0;
		double smallestPlanetMass = Constant.MAX_TERRESTRIAL_MASS;
		for( int i = 0; i < planetNum; ++ i ) {
			/*
			Planet planet = null;
			boolean habitable = false;
			if( planetMasses.get(i) > Constant.MIN_TERRESTRIAL_MASS && planetMasses.get(i) < Constant.MAX_TERRESTRIAL_MASS ) {
				planet = PlanetGenerator.newTerrestialPlanet(star, planetMasses.get(i), star.name() + " " + (char)('b' + generatedPlanets), habitable ? 0 : 1000);
			} else {
				planet = PlanetGenerator.newGasgiant(star, planetMasses.get(i), star.name() + " " + (char)('b' + generatedPlanets));
			}
			if( null != planet )
			{
				habitable = habitable || planet.habitable();
				star.planets.add(planet);
				++ generatedPlanets;
				if( planet.mass() < smallestPlanetMass )
				{
					smallestPlanetMass = planet.mass();
				}
			}
			*/
			boolean habitable = false;
			Planet planet = planets.get(i);
			planet.name(star.name() + " " + (char)('b' + generatedPlanets));
			habitable = habitable || planet.habitable();
			//star.planets.add(planet);
			++ generatedPlanets;
			if( planet.mass() < smallestPlanetMass )
			{
				smallestPlanetMass = planet.mass();
			}
		}

		// Add planetoids
		double planetoidEstimate = Math.min(Math.pow(stellarDust / Constant.YOTTAGRAM, 0.3) * 20.0, 20.0 + Math.abs(star.random().nextGaussian() * 15.0));
		int planetoids = (int)Math.round(planetoidEstimate);
		
		for( int i = 0; i < planetoids; ++ i )
		{
			Planet planet = PlanetGenerator.newPlanetoid(star, smallestPlanetMass / 10);
			if( null != planet )
			{
				star.planetoids.add(planet);
			}
		}
	}
		
	/**
	 * Root mean square velocity of the molecule/atom given the temperature
	 * (for calculating escape velocity of the atmosphere).
	 * 
	 * @param molWeight molar mass (g/mol)
	 * @return Velocity in m/s
	 */
	public static double rmsVelocity(double molWeight, double temperature)
	{
		return Math.sqrt(3 * Constant.MOLAR_GAS * temperature / molWeight * 1000);
	}
	
	/** Returns a pair of mass (in kG) and random seed used to create the mass */
	private static Pair<Double, Long> planetMass(Random rnd, double minMass, double maxMass)
	{
		double mass;
		long seed;
		do {
			seed = rnd.nextLong();
			mass = Satellite.newMass(new Random(seed));
		} while( mass > maxMass || mass < minMass );
		return Pair.<Double, Long>of(mass, seed);
	}
}
