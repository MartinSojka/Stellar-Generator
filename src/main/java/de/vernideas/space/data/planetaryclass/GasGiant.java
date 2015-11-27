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
	
	protected GasGiant(String name, Predicate<Satellite> predicate, double albedo, double molLimit) {
		super(name, predicate, albedo);
		
		this.molLimit = molLimit;
		// We just care about the metallic hydrogen and similar in the inside,
		// so let's make it comparatively incompressible
		materialCompressibility(1.0e-12, 2.0e-12, 1.0, 0.0);
	}
	
	@Override public boolean validClass(Satellite planet) {
		return (super.validClass(planet) && planet.molecularLimit <= molLimit
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS);
	}
}