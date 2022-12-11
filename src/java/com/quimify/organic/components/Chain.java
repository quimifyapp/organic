package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.*;
import java.util.stream.Collectors;

public class Chain extends Organic {

	private final List<Carbon> carbons;

	// Constants:

	private static final Carbon CH3 = new Carbon(1);

	static {
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);
	}

	private static final Carbon CHCH3 = new Carbon(2);

	static {
		CHCH3.bond(Group.hydrogen);
		CHCH3.bond(new Substituent(1));
	}

	private static final Carbon CH2 = new Carbon(2);

	static {
		CH2.bond(Group.hydrogen);
		CH2.bond(Group.hydrogen);
	}

	// Constructors:

	public Chain(int usedBondCount) {
		carbons = new ArrayList<>();
		carbons.add(new Carbon(usedBondCount));
	}

	// Public ------------------------------------------------------------------------

	public void bond(Substituent substituent) {
		getLastCarbon().bond(substituent);
	}

	public void bondCarbon() {
		if (getFreeBondCount() > 0) {
			Carbon ultimo = getLastCarbon();
			ultimo.useBond();
			carbons.add(new Carbon(ultimo.getFreeBondCount() + 1));
		} else throw new IllegalStateException("No se puede enlazar un carbono a [" + getStructure() + "].");
	}

	public void removeCarbon(Carbon carbon) {
		carbons.remove(carbon);
	}

	public void correctChainToTheRight() {
		// -CH2-CH2(CH3) → -CH2-CH2-CH3
		if(isBondedTo(Group.radical)) { // To avoid reversing itself needlessly
			Chain inverseOrientation = getInverseOrientation();
			inverseOrientation.correctChainToTheLeft();
			copyCarbons(inverseOrientation.carbons);
		}
	}

	public void correctChainToTheLeft() {
		// CH2(CH3)-CH2- → CH3-CH2-CH2-
		int carbonIndex = 0;

		while (carbonIndex < carbons.size()) {
			boolean correctionPerformed = correctChainToTheLeftIn(carbonIndex);

			// Check if past carbons could be part of a radical
			if (couldBePartOfARadical(carbonIndex))
				// If a correction was performed, start again at the beginning
				// Otherwise, move to the next carbon
				carbonIndex = correctionPerformed ? 0 : carbonIndex + 1;
			else break; // Stop the loop if the current carbon cannot be part of a radical
		}
	}

	public int getSize() {
		return carbons.size();
	}

	public boolean isDone() {
		return getFreeBondCount() == 0;
	}

	public boolean canBondCarbon() {
		return getFreeBondCount() > 0 && getFreeBondCount() < 4;
	}

	public Carbon getCarbon(int index) {
		return carbons.get(index);
	}

	public Carbon getFirstCarbon() {
		return carbons.get(0);
	}

	public Carbon getLastCarbon() {
		return carbons.get(carbons.size() - 1);
	}

	public boolean isBondedTo(Group group) {
		for(Carbon carbon : carbons)
			if(carbon.isBondedTo(group))
				return true;

		return false;
	}

	public void reverseOrientation() {
		Chain inverseOrientation = getInverseOrientation();
		copyCarbons(inverseOrientation.carbons);
	}

	public Chain getInverseOrientation() {
		Chain reversed = clone();

		// Le da la vuelta a los carbonos:
		Collections.reverse(reversed.carbons);

		// Ajusta los enlaces (no son simétricos):
		if(reversed.getSize() > 1) {
			for(int i = 0, j = carbons.size() - 2; i < reversed.getSize() - 1; i++)
				reversed.carbons.get(i).setFreeBondCount(carbons.get(j--).getFreeBondCount());

			reversed.carbons.get(reversed.getSize() - 1).setFreeBondCount(0); // Se supone que no tiene enlaces sueltos
		}

		return reversed;
	}

	public int getFreeBondCount() {
		return getLastCarbon().getFreeBondCount();
	}

	public int getAmountOf(Group group) {
		int amount = 0;

		for(Carbon carbon : carbons)
			amount += carbon.getAmountOf(group);

		return amount;
	}

	public Optional<Group> getPriorityGroup() {
		for(Group group : Group.values())
			for(Carbon carbon : carbons)
				if(carbon.isBondedTo(group))
					return Optional.of(group);

		return Optional.empty();
	}

	public List<Group> getGroups() {
		return Arrays.stream(Group.values()).filter(this::isBondedTo).collect(Collectors.toList());
	}

	public List<Integer> getIndexesOf(Group group) {
		return getIndexesOf(carbons.stream()
				.map(carbon -> carbon.getAmountOf(group))
				.collect(Collectors.toList()));
	}

	public List<Integer> getIndexesOf(Substituent substituent) {
		return getIndexesOf(carbons.stream()
				.map(carbon -> carbon.getAmountOf(substituent))
				.collect(Collectors.toList()));
	}

	public List<Substituent> getSubstituents() {
		List<Substituent> substituents = new ArrayList<>();
		carbons.forEach(carbon -> substituents.addAll(carbon.getSubstituents()));
		return substituents;
	}

	@Override
	protected Chain clone() { // TODO throw?
		try {
			Chain chain = (Chain) super.clone();
			chain.carbons.clear();
			carbons.forEach(carbon -> chain.carbons.add(carbon.clone()));
			return chain;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	// Private:

	private void copyCarbons(List<Carbon> carbons) {
		this.carbons.clear();
		carbons.forEach(carbon -> this.carbons.add(carbon.clone()));
	}

	private List<Integer> getIndexesOf(List<Integer> amounts) {
		List<Integer> indexes = new ArrayList<>();

		for(int i = 0; i < amounts.size(); i++)
			indexes.addAll(Collections.nCopies(amounts.get(i), i));

		return indexes;
	}

	private boolean correctChainToTheLeftIn(int carbonIndex) {
		// Pre-condition: any carbon to the left could be part of a radical
		Carbon carbon = carbons.get(carbonIndex);

		if (!carbon.isBondedTo(Group.radical))
			return false;

		Substituent greatestRadical = getGreatestRadicalIn(carbon);

		// Replaces the greatest radical with the left part of the chain:

		if(carbonIndex > 0) {
			Substituent leftSideRadical = getRadicalToTheLeftOf(carbonIndex);

			// Calculates if that radical would make a longer chain:
			if(greatestRadical.compareTo(leftSideRadical) <= 0)
				return false;

			// Replaces the greatestRadical with the newly created:
			carbon.unbond(greatestRadical);
			carbon.bond(leftSideRadical);
		}
		else carbon.remove(greatestRadical); // There is no left side of the chain

		// Replaces the left part of the chain with the greatest radical:

		List<Carbon> newCarbons = getCarbonsInRadical(greatestRadical);

		// Substitutes the current chain with the new one:

		newCarbons.addAll(carbons.subList(carbonIndex, carbons.size())); // The rest of it

		carbons.clear();
		carbons.addAll(newCarbons);

		return true;
	}

	private Substituent getGreatestRadicalIn(Carbon carbon) {
		if (!carbon.isBondedTo(Group.radical))
			throw new IllegalArgumentException("The carbon: " + carbon + " isn't bonded to any radical.");

		List<Substituent> radicals = new ArrayList<>(carbon.getSubstituents());
		radicals.removeIf(substituent -> substituent.getGroup() != Group.radical);
		radicals.sort(Substituent::compareTo);

		return radicals.get(radicals.size() - 1);
	}

	private Substituent getRadicalToTheLeftOf(int carbonIndex) {
		if (carbonIndex == 0)
			throw new IllegalArgumentException("First carbon doesn't have any radical to its left.");

		if (carbonIndex > 1 && carbons.get(1).equals(CHCH3))
			return new Substituent(carbonIndex + 1, true);
		else return new Substituent(carbonIndex);
	}

	private List<Carbon> getCarbonsInRadical(Substituent radical) {
		if (radical.getGroup() != Group.radical)
			throw new IllegalArgumentException("Substituent: " + radical + " is not a radical.");

		List<Carbon> carbonsInRadical = new ArrayList<>();
		carbonsInRadical.add(CH3);

		int carbonCount = 1; // CH3-

		if (radical.isIso()) {
			carbonsInRadical.add(CHCH3); // CH3-CH(CH3)-
			carbonCount += 2; // It had a methyl bonded to it
		}

		int remaining = radical.getCarbonCount() - carbonCount;
		carbonsInRadical.addAll(Collections.nCopies(remaining, CH2)); // CH3-CH(CH3)-CH2-

		return carbonsInRadical;
	}

	private boolean couldBePartOfARadical(int carbonIndex) {
		Carbon carbon = carbons.get(carbonIndex);

		if(carbon.isBondedTo(Group.alkene) || carbon.isBondedTo(Group.alkyne))
			return false;

		Carbon CH3 = new Carbon(1);
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);

		Carbon CH2 = new Carbon(2);
		CH2.bond(Group.hydrogen);
		CH2.bond(Group.hydrogen);

		switch (carbonIndex) {
			case 0:
				return carbon.equals(CH3);
			case 1: // Could be part of a 'iso' radical
				Carbon CHCH3 = new Carbon(2);
				CHCH3.bond(Group.hydrogen);
				CHCH3.bond(new Substituent(1));

				return Set.of(CH2, CHCH3).contains(carbon);
			default:
				return carbon.equals(CH2);
		}
	}

	// Text:

	private String getStructure() {
		StringBuilder formula = new StringBuilder();

		if(carbons.size() > 0) {
			// Se escribe el primero:
			Carbon firstCarbon = carbons.get(0);
			formula.append(firstCarbon); // Como CH

			// Se escribe el resto con los enlaces libres del anterior:
			int previousFreeBonds = firstCarbon.getFreeBondCount();
			for(int i = 1; i < carbons.size(); i++) {
				formula.append(bondSymbolFor(previousFreeBonds)); // Como CH=
				formula.append(carbons.get(i)); // Como CH=CH

				previousFreeBonds = carbons.get(i).getFreeBondCount();
			}

			// Se escribe los enlaces libres del último:
			if(previousFreeBonds > 0 && previousFreeBonds < 4) // Ni está completo ni es el primero vacío
				formula.append(bondSymbolFor(previousFreeBonds - 1)); // Como CH=CH-CH2-C≡
		}

		return formula.toString();
	}

	@Override
	public String toString() {
		return getStructure();
	}

}
