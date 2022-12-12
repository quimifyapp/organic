package com.quimify.organic.components;

// This enum represents functional groups. TODO reverse order?

public enum Group { // Ordered by naming priority:
    acid,       // =O, -OH
    amide,      // =O, -NH2
    carbamoyl,  // -CONH2 amida no principal
    nitrile,    // ≡N
    cyanide,    // -CN nitrilo no principal
    aldehyde,   // =O, -H
    ketone,     // =O
    alcohol,    // -OH
    amine,      // -NH2
    ether,      // -O-R'
    alkene,     // = doble bond
    alkyne,     // ≡ triple bond
    nitro,      // -NO2
    bromine,    // -Br
    chlorine,   // -Cl
    fluorine,   // -F
    iodine,     // -I
    radical,    // -R
    hydrogen    // -H
}
