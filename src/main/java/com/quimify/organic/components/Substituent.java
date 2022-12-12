package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.Objects;

public class Substituent extends Organic {

    private final Group group;
    private final int bondCount;
    private final int carbonCount;
    private final boolean iso;

    // Examples:

	// -Cl             →   { Group.chlorine, bondCount: 1, carbonCount: 0, iso: false }
	// =O              →   { Group.ketone,   bondCount: 2, carbonCount: 0, iso: false }
	// -CH2-CH2-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, iso: false }
	// -CH(CH3)-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, iso: true  }

    // Constants:

    private static final String noUniqueError = "There is no unique substituent with functional group: %s.";
    private static final String noSuchError = "There is no substituent with functional group: %s.";
    private static final String radicalTooShortError = "Radicals must have at least 1 carbon.";
    private static final String isoRadicalTooShortError = "Isotopic radicals must have at least 3 carbons.";
    private static final String unknownStructureError = "Unknown structure for substituent with functional group: %s.";

    // Constructors:

    public Substituent(Group group) {
        if(group == Group.radical)
            throw new IllegalArgumentException(String.format(noUniqueError, Group.radical));

        switch (group) {
            case acid:
            case amide:
            case nitrile:
            case aldehyde:
                this.bondCount = 3;
                break;
            case ketone:
                this.bondCount = 2;
                break;
            case carbamoyl:
            case cyanide:
            case alcohol:
            case amine:
            case ether:
            case nitro:
            case bromine:
            case chlorine:
            case fluorine:
            case iodine:
            case hydrogen:
                this.bondCount = 1;
                break;
            default:
                throw new IllegalArgumentException(String.format(noSuchError, group));
        }

        this.group = group;
        this.carbonCount = 0;
        this.iso = false;
    }

    public Substituent(int carbonCount, boolean iso) {
        if(!iso && carbonCount < 1)
            throw new IllegalArgumentException(radicalTooShortError);

        if(iso && carbonCount < 3)
            throw new IllegalArgumentException(isoRadicalTooShortError);

        this.group = Group.radical;
        this.bondCount = 1;
        this.carbonCount = carbonCount;
        this.iso = iso;
    }

    public Substituent(int carbonCount) {
        if(carbonCount < 1)
            throw new IllegalArgumentException("Radicals must have at least 1 carbon.");

        this.group = Group.radical;
        this.bondCount = 1;
        this.carbonCount = carbonCount;
        this.iso = false;
    }

    Substituent(Substituent other) {
        this.group = other.group;
        this.bondCount = other.bondCount;
        this.carbonCount = other.carbonCount;
        this.iso = other.iso;
    }

    // Queries:

    public int compareTo(Substituent other) {
        // OOH < Cl < CH(CH3)2 < CH2CH3 < CH2CH2CH3 < H
        if(group == Group.radical && other.group == Group.radical)
            return compareToRadical(other); // CH(CH3)2 < CH2CH3 < CH2CH2CH3

        return group.compareTo(other.group); // OOH < Cl < H
    }

    private int compareToRadical(Substituent radical) {
        // CH(CH3)2 < CH2CH3 < CH2CH2CH3
        int comparaison = Integer.compare(carbonCount, radical.carbonCount);

        if(comparaison == 0)
            return iso == radical.iso ? 0 : iso ? -1 : 1; // CH(CH3)2 < CH2CH3

        return comparaison; // CH2CH3 < CH2CH2CH3
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, bondCount, carbonCount, iso);
    }

    @Override
    public boolean equals(Object other) {
        if(other == null || other.getClass() != this.getClass())
            return false;

        Substituent otherSubstituent = (Substituent) other;

        if(group != otherSubstituent.group)
            return false;

        if(carbonCount != otherSubstituent.carbonCount)
            return false;

        return iso == otherSubstituent.iso;
    }

    // Text:

    private String getStructure() {
        switch (group) {
            case acid:
                return "OOH";
            case amide:
                return "ONH2";
            case carbamoyl:
                return "COHN2";
            case nitrile:
                return "N";
            case cyanide:
                return "CN";
            case aldehyde:
                return "HO";
            case ketone:
                return "O";
            case alcohol:
                return "OH";
            case amine:
                return "NH2";
            case ether:
                return "-O-";
            case nitro:
                return "NO2";
            case bromine:
                return "Br";
            case chlorine:
                return "Cl";
            case fluorine:
                return "F";
            case iodine:
                return "I";
            case hydrogen:
                return "H";
            case radical:
                return getRadicalStructure(carbonCount, iso);
            default:
                throw new IllegalArgumentException(unknownStructureError);
        }
    }

    private String getRadicalStructure(int carbonCount, boolean isIso) {
        return isIso
                ? "CH2".repeat(Math.max(0, carbonCount -  3)) + "CH(CH3)2"
                : "CH2".repeat(Math.max(0, carbonCount -  1)) + "CH3";
    }

    @Override
    public String toString() {
        return getStructure();
    }

    // Getters:

    public Group getGroup() {
        return group;
    }

    public int getBondCount() {
        return bondCount;
    }

    public int getCarbonCount() {
        return carbonCount;
    }

    public boolean isIso() {
        return iso;
    }

}
