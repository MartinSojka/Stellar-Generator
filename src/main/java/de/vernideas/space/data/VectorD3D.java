package de.vernideas.space.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

/** Double vector class */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class VectorD3D {
	@Wither public final double x;
	@Wither public final double y;
	@Wither public final double z;
	
	public VectorD3D() {
		this(0.0, 0.0, 0.0);
	}
	
	public double rightAscension(VectorD3D from) {
		if( y == from.y && x == from.x ) {
			return 0.0;
		}
		double result = Math.atan2(y - from.y, x - from.x);
		if( result < 0.0 ) {
			result = 2.0 * Math.PI + result;
		}
		return result;
	}

	public double declination(VectorD3D from) {
		if( x == from.x && y == from.y && z == from.z ) {
			return 0.0;
		}
		return Math.atan2(z - from.z, Math.sqrt(Math.pow(x - from.x, 2.0) + Math.pow(y - from.y, 2.0)));
	}
	
	public double distance(VectorD3D to) {
		return Math.sqrt(Math.pow(x - to.x, 2.0) + Math.pow(y - to.y, 2.0) + Math.pow(z - to.z, 2.0));
	}
}
