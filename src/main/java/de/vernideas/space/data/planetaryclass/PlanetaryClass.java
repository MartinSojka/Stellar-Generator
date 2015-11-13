package de.vernideas.space.data.planetaryclass;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class PlanetaryClass {
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
	public static final PlanetaryClass GAS_GIANT_I = new GasGiant("Ammonia-clouded gas giant", 0.57, 20, 170);
	/** Water clouds, max temperature 300 K; white */
	public static final PlanetaryClass GAS_GIANT_II = new GasGiant("Water-clouded gas giant", 0.81, 150, 400);
	/** No global cloud cover, temperatures between 300 K and 800 K; dark blue */
	public static final PlanetaryClass GAS_GIANT_III = new GasGiant("Cloudless gas giant", 0.12, 300, 900);
	/** Deep cloud cover of silicates and iron, temperature range 800 to 1400 K; dark greenish grey */
	public static final PlanetaryClass GAS_GIANT_IV = new GasGiant("Alkali gas giant", 0.03, 900, 1500);
	/** High cloud cover of silicates and iron, temperatures above 1400 K; greenish grey */
	public static final PlanetaryClass GAS_GIANT_V = new GasGiant("Silicate-clouded gas giant", 0.55, 1500, 9999);
	// Hellium-rich variants of the above
	public static final PlanetaryClass HELLIUM_GIANT_I = new HelliumGiant("Ammonia-clouded gas giant (hydrogen-poor)", 0.57, 0, 170);
	public static final PlanetaryClass HELLIUM_GIANT_II = new HelliumGiant("Water-clouded gas giant (hydrogen-poor)", 0.81, 150, 400);
	public static final PlanetaryClass HELLIUM_GIANT_III = new HelliumGiant("Cloudless gas giant (hydrogen-poor)", 0.12, 300, 900);
	public static final PlanetaryClass HELLIUM_GIANT_IV = new HelliumGiant("Alkali gas giant (hydrogen-poor)", 0.03, 900, 1500);
	public static final PlanetaryClass HELLIUM_GIANT_V = new HelliumGiant("Silicate-clouded gas giant (hydrogen-poor)", 0.55, 1500, 9999);
	/** Metallic core of a gas giant stripped of hydrogen and helium atmosphere due to close proximity to a star */
	public static final PlanetaryClass CTHONIAN = new PlanetaryClass("Cthonian planet", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return ((planet.molecularLimit > 4.00 || planet.density + planet.blackbodyTemperature / 10 > 2500)
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.9
					&& planet.density > 2000
					&& planet.blackbodyTemperature > 300);
		}
	};
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
	public static final PlanetaryClass ICE_GIANT = new PlanetaryClass("Ice giant", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit <= 1.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density > 1200 && planet.density <= 3000
					&& planet.blackbodyTemperature > 20 && planet.blackbodyTemperature <= 80 + planet.density / 120);
		}
	};
	
	public static final PlanetaryClass HELLIUM_ICE_GIANT = new PlanetaryClass("Ice giant (hydrogen-poor)", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return ((planet.molecularLimit <= 4.00 && planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20)
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density > 1400 && planet.density <= 3200
					&& planet.blackbodyTemperature <= 80 + planet.density / 120);
			}
	};
	/** Hot 'Puffy' giants, very hot and in transition to cthonian planets */
	public static final PlanetaryClass HOT_PUFFY_GIANT = new PlanetaryClass("Hot 'puffy' gas giant", 0.55) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit > Math.min(1.00, planet.density / 2000.0)
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density < 1000
					&& planet.blackbodyTemperature >= 1000);
		}
	};
	/** Cold "puffy" giant, implies internal heating and strong magnetic fields */
	public static final PlanetaryClass COLD_PUFFY_GIANT = new PlanetaryClass("'Puffy' gas giant", 0.55) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit <= 1.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density < 500
					&& planet.blackbodyTemperature > 20 && planet.blackbodyTemperature < 1000);
		}
	};
	/** Cold "puffy" giant with little hydrogen, implies internal heating and strong magnetic fields */
	public static final PlanetaryClass HELLIUM_COLD_PUFFY_GIANT = new PlanetaryClass("'Puffy' gas giant (hydrogen-poor)", 0.55) {
		@Override protected boolean possibleClass(Satellite planet) {
			return ((planet.molecularLimit <= 4.00 && planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20)
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density < 700
					&& planet.blackbodyTemperature < 1000);
		}
	};
	/** Rocky core, thick hydrogen/hellium atmosphere */
	public static final PlanetaryClass GAS_DWARF = new PlanetaryClass("Gas dwarf", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit <= 1.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.5
					&& planet.density > 1500 && planet.density < 3700 + planet.blackbodyTemperature
					&& planet.blackbodyTemperature > 80 && planet.blackbodyTemperature < 1700);
		}
	};
	/** Rocky core, thick hellium atmosphere */
	public static final PlanetaryClass HELLIUM_GAS_DWARF = new PlanetaryClass("Gas dwarf (hydrogen-poor)", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit > 1.00 && planet.molecularLimit <= 4.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.5
					&& planet.density > 1500 && planet.density < 4000 + planet.blackbodyTemperature
					&& planet.blackbodyTemperature > 80 && planet.blackbodyTemperature < 1700);
		}
	};
	
	/** Rocky core, thick hydrogen/hellium atmosphere */
	public static final PlanetaryClass FROZEN_GAS_DWARF = new PlanetaryClass("Frozen gas dwarf", 0.55) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit <= 4.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.density >= 2500 && planet.density < 4000
					&& planet.blackbodyTemperature <= 80);
		}
	};

	/** Late-stage hot "puffy" gas giant with atmosphere boiling away and a comet-like trail */
	public static final PlanetaryClass BOILING_GIANT = new PlanetaryClass("Boiling Giant", 0.3) {
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit > 4.00
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.9
					&& planet.density < 2200
					&& planet.blackbodyTemperature > 200);
		}
	};
	/** Low-water variant on Earth-sized planet */
	public static final PlanetaryClass DESERT = new Terrestrial("Desert planet", 0.27, 0.16) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 250 && planet.blackbodyTemperature <= 700
					&& planet.molecularLimit > 18.00 /* water vapour */);
		}
	};
	/** Earth-sized planet with oceans and water clouds, but without much hellium in the atmosphere */
	public static final PlanetaryClass EARTH_LIKE = new Terrestrial("Earth-like planet", 0.29, 0.16) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 250 && planet.blackbodyTemperature <= 330
					&& planet.molecularLimit <= 18.00 /* water vapour */ && planet.molecularLimit > 4.00);
		}
	};
	/** Variant of an Earth-like planet with a runaway greenhouse effect. See: Venus */
	public static final PlanetaryClass GREENHOUSE = new Terrestrial("Greenhouse planet", 0.65, 2.0) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 330 && planet.blackbodyTemperature <= 500
					&& planet.molecularLimit <= 18.00 /* water vapour */ && planet.molecularLimit > 4.00);
		}
	};
	
	/** Hot planets still retaining a substantial atmosphere (CO2 molar mass = 44), but not liquid water;
	 * also likely lacking a strong magnetic field */
	public static final PlanetaryClass HELL = new Terrestrial("Hell planet", 0.4, 0.5) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity < 5000
					&& (planet.blackbodyTemperature >= 500
						|| (planet.blackbodyTemperature >= 330 && planet.molecularLimit > 18.00))
					&& planet.molecularLimit <= 44.00 );
		}
	};
	
	/** Cold planet lacking a magnetic field, consisting mostly of rock */
	public static final PlanetaryClass FROZEN_ROCK = new Terrestrial("Frozen rock planet", 0.4, 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity < 5000 && planet.uncompressedDensity >= 3000
					&& planet.blackbodyTemperature <= 260
					&& planet.molecularLimit <= 44.00 /* carbon dioxide */);
		}
	};
	
	/** Airless rock, mostly relatively warm */
	public static final PlanetaryClass AIRLESS = new Terrestrial("Airless rock planet", 0.4, 0.0) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity < 5000
					&& (planet.uncompressedDensity >= 2600 || planet.blackbodyTemperature >= 260)
					&& planet.molecularLimit > 44.00 /* carbon dioxide */);
		}
	};

	/** Small rocky worlds with carbon dioxide atmosphere (mostly) */
	public static final PlanetaryClass DRY_ROCK = new Terrestrial("Dry rocky planet", 0.2, 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity <= 3500 && planet.uncompressedDensity >= 2600
					&& planet.blackbodyTemperature >= 250 && planet.blackbodyTemperature <= 330
					&& planet.molecularLimit > 18.00 && planet.molecularLimit <= 44.00 );
		}
	};


	/** Earth-like conditions, including fluid water, but no notable magnetic field */
	public static final PlanetaryClass ROCKY = new Terrestrial("Rocky planet", 0.2, 0.16) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity <= 3500 && planet.uncompressedDensity >= 2600
					&& planet.blackbodyTemperature >= 250 && planet.blackbodyTemperature <= 330
					&& planet.molecularLimit <= 18.00 /* water vapour */);
		}
	};

	/** Earth-sized planet with oceans and water clouds, thick hellium-rich atmosphere */
	public static final PlanetaryClass HIGH_PRESSURE = new Terrestrial("Earth-like planet (hellium-rich)", 0.29, 0.16) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity >= 3500 && planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 250 && planet.blackbodyTemperature <= 330
					&& planet.molecularLimit <= 4.00);
		}
	};

	/** Variant of an Earth-like planet with a runaway greenhouse effect and extremly thick hellium-rich atmosphere */
	public static final PlanetaryClass EXTREME_GREENHOUSE = new Terrestrial("Greenhouse planet (hellium-rich)", 0.65, 2.0) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 330 && planet.blackbodyTemperature <= 500
					&& planet.molecularLimit <= 4.00);
		}
	};

	/**
	 * Earth-sized planet or planetoid with a thick ice cover: Below 260 K for water ice, below 180 K for CO2 and ammonia and below 80K for methane
	 * <p>
	 * Examples: Pluto [methane] (mass 13, density 1983, temp 44, blackbody 50, albedo 0.58, mollimit 108.15)
	 *           Europa [CO2/NH3] (mass 48, density 3014, temp 102, blackbody 138, albedo 0.67, mollimit 108.68)
	 *           Eris [methane] (mass 17, density 2580, temp 43, blackbody 38, albedo 0.97, mollimit 62.96)
	 */
	public static final PlanetaryClass WATER_ICE = new Terrestrial("Ice planet", 0.65, 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity < 3000
					&& planet.blackbodyTemperature <= 260 && planet.blackbodyTemperature >= 170 );
		}
	};
	public static final PlanetaryClass AMMONIA_ICE = new Terrestrial("CO₂/ammonia ice planet", 0.65, 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity < 3000
					&& planet.blackbodyTemperature <= 180 && planet.blackbodyTemperature >= 70 );
		}
	};
	public static final PlanetaryClass METHANE_ICE = new Terrestrial("Methane ice planet", 0.65, 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity < 3000
					&& planet.blackbodyTemperature <= 80 );
		}
	};
	/**
	 * Iron-rich small planets without much or any mantle; typically close to their star or around big stars
	 * <p>
	 * Examples: Mercury (mass 330, density 5425, temp 340, blackbody 508, albedo 0.07, mollimit 90.96)
	 */
	public static final PlanetaryClass IRON = new Terrestrial("Iron planet", 0.07, 0.08) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity >= 5000 );
		}
	};
	/** Very hot rocky planets */
	public static final PlanetaryClass LAVA = new Terrestrial("Lava planet", 0.07, 0.06) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity <= 5000
					&& planet.blackbodyTemperature >= 700 );
		}
	};
	
	/** High-water content planets with temperatures between 260 K and 350 K; typically good cloud cover and greenhouse effect */
	public static final PlanetaryClass OCEAN = new Terrestrial("Ocean planet", 0.2, 1.5) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet) && planet.uncompressedDensity <= 3800
					&& planet.blackbodyTemperature >= 260 && planet.blackbodyTemperature <= 350
					&& planet.molecularLimit <= 18.00 /* water vapour */);
		}
	};
	

	// Planetoid classes
	/** Very dark, consisting mostly of carbonaceous chondite */
	public static final PlanetaryClass CARBONACEOUS_PLANETOID = new Planetoid("Carbonaceous planetoid", 0.1) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.density >= 2000 && planet.density < 4000
					&& planet.blackbodyTemperature > 200 );
		}
	};
	/** Mostly packed ice/gravel */
	public static final PlanetaryClass ICE_PLANETOID = new Planetoid("Ice planetoid", 0.6) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.density < 3500
					&& planet.blackbodyTemperature < 260 );
		}
	};
	/** Rock */
	public static final PlanetaryClass SILICATE_PLANETOID = new Planetoid("Silicate planetoid", 0.35) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.density >= 3000 );
		}
	};
	public static final PlanetaryClass METALLIC_PLANETOID = new Planetoid("Metallic planetoid", 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.density >= 4000 );
		}
	};
	/** Loose gravel stuff, but not frozen */
	public static final PlanetaryClass GRAVEL_PLANETOID = new Planetoid("Gravel planetoid", 0.2) {
		@Override protected boolean possibleClass(Satellite planet) {
			return( super.possibleClass(planet)
					&& planet.density < 2000
					&& planet.blackbodyTemperature >= 250 );
		}
	};
	
	public static final PlanetaryClass UNKNOWN = new PlanetaryClass("UNKNOWN", 0.0) {
		@Override protected boolean possibleClass(Satellite planet) {
			return false;
		}
	};
	
	public final String name;
	public final double albedo;
	
	protected PlanetaryClass(String name, double albedo)
	{
		this.name = name;
		this.albedo = albedo;
	}
	
	/** Is this planet a possible candidate for this planetary class? */
	protected abstract boolean possibleClass(Satellite planet);
	/** Average greenhouse factor (only important for terrestial planets) */
	public double avgGreenhouseFactor() {
		return 0.0;
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
			/*
			System.out.print("Multiple classification possible for \"" + planet.name + "\": ");
			for( PlanetaryClass pc : possibleClasses )
			{
				System.out.print(pc.name + " ");
			}
			System.out.println("");
			*/
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
	
	public static class GasGiant extends PlanetaryClass {
		private final int minTemp;
		private final int maxTemp;
		
		protected GasGiant(String name, double albedo, int minTemp, int maxTemp) {
			super(name, albedo);
			
			this.minTemp = minTemp;
			this.maxTemp = maxTemp;
		}
		
		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.molecularLimit <= 1.00
					&& planet.density <= 1500 + planet.blackbodyTemperature / 2.0 && planet.density > 300
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.blackbodyTemperature >= minTemp && planet.blackbodyTemperature <= maxTemp);
		}
	}
	
	public static class HelliumGiant extends PlanetaryClass {
		private final int minTemp;
		private final int maxTemp;
		
		protected HelliumGiant(String name, double albedo, int minTemp, int maxTemp) {
			super(name, albedo);
			
			this.minTemp = minTemp;
			this.maxTemp = maxTemp;
		}
		
		@Override protected boolean possibleClass(Satellite planet) {
			return ((planet.molecularLimit <= 4.00 && planet.molecularLimit > 1.00 || planet.blackbodyTemperature <= 20 || planet.density > 1500)
					&& planet.density <= 1600 + planet.blackbodyTemperature / 2.5 && planet.density > 400
					&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
					&& planet.blackbodyTemperature >= minTemp && planet.blackbodyTemperature <= maxTemp);
		}
	}
	

	public static class Terrestrial extends PlanetaryClass {
		private final double avgGreenhouseFactor;
		
		protected Terrestrial(String name, double albedo, double greenhouseFactor) {
			super(name, albedo);
			avgGreenhouseFactor = greenhouseFactor;
		}

		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.mass <= Constant.MAX_TERRESTRIAL_MASS && planet.mass >= Constant.MIN_TERRESTRIAL_MASS
					&& planet.density > 1000);
		}
		
		@Override public double avgGreenhouseFactor() {
			return avgGreenhouseFactor;
		}
	}
	
	public static class Planetoid extends PlanetaryClass {
		protected Planetoid(String name, double albedo) {
			super(name, albedo);
		}

		@Override protected boolean possibleClass(Satellite planet) {
			return (planet.mass <= Constant.MIN_TERRESTRIAL_MASS);
		}
	}

}
