package de.vernideas.space.data.planetaryclass;

import java.util.function.Predicate;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GasGiant extends PlanetaryClass {
	private final double molLimit;
	private final double minDensity;
	private final double maxDensity;
	
	protected GasGiant(String name, Predicate<Satellite> predicate, double albedo, double molLimit,
			double minDensity, double maxDensity) {
		super(name, predicate, albedo);
		
		this.molLimit = molLimit;
		this.minDensity = minDensity;
		this.maxDensity = maxDensity;
	}
	
	@Override public boolean validClass(Satellite planet) {
		return (super.validClass(planet) && planet.molecularLimit <= molLimit
				&& planet.density <= maxDensity && planet.density > minDensity
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS);
	}
}