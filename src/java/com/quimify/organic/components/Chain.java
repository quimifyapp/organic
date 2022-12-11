package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.*;
import java.util.stream.Collectors;

public class Chain extends Organic {

	private final List<Carbon> carbons;

	// Constructors:

	public Chain(int usedBondCount) {
		carbons = new ArrayList<>();
		carbons.add(new Carbon(usedBondCount));
	}

	public Chain(Chain other) {
		carbons = new ArrayList<>();
		addCopyOf(other.carbons);
	}

	private void addCopyOf(List<Carbon> carbons) {
		for(Carbon carbon : carbons)
			this.carbons.add(new Carbon(carbon));
	}

	// TODO organize

	// Public ------------------------------------------------------------------------

	public void bond(Substituent substituent) {
		getLastCarbon().bond(substituent);
	}

	public void bond(Group group) {
		bond(new Substituent(group));
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

	public void invertOrientation() {
		become(getReversed());
	}

	public void correctChainToTheLeft() { // CH2(CH3)-CH2- → CH3-CH2-CH2-
		int carbonIndex = 0;

		while (carbonIndex < carbons.size()) {
			boolean corrected = correctChainToTheLeftIn(carbonIndex);

			// To this point, past carbons COULD be part of a radical
			if(couldBePartOfARadical(carbonIndex))
				carbonIndex = corrected ? 0 : carbonIndex + 1;
			else break;
		}
	}

	private boolean correctChainToTheLeftIn(int carbonIndex) {
		// Pre-condition: any carbon to the left could be part of a radical
		Carbon carbon = carbons.get(carbonIndex);

		if (!carbon.isBondedTo(Group.radical))
			return false;

		List<Substituent> radicals = new ArrayList<>(carbon.getSubstituents());
		radicals.removeIf(substituent -> substituent.getGroup() != Group.radical);
		radicals.sort(Substituent::compareTo);

		Substituent greatestRadical = radicals.get(radicals.size() - 1);

		if(carbonIndex == 0)
			carbon.remove(greatestRadical);
		else {
			Substituent leftSide;

			if(carbonIndex > 1) { // Could be whatever radical, maybe even an 'iso' one
				Carbon CHCH3 = new Carbon(2);
				CHCH3.bond(Group.hydrogen);
				CHCH3.bond(new Substituent(1));

				boolean isIso = carbons.get(1).equals(CHCH3);

				leftSide = new Substituent(isIso ? carbonIndex + 1 : carbonIndex, isIso);
			}
			else leftSide = new Substituent(1); // Can only be methyl

			// Calculates if that radical would make a longer chain:
			if(greatestRadical.compareTo(leftSide) <= 0)
				return false;

			// Radical substitution:
			carbon.unbond(greatestRadical);
			carbon.bond(leftSide);
		}

		// New chain left side:
		Chain newLeftSide = greatestRadical.toChain();

		// Chain substitution:
		newLeftSide.bondCarbons(carbons.subList(carbonIndex, carbons.size()));

		// Chain substitution:
		become(newLeftSide);

		return true;
	}

	private boolean couldBePartOfARadical(int carbonIndex) {
		Carbon carbon = carbons.get(carbonIndex);

		if(carbon.isBondedTo(Group.alkene) || carbon.isBondedTo(Group.alkyne))
			return false;

		boolean couldBePartOfARadical;

		Carbon CH3 = new Carbon(1);
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);
		CH3.bond(Group.hydrogen);

		Carbon CH2 = new Carbon(2);
		CH2.bond(Group.hydrogen);
		CH2.bond(Group.hydrogen);

		switch (carbonIndex) {
			case 0:
				couldBePartOfARadical = carbon.equals(CH3);
				break;
			case 1: // Could be part of a 'iso' radical
				Carbon CHCH3 = new Carbon(2);
				CHCH3.bond(Group.hydrogen);
				CHCH3.bond(new Substituent(1));

				couldBePartOfARadical = Set.of(CH2, CHCH3).contains(carbon);
				break;
			default:
				couldBePartOfARadical = carbon.equals(CH2);
				break;
		}

		return couldBePartOfARadical;
	}

	// QUERIES -----------------------------------------------------------------------

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

	public Chain getReversed() {
		Chain reversed = new Chain(this);

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

	private List<Integer> getIndexesOf(List<Integer> amounts) {
		List<Integer> indexes = new ArrayList<>();

		for(int i = 0; i < amounts.size(); i++)
			indexes.addAll(Collections.nCopies(amounts.get(i), i));

		return indexes;
	}

	private void bondCarbons(List<Carbon> carbons) {
		getLastCarbon().useBond();
		addCopyOf(carbons);
	}

	private void become(Chain other) {
		carbons.clear();
		addCopyOf(other.carbons);
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
