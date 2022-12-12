package com.quimify.organic.bridges.pubchem;

public class PubChemResult {

	private String url2D;
	private String mass;
	private String englishName;

	// Getters y setters:

	public String getMass() {
		return mass;
	}

	public void setMass(String mass) {
		this.mass = mass;
	}

	public String getUrl2D() {
		return url2D;
	}

	public void setUrl2D(String url2D) {
		this.url2D = url2D;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

}
