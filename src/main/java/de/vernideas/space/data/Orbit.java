package de.vernideas.space.data;

import java.util.Locale;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@EqualsAndHashCode
public class Orbit {
	/**
	 * Semimajor axis; the average distance of the body to whater it is orbiting.
	 *  */
	@Wither public final double radius;
	/**
	 * 0.0 for perfectly round orbits, below 1.0 for all the other (elliptical) ones.
	 */
	@Wither public final float eccentricity;
	/**
	 * In radians to the main body's equator
	 */
	@Wither public final float inclination;
	public final double pericenter;
	public final double apocenter;
	
	public Orbit(double r, float e, float i)
	{
		if( r < 0.0 || e < 0.0f || e >= 1.0f )
		{
			throw new IllegalArgumentException("Illegal orbital parameters (r=" + r + ", e=" + e + ", i=" + i + ")");
		}
		// Limit inclination
		while( i < 0.0f )
		{
			i += 2 * Math.PI;
		}
		while( i > 2 * Math.PI )
		{
			i -= 2 * Math.PI;
		}
		radius = r;
		eccentricity = e;
		inclination = i;
		
		pericenter = (1.0f - e) * r;
		apocenter = (1.0f + e) * r;
	}
	
	public OrbitalZone orbitalZone(Star star) {
		if( radius < star.habitableZoneMin ) { return OrbitalZone.HOT; }
		if( radius < star.habitableZoneMax ) { return OrbitalZone.HABITABLE; }
		if( radius < star.frostLine ) { return OrbitalZone.COLD; }
		return OrbitalZone.FROZEN;
	}
	
	// for @Wither
	private Orbit(double r, float e, float i, double _ignore1, double _ignore2)
	{
		this(r, e, i);
	}
	
	public String printablePlanetString() {
		return String.format(Locale.ROOT, "radius %.3f AU, eccentricity %.4f, inclination %.2f°",
				radius / Constant.AU, eccentricity, Math.toDegrees(inclination));
	}
}
