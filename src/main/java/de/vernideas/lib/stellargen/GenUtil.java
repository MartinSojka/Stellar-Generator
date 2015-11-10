package de.vernideas.lib.stellargen;

public final class GenUtil {
	private static String roman[] = {"ↂ", "Mↂ", "ↁ", "Mↁ", "M","CM","D", "CD","C","XC","L","XL","X","IX","V","IV","I"};
	private static int arab[] = {10000, 9000, 5000, 4000, 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	public static String romanNumber(int number)
	{
	    StringBuilder result = new StringBuilder();
	    int i = 0;
	    while (number > 0 || arab.length == (i - 1))
	    {
	        while ((number - arab[i]) >= 0)
	        {
	            number -= arab[i];
	            result.append(roman[i]);
	        }
	        ++ i;
	    }
	    return result.toString();
	}
	
	private static String subscripts[] = {"₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉"};
	public static String subscriptNumber(int number)
	{
		StringBuilder result = new StringBuilder();
		
		int multiplier = 100;
		if( number < 100 ) { multiplier = 10; }
		if( number < 10 ) { multiplier = 1; }
		while( multiplier > 0 )
		{
			int rest = number / multiplier;
			result.append(subscripts[rest]);
			number %= multiplier;
			multiplier /= 10;
		}
		
		return result.toString();
		
	}
	
}
