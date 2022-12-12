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
		String firstChainStructure = firstChain.toString();

		return currentChain == firstChain
				? firstChainStructure.substring(0, firstChainStructure.length() - 1) // CH-O-= -> CH-O-
				: firstChainStructure + secondChain.toString();
	}

	// TODO fix repeated code

	// Naming:

	private boolean isRedundantInName(Group group, Chain chain) {
		boolean isRedundant;

		// Derivados del propil:
		if (chain.getSize() == 3)
			isRedundant = group == Group.alkene && chain.getAmountOf(Group.alkene) == 2; // Es propadienil
			// Derivados del etil:
		else if (chain.getSize() == 2)
			isRedundant = isBond(group); // Solo hay una posición posible para el enlace
			// Derivados del metil:
		else isRedundant = chain.getSize() == 1;

		return isRedundant;
	}

	private Locator getPrefixFor(Group group, Chain chain) {
		Locator prefix;

		List<Integer> indexes = chain.getIndexesOf(group);
		String name = prefixNameParticleFor(group);

		if (isRedundantInName(group, chain)) // Sobran los localizadores porque son evidentes
			prefix = new Locator(multiplierFor(indexes.size()), name); // Como "difluoro"
		else prefix = new Locator(indexes, name); // Como "1,2-difluoro"

		return prefix;
	}

	private String getBondNameForIn(Group bond, Chain chain) {
		String bondName = "";

		List<Integer> indexes = chain.getIndexesOf(bond);
		String nameParticle = bondNameParticleFor(bond);

		if (indexes.size() > 0) {
			Locator locator;

			if (isRedundantInName(bond, chain)) // Sobran los localizadores porque son evidentes
				locator = new Locator(multiplierFor(indexes.size()), nameParticle); // Como "dien"
			else locator = new Locator(indexes, nameParticle); // Como "1,2-dien"

			String localizador_to_string = locator.toString();

			if (startsWithDigit(localizador_to_string))
				bondName += "-"; // Guion *antes* de los localizadores

			bondName += localizador_to_string;
		}

		return bondName;
	}

	private String getNameFor(Chain chain) {
		List<Group> bondedGroups = chain.getGroups();
		bondedGroups.removeIf(group -> group == Group.hydrogen || group == Group.ether);

		int groupsIndex = 0;

		// Se procesan los prefijos:
		List<Locator> prefixes = new ArrayList<>();

		while (groupsIndex < bondedGroups.size()) {
			if (!isBond(bondedGroups.get(groupsIndex)) && bondedGroups.get(groupsIndex) != Group.radical)
				prefixes.add(getPrefixFor(bondedGroups.get(groupsIndex), chain));

			groupsIndex++;
		}

		Set<Substituent> uniqueRadicals = new HashSet<>(chain.getSubstituents());
		uniqueRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

		for (Substituent radical : uniqueRadicals)
			prefixes.add(new Locator(chain.getIndexesOf(radical), radicalNameParticleFor(radical)));

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
		String enlaces = getBondNameForIn(Group.alkene, chain) + getBondNameForIn(Group.alkyne, chain);

		// Se procesa el cuantificador:
		String cuantificador = quantifierFor(chain.getSize());

		if (!enlaces.equals("") && Organic.doesNotStartWithVowel(enlaces))
			cuantificador += "a";

		return prefix + cuantificador + enlaces + "il";
	}

}
