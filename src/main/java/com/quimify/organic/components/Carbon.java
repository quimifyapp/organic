package com.quimify.organic.components;

import com.quimify.organic.Nomenclature;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Carbon extends Nomenclature {

    private int freeBondCount;
    private final List<Substituent> substituents; // TODO set?

    // Error messages:

    private static final String cannotBondError = "Cannot bond %s to carbon: %s.";

    // Constants:

    protected static final Carbon CH3 = new Carbon(1);
    static {
        CH3.bond(Group.hydrogen);
        CH3.bond(Group.hydrogen);
        CH3.bond(Group.hydrogen);
    }

    protected static final Carbon CHCH3 = new Carbon(2);
    static {
        CHCH3.bond(Group.hydrogen);
        CHCH3.bond(new Substituent(1));
    }

    protected static final Carbon CH2 = new Carbon(2);
    static {
        CH2.bond(Group.hydrogen);
        CH2.bond(Group.hydrogen);
    }

    // Constructor:

    protected Carbon(int usedBondCount) {
        freeBondCount = 4 - usedBondCount;
        substituents = new ArrayList<>();
    }

    protected Carbon(Carbon other) {
        freeBondCount = other.freeBondCount;
        substituents = new ArrayList<>();
        other.substituents.forEach(s -> substituents.add(new Substituent(s)));
    }

    // Queries:

    public boolean isBondedTo(Group group) {
        if(group == Group.alkene)
            return freeBondCount == 1; // -CO=

        if(group == Group.alkyne)
            return freeBondCount == 2; // -CH≡

        return substituents.stream().anyMatch(s -> s.getGroup() == group);
    }

    protected int getAmountOf(Group group) {
        if(Nomenclature.isBond(group))
            return isBondedTo(group) ? 1 : 0;

        return (int) substituents.stream().filter(s -> s.getGroup() == group).count();
    }

    protected int getAmountOf(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    @Override
    public int hashCode() {
        Stream<Substituent> sortedSubstituents = substituents.stream().sorted(Substituent::compareTo);
        return Objects.hash(freeBondCount, sortedSubstituents.collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        Carbon otherCarbon = (Carbon) other;

        if (freeBondCount != otherCarbon.freeBondCount)
            return false;

        List<Substituent> sortedSubstituents = substituents.stream()
                .sorted(Substituent::compareTo).collect(Collectors.toList());

        List<Substituent> sortedOtherSubstituents = otherCarbon.substituents.stream()
                .sorted(Substituent::compareTo).collect(Collectors.toList());

        return sortedSubstituents.equals(sortedOtherSubstituents);
    }

    // Modifiers:

    public void bond(Group group) {
        bond(new Substituent(group));
    }

    protected void bond(Substituent substituent) {
        if(substituent.getBondCount() > freeBondCount)
            throw new IllegalStateException(String.format(cannotBondError, substituent, getStructure()));

        substituents.add(substituent);
        freeBondCount -= substituent.getBondCount();
    }

    public void unbond(Group group) {
        unbond(new Substituent(group));
    }

    protected void unbond(Substituent substituent) {
        freeBondCount += substituent.getBondCount();
        remove(substituent);
    }

    protected void remove(Substituent substituent) {
        substituents.remove(substituent);
    }

    protected void useBond() {
        freeBondCount--;
    }

    public void freeBond() {
        freeBondCount++;
    }

    // Text:

    private String getStructure() {
        // "CHCl2-O-" = "C" + "H" + "Cl2" + "-O-"
        return "C" + getHydrogenStructure() + getOtherSubstituentsStructure() + getEtherStructure();
    }

    private String getHydrogenStructure() {
        int hydrogenCount = getAmountOf(Group.hydrogen);

        if(hydrogenCount == 0)
            return "";

        // "H", "H2", "H3"
        return new Substituent(Group.hydrogen) + molecularQuantifierFor(hydrogenCount);
    }

    private StringBuilder getOtherSubstituentsStructure() {
        Stream<Substituent> uniqueSubstituents = substituents.stream()
                .filter(s -> s.getGroup() != Group.hydrogen && s.getGroup() != Group.ether).distinct();

        List<Substituent> uniqueOrderedSubstituents = uniqueSubstituents
                .sorted(Substituent::compareTo).collect(Collectors.toList());

        StringBuilder otherSubstituentsStructure;

        if (uniqueOrderedSubstituents.size() == 1)
            otherSubstituentsStructure = getSingleSubstituentStructure(uniqueOrderedSubstituents.get(0));
        else otherSubstituentsStructure = getMultipleSubstituentsStructure(uniqueOrderedSubstituents);

        return otherSubstituentsStructure;
    }

    private StringBuilder getSingleSubstituentStructure(Substituent substituent) {
        StringBuilder otherGroupsStructure = new StringBuilder();

        Group group = substituent.getGroup();
        int hydrogenCount = getAmountOf(Group.hydrogen);

        if (substituent.getBondCount() == 3 && group != Group.aldehyde)
            otherGroupsStructure.append(substituent); // CHOOH, CONH2-...
        else if (group == Group.aldehyde && hydrogenCount == 0)
            otherGroupsStructure.append(substituent); // CHO
        else if (group == Group.ketone && hydrogenCount == 0)
            otherGroupsStructure.append(substituent); // -CO-
        else if (Nomenclature.isHalogen(group))
            otherGroupsStructure.append(substituent); // CHCl2, CF3-...
        else otherGroupsStructure.append("(").append(substituent).append(")"); // CH(HO), CH(NO2)3, CH2(CH3)-...

        otherGroupsStructure.append((molecularQuantifierFor(getAmountOf(substituent))));

        return otherGroupsStructure;
    }

    private StringBuilder getMultipleSubstituentsStructure(List<Substituent> uniqueOrderedSubstituents) {
        StringBuilder otherGroupsStructure = new StringBuilder();

        // C(OH)3(Cl)
        for (Substituent substituent : uniqueOrderedSubstituents) {
            otherGroupsStructure.append("(").append(substituent).append(")"); // C(OH)
            otherGroupsStructure.append(molecularQuantifierFor(getAmountOf(substituent))); // C(OH)3
        }

        return otherGroupsStructure;
    }

    private String getEtherStructure() {
        if(!isBondedTo(Group.ether))
            return "";

        // -O-
        return new Substituent(Group.ether).toString();
    }

    @Override
    public String toString() {
        return getStructure();
    }

    // Getters and setters:

    protected List<Substituent> getSubstituents() {
        return substituents;
    }

    protected int getFreeBondCount() {
        return freeBondCount;
    }

    protected void setFreeBondCount(int freeBondCount) {
        this.freeBondCount = freeBondCount;
    }

}
