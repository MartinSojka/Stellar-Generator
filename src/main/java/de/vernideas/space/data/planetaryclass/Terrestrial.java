package de.vernideas.space.data.planetaryclass;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class Terrestrial extends PlanetaryClass {
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