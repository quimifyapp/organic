package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.*;
import java.util.stream.Collectors;

public class Carbon extends Organic {

    private final List<Substituent> substituents;
    private int freeBondCount;

    // Constructor:

    public Carbon(int usedBondCount) {
        substituents = new ArrayList<>();
        freeBondCount = 4 - usedBondCount;
    }

    public Carbon(Carbon other) {
        substituents = new ArrayList<>(other.substituents);
        freeBondCount = other.freeBondCount;
    }

    // Queries:

    public boolean isBondedTo(Group group) {
        switch(group) {
            case alkene:
                return freeBondCount == 1; // -CO=
            case alkyne:
                return freeBondCount == 2; // -CH≡
            default:
                for(Substituent substituent : substituents)
                    if(substituent.getGroup() == group)
                        return true;

                return false;
        }
    }

    public int getAmountOf(Group group) {
        if(Organic.isBond(group))
            return isBondedTo(group) ? 1 : 0;

        return (int) substituents.stream().filter(s -> s.getGroup() == group).count();
    }

    public int getAmountOf(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        Carbon otherCarbon = (Carbon) other;

        if (freeBondCount != otherCarbon.freeBondCount)
            return false;

        if (substituents.size() != otherCarbon.substituents.size())
            return false;

        for (Substituent substituent : substituents)
            if (Collections.frequency(substituents, substituent) !=
                    Collections.frequency(otherCarbon.substituents, substituent))
                return false;

        return true;
    }

    // TODO remove:

    public List<Substituent> getSubstituentsOf(Group group) { // TODO remove
        return substituents.stream().filter(substituent ->
                substituent.getGroup() == group).collect(Collectors.toList());
    }

    public Substituent getGreatestRadical() { // TODO remove
        Substituent greatestRadical;

        List<Substituent> radicals = getSubstituentsOf(Group.radical);
        greatestRadical = radicals.get(0); // Se asume que tiene radicales

        for(int i = 1; i < radicals.size(); i++)
            if(radicals.get(i).isGreaterThan(greatestRadical))
                greatestRadical = radicals.get(i);

        return greatestRadical;
    }

    // Modifiers:

    public void bond(Group group) {
        bond(new Substituent(group));
    }

    public void bond(Substituent substituent) {
        substituents.add(substituent);
        freeBondCount -= substituent.getBondCount();
    }

    public void unbond(Group group) {
        unbond(new Substituent(group));
    }

    public void unbond(Substituent substituent) {
        freeBondCount += substituent.getBondCount();
        remove(substituent);
    }

    public void remove(Substituent substituent) {
        substituents.remove(substituent);
    }

    public void useBond() {
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
                .sorted(Comparator.comparing(Substituent::getGroup)).collect(Collectors.toList());

        if(uniqueOrderedSubstituents.size() == 1) { // Only one kind except for hydrogen and ether
            Substituent substituent = uniqueOrderedSubstituents.get(0);

            boolean isAldehyde = substituent.getGroup() == Group.aldehyde;

            if(substituent.getBondCount() == 3 && !isAldehyde)
                result.append(substituent); // CHOOH, CONH2...
            else if(isAldehyde && hydrogenCount == 0)
                result.append(substituent); // CHO
            else if (Organic.isHalogen(substituent.getGroup()))
                result.append(substituent); // CHCl2, CF3...
            else result.append("(").append(substituent).append(")"); // CH(HO), CH(NO2)3, CH2(CH3)...

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

    public List<Substituent> getSubstituents() {
        return substituents;
    }

    public int getFreeBondCount() {
        return freeBondCount;
    }

    public void setFreeBondCount(int freeBondCount) {
        this.freeBondCount = freeBondCount;
    }

}
