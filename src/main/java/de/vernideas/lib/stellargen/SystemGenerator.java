package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.starclass.StarClassHelper;

/**
 * Random stateless star generator
 */
public class SystemGenerator {
	/**
	 * Generate a random star in the specified universe
	 * 
	 * @param u
	 */
	public static Star star(Universe u) {
		// Build the star first
		Star star = StarGenerator.star(u);
		double maxPlanetaryMass = Math.min(25e28, star.mass / 25.0);
		
		// Planet building phase
		int gasgiantMod = StarClassHelper.gasgiantMod(star.starClass);
		int planetNum = (int)Math.round(StarClassHelper.randomPlanets(star.starClass, star.random));
		if( planetNum < 0 ) { planetNum = 0; }
		if( planetNum > 26 ) { planetNum = 26; } // Arbitrary, to not complicate the naming scheme

		double stellarDustLimit = 1e13 * Math.pow(12 + gasgiantMod, 3.0) * Math.pow(star.mass, 1.0 / 3.0) * (1.0 + star.random.nextGaussian() * 0.1);
		if( stellarDustLimit < 0.0 ) {
			stellarDustLimit = 1e13 *  Math.pow(star.mass, 1.0 / 3.0);
		}
		stellarDustLimit += planetNum * Constant.MIN_TERRESTRIAL_MASS;
		
		double stellarDust = stellarDustLimit;
		
		System.err.println(star.name + " - trying to create " + planetNum + " planets from " + String.format("%.2f", stellarDust / Constant.YOTTAGRAM) + " Yg stellar dust"
				+ " (gas giant mod " + gasgiantMod + ")");
		
		List<Double> planetMasses = null;
		if( planetNum == 0 )
		{
			planetMasses = new ArrayList<Double>(0);
		}
		else
		{
			planetMasses = new ArrayList<Double>(planetNum);

			int curPlanetNum = 0;
			while( stellarDust > 1000.0 * Constant.YOTTAGRAM && planetNum > curPlanetNum )
			{
				double randomMass = 0.0;
				if( stellarDust > stellarDustLimit * 0.01 && stellarDust > Constant.MAX_TERRESTRIAL_MASS && star.random.nextInt(10) + gasgiantMod > 3 )
				{
					// Try generating gas giants first
					double minMass = Math.max(Constant.MAX_TERRESTRIAL_MASS, stellarDust / 2.0);
					randomMass = planetMass(u, minMass, Math.min(maxPlanetaryMass, stellarDust));
					// System.err.println(star.name + " generated gas giant of " + randomMass + " [" + minMass + ", " + Math.min(maxPlanetaryMass, stellarDust) + "]");
				}
				else
				{
					double maxMass = Math.min(maxPlanetaryMass, stellarDust * (0.5 + (planetNum - curPlanetNum) / (2.0 * planetNum)));
					randomMass = planetMass(u, 1e23, maxMass);
					// System.err.println(star.name + " generated rocky planet of " + randomMass + " [100.0, " +maxMass + "]");
				}
				if( randomMass <= stellarDust )
				{
					// Would be we left with a whole planet's worth of mass?
					if( curPlanetNum >= planetNum - 1 && stellarDust - randomMass > Constant.MIN_TERRESTRIAL_MASS * 1.1 ) {
						randomMass += (stellarDust - randomMass) * (1.0 - Math.abs(star.random.nextGaussian()) * 0.001);
					}
					randomMass = Math.min(randomMass, maxPlanetaryMass);
					planetMasses.add(randomMass);
					stellarDust -= randomMass;
					++ curPlanetNum;
				}
			}
			
			planetNum = curPlanetNum;
		}
				
		// Assign orbits
		Collections.sort(planetMasses, Collections.<Double>reverseOrder());
		int generatedPlanets = 0;
		double smallestPlanetMass = Constant.MAX_TERRESTRIAL_MASS;
		for( int i = 0; i < planetNum; ++ i ) {
			Planet planet = null;
			if( planetMasses.get(i) > Constant.MIN_TERRESTRIAL_MASS && planetMasses.get(i) < Constant.MAX_TERRESTRIAL_MASS ) {
				planet = PlanetGenerator.newTerrestialPlanet(star, planetMasses.get(i), star.name + " " + (char)('b' + generatedPlanets));
			} else {
				planet = PlanetGenerator.planet(star, planetMasses.get(i), star.name + " " + (char)('b' + generatedPlanets));
			}
			if( null != planet )
			{
				star.planets.add(planet);
				++ generatedPlanets;
				if( planet.mass < smallestPlanetMass )
				{
					smallestPlanetMass = planet.mass;
				}
			}
		}

		// Add planetoids
		double planetoidEstimate = Math.min(Math.pow(stellarDust / Constant.YOTTAGRAM, 0.3) * 20.0, 20.0 + Math.abs(star.random.nextGaussian() * 15.0));
		int planetoids = (int)Math.round(planetoidEstimate);
		System.err.println("Remaining mass of "+ star.name + ": " + String.format("%.2f", stellarDust / Constant.YOTTAGRAM) + " Yg -> " + planetoids + " planetoids");
		
		for( int i = 0; i < planetoids; ++ i )
		{
			double planetoidMass = Math.pow(Math.min(star.random.nextDouble(), star.random.nextDouble()), 6.0) * (smallestPlanetMass / 10 - 1e19) + 1e19;
			Planet planet = PlanetGenerator.newPlanetoid(star, planetoidMass);
			if( null != planet )
			{
				star.planetoids.add(planet);
			}
		}
		return star;
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
	
	
	private static double planetMass(Universe u, double maxMass)
	{
		return planetMass(u, 0.0, maxMass);
	}

	private static double planetMass(Universe u, double minMass, double maxMass)
	{
		double mass;
		do {
			double rnd = u.random.nextDouble();
			mass = 0.0001814813990910743 * Math.exp(25.647952850461436 * rnd) + 19765.338232060116 * rnd;
			mass *= Constant.YOTTAGRAM;
		} while( mass > maxMass || mass < minMass );
		return mass;
	}
}
