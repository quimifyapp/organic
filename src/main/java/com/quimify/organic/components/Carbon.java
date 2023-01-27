package com.quimify.organic.components;

import com.quimify.organic.Nomenclature;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Carbon extends Nomenclature {

    private int freeBondCount;
    private final List<Substituent> substituents; // TODO set?

    // Constructor:

    protected Carbon(int usedBondCount) {
        freeBondCount = 4 - usedBondCount;
        substituents = new ArrayList<>();
    }

    Carbon(Carbon other) {
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
        StringBuilder result = new StringBuilder("C");

        // Hydrogen:

        final int hydrogenCount = getAmountOf(Group.hydrogen);

        if(hydrogenCount > 0)
            result.append(new Substituent(Group.hydrogen)).append(molecularQuantifierFor(hydrogenCount));

        // Rest of them except ether:

        Set<Substituent> uniqueSubstituents = new HashSet<>(substituents);
        uniqueSubstituents.removeIf(s -> s.getGroup() == Group.hydrogen || s.getGroup() == Group.ether);

        List<Substituent> uniqueOrderedSubstituents = uniqueSubstituents.stream()
                .sorted(Substituent::compareTo).collect(Collectors.toList());

        if(uniqueOrderedSubstituents.size() == 1) { // Only one kind except for hydrogen and ether
            Substituent substituent = uniqueOrderedSubstituents.get(0);
            Group group = substituent.getGroup();

            if(substituent.getBondCount() == 3 && group != Group.aldehyde)
                result.append(substituent); // CHOOH, CONH2-...
            else if(group == Group.aldehyde && hydrogenCount == 0)
                result.append(substituent); // CHO
            else if(group == Group.ketone && hydrogenCount == 0)
                result.append(substituent); // -CO-
            else if (Nomenclature.isHalogen(group))
                result.append(substituent); // CHCl2, CF3-...
            else result.append("(").append(substituent).append(")"); // CH(HO), CH(NO2)3, CH2(CH3)-...

            result.append((molecularQuantifierFor(getAmountOf(substituent))));
        }
        else if(uniqueOrderedSubstituents.size() > 1) // More than one kind except for hydrogen and ether
            for (Substituent substituent : uniqueOrderedSubstituents)
                result.append("(").append(substituent).append(")") // C(OH)3(Cl), CH2(NO2)(CH3)...
                        .append(molecularQuantifierFor(getAmountOf(substituent)));

        // Ether:

        if(isBondedTo(Group.ether))
            result.append(new Substituent(Group.ether)); // CHBr-O-

        return result.toString();
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
