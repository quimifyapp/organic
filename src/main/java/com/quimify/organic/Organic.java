package com.quimify.organic;

public class Organic {

    private final String name;  // I.E. "prop-1-ene"
    private final String structure; // I.E. "CH3-CH=CH2"
    private final String smiles; // I.E. "C=CC" (Simplified Molecular Input Line Entry Specification)


    // Constructor:

    protected Organic( String name, String structure, String smiles) {
        this.name = name;
        this.structure = structure;
        this.smiles = smiles;
    }

    // Getters:

    public String getName() {
        return name;
    }

    public String getStructure() {
        return structure;
    }

    public String getSmiles() {
        return smiles;
    }

}
