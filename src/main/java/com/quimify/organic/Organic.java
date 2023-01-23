package com.quimify.organic;

public class Organic {

    private final String smiles; // I.E. "C=CC" (Simplified Molecular Input Line Entry Specification)
    private final String structure; // I.E. "CH3-CH=CH2"
    private final String name;  // I.E. "prop-1-ene"

    // Constructor:

    protected Organic(String smiles, String structure, String name) {
        this.smiles = smiles;
        this.structure = structure;
        this.name = name;
    }

    // Getters:

    public String getSmiles() {
        return smiles;
    }

    public String getStructure() {
        return structure;
    }

    public String getName() {
        return name;
    }

}
