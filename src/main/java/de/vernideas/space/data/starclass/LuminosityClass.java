package de.vernideas.space.data.starclass;

public enum LuminosityClass {
	HYPERGIANT("0"), SUPERGIANT("I"), SUPERGIANT_BRIGHT("Ia"), SUPERGIANT_INTERMEDIATE("Iab"), SUPERGIANT_DARK("Ib"),
	BRIGHT_GIANT("II"), GIANT("III"), SUBGIANT("IV"), DWARF("V"), SUBDWARF("VI"), WHITE_DWARF("VII"),
	// Intermediate types
	BRIGHT_GIANT_EVOLVED("I/II"), GIANT_EVOLVED("II/III"), SUBGIANT_EVOLVED("III/IV"), DWARF_EVOLVED("IV/V");
	
	public final String string;
	
	private LuminosityClass(String string) {
		this.string = string;
	}
	
	@Override public String toString() {
		return string;
	}
	
	// We need this since luminosity classes are variable length and often start with the same chars ...
	public static LuminosityClass parse(String lc) {
		if( lc.startsWith("I/II") ) { return BRIGHT_GIANT_EVOLVED; }
		if( lc.startsWith("I-II") ) { return BRIGHT_GIANT_EVOLVED; }
		if( lc.startsWith("Ib/II") ) { return BRIGHT_GIANT_EVOLVED; }
		if( lc.startsWith("Ib-II") ) { return BRIGHT_GIANT_EVOLVED; }
		if( lc.startsWith("II/III") ) { return GIANT_EVOLVED; }
		if( lc.startsWith("II-III") ) { return GIANT_EVOLVED; }
		if( lc.startsWith("III/IV") ) { return SUBGIANT_EVOLVED; }
		if( lc.startsWith("III-IV") ) { return SUBGIANT_EVOLVED; }
		if( lc.startsWith("IV/V") ) { return DWARF_EVOLVED; }
		if( lc.startsWith("IV-V") ) { return DWARF_EVOLVED; }
		if( lc.startsWith("III") ) { return GIANT; }
		if( lc.startsWith("II") ) { return BRIGHT_GIANT; }
		if( lc.startsWith("IV") ) { return SUBGIANT; }
		if( lc.startsWith("Ia-0") ) { return HYPERGIANT; } // Alias
		if( lc.startsWith("Ia+") ) { return HYPERGIANT; } // Alias
		if( lc.startsWith("Iab") ) { return SUPERGIANT_INTERMEDIATE; }
		if( lc.startsWith("Ia") ) { return SUPERGIANT_BRIGHT; }
		if( lc.startsWith("Ib") ) { return SUPERGIANT_DARK; }
		if( lc.startsWith("I") ) { return SUPERGIANT; } // Includes Ia, Iab and Ib
		if( lc.startsWith("O") ) { return HYPERGIANT; }
		if( lc.startsWith("VII") ) { return WHITE_DWARF; }
		if( lc.startsWith("VI") ) { return SUBDWARF; }
		if( lc.startsWith("V") ) { return DWARF; }
		return null;
	}
}