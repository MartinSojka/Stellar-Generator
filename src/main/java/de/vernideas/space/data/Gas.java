package de.vernideas.space.data;

import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Atmospheric gases
 */
@ToString
@Accessors(fluent = true)
@EqualsAndHashCode
public class Gas {
	public static final Gas ATOMIC_HYDROGEN = new Gas("Atomic Hydrogen", "H", 1.01);
	public static final Gas HYDROGEN = new Gas("Hydrogen", "H₂", 2.02);
	public static final Gas JUPITER_ATMOSPHERE = new Gas("Atmosphere (Jupiter)", "", 2.3);
	public static final Gas HELIUM = new Gas("Helium", "He", 4.00);
	public static final Gas ATOMIC_NITROGEN = new Gas("Atomic Nitrogen", "N", 14.01);
	public static final Gas ATOMIC_OXYGEN = new Gas("Atomic Oxygen", "O", 16.00);
	public static final Gas METHANE = new Gas("Methane", "CH₄", 16.04);
	public static final Gas AMMONIA = new Gas("Ammonia", "NH₃", 17.03);
	public static final Gas WATER_VAPOUR = new Gas("Water vapour", "H₂O", 18.02);
	public static final Gas ATOMIC_FLUORINE = new Gas("Atomic Fluorine", "F", 19.00);
	public static final Gas HYDROGEN_FLUORIDE = new Gas("Hydrogen Fluoride", "HF", 20.01);
	public static final Gas NEON = new Gas("Neon", "Ne", 20.18);
	public static final Gas SODIUM_VAPOUR = new Gas("Sodium vapour", "Na", 22.99);
	public static final Gas MAGNESIUM_VAPOUR = new Gas("Magnesium vapour", "Mg", 24.31);
	public static final Gas ACETYLENE = new Gas("Acetylene", "C₂H₂", 26.04);
	public static final Gas NITROGEN = new Gas("Nitrogen", "N₂", 28.01);
	public static final Gas CARBON_MONOXIDE = new Gas("Carbon monoxide", "CO", 28.01);
	public static final Gas EARTH_ATMOSPHERE = new Gas("Atmosphere (Earth)", "", 28.9645);
	public static final Gas NITRIC_OXIDE = new Gas("Nitric oxide", "NO", 30.01);
	public static final Gas ETHANE = new Gas("Ethane", "C₂H₆", 30.07);
	public static final Gas OXYGEN = new Gas("Oxygen", "O₂", 32.00);
	public static final Gas ATOMIC_SULPHUR = new Gas("Atomic Sulphur", "S", 32.07);
	public static final Gas PHOSPHINE = new Gas("Phosphine", "PH₃", 34.00);
	public static final Gas HYDROGEN_SULPHIDE = new Gas("Hydrogen sulphide", "H₂S", 34.09);
	public static final Gas ATOMIC_CHLORINE = new Gas("Atomic Chlorine", "Cl", 35.45);
	public static final Gas FLUORINE = new Gas("Fluorine", "F₂", 36.00);
	public static final Gas HYDROGEN_CHLORIDE = new Gas("Hydrogen chloride", "HCl", 36.46);
	public static final Gas POTASSIUM_VAPOUR = new Gas("Potassium vapour", "K", 39.10);
	public static final Gas ARGON = new Gas("Argon", "Ar", 39.95);
	public static final Gas CALCIUM_VAPOUR = new Gas("Calcium vapour", "Ca", 40.08);
	public static final Gas MARS_ATMOSPHERE = new Gas("Atmosphere (Mars)", "", 43.34);
	public static final Gas CARBON_DIOXIDE = new Gas("Carbon dioxide", "CO₂", 44.01);
	public static final Gas NITROUS_OXIDE = new Gas("Nitrous oxide", "N₂O", 44.01);
	public static final Gas PROPANE = new Gas("Propane", "C₃H₈", 44.10);
	public static final Gas NITROGEN_OXIDE = new Gas("Nitrogen oxide", "NO₂", 46.01);
	public static final Gas OZONE = new Gas("Ozone", "O₃", 48.00);
	public static final Gas SULPHUR_MONOXIDE = new Gas("Sulphur monoxide", "SO", 48.06);
	public static final Gas DIACETYLENE = new Gas("Diacetylene", "C₄H₂", 50.06);
	public static final Gas AMMONIUM_HYDROSULPHIDE = new Gas("Ammonium hydrosulphide", "NH₄SH", 51.11);
	public static final Gas SODIUM_CHLORIDE = new Gas("Sodium chloride", "NaCl", 58.44);
	public static final Gas ARGON_FLUOROHYDRIDE = new Gas("Argon fluorohydrid", "ArHF", 59.96);
	public static final Gas SULPHUR_DIOXIDE = new Gas("Sulphur dioxide", "SO₂", 64.06);
	public static final Gas FLUOROFORM = new Gas("Fluoroform", "CHF₃", 70.01);
	public static final Gas CHLORINE = new Gas("Chlorine", "Cl₂", 70.90);
	public static final Gas GERMANE = new Gas("Germane", "GeH₄", 76.67);
	public static final Gas ARSINE = new Gas("Arsine", "AsH₃", 77.95);
	public static final Gas ATOMIC_BROMINE = new Gas("Atomic Bromine", "Br", 79.90);
	public static final Gas SULPHUR_TRIOXIDE = new Gas("Sulphur trioxide", "SO₃", 80.06);
	public static final Gas KRYPTON = new Gas("Krypton", "Kr", 83.80);
	public static final Gas TETRAFLUOROMETHANE = new Gas("Tetrafluoromethane", "CF₄", 88.00);
	public static final Gas SULPHURIC_ACID = new Gas("Sulphuric acid", "H₂SO₄", 98.08);
	public static final Gas XENON = new Gas("Xenon", "Xe", 131.29);
	public static final Gas HEXAFLUOROETHANE = new Gas("Hexafluoroethane", "C₂F₆", 138.01);
	public static final Gas BROMINE = new Gas("Bromine", "Br₂", 159.81);
	
	public final String name;
	public final double molWeight;
	public final String formula;
	
	/** Temperature at which the compound decomposes, and what it decomposes to. */
	@Getter private double decompositionTemp;
	@Getter private List<Gas> decomposeTo;
	
	protected Gas(String name, double molWeight)
	{
		this.name = name;
		this.formula = name;
		this.molWeight = molWeight;
	}
	
	protected Gas(String name, String formula, double molWeight)
	{
		this.name = name;
		this.formula = formula;
		this.molWeight = molWeight;
	}

	protected Gas decomposition(double temperature, Gas ... result)
	{
		decompositionTemp = temperature;
		decomposeTo = Arrays.<Gas>asList(result);
		return this;
	}
	
	static {
		METHANE.decomposition(1400, HYDROGEN); /* Carbon falls out */ // TODO: Check
		AMMONIA.decomposition(1100, ATOMIC_NITROGEN, HYDROGEN); // TODO: Check
		WATER_VAPOUR.decomposition(3296, ATOMIC_OXYGEN, HYDROGEN); // Calculated?
		CARBON_MONOXIDE.decomposition(4140, ATOMIC_OXYGEN); /* Carbon falls out */ // TODO: Check
		HYDROGEN_CHLORIDE.decomposition(494, HYDROGEN, CHLORINE); // Calculated?
		ARGON_FLUOROHYDRIDE.decomposition(27, ARGON, HYDROGEN_FLUORIDE);
	}
}
