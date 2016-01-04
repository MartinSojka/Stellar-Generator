package de.vernideas.space.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** Encompasses planets and planetoids */
@ToString(of={"moons", "minor", "capitalPlace"}, callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Planet extends Satellite implements Location {
	@NonNull public final List<Moon> moons;
	public final boolean minor;
	
	@Getter @Setter private Place capitalPlace;
	
	@Getter @Setter private boolean valid;
	
	public Planet(String name, boolean minor)
	{
		super(name);
		
		this.moons = new ArrayList<Moon>();
		this.minor = minor;
	}
	
	@Override public Star parent() {
		return (Star)this.parent;
	};
	
	public static Planet byID(int id) { return null; }
}
