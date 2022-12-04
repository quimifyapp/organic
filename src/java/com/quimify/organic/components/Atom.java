package com.quimify.organic.components;

import java.util.*;
import java.util.stream.Collectors;

public class Atom {

	private final Integer id;
	private final Element element;
	private final List<Atom> bondedAtoms;

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

	public Atom(Element element, List<Atom> bondedAtoms) { // TODO set?
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

	// Modificadores:

	public void bond(Atom other) {
		bondedAtoms.add(other);
	}

	// Queries:

	public boolean isElement(Element element) {
		return this.element == element;
	}

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

	// Métodos get:

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

	public Atom toAnonymous() {
		Atom anonymousAtom = new Atom(element, getBondedAtomsCutOff());
		anonymousAtom.bondedAtoms.replaceAll(Atom::toAnonymous);
		return anonymousAtom;
	}

	public List<Atom> getBonded(Element element) {
		return bondedAtoms.stream().filter(bondedAtom -> bondedAtom.isElement(element)).collect(Collectors.toList());
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
