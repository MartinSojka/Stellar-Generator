package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;

/** Encompasses planets and planetoids */
@ToString(of={"name", "moons", "yearLength", "minor", "capitalPlace"}, callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Planet extends Satellite implements Location {
	@NonNull public final List<Moon> moons;
	public final double yearLength;
	public final boolean minor;
	
	@Getter @Setter private Place capitalPlace;
	
	@Getter @Setter private boolean valid;
	
	@Builder(fluent=true, chain=true)
	private Planet(String name, double mass, double diameter, @NonNull Star parent, @NonNull Orbit orbit, float rotationPeriod, double planetRadius, boolean minor)
	{
		super(name, mass, diameter, parent, orbit, rotationPeriod);
		
		this.moons = new ArrayList<Moon>();
		this.minor = minor;
		
		yearLength = Math.round(siderealPeriod);
	}
	
	public static Planet byID(int id) { return null; }

}
