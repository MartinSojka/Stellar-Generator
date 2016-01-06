package de.vernideas.space.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/** Place on (or near) surface of a rounded body, like a planet or moon, or in close orbit around it */
@ToString
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class City extends Place {
	/** On which planet or moon this city lies on */
	@NonNull public final Satellite planet;
	/** Position relative to the planet's prime meridian; -180.0 to +180.0 */
	public final float longitude;
	/** Offset in minute */
	public final int dayOffset;

	@Builder
	protected City(@NonNull String name, Satellite planet, float longitude) {
		super(name, planet.dayLength());
		
		if( longitude < -180.0f || longitude > 180.0f )
		{
			throw new IllegalArgumentException("Longitude" + longitude + " outside of [-180, +180]");
		}
		
		this.planet = planet;
		this.longitude = longitude;
		this.dayOffset = (int)(longitude * dayLength / 360.0f);
	}

}