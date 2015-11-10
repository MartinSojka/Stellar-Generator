package de.vernideas.space.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Moon extends Satellite implements Location {
	@Builder
	private Moon(String name, double mass, double diameter, @NonNull Planet parent, Orbit orbit, float rotationPeriod)
	{
		super(name, mass, diameter, parent, orbit, rotationPeriod);
	}
}
