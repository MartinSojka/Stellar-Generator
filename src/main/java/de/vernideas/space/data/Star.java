package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.starclass.StarClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true,of={"starClass","temperature"})
public class Star extends StellarObject {
	/** Spectral class */
	@NonNull public final StarClass starClass;
	@NonNull public final List<Planet> planets;
	@NonNull public final List<Planet> planetoids;
	/** Effective temperature in Kelvin; used for luminosity and colour */
	public final double temperature;
	/** Luminosity in W */
	public final double luminosity;
	public final double originalLuminosity;
	/** 3200K cut-off distance in m */
	public final double boilingLine;
	/** Frost line distance in m */
	public final double frostLine;
	public final double habitableZoneMin;
	public final double habitableZoneMax;
	public final double innerPlanetLimit;
	public final double outerPlanetLimit;
	@NonNull public final VectorI3D position;
	
	@Builder
	private Star(@NonNull String name, double mass, double diameter,
			VectorI3D position, @NonNull StarClass starClass, double temperature, double luminosity,
			double originalLuminosity,
			long seed)
	{
		super(name, mass, diameter, seed + 37L * position.hashCode());
		this.starClass = starClass;
		this.temperature = temperature;
		
		this.planets = new ArrayList<Planet>();
		this.planetoids = new ArrayList<Planet>();
		
		this.luminosity = luminosity;
		this.originalLuminosity = (originalLuminosity > 0.0 ? originalLuminosity : luminosity);
		
		this.boilingLine = Math.sqrt(this.luminosity / Constant.STEFAN_BOLTZMANN_PI) / (4 * 3200 * 3200);
		
		// Calculate the frost line by assuming a black-body with temperature of 150K (water sublimation in vacuum)
		this.frostLine = Math.sqrt(this.originalLuminosity / Constant.STEFAN_BOLTZMANN_PI) / (4 * 150 * 150);
		
		// Habitable zone for Earth-like planets: from 310 K at 0.4 albedo to 220 K at 0.1 albedo (below that - desert planet, above - snow planets)
		this.habitableZoneMin = Math.sqrt(this.luminosity * 0.6 / Constant.STEFAN_BOLTZMANN_PI) / (4 * 330 * 330);
		this.habitableZoneMax = Math.sqrt(this.luminosity * 0.9 / Constant.STEFAN_BOLTZMANN_PI) / (4 * 220 * 220);
		
		// Empirical data
		// Inner planet limit : either mass in solar masses times 0.1 or original luminosity in solar lums
		// times 0.01; whichever is bigger, in AU
		this.innerPlanetLimit = Math.max(this.mass / Constant.SOLAR_MASS * 0.1, this.originalLuminosity / Constant.SOLAR_LUM * 0.01) * Constant.AU;
		// Outer planet limit : mass in solar masses times 40, as AU
		this.outerPlanetLimit = this.mass / Constant.SOLAR_MASS * 40.0 * Constant.AU;
		
		this.position = position;	
	}
	
	/** This method doesn't check for conflicting data, it just adds the planet to the right list */
	public Planet addPlanet(String name, double mass, double diameter, @NonNull Orbit orbit, float rotationPeriod, boolean minor)
	{
		Planet planet = Planet.builder().name(name).mass(mass).orbit(orbit).rotationPeriod(rotationPeriod).diameter(diameter).minor(minor).parent(this).build();
		
		(minor ? planetoids : planets).add(planet);
		
		return planet;
	}
	
	/**
	 * Goes through the current planet list and checks if the orbit is "free" to house another planet.
	 */
	public boolean orbitFree(double radius, float eccentricity) {
		double peri = (1.0f - eccentricity) * radius;
		double apo = (1.0f + eccentricity) * radius;
		
		// Check for star radius first
		if( peri < diameter || peri < boilingLine ) {
			return false;
		}
		if( radius < innerPlanetLimit || radius > outerPlanetLimit ) {
			return false;
		}
		
		for(Planet p : planets) {
			if( apo >= p.orbit.pericenter - p.exclusionZone && peri <= p.orbit.apocenter + p.exclusionZone ) {
				return false;
			}
		}
		
		return true;
	}
	
	public double absoluteMagnitude() {
		return( 4.83 - 2.5 * Math.log10(luminosity / Constant.SOLAR_LUM));
	}
	
	/**
	 * Blackbody temperature at a given orbital distance
	 */
	public double blackbodyTemp(double distance) {
		return Math.pow(luminosity / (Constant.STEFAN_BOLTZMANN_PI * distance * distance), 0.25) / 2.0;
	}
	
}
