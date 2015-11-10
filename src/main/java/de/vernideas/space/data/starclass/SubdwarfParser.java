package de.vernideas.space.data.starclass;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.starclass.StarClass.Main;

public class SubdwarfParser implements StarClassParser {

	@Override public StarClass parse(String string) {
		// subdwarf (luminosity class VI)
		if( string.startsWith("sd") && string.length() > 2 ) {
			// Format is: type (one of O, B, A, F, G, K, M, L, T), optionally followed by sub-type (0-9)
			Type t = Type.valueOf(string.substring(2, 3));
			if( t.mainSequence ) {
				int subType = 5;
				if( string.length() > 3 ) {
					subType = Integer.parseInt(string.substring(3, 4));
				}
				return new Main(t, subType, LuminosityClass.SUBDWARF);
			}
		}
		return null;
	}
	
	private List<String> validStarClasses = null;
	@Override public List<String> validStarClasses() {
		if( null == validStarClasses )
		{
			validStarClasses = new ArrayList<String>();
			String[] prefixes = new String[]{"O", "B", "A", "F", "G", "K", "M", "L", "T"};
			for( int i = 0; i <= 9; ++ i ) {
				for( String prefix : prefixes ) {
					validStarClasses.add(String.format("sd%s%d", prefix, i));
				}
			}
		}
		return validStarClasses;
	}
}