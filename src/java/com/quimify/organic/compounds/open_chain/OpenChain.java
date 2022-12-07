package com.quimify.organic.compounds.open_chain;

// Esta interfaz implementa compuestos orgánicos no cíclicos:
//  - Simple: R
//  - Éter: R - O - R' (con funciones de prioridad menor a la función éter)

import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.List;

public interface OpenChain {

    List<Group> getBondableGroups();

    OpenChain bond(Group group);

    OpenChain bond(Substituent substituent);

    boolean canBondCarbon();

    void bondCarbon();

    void correct();

    String getName();

    String getStructure();

}
