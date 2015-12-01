package de.vernideas.lib.stellargen;

public final class GenUtil {
	private static String roman[] = {"ↂ", "Mↂ", "ↁ", "Mↁ", "M","CM","D", "CD","C","XC","L","XL","X","IX","V","IV","I"};
	private static int arab[] = {10000, 9000, 5000, 4000, 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	
	public static String romanNumber(int number)
	{
		StringBuilder result = new StringBuilder();
		int i = 0;
		while( number > 0 || arab.length == i - 1 ) {
			while( number - arab[i] >= 0 ) {
				number -= arab[i];
				result.append(roman[i]);
			}
			++ i;
		}
		return result.toString();
	}

	private static String subscripts[] = {"₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉"};
	
	public static String subscriptNumber(int number) {
		StringBuilder result = new StringBuilder();

		int multiplier = 100;
		if( number < 100 ) {
			multiplier = 10;
		}
		if( number < 10 ) {
			multiplier = 1;
		}
		while( multiplier > 0 ) {
			int rest = number / multiplier;
			result.append(subscripts[rest]);
			number %= multiplier;
			multiplier /= 10;
		}
		return result.toString();
	}

	/** Linear interpolation */
	public static double lerp(double min, double max, double val) {
		return (1.0 - val) * min + val * max;
	}
	
	/** Linear interpolation */
	public static int lerp(int min, int max, double val) {
		return Long.valueOf(Math.round((1.0 - val) * min + val * max)).intValue();
	}

	/** Cubic Hermite spline interpolation with tangents = 0 ("flat") at both ends */
	public static double cspline(double min, double max, double val) {
		return lerp(min, max, -2.0 * val * val * val + 3.0 * val * val);
	}
	
	/** Cubic Hermite spline interpolation with tangents specified */
	public static double cspline(double min, double max, double minTangent, double maxTangent, double val) {
		double val2 = val * val;
		double val3 = val * val * val;
		return lerp(min, max, -2.0 * val3 + 3.0 * val2)
				+ (val3 - 2.0 * val2 + val) * minTangent + (val3 - val2) * maxTangent;
	}

}
