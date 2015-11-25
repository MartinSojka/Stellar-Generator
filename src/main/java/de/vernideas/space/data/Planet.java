package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.planetaryclass.PlanetaryClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** Encompasses planets and planetoids */
@ToString(of={"moons", "yearLength", "minor", "capitalPlace"}, callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Planet extends Satellite implements Location {
	@NonNull public final List<Moon> moons;
	public final double yearLength;
	public final boolean minor;
	
	@Getter @Setter private Place capitalPlace;
	
	@Getter @Setter private boolean valid;
	
	@Builder
	private Planet(String name, double mass, double diameter, @NonNull Star parent, @NonNull Orbit orbit,
			float rotationPeriod, double compressibility, boolean minor, PlanetaryClass planetaryClass)
	{
		super(name, mass, diameter, parent, orbit, rotationPeriod, compressibility, planetaryClass);
		
		this.moons = new ArrayList<Moon>();
		this.minor = minor;
		
		yearLength = Math.round(siderealPeriod);
	}
	
	public static Planet byID(int id) { return null; }

}
