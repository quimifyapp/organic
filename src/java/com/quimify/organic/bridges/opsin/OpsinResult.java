package com.quimify.organic.bridges.opsin;

public class OpsinResult {

    private final String smiles; // Especie de fórmula más técnica
    private final String chemicalMarkupLanguage; // Chemical Markup Language

    // Constructores:

    public OpsinResult(es.opsin.OpsinResult opsinResult) {
        smiles = opsinResult.getSmiles();
        chemicalMarkupLanguage = opsinResult.getCml();
    }

    public OpsinResult(uk.ac.cam.ch.wwmm.opsin.OpsinResult opsinResult) {
        smiles = opsinResult.getSmiles();
        chemicalMarkupLanguage = opsinResult.getCml();
    }

    // Métodos get:

    public String getSmiles() {
        return smiles;
    }

    public String getChemicalMarkupLanguage() {
        return chemicalMarkupLanguage;
    }

}
