package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/** A specific place somewhere, like a city on the surface of a planet or moon, orbital station, deep-space station, ... */
@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class Place implements Location {
	/** Human-readable or -translatable name */
	public final String name;
	/** Day length in minutes */
	public final double dayLength;
	
	protected Place(String name, double dayLength)
	{
		this.name = name;
		this.dayLength = dayLength;
	}
}
