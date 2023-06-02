package com.quimify.organic.components;

import com.quimify.organic.Nomenclature;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Carbon extends Nomenclature {

    private int freeBondCount;
    private final List<Substituent> substituents;

    // Error messages:

    private static final String cantBondError = "Can't bond %s to carbon: %s.";

    // Constants:

    static final Carbon CH3 = new Carbon(Collections.nCopies(3, new Substituent(Group.hydrogen)));
    static final Carbon CH2 = new Carbon(Collections.nCopies(2, new Substituent(Group.hydrogen)));
    static final Carbon CHCH3 = new Carbon(List.of(new Substituent(Group.hydrogen), Substituent.radical(1)));

    // Constructors:

    private Carbon(int freeBondCount, List<Substituent> substituents) {
        this.freeBondCount = freeBondCount;
        this.substituents = substituents;
    }

    private Carbon(List<Substituent> substituents) {
        this(0, substituents);
    }

    Carbon(int usedBondCount) {
        this(4 - usedBondCount, new ArrayList<>());
    }

    Carbon(Carbon other) {
        this(other.freeBondCount, other.substituents.stream().map(Substituent::new).collect(Collectors.toList()));
    }

    // Queries:

    public boolean isBondedTo(Group group) {
        if (group == Group.alkene)
            return freeBondCount == 1; // -CO=

        if (group == Group.alkyne)
            return freeBondCount == 2; // -CH≡

        return substituents.stream().anyMatch(s -> s.getGroup() == group);
    }

    int getAmountOf(Group group) {
        if (isBond(group))
            return isBondedTo(group) ? 1 : 0;

        return (int) substituents.stream().filter(s -> s.getGroup() == group).count();
    }

    int getAmountOf(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    @Override
    public int hashCode() {
        Stream<Substituent> sortedSubstituents = substituents.stream().sorted(Substituent::compareTo);
        return Objects.hash(freeBondCount, sortedSubstituents.collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
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

    void bond(Substituent substituent) {
        if (substituent.getBondCount() > freeBondCount) // TODO test NameToStructure
            throw new IllegalStateException(String.format(cantBondError, substituent, getStructure()));

        substituents.add(substituent);
        freeBondCount -= substituent.getBondCount();
    }

    public void unbond(Group group) {
        unbond(new Substituent(group));
    }

    void unbond(Substituent substituent) {
        freeBondCount += substituent.getBondCount();
        remove(substituent);
    }

    void remove(Substituent substituent) {
        substituents.remove(substituent);
    }

    void useBond() {
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

        if (hydrogenCount == 0)
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
        else if (isHalogen(group))
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
        if (!isBondedTo(Group.ether))
            return "";

        // -O-
        return new Substituent(Group.ether).toString();
    }

    @Override
    public String toString() {
        return getStructure();
    }

    // Getters and setters:

    List<Substituent> getSubstituents() {
        return substituents;
    }

    int getFreeBondCount() {
        return freeBondCount;
    }

    void setFreeBondCount(int freeBondCount) {
        this.freeBondCount = freeBondCount;
    }

}
