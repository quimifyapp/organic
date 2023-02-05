package com.quimify.organic.molecules.open_chain;

// This interface implements non-cyclic organic compounds:
// - Simple: R
// - Ether: R - O - R' (with lower priority functional groups than the ether function)

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
