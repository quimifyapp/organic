package com.quimify.organic.compounds.open_chain;

// Esta interfaz implementa compuestos orgánicos no cíclicos:
//  - Simple: R
//  - Éter: R - O - R' (con funciones de prioridad menor a la función éter)
//  - Éster: TODO

import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.List;

public interface OpenChain {
    int getFreeBonds();

    boolean isDone();

    void bondCarbon();

    void bond(Substituent substituent);

    void bond(Group group);

    void correct();

    List<Group> getBondableGroups();

    String getName();

    String getStructure();
}
