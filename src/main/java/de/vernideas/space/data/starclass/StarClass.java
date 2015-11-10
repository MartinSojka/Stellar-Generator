package de.vernideas.space.data.starclass;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.common.collect.ImmutableSortedSet;

@Accessors(fluent=true)
public abstract class StarClass {
	@Getter protected final Type type;
	@Getter protected final int subType;
	@Getter protected final LuminosityClass luminosityClass;
	@Getter protected final String genString;
	
	private double minTemp = -1.0;
	private double maxTemp = -1.0;
	private double minMass = -1.0;
	private double maxMass = -1.0;
	private double safeJumpDistance = -1.0;

	protected StarClass(Type type, int subType, LuminosityClass luminosityClass) {
		this.type = type;
		this.subType = subType;
		this.luminosityClass = luminosityClass;
		this.genString = luminosityClass.string;
	}
	
	protected StarClass(Type type, int subType) {
		this(type, subType, LuminosityClass.DWARF);
	}
	
	protected StarClass(Type type) {
		this(type, 5, LuminosityClass.DWARF);
	}
	
	/** Minimum effective temperature for this class, in Kelvin */
	public double minTemp() {
		if( minTemp < 0 ) {
			minTemp = StarClassHelper.minTemp(this);
		}
		return minTemp;
	}

	/** Maximum effective temperature for this class, in Kelvin */
	public double maxTemp() {
		if( maxTemp < 0 ) {
			maxTemp = StarClassHelper.maxTemp(this);
		}
		return maxTemp;
	}
	
	/** Minimum mass for this class, in solar masses */
	public double minMass() {
		if( minMass < 0 ) {
			minMass = StarClassHelper.minMass(this);
		}
		return minMass;
	}

	/** Maximum mass for this class, in solar masses */
	public double maxMass() {
		if( maxMass < 0 ) {
			maxMass = StarClassHelper.maxMass(this);
		}
		return maxMass;
	}
	
	/**
	 * Full (human-readable) class declaration, for example "G4V", "K3II", "DA7" and so on.
	 * <p>
	 * By contract, the method should never return anything which the static parse()
	 * method of this class can't turn back into a valid StarClass instance.
	 */
	public abstract String fullDeclaration();
	
	/**
	 * (BT) safe jump distance in AU
	 */
	public double safeJumpDistance() {
		if( safeJumpDistance < 0.0 ) {
			safeJumpDistance = StarClassHelper.safeJumpDistance(this);
		}
		return safeJumpDistance;
	}
	
	@Override public String toString() {
		return "[StarClass: " + fullDeclaration() + "]";
	}

	/**
	 * Main sequence stars and typical giants; also includes subdwarfs.
	 */
	public static class Main extends StarClass {
		private String declaration = null;
		
		public Main(Type type, int subType, LuminosityClass luminosityClass) {
			super(type, subType, luminosityClass);
			// Check that we're not abused for something we're not
			if( luminosityClass == LuminosityClass.WHITE_DWARF ) {
				throw new IllegalArgumentException("Wrong luminosity class: white dwarf.");
			}
		}
		
		public Main(Type type, int subType) {
			this(type, subType, LuminosityClass.DWARF);
		}

		@Override public String fullDeclaration() {
			if( declaration == null ) {
				StringBuilder strB = new StringBuilder();
				if( this.luminosityClass == LuminosityClass.SUBDWARF )
				{
					strB.append("sd");
				}
				strB.append(this.type.toString()).append(this.subType);
				if( this.luminosityClass != LuminosityClass.SUBDWARF ) {
					strB.append(this.luminosityClass.toString());
				}
				declaration = strB.toString();
			}
			return declaration;
		}
	}
	
	/**
	 * White dwarfs - subdwarfs can use the Main class
	 */
	public static class WhiteDwarf extends StarClass {
		@Getter protected Set<WhiteDwarfType> dwarfType;
		private String declaration = null;
		
		public WhiteDwarf(int subType, List<WhiteDwarfType> dwarfTypes) {
			super(Type.D, subType, LuminosityClass.WHITE_DWARF);
			// Treeset, since we want sorting
			this.dwarfType = ImmutableSortedSet.<WhiteDwarfType>copyOf(dwarfTypes);
		}

		public WhiteDwarf(int subType, WhiteDwarfType ... dwarfType) {
			this(subType, Arrays.asList(dwarfType));
		}
		
		@Override public String fullDeclaration() {
			if( declaration == null ) {
				StringBuilder strB = new StringBuilder();
				strB.append("D");
				for( WhiteDwarfType type : dwarfType ) {
					strB.append(type.string);
				}
				strB.append(this.subType);
				declaration = strB.toString();
			}
			return declaration;
		}
	}
}
