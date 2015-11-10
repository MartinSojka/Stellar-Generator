package de.vernideas.space.data.starclass;

import java.util.List;

// TODO:
// Implement Wolf-Rayet stars (W spectral types) https://en.wikipedia.org/wiki/Wolf%E2%80%93Rayet_star
// Implement Carbon stars (C spectral types) https://en.wikipedia.org/wiki/Carbon_star
// Implement S-type stars (S spectral types) https://en.wikipedia.org/wiki/S-type_star
// Implement intermediary classes (MS, SC)
// (Intrinsic) Variable stars https://en.wikipedia.org/wiki/Variable_star#Intrinsic_variable_stars

public interface StarClassParser {
	StarClass parse(String string);
	List<String> validStarClasses();
}