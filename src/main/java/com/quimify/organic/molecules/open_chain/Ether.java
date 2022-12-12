package com.quimify.organic.molecules.open_chain;

import com.quimify.organic.Organic;
import com.quimify.organic.components.Atom;
import com.quimify.organic.components.Chain;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.*;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Ether extends Organic implements OpenChain {

	private final Chain firstChain; // R
	private final Chain secondChain; // R'
	private Chain currentChain; // Pointer

	// Constants:

	public static final Set<Atom> bondableAtoms = Set.of(
			Atom.NO2,
			Atom.Br,
			Atom.Cl,
			Atom.F,
			Atom.I,
			Atom.H
	);

	private static final Set<Group> bondableGroups = Set.of(
			Group.ether,
			Group.nitro,
			Group.bromine,
			Group.chlorine,
			Group.fluorine,
			Group.iodine,
			Group.radical,
			Group.hydrogen
	);

	// Constructors:

	public Ether() {
		this.firstChain = new Chain(0); // (C)
		this.secondChain = new Chain(1); // -C≡
		currentChain = this.firstChain;
	}

	Ether(Chain firstChain) {
		this.firstChain = firstChain; // R
		this.secondChain = new Chain(1); // -C≡
		currentChain = firstChain.isDone() ? secondChain : firstChain;
	}

	// Interface:

	public boolean isDone() {
		return currentChain.isDone();
	}

	public List<Group> getBondableGroups() {
		List<Group> bondableGroups = new ArrayList<>();

		if(currentChain.getFreeBondCount() >= 1) {
			if (!firstChain.isBondedTo(Group.ether))
				bondableGroups.add(Group.ether);

			bondableGroups.add(Group.nitro);
			bondableGroups.add(Group.bromine);
			bondableGroups.add(Group.chlorine);
			bondableGroups.add(Group.fluorine);
			bondableGroups.add(Group.iodine);
			bondableGroups.add(Group.radical);
			bondableGroups.add(Group.hydrogen);
		}

		return bondableGroups;
	}

	public OpenChain bond(Group group) {
		return bond(new Substituent(group));
	}

	public OpenChain bond(Substituent substituent) {
		if (bondableGroups.contains(substituent.getGroup())) {
			currentChain.bond(substituent);

			if (currentChain == firstChain && firstChain.isDone())
				currentChain = secondChain;
		}
		else throw new IllegalArgumentException("Couldn't bond " + substituent.getGroup() + " to an Ether.");

		return this;
	}

	public boolean canBondCarbon() {
		if(currentChain == firstChain)
			return !firstChain.isBondedTo(Group.ether) && firstChain.canBondCarbon();
		else return secondChain.canBondCarbon();
	}

	public void bondCarbon() {
		currentChain.bondCarbon();
	}

	public void correct() {
		firstChain.correctChainToTheLeft(); // CF(CH3)-O- → CH3-CF-O-
		secondChain.correctChainToTheRight(); // -O-CHF(CH3) → -O-CHF-CH3
	}

	public String getName() {
		String name;

		String firstChainName = getNameFor(firstChain.getInverseOrientation()); // Se empieza a contar desde el oxígeno
		String secondChainName = getNameFor(secondChain); // La secundaria ya está en el orden bueno

		if (!firstChainName.equals(secondChainName)) {
			// Chains are alphabetically ordered:
			if (firstChainName.compareTo(secondChainName) < 0)
				name = firstChainName + " " + secondChainName;
			else name = secondChainName + " " + firstChainName;
		}
		else name = (startsWithDigit(firstChainName) ? "di " : "di") + firstChainName;

		return name + " éter";
	}

	public String getStructure() {
		String firstChain = this.firstChain.toString();

		if(currentChain == this.firstChain)
			return firstChain.substring(0, firstChain.length() - 1); // CH-O-= -> CH-O-
		else return firstChain + secondChain.toString();
	}

	// Naming:

	private boolean isRedundantInNameIn(Group group, Chain chain) {
		boolean redundant;

		// Derivados del propil:
		if (chain.getSize() == 3)
			redundant = group == Group.alkene && chain.getAmountOf(Group.alkene) == 2; // Es propadienil
			// Derivados del etil:
		else if (chain.getSize() == 2)
			redundant = isBond(group); // Solo hay una posición posible para el enlace
			// Derivados del metil:
		else redundant = chain.getSize() == 1;

		return redundant;
	}

	private String getNameFor(Chain chain) { // TODO fix repeated code
		List<Group> groups = chain.getGroups();
		groups.removeIf(group -> group == Group.hydrogen || group == Group.ether);

		int groupIndex = 0;

		// Se procesan los prefijos:
		List<Locator> prefixes = new ArrayList<>();

		while (groupIndex++ < groups.size()) {
			if (!isBond(groups.get(groupIndex)) && groups.get(groupIndex) != Group.radical) {
				Group group = groups.get(groupIndex);
				boolean redundant = isRedundantInNameIn(group, chain);

				prefixes.add(Organic.getPrefixForIn(group, chain, redundant));
			}

			groupIndex++;
		}

		Set<Substituent> uniqueRadicals = new HashSet<>(chain.getSubstituents());
		uniqueRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

		for (Substituent radical : uniqueRadicals)
			prefixes.add(new Locator(chain.getIndexesOf(radical), Organic.radicalNameParticleFor(radical)));

		StringBuilder prefix = new StringBuilder(chain.isBondedTo(Group.acid) ? "ácido " : "");
		if (prefixes.size() > 0) {
			Locator.ordenarAlfabeticamente(prefixes);

			for (int i = 0; i < prefixes.size() - 1; i++) {
				prefix.append(prefixes.get(i).toString());

				if (doesNotStartWithLetter(prefixes.get(i + 1).toString()))
					prefix.append("-");
			}

			prefix.append(prefixes.get(prefixes.size() - 1));
		}

		// Se procesan los enlaces:
		String enlaces = Organic.getBondNameForIn(Group.alkene, chain, isRedundantInNameIn(Group.alkene, chain)) +
				Organic.getBondNameForIn(Group.alkyne, chain, isRedundantInNameIn(Group.alkyne, chain));

		// Se procesa el cuantificador:
		String cuantificador = quantifierFor(chain.getSize());

		if (!enlaces.equals("") && Organic.doesNotStartWithVowel(enlaces))
			cuantificador += "a";

		return prefix + cuantificador + enlaces + "il";
	}

}
