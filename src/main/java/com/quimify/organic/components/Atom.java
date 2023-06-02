package com.quimify.organic.components;

import java.util.*;
import java.util.stream.Collectors;

public class Atom {

    private final Element element;
    private final List<Atom> bondedAtoms;

    // Constants:

    public static final Atom H = new Atom(Element.H);
    public static final Atom N = new Atom(Element.N);
    public static final Atom O = new Atom(Element.O);
    public static final Atom OH = new Atom(Element.O, H);
    public static final Atom NH2 = new Atom(Element.N, H, H);
    public static final Atom NO2 = new Atom(Element.N, O, O);
    public static final Atom Br = new Atom(Element.Br);
    public static final Atom Cl = new Atom(Element.Cl);
    public static final Atom F = new Atom(Element.F);
    public static final Atom I = new Atom(Element.I);

    // Constructor:

    private Atom(Element element, List<Atom> bondedAtoms) {
        this.element = element;
        this.bondedAtoms = bondedAtoms;
    }

    private Atom(Element element, Atom... bondedAtoms) {
        this(element, List.of(bondedAtoms));
    }

    public Atom(Element element) {
        this(element, new ArrayList<>());
    }

    // Modifiers:

    public void bond(Atom atom) {
        bondedAtoms.add(atom);
    }

    // Queries:

    public int getAmountOf(Element element) {
        return (int) bondedAtoms.stream().filter(bondedAtom -> bondedAtom.element == element).count();
    }

    @Override
    public int hashCode() { // Only one node deep
        return Objects.hash(element, getOrderedBondedElements());
    }

    @Override
    public boolean equals(Object other) { // Only one node deep
        if (other == null || getClass() != other.getClass())
            return false;

        Atom otherAtom = (Atom) other;

        if (element != otherAtom.element)
            return false;

        return getOrderedBondedElements().equals(otherAtom.getOrderedBondedElements());
    }

    // Private:

    private List<Element> getOrderedBondedElements() {
        return bondedAtoms.stream().map(Atom::getElement).sorted().collect(Collectors.toList());
    }

    // Getters:

    public Element getElement() {
        return element;
    }

    public List<Atom> getBondedAtoms() {
        return bondedAtoms;
    }

}
