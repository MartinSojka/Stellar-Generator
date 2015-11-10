package de.vernideas.space.data.starclass;

import java.util.ArrayList;
import java.util.List;

import de.vernideas.space.data.starclass.StarClass.Main;

public class MainParser implements StarClassParser {

	@Override public StarClass parse(String string) {
		// Main sequence star or derived giant
		Type t = Type.valueOf(string.substring(0, 1));
		if( t.mainSequence )
		{
			int subType = 5;
			LuminosityClass lc = null;
			if( string.length() > 1 )
			{
				subType = Integer.parseInt(string.substring(1, 2));
				if( string.length() > 2 )
				{
					lc = LuminosityClass.parse(string.substring(2));
				}
			}
			if( lc == null ) // Not there? Unparsable?
			{
				lc = LuminosityClass.DWARF;
			}
			return new Main(t, subType, lc);
		}
		return null;
	}

	private List<String> validStarClasses = null;
	@Override public List<String> validStarClasses() {
		if( null == validStarClasses )
		{
			validStarClasses = new ArrayList<String>();
			String[] prefixes = new String[]{"O", "B", "A", "F", "G", "K", "M", "L", "T"};
			for( String lc : new String[]{"V", "IV", "III", "II", "I", "O"} ) {
				for( int i = 0; i <= 9; ++ i ) {
					for( String prefix : prefixes ) {
						validStarClasses.add(String.format("%s%d%s", prefix, i, lc));
					}
				}
			}
		}
		return validStarClasses;
	}
}