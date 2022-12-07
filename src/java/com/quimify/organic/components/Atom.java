package com.quimify.organic.components;

import java.util.*;
import java.util.stream.Collectors;

public class Atom {

	private final Integer id;
	private final Element element;
	private final List<Atom> bondedAtoms;

	// Constants:

	public static final Atom H = new Atom(Element.H);
	public static final Atom N = new Atom(Element.N);
	public static final Atom O = new Atom(Element.O);
	public static final Atom OH = new Atom(Element.O, List.of(H));
	public static final Atom NH2 = new Atom(Element.N, List.of(H, H));
	public static final Atom NO2 = new Atom(Element.N, List.of(O, O));
	public static final Atom Br = new Atom(Element.Br);
	public static final Atom Cl = new Atom(Element.Cl);
	public static final Atom F = new Atom(Element.F);
	public static final Atom I = new Atom(Element.I);

	// Constructor:

	public Atom(int id, String symbol) {
		this.id = id;
		bondedAtoms = new ArrayList<>();

		switch (symbol) {
			case "C":
				element = Element.C;
				break;
			case "H":
				element = Element.H;
				break;
			case "N":
				element = Element.N;
				break;
			case "O":
				element = Element.O;
				break;
			case "Br":
				element = Element.Br;
				break;
			case "Cl":
				element = Element.Cl;
				break;
			case "F":
				element = Element.F;
				break;
			case "I":
				element = Element.I;
				break;
			default:
				throw new IllegalArgumentException("No se contempla el átomo \"" + symbol + "\".");
		}
	}

	public Atom(Element element, List<Atom> bondedAtoms) {
		id = null;
		this.element = element;
		this.bondedAtoms = bondedAtoms;
	}

	public Atom(Element element) {
		id = null;
		this.element = element;
		bondedAtoms = new ArrayList<>();
	}

	private Atom(Atom other) {
		id = other.id;
		element = other.element;
		bondedAtoms = new ArrayList<>(other.bondedAtoms);
	}

	// Modifiers:

	public void bond(Atom other) {
		bondedAtoms.add(other);
	}

	// Queries:

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;

		Atom otherAtom = (Atom) other;

		if(!Objects.equals(id, otherAtom.id))
			return false; // Objects.equals(null, null) = true
		if (element != otherAtom.element)
			return false;
		if (bondedAtoms.size() != otherAtom.bondedAtoms.size())
			return false;

		List<Atom> otherBondedAtomsCutOff = otherAtom.getBondedAtomsCutOff();

		return getBondedAtomsCutOff().stream().allMatch(bondedAtomCutOff ->
				otherBondedAtomsCutOff.stream().anyMatch(bondedAtomCutOff::equals));
	}

	public Atom toAnonymous() {
		Atom anonymousAtom = new Atom(element, getBondedAtomsCutOff());
		anonymousAtom.bondedAtoms.replaceAll(Atom::toAnonymous);
		return anonymousAtom;
	}

	public Group toFunctionalGroup() {
		Group group;

		Atom anonymousAtom = toAnonymous();
		if (anonymousAtom.equals(Atom.H))
			group = Group.hydrogen;
		else if (anonymousAtom.equals(Atom.N))
			group = Group.nitrile;
		else if (anonymousAtom.equals(Atom.O))
			group = Group.ketone;
		else if (anonymousAtom.equals(Atom.OH))
			group = Group.alcohol;
		else if (anonymousAtom.equals(Atom.NH2))
			group = Group.amine;
		else if (anonymousAtom.equals(Atom.NO2))
			group = Group.nitro;
		else if (anonymousAtom.equals(Atom.Br))
			group = Group.bromine;
		else if (anonymousAtom.equals(Atom.Cl))
			group = Group.chlorine;
		else if (anonymousAtom.equals(Atom.F))
			group = Group.fluorine;
		else if (anonymousAtom.equals(Atom.I))
			group = Group.iodine;
		else throw new IllegalArgumentException("Couldn't find FunctionalGroup of Atom: " + element + ".");

		return group;
	}

	public List<Atom> getBondedAtomsCutOff() {
		List<Atom> bondedAtomsCutOff = new ArrayList<>();

		for (Atom bondedAtom : bondedAtoms)
			bondedAtomsCutOff.add(new Atom(bondedAtom));

		if (id != null)
			for (Atom bondedAtomCutOff : bondedAtomsCutOff)
				bondedAtomCutOff.bondedAtoms.removeIf(bondedAtom ->
						Objects.equals(id, bondedAtom.id)); // Cuts itself off from the copy

		return bondedAtomsCutOff;
	}

	public List<Atom> getBonded(Element element) {
		return bondedAtoms.stream().filter(bondedAtom -> bondedAtom.getElement() == element).collect(Collectors.toList());
	}

	// Getters:

	public Integer getId() {
		return id;
	}

	public Element getElement() {
		return element;
	}

	public List<Atom> getBondedAtoms() {
		return bondedAtoms;
	}

}
