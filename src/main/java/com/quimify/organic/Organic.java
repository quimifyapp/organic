package com.quimify.organic;

public class Organic {

    private final String name; // I.E. "prop-1-ene"
    private final String smiles; // I.E. "C=CC" (Simplified Molecular Input Line Entry Specification)
    private final String structure; // I.E. "CH3-CH=CH2"

    // If structure = null:

    private final Exception structureException;  // I.E. "java.lang.IllegalArgumentException: Unknown organic atom: Na."

    // Constructors:

    protected Organic(String name, String smiles, String structure) {
        this.name = name;
        this.smiles = smiles;
        this.structure = structure;
        this.structureException = null;
    }

    protected Organic(String name, String smiles, Exception structureException) {
        this.name = name;
        this.smiles = smiles;
        this.structure = null;
        this.structureException = structureException;
    }

    // Getters:

    public String getName() {
        return name;
    }

    public String getSmiles() {
        return smiles;
    }

    public String getStructure() {
        return structure;
    }

    public Exception getStructureException() {
        return structureException;
    }

}
