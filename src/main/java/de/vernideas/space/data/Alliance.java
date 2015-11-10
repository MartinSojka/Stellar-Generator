package de.vernideas.space.data;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
/**
 * To implemented alliance (and treaty) types:
 * <p>
 * UNION - both sides have the same sovereign and automatically share wars.<br>
 * FULL - both sides are obliged to come to each other's aid when attacked or declaring war.<br>
 * DEFENSIVE - both sides are obliged to come to each other's aid when attacked<br>
 */
public abstract class Alliance {
	public static final Alliance NONE = new NoAlliance();
	
	@Getter @Setter private List<Person> members;
	@Getter @Setter private Person leader;
	
	public abstract int membersNum();
	
	private static class NoAlliance extends Alliance {
		@Override public List<Person> members() { return Lists.<Person>newArrayList(); }
		@Override public Alliance members(List<Person> members) { return this; }
		@Override public Alliance leader(Person leader) { return this; }
		@Override public int membersNum() { return 1; }
	}

}
