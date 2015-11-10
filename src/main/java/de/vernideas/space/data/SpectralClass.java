package de.vernideas.space.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(of={"spec","meanEffectiveTemperature"})
@EqualsAndHashCode(of={"spec"})
public class SpectralClass {
	/** One of O, B, A, F, G, K, M, L, T, Y, C (Carbon), S, D
	 * 
	 *  Habitable systems are in the range of O-K, life-creating F-K
	 *  */
	public final String classification;
	/** Temperature bracket within classification, 0-9 */
	public final int temperature;
	public final double meanEffectiveTemperature;
	/** Luminosity class
	 * O (hypergiant), I (supergiant), II (bright giant), III (giant), IV (sub-giant)
	 * V (main sequence star), VI (sub-dwarf), VII (white dwarf), VIII (brown dwarf)
	 */
	public final String luminosityClass;
	/** Full string representation */
	public final String spec;
	
	private static List<String> validClassifications =
			Arrays.<String>asList("O", "B", "A", "F", "G", "K", "M", "L", "T", "Y", "C", "S", "D");
	private static List<String> validLuminosityClasses =
			Arrays.<String>asList("O", "I", "II", "III", "IV", "V", "VI", "VII", "VIII");
	
	/*
	 * Effective temperature table
	 *  	0 		1 		2 		3 		4 		5 		6 		7 		8 		9
	 * O 	60000 	55000 	52000 	48000 	45000 	42000 	39000 	36000 	33000 	30000
	 * B 	27000 	24000 	20000 	17000 	14500 	12500 	11700 	11100 	10600 	10200
	 * A 	9800 	9500 	9200 	8900 	8700 	8500 	8300 	8100 	7900 	7700
	 * F 	7500 	7300 	7100 	6900 	6700 	6500 	6400 	6300 	6200 	6100
	 * G 	6000 	5900 	5800 	5700 	5600 	5500 	5400 	5350 	5300 	5250
	 * K 	5200 	5100 	5000 	4900 	4800 	4650 	4500 	4300 	4100 	3900
	 * M 	3700 	3550 	3400 	3250 	3100 	2950 	2800 	2650 	2500 	2400
	 * L 	2300 	2200 	2100 	2000 	1900 	1800 	1700 	1600 	1500 	1400
	 * T 	1300 	1200 	1100 	1000 	900 	800 	700 	650 	600 	550
	 * Y 	500 	450 	400 	470 	440 	410 	380 	360 	340 	320 
	 */
	private static final HashMap<String, Double> effectiveTemperature = new HashMap<String, Double>(100);
	
	public SpectralClass(String spectralClass)
	{
		this(spectralClass.substring(0, 1), Integer.valueOf(spectralClass.substring(1, 2)));
	}
	
	public SpectralClass(String type, int temp)
	{
		this(type, temp, "V");
	}

	public SpectralClass(String type, int temp, String lum)
	{
		if( !validClassifications.contains(type) )
		{
			throw new IllegalArgumentException("Unknown spectrall class identifier '" + type + "'");
		}
		if( !validLuminosityClasses.contains(lum) )
		{
			throw new IllegalArgumentException("Unknown luminosity class '" + lum + "'");
		}
		classification = type;
		temperature = temp;
		luminosityClass = lum;
		spec = type + temp + lum;
		Double effectiveTemp = effectiveTemperature.get(classification + temperature);
		meanEffectiveTemperature = ( null == effectiveTemp ? 1000.0 : effectiveTemp.doubleValue());
	}

	public SpectralClass(SpectralClass other)
	{
		this(other.classification, other.temperature, other.luminosityClass);
	}
		
	static {
		effectiveTemperature.put("O0", 60000.0);
		effectiveTemperature.put("O1", 55000.0);
		effectiveTemperature.put("O2", 52000.0);
		effectiveTemperature.put("O3", 48000.0);
		effectiveTemperature.put("O4", 45000.0);
		effectiveTemperature.put("O5", 42000.0);
		effectiveTemperature.put("O6", 39000.0);
		effectiveTemperature.put("O7", 36000.0);
		effectiveTemperature.put("O8", 33000.0);
		effectiveTemperature.put("O9", 30000.0);
		effectiveTemperature.put("B0", 27000.0);
		effectiveTemperature.put("B1", 24000.0);
		effectiveTemperature.put("B2", 20000.0);
		effectiveTemperature.put("B3", 17000.0);
		effectiveTemperature.put("B4", 14500.0);
		effectiveTemperature.put("B5", 12500.0);
		effectiveTemperature.put("B6", 11700.0);
		effectiveTemperature.put("B7", 11100.0);
		effectiveTemperature.put("B8", 10600.0);
		effectiveTemperature.put("B9", 10200.0);
		effectiveTemperature.put("A0", 9800.0);
		effectiveTemperature.put("A1", 9500.0);
		effectiveTemperature.put("A2", 9200.0);
		effectiveTemperature.put("A3", 8900.0);
		effectiveTemperature.put("A4", 8700.0);
		effectiveTemperature.put("A5", 8500.0);
		effectiveTemperature.put("A6", 8300.0);
		effectiveTemperature.put("A7", 8100.0);
		effectiveTemperature.put("A8", 7900.0);
		effectiveTemperature.put("A9", 7700.0);
		effectiveTemperature.put("F0", 7500.0);
		effectiveTemperature.put("F1", 7300.0);
		effectiveTemperature.put("F2", 7100.0);
		effectiveTemperature.put("F3", 6900.0);
		effectiveTemperature.put("F4", 6700.0);
		effectiveTemperature.put("F5", 6500.0);
		effectiveTemperature.put("F6", 6400.0);
		effectiveTemperature.put("F7", 6300.0);
		effectiveTemperature.put("F8", 6200.0);
		effectiveTemperature.put("F9", 6100.0);
		effectiveTemperature.put("G0", 6000.0);
		effectiveTemperature.put("G1", 5900.0);
		effectiveTemperature.put("G2", 5800.0);
		effectiveTemperature.put("G3", 5700.0);
		effectiveTemperature.put("G4", 5600.0);
		effectiveTemperature.put("G5", 5500.0);
		effectiveTemperature.put("G6", 5400.0);
		effectiveTemperature.put("G7", 5350.0);
		effectiveTemperature.put("G8", 5300.0);
		effectiveTemperature.put("G9", 5250.0);
		effectiveTemperature.put("K0", 5200.0);
		effectiveTemperature.put("K1", 5100.0);
		effectiveTemperature.put("K2", 5000.0);
		effectiveTemperature.put("K3", 4900.0);
		effectiveTemperature.put("K4", 4800.0);
		effectiveTemperature.put("K5", 4650.0);
		effectiveTemperature.put("K6", 4500.0);
		effectiveTemperature.put("K7", 4300.0);
		effectiveTemperature.put("K8", 4100.0);
		effectiveTemperature.put("K9", 3900.0);
		effectiveTemperature.put("M0", 3700.0);
		effectiveTemperature.put("M1", 3550.0);
		effectiveTemperature.put("M2", 3400.0);
		effectiveTemperature.put("M3", 3250.0);
		effectiveTemperature.put("M4", 3100.0);
		effectiveTemperature.put("M5", 2950.0);
		effectiveTemperature.put("M6", 2800.0);
		effectiveTemperature.put("M7", 2650.0);
		effectiveTemperature.put("M8", 2500.0);
		effectiveTemperature.put("M9", 2400.0);
		effectiveTemperature.put("L0", 2300.0);
		effectiveTemperature.put("L1", 2200.0);
		effectiveTemperature.put("L2", 2100.0);
		effectiveTemperature.put("L3", 2000.0);
		effectiveTemperature.put("L4", 1900.0);
		effectiveTemperature.put("L5", 1800.0);
		effectiveTemperature.put("L6", 1700.0);
		effectiveTemperature.put("L7", 1600.0);
		effectiveTemperature.put("L8", 1500.0);
		effectiveTemperature.put("L9", 1400.0);
		effectiveTemperature.put("T0", 1300.0);
		effectiveTemperature.put("T1", 1200.0);
		effectiveTemperature.put("T2", 1100.0);
		effectiveTemperature.put("T3", 1000.0);
		effectiveTemperature.put("T4", 900.0);
		effectiveTemperature.put("T5", 800.0);
		effectiveTemperature.put("T6", 700.0);
		effectiveTemperature.put("T7", 650.0);
		effectiveTemperature.put("T8", 600.0);
		effectiveTemperature.put("T9", 550.0);
		effectiveTemperature.put("Y0", 500.0);
		effectiveTemperature.put("Y1", 450.0);
		effectiveTemperature.put("Y2", 400.0);
		effectiveTemperature.put("Y3", 470.0);
		effectiveTemperature.put("Y4", 440.0);
		effectiveTemperature.put("Y5", 410.0);
		effectiveTemperature.put("Y6", 380.0);
		effectiveTemperature.put("Y7", 360.0);
		effectiveTemperature.put("Y8", 340.0);
		effectiveTemperature.put("Y9", 320.0);
	}
}
