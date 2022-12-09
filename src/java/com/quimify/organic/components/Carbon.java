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

    // Consultas:

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

    public int getAmountOf(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    public int getAmountOf(Group group) {
        if(!isBondedTo(group))
            return 0;

        if(isBond(group))
            return 1; // Maximum

        return (int) substituents.stream().filter(s -> s.getGroup() == group).count();
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Carbon nuevo = (Carbon) otro;

            if(freeBondCount == nuevo.freeBondCount && substituents.size() == nuevo.substituents.size()) {
                es_igual = true;

                for(int i = 0; i < substituents.size(); i++)
                    if(!substituents.get(i).equals(nuevo.substituents.get(i))) {
                        es_igual = false;
                        break;
                    }
            }
            else es_igual = false;
        }
        else es_igual = false;

        return es_igual;
    }

    // Métodos get:

    public List<Substituent> getSubstituentsOf(Group group) { // TODO remove
        return substituents.stream().filter(substituent ->
                        substituent.getGroup() == group).collect(Collectors.toList());
    }

    public List<Substituent> getSubstituentsWithoutHydrogen() { // TODO remove
        return substituents.stream().filter(substituent ->
                        substituent.getGroup() != Group.hydrogen).collect(Collectors.toList());
    }

    public List<Substituent> getUniqueSubstituents() { // TODO set?
        List<Substituent> uniqueSubstituents = new ArrayList<>();

        for(Substituent substituent : substituents)
            if(!uniqueSubstituents.contains(substituent))
                uniqueSubstituents.add(substituent);

        return uniqueSubstituents;
    }

    public Substituent getGreatestRadical() {
        Substituent greatestRadical;

        List<Substituent> radicals = getSubstituentsOf(Group.radical);
        greatestRadical = radicals.get(0); // Se asume que tiene radicales

        for(int i = 1; i < radicals.size(); i++)
            if(radicals.get(i).isLongerThan(greatestRadical))
                greatestRadical = radicals.get(i);

        return greatestRadical;
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("C");

        // Se recogen los tipos de sustituyente:
        List<Substituent> uniques = getUniqueSubstituents(); // Sin repetirse

        // Se ordenan según la prioridad de su función:
        Organic.ordenarPorFunciones(uniques);

        // Se escribe los hidrógenos:

        final int hydrogenCount = getAmountOf(Group.hydrogen);

        if(hydrogenCount > 0) {
            Substituent hydrogen = new Substituent(Group.hydrogen);
            result.append(hydrogen).append(getMolecularQuantifier(hydrogenCount));
            uniques.remove(uniques.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes excepto el éter:
        uniques.removeIf(substituent -> substituent.getGroup() == Group.ether);

        if(uniques.size() == 1) { // Solo hay un tipo además del hidrógeno y éter
            Substituent unique = uniques.get(0);
            String text = unique.toString();

            if (unique.getBondCount() == 3 && !(unique.getGroup() == Group.aldehyde && hydrogenCount > 0))
                result.append(text); // COOH, CHO...
            else if (unique.isHalogen())
                result.append(text); // CHCl, CF...
            else if(unique.getGroup() == Group.ketone && hydrogenCount == 0)
                result.append(text); // CO
            else result.append("(").append(text).append(")"); // CH(HO), CH(OH)3, CH3(CH2CH3)...

            result.append((getMolecularQuantifier(getAmountOf(unique))));
        }
        else if(uniques.size() > 1) { // Hay más de un tipo además del hidrógeno y éter
            for (Substituent substituent : uniques)
                result.append("(").append(substituent).append(")") // C(OH)3(Cl), CH2(NO2)(CH3)...
                        .append(getMolecularQuantifier(getAmountOf(substituent)));
        }

        // Se escribe el éter:
        if(isBondedTo(Group.ether))
            result.append(new Substituent(Group.ether));

        return result.toString();
    }

    // Modificadores:

    public void bond(Substituent substituent) {
        substituents.add(substituent);
        freeBondCount -= substituent.getBondCount();
    }

    public void bond(Group group) {
        bond(new Substituent(group));
    }

    public void remove(Substituent substituent) {
        substituents.remove(substituent); // No se ha eliminado su enlace
    }

    public void removeWithBonds(Substituent substituent) {
        remove(substituent);
        freeBondCount += substituent.getBondCount();
    }

    public void removeWithBonds(Group group) {
        removeWithBonds(new Substituent(group));
    }

    public void useBond() {
        freeBondCount--;
    }

    public void freeBond() {
        freeBondCount++;
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
