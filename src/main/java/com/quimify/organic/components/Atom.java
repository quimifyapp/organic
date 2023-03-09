package com.quimify.organic.components;

import java.util.*;

public class Atom {

	private final Integer id;
	private final Element element;
	private final List<Atom> bondedAtoms;

	// Constants:

	public static final Atom H = new Atom(Element.H);
	public static final Atom N = new Atom(Element.N);
	public static final Atom O = new Atom(Element.O);
	public static final Atom OH = new Atom(Element.O, List.of(H)); // TODO set?
	public static final Atom NH2 = new Atom(Element.N, List.of(H, H));
	public static final Atom NO2 = new Atom(Element.N, List.of(O, O));
	public static final Atom Br = new Atom(Element.Br);
	public static final Atom Cl = new Atom(Element.Cl);
	public static final Atom F = new Atom(Element.F);
	public static final Atom I = new Atom(Element.I);

	// Error messages:

	private static final String unknownAtomError = "Unknown organic atom: %s.";
	private static final String unknownFunctionalGroupError = "Unknown functional group given atom: %s.";

	// Constructor:

	private Atom(Integer id, Element element, List<Atom> bondedAtoms) {
		this.id = id;
		this.element = element;
		this.bondedAtoms = bondedAtoms;
	}

	public Atom(int id, String symbol) {
		this.id = id;

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
				throw new IllegalArgumentException(String.format(unknownAtomError, symbol));
		}

		this.bondedAtoms = new ArrayList<>();
	}

	private Atom(Element element, List<Atom> bondedAtoms) {
		this(null, element, bondedAtoms);
	}

	protected Atom(Element element) {
		this(element, new ArrayList<>());
	}

	private Atom(Atom other) {
		this(other.id, other.element, new ArrayList<>(other.bondedAtoms));
	}

	// Modifiers:

	public void bond(Atom other) {
		bondedAtoms.add(other);
	}

	// Queries:

	public int getAmountOf(Element element) {
		return (int) bondedAtoms.stream().filter(bondedAtom -> bondedAtom.getElement() == element).count();
	}

	public List<Atom> getBondedAtomsSeparated() {
		List<Atom> bondedAtomsCutOff = new ArrayList<>();

		for (Atom bondedAtom : bondedAtoms)
			bondedAtomsCutOff.add(new Atom(bondedAtom));

		if (id != null)
			for (Atom bondedAtomCutOff : bondedAtomsCutOff)
				bondedAtomCutOff.bondedAtoms.removeIf(bondedAtom ->
						Objects.equals(id, bondedAtom.id)); // Removes itself from the copy

		return bondedAtomsCutOff;
	}

	public Atom toAnonymous() {
		Atom anonymousAtom = new Atom(element, getBondedAtomsSeparated());
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
		else throw new IllegalArgumentException(String.format(unknownFunctionalGroupError, element));

		return group;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, element, new HashSet<>(getBondedAtomsSeparated()));
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;

		Atom otherAtom = (Atom) other;

		if(!Objects.equals(id, otherAtom.id)) // Objects.equals(null, null) = true
			return false;

		if (element != otherAtom.element)
			return false;

		if (bondedAtoms.size() != otherAtom.bondedAtoms.size())
			return false;

		// Equality check between bonded atoms, one node deep and regardless of order:

		List<Atom> bondedAtomsCutOff = getBondedAtomsSeparated();
		List<Atom> othersBondedAtomsCutOff = otherAtom.getBondedAtomsSeparated();

		for (Atom bondedAtom : bondedAtomsCutOff) {
			int frequency = Collections.frequency(bondedAtomsCutOff, bondedAtom);
			int otherFrequency = Collections.frequency(othersBondedAtomsCutOff, bondedAtom);

			if (frequency != otherFrequency)
				return false;
		}

		return true;
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
