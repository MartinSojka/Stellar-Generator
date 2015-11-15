package de.vernideas.space.data.planetaryclass;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class Planetoid extends PlanetaryClass {
	protected Planetoid(String name, double albedo) {
		super(name, albedo);
	}

	@Override protected boolean possibleClass(Satellite planet) {
		return (planet.mass <= Constant.MIN_TERRESTRIAL_MASS);
	}
}