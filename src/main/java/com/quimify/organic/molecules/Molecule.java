package com.quimify.organic.molecules;

import com.quimify.organic.Organic;
import com.quimify.organic.components.*;
import com.quimify.organic.molecules.open_chain.Ether;
import com.quimify.organic.molecules.open_chain.Simple;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Esta clase representa una molécula cualquiera a partir de un CML en formato XML para intentar redactarle una fórmula.

// Un compuesto genérico, químicamente hablando, podría ser de otro tipo ya contemplado en este programa (simple, éter,
// éster, cíclico...), pero también podría no encajar en ninguno de esos tipos.

public class Molecule extends Organic {

	private final String smiles;
	private final Set<Atom> molecule;

	// Constructor:

	public Molecule(String cml, String smiles) throws ParserConfigurationException, IOException, SAXException {
		molecule = new HashSet<>();
		this.smiles = smiles;

		// Se procesa el Chemical Markup Language:
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xml = documentBuilder.parse(new InputSource(new StringReader(cml)));

		// Se recogen los átomos:
		NodeList xmlAtoms = xml.getElementsByTagName("atom");
		for(int i = 0; i < xmlAtoms.getLength(); i++) {
			org.w3c.dom.Element atom = (org.w3c.dom.Element) xmlAtoms.item(i);

			int id = Integer.parseInt(atom.getAttribute("id").replace("a", ""));
			String elementType = atom.getAttribute("elementType");

			molecule.add(new Atom(id, elementType));
		}

		// Se enlazan entre sí:
		NodeList xmlBonds = xml.getElementsByTagName("bond");
		for(int i = 0; i < xmlBonds.getLength(); i++) {
			org.w3c.dom.Element bond = (org.w3c.dom.Element) xmlBonds.item(i);

			int[] ids = Arrays.stream(bond.getAttribute("id").replace("a", "").split("_"))
					.mapToInt(Integer::valueOf).toArray();

			Atom[] atoms = {
					molecule.stream().filter(atom -> atom.getId().equals(ids[0])).findAny()
							.orElseThrow(NoSuchElementException::new),
					molecule.stream().filter(atom -> atom.getId().equals(ids[1])).findAny()
							.orElseThrow(NoSuchElementException::new)
			};

			atoms[0].bond(atoms[1]);
			atoms[1].bond(atoms[0]);
		}
	}

	// Text

	public Optional<String> getStructure() {
		Optional<String> structure;

		if(isOpenChain()) {
			structure = getSimpleStructure();

			if(structure.isEmpty())
				structure = getEtherStructure();
		}
		else structure = Optional.empty();

		return structure;
	}

	// Private methods:

	private boolean isCycle() {
		return smiles.matches(".*[0-9].*"); // SMILES uses digits only for cycles
	}

	private boolean isOpenChain() {
		return !isCycle(); // By definition
	}

	private List<Atom> getCarbons() {
		return molecule.stream().filter(atom -> atom.getElement() == Element.C).collect(Collectors.toList());
	}

	private List<Atom> getEndingCarbons() {
		List<Atom> carbons = getCarbons();
		return carbons.stream().filter(carbon -> carbon.getBonded(Element.C).size() < 2).collect(Collectors.toList());
	}

	// Simple structure:

	private Optional<String> getSimpleStructure() {
		Optional<String> simpleStructure;

		Optional<Atom> endingCarbon = getSimpleEndingCarbon();
		if(endingCarbon.isPresent()) {
			Simple simple = new Simple();
			buildSimpleFrom(simple, endingCarbon.get());
			simple.correct();

			simpleStructure = Optional.of(simple.getStructure());
		}
		else simpleStructure = Optional.empty();

		return simpleStructure;
	}

	private Optional<Atom> getSimpleEndingCarbon() {
		return getEndingCarbons().stream().filter(this::isSimpleCarbon).findAny();
	}

	private boolean isSimpleCarbon(Atom carbon) {
		Set<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtomsCutOff().stream().filter(bondedAtom ->
				isNotSubstituent(bondedAtom, Simple.bondableAtoms)).collect(Collectors.toSet());

		if(nonSubstituentBondedAtoms.size() == 1) {
			Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();
			return nonSubstituent.getElement() == Element.C && isSimpleCarbon(nonSubstituent); // Recursion
		}

		return nonSubstituentBondedAtoms.size() == 0;
	}

	private void buildSimpleFrom(Simple simple, Atom simpleCarbon) {
		Optional<Atom> nextCarbon = Optional.empty();

		for(Atom bondedAtom : simpleCarbon.getBondedAtomsCutOff()) {
			if (isBondableAtom(bondedAtom, Simple.bondableAtoms))
				simple.bond(bondedAtom.toFunctionalGroup());
			else if(isRadicalCarbon(bondedAtom))
				simple.bond(buildRadicalFrom(bondedAtom));
			else nextCarbon = Optional.of(bondedAtom);
		}

		if(nextCarbon.isPresent()) {
			simple.bondCarbon();
			buildSimpleFrom(simple, nextCarbon.get()); // Recursion
		}
	}

	// Ether structure:

	private Optional<String> getEtherStructure() {
		Optional<String> etherStructure;

		Optional<Atom> endingCarbon = getEtherEndingCarbon();
		if(endingCarbon.isPresent()) {
			Ether ether = new Ether();
			buildEtherFrom(ether, endingCarbon.get());
			ether.correct();

			etherStructure = Optional.of(ether.getStructure());
		}
		else etherStructure = Optional.empty();

		return etherStructure;
	}

	private Optional<Atom> getEtherEndingCarbon() {
		List<Atom> endingCarbons = getEndingCarbons();

		if(endingCarbons.size() > 2) // -C-O-C- minimum
			endingCarbons = endingCarbons.stream().filter(endingCarbon ->
					endingCarbon.getBondedAtomsCutOff().stream().noneMatch(bonded ->
							bonded.getElement() == Element.O)).collect(Collectors.toList());

		return endingCarbons.stream().filter(endingCarbon -> isEtherCarbon(endingCarbon, false)).findAny();
	}

	private boolean isEtherCarbon(Atom carbon, boolean hasFoundEther) {
		boolean ether;

		Set<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtomsCutOff().stream().filter(bondedAtom ->
				isNotSubstituent(bondedAtom, Ether.bondableAtoms)).collect(Collectors.toSet());

		if (nonSubstituentBondedAtoms.size() == 1) {
			Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();

			if (nonSubstituent.getElement() == Element.C)
				ether = isEtherCarbon(nonSubstituent, hasFoundEther); // Recursion
			else if (nonSubstituent.getElement() == Element.O)
				ether = !hasFoundEther && isEtherCarbon(nonSubstituent, true); // Recursion
			else ether = false;
		} else ether = nonSubstituentBondedAtoms.size() == 0;

		return ether;
	}

	private void buildEtherFrom(Ether ether, Atom etherCarbon) {
		Optional<Atom> nextAtom = Optional.empty();

		for(Atom bondedAtom : etherCarbon.getBondedAtomsCutOff()) {
			if (isBondableAtom(bondedAtom, Ether.bondableAtoms))
				ether.bond(bondedAtom.toFunctionalGroup());
			else if(isRadicalCarbon(bondedAtom))
				ether.bond(buildRadicalFrom(bondedAtom));
			else nextAtom = Optional.of(bondedAtom);
		}

		if(nextAtom.isPresent()) {
			if(nextAtom.get().getElement() == Element.O)  {
				ether.bond(Group.ether);
				nextAtom = Optional.of(nextAtom.get().getBondedAtomsCutOff().get(0));
			}
			else ether.bondCarbon(); // It's a carbon

			buildEtherFrom(ether, nextAtom.get()); // Recursion
		}
	}

	// Simple or ether structure:

	private boolean isNotSubstituent(Atom atom, Set<Atom> bondableAtoms) {
		return !isBondableAtom(atom, bondableAtoms) && !isRadicalCarbon(atom);
	}

	private boolean isBondableAtom(Atom atom, Set<Atom> bondableAtoms) {
		return bondableAtoms.stream().anyMatch(atom.toAnonymous()::equals);
	}

	private boolean isRadicalCarbon(Atom atom) { // TODO more readable
		if(atom.getElement() != Element.C)
			return false;

		boolean radical;

		// It must be one of the following: CH2-C..., CH3, CH(CH3)2
		List<Atom> bondedAtomsCutOff = atom.getBondedAtomsCutOff();
		if(bondedAtomsCutOff.size() == 3) {
			switch (atom.getBonded(Element.H).size()) {
				case 3:
					radical = true; // CH3
					break;
				case 2:
					Stream<Atom> bondedCarbons = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.getElement() == Element.C);

					radical = atom.getBonded(Element.C).size() == 1 // CH2-C...
							&& bondedCarbons.allMatch(this::isRadicalCarbon); // CH2-CH2-C... (recursive)
					break;
				case 1:
					Stream<Atom> bondedCH3s = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.getBondedAtoms().size() == 3 && bondedAtom.getBonded(Element.H).size() == 3);

					radical = bondedCH3s.count() == 2; // CH(CH3)2
					break;
				default:
					radical = false; // No hydrogen
			}
		}
		else radical = false;

		return radical;
	}

	private Substituent buildRadicalFrom(Atom radicalCarbon) {
		Substituent radical;

		switch (radicalCarbon.getBonded(Element.H).size()) {
			case 3: // -CH3
				radical = new Substituent(1);
				break;
			case 2: // -CH2-
				Atom nextCarbon = radicalCarbon.toAnonymous().getBonded(Element.C).get(0); // There must be one
				Substituent radicalEnd = buildRadicalFrom(nextCarbon); // Recursive
				radical = new Substituent(1 + radicalEnd.getCarbonCount(), radicalEnd.isIso()); // Appended
				break;
			case 1: // -CH(CH3)2
				radical = new Substituent(3, true);
				break;
			default:
				radical = null;
				break;
		}

		return radical;
	}

}
