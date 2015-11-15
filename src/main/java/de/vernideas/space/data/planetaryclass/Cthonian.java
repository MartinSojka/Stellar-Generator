package de.vernideas.space.data.planetaryclass;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class Cthonian extends PlanetaryClass {
	public Cthonian() {
		super("Cthonian planet", 0.3);
	}

	@Override protected boolean possibleClass(Satellite planet) {
		return ((planet.molecularLimit > 4.00 || planet.density + planet.blackbodyTemperature / 10 > 2500)
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.9
				&& planet.density > 2000
				&& planet.blackbodyTemperature > 300);
	}
}