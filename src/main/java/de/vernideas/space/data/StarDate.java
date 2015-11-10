package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *  A date representation independant of the local date on any given planet or moon.
 *  
 *  Basically, a count of game time ticks (minutes) since an arbitrary "point zero"
 */
@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public class StarDate {
	@Getter @Setter private int month;
	@Getter @Setter private int year;
}
