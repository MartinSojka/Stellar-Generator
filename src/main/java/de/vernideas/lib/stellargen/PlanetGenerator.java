package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Material;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Planet.PlanetBuilder;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.planetaryclass.PlanetaryClass;
import lombok.NonNull;

public final class PlanetGenerator {
	public static Planet newTerrestialPlanet(Star star, double mass, String name) {
		return newTerrestialPlanet(star, mass, name, 0);
	}
	
	private static void generateMoons(Planet planet) {
		if( planet.mass() / 25.0 > Constant.MIN_MOON_MASS ) {
			double moonEstimate = 8.5 * Math.exp(-65000.0 / planet.mass() * Constant.YOTTAGRAM)
					+ planet.random().nextGaussian() * 0.5 * Math.pow(planet.mass() / Constant.YOTTAGRAM, 0.135);
			// Lower the chances for small Hill radii
			if( planet.hillsRadius() < 0.01 * Constant.AU ) {
				moonEstimate *= Math.pow(planet.hillsRadius() * 100.0 / Constant.AU, 0.4);
			}
			int majorMoons = Long.valueOf(Math.round(Math.min(moonEstimate, planet.hillsRadius() * 1000.0 / Constant.AU))).intValue();
			double maxMass = Math.min(planet.mass() / 25.0, Constant.MAX_TERRESTRIAL_MASS * 2.0);
			for( int m = 0; m < majorMoons; ++ m )
			{
				double moonMass = GenUtil.lerp(Constant.MIN_MOON_MASS, maxMass, Math.pow(planet.random().nextDouble(), 9.0));
				planet.moons.add(newMoon((Star)planet.parent(), planet, moonMass, planet.name + " " + GenUtil.romanNumber(m + 1)));
			}
		}
	}
	
	private static Orbit newPlanetaryOrbit(@NonNull Star star, OrbitFilter filter, @NonNull OrbitValidator validator, double inclinationMult) {
		int tryCount = 0;
		double orbit = 0.0;
		double eccentrity = 0.0;
		do {
			++ tryCount;
			if( tryCount > 20 ) {
				// We give up on that one
				return null;
			}
			orbit = GenUtil.lerp(star.innerPlanetLimit(), star.outerPlanetLimit(), Math.pow(Math.min(star.random().nextDouble(), star.random().nextDouble()), 2.0));
			eccentrity = Math.pow(star.random().nextDouble(), 6.0) / 2.0;
			// Flatten out the eccentrity for low-lying planetary orbits (below 1.99 AU for the Sun)
			if( orbit / Constant.AU < star.mass() / 1e29 ) {
				eccentrity *= (orbit / Constant.AU * 1e29 / star.mass());
			}
			// Filter if needed
			if( null != filter ) {
				orbit = filter.filter(orbit);
			}
		} while( !validator.validate(orbit, eccentrity) );
		// Rayleigh distribution, sigma = 1° * multiplier (see arXiv:1207.5250 [astro-ph.EP])
		double inclination = Math.toRadians(inclinationMult * Math.sqrt(-2.0 * Math.log(star.random().nextDouble())));
		return new Orbit(orbit, eccentrity, inclination);
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
			Orbit planetaryOrbit = newPlanetaryOrbit(star,
					(orbit) -> {
						if( orbit > star.frostLine() && mass < star.random().nextDouble() * Constant.MAX_TERRESTRIAL_MASS ) {
							return Math.max(Math.min(orbit, star.random().nextDouble() * star.outerPlanetLimit()), star.innerPlanetLimit());
						} else {
							return orbit;
						}
					},
					(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity) && star.sternLevisonParameter(mass, orbit) >= 100.0, 1.0);
			if( null == planetaryOrbit ) {
				return null;
			}
			double rotationPeriod = star.random().nextGaussian() * 60000 + 72000;
			
			builder.orbit(planetaryOrbit).rotationPeriod(rotationPeriod);

			// Pick planetary model
			PlanetaryClass pClass = newTerrestialClass(star.random());
			while( !pClass.validTemperature(star, planetaryOrbit) ) {
				pClass = newTerrestialClass(star.random());
			}

			// Create planetary material
			Material material = pClass.newMaterial(star.random(), planetaryOrbit.blackbodyTemp(star));
			builder.material(material);
			double density = material.estimateCompressedDensity(mass);
			builder.diameter(Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0));
			builder.planetaryClass(pClass);
			
			planet = builder.build();
			// Try to make the planet habitable (for humans) if possible ...
			-- habitableRetries;
		} while( habitableRetries >= 0 && !planet.habitable() );

		generateMoons(planet);

		return planet;
	}
	
	public static Planet newGasgiant(Star star, double mass, String planetName) {
		PlanetBuilder builder = Planet.builder().parent(star).name(planetName).mass(mass).minor(false);
		Planet planet = null;
		
		// Trying to get a free orbit
		Orbit planetaryOrbit = newPlanetaryOrbit(star,
				(orbit) -> {
					if( orbit < star.frostLine() && mass * star.random().nextDouble() > Constant.MAX_TERRESTRIAL_MASS ) {
						return GenUtil.lerp(star.frostLine(), star.outerPlanetLimit(), Math.pow(Math.min(star.random().nextDouble(), star.random().nextDouble()), 1.5));
					} else {
						return orbit;
					}
				},
				(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity) && star.sternLevisonParameter(mass, orbit) >= 100.0, 1.0);
		if( null == planetaryOrbit ) {
			return null;
		}
		double rotationPeriod = star.random().nextGaussian() * 60000 + 72000;
		
		builder.orbit(planetaryOrbit).rotationPeriod(rotationPeriod);

		// Pick planetary model
		PlanetaryClass pClass = newGasgiantClass(star.random());
		while( !pClass.validTemperature(star, planetaryOrbit) ) {
			pClass = newGasgiantClass(star.random());
		}

		// Create planetary material
		Material material = pClass.newMaterial(star.random(), planetaryOrbit.blackbodyTemp(star));
		builder.material(material);
		// We need a proper estimate for gas giants here
		// double density = material.estimateCompressedDensity(mass);
		builder.diameter(Math.pow(6 * mass / (Math.PI * material.uncompressedDensity), 1.0 / 3.0));
		builder.planetaryClass(pClass);

		planet = builder.build();
		
		generateMoons(planet);
		
		return planet;
	}
	
	private static final RealDistribution moonDistribution = new BetaDistribution(3.0, 9.0);
	
	public static Moon newMoon(Star star, Planet planet, double mass, String name) {
		// Pick planetary model
		boolean minor = mass < Constant.MIN_TERRESTRIAL_MASS;
		PlanetaryClass pClass = minor ? newPlanetoidClass(planet.random()) : newTerrestialClass(planet.random());
		while( !pClass.validTemperature(star, planet.orbit()) ) {
			pClass = minor ? newPlanetoidClass(planet.random()) : newTerrestialClass(planet.random());
		}

		// Create planetary material
		Material material = pClass.newMaterial(planet.random(), planet.orbit().blackbodyTemp(star));
		double density = material.estimateCompressedDensity(mass);
		double diameter = Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0);
		
		// Make sure we don't get too near to the Roche limit (rough estimate for fluid moon).
		// This is almost never more than 1.0 and practically never more than 2.0
		double rocheLimit = Math.max(planet.diameter() * 0.55, Constant.ROCHE_LIMIT_RIGID * diameter / 2.0 * Math.pow(planet.mass() / mass, 1.0 / 3.0));
		// beta distribution with a=3, b=9 between the Roche limit and Hill's radius
		double orbit = GenUtil.lerp(rocheLimit, planet.hillsRadius(), moonDistribution.inverseCumulativeProbability(planet.random().nextDouble()));
		double rotationPeriod = planet.random().nextGaussian() * 1000 + 1200;
		double eccentrity = Math.pow(planet.random().nextDouble(), 6.0) / 1.01;
		// Limit eccentrity for anything which would dip below the Roche limit
		eccentrity = Math.min(eccentrity, 1.0 - rocheLimit / orbit);
		// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
		if( orbit < planet.mass() / (10000 * Constant.YOTTAGRAM) )
		{
			eccentrity *= (orbit * 10000.0 * Constant.YOTTAGRAM / planet.mass());
		}
		Moon newMoon = Moon.builder().name(name)
				.mass(mass)
				.orbit(new Orbit(orbit, eccentrity, Math.abs(planet.random().nextGaussian() / 6 / Math.PI)))
				.parent(planet)
				.rotationPeriod(rotationPeriod)
				.diameter(diameter)
				.material(material)
				.planetaryClass(pClass)
				.build();
		return newMoon;
	}
	
	public static Planet newPlanetoid(Star star, double mass) {
		return newPlanetoid(star, mass, null);
	}
	
	public static Planet newPlanetoid(Star star, double mass, String name)
	{
		// Trying to get a free orbit
		Orbit planetoidOrbit = newPlanetaryOrbit(star, null,
				(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity) && star.sternLevisonParameter(mass, orbit) <= 0.01, 5.0);
		if( null == planetoidOrbit ) {
			return null;
		}
		double rotationPeriod = star.random().nextGaussian() * 60000 + 72000;
		String planetoidName = null != name ? name : planetoidName(star.random());

		// Create planetary material
		PlanetaryClass pClass = newPlanetoidClass(star.random());
		while( !pClass.validTemperature(star, planetoidOrbit) ) {
			pClass = newPlanetoidClass(star.random());
		}
		
		Material material = pClass.newMaterial(star.random(), planetoidOrbit.blackbodyTemp(star));
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
		
		generateMoons(planet);
		
		return planet;
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
	
	private static PlanetaryClass newGasgiantClass(Random rnd) {
		int randomSC = rnd.nextInt(maxGasgiantClasses) + 1;
		return gasgiantClassesDistribution.lowerEntry(randomSC).getValue();
	}
	
	private static final List<Pair<PlanetaryClass, Integer>> gasgiantClassesList = new ArrayList<Pair<PlanetaryClass,Integer>>(5);
	private static final TreeMap<Integer, PlanetaryClass> gasgiantClassesDistribution = new TreeMap<Integer, PlanetaryClass>();
	private static final int maxGasgiantClasses;

	static {
		// Weights for gas giant classes
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_GIANT_I, 30));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_GIANT_II, 20));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_GIANT_III, 10));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_GIANT_IV, 5));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_GIANT_V, 2));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GIANT_I, 20));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GIANT_II, 10));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GIANT_III, 5));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GIANT_IV, 2));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GIANT_V, 1));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.ICE_GIANT, 40));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_ICE_GIANT, 20));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HOT_PUFFY_GIANT, 7));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.COLD_PUFFY_GIANT, 4));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_COLD_PUFFY_GIANT, 2));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.GAS_DWARF, 50));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.HELLIUM_GAS_DWARF, 20));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.FROZEN_GAS_DWARF, 15));
		gasgiantClassesList.add(Pair.<PlanetaryClass, Integer>of(PlanetaryClass.BOILING_GIANT, 1));

		int count = 0;
		for( Pair<PlanetaryClass, Integer> spectral : gasgiantClassesList) {
			gasgiantClassesDistribution.put(count, spectral.first);
			count += spectral.second;
		}
		maxGasgiantClasses = count;
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
	
	@FunctionalInterface
	private static interface OrbitFilter {
		public double filter(double orbit);
	}

	@FunctionalInterface
	private static interface OrbitValidator {
		public boolean validate(double orbit, double eccentrity);
	}
}
