package de.vernideas.space.data.starclass;

import java.util.List;

public interface StarClassParser {
	StarClass parse(String string);
	List<String> validStarClasses();
}