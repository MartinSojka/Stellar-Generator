package de.vernideas.space.data;

import java.util.Arrays;
import java.util.List;

import com.artemis.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=false)
public class Faction extends Component {
	public static final Faction NONE = new NullFaction();
	
	@Setter @Getter private List<Person> members;
	@Setter @Getter private Person leader;
	
	public int memberCount() {
		return members.size();
	}

	public boolean valid() { return true; }
	
	public static enum Rank { MEMBER, OFFICER, LEADER }
	
	/** No-Op null object class */
	@ToString
	@EqualsAndHashCode(callSuper=true)
	private static class NullFaction extends Faction
	{
		@Override public Faction members(List<Person> members) { return this; }
		@Override public List<Person> members() { return Arrays.asList(new Person[0]); }
		@Override public int memberCount() { return 0; }
		@Override public boolean valid() { return false; }
		@Override public Faction leader(Person leader) { return this; }
		@Override public Person leader() { return null; }
	}
}
