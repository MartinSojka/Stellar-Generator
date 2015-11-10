package test;

import de.vernideas.lib.stellargen.StarGenerator;
import de.vernideas.lib.stellargen.SystemGenerator;
import de.vernideas.space.data.Star;
import de.vernideas.space.data.Universe;

public final class StarGenTest1 {

	public static void main(String[] args) {
		Universe u = new Universe();
		Star s = StarGenerator.star(u);
		System.out.println(s);
	}

}
