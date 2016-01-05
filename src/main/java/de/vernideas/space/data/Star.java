package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.starclass.StarClass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
	@Getter @Setter protected double temperature;
	/** Luminosity in W */
	@Getter protected double luminosity;
	@Getter protected double originalLuminosity = -1.0;
	/** 3200K cut-off distance in m */
	@Getter protected double boilingLine;
	/** Frost line distance in m */
	@Getter protected double frostLine;
	@Getter protected double habitableZoneMin;
	@Getter protected double habitableZoneMax;
	@Getter protected double innerPlanetLimit;
	@Getter protected double outerPlanetLimit;
	@NonNull @Getter @Setter protected VectorD3D position;
	
	public Star(@NonNull String name, @NonNull StarClass starClass)
	{
		super(name);
		this.starClass = starClass;
		
		this.planets = new ArrayList<Planet>();
		this.planetoids = new ArrayList<Planet>();
		
		this.position = new VectorD3D();
	}
	
	public Star luminosity(double luminosity) {
		this.luminosity = luminosity;
		
		this.boilingLine = Math.sqrt(this.luminosity / Constant.STEFAN_BOLTZMANN_PI) / (4 * 3200 * 3200);
		// Habitable zone for Earth-like planets: from 310 K at 0.4 albedo to 220 K at 0.1 albedo
		// Below that - desert planet, above - snow planets
		// Corresponds to 375 K and 226 K for an orbit with no eccentricity
		this.habitableZoneMin = Math.sqrt(this.luminosity * 0.6 / Constant.STEFAN_BOLTZMANN_PI) / (4 * 330 * 330);
		this.habitableZoneMax = Math.sqrt(this.luminosity * 0.9 / Constant.STEFAN_BOLTZMANN_PI) / (4 * 220 * 220);
		
		if( this.originalLuminosity < 0.0 ) {
			originalLuminosity(luminosity);
		}
		
		return this;
	}
	
	public Star originalLuminosity(double originalLuminosity) {
		this.originalLuminosity = originalLuminosity;
		
		// Calculate the frost line by assuming a black-body with temperature of 150K (water sublimation in vacuum)
		this.frostLine = Math.sqrt(this.originalLuminosity / Constant.STEFAN_BOLTZMANN_PI) / (4 * 150 * 150);
		
		// Empirical data
		// Inner planet limit : either mass in solar masses times 0.1 or original luminosity in solar lums
		// times 0.01; whichever is bigger, in AU
		this.innerPlanetLimit = Math.max(this.mass / Constant.SOLAR_MASS * 0.1, this.originalLuminosity / Constant.SOLAR_LUM * 0.01) * Constant.AU;
		
		return this;
	}
	
	@Override public StellarObject mass(double mass) {
		super.mass(mass);
		
		// Empirical data
		// Outer planet limit : mass in solar masses times 40, as AU
		this.outerPlanetLimit = this.mass / Constant.SOLAR_MASS * 40.0 * Constant.AU;
		
		return this;
	};
	
	/** This method doesn't check for conflicting data, it just adds the planet to the right list */
	public Planet addPlanet(String name, double mass, double diameter, @NonNull Orbit orbit, float rotationPeriod, boolean minor)
	{
		Planet planet = new Planet(name, minor);
		planet.mass(mass);
		planet.orbit(this, orbit);
		planet.rotationPeriod(rotationPeriod);
		planet.diameter(diameter);
		
		(minor ? planetoids : planets).add(planet);
		
		return planet;
	}
	
	// Resonance distances (for gaps)
	// Strong - 2:1, 3:1
	private final double RESONANCE_2_1 = Math.pow(2.0, -2.0/3.0);
	private final double RESONANCE_3_1 = Math.pow(3.0, -2.0/3.0);
	// Medium - 4:1, 5:2, 7:3
	private final double RESONANCE_4_1 = Math.pow(4.0, -2.0/3.0);
	private final double RESONANCE_5_2 = Math.pow(5.0/2.0, -2.0/3.0);
	private final double RESONANCE_7_3 = Math.pow(7.0/3.0, -2.0/3.0);
	// Weak - 9:2, 7:2, 10:3, 8:3, 5:3, 9:4, 11:5, 11:6 (not sure if we need to bother)
	
	public boolean orbitFree(double radius, double eccentrity) {
		return orbitFree(radius, eccentrity, 1.0);
	}
	
	/**
	 * Goes through the current planet list and checks if the orbit is "free" to house another planet.
	 */
	public boolean orbitFree(double radius, double eccentrity, double maxOrbitMultiplier) {
		double peri = (1.0 - eccentrity) * radius;
		double apo = (1.0 + eccentrity) * radius;
		
		// Check for star radius first
		if( peri < diameter || peri < boilingLine ) {
			return false;
		}
		if( radius < innerPlanetLimit || radius > outerPlanetLimit * maxOrbitMultiplier ) {
			return false;
		}
		
		for(Planet p : planets) {
			if( apo >= p.orbit.pericenter - p.exclusionZone && peri <= p.orbit.apocenter + p.exclusionZone ) {
				return false;
			}
			// Kirkwood gaps
			// Strong resonance gaps: 3:1 and 2:1
			double resDist = Math.abs(p.orbit.radius * RESONANCE_2_1 - radius);
			resDist = Math.min(resDist, Math.abs(p.orbit.radius * RESONANCE_3_1 - radius));
			if( resDist < p.orbit.radius / 1000.0 )  {
				return false;
			}
			// Weak resonance gaps
			resDist = Math.abs(p.orbit.radius * RESONANCE_4_1 - radius);
			resDist = Math.min(resDist, Math.abs(p.orbit.radius * RESONANCE_5_2 - radius));
			resDist = Math.min(resDist, Math.abs(p.orbit.radius * RESONANCE_7_3 - radius));
			if( resDist < p.orbit.radius / 5000.0 )  {
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
	
	/**
	 * Orbital distance for the given blackbody temperature
	 * 
	 * @param blackbodyTemp
	 * @return
	 */
	public double distanceForTemperature(double blackbodyTemp) {
		return Math.sqrt(luminosity / Constant.STEFAN_BOLTZMANN_PI) / (4.0 * blackbodyTemp * blackbodyTemp);
	}

	/**
	 * Calculate the Stern-Levison parameter for the given planet mass and orbital distance.
	 * <p>
	 * If it's 100 or more, this is definitively a planet.
	 * If it's 0.01 or less, it's definitively NOT a planet (didn't clean its neighbourhood).
	 * <p>
	 * During filtering, this removes biggest minor planets from the low orbits and the small planets
	 * from far away orbits.
	 */
	public double sternLevisonParameter(double planetMass, double orbitalRadius) {
		return Constant.STERN_LEVISON_CONSTANT * planetMass * planetMass / Math.sqrt(Math.pow(orbitalRadius, 3)) * Math.sqrt(this.mass);
	}
}
