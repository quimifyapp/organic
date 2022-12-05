package com.quimify.organic.components;

import com.quimify.organic.Organic;

public class Substituent extends Organic {

    private FunctionalGroup functionalGroup; // El tipo de sustituyente
    private int bondCount; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonCount;
    private boolean isIso;

	// EJEMPLOS:
    /*
	cetona:     =O              →  { Id:cetona,    enlaces: 2,  carbonos: 0,  iso: false }

	propil:     -CH2-CH2-CH3    →  { Id::radical,  enlaces: 1,  carbonos: 3,  iso: false }

                           CH3
                          /
	isopentil:  -CH2-CH2-CH     →  {Id::radical,  enlaces: 1,  carbonos: 5,  iso: true  }
                          \
                           CH3
	*/

    public static final Substituent CH3 = new Substituent(1);

    // Constructores:

    public Substituent(int carbonCount, boolean isIso) {
        if(isIso) {
            switch(carbonCount) {
                case 0:
                    throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
                case 1:
                    throw new IllegalArgumentException("No existe el \"isometil\".");
                case 2:
                    throw new IllegalArgumentException("No existe el \"isoetil\".");
                default:
                    build(carbonCount, true);
                    break;
            }
        }
        else build(carbonCount);
    }

    public Substituent(int carbonCount) {
        build(carbonCount);
    }

    public Substituent(FunctionalGroup functionalGroup) {
        switch(functionalGroup) {
            case acid:
            case amide:
            case nitrile:
            case aldehyde:
                build(functionalGroup, 3);
                // Hasta aquí
                break;
            case ketone:
                build(functionalGroup, 2);
                break;
            case carboxyl:
            case carbamoyl:
            case cyanide:
            case alcohol:
            case amine:
            case ether:
            case nitro:
            case bromine:
            case chlorine:
            case fluorine:
            case iodine:
            case hydrogen:
                build(functionalGroup, 1);
                // Hasta aquí
                break;
            case radical:
                throw new IllegalArgumentException("No existe un único sustituyente con función de radical.");
            default: // Id.alqueno, Id.alquino
                throw new IllegalArgumentException("No existen sustituyentes con función de [" + functionalGroup + "].");
        }
    }

    private void build(FunctionalGroup functionalGroup, int bonds, int carbonos, boolean iso) {
        this.functionalGroup = functionalGroup;
        this.bondCount = bonds;
        this.carbonCount = carbonos;
        this.isIso = iso;
    }

    private void build(FunctionalGroup functionalGroup, int bonds) {
        build(functionalGroup, bonds, 0, false);
    }

    private void build(int carbons, boolean isIso) {
        build(FunctionalGroup.radical, 1, carbons, isIso);
    }

    private void build(int carbonos) {
        if(carbonos > 0)
            build(carbonos, false);
        else throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
    }

    // Queries:

    public boolean isHalogen() {
        return Organic.isHalogen(functionalGroup);
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Substituent nuevo = (Substituent) otro;

            es_igual = functionalGroup == FunctionalGroup.radical
                    ? carbonCount == nuevo.carbonCount && isIso == nuevo.isIso
                    : functionalGroup == nuevo.functionalGroup && bondCount == nuevo.bondCount;
        }
        else es_igual = false;

        return es_igual;
    }

    // Para radicales:

    public int getStraightCarbonCount() {
        return carbonCount - (isIso ? 1 : 0);
    }

    public boolean isLongerThan(Substituent radical) {
        switch(Integer.compare(getStraightCarbonCount(), radical.getStraightCarbonCount())) {
            case 1: // Lo supera
                return true;
            case 0: // Lo iguala
                return isIso && !radical.isIso; // Pero es 'iso'
            default:
                return false;
        }
    }

    public Chain toChain() {
        if (carbonCount == 0)
            return new Chain(); // Empty

        Chain chain = new Chain(0); // (C)

        chain.bond(FunctionalGroup.hydrogen, 3); // CH3-

        int previous = 1; // CH3-

        if (isIso) {
            chain.bondCarbon(); // CH3-C≡
            chain.bond(FunctionalGroup.hydrogen); // CH3-CH=
            chain.bond(CH3); // CH3-CH(CH3)-

            previous += 2; // CH3-CH(CH3)-
        }

        for (int i = previous; i < carbonCount; i++) {
            chain.bondCarbon(); // CH3-CH(CH3)-C≡
            chain.bond(FunctionalGroup.hydrogen, 2); // CH3-CH(CH3)-CH2-
        }

        return chain; // CH3-CH(CH3)-CH2-
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();

        switch(functionalGroup) {
            case carboxyl:
                resultado.append("C");
            case acid:
                resultado.append("OOH");
                // Hasta aquí
                break;
            case carbamoyl:
                resultado.append("C");
            case amide:
                resultado.append("ONH2");
                // Hasta aquí
                break;
            case cyanide:
                resultado.append("C");
            case nitrile:
                resultado.append("N");
                // Hasta aquí
                break;
            case aldehyde:
                resultado.append("HO");
                break;
            case ketone:
                resultado.append("O");
                break;
            case alcohol:
                resultado.append("OH");
                break;
            case amine:
                resultado.append("NH2");
                break;
            case ether:
                resultado.append("-O-");
                break;
            case nitro:
                resultado.append("NO2");
                break;
            case bromine:
                resultado.append("Br");
                break;
            case chlorine:
                resultado.append("Cl");
                break;
            case fluorine:
                resultado.append("F");
                break;
            case iodine:
                resultado.append("I");
                break;
            case radical:
                if(isIso)
                    resultado.append("CH2".repeat(Math.max(0, carbonCount -  3))).append("CH(CH3)2");
                else resultado.append("CH2".repeat(Math.max(0, carbonCount -  1))).append("CH3");
                break;
            case hydrogen:
                resultado.append("H");
                break;
        }

        return resultado.toString();
    }

    // Getters:

    public FunctionalGroup getFunctionalGroup() {
        return functionalGroup;
    }


    public int getBondCount() {
        return bondCount;
    }


    public int getCarbonCount() {
        return carbonCount;
    }


    public boolean isIso() {
        return isIso;
    }

}
