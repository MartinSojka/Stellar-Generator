package de.vernideas.space.data.planetaryclass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Material;
import de.vernideas.space.data.Orbit;
import de.vernideas.space.data.OrbitalZone;
import de.vernideas.space.data.Satellite;
import de.vernideas.space.data.Star;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public class PlanetaryClass {
	public static final Set<PlanetaryClass> knownClasses = new HashSet<PlanetaryClass>();

	/**
	 * Ammonia clouds, max temperature of 150 K; reddish due to organic compounds
	 * <p>
	 * Examples: Jupiter (mass 1 898 600, density 1327, temp 165, blackbody 138, albedo 0.34, mollimit 0.12)
	 *           Saturn (mass 568 460, density 687, temp 134, blackbody 102, albedo 0.34, mollimit 0.25)
	 * <p>
	 * Molar limits for variants:<br>
	 * Below 1.00 -> Hydrogen/Helium<br>
	 * 1.00 to 4.00 -> Helium
	 */
	public static final PlanetaryClass GAS_GIANT_I = new GasGiant("Ammonia-clouded gas giant", null, 0.57, 1.0, 20, 170, 300, 1550);
	/** Water clouds, max temperature 300 K; white */
	public static final PlanetaryClass GAS_GIANT_II = new GasGiant("Water-clouded gas giant",null,  0.81, 1.0, 150, 400, 300, 1650);
	/** No global cloud cover, temperatures between 300 K and 800 K; dark blue */
	public static final PlanetaryClass GAS_GIANT_III = new GasGiant("Cloudless gas giant", null, 0.12, 300, 1.0, 900, 300, 1850);
	/** Deep cloud cover of silicates and iron, temperature range 800 to 1400 K; dark greenish grey */
	public static final PlanetaryClass GAS_GIANT_IV = new GasGiant("Alkali gas giant", null, 0.03, 900, 1.0, 1500, 300, 2100);
	/** High cloud cover of silicates and iron, temperatures above 1400 K; greenish grey */
	public static final PlanetaryClass GAS_GIANT_V = new GasGiant("Silicate-clouded gas giant", null, 0.55, 1.0, 1500, 9999, 300, 2500);
	// Hellium-rich variants of the above
	public static final PlanetaryClass HELLIUM_GIANT_I = new GasGiant("Ammonia-clouded gas giant (hydrogen-poor)", (planet) -> planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500, 0.57, 4.0, 0, 170, 400, 1650);
	public static final PlanetaryClass HELLIUM_GIANT_II = new GasGiant("Water-clouded gas giant (hydrogen-poor)", (planet) -> planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500, 0.81, 4.0, 150, 400, 400, 1750);
	public static final PlanetaryClass HELLIUM_GIANT_III = new GasGiant("Cloudless gas giant (hydrogen-poor)", (planet) -> planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500, 0.12, 4.0, 300, 900, 400, 1950);
	public static final PlanetaryClass HELLIUM_GIANT_IV = new GasGiant("Alkali gas giant (hydrogen-poor)", (planet) -> planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500, 0.03, 900, 4.0, 1500, 400, 2200);
	public static final PlanetaryClass HELLIUM_GIANT_V = new GasGiant("Silicate-clouded gas giant (hydrogen-poor)", (planet) -> planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500, 0.55, 4.0, 1500, 9999, 400, 2600);
	
	/** Metallic core of a gas giant stripped of hydrogen and helium atmosphere due to close proximity to a star */
	public static final PlanetaryClass CTHONIAN = new Cthonian();
	/**
	 *  Giant planet composed mostly of water, methane and ammonia; typically very cold.
	 *  <p>
	 *  Examples: Neptune (mass 102 430, density 1638, temp 72, blackbody 72, albedo 0.29, mollimit 0.51)
	 *            Uranus (mass 86 810, density 1270, temp 76, blackbody 57, albedo 0.30, mollimit 0.33)
	 * <p>
	 * Molar limits for variants:<br>
	 * Below 1.00 -> Hydrogen/Helium<br>
	 * 1.00 to 4.00 -> Helium
	 */
	public static final PlanetaryClass ICE_GIANT = new GasGiant("Ice giant", (planet) -> planet.blackbodyTemperature <= 80 + planet.density / 120, 0.3, 1.0, 20, 9999, 1200, 3000);
	public static final PlanetaryClass HELLIUM_ICE_GIANT = new GasGiant("Ice giant (hydrogen-poor)", (planet) -> planet.blackbodyTemperature <= 80 + planet.density / 120 && (planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20), 0.3, 4.0, 0, 9999, 1200, 3000);

	/** Hot 'Puffy' giants, very hot and in transition to cthonian planets */
	public static final PlanetaryClass HOT_PUFFY_GIANT = new PlanetaryClass("Hot 'puffy' gas giant",
			(planet) -> planet.molecularLimit > Math.min(1.00, planet.density / 2000.0)
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
			&& planet.density < 1000
			&& planet.blackbodyTemperature >= 1000, 0.55);

	/** Cold "puffy" giant, implies internal heating and strong magnetic fields */
	public static final PlanetaryClass COLD_PUFFY_GIANT = new PlanetaryClass("'Puffy' gas giant", 
			(planet) -> planet.molecularLimit <= 1.00
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
			&& planet.density < 500
			&& planet.blackbodyTemperature > 20 && planet.blackbodyTemperature < 1000, 0.55);

	/** Cold "puffy" giant with little hydrogen, implies internal heating and strong magnetic fields */
	public static final PlanetaryClass HELLIUM_COLD_PUFFY_GIANT = new PlanetaryClass("'Puffy' gas giant (hydrogen-poor)",
			(planet) -> (planet.molecularLimit <= 4.00 && planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20)
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
			&& planet.density < 700
			&& planet.blackbodyTemperature < 1000, 0.55);

	/** Rocky core, thick hydrogen/hellium atmosphere */
	public static final PlanetaryClass GAS_DWARF = new PlanetaryClass("Gas dwarf",
			(planet) -> planet.molecularLimit <= 1.00
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.5
			&& planet.density > 1500 && planet.density < 3700 + planet.blackbodyTemperature
			&& planet.blackbodyTemperature > 80 && planet.blackbodyTemperature < 1700, 0.3);

	/** Rocky core, thick hellium atmosphere */
	public static final PlanetaryClass HELLIUM_GAS_DWARF = new PlanetaryClass("Gas dwarf (hydrogen-poor)",
			(planet) -> planet.molecularLimit > 1.00 && planet.molecularLimit <= 4.00
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.5
			&& planet.density > 1500 && planet.density < 4000 + planet.blackbodyTemperature
			&& planet.blackbodyTemperature > 80 && planet.blackbodyTemperature < 1700, 0.3);
	
	
	/** Rocky core, thick hydrogen/hellium atmosphere */
	public static final PlanetaryClass FROZEN_GAS_DWARF = new PlanetaryClass("Frozen gas dwarf",
			(planet) -> planet.molecularLimit <= 4.00
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
			&& planet.density >= 2500 && planet.density < 4000
			&& planet.blackbodyTemperature <= 80, 0.55);

	/** Late-stage hot "puffy" gas giant with atmosphere boiling away and a comet-like trail */
	public static final PlanetaryClass BOILING_GIANT = new PlanetaryClass("Boiling Giant",
			(planet) -> planet.molecularLimit > 4.00
			&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.9
			&& planet.density < 2200
			&& planet.blackbodyTemperature > 200, 0.3);

	// Terrestial planets
	
	/** Low-water variant on Earth-sized planet */
	public static final PlanetaryClass DESERT = new Terrestrial("Desert planet",
			(planet) -> planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
			&& planet.molecularLimit > 18.00 /* water vapour */, 0.27, 0.16)
			.materialDensity(4200, 1000, 3500, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE)
			.temperatureLimits(250, 700);

	/** Earth-sized planet with oceans and water clouds, but without much hellium in the atmosphere */
	public static final PlanetaryClass EARTH_LIKE = new Terrestrial("Earth-like planet",
			(planet) -> planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
			&& planet.molecularLimit <= 18.00 /* water vapour */ && planet.molecularLimit > 4.00, 0.29, 0.16)
			.materialDensity(4200, 1000, 3500, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE)
			.temperatureLimits(250, 330);

	/** Variant of an Earth-like planet with a runaway greenhouse effect. See: Venus */
	public static final PlanetaryClass GREENHOUSE = new Terrestrial("Greenhouse planet",
			(planet) -> planet.uncompressedDensity <= 5000
			&& planet.molecularLimit <= 18.00 /* water vapour */ && planet.molecularLimit > 4.00, 0.65, 2.0)
			.materialDensity(4000, 1000, 1200, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE)
			.temperatureLimits(330, 500);

	/** Hot planets still retaining a substantial atmosphere (CO2 molar mass = 44), but not liquid water;
	 * also likely lacking a strong magnetic field */
	public static final PlanetaryClass HELL = new Terrestrial("Hell planet",
			(planet) -> planet.uncompressedDensity < 5000
			&& (planet.blackbodyTemperature >= 500 || planet.molecularLimit > 18.00)
			&& planet.molecularLimit <= 44.00, 0.4, 0.5)
			.materialDensity(4000, 1000, 1200, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE)
			.temperatureLimits(330, 5000);

	/** Cold planet lacking a magnetic field, consisting mostly of rock */
	public static final PlanetaryClass FROZEN_ROCK = new Terrestrial("Frozen rock planet",
			(planet) -> planet.uncompressedDensity < 5000 && planet.uncompressedDensity >= 3000
			&& planet.molecularLimit <= 44.00 /* carbon dioxide */, 0.4, 0.2)
			.materialDensity(3900, 1000, 3000, 5000)
			.materialCompressibility(1000e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE, OrbitalZone.COLD, OrbitalZone.FROZEN)
			.temperatureLimits(0, 260);

	/** Airless rock, mostly relatively warm */
	public static final PlanetaryClass AIRLESS = new Terrestrial("Airless rock planet",
			(planet) -> planet.uncompressedDensity < 5000
			&& (planet.uncompressedDensity >= 2600 || planet.blackbodyTemperature >= 260)
			&& planet.molecularLimit > 44.00 /* carbon dioxide */, 0.4, 0.0)
			.materialDensity(3600, 1000, 2600, 5000)
			.materialCompressibility(1000e-12, 2500e-12, 2.0);

	/** Small rocky worlds with carbon dioxide atmosphere (mostly) */
	public static final PlanetaryClass DRY_ROCK = new Terrestrial("Dry rocky planet",
			(planet) -> planet.uncompressedDensity <= 3500 && planet.uncompressedDensity >= 2600
			&& planet.molecularLimit > 18.00 && planet.molecularLimit <= 44.00, 0.2, 0.2)
			.materialDensity(3000, 500, 2600, 3500)
			.materialCompressibility(1000e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE)
			.temperatureLimits(250, 330);

	/** Earth-like conditions, including fluid water, but no notable magnetic field */
	public static final PlanetaryClass ROCKY = new Terrestrial("Rocky planet",
			(planet) -> planet.uncompressedDensity <= 3500 && planet.uncompressedDensity >= 2600
			&& planet.molecularLimit <= 18.00 /* water vapour */, 0.2, 0.16)
			.materialDensity(3000, 500, 2600, 3500)
			.materialCompressibility(1000e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE)
			.temperatureLimits(250, 330);

	/** Earth-sized planet with oceans and water clouds, thick hellium-rich atmosphere */
	public static final PlanetaryClass HIGH_PRESSURE = new Terrestrial("Earth-like planet (hellium-rich)",
			(planet) -> planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
			&& planet.molecularLimit <= 4.00, 0.29, 0.16)
			.materialDensity(4200, 1000, 3500, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE)
			.temperatureLimits(250, 330);

	/** Variant of an Earth-like planet with a runaway greenhouse effect and extremly thick hellium-rich atmosphere */
	public static final PlanetaryClass EXTREME_GREENHOUSE = new Terrestrial("Greenhouse planet (hellium-rich)",
			(planet) -> planet.uncompressedDensity <= 5000
			&& planet.molecularLimit <= 4.00, 0.65, 2.0)
			.materialDensity(4000, 1000, 1200, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE)
			.temperatureLimits(330, 500);

	/**
	 * Earth-sized planet or planetoid with a thick ice cover: Below 260 K for water ice, below 180 K for CO2 and ammonia and below 80K for methane
	 * <p>
	 * Examples: Pluto [methane] (mass 13, density 1983, temp 44, blackbody 50, albedo 0.58, mollimit 108.15)
	 *           Europa [CO2/NH3] (mass 48, density 3014, temp 102, blackbody 138, albedo 0.67, mollimit 108.68)
	 *           Eris [methane] (mass 17, density 2580, temp 43, blackbody 38, albedo 0.97, mollimit 62.96)
	 */
	public static final PlanetaryClass WATER_ICE = new Terrestrial("Ice planet",
			(planet) -> planet.uncompressedDensity < 3000, 0.65, 0.2)
			.materialDensity(2000, 700, 800, 3000)
			.materialCompressibility(1000e-12, 5000e-12)
			.validZones(OrbitalZone.HABITABLE, OrbitalZone.COLD)
			.temperatureLimits(170, 260);

	public static final PlanetaryClass AMMONIA_ICE = new Terrestrial("CO₂/ammonia ice planet",
			(planet) -> planet.uncompressedDensity < 3000, 0.65, 0.2)
			.materialDensity(2000, 700, 1000, 3000)
			.materialCompressibility(1000e-12, 5000e-12)
			.validZones(OrbitalZone.COLD, OrbitalZone.FROZEN)
			.temperatureLimits(70, 180);

	public static final PlanetaryClass METHANE_ICE = new Terrestrial("Methane ice planet",
			(planet) -> planet.uncompressedDensity < 3000, 0.65, 0.2)
			.materialDensity(2000, 700, 800, 3000)
			.materialCompressibility(1000e-12, 5000e-12)
			.validZones(OrbitalZone.FROZEN)
			.temperatureLimits(0, 80);

	/**
	 * Iron-rich small planets without much or any mantle; typically close to their star or around big stars
	 * <p>
	 * Examples: Mercury (mass 330, density 5425, temp 340, blackbody 508, albedo 0.07, mollimit 90.96)
	 */
	public static final PlanetaryClass IRON = new Terrestrial("Iron planet",
			(planet) -> planet.uncompressedDensity >= 5000, 0.07, 0.08)
			.materialDensity(5500, 1000, 5000)
			.materialCompressibility(100e-12, 1000e-12, 2.0);

	/** Very hot rocky planets. Zone HOT only. */
	public static final PlanetaryClass LAVA = new Terrestrial("Lava planet",
			(planet) -> planet.uncompressedDensity <= 5000, 0.07, 0.06)
			.materialDensity(4000, 1000, 2600, 5000)
			.materialCompressibility(250e-12, 2500e-12, 2.0)
			.validZones(OrbitalZone.HOT)
			.temperatureLimits(700, 5000);

	/**
	 * High-water content planets with temperatures between 260 K and 350 K;
	 * typically good cloud cover and greenhouse effect.
	 * Zone HABITABLE only.
	 */
	public static final PlanetaryClass OCEAN = new Terrestrial("Ocean planet",
			(planet) -> planet.uncompressedDensity <= 3800
			&& planet.molecularLimit <= 18.00 /* water vapour */, 0.2, 1.5)
			.materialDensity(2800, 1000, 1200, 3800)
			.materialCompressibility(500e-12, 10000e-12, 2.0)
			.validZones(OrbitalZone.HABITABLE)
			.temperatureLimits(260, 350);

	/**
	 * Special - tidally flexed small planet, see Io.
	 * These need special calculations to appear, and can only appear as moons of gas giants.
	 * Zones HABITABLE, COLD and FROZEN.
	 */
	public static final PlanetaryClass SULFUR = new Terrestrial("Tidally-flexed planet",
			(planet) -> planet.uncompressedDensity < 3000, 0.65, 0)
			.materialDensity(2000, 700, 800, 3000)
			.materialCompressibility(1000e-12, 5000e-12)
			.validZones(OrbitalZone.HABITABLE, OrbitalZone.COLD, OrbitalZone.FROZEN)
			.temperatureLimits(0, 260);

	// Planetoid classes
	
	/** Very dark, consisting mostly of carbonaceous chondite. Zones HOT, HABITABLE and COLD. */
	public static final PlanetaryClass CARBONACEOUS_PLANETOID = new Planetoid("Carbonaceous planetoid",
			(planet) -> planet.density >= 2000 && planet.density < 4000, 0.1)
			.materialDensity(2800, 1500, 2000, 4000)
			.materialCompressibility(100e-12, 25000e-12, 1.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE, OrbitalZone.COLD)
			.temperatureLimits(200, 5000);

	/** Mostly packed ice/gravel. Zones HABITABLE, COLD and FROZEN. */
	public static final PlanetaryClass ICE_PLANETOID = new Planetoid("Ice planetoid",
			(planet) -> planet.density < 3500, 0.6)
			.materialDensity(1400, 800, 700, 3500)
			.materialCompressibility(100e-12, 25000e-12, 1.0)
			.validZones(OrbitalZone.HABITABLE, OrbitalZone.COLD, OrbitalZone.FROZEN)
			.temperatureLimits(0, 260);

	/** Rock; the most common type */
	public static final PlanetaryClass SILICATE_PLANETOID = new Planetoid("Silicate planetoid",
			(planet) -> planet.density >= 3000 && planet.density <= 5000, 0.35)
			.materialDensity(3800, 1500, 3000, 5000)
			.materialCompressibility(100e-12, 25000e-12, 1.0);

	/** Metal (resource-rich) */
	public static final PlanetaryClass METALLIC_PLANETOID = new Planetoid("Metallic planetoid",
			(planet) -> planet.density >= 4000, 0.2)
			.materialDensity(5000, 1500, 4000, 15000)
			.materialCompressibility(100e-12, 25000e-12, 1.0);

	/** Loose gravel stuff, but not frozen. Zones HOT and HABITABLE. */
	public static final PlanetaryClass GRAVEL_PLANETOID = new Planetoid("Gravel planetoid",
			(planet) -> planet.density < 2000, 0.2)
			.materialDensity(1200, 1000, 600, 2000)
			.materialCompressibility(100e-12, 25000e-12, 1.0)
			.validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE, OrbitalZone.COLD)
			.temperatureLimits(250, 5000);
	
	public static final PlanetaryClass UNKNOWN = new PlanetaryClass("UNKNOWN", (planet) -> false, 0.0);
	
	public final String name;
	public final double albedo;
	public final Predicate<Satellite> predicate;
	
	protected PlanetaryClass(String name, Predicate<Satellite> predicate, double albedo)
	{
		this.name = name;
		this.predicate = predicate;
		this.albedo = albedo;
	}
	
	private double between(double min, double max, double val) {
		return min + (max - min) * val;
	}

	/** Is this planet a possible candidate for this planetary class? */
	protected boolean possibleClass(Satellite planet) {
		return (null == predicate || predicate.test(planet))
				&& validTemperature(planet.blackbodyTemperature);
	}
	
	/** Average greenhouse factor (only important for terrestial planets) */
	public double avgGreenhouseFactor() {
		return 0.0;
	}
	
	/** Get a random greenhouse factor */
	public double newGreenhouseFactor() {
		return 0.0;
	}
	
	/** Get a random amount of surface covered by water, in percent (0.0-1.0) */
	public double newHydrographicCoverage(Random rnd, double blackbodyTemperature) {
		return 0.0;
	}
	
	// Temperature limits
	private double minTemperature = 0.0;
	private double maxTemperature = Double.MAX_VALUE;
	
	public PlanetaryClass temperatureLimits(double temp1, double temp2) {
		minTemperature = Math.min(temp1, temp2);
		maxTemperature = Math.max(temp1, temp2);
		return this;
	}
	
	public boolean validTemperature(double temperature) {
		return temperature >= minTemperature && temperature <= maxTemperature;
	}
	
	public boolean validTemperature(Star star, Orbit orbit) {
		return validTemperature(orbit.blackbodyTemp(star));
	}
	
	// Valid orbital zones
	private OrbitalZone[] validZones = {OrbitalZone.HOT, OrbitalZone.HABITABLE, OrbitalZone.COLD, OrbitalZone.FROZEN};
	
	public PlanetaryClass validZones(OrbitalZone ... zones) {
		validZones = null == zones ? new OrbitalZone[0] : zones;
		return this;
	}
	
	public boolean inValidZone(OrbitalZone zone) {
		if( null == zone ) { return false; }
		for( OrbitalZone validZone : validZones ) {
			if( validZone == zone ) { return true; }
		}
		return false;
	}
	
	// Material generation
	private double avgDensity = 3000.0;
	private double sigmaDensity = 2000.0;
	private double minDensity = Double.MIN_NORMAL;
	private double maxDensity = Double.MAX_VALUE;
	private double minCompressibility = 100.0e-12;
	private double maxCompressibility = 25000.0e-12;
	/**
	 * The power to which the (linear) random value for compressibility is taken.
	 * Positive values if lower compressibilities are more likely, negative if the opposite.
	 */
	private double compressibilityScale = 1.5;
	/**
	 * 0.0 if density doesn't affect compressibility,
	 * negative if denser materials are less compressible (the default
	 */
	private double compressibilityDensityScale = -0.5;
	
	/** Set material density settings */
	public PlanetaryClass materialDensity(double avgDensity, double sigmaDensity) {
		return materialDensity(avgDensity, sigmaDensity, this.minDensity, this.maxDensity);
	}
	
	/** Set material density settings */
	public PlanetaryClass materialDensity(double avgDensity, double sigmaDensity, double minDensity) {
		return materialDensity(avgDensity, sigmaDensity, minDensity, this.maxDensity);
	}
	
	/** Set material density settings */
	public PlanetaryClass materialDensity(double avgDensity, double sigmaDensity, double minDensity, double maxDensity) {
		this.avgDensity = avgDensity;
		this.sigmaDensity = sigmaDensity;
		this.minDensity = Math.min(minDensity, maxDensity);
		this.maxDensity = Math.max(minDensity, maxDensity);
		return this;
	}
	
	/** Set material compressibility settings */
	public PlanetaryClass materialCompressibility(double minCompressibility, double maxCompressibility) {
		return materialCompressibility(minCompressibility, maxCompressibility, this.compressibilityScale, this.compressibilityDensityScale);
	}
	
	/** Set material compressibility settings */
	public PlanetaryClass materialCompressibility(double minCompressibility, double maxCompressibility, double compressibilityScale) {
		return materialCompressibility(minCompressibility, maxCompressibility, compressibilityScale, this.compressibilityDensityScale);
	}
	
	/** Set material compressibility settings */
	public PlanetaryClass materialCompressibility(double minCompressibility, double maxCompressibility, double compressibilityScale, double compressibilityDensityScale) {
		this.minCompressibility = Math.min(minCompressibility, maxCompressibility);
		this.maxCompressibility = Math.max(minCompressibility, maxCompressibility);
		this.compressibilityScale = compressibilityScale;
		this.compressibilityDensityScale = compressibilityDensityScale;
		return this;
	}
	
	/** Get a random material for the given planet class */
	public Material newMaterial(Random rnd, double blackbodyTemperature) {
		double density = rnd.nextGaussian() * sigmaDensity + avgDensity;
		while( density < minDensity || density > maxDensity ) {
			density = rnd.nextGaussian() * sigmaDensity + avgDensity;
		}
		double compressibility = between(minCompressibility, maxCompressibility,
				Math.pow(rnd.nextDouble(), compressibilityScale)) * Math.pow(density, compressibilityDensityScale);
		return new Material("", density, compressibility);
	}
	

	/* public abstract Map<Gas, Integer> atmosphere(Orbit orbit, SpectralClass sc, int temperature, long seed); */
	
	public static PlanetaryClass classify(Satellite planet)
	{
		List<PlanetaryClass> possibleClasses = new ArrayList<PlanetaryClass>();
		
		for( PlanetaryClass pc : knownClasses )
		{
			if( pc.possibleClass(planet) )
			{
				possibleClasses.add(pc);
			}
		}
		
		if( possibleClasses.size() < 1 )
		{
			possibleClasses.add(UNKNOWN);
		}
		
		if( possibleClasses.size() > 1 )
		{
			System.out.print("Multiple classification possible for \"" + planet.name + "\": ");
			for( PlanetaryClass pc : possibleClasses )
			{
				System.out.print(pc.name + " ");
			}
			System.out.println("");
		}
		return possibleClasses.get(planet.random.nextInt(possibleClasses.size()));
	}
	
	static {
		knownClasses.add(GAS_GIANT_I);
		knownClasses.add(GAS_GIANT_II);
		knownClasses.add(GAS_GIANT_III);
		knownClasses.add(GAS_GIANT_IV);
		knownClasses.add(GAS_GIANT_V);
		knownClasses.add(HELLIUM_GIANT_I);
		knownClasses.add(HELLIUM_GIANT_II);
		knownClasses.add(HELLIUM_GIANT_III);
		knownClasses.add(HELLIUM_GIANT_IV);
		knownClasses.add(HELLIUM_GIANT_V);
		knownClasses.add(CTHONIAN);
		knownClasses.add(ICE_GIANT);
		knownClasses.add(HELLIUM_ICE_GIANT);
		knownClasses.add(HOT_PUFFY_GIANT);
		knownClasses.add(COLD_PUFFY_GIANT);
		knownClasses.add(HELLIUM_COLD_PUFFY_GIANT);
		knownClasses.add(BOILING_GIANT);
		knownClasses.add(GAS_DWARF);
		knownClasses.add(HELLIUM_GAS_DWARF);
		knownClasses.add(FROZEN_GAS_DWARF);
		knownClasses.add(DESERT);
		knownClasses.add(EARTH_LIKE);
		knownClasses.add(GREENHOUSE);
		knownClasses.add(EXTREME_GREENHOUSE);
		knownClasses.add(WATER_ICE);
		knownClasses.add(AMMONIA_ICE);
		knownClasses.add(METHANE_ICE);
		knownClasses.add(IRON);
		knownClasses.add(LAVA);
		knownClasses.add(OCEAN);
		knownClasses.add(HELL);
		knownClasses.add(FROZEN_ROCK);
		knownClasses.add(ROCKY);
		knownClasses.add(DRY_ROCK);
		knownClasses.add(AIRLESS);
		knownClasses.add(HIGH_PRESSURE);
		knownClasses.add(CARBONACEOUS_PLANETOID);
		knownClasses.add(ICE_PLANETOID);
		knownClasses.add(SILICATE_PLANETOID);
		knownClasses.add(METALLIC_PLANETOID);
		knownClasses.add(GRAVEL_PLANETOID);
	}

}
