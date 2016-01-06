package de.vernideas.lib.stellargen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Material;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.Pair;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Satellite;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.planetaryclass.PlanetaryClass;
import lombok.NonNull;

public final class PlanetGenerator {	
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
				planet.moons.add(newMoon((Star)planet.parent(), planet, 
						(rnd) -> GenUtil.lerp(Constant.MIN_MOON_MASS, maxMass, Math.pow(rnd.nextDouble(), 9.0)),
						null));
			}
		}
	}
	
	// TODO: Rewrite this so it's using the planet's random instance, and get the orbital radius from
	// generating a random blackbody temperature first.
	// Max temperature (boiling line): 3200 K
	// Min temperature (outer planet limit): 0 K
	//   (it actually "only" goes down to 23 K for M-class, 18 K for L-class and 6 K for T-class stars)
	
	/**
	 * This method tries <i>once</i> and returns null if it failed for the given star.
	 * This lets the generator retry with a new planetary seed.
	 * 
	 * @param planet
	 * @param star
	 * @param filter
	 * @param validator
	 * @param inclinationMult
	 * @return
	 */
	public static Orbit newPlanetaryOrbit(@NonNull Planet planet, @NonNull Star star, OrbitFilter filter, @NonNull OrbitValidator validator, double inclinationMult) {
		double blackbodyTemp = GenUtil.lerp(3200.0, 0.0, Math.pow(Math.min(planet.random().nextDouble(), planet.random().nextDouble()), 0.25));
		double orbit = star.distanceForTemperature(blackbodyTemp);
		double eccentrity = Math.pow(planet.random().nextDouble(), 6.0) / 2.0;
		// Flatten out the eccentrity for low-lying planetary orbits (below 1.99 AU for the Sun)
		if( orbit / Constant.AU < star.mass() / 1e29 ) {
			eccentrity *= (orbit / Constant.AU * 1e29 / star.mass());
		}
		// Filter if needed
		if( null != filter ) {
			orbit = filter.filter(orbit);
		}
		if( validator.validate(orbit, eccentrity) ) {
			double inclination = Math.toRadians(inclinationMult * Math.sqrt(-2.0 * Math.log(planet.random().nextDouble())));
			return new Orbit(orbit, eccentrity, inclination);
		}
		return null;
	}
	
	/**
	 * Reset the planet randomiser with a new seed, and apply all data that's generatable without
	 * potential failure.
	 * 
	 * @param planet
	 */
	private static void seedPlanet(Planet planet, String name) {
		planet.seed(planet.seed() + 13377331L);
		planet.name(name);
		planet.rotationPeriod(planet.random().nextGaussian() * 60000 + 72000);
	}
	
	private static void decorateTerrestialPlanet(Planet planet, double mass, Star star, PlanetaryClass pClass, Orbit planetaryOrbit) {
		planet.mass(mass);
		planet.orbit(star, planetaryOrbit);

		Material material = pClass.newMaterial(planet.random(), planetaryOrbit.blackbodyTemp(star));
		planet.material(material);
		double density = material.estimateCompressedDensity(mass);
		planet.diameter(Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0));
		planet.planetaryClass(pClass);
		
		generateMoons(planet);
	}

	/** Try to generate a new terrestial planet */
	public static Planet newTerrestialPlanet(Star star, String name, double minMass, double maxMass) {
		Planet planet = new Planet(name, false);
		planet.seed(star.seed() + 47L * star.random().nextInt());
		
		int retriesLeft = 100;
		
		do {
			seedPlanet(planet, name);
			double mass = Satellite.newMass(planet.random());
			Orbit planetaryOrbit = newPlanetaryOrbit(planet, star,
					(orbit) -> {
						if( orbit > star.frostLine() && mass < planet.random().nextDouble() * Constant.MAX_TERRESTRIAL_MASS ) {
							return Math.max(Math.min(orbit, planet.random().nextDouble() * star.outerPlanetLimit()), star.innerPlanetLimit());
						} else {
							return orbit;
						}
					},
					(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity)
						&& star.sternLevisonParameter(mass, orbit) >= 100.0, 1.0);
			PlanetaryClass pClass = newTerrestialClass(planet.random());
			if( pClass.validTemperature(star, planetaryOrbit) && mass >= minMass && mass <= maxMass ) {
				decorateTerrestialPlanet(planet, mass, star, pClass, planetaryOrbit);
				return planet;
			}
			-- retriesLeft;
		} while( retriesLeft > 0 );
		return null;
	}
	
	public static Planet newTerrestialPlanet(Star star, long seed) {
		return newTerrestialPlanet(star, (String)null, seed);
	}

	public static Planet newTerrestialPlanet(Star star, String name, long seed) {
		Planet planet = new Planet(name, false);
		planet.seed(seed);
		planet.name(name);
		planet.rotationPeriod(planet.random().nextGaussian() * 60000 + 72000);
		double mass = Satellite.newMass(planet.random());
		Orbit planetaryOrbit = newPlanetaryOrbit(planet, star,
				(orbit) -> {
					if( orbit > star.frostLine() && mass < planet.random().nextDouble() * Constant.MAX_TERRESTRIAL_MASS ) {
						return Math.max(Math.min(orbit, planet.random().nextDouble() * star.outerPlanetLimit()), star.innerPlanetLimit());
					} else {
						return orbit;
					}
				},
				(orbit, eccentrity) -> true, 1.0);
		PlanetaryClass pClass = newTerrestialClass(planet.random());
		decorateTerrestialPlanet(planet, mass, star, pClass, planetaryOrbit);
		return planet;
	}
	
	private static void decorateGasgiant(Planet planet, double mass, Star star, PlanetaryClass pClass, Orbit planetaryOrbit) {
		planet.mass(mass);
		planet.orbit(star, planetaryOrbit);

		// Create planetary material
		Material material = pClass.newMaterial(planet.random(), planetaryOrbit.blackbodyTemp(star));
		planet.material(material);
		// We need a proper estimate for gas giants here
		// double density = material.estimateCompressedDensity(mass);
		planet.diameter(Math.pow(6 * mass / (Math.PI * material.uncompressedDensity), 1.0 / 3.0));
		planet.planetaryClass(pClass);

		generateMoons(planet);
	}

	/** Try to generate a new gas giant */
	public static Planet newGasgiant(Star star, String name, double minMass, double maxMass) {
		Planet planet = new Planet(name, false);
		planet.seed(star.seed() + 47L * star.random().nextInt());
		
		int retriesLeft = 100;
		
		do {
			seedPlanet(planet, name);
			double mass = Satellite.newMass(planet.random());
			Orbit planetaryOrbit = newPlanetaryOrbit(planet, star,
					(orbit) -> {
						if( orbit < star.frostLine() && mass * planet.random().nextDouble() > Constant.MAX_TERRESTRIAL_MASS ) {
							return GenUtil.lerp(star.frostLine(), star.outerPlanetLimit(), Math.pow(Math.min(planet.random().nextDouble(), planet.random().nextDouble()), 1.5));
						} else {
							return orbit;
						}
					},
					(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity)
						&& star.sternLevisonParameter(mass, orbit) >= 100.0, 1.0);
			PlanetaryClass pClass = newGasgiantClass(planet.random());
			if( pClass.validTemperature(star, planetaryOrbit) && mass >= minMass && mass <= maxMass ) {
				decorateGasgiant(planet, mass, star, pClass, planetaryOrbit);
				return planet;
			}
			-- retriesLeft;
		} while( retriesLeft > 0 );
		return null;
	}
	
	public static Planet newGasgiant(Star star, long seed) {
		return newGasgiant(star, (String)null, seed);
	}

	public static Planet newGasgiant(Star star, String name, long seed) {
		Planet planet = new Planet(name, false);
		planet.seed(seed);
		planet.name(name);
		planet.rotationPeriod(planet.random().nextGaussian() * 60000 + 72000);
		double mass = Satellite.newMass(planet.random());
		Orbit planetaryOrbit = newPlanetaryOrbit(planet, star,
				(orbit) -> {
					if( orbit < star.frostLine() && mass * planet.random().nextDouble() > Constant.MAX_TERRESTRIAL_MASS ) {
						return GenUtil.lerp(star.frostLine(), star.outerPlanetLimit(), Math.pow(Math.min(planet.random().nextDouble(), planet.random().nextDouble()), 1.5));
					} else {
						return orbit;
					}
				},
				(orbit, eccentrity) -> true, 1.0);
		PlanetaryClass pClass = newGasgiantClass(planet.random());
		decorateGasgiant(planet, mass, star, pClass, planetaryOrbit);
		return planet;
	}
	
	private static final RealDistribution moonDistribution = new BetaDistribution(3.0, 9.0);
	
	public static Moon newMoon(Star star, Planet planet, Function<Random, Double> massGenerator, String name) {
		if( null == name ) {
			name = planet.name() + " " + GenUtil.romanNumber(planet.moons.size() + 1);
		}
		Moon moon = new Moon(name);
		
		// Pick planetary model
		moon.seed(planet.seed() + planet.random().nextInt());
		double mass = massGenerator.apply(moon.random());
		moon.mass(mass);
		boolean minor = mass < Constant.MIN_TERRESTRIAL_MASS;
		PlanetaryClass pClass = minor ? newPlanetoidClass(moon.random()) : newTerrestialClass(moon.random());
		while( !pClass.validTemperature(star, planet.orbit()) ) {
			// Try with a different seed
			moon.seed(moon.seed() + 1337);
			mass = massGenerator.apply(moon.random());
			moon.mass(mass);
			minor = mass < Constant.MIN_TERRESTRIAL_MASS;
			pClass = minor ? newPlanetoidClass(moon.random()) : newTerrestialClass(moon.random());
		}

		// Create planetary material
		Material material = pClass.newMaterial(moon.random(), planet.orbit().blackbodyTemp(star));
		double density = material.estimateCompressedDensity(mass);
		double diameter = Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0);
		
		// Make sure we don't get too near to the Roche limit (rough estimate for fluid moon).
		// This is almost never more than 1.0 and practically never more than 2.0
		double rocheLimit = Math.max(planet.diameter() * 0.55, Constant.ROCHE_LIMIT_RIGID * diameter / 2.0 * Math.pow(planet.mass() / mass, 1.0 / 3.0));
		// beta distribution with a=3, b=9 between the Roche limit and Hill's radius
		double orbit = GenUtil.lerp(rocheLimit, planet.hillsRadius(), moonDistribution.inverseCumulativeProbability(planet.random().nextDouble()));
		double rotationPeriod = moon.random().nextGaussian() * 1000 + 1200;
		double eccentrity = Math.pow(planet.random().nextDouble(), 6.0) / 1.01;
		// Limit eccentrity for anything which would dip below the Roche limit
		eccentrity = Math.min(eccentrity, 1.0 - rocheLimit / orbit);
		// Flatten out the eccentrity for low-lying orbits (below 1.99 AU for the Sun)
		if( orbit < planet.mass() / (10000 * Constant.YOTTAGRAM) )
		{
			eccentrity *= (orbit * 10000.0 * Constant.YOTTAGRAM / planet.mass());
		}
		
		moon.orbit(planet, new Orbit(orbit, eccentrity, Math.abs(planet.random().nextGaussian() / 6 / Math.PI)));
		moon.rotationPeriod(rotationPeriod);
		moon.diameter(diameter);
		moon.material(material);
		moon.planetaryClass(pClass);
		
		return moon;
	}
	
	/**
	 * Reset the randomiser with a new seed, and apply all data that's generatable without
	 * potential failure.
	 * 
	 * @param planetoid
	 */
	private static void seedPlanetoid(Planet planetoid, String name) {
		planetoid.seed(planetoid.seed() + 27331L);
		planetoid.name(null != name ? name : planetoidName(planetoid.random()));
		planetoid.explicitName(true);
		planetoid.rotationPeriod(planetoid.random().nextGaussian() * 60000 + 72000);
	}
	
	/**
	 * Add remaining data to the planetoid after we got one with the right orbital params
	 * and planetary class
	 * 
	 * @param planetoid
	 */
	private static void decoratePlanetoid(Planet planet, double mass, Star star, PlanetaryClass pClass, Orbit planetoidOrbit) {
		planet.mass(mass);
		
		Material material = pClass.newMaterial(planet.random(), planetoidOrbit.blackbodyTemp(star));
		double density = material.estimateCompressedDensity(mass);
		double diameter = Math.pow(6 * mass / (Math.PI * density), 1.0 / 3.0);

		planet.orbit(star, planetoidOrbit);
		planet.diameter(diameter);
		planet.material(material);
		planet.planetaryClass(pClass);
		
		assert mass >= Constant.MIN_TERRESTRIAL_MASS || pClass.validClass(planet);
		
		generateMoons(planet);
	}
	
	private static Function<Random, Double> DEFAULT_PLANETOID_MASSGENERATOR =
			(rnd) -> GenUtil.lerp(Constant.MIN_TERRESTRIAL_MASS / 1000, Constant.MIN_TERRESTRIAL_MASS * 10,
					Math.pow(Math.min(rnd.nextDouble(), rnd.nextDouble()), 6.0));
	
	public static Planet newPlanetoid(Star star, double maxMass) {
		return newPlanetoid(star, maxMass, (String)null);
	}
	
	public static Planet newPlanetoid(Star star, double maxMass, String name)
	{
		Function<Random, Double> massGenerator = DEFAULT_PLANETOID_MASSGENERATOR;
		Planet planet = new Planet(null, true);
		planet.seed(star.seed() + 47L * star.random().nextInt());
		
		// Trying to get a free orbit and a valid planetoid class for it
		int orbitRetriesLeft = 100;
		
		// Initial data
		do {
			seedPlanetoid(planet, name);
			
			double mass = massGenerator.apply(planet.random());
			Orbit planetoidOrbit = newPlanetaryOrbit(planet, star, (orbit) -> orbit * 30,
					(orbit, eccentrity) -> star.orbitFree(orbit, eccentrity, 2.0)
					&& star.sternLevisonParameter(mass, orbit) <= 0.01, 5.0);
			PlanetaryClass pClass = newPlanetoidClass(planet.random());
			
			if( pClass.validTemperature(star, planetoidOrbit) && mass <= maxMass ) {
				decoratePlanetoid(planet, mass, star, pClass, planetoidOrbit);
				return planet;
			}
			-- orbitRetriesLeft;
		} while( orbitRetriesLeft > 0 );
		
		return null;
	}

	public static Planet newPlanetoid(Star star, long seed) {
		return newPlanetoid(star, (String)null, seed);
	}
	
	public static Planet newPlanetoid(Star star, String name, long seed) {
		Function<Random, Double> massGenerator = DEFAULT_PLANETOID_MASSGENERATOR;
		Planet planet = new Planet(null, true);
		planet.seed(seed);
		planet.name(null != name ? name : planetoidName(planet.random()));
		planet.explicitName(true);
		planet.rotationPeriod(planet.random().nextGaussian() * 60000 + 72000);
		double mass = massGenerator.apply(planet.random());
		Orbit planetoidOrbit = newPlanetaryOrbit(planet, star, (orbit) -> orbit * 30,
				(orbit, eccentrity) -> true, 5.0);
		PlanetaryClass pClass = newPlanetoidClass(planet.random());
		decoratePlanetoid(planet, mass, star, pClass, planetoidOrbit);
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
