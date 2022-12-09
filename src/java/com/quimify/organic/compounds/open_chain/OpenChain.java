package com.quimify.organic.compounds.open_chain;

// Esta interfaz implementa compuestos orgánicos no cíclicos:
//  - Simple: R
//  - Éter: R - O - R' (con funciones de prioridad menor a la función éter)

import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.List;

public interface OpenChain {

    boolean isDone();

    List<Group> getBondableGroups();

    OpenChain bond(Substituent substituent);

    OpenChain bond(Group group);

    boolean canBondCarbon();

    void bondCarbon();

    void correct();

    String getName();

    String getStructure();

}
