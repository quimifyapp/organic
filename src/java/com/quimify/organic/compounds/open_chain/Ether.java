package com.quimify.organic.compounds.open_chain;

import com.quimify.organic.Organic;
import com.quimify.organic.components.Atom;
import com.quimify.organic.components.Chain;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Ether extends Organic implements OpenChain {

	private final Chain firstChain; // R
	private Chain secondChain, currentChain; // R', ->

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
		firstChain = new Chain(0); // R - O
		currentChain = this.firstChain;
	}

	public Ether(Simple firstChain) {
		this.firstChain = firstChain.getChain(); // R - O
		if(firstChain.isDone())
			startSecondChain(); // R - O - C≡
		else currentChain = this.firstChain;
	}

	// OPEN CHAIN --------------------------------------------------------------------

	public int getFreeBonds() {
		return currentChain.getFreeBonds();
	}

	public boolean isDone() {
		return currentChain.isDone();
	}

	public void bondCarbon() {
		currentChain.bondCarbon();
	}

	public void bond(Substituent substituent) {
		if (bondableGroups.contains(substituent.getGroup())) {
			currentChain.bond(substituent);

			if (currentChain == firstChain && firstChain.isDone())
				if (currentChain.isDone())
					startSecondChain();
		}
		else throw new IllegalArgumentException("Couldn't bond " + substituent.getGroup() + " to an Ether.");
	}

	public void bond(Group group) {
		bond(new Substituent(group));
	}

	public void correct() {
		correctChainsStructure();
	}

	public List<Group> getBondableGroups() {
		if(getFreeBonds() == 0)
			return Collections.emptyList();

		List<Group> result = new ArrayList<>();

		if(currentChain == firstChain)
			result.add(Group.ether);

		result.add(Group.nitro);
		result.add(Group.bromine);
		result.add(Group.chlorine);
		result.add(Group.fluorine);
		result.add(Group.iodine);
		result.add(Group.radical);
		result.add(Group.hydrogen);

		return result;
	}

	public String getName() {
		String name;

		String firstChainName = getChainNameFor(firstChain.getInverseOriented()); // Se empieza a contar desde el oxígeno
		String secondChainName = getChainNameFor(secondChain); // La secundaria ya está en el orden bueno

		if (!firstChainName.equals(secondChainName)) {
			// Chains are alphabetically ordered:
			if (firstChainName.compareTo(secondChainName) < 0)
				name = firstChainName + " " + secondChainName;
			else name = secondChainName + " " + firstChainName;
		} else name = (startsWithDigit(firstChainName) ? "di " : "di") + firstChainName;

		return name + " éter";
	}

	public String getStructure() {
		String firstChainStructure = firstChain.getStructure();

		return currentChain == firstChain
				? firstChainStructure.substring(0, firstChainStructure.length() - 1)
				: firstChainStructure + secondChain.getStructure();
	}

	// PRIVATE -----------------------------------------------------------------------

	// Modifiers:

	private void correctChainsStructure() {
		// Se corrigen los radicales que podrían formar parte de las cadenas principales:
		firstChain.correctChainStructureToTheLeft(); // Si no tiene radicales, no hará nada

		if (secondChain.hasFunctionalGroup(Group.radical)) { // Para ahorrar el invertir la cadena
			secondChain.invertOrientation(); // En lugar de corregirlos por la derecha
			secondChain.correctChainStructureToTheLeft(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
			secondChain.invertOrientation(); // Es necesario para no romper el orden del éter
		}
	}

	private void startSecondChain() {
		secondChain = new Chain(1);
		currentChain = secondChain;
	}

	// Queries:

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;

		Ether ether = (Ether) other;

		return firstChain.equals(ether.firstChain) && secondChain.equals(ether.secondChain);
	}

	// Text: TODO: poner en común en Cadena

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
		String name = getPrefixNameParticle(group);

		if (isRedundantInName(group, chain)) // Sobran los localizadores porque son evidentes
			prefix = new Locator(multiplicadorDe(indexes.size()), name); // Como "difluoro"
		else prefix = new Locator(indexes, name); // Como "1,2-difluoro"

		return prefix;
	}

	private String getBondNameForIn(Group bond, Chain chain) {
		String bondName = "";

		List<Integer> indexes = chain.getIndexesOf(bond);
		String nameParticle = getBondNameParticle(bond);

		if (indexes.size() > 0) {
			Locator locator;

			if (isRedundantInName(bond, chain)) // Sobran los localizadores porque son evidentes
				locator = new Locator(multiplicadorDe(indexes.size()), nameParticle); // Como "dien"
			else locator = new Locator(indexes, nameParticle); // Como "1,2-dien"

			String localizador_to_string = locator.toString();

			if (startsWithDigit(localizador_to_string))
				bondName += "-"; // Guion *antes* de los localizadores

			bondName += localizador_to_string;
		}

		return bondName;
	}

	private String getChainNameFor(Chain chain) {
		List<Group> bondedGroups = chain.getBondedGroups();
		bondedGroups.removeIf(group -> group == Group.hydrogen || group == Group.ether);

		// Exceptional cases:
		if (bondedGroups.size() == 1) {
			if (chain.hasMethylAt(1))
				return "iso" + cuantificadorDe(chain.getSize() + 1) + "il";
			else if (chain.hasMethylAt(2))
				return "sec" + cuantificadorDe(chain.getSize() + 1) + "il";
		}

		int groupsIndex = 0;

		// Se procesan los prefijos:
		List<Locator> prefixes = new ArrayList<>();

		while (groupsIndex < bondedGroups.size()) {
			if (!isBond(bondedGroups.get(groupsIndex)) && bondedGroups.get(groupsIndex) != Group.radical)
				prefixes.add(getPrefixFor(bondedGroups.get(groupsIndex), chain));

			groupsIndex++;
		}

		Set<Substituent> uniqueRadicals = chain.getUniqueSubstituents();
		uniqueRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

		for (Substituent radical : uniqueRadicals)
			prefixes.add(new Locator(chain.getIndexesOf(radical), getRadicalNameParticle(radical)));

		StringBuilder prefix = new StringBuilder(chain.hasFunctionalGroup(Group.acid) ? "ácido " : "");
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
		String cuantificador = cuantificadorDe(chain.getSize());

		if (!enlaces.equals("") && Organic.doesNotStartWithVowel(enlaces))
			cuantificador += "a";

		return prefix + cuantificador + enlaces + "il";
	}

	@Override
	public String toString() {
		return getStructure();
	}

}
