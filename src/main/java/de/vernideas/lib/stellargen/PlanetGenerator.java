package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Material;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.OrbitalZone;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Planet.PlanetBuilder;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.planetaryclass.PlanetaryClass;

public final class PlanetGenerator {
	private static double between(double min, double max, double val) {
		return min + (max - min) * val;
	}
	
	public static Planet newTerrestialPlanet(Star star, double mass, String name) {
		return newTerrestialPlanet(star, mass, name, 0);
	}
	
	/** Try to generate a new terrestial planet */
	public static Planet newTerrestialPlanet(Star star, double mass, String name, int habitableRetries) {
		if( mass > Constant.MAX_TERRESTRIAL_MASS || mass < Constant.MIN_TERRESTRIAL_MASS || null == star ) {
			return null;
		}
		PlanetBuilder builder = Planet.builder().parent(star).name(name).mass(mass).minor(false);
		Planet planet = null;
		
		do {
			// Trying to get a free orbit
			double orbit = 0;
			float eccentrity = 0.0f;
			int errCount = 0;
			do {
				orbit = between(star.innerPlanetLimit, star.outerPlanetLimit, Math.pow(Math.min(star.random.nextDouble(), star.random.nextDouble()), 2.0));
				eccentrity = (float)Math.pow(star.random.nextDouble(), 6.0) / 2.0f;
				// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
				if( orbit / Constant.AU < star.mass / 1e29 )
				{
					eccentrity *= (orbit / Constant.AU * 1e29 / star.mass);
				}
				// We try to keep the terrestial planets in the "inside" of the frost zone.
				if( orbit > star.frostLine && mass < star.random.nextDouble() * Constant.MAX_TERRESTRIAL_MASS ) {
					orbit = Math.max(Math.min(orbit, star.random.nextDouble() * star.outerPlanetLimit), star.innerPlanetLimit);
				}
				if( !star.orbitFree(orbit, eccentrity) )
				{
					++ errCount;
					if( errCount > 20 )
					{
						// We give up on that one
						return null;
					}
				}
			} while( !star.orbitFree(orbit, eccentrity) );
			// Rayleigh distribution, sigma = 1° (see arXiv:1207.5250 [astro-ph.EP])
			float inclination = (float)Math.toRadians(Math.sqrt(-2.0 * Math.log(star.random.nextDouble())));
			float rotationPeriod = (float)star.random.nextGaussian() * 60000 + 72000;
			
			Orbit planetaryOrbit = new Orbit(orbit, eccentrity, inclination);
			builder.orbit(planetaryOrbit).rotationPeriod(rotationPeriod);
			OrbitalZone orbitalZone = planetaryOrbit.orbitalZone(star);

			// Pick planetary model
			PlanetaryClass pClass = newTerrestialClass(star.random);
			while( !pClass.validTemperature(star, planetaryOrbit) ) {
				pClass = newTerrestialClass(star.random);
			}

			// Create planetary material
			Material material = pClass.newMaterial(star.random, planetaryOrbit.blackbodyTemp(star));
			builder.material(material);
			double density = material.estimateCompressedDensity(mass);
			builder.diameter(Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0));
			builder.planetaryClass(pClass);
			
			planet = builder.build();
			// Try to make the planet habitable (for humans) if possible ...
			-- habitableRetries;
		} while( habitableRetries >= 0 && !planet.habitable() );

		return planet;
	}
	
	public static Planet planet(Star star, double mass, String planetName, int habitableRetries) {
		PlanetBuilder builder = Planet.builder().parent(star).name(planetName).mass(mass).minor(false);
		Planet planet = null;
		do
		{
			double orbit = 0;
			float eccentrity = 0.0f;
			int errCount = 0;
			do {
				orbit = Math.pow(Math.min(star.random.nextDouble(), star.random.nextDouble()), 2.0) * (star.outerPlanetLimit - star.innerPlanetLimit) + star.innerPlanetLimit;
				eccentrity = (float)Math.pow(star.random.nextDouble(), 6.0) / 2.0f;
				// We try to keep the planets with less than about 50000 Yg weight in the "inside" of the frost zone,
				// and gas giants outside
				if( orbit > star.frostLine && mass < star.random.nextDouble() * Constant.MAX_TERRESTRIAL_MASS ) {
					orbit = Math.max(Math.min(orbit, star.random.nextDouble() * star.outerPlanetLimit), star.innerPlanetLimit);
				} else if( orbit < star.frostLine && mass * star.random.nextDouble() > Constant.MAX_TERRESTRIAL_MASS ) {
					orbit = Math.pow(Math.min(star.random.nextDouble(), star.random.nextDouble()), 1.5) * (star.outerPlanetLimit - star.frostLine) + star.frostLine;
				}
				if( !star.orbitFree(orbit, eccentrity) )
				{
					++ errCount;
					if( errCount > 20 )
					{
						// We give up on that one
						return null;
					}
				}
			} while( !star.orbitFree(orbit, eccentrity) );
			float rotationPeriod = (float)star.random.nextGaussian() * 60000 + 72000;
			// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
			if( orbit / Constant.AU < star.mass / 1e29 )
			{
				eccentrity *= (orbit / Constant.AU * 1e29 / star.mass);
			}
			// Rayleigh distribution, sigma = 1° (see arXiv:1207.5250 [astro-ph.EP])
			float inclination = (float)Math.toRadians(Math.sqrt(-2.0 * Math.log(star.random.nextDouble())));
			Orbit planetOrbit = new Orbit(orbit, eccentrity, inclination);
			Pair<Double, Double> radiusCompressibility = newPlanetRadiusCompressibility(star.random, mass, planetOrbit.orbitalZone(star));
			builder.orbit(planetOrbit)
					.rotationPeriod(rotationPeriod)
					.diameter(radiusCompressibility.first * 2)
					.material(new Material("", 200, radiusCompressibility.second));
					//.compressibility(radiusCompressibility.second);
			planet = builder.build();
			// Try to make the planet habitable (for humans) if possible ...
			-- habitableRetries;
		}
		while( habitableRetries >= 0 && !planet.habitable() );
		
		// Moon generation
		// Estimated amount of major moons
		double moonEstimate = 8.5 * Math.exp(-65000.0 / planet.mass * Constant.YOTTAGRAM) + planet.random.nextGaussian() * 0.4 * Math.pow(planet.mass / Constant.YOTTAGRAM, 0.135);
		// Lower the chances for small Hill radii
		if( planet.hillsRadius < 0.1 * Constant.AU )
		{
			moonEstimate *= Math.pow(planet.hillsRadius * 10.0 / Constant.AU, 0.4);
		}
		int majorMoons = (int)Math.min(moonEstimate, planet.hillsRadius);
		for( int m = 0; m < majorMoons; ++ m )
		{
			double moonMass = Math.pow(planet.random.nextDouble(), 12.0) * Math.min(planet.mass / 25.0, Constant.MAX_TERRESTRIAL_MASS * 2.0);
			if( moonMass < Constant.MIN_MOON_MASS )
			{
				moonMass = (planet.random.nextDouble() * 999.0 + 1.0 ) * Constant.MIN_MOON_MASS;
			}
			Pair<Double, Double> radiusCompressibility = newPlanetRadiusCompressibility(planet.random, moonMass, planet.orbit.orbitalZone(star));
			double moonRadius = radiusCompressibility.first;
			// Make sure we don't get too near to the Roche limit (rough estimate for fluid moon).
			// This is almost never more than 1.0 and practically never more than 2.0
			double rocheLimit = Math.max(1.0, Math.ceil(3.0 * moonRadius * Math.pow(planet.mass / moonMass, 1.0 / 3.0) / Constant.DISTANCE_UNIT));
			double moonOrbit = Math.pow(planet.random.nextDouble(), 3.0) * (planet.hillsRadius - rocheLimit) + rocheLimit;
			float moonRotationPeriod = (float)planet.random.nextGaussian() * 1000 + 1200;
			float moonEccentrity = (float)Math.pow(planet.random.nextDouble(), 6.0) / 1.01f;
			// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
			if( moonOrbit < planet.mass / (10000 * Constant.YOTTAGRAM) )
			{
				moonEccentrity *= (moonOrbit * 10000.0 * Constant.YOTTAGRAM / planet.mass);
			}
			Moon newMoon = Moon.builder().name(planetName + " " + GenUtil.romanNumber(m +1))
					.mass(moonMass)
					.orbit(new Orbit(moonOrbit, moonEccentrity, (float)Math.abs(planet.random.nextGaussian() / 6 / Math.PI)))
					.parent(planet)
					.rotationPeriod(moonRotationPeriod)
					.diameter(moonRadius * 2)
					.material(Material.ROCK)
					//.compressibility(radiusCompressibility.second * 2.0)
					.build();
			planet.moons.add(newMoon);
		}
		return planet;
	}
	
	public static Planet planet(Star star, double mass, String planetName)
	{
		return planet(star, mass, planetName, 0);
	}
	
	public static Planet newPlanetoid(Star star, double mass) {
		return newPlanetoid(star, mass, null);
	}
	
	public static Planet newPlanetoid(Star star, double mass, String name)
	{
		double orbit = 0.0;
		float eccentrity;
		int errCount = 0;
		do {
			orbit = star.random.nextDouble() * (star.outerPlanetLimit - star.boilingLine) + star.boilingLine;
			eccentrity = (float)Math.pow(star.random.nextDouble(), 2.5) / 1.01f;
			if( !star.orbitFree(orbit, eccentrity) )
			{
				++ errCount;
				if( errCount > 20 )
				{
					// We give up on that one
					return null;
				}
			}
		} while( !star.orbitFree(orbit, eccentrity) );
		float rotationPeriod = (float)star.random.nextGaussian() * 60000 + 72000;
		// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
		if( orbit / Constant.AU < star.mass / 1e29 )
		{
			eccentrity *= (orbit / Constant.AU * 1e29 / star.mass);
		}
		// Rayleigh distribution, sigma = 5°
		float inclination = (float)Math.toRadians(5.0 * Math.sqrt(-2.0 * Math.log(star.random.nextDouble())));
		Orbit planetoidOrbit = new Orbit(orbit, eccentrity, inclination);
		String planetoidName = null != name ? name : planetoidName(star.random);

		// Create planetary material
		PlanetaryClass pClass = newPlanetoidClass(star.random);
		while( !pClass.validTemperature(star, planetoidOrbit) ) {
			pClass = newPlanetoidClass(star.random);
		}
		
		Material material = pClass.newMaterial(star.random, planetoidOrbit.blackbodyTemp(star));
		double density = material.estimateCompressedDensity(mass);
		double diameter = Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0);

		Planet planet = Planet.builder()
				.parent(star)
				.name(planetoidName).mass(mass)
				.orbit(planetoidOrbit)
				.rotationPeriod(rotationPeriod)
				.diameter(diameter)
				.material(material)
				.planetaryClass(pClass)
				.minor(true).build();
		assert mass >= Constant.MIN_TERRESTRIAL_MASS || pClass.validClass(planet);
		return planet;
	}

	/**
	 * Calculate the planetary radius (excluding the atmosphere) depending on mass and orbital zone
	 */
	private static Pair<Double, Double> newPlanetRadiusCompressibility(Random rnd, double mass, OrbitalZone zone)
	{
		// Density in kg/m^3
		double density = 1200.0;
		double compressibility = 20.0e-12;
		do
		{
			switch(zone)
			{
				case HOT:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						while( density < 3800 )
						{
							// No icy or watery planets here
							density = rnd.nextGaussian() * 1000 + 5500;
						}
						compressibility = rnd.nextDouble() * rnd.nextDouble() * 30e-12 + 1e-12;
					}
					else
					{
						density = Math.abs(rnd.nextGaussian() * 1000 + 500);
						compressibility = rnd.nextDouble() * 900e-12 + 100e-12;
					}
					break;
				case HABITABLE:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						while( density < 3000 )
						{
							// No icy planets here
							density = rnd.nextGaussian() * 1000 + 5000;
						}
						compressibility = rnd.nextDouble() * rnd.nextDouble() * 40e-12 + 1e-12;
					}
					else
					{
						density =  Math.abs(rnd.nextGaussian() * 500 + 700);
						compressibility = rnd.nextDouble() * 900e-12 + 100e-12;
					}
					break;
				case COLD:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						density = rnd.nextGaussian() * 1200 + 4000;
						compressibility = rnd.nextDouble() * rnd.nextDouble() * 50e-12 + 1e-12;
					}
					else
					{
						density = rnd.nextGaussian() * 500 + 1000;
						compressibility = rnd.nextDouble() * 900e-12 + 100e-12;
					}
					break;
				case FROZEN:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						density = rnd.nextGaussian() * 700 + 3000;
						compressibility = rnd.nextDouble() * rnd.nextDouble() * 100e-12 + 1e-12;
					}
					else
					{
						density = rnd.nextGaussian() * 500 + 1300;
						compressibility = rnd.nextDouble() * 900e-12 + 100e-12;
					}
					break;
			}
		}
		while( density < 50.0 
				|| (mass <= Constant.MAX_TERRESTRIAL_MASS && density < 1200.0)
				|| (mass <= Constant.MAX_TERRESTRIAL_MASS * 11 && (density - 50.0) / 115.0 < 11.0 - mass / Constant.MAX_TERRESTRIAL_MASS ));
		return Pair.of(Math.pow(0.75 * mass / (Math.PI * density), 1.0 / 3.0), compressibility);
	}

	private static String firstPlanetoidPart[] = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"};
	private static String secondPlanetoidPart[] = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private static String planetoidName(Random rnd)
	{
		String name = "" + (Math.min(rnd.nextInt(990), rnd.nextInt(990)) + 2009);
		
		name = name + " " + firstPlanetoidPart[rnd.nextInt(24)] + secondPlanetoidPart[rnd.nextInt(25)];
		
		int number = rnd.nextInt(1000);
		if( number > 0 )
		{
			name = name + GenUtil.subscriptNumber(number);
		}
		
		return name;
	}
	
	private static PlanetaryClass newTerrestialClass(Random rnd) {
		int randomSC = rnd.nextInt(maxTerrestialClasses) + 1;
		return terrestialClassesDistribution.lowerEntry(randomSC).getValue();
	}
	
	private static final List<Pair<PlanetaryClass, Integer>> terrestialClassesList = new ArrayList<Pair<PlanetaryClass,Integer>>();
	private static final TreeMap<Integer, PlanetaryClass> terrestialClassesDistribution = new TreeMap<Integer, PlanetaryClass>();
	private static final int maxTerrestialClasses;

	static {
		// Weights for planetoid classes
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.DESERT, 10));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.EARTH_LIKE, 10));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GREENHOUSE, 10));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELL, 5));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.AIRLESS, 25));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.DRY_ROCK, 20));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.ROCKY, 10));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HIGH_PRESSURE, 5));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.EXTREME_GREENHOUSE, 2));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.WATER_ICE, 10));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.AMMONIA_ICE, 20));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.METHANE_ICE, 25));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.IRON, 5));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.LAVA, 2));
		terrestialClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.OCEAN, 5));

		int count = 0;
		for( Pair<PlanetaryClass, Integer> spectral : terrestialClassesList) {
			terrestialClassesDistribution.put(count, spectral.first);
			count += spectral.second;
		}
		maxTerrestialClasses = count;
	}
	
	private static PlanetaryClass newPlanetoidClass(Random rnd) {
		int randomSC = rnd.nextInt(maxPlanetoidClasses) + 1;
		return planetoidClassesDistribution.lowerEntry(randomSC).getValue();
	}
	
	private static final List<Pair<PlanetaryClass, Integer>> planetoidClassesList = new ArrayList<Pair<PlanetaryClass,Integer>>(5);
	private static final TreeMap<Integer, PlanetaryClass> planetoidClassesDistribution = new TreeMap<Integer, PlanetaryClass>();
	private static final int maxPlanetoidClasses;

	static {
		// Weights for planetoid classes
		planetoidClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.SILICATE_PLANETOID, 200));
		planetoidClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.METALLIC_PLANETOID, 10));
		planetoidClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.ICE_PLANETOID, 50));
		planetoidClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GRAVEL_PLANETOID, 5));
		planetoidClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.CARBONACEOUS_PLANETOID, 2));
		
		int count = 0;
		for( Pair<PlanetaryClass, Integer> spectral : planetoidClassesList) {
			planetoidClassesDistribution.put(count, spectral.first);
			count += spectral.second;
		}
		maxPlanetoidClasses = count;
	}
}
