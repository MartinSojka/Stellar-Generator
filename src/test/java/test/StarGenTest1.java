package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.vernideas.lib.stellargen.PlanetGenerator;
import de.vernideas.lib.stellargen.SystemGenerator;
import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Satellite;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.VectorD3D;
import de.vernideas.space.data.starclass.StarClassHelper;
import de.vernideas.space.data.starclass.Type;

public final class StarGenTest1 {

	public static void main(String[] args) {
		Universe u = new Universe(/*-5638973688361399781L*/);
		System.err.println("UNIVERSE SEED " + u.seed);
		Star s = null;
		do {
			s = SystemGenerator.star(u, "G6V", -5018178568867470810L);
		} while( s.starClass.type() != Type.G );
		
		s.name("Aquagea");

		printStarMM(s);
		
		//printStar(s, 1);
		List<Planet> planets = new ArrayList<Planet>(s.planets);
		//planets.addAll(s.planetoids);
		long pSeed = 0;
		if( s.planets.size() > 0 ) {
			pSeed = s.planets.get(0).seed();
		}
		Collections.sort(planets, Satellite.ORBITAL_COMPARATOR);
		int sysPos = 1;
		for( Planet p : planets )
		{
			printPlanetMM(p, sysPos);
			List<Moon> moons = new ArrayList<Moon>(p.moons);
			Collections.sort(moons, Satellite.ORBITAL_COMPARATOR);
			for( Moon m : moons ) {
				//printMoon(m);
			}
			++ sysPos;
		}
		
		System.exit(0);

		// Generate a bunch more to test for bugs and outliers
		for( int i = 2; i < 1; ++ i ) {
			printStar(SystemGenerator.star(u), i);
		}
		
		// Test for Earth
		
		/*
		Star sol = new Star("Sol", StarClassHelper.parse("G2V"));
		sol.diameter(Constant.SOLAR_DIAMETER);
		sol.luminosity(Constant.SOLAR_LUM).mass(Constant.SOLAR_MASS);
		sol.temperature(Constant.SOLAR_TEMPERATURE).position(new VectorD3D());
		*/
		/*
		Planet earth = Planet.builder().name("Earth").diameter(Constant.EARTH_DIAMETER).mass(Constant.EARTH_MASS).parent(sol)
				.rotationPeriod(24 * 3600 * 364.0f / 365.0f).orbit(new Orbit(Constant.AU, 0.0167086f, 0.0f))
				.material(new Material("", 4030, 7.64997739863382e-12)).build();
		Planet jupiter = Planet.builder().name("Jupiter").diameter(69911000 * 2.0).mass(1.898e+27).parent(sol)
				.rotationPeriod(24 * 3600 /* ignorable *//*).orbit(new Orbit(778547200000.0, 0.048775f, 0.0f))
				.material(new Material("", 1326, 1e-12)).build();
				*/
		/*
		printStar(sol, 2);
		//printPlanet(earth);
		//printPlanet(jupiter);
		Planet test = null;
		//Planet test = PlanetGenerator.newGasgiant(sol, "Test Giant", pSeed);
		//printPlanet(test);
		
		test = PlanetGenerator.newGasgiant(sol, "Test Planet", -7956472799291748583L);
		printPlanet(test, true);
		test = PlanetGenerator.newGasgiant(s, "Test Planet", -7956472799291748583L);
		printPlanet(test, true);
		*/

		System.exit(0);
	}
	
	private static void printStarMM(Star star) {
		System.out.println("<star>");
		System.out.println("<name>" + star.name() + "</name>");
		System.out.println("<spectralType>" + star.starClass.fullDeclaration() + "</spectralType>");
		System.out.println(String.format(Locale.ROOT, "<mass>%.3f</mass>", star.mass() / Constant.SOLAR_MASS ));
		System.out.println(String.format(Locale.ROOT, "<lum>%.6f</lum>", star.luminosity() / Constant.SOLAR_LUM ));
		System.out.println(String.format(Locale.ROOT, "<temperature>%.0f</temperature>", star.temperature() ));
		System.out.println(String.format(Locale.ROOT, "<radius>%.4f</radius>", star.diameter() / Constant.SOLAR_DIAMETER ));
		System.out.println("<planets>" + star.planets.size() + "</planets>");
		System.out.println("<minorPlanets>" + star.planetoids.size() + "</minorPlanets>");
		System.out.println("</star>");
	}

	private static void printStar(Star star, int i)
	{
		System.out.println("[" + String.format("%02d", i) + "] (" + star.seed() + ") " + star.name()
				+ " (" + star.starClass.fullDeclaration() + "), mass "
				+ String.format(Locale.ROOT, "%.3f", star.mass() / Constant.SOLAR_MASS)
				+ " M☉, "
				+ "planets " + star.planets.size() + ", planetoids " + star.planetoids.size()
				+ ", luminosity " + String.format(Locale.ROOT, "%f", star.luminosity() / Constant.SOLAR_LUM) + " L☉");
		System.out.println("     boiling line "
				+ String.format(Locale.ROOT, "%.4f", star.boilingLine() / Constant.AU) + " AU, inner planet limit "
				+ String.format(Locale.ROOT, "%.4f", star.innerPlanetLimit() / Constant.AU) + " AU, min habitable zone "
				+ String.format(Locale.ROOT, "%.4f", star.habitableZoneMin() / Constant.AU) + " AU, max habitable zone "
				+ String.format(Locale.ROOT, "%.4f", star.habitableZoneMax() / Constant.AU) + " AU, frost line "
				+ String.format(Locale.ROOT, "%.4f", star.frostLine() / Constant.AU) + " AU, outer planet limit "
				+ String.format(Locale.ROOT, "%.4f", star.outerPlanetLimit() / Constant.AU) + " AU");
		System.out.println("     blackbody temperatures "
				+ String.format(Locale.ROOT, "%.0f K, %.0f K, %.0f K, %.0f K, %.0f K, %.0f K",
					star.blackbodyTemp(star.boilingLine()), star.blackbodyTemp(star.innerPlanetLimit()),
					star.blackbodyTemp(star.habitableZoneMin()), star.blackbodyTemp(star.habitableZoneMax()),
					star.blackbodyTemp(star.frostLine()), star.blackbodyTemp(star.outerPlanetLimit())));
	}
	
	private static void printPlanet(Planet p) {
		printPlanet(p, false);
	}
	
	private static void printPlanetMM(Planet planet, int sysPos) {
		System.out.println("<planet>");
		System.out.println("<name>" + planet.name() + "</name>");
		System.out.println("<starId>" + planet.mainStar().name() + "</starId>");
		System.out.println("<sysPos>" + sysPos + "</sysPos>");
		System.out.println(String.format(Locale.ROOT, "<orbitRadius>%.4f</orbitRadius>", planet.orbit().radius / Constant.AU));
		System.out.println(String.format(Locale.ROOT, "<orbitEccentricity>%.4f</orbitEccentricity>", planet.orbit().eccentricity));
		System.out.println(String.format(Locale.ROOT, "<orbitInclination>%.2f</orbitInclination>", planet.orbit().inclination));
		System.out.println(String.format(Locale.ROOT, "<temperature>%.0f</temperature>", planet.blackbodyTemperature() - Constant.CELSIUS_ZERO));
		System.out.println("<satellites>" + planet.moons.size() + "</satellites>");
		for( Moon moon : planet.moons ) {
			System.out.println("<satellite>" + moon.name() + "</satellite>");
		}
		System.out.println(String.format(Locale.ROOT, "<mass>%.3f</mass>", planet.mass() / Constant.EARTH_MASS ));
		System.out.println(String.format(Locale.ROOT, "<radius>%.3f</radius>", planet.diameter() / Constant.EARTH_DIAMETER ));
		System.out.println(String.format(Locale.ROOT, "<density>%.3f</density>", planet.density()));
		System.out.println(String.format(Locale.ROOT, "<gravity>%.3f</gravity>", planet.surfaceGravity() / Constant.EARTH_SURFACE_GRAVITY));
		System.out.println(String.format(Locale.ROOT, "<dayLength>%.2f</dayLength>", planet.dayLength() / 3600));
		System.out.println("<class>" + planet.planetaryClass().name + "</class>");
		System.out.println("</planet>");
	}
	
	private static void printPlanet(Planet p, boolean printMoons) {
		System.out.println("     (" + p.seed() + ") " + p.name() + " [" + p.planetaryClass().name + "]"
				+ ", mass: " + String.format(Locale.ROOT, "%.3f Yg [gas giant limit %.3f Yg]", p.mass() / Constant.YOTTAGRAM, p.criticalMass() / Constant.YOTTAGRAM)
				+ ", exclusion zone "
				+ String.format(Locale.ROOT, "%.3f", p.exclusionZone() / Constant.AU)
				+ " AU, blackbody temp. " + String.format(Locale.ROOT, "%.1f", p.blackbodyTemperature()) + " K");
		System.out.println("       orbit " + p.orbit().printablePlanetString() + ", S-L param "
				+ String.format(Locale.ROOT, "%.1f", ((Star)p.parent()).sternLevisonParameter(p.mass(), p.orbit().radius))
				+ String.format(Locale.ROOT, ", hills radius %.3f AU", p.hillsRadius() / Constant.AU));
		System.out.println("       diameter (Earth) "
				+ String.format(Locale.ROOT, "%.2f", p.diameter() / 6371000.8 / 2) + ", gravity: "
				+ String.format(Locale.ROOT, "%.3f", p.surfaceGravity())
				+ " m/s², density: " + String.format(Locale.ROOT, "%.0f [%.0f]", p.density(), p.uncompressedDensity())
				+ " kg/m³, escape V: " + String.format(Locale.ROOT, "%.0f", p.escapeVelocity())
				+ " m/s, mol. limit: " + String.format(Locale.ROOT, "%.2f", p.molecularLimit()));
		System.out.println("       core pressure (Earth) " + String.format(Locale.ROOT, "%.2f GPa", p.corePressure(13300, 2600) / 1e9)
				+ " compressibility: " + String.format(Locale.ROOT, "%.2f TPa^-1", p.compressibility() * 1e12));
		System.out.println("       rotational period: "
				+ String.format(Locale.ROOT, "%.2f",  p.rotationPeriod() / 3600.0) + " hours, day length: "
				+ String.format(Locale.ROOT, "%.2f",  p.dayLength() / 3600.0) + " hours, year length: "
				+ String.format(Locale.ROOT, "%.2f", p.siderealPeriod() / 90000.0) + " galactic days, moons: "
				+ p.moons.size());
		if( printMoons ) {
			List<Moon> moons = new ArrayList<Moon>(p.moons);
			Collections.sort(moons, Satellite.ORBITAL_COMPARATOR);
			for( Moon m : moons ) {
				printMoon(m);
			}
		}
	}
	
	private static void printMoon(Moon m)
	{
		System.out.println("         " + m.name() + ", type: " + m.planetaryClass().name
				+ ", mass: " + String.format("%.3f", m.mass() / Constant.YOTTAGRAM)
				+ " Yg, radius (Earth radii) " + String.format("%.2f", m.diameter() / Constant.EARTH_DIAMETER)
				+ ", gravity: " + String.format("%.3f", m.surfaceGravity())
				+ " m/s²\n"
				+"           density: " + String.format("%.0f", m.density()) + " kg/m³, orbit " + m.orbit().printableMoonString()
				+ ", escape V: " + String.format("%.0f", m.escapeVelocity())
				+ " m/s, mol. limit: " + String.format("%.2f", m.molecularLimit()));
	}

}
