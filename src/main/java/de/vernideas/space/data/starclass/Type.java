package de.vernideas.space.data.starclass;

public enum Type {
	O, B, A, F, G, K, M, L, T, D(false); // TODO: W, C and S
	
	public final boolean mainSequence;
	
	private Type() {
		this.mainSequence = true;
	}
	
	private Type(boolean mainSequence) {
		this.mainSequence = mainSequence;
	}
}
