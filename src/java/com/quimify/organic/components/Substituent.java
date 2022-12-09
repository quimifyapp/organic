package com.quimify.organic.components;

import com.quimify.organic.Organic;

public class Substituent extends Organic {

    private Group group; // El tipo de sustituyente
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

    public Substituent(Group group) {
        switch(group) {
            case acid:
            case amide:
            case nitrile:
            case aldehyde:
                build(group, 3);
                // Hasta aquí
                break;
            case ketone:
                build(group, 2);
                break;
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
                build(group, 1);
                // Hasta aquí
                break;
            case radical:
                throw new IllegalArgumentException("No existe un único sustituyente con función de radical.");
            default: // Id.alqueno, Id.alquino
                throw new IllegalArgumentException("No existen sustituyentes con función de [" + group + "].");
        }
    }

    private void build(Group group, int bonds, int carbonos, boolean iso) {
        this.group = group;
        this.bondCount = bonds;
        this.carbonCount = carbonos;
        this.isIso = iso;
    }

    private void build(Group group, int bonds) {
        build(group, bonds, 0, false);
    }

    private void build(int carbons, boolean isIso) {
        build(Group.radical, 1, carbons, isIso);
    }

    private void build(int carbonos) {
        if(carbonos > 0)
            build(carbonos, false);
        else throw new IllegalArgumentException("No existen radicales con 0 carbonos.");
    }

    // Queries:

    public boolean isHalogen() {
        return Organic.isHalogen(group);
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual;

        if(otro != null && otro.getClass() == this.getClass()) {
            Substituent nuevo = (Substituent) otro;

            es_igual = group == Group.radical
                    ? carbonCount == nuevo.carbonCount && isIso == nuevo.isIso
                    : group == nuevo.group && bondCount == nuevo.bondCount;
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
            return null;

        Chain chain = new Chain(0);

        chain.bond(Group.hydrogen); // CH≡
        chain.bond(Group.hydrogen); // CH2=
        chain.bond(Group.hydrogen); // CH3-

        int previous = 1; // CH3-

        if (isIso) {
            chain.bondCarbon(); // CH3-C≡
            chain.bond(Group.hydrogen); // CH3-CH=
            chain.bond(new Substituent(1)); // CH3-CH(CH3)-

            previous += 2; // CH3-CH(CH3)-
        }

        for (int i = previous; i < carbonCount; i++) {
            chain.bondCarbon(); // CH3-CH(CH3)-C≡
            chain.bond(Group.hydrogen); // CH3-CH(CH3)-CH¡
            chain.bond(Group.hydrogen); // CH3-CH(CH3)-CH2-
        }

        return chain; // CH3-CH(CH3)-CH2-
    }

    // Texto:

    private String getStructure() {
        StringBuilder structure = new StringBuilder();

        switch(group) {
            case acid:
                structure.append("COOH");
                break;
            case carbamoyl:
                structure.append("C");
            case amide:
                structure.append("ONH2");
                break;
            case cyanide:
                structure.append("C");
            case nitrile:
                structure.append("N");
                break;
            case aldehyde:
                structure.append("HO");
                break;
            case ketone:
                structure.append("O");
                break;
            case alcohol:
                structure.append("OH");
                break;
            case amine:
                structure.append("NH2");
                break;
            case ether:
                structure.append("-O-");
                break;
            case nitro:
                structure.append("NO2");
                break;
            case bromine:
                structure.append("Br");
                break;
            case chlorine:
                structure.append("Cl");
                break;
            case fluorine:
                structure.append("F");
                break;
            case iodine:
                structure.append("I");
                break;
            case radical:
                if(isIso)
                    structure.append("CH2".repeat(Math.max(0, carbonCount -  3))).append("CH(CH3)2");
                else structure.append("CH2".repeat(Math.max(0, carbonCount -  1))).append("CH3");
                break;
            case hydrogen:
                structure.append("H");
                break;
        }

        return structure.toString();
    }

    @Override
    public String toString() {
        return getStructure();
    }

    // Getters:

    public Group getGroup() {
        return group;
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
