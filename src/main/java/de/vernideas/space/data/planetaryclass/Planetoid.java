package de.vernideas.space.data.planetaryclass;

import java.util.function.Predicate;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;

public class Planetoid extends PlanetaryClass {
	protected Planetoid(String name, Predicate<Satellite> predicate, double albedo) {
		super(name, predicate, albedo);
	}

	@Override public boolean validClass(Satellite planet) {
		return (super.validClass(planet) && planet.mass() <= Constant.MIN_TERRESTRIAL_MASS);
	}
}