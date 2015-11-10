package de.vernideas.space.data.starclass;

public enum WhiteDwarfType {
	HYDROGEN("A"), NEUTRAL_HELIUM("B"), IONIZED_HELIUM("O"), CARBON("Q"), METAL("Z"), MIXED("C"), UNKNOWN("X"), VARIABLE("V");
	
	public final String string;
	
	private WhiteDwarfType(String string)
	{
		this.string = string;
	}
	
	public static WhiteDwarfType byString(String val) {
		switch( val ) {
		case "A": return HYDROGEN;
		case "B": return NEUTRAL_HELIUM;
		case "O": return IONIZED_HELIUM;
		case "Q": return CARBON;
		case "Z": return METAL;
		case "C": return MIXED;
		case "X": return UNKNOWN;
		case "V": return VARIABLE;
		default: throw new IllegalArgumentException(String.format("Unknown white dwarf variant %s", val));
		}
	}
	
	@Override public String toString() {
		return string;
	}
}