package de.vernideas.space.data.starclass;

public enum LuminosityClass {
	HYPERGIANT("0"), SUPERGIANT("I"), BRIGHT_GIANT("II"), GIANT("III"), SUBGIANT("IV"), DWARF("V"), SUBDWARF("VI"), WHITE_DWARF("VII");
	
	public final String string;
	
	private LuminosityClass(String string) {
		this.string = string;
	}
	
	@Override public String toString() {
		return string;
	}
	
	// We need this since luminosity classes are variable length and often start with the same chars ...
	public static LuminosityClass parse(String lc) {
		if( lc.startsWith("III") ) { return GIANT; }
		if( lc.startsWith("II") ) { return BRIGHT_GIANT; }
		if( lc.startsWith("IV") ) { return SUBGIANT; }
		if( lc.startsWith("I") ) { return SUPERGIANT; }
		if( lc.startsWith("O") ) { return HYPERGIANT; }
		if( lc.startsWith("VII") ) { return WHITE_DWARF; }
		if( lc.startsWith("VI") ) { return SUBDWARF; }
		if( lc.startsWith("V") ) { return DWARF; }
		return null;
	}
}