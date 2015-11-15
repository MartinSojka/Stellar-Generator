package de.vernideas.space.data.planetaryclass;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class HelliumGiant extends PlanetaryClass {
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