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

	public void correctChainStructureToTheLeft() { // CH2(CH3)-CH2- → CH3-CH2-CH2-
		boolean corrected; // Para actualizar el iterador tras iteración
		for (int i = 0; i < carbons.size(); i = corrected ? 0 : i + 1) { // Sin incremento
			if(carbons.get(i).getSubstituentsOf(Group.radical).size() > 0) { // Este carbono tiene radicales
				// Se obtiene el mayor radical de este carbono:
				Substituent mayor_radical = carbons.get(i).getGreatestRadical();

				// Se calcula si el "camino" por este radical es preferible a la cadena principal:
				int comparacion = Integer.compare(mayor_radical.getStraightCarbonCount(), i);

				if(comparacion == 1 || (comparacion == 0 && mayor_radical.isIso())) {
					// Se corrige la cadena por la izquierda:
					if(i != 0) {
						// Se convierte el camino antiguo de la cadena principal en radical:
						Substituent oldRadical;

						// Aquí se tiene en cuenta que, de haber un radical, solo podría ser metil
						if(i > 1 && carbons.get(1).isBondedTo(Group.radical) // Hay un metil en el segundo carbono
								&& carbons.get(1).getSubstituentsWithoutHydrogen().get(0).equals(new Substituent(1)))
							oldRadical = new Substituent(i + 1, true);
						else oldRadical = new Substituent(i);

						// Se enlaza tal radical:
						carbons.get(i).bond(oldRadical);

						// Se elimina el radical que será el camino de la cadena principal:
						carbons.get(i).unbond(mayor_radical);

						// Se elimina el camino antiguo de la cadena principal:
						carbons.subList(0, i).clear();
					}
					else carbons.get(0).remove(mayor_radical); // Será el camino de la cadena principal

					// Se convierte el radical en el nuevo camino de la cadena principal:
					Chain parte_izquierda = mayor_radical.toChain();
					parte_izquierda.bondCarbons(carbons);

					// Se efectúa el cambio:
					become(parte_izquierda);
					corrected = true;
				}
				else corrected = false;
			}
			else corrected = false;

			// Se comprueba si este carbono no podría estar en un radical, ergo debe pertenecer a la cadena principal:

			List<Substituent> substituents = carbons.get(i).getSubstituentsWithoutHydrogen(); // Se da por hecho que
			// los carbonos anteriores sí pueden estar en un radical gracias a los siguientes 'break'

			if(carbons.get(i).getFreeBondCount() > 0)
				break; // Le sigue un alqueno o alquino

			if(substituents.size() > 0) // Hay sustituyentes distintos del hidrógeno
				if(!(i == 1 && substituents.size() == 1 && substituents.get(0).getCarbonCount() == 1))
					break; // Y estos NO son un solo metil en el segundo carbono (NO podría formar un radical 'iso')
		}
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

	public String getStructure() {
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
