package de.vernideas.lib.stellargen;

import java.util.Random;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Planet.PlanetBuilder;
import de.vernideas.space.data.Star;

public final class PlanetGenerator {
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
			builder.orbit(new Orbit(orbit, eccentrity, inclination))
					.rotationPeriod(rotationPeriod)
					.diameter(planetRadius(star.random, mass, orbitalZone(star, orbit)) * 2);
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
			double moonRadius = planetRadius(planet.random, moonMass, orbitalZone(star, planet.orbit.radius));
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
					.build();
			planet.moons.add(newMoon);
		}
		return planet;
	}
	
	public static Planet planet(Star star, double mass, String planetName)
	{
		return planet(star, mass, planetName, 0);
	}
	
	public static Planet planetoid(Star star, double mass)
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
		String planetoidName = planetoidName(star.random);
		Planet planet = Planet.builder()
				.parent(star)
				.name(planetoidName).mass(mass)
				.orbit(new Orbit(orbit, eccentrity, inclination))
				.rotationPeriod(rotationPeriod)
				.diameter(planetRadius(star.random, mass, orbitalZone(star, orbit)) * 2)
				.minor(true).build();
		return planet;
	}

	private static OrbitalZone orbitalZone(Star star, double orbit)
	{
		if( orbit < star.habitableZoneMin ) { return OrbitalZone.HOT; }
		if( orbit < star.habitableZoneMax ) { return OrbitalZone.HABITABLE; }
		if( orbit < star.frostLine ) { return OrbitalZone.COLD; }
		return OrbitalZone.FROZEN;
	}
	
	/**
	 * Calculate the planetary radius (excluding the atmosphere) depending on mass and orbital zone
	 */
	private static double planetRadius(Random rnd, double mass, OrbitalZone zone)
	{
		// Density in kg/m^3
		double density = 1200.0;
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
					}
					else
					{
						density = Math.abs(rnd.nextGaussian() * 1000 + 500);
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
					}
					else
					{
						density =  Math.abs(rnd.nextGaussian() * 500 + 700);
					}
					break;
				case COLD:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						density = rnd.nextGaussian() * 1200 + 4000;
					}
					else
					{
						density = rnd.nextGaussian() * 500 + 1000;
					}
					break;
				case FROZEN:
					if( mass < Constant.MAX_TERRESTRIAL_MASS )
					{
						density = rnd.nextGaussian() * 700 + 3000;
					}
					else
					{
						density = rnd.nextGaussian() * 500 + 1300;
					}
					break;
			}
		}
		while( density < 50.0 
				|| (mass <= Constant.MAX_TERRESTRIAL_MASS && density < 1200.0)
				|| (mass <= Constant.MAX_TERRESTRIAL_MASS * 11 && (density - 50.0) / 115.0 < 11.0 - mass / Constant.MAX_TERRESTRIAL_MASS ));
		return Math.pow(0.75 * mass / (Math.PI * density), 1.0 / 3.0);
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
	
	private static enum OrbitalZone
	{
		HOT, HABITABLE, COLD, FROZEN
	}
}
