package com.quimify.organic.components;

// This enum represents functional groups.

public enum Group { // Ordered by naming priority:
    acid,       // =O, -OH  (0)
    amide,      // =O, -NH2 (1)
    carbamoyl,  // -CONH2 (like a non-priority amide) (2)
    nitrile,    // ≡N (3)
    cyanide,    // -CN (like a non-priority nitrile) (4)
    aldehyde,   // =O, -H (5)
    ketone,     // =O (6)
    alcohol,    // -OH (7)
    amine,      // -NH2 (8)
    ether,      // -O-R' (9)
    alkene,     // = (doble bond) (10)
    alkyne,     // ≡ (triple bond) (11)
    nitro,      // -NO2 (12)
    bromine,    // -Br (13)
    chlorine,   // -Cl (14)
    fluorine,   // -F (15)
    iodine,     // -I (16)
    radical,    // -R (17)
    hydrogen    // -H (18)
}
