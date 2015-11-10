package de.vernideas.space.data.starclass;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.starclass.StarClass.WhiteDwarf;

public class WhiteDwarfParser implements StarClassParser {

	@Override public StarClass parse(String string) {
		// white dwarf (luminosity class VII)
		if( string.startsWith("D") && string.length() > 1 ) {
			// A (potentially empty) string of white dwarf types, followed by a subtype number
			List<WhiteDwarfType> variants = new ArrayList<WhiteDwarfType>();
			int subType = -1;
			int index = 1;
			while( string.length() > index && subType < 0 )
			{
				try {
					WhiteDwarfType variant = WhiteDwarfType.byString(string.substring(index, index + 1));
					variants.add(variant);
					++ index;
				}
				catch( IllegalArgumentException iae ) {
					// This means we're done with parsing the white dwarf variants - time to try and add the subtype
					subType = Integer.parseInt(string.substring(index, index + 1));
				}
			}
			if( subType > -1 )
			{
				return new WhiteDwarf(subType, variants);
			}
		}
		return null;
	}
	
	private List<String> validStarClasses = null;
	@Override public List<String> validStarClasses() {
		if( null == validStarClasses )
		{
			validStarClasses = new ArrayList<String>();
			String[] subclasses = new String[]{"", "A", "B", "O", "Q", "Z",
					"AB", "AO", "AQ", "AZ", "BO", "BQ", "BZ", "QZ",
					"ABO", "ABQ", "ABZ", "AOQ", "AOZ", "AQZ", "BOQ", "BOZ", "BQZ", "OQZ",
					"ABOQ", "ABOZ", "ABQZ", "AOQZ", "BOQZ",
					"ABOQZ", "C", "X"};
			for( String subclass : subclasses ) {
				for( int i = 0; i <= 9; ++ i ) {
					validStarClasses.add(String.format("D%s%d", subclass, i));
				}
				for( int i = 0; i <= 9; ++ i ) {
					validStarClasses.add(String.format("D%sV%d", subclass, i));
				}
			}
		}
		return validStarClasses;
	}

}