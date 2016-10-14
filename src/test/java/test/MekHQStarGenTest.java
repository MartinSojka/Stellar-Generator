package test;

import java.util.Locale;

import de.vernideas.lib.stellargen.GenUtil;
import de.vernideas.lib.stellargen.SystemGenerator;
import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Moon;
import de.vernideas.space.data.Planet;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;

public class MekHQStarGenTest {
	public static void main(String[] args) {
		double x = (args.length < 2) ? Math.random() * 2000.0 - 1000.0 : Double.parseDouble(args[0]);
		double y = (args.length < 2) ? Math.random() * 2000.0 - 1000.0 : Double.parseDouble(args[1]);
		x = Math.round(x * 1000.0) / 1000.0;
		y = Math.round(y * 1000.0) / 1000.0;
		Universe u = new Universe(Double.doubleToLongBits(x) | Double.doubleToLongBits(y));
		Star s = SystemGenerator.star(u);
		System.out.println(String.format(Locale.ROOT, "<planet>\n<id>RAND_%.3f_%.3f</id>", x, y));
		System.out.println(String.format(Locale.ROOT, "<xcood>%.3f</xcood>\n<ycood>%.3f</ycood>\n<faction>NONE</faction>", x, y));
		printStarMM(s);
		System.out.println("<!-- Most livable planet, if any -->");
		Planet planet = null;
		int sysPos = 0;
		for(Planet p : s.planets) {
			++ sysPos;
			if(p.habitable()) {
				planet = p;
				break;
			}
		}
		if(null != planet) {
			printPlanetMM(planet, sysPos);
		}
		System.out.println("</planet>");
	}
		
	private static void printStarMM(Star star) {
		//System.out.println("<name>" + star.name() + "</name>");
		System.out.println("<spectralType>" + star.starClass.fullDeclaration() + "</spectralType>");
		/*
		System.out.println(String.format(Locale.ROOT, "<mass>%.3f</mass>", star.mass() / Constant.SOLAR_MASS ));
		System.out.println(String.format(Locale.ROOT, "<lum>%.6f</lum>", star.luminosity() / Constant.SOLAR_LUM ));
		System.out.println(String.format(Locale.ROOT, "<temperature>%.0f</temperature>", star.temperature() ));
		System.out.println(String.format(Locale.ROOT, "<radius>%.4f</radius>", star.diameter() / Constant.SOLAR_DIAMETER ));
		*/
		System.out.println("<planets>" + star.planets.size() + "</planets>");
		System.out.println("<minorPlanets>" + star.planetoids.size() + "</minorPlanets>");
	}

	private static void printPlanetMM(Planet planet, int sysPos) {
		System.out.println("<name>" + planet.mainStar().name() + " " + GenUtil.romanNumber(sysPos) + "</name>");
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
	}

}
