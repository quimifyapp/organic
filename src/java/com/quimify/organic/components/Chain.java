package com.quimify.organic.components;

import com.quimify.organic.Organic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chain extends Organic {

	private final List<Carbon> carbons;

	// Constructores:

	public Chain() {
		carbons = new ArrayList<>();
	}

	public Chain(int previousBonds) {
		carbons = new ArrayList<>();
		carbons.add(new Carbon(previousBonds));
	}

	public Chain(Chain nueva) {
		carbons = new ArrayList<>();
		bondCopyOf(nueva);
	}

	public Chain(List<Carbon> nueva) {
		carbons = new ArrayList<>();
		bondCopyOf(nueva);
	}

	private void bondCopyOf(List<Carbon> carbons) {
		for(Carbon carbon : carbons)
			this.carbons.add(new Carbon(carbon));
	}

	private void bondCopyOf(Chain other) {
		bondCopyOf(other.carbons);
	}

	// Modificadores:

	public void bond(Substituent substituent) {
		getLastCarbon().bond(substituent);
	}

	public void bond(FunctionalGroup functionalGroup) {
		bond(new Substituent(functionalGroup));
	}

	public void bond(Substituent substituent, int times) {
		getLastCarbon().bond(substituent, times);
	}

	public void bond(FunctionalGroup functionalGroup, int times) {
		this.bond(new Substituent(functionalGroup), times);
	}

	public void bondCarbon() {
		if (getEnlacesLibres() > 0) {
			Carbon ultimo = getLastCarbon();
			ultimo.useBond();
			carbons.add(new Carbon(ultimo.getEnlacesLibres() + 1));
		} else throw new IllegalStateException("No se puede enlazar un carbono a [" + getStructure() + "].");
	}

	private void bondCarbons(List<Carbon> carbons) {
		getLastCarbon().useBond();
		bondCopyOf(carbons);
	}

	private void become(Chain other) {
		carbons.clear();
		bondCopyOf(other);
	}

	public void invertOrientation() {
		become(getInverseOriented());
	}

	public void correctChainStructureToTheLeft() { // CH2(CH3)-CH2- → CH3-CH2-CH2-
		boolean corrected; // Para actualizar el iterador tras iteración
		for (int i = 0; i < carbons.size(); i = corrected ? 0 : i + 1) { // Sin incremento
			if(carbons.get(i).getSubstituentsOf(FunctionalGroup.radical).size() > 0) { // Este carbono tiene radicales
				// Se obtiene el mayor radical de este carbono:
				Substituent mayor_radical = carbons.get(i).getGreatesRadical();

				// Se calcula si el "camino" por este radical es preferible a la cadena principal:
				int comparacion = Integer.compare(mayor_radical.getStraightCarbonCount(), i);

				if(comparacion == 1 || (comparacion == 0 && mayor_radical.isIso())) {
					// Se corrige la cadena por la izquierda:
					if(i != 0) {
						// Se convierte el camino antiguo de la cadena principal en radical:
						Substituent oldRadical;

						// Aquí se tiene en cuenta que, de haber un radical, solo podría ser metil
						if(i > 1 && carbons.get(1).isBondedTo(FunctionalGroup.radical) // Hay un metil en el segundo carbono
								&& carbons.get(1).getSubstituentsWithoutHydrogen().get(0).equals(Substituent.CH3))
							oldRadical = new Substituent(i + 1, true);
						else oldRadical = new Substituent(i);

						// Se enlaza tal radical:
						carbons.get(i).bond(oldRadical);

						// Se elimina el radical que será el camino de la cadena principal:
						carbons.get(i).removeWithBonds(mayor_radical);

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

			if(carbons.get(i).getEnlacesLibres() > 0)
				break; // Le sigue un alqueno o alquino

			if(substituents.size() > 0) // Hay sustituyentes distintos del hidrógeno
				if(!(i == 1 && substituents.size() == 1 && substituents.get(0).getCarbonCount() == 1))
					break; // Y estos NO son un solo metil en el segundo carbono (NO podría formar un radical 'iso')
		}
	}

	public void breakDownTerminalToKetoneWith(FunctionalGroup terminal, FunctionalGroup companion) {
		breakDownTerminalToKetoneWithIn(terminal, companion, carbons.get(0));
		breakDownTerminalToKetoneWithIn(terminal, companion, getLastCarbon());
	}

	private void breakDownTerminalToKetoneWithIn(FunctionalGroup terminal, FunctionalGroup companion, Carbon carbon) {
		if(carbon.isBondedTo(terminal)) {
			carbon.removeWithBonds(terminal); // C(A)- → C-
			carbon.bond(FunctionalGroup.ketone); // C- → C(O)-
			carbon.bond(companion); // C- → C(O)(B)-
		}
	}

	public void groupKetoneWithToTerminal(FunctionalGroup companion, FunctionalGroup terminal) {
		groupKetoneWithToTerminalIn(companion, terminal, carbons.get(0));
		groupKetoneWithToTerminalIn(companion, terminal, getLastCarbon());
	}

	private void groupKetoneWithToTerminalIn(FunctionalGroup companion, FunctionalGroup terminal, Carbon carbon) {
		if(carbon.isBondedTo(FunctionalGroup.ketone) && carbon.isBondedTo(companion)) {
			carbon.removeWithBonds(FunctionalGroup.ketone); // C(O)(A)- → C(A)-
			carbon.removeWithBonds(companion); // C(A)- → C-
			carbon.bond(terminal);// C- → C(B)-
		}
	}

	public void moveOutWithAs(FunctionalGroup terminal, FunctionalGroup substitute) {
		if(terminal != getProrityFunctionalGroup()) {
			if(carbons.size() > 1)
				moveOutWithAsIn(terminal, substitute, carbons.get(0), carbons.get(1));
			if(carbons.size() > 1) // Might have changed
				moveOutWithAsIn(terminal, substitute, getLastCarbon(), carbons.get(carbons.size() - 2));
		}
	}

	private void moveOutWithAsIn(FunctionalGroup terminal, FunctionalGroup substitute, Carbon ending, Carbon before) {
		if (ending.isBondedTo(terminal)) {
			carbons.remove(ending); // C(A)-C(X)- → C(X)=
			before.freeBond(); // C(X)= → C(X)≡
			before.bond(substitute); // C(X)≡ → C(CA)(X)-
		}
	}

	// QUERIES -----------------------------------------------------------------------

	public int getSize() {
		return carbons.size();
	}

	public boolean isDone() {
		return getEnlacesLibres() == 0;
	}

	public boolean hasFunctionalGroup(FunctionalGroup functionalGroup) {
		for(Carbon carbon : carbons)
			if(carbon.isBondedTo(functionalGroup))
				return true;

		return false;
	}

	@Override
	public boolean equals(Object other) {
		boolean isEqual = false;

		if(other != null && other.getClass() == this.getClass()) {
			Chain otherChain = (Chain) other;

			if(carbons.size() == otherChain.getSize())
				for(int i = 0; i < carbons.size(); i++)
					if(carbons.get(i).equals(otherChain.carbons.get(i))) {
						isEqual = true;
						break;
					}
		}

		return isEqual;
	}

	public Chain getInverseOriented() {
		Chain inversa = new Chain(carbons);

		// Le da la vuelta a los carbonos:
		Collections.reverse(inversa.carbons);

		// Ajusta los enlaces (no son simétricos):
		if(inversa.getSize() > 1) {
			for(int i = 0, j = carbons.size() - 2; i < inversa.getSize() - 1; i++)
				inversa.carbons.get(i).setEnlacesLibres(carbons.get(j--).getEnlacesLibres());

			inversa.carbons.get(inversa.getSize() - 1).setEnlacesLibres(0); // Se supone que no tiene enlaces sueltos
		}

		return inversa;
	}

	private Carbon getLastCarbon() {
		return carbons.get(carbons.size() - 1);
	}

	public int getEnlacesLibres() {
		return getLastCarbon().getEnlacesLibres();
	}

	public int getAmountOf(FunctionalGroup functionalGroup) {
		int amount = 0;

		for(Carbon carbon : carbons)
			amount += carbon.getCantidadDe(functionalGroup);

		return amount;
	}

	public FunctionalGroup getProrityFunctionalGroup() { // Con hidrógeno
		for(FunctionalGroup functionalGroup : FunctionalGroup.values()) // Todas las funciones recogidas en Id
			for(Carbon carbon : carbons)
				if(carbon.isBondedTo(functionalGroup))
					return functionalGroup;

		return null;
	}

	public List<FunctionalGroup> getOrderedGroupsWithoutHydrogenNorEther() { // Sin hidrógeno ni éter
		List<FunctionalGroup> funciones = new ArrayList<>(); // Funciones presentes sin repetición y en orden

		for(FunctionalGroup functionalGroup : FunctionalGroup.values()) // Todas las funciones recogidas en Id
			if(functionalGroup != FunctionalGroup.hydrogen && functionalGroup != FunctionalGroup.ether) // Excepto hidrógeno y éter
				for(Carbon carbon : carbons)
					if(carbon.isBondedTo(functionalGroup)) {
						funciones.add(functionalGroup);
						break;
					}

		return funciones;
	}

	public List<Integer> getIndexesOfAll(FunctionalGroup functionalGroup) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos con la función

		for(int i = 0; i < carbons.size(); i++) {
			int cantidad = carbons.get(i).getCantidadDe(functionalGroup);

			for(int j = 0; j < cantidad; j++)
				posiciones.add(i);
		}

		return posiciones;
	}

	public List<Integer> getIndexesOfAll(Substituent substituent) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos enlazados al sustituyente

		for(int i = 0; i < carbons.size(); i++) {
			int cantidad = carbons.get(i).getCantidadDe(substituent);

			for(int j = 0; j < cantidad; j++)
				posiciones.add(i);
		}

		return posiciones;
	}

	public List<Substituent> getRadicalSubstituents() {
		List<Substituent> substituents = new ArrayList<>();

		for(Carbon carbon : carbons)
			substituents.addAll(carbon.getSubstituentsOf(FunctionalGroup.radical));

		return substituents;
	}

	public List<Substituent> getUniqueRadicals() {
		List<Substituent> unicos = new ArrayList<>();

		for(Carbon carbon : carbons)
			for(Substituent substituent : carbon.getSubstituentsOf(FunctionalGroup.radical))
				if(!unicos.contains(substituent))
					unicos.add(substituent);

		return unicos;
	}

	public List<Substituent> getSubstituentsWithoutHydrogen() {
		List<Substituent> substituentsWithoutHydrogen = new ArrayList<>();

		for(Carbon carbon : carbons)
			substituentsWithoutHydrogen.addAll(carbon.getSubstituentsWithoutHydrogen());

		return substituentsWithoutHydrogen;
	}
	
	public boolean hasMethylAt(int index) {
		return getSize() > index && carbons.get(getSize() - index - 1).isBondedTo(Substituent.CH3);
	}

	// Texto:

	public String getStructure() {
		StringBuilder formula = new StringBuilder();

		if(carbons.size() > 0) {
			// Se escribe el primero:
			Carbon firstCarbon = carbons.get(0);
			formula.append(firstCarbon); // Como CH

			// Se escribe el resto con los enlaces libres del anterior:
			int previousFreeBonds = firstCarbon.getEnlacesLibres();
			for(int i = 1; i < carbons.size(); i++) {
				formula.append(getBondSymbol(previousFreeBonds)); // Como CH=
				formula.append(carbons.get(i)); // Como CH=CH

				previousFreeBonds = carbons.get(i).getEnlacesLibres();
			}

			// Se escribe los enlaces libres del último:
			if(previousFreeBonds > 0 && previousFreeBonds < 4) // Ni está completo ni es el primero vacío
				formula.append(getBondSymbol(previousFreeBonds - 1)); // Como CH=CH-CH2-C≡
		}

		return formula.toString();
	}

	@Override
	public String toString() {
		return getStructure();
	}

}
