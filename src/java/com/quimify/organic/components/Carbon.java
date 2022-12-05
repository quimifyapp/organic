package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.*;
import java.util.stream.Collectors;

public class Carbon extends Organic {

    private final List<Substituent> substituents;
    private int freeBondCount;

    // Constructor:

    public Carbon(int enlaces_previos) {
        substituents = new ArrayList<>();
        freeBondCount = 4 - enlaces_previos;
    }

    public Carbon(Carbon otro) {
        substituents = new ArrayList<>(otro.substituents);
        freeBondCount = otro.freeBondCount;
    }

    // Consultas:

    public boolean isBondedTo(FunctionalGroup functionalGroup) {
        switch(functionalGroup) {
            case alkene:
                return freeBondCount == 1; // Como en -CO=
            case alkyne:
                return freeBondCount == 2; // Como en -CH#
            default:
                for(Substituent substituent : substituents)
                    if(substituent.getFunctionalGroup() == functionalGroup)
                        return true;

                return false;
        }
    }

    public boolean isBondedTo(Substituent substituent) {
        for(Substituent otro_substituent : substituents)
            if(otro_substituent.equals(substituent))
                return true;

        return false;
    }

    public int getCantidadDe(Substituent substituent) {
        return Collections.frequency(substituents, substituent);
    }

    public int getCantidadDe(FunctionalGroup functionalGroup) {
        int cantidad = 0;

        if(isBondedTo(functionalGroup)) {
            if(functionalGroup != FunctionalGroup.alkene && functionalGroup != FunctionalGroup.alkyne) {
                for(Substituent substituent : substituents)
                    if(substituent.getFunctionalGroup() == functionalGroup)
                        cantidad += 1;
            }
            else cantidad = 1;
        }

        return cantidad;
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

    public List<Substituent> getSubstituentsOf(FunctionalGroup functionalGroup) {
        return substituents.stream().filter(substituent ->
                        substituent.getFunctionalGroup() == functionalGroup).collect(Collectors.toList());
    }

    public List<Substituent> getSubstituentsWithoutHydrogen() {
        return substituents.stream().filter(substituent ->
                        substituent.getFunctionalGroup() != FunctionalGroup.hydrogen).collect(Collectors.toList());
    }

    public List<Substituent> getUniqueSubstituents() {
        List<Substituent> unicos = new ArrayList<>();

        for(Substituent substituent : substituents)
            if(!unicos.contains(substituent))
                unicos.add(substituent);

        return unicos;
    }

    public Substituent getGreatesRadical() {
        Substituent greatestRadical;

        List<Substituent> radicales = getSubstituentsOf(FunctionalGroup.radical);
        greatestRadical = radicales.get(0); // Se asume que tiene radicales

        for(int i = 1; i < radicales.size(); i++)
            if(radicales.get(i).isLongerThan(greatestRadical))
                greatestRadical = radicales.get(i);

        return greatestRadical;
    }

    public int getEnlacesLibres() {
        return freeBondCount;
    }

    // Métodos set:

    public void setEnlacesLibres(int enlaces_libres) {
        this.freeBondCount = enlaces_libres;
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

        final int hydrogenCount = getCantidadDe(FunctionalGroup.hydrogen);

        if(hydrogenCount > 0) {
            Substituent hydrogen = new Substituent(FunctionalGroup.hydrogen);
            result.append(hydrogen).append(getMolecularQuantifier(hydrogenCount));
            uniques.remove(uniques.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes excepto el éter:
        uniques.removeIf(substituent -> substituent.getFunctionalGroup() == FunctionalGroup.ether);

        if(uniques.size() == 1) { // Solo hay un tipo además del hidrógeno y éter
            Substituent unique = uniques.get(0);
            String text = unique.toString();

            if (unique.getBondCount() == 3 && !(unique.getFunctionalGroup() == FunctionalGroup.aldehyde && hydrogenCount > 0))
                result.append(text); // COOH, CHO...
            else if (unique.isHalogen() || (unique.getFunctionalGroup() == FunctionalGroup.ketone && hydrogenCount == 0))
                result.append(text); // CO, CCl...
            else result.append("(").append(text).append(")"); // CH(HO), CH(OH)3, CH3(CH2CH3)...

            result.append((getMolecularQuantifier(getCantidadDe(unique))));
        }
        else if(uniques.size() > 1) { // Hay más de un tipo además del hidrógeno y éter
            for (Substituent substituent : uniques)
                result.append("(").append(substituent).append(")") // C(OH)3(Cl), CH2(NO2)(CH3)...
                        .append(getMolecularQuantifier(getCantidadDe(substituent)));
        }

        // Se escribe el éter:
        if(isBondedTo(FunctionalGroup.ether))
            result.append(new Substituent(FunctionalGroup.ether));

        return result.toString();
    }

    // Modificadores:

    public void bond(Substituent substituent) {
        substituents.add(substituent);
        freeBondCount -= substituent.getBondCount();
    }

    public void bond(FunctionalGroup functionalGroup) {
        bond(new Substituent(functionalGroup));
    }

    public void bond(Substituent substituent, int times) {
        for(int i = 0; i < times; i++)
            bond(substituent);
    }

    public void bond(FunctionalGroup functionalGroup, int times) {
        for(int i = 0; i < times; i++)
            bond(functionalGroup);
    }

    public void remove(Substituent substituent) {
        substituents.remove(substituent); // No se ha eliminado su enlace
    }

    public void removeWithBonds(Substituent substituent) {
        remove(substituent);
        freeBondCount += substituent.getBondCount();
    }

    public void removeWithBonds(FunctionalGroup functionalGroup) {
        removeWithBonds(new Substituent(functionalGroup));
    }

    public void useBond() {
        freeBondCount--;
    }

    public void freeBond() {
        freeBondCount++;
    }

}
