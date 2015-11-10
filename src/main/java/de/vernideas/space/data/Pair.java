package de.vernideas.space.data;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Pair<A, B> {
	@NonNull public final A first;
	@NonNull public final B second;
	
	public static <A, B> Pair<A, B> of(A first, B second) {
		return new Pair<A, B>(first, second);
	}
}
