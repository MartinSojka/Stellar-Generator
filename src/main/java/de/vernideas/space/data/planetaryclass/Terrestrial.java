package de.vernideas.space.data.planetaryclass;

import java.util.function.Predicate;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class Terrestrial extends PlanetaryClass {
	private final double avgGreenhouseFactor;
	
	protected Terrestrial(String name, Predicate<Satellite> predicate, double albedo, double greenhouseFactor) {
		super(name, predicate, albedo);
		avgGreenhouseFactor = greenhouseFactor;
	}

	@Override public boolean validClass(Satellite planet) {
		return (super.validClass(planet)
				&& planet.mass() <= Constant.MAX_TERRESTRIAL_MASS && planet.mass() >= Constant.MIN_TERRESTRIAL_MASS
				&& planet.density() > 800);
	}
	
	@Override public double avgGreenhouseFactor() {
		return avgGreenhouseFactor;
	}
}