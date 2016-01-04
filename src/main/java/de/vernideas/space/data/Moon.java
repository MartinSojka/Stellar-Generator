package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(callSuper=true)
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true)
public class Moon extends Satellite implements Location {
	public Moon(String name)
	{
		super(name);
	}
	
	@Override public Planet parent() {
		return (Planet)this.parent;
	};
}
