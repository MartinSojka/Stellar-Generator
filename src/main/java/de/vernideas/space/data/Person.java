package de.vernideas.space.data;

import java.util.List;

import com.artemis.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class Person {
	// My entity
	@Getter private Entity entity;
	
	@Setter @Getter private String name;
	@Setter @Getter private String birthName;
	@Setter @Getter private StarDate birthday;
	@Setter @Getter private StarDate deathday;
	@Setter @Getter private Person father;
	@Setter @Getter private Person mother;
	@Setter @Getter private StarDate fertilityStart;
	@Setter @Getter private StarDate fertilityEnd;
	@Getter @NonNull private List<Person> children;
	@Setter @Getter private Gender gender;
	@Setter @Getter private StarDate pregnancyStart;
	@Setter @Getter private StarDate pregnancyEnd;
	@Setter @Getter private Location loc;
	// Attributes
	// Traits
	// Skills
	// Factions
	// Ownership - Items
	// Ownership - Ships
	// Ownership - Animals
	// Ownership - Slaves
	// Ownership - Facilities
	// Ownership - Titles
	
	@Setter @Getter private Nation nation;
	
	/** Get a person by their ID, or null if the ID doesn't correspond to one. */
	public static Person byID(int id) { return null; }
	
}
