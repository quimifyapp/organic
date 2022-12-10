package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.Map;

import static java.util.Map.entry;

public class Substituent extends Organic {

    private Group group;
    private int bondCount;
    private int carbonCount;
    private boolean isIso;

    /* Examples:
	-Cl             →   { Group.chlorine, bondCount: 1, carbonCount: 0, isIso: false }
	=O              →   { Group.ketone,   bondCount: 2, carbonCount: 0, isIso: false }
	-CH2-CH2-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, isIso: false }
	-CH(CH3)-CH3    →   { Group.radical,  bondCount: 1, carbonCount: 3, isIso: true  }
	*/

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
            entry(Group.acid, "COOH"),
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

        build(group, bondCount, 0, false);
    }

    public Substituent(int carbonCount, boolean isIso) {
        if(isIso && carbonCount == 0)
            throw new IllegalArgumentException("Radicals must have at least 1 carbon.");

        if(isIso && carbonCount == 1)
            throw new IllegalArgumentException("There is no \"isomethyl\".");

        if(isIso && carbonCount == 2)
            throw new IllegalArgumentException("There is no \"isoethyl\".");

        build(carbonCount, isIso);
    }

    public Substituent(int carbonCount) {
        if(carbonCount < 1)
            throw new IllegalArgumentException("Radicals must have at least 1 carbon.");

        build(carbonCount, false);
    }

    private void build(int carbonCount, boolean isIso) {
        build(Group.radical, 1, carbonCount, isIso);
    }

    private void build(Group group, int bondCount, int carbonCount, boolean isIso) {
        this.group = group;
        this.bondCount = bondCount;
        this.carbonCount = carbonCount;
        this.isIso = isIso;
    }

    // Queries:

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

    // Queries for radicals:

    public int getStraightCarbonCount() { // TODO private?
        return carbonCount - (isIso ? 1 : 0);
    }

    public boolean isGreaterThan(Substituent radical) {
        int comparaison = Integer.compare(getStraightCarbonCount(), radical.getStraightCarbonCount());

        // -CH(CH3)-CH3 > -CH2-CH3
        if(comparaison == 0)
            return isIso && !radical.isIso;

        // -CH2-CH3 > -CH3
        return comparaison > 0;
    }

    public Chain toChain() {
        if (carbonCount == 0)
            return null;

        Chain chain = new Chain(0);

        chain.bond(Group.hydrogen); // CH≡
        chain.bond(Group.hydrogen); // CH2=
        chain.bond(Group.hydrogen); // CH3-

        int previous = 1; // CH3-

        if (isIso) {
            chain.bondCarbon(); // CH3-C≡
            chain.bond(Group.hydrogen); // CH3-CH=
            chain.bond(new Substituent(1)); // CH3-CH(CH3)-

            previous += 2; // CH3-CH(CH3)-
        }

        for (int i = previous; i < carbonCount; i++) {
            chain.bondCarbon(); // CH3-CH(CH3)-C≡
            chain.bond(Group.hydrogen); // CH3-CH(CH3)-CH-
            chain.bond(Group.hydrogen); // CH3-CH(CH3)-CH2-
        }

        return chain; // CH3-CH(CH3)-CH2-
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
