package com.quimify.organic.molecules;

import com.quimify.organic.Nomenclature;
import com.quimify.organic.components.*;
import com.quimify.organic.molecules.open_chain.Ether;
import com.quimify.organic.molecules.open_chain.OpenChain;
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

// This class represents any molecule described by a CML to try to convert it into recognized molecule types.

public class Molecule extends Nomenclature {

	private final String smiles;
	private final Set<Atom> molecule;

	// Constructor:

	public Molecule(String cml, String smiles) throws ParserConfigurationException, IOException, SAXException {
		this.molecule = new HashSet<>();
		this.smiles = smiles;

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document cmlXML = documentBuilder.parse(new InputSource(new StringReader(cml)));

		collectAtoms(cmlXML);
		bondAtoms(cmlXML);
	}

	private void collectAtoms(Document cmlXML) {
		NodeList atomsXML = cmlXML.getElementsByTagName("atom");

		for(int i = 0; i < atomsXML.getLength(); i++) {
			org.w3c.dom.Element atomXML = (org.w3c.dom.Element) atomsXML.item(i);

			int id = Integer.parseInt(atomXML.getAttribute("id").replace("a", ""));
			String elementType = atomXML.getAttribute("elementType");

			molecule.add(new Atom(id, elementType));
		}
	}

	private void bondAtoms(Document cmlXML) {
		NodeList bondsXML = cmlXML.getElementsByTagName("bond");

		for (int i = 0; i < bondsXML.getLength(); i++) {
			org.w3c.dom.Element bondXML = (org.w3c.dom.Element) bondsXML.item(i);

			String[] idsAsStrings = bondXML.getAttribute("id").replace("a", "").split("_");
			int[] ids = Arrays.stream(idsAsStrings).mapToInt(Integer::valueOf).toArray();

			Stream<Atom> identified = molecule.stream().filter(atom -> List.of(ids[0], ids[1]).contains(atom.getId()));
			Atom[] atoms = identified.toArray(Atom[]::new);

			atoms[0].bond(atoms[1]);
			atoms[1].bond(atoms[0]);
		}
	}

	// Queries:

	public Optional<OpenChain> toOpenChain() {
		if(!isOpenChain())
			return Optional.empty();

		Optional<OpenChain> openChain = getSimpleOpenChain();

		if(openChain.isEmpty())
			openChain = getEtherOpenChain();

		return openChain;
	}

	// Private:

	private boolean isCycle() {
		return smiles.matches(".*[0-9].*"); // SMILES uses digits only for cycles
	}

	private boolean isOpenChain() {
		return !isCycle(); // By definition of an open chain
	}

	private List<Atom> getCarbons() {
		return molecule.stream().filter(atom ->
				atom.getElement() == Element.C)
				.collect(Collectors.toList());
	}

	private List<Atom> getEndingCarbons() {
		return getCarbons().stream().filter(carbon -> carbon.getAmountOf(Element.C) < 2).collect(Collectors.toList());
	}

	// Simple open chain:

	private Optional<OpenChain> getSimpleOpenChain() {
		Optional<Atom> simpleEndingCarbon = getSimpleEndingCarbon();

		if(simpleEndingCarbon.isEmpty())
			return Optional.empty();

		Simple simple = new Simple();
		buildSimpleFrom(simple, simpleEndingCarbon.get());
		simple.correct();

		return Optional.of(simple);
	}

	private Optional<Atom> getSimpleEndingCarbon() {
		return getEndingCarbons().stream().filter(this::isSimpleCarbon).findAny();
	}

	private boolean isSimpleCarbon(Atom carbon) {
		Set<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtomsSeparated().stream().filter(bondedAtom ->
				isNotSubstituent(bondedAtom, Simple.bondableAtoms)).collect(Collectors.toSet());

		if(nonSubstituentBondedAtoms.size() == 1) {
			Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();
			return nonSubstituent.getElement() == Element.C && isSimpleCarbon(nonSubstituent); // Recursion
		}

		return nonSubstituentBondedAtoms.size() == 0;
	}

	private void buildSimpleFrom(Simple simple, Atom simpleCarbon) { // TODO runtime exceptions
		Optional<Atom> nextCarbon = Optional.empty();

		for(Atom bondedAtom : simpleCarbon.getBondedAtomsSeparated()) {
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

	// Ether open chain:

	private Optional<OpenChain> getEtherOpenChain() {
		Optional<Atom> etherEndingCarbon = getEtherEndingCarbon();

		if (etherEndingCarbon.isEmpty())
			return Optional.empty();

		Ether ether = new Ether();
		buildEtherFrom(ether, etherEndingCarbon.get());
		ether.correct();

		return Optional.of(ether);
	}

	private Optional<Atom> getEtherEndingCarbon() {
		List<Atom> endingCarbons = getEndingCarbons();

		if(endingCarbons.size() > 2) // -C-O-C- minimum
			endingCarbons = endingCarbons.stream().filter(endingCarbon ->
					endingCarbon.getBondedAtomsSeparated().stream().noneMatch(bonded ->
							bonded.getElement() == Element.O)).collect(Collectors.toList());

		return endingCarbons.stream().filter(endingCarbon -> isEtherCarbon(endingCarbon, false)).findAny();
	}

	private boolean isEtherCarbon(Atom carbon, boolean hasFoundEther) {
		boolean ether;

		Set<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtomsSeparated().stream().filter(bondedAtom ->
				isNotSubstituent(bondedAtom, Ether.bondableAtoms)).collect(Collectors.toSet());

		if (nonSubstituentBondedAtoms.size() == 1) {
			Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();

			if (nonSubstituent.getElement() == Element.C)
				ether = isEtherCarbon(nonSubstituent, hasFoundEther); // Recursion
			else if (nonSubstituent.getElement() == Element.O)
				ether = !hasFoundEther && isEtherCarbon(nonSubstituent, true); // Recursion
			else ether = false;
		}
		else ether = nonSubstituentBondedAtoms.size() == 0;

		return ether;
	}

	private void buildEtherFrom(Ether ether, Atom etherCarbon) { // TODO runtime exceptions
		Optional<Atom> nextAtom = Optional.empty();

		for(Atom bondedAtom : etherCarbon.getBondedAtomsSeparated()) {
			if (isBondableAtom(bondedAtom, Ether.bondableAtoms))
				ether.bond(bondedAtom.toFunctionalGroup());
			else if(isRadicalCarbon(bondedAtom))
				ether.bond(buildRadicalFrom(bondedAtom));
			else nextAtom = Optional.of(bondedAtom);
		}

		if(nextAtom.isPresent()) {
			if(nextAtom.get().getElement() == Element.O)  {
				ether.bond(Group.ether);
				nextAtom = Optional.of(nextAtom.get().getBondedAtomsSeparated().get(0));
			}
			else ether.bondCarbon(); // It's a carbon

			buildEtherFrom(ether, nextAtom.get()); // Recursion
		}
	}

	// Open chain:

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
		List<Atom> bondedAtomsCutOff = atom.getBondedAtomsSeparated();
		if(bondedAtomsCutOff.size() == 3) {
			switch (atom.getAmountOf(Element.H)) {
				case 3:
					radical = true; // CH3
					break;
				case 2:
					Stream<Atom> bondedCarbons = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.getElement() == Element.C);

					radical = atom.getAmountOf(Element.C) == 1 // CH2-C...
							&& bondedCarbons.allMatch(this::isRadicalCarbon); // CH2-CH2-C... (recursive)
					break;
				case 1:
					Stream<Atom> bondedCH3s = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.getBondedAtoms().size() == 3 && bondedAtom.getAmountOf(Element.H) == 3);

					radical = bondedCH3s.count() == 2; // CH(CH3)2
					break;
				default:
					radical = false; // No hydrogen
			}
		}
		else radical = false;

		return radical;
	}

	private Substituent buildRadicalFrom(Atom radicalCarbon) { // TODO runtime exceptions
		Substituent radical;

		switch (radicalCarbon.getAmountOf(Element.H)) {
			case 3: // -CH3
				radical = new Substituent(1);
				break;
			case 2: // -CH2-
				List<Atom> bondedCarbons = radicalCarbon.getBondedAtomsSeparated();
				bondedCarbons.removeIf(bondedAtom -> bondedAtom.getElement() != Element.C);
				Atom nextCarbon = bondedCarbons.get(0); // There must be one
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
