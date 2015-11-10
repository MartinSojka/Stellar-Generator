package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;

@ToString(callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Moon extends Satellite implements Location {
	@Builder(fluent=true, chain=true)
	private Moon(String name, double mass, double diameter, @NonNull Planet parent, Orbit orbit, float rotationPeriod)
	{
		super(name, mass, diameter, parent, orbit, rotationPeriod);
	}
}
