package com.quimify.organic.opsin;

public class OpsinResult {

    // Scientific ways to describe a structure using text:
    private final String smiles; // Simplified Molecular Input Line Entry Specification
    private final String cml; // Chemical Markup Language

    // Constructor:

    protected OpsinResult(String smiles, String cml) {
        this.smiles = smiles;
        this.cml = cml;
    }

    // Getters:

    public String getSmiles() {
        return smiles;
    }
    public String getCml() {
        return cml;
    }

}
