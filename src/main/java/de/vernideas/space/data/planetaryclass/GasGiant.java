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
	private final double minTemp;
	private final double maxTemp;
	private final double minDensity;
	private final double maxDensity;
	
	protected GasGiant(String name, Predicate<Satellite> predicate, double albedo, double molLimit,
			double minTemp, double maxTemp, double minDensity, double maxDensity) {
		super(name, predicate, albedo);
		
		this.molLimit = molLimit;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.minDensity = minDensity;
		this.maxDensity = maxDensity;
	}
	
	@Override protected boolean possibleClass(Satellite planet) {
		return (super.possibleClass(planet) && planet.molecularLimit <= molLimit
				&& planet.density <= maxDensity && planet.density > minDensity
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
				&& planet.blackbodyTemperature >= minTemp && planet.blackbodyTemperature <= maxTemp);
	}
}