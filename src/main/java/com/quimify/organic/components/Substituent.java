package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class Substituent extends Organic {

    private final Group group;
    private final int bondCount;
    private final int carbonCount;
    private final boolean isIso;

    // Examples:

	// -Cl             →   { Group.chlorine, bondCount: 1, carbonCount: 0, isIso: false }
	// =O              →   { Group.ketone,   bondCount: 2, carbonCount: 0, isIso: false }
	// -CH2-CH2-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, isIso: false }
	// -CH(CH3)-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, isIso: true  }

    // Constants:

    private static final Map<Group, Integer> groupToBondCount = Map.ofEntries(
            entry(Group.acid, 3),
            entry(Group.amide, 3),
            entry(Group.carbamoyl, 1),
            entry(Group.nitrile, 3),
            entry(Group.cyanide, 1),
            entry(Group.aldehyde, 3),
            entry(Group.ketone, 2),
            entry(Group.alcohol, 1),
            entry(Group.amine, 1),
            entry(Group.ether, 1),
            entry(Group.nitro, 1),
            entry(Group.bromine, 1),
            entry(Group.chlorine, 1),
            entry(Group.fluorine, 1),
            entry(Group.iodine, 1),
            entry(Group.hydrogen, 1)
    );

    private static final Map<Group, String> groupToStructure = Map.ofEntries(
            entry(Group.acid, "OOH"),
            entry(Group.amide, "ONH2"),
            entry(Group.carbamoyl, "COHN2"),
            entry(Group.nitrile, "N"),
            entry(Group.cyanide, "CN"),
            entry(Group.aldehyde, "HO"),
            entry(Group.ketone, "O"),
            entry(Group.alcohol, "OH"),
            entry(Group.amine, "NH2"),
            entry(Group.ether, "-O-"),
            entry(Group.nitro, "NO2"),
            entry(Group.bromine, "Br"),
            entry(Group.chlorine, "Cl"),
            entry(Group.fluorine, "F"),
            entry(Group.iodine, "I"),
            entry(Group.hydrogen, "H")
    );

    // Constructors:

    public Substituent(Group group) {
        if(group == Group.radical)
            throw new IllegalArgumentException("There is no unique radical substituent.");

        Integer bondCount = groupToBondCount.get(group);

        if(bondCount == null)
            throw new IllegalArgumentException("There are no substituents with functional group: " + group + ".");

        this.group = group;
        this.bondCount = bondCount;
        this.carbonCount = 0;
        this.isIso = false;
    }

    public Substituent(int carbonCount, boolean isIso) {
        if(isIso && carbonCount == 0)
            throw new IllegalArgumentException("Radicals must have at least 1 carbon.");

        if(isIso && carbonCount == 1)
            throw new IllegalArgumentException("There is no \"isomethyl\".");

        if(isIso && carbonCount == 2)
            throw new IllegalArgumentException("There is no \"isoethyl\".");

        this.group = Group.radical;
        this.bondCount = 1;
        this.carbonCount = carbonCount;
        this.isIso = isIso;
    }

    public Substituent(int carbonCount) {
        if(carbonCount < 1)
            throw new IllegalArgumentException("Radicals must have at least 1 carbon.");

        this.group = Group.radical;
        this.bondCount = 1;
        this.carbonCount = carbonCount;
        this.isIso = false;
    }

    Substituent(Substituent other) {
        this.group = other.group;
        this.bondCount = other.bondCount;
        this.carbonCount = other.carbonCount;
        this.isIso = other.isIso;
    }

    // Queries:

    @Override
    public int hashCode() {
        return Objects.hash(group, bondCount, carbonCount, isIso);
    }

    @Override
    public boolean equals(Object other) {
        if(other == null || other.getClass() != this.getClass())
            return false;

        Substituent otherSubstituent = (Substituent) other;

        if(group != otherSubstituent.group)
            return false;

        if(bondCount != otherSubstituent.bondCount)
            return false;

        if(carbonCount != otherSubstituent.carbonCount)
            return false;

        return isIso == otherSubstituent.isIso;
    }

    public int compareTo(Substituent substituent) {
        // OOH < Cl < CH2CH3 < CH(CH3)2 < CH2CH2CH3 < H
        if(group != Group.radical || substituent.group != Group.radical)
            return group.compareTo(substituent.group);
        else return compareToRadical(substituent);
    }

    // Queries for radicals:

    private int compareToRadical(Substituent radical) {
        // CH2CH3 < CH(CH3)2 < CH2CH2CH3
        int comparaison = Integer.compare(getStraightCarbonCount(), radical.getStraightCarbonCount());

        if(comparaison == 0) {
            if (isIso == radical.isIso)
                return 0; // -CH(CH3)-CH3 = -CH2(CH3)-CH3

            return isIso ? 1 : -1; // -CH(CH3)-CH3 > -CH2-CH3
        }

        return comparaison; // -CH2-CH3 > -CH3
    }

    private int getStraightCarbonCount() {
        return carbonCount - (isIso ? 1 : 0);
    }

    // Text:

    private String getStructure() {
        if(group == Group.radical)
            return isIso
                ? "CH2".repeat(Math.max(0, carbonCount -  3)) + "CH(CH3)2"
                : "CH2".repeat(Math.max(0, carbonCount -  1)) + "CH3";

        String structure = groupToStructure.get(group);

        if(structure == null)
            throw new IllegalArgumentException("Unknown structure for functional group: " + group + ".");

        return structure;
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
        return isIso;
    }

}