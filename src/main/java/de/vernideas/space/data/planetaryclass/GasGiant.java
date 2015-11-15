package de.vernideas.space.data.planetaryclass;

import java.util.function.Function;

import de.vernideas.space.data.Constant;
import de.vernideas.space.data.Satellite;
import lombok.Data;

@Data
public class GasGiant extends PlanetaryClass {
	private final int minTemp;
	private final int maxTemp;
	private Function<Satellite, Double> maxDensity;
	
	protected GasGiant(String name, double albedo, int minTemp, int maxTemp) {
		super(name, albedo);
		
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
	}
	
	@Override protected boolean possibleClass(Satellite planet) {
		return (planet.molecularLimit <= 1.00
				&& planet.density <= 1500 + planet.blackbodyTemperature / 2.0 && planet.density > 300
				&& planet.mass >= Constant.MAX_TERRESTRIAL_MASS
				&& planet.blackbodyTemperature >= minTemp && planet.blackbodyTemperature <= maxTemp);
	}
}