package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;
import de.vernideas.space.data.starclass.StarClass;

@ToString(callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true,of={"spectralClass","temperature"})
public class Star extends StellarObject {
	/** Spectral class */
	@NonNull public final StarClass starClass;
	@NonNull public final List<Planet> planets;
	@NonNull public final List<Planet> planetoids;
	/** Effective temperature in Kelvin; used for luminosity and colour */
	public final double temperature;
	/** Luminosity in W */
	public final double luminosity;
	/** 3200K cut-off distance in m */
	public final double boilingLine;
	/** Frost line distance in m */
	public final double frostLine;
	public final double habitableZoneMin;
	public final double habitableZoneMax;
	public final VectorI3D position;
	
	@Builder(fluent=true, chain=true)
	private Star(@NonNull String name, double mass, double diameter,
			@NonNull VectorI3D position, @NonNull StarClass starClass, double temperature,
			long seed)
	{
		super(name, mass, diameter, seed + 37L * position.hashCode());
		this.starClass = starClass;
		this.temperature = temperature;
		
		this.planets = new ArrayList<Planet>();
		this.planetoids = new ArrayList<Planet>();
		
		this.luminosity = Math.PI * Constant.STEFAN_BOLTZMANN * diameter * diameter * temperature * temperature * temperature * temperature;
		
		this.boilingLine = Math.pow(this.luminosity / (Math.PI * Constant.STEFAN_BOLTZMANN), 0.5) / (4 * 3200 * 3200);
		
		// Calculate the frost line by assuming a black-body with temperature of 150K (water sublimation in vacuum) and about 70% current lum
		this.frostLine = Math.pow(this.luminosity * 0.7 / (Math.PI * Constant.STEFAN_BOLTZMANN), 0.5) / (4 * 150 * 150);
		
		// TODO: Habitable zone for Earth-like planets: from 330 K to 220 K (below that - desert planet, above - snow planets)
		this.habitableZoneMin = Math.pow(this.luminosity / (Math.PI * Constant.STEFAN_BOLTZMANN), 0.5) / (4 * 330 * 330);
		this.habitableZoneMax = Math.pow(this.luminosity / (Math.PI * Constant.STEFAN_BOLTZMANN), 0.5) / (4 * 220 * 220);
		
		this.position = position;	
	}
	
	/** This method doesn't check for conflicting data, it just adds the planet to the right list */
	public Planet addPlanet(String name, double mass, @NonNull Orbit orbit, float rotationPeriod, double planetRadius, boolean minor)
	{
		Planet planet = Planet.builder().name(name).mass(mass).orbit(orbit).rotationPeriod(rotationPeriod).planetRadius(planetRadius).minor(minor).parent(this).build();
		
		(minor ? planetoids : planets).add(planet);
		
		return planet;
	}
	
	/**
	 * Goes through the current planet list and checks if the orbit is "free" to house another planet.
	 */
	public boolean orbitFree(double radius, float eccentricity)
	{
		double peri = (1.0f - eccentricity) * radius;
		double apo = (1.0f + eccentricity) * radius;
		
		// Check for star radius first
		if( peri < diameter || peri < boilingLine )
		{
			return false;
		}
		
		for(Planet p : planets)
		{
			if( apo >= p.orbit.pericenter - p.exclusionZone && peri <= p.orbit.apocenter + p.exclusionZone )
			{
				return false;
			}
		}
		
		return true;
	}
	
	public double absoluteMagnitude()
	{
		return( 4.83 - 2.5 * Math.log10(luminosity / Constant.SOLAR_LUM));
	}
}
