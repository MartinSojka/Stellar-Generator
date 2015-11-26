package de.vernideas.space.data.planetaryclass;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.OrbitalZone;
import de.vernideas.space.data.Satellite;

public class Cthonian extends PlanetaryClass {
	public Cthonian() {
		super("Cthonian planet", null, 0.3);
		validZones(OrbitalZone.HOT, OrbitalZone.HABITABLE);
		temperatureLimits(300, 5000);
	}

	@Override public boolean validClass(Satellite planet) {
		return (super.validClass(planet)
				&& (planet.molecularLimit > 4.00 || planet.density + planet.blackbodyTemperature / 10 > 2500)
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS * 0.9
				&& planet.density > 2000);
	}
}