package de.vernideas.space.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Holding composition and the amount of them for a given planet
 * (mass recorded as a percentage of a given planet's mass)
 */
public class Atmosphere {
	public final Map<Gas, Double> gases = new HashMap<Gas, Double>();
	public double planetaryPercentage;
}
