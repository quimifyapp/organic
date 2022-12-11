package com.quimify.organic;

public class OrganicResult {

    private Boolean present;
    private String name;
    private String structure;
    private Float molecularMass;
    private String url2D;

    // --------------------------------------------------------------------------------

    // Constructor:

    OrganicResult(boolean present) {
        this.present = present;
    }

    // Getters y setters:

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMolecularMass() {
        return molecularMass;
    }

    public void setMolecularMass(Float molecularMass) {
        this.molecularMass = molecularMass;
    }

    public String getUrl2D() {
        return url2D;
    }

    public void setUrl2D(String url2D) {
        this.url2D = url2D;
    }

}
