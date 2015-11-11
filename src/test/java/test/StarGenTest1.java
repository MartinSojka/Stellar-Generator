package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.vernideas.lib.stellargen.SystemGenerator;
import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;
import de.vernideas.space.data.VectorI3D;
import de.vernideas.space.data.starclass.StarClassHelper;

public final class StarGenTest1 {

	public static void main(String[] args) {
		Universe u = new Universe(-6168985105448355243L);
		System.err.println("UNIVERSE SEED " + u.seed);
		Star s = SystemGenerator.star(u);

		printStar(s, 1);
		List<Planet> planets = new ArrayList<Planet>(s.planets);
		//planets.addAll(s.planetoids);
		Collections.sort(planets, new Comparator<Planet>(){
			@Override public int compare(Planet o1, Planet o2) {
				return (o1.orbit.radius < o2.orbit.radius ? -1 : o1.orbit.radius > o2.orbit.radius ? 1 : 0);
			}
		});
		for( Planet p : planets )
		{
			printPlanet(p);
		}
		
		// Test for Earth
		
		Star sol = Star.builder().name("Sol").diameter(Constant.SOLAR_DIAMETER).luminosity(Constant.SOLAR_LUM).mass(Constant.SOLAR_MASS)
				.temperature(Constant.SOLAR_TEMPERATURE).starClass(StarClassHelper.parse("G2V")).position(new VectorI3D(0, 0, 0)).build();
		Planet earth = Planet.builder().name("Earth").diameter(Constant.EARTH_DIAMETER).mass(Constant.EARTH_MASS).parent(sol)
				.rotationPeriod(24 * 3600 * 364.0f / 365.0f).orbit(new Orbit(Constant.AU, 0.0167086f, 0.0f)).build();
		printPlanet(earth);
	}

	private static void printStar(Star star, int i)
	{
		System.out.println("[" + String.format("%02d", i) + "] " + star.name
				+ " (" + star.starClass.fullDeclaration() + "), mass "
				+ String.format(Locale.ROOT, "%.3f", star.mass / Constant.SOLAR_MASS)
				+ " M☉, "
				+ "planets " + star.planets.size() + ", planetoids " + star.planetoids.size()
				+ ", luminosity " + String.format(Locale.ROOT, "%f", star.luminosity / Constant.SOLAR_LUM) + " L☉");
		System.out.println("     boiling line "
				+ String.format(Locale.ROOT, "%.4f", star.boilingLine / Constant.AU) + " AU, inner planet limit "
				+ String.format(Locale.ROOT, "%.4f", star.innerPlanetLimit / Constant.AU) + " AU, min habitable zone "
				+ String.format(Locale.ROOT, "%.4f", star.habitableZoneMin / Constant.AU) + " AU, max habitable zone "
				+ String.format(Locale.ROOT, "%.4f", star.habitableZoneMax / Constant.AU) + " AU, frost line "
				+ String.format(Locale.ROOT, "%.4f", star.frostLine / Constant.AU) + " AU, outer planet limit "
				+ String.format(Locale.ROOT, "%.4f", star.outerPlanetLimit / Constant.AU) + " AU");
	}
	
	private static void printPlanet(Planet p)
	{
		System.out.println("     " + p.name + " [" + p.planetaryClass.name + "]"
				+ ", mass: " + String.format(Locale.ROOT, "%.3f", p.mass / Constant.YOTTAGRAM) + " Yg, exclusion zone "
				+ String.format(Locale.ROOT, "%.3f", p.exclusionZone / Constant.AU)
				+ " AU, blackbody temp. " + String.format(Locale.ROOT, "%.1f", p.blackbodyTemperature) + " K");
		printOrbit(p.orbit, "       ");
		System.out.println("       diameter (Earth) "
				+ String.format(Locale.ROOT, "%.2f", p.diameter / 6371000.8 / 2) + ", gravity: "
				+ String.format(Locale.ROOT, "%.3f", p.surfaceGravity)
				+ " m/s², density: " + String.format(Locale.ROOT, "%.0f", p.density)
				+ " kg/m³, escape V: " + String.format(Locale.ROOT, "%.0f", p.escapeVelocity)
				+ " m/s, mol. limit: " + String.format(Locale.ROOT, "%.2f", p.molecularLimit));
		System.out.println("       day length: "
				+ String.format(Locale.ROOT, "%.2f",  p.dayLength / 3600.0) + " hours, year length: "
				+ String.format(Locale.ROOT, "%.2f", p.yearLength / 90000.0) + " galactic days, moons: "
				+ p.moons.size());
	}
	
	private static void printMoon(Moon m)
	{
		System.out.println(String.format("\"%s\"\t\"%s\"\t%.3f\t%.0f\t%.0f\t%.0f\t%.2f\t%d",
				m.name, m.planetaryClass.name, m.mass, m.diameter / 1000, m.density, m.uncompressedDensity, m.molecularLimit, m.blackbodyTemperature));
		/*
		System.out.println("         " + m.name + ", mass: " + String.format("%.3f", m.mass)
				+ " Yg, radius (Earth radii) " + String.format("%.2f", m.planetRadius / 6371000)
				+ ", gravity: " + String.format("%.3f", m.surfaceGravity)
				+ " m/s², density: " + String.format("%.0f", m.density) + " kg/m³, orbit " + m.orbit
				+ ", escape V: " + String.format("%.0f", m.escapeVelocity)
				+ " m/s, mol. limit: " + String.format("%.2f", m.molecularLimit));
		*/
	}

	private static void printOrbit(Orbit o, String prefix) {
		System.out.println(prefix + "orbit radius "
				+ String.format(Locale.ROOT, "%.3f", o.radius / Constant.AU) + " AU"
				+ ", eccentricity " + String.format(Locale.ROOT, "%.3f", o.eccentricity)
				+ ", inclination " + String.format(Locale.ROOT, "%.2f", o.inclination));
	}
}
