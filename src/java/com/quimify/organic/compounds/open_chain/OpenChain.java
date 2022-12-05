package com.quimify.organic.compounds.open_chain;

// Esta interfaz implementa compuestos orgánicos no cíclicos:
//  - Simple: R
//  - Éter: R - O - R' (con funciones de prioridad menor a la función éter)
//  - Éster: TODO

import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.components.Substituent;

import java.util.List;

public interface OpenChain {
    int getFreeBonds();

    boolean isDone();

    void bondCarbon();

    void bond(Substituent substituent);

    void bond(FunctionalGroup functionalGroup);

    void correct();

    List<FunctionalGroup> getOrderedBondableGroups();

    String getName();

    String getStructure();
}
