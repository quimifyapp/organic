package com.quimify.organic.compounds;

import com.quimify.organic.Organic;
import com.quimify.organic.components.*;
import com.quimify.organic.compounds.open_chain.Simple;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Esta clase representa una molécula cualquiera a partir de un CML en formato XML para intentar redactarle una fórmula.

// Un compuesto genérico, químicamente hablando, podría ser de otro tipo ya contemplado en este programa (simple, éter,
// éster, cíclico...), pero también podría no encajar en ninguno de esos tipos.

public class Molecule extends Organic {

	private static final Logger logger = Logger.getLogger(Molecule.class.getName());

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
		for(int i = 0; i < xmlBonds.getLength(); i++) { // TODO for each
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

	public Optional<String> getStructure() {
		if(isOpenChain()) {
			Optional<Atom> simpleEndingCarbon =  getSimpleEndingCarbon();

			if(simpleEndingCarbon.isPresent()) {
				Simple simple = new Simple();
				buildSimple(simpleEndingCarbon.get(), simple);
				simple.correct();
				return Optional.of(simple.getStructure());
			}
		}

		return Optional.empty();
	}

	// Private methods:

	private boolean isOpenChain() {
		return !isCycle(); // By definition
	}

	private boolean isCycle() {
		return smiles.matches(".*[0-9].*"); // SMILES uses digits only for cycles
	}

	private Optional<Atom> getSimpleEndingCarbon() {
		return getEndingCarbons().stream().filter(this::isSimpleCarbon).findAny();
	}

	private List<Atom> getEndingCarbons() {
		List<Atom> carbons = getCarbons();
		return carbons.stream().filter(carbon -> carbon.getBonded(Element.C).size() < 2).collect(Collectors.toList());
	}

	private List<Atom> getCarbons() {
		return molecule.stream().filter(atom -> atom.getElement() == Element.C).collect(Collectors.toList());
	}

	private boolean isSimpleCarbon(Atom carbon) {
		boolean isSimpleCarbon;

		Set<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtomsCutOff().stream().filter(bondedAtom ->
				!isSubstituent(bondedAtom, Simple.bondableAtoms)).collect(Collectors.toSet());

		if(nonSubstituentBondedAtoms.size() == 1) {
			Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();
			isSimpleCarbon = nonSubstituent.isElement(Element.C) && isSimpleCarbon(nonSubstituent); // Recursion
		}
		else isSimpleCarbon = nonSubstituentBondedAtoms.size() == 0;

		return isSimpleCarbon;
	}

	private boolean isSubstituent(Atom atom, Set<Atom> bondableAtoms) {
		return isBondableAtom(atom, bondableAtoms) || isRadicalCarbon(atom);
	}

	private boolean isBondableAtom(Atom atom, Set<Atom> bondableAtoms) {
		return bondableAtoms.stream().anyMatch(atom.toAnonymous()::equals);
	}

	private boolean isRadicalCarbon(Atom atom) {
		if(!atom.isElement(Element.C))
			return false;

		boolean isRadicalCarbon;

		// It must be one of the following: CH2-C..., CH3, CH(CH3)2
		List<Atom> bondedAtomsCutOff = atom.getBondedAtomsCutOff();
		if(bondedAtomsCutOff.size() == 3) {
			switch (atom.getBonded(Element.H).size()) {
				case 3:
					isRadicalCarbon = true; // CH3
					break;
				case 2:
					Stream<Atom> bondedCarbons = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.isElement(Element.C));

					isRadicalCarbon = atom.getBonded(Element.C).size() == 1 // CH2-C...
							&& bondedCarbons.allMatch(this::isRadicalCarbon); // CH2-CH2-C... (recursive)
					break;
				case 1:
					Stream<Atom> bondedCH3s = bondedAtomsCutOff.stream().filter(bondedAtom ->
							bondedAtom.getBondedAtoms().size() == 3 && bondedAtom.getBonded(Element.H).size() == 3);

					isRadicalCarbon = bondedCH3s.count() == 2; // CH(CH3)2
					break;
				default:
					isRadicalCarbon = false; // No hydrogen
			}
		}
		else isRadicalCarbon = false;

		return isRadicalCarbon;
	}

	private void buildSimple(Atom carbon, Simple simple) {
		Optional<Atom> nextCarbon = Optional.empty();
		for(Atom bondedAtom : carbon.getBondedAtomsCutOff()) {
			if (isBondableAtom(bondedAtom, Simple.bondableAtoms))
				simple.bond(bondedAtom.toFunctionalGroup());
			else if(isRadicalCarbon(bondedAtom))
				simple.bond(buildRadical(bondedAtom));
			else nextCarbon = Optional.of(bondedAtom);
		}

		if(nextCarbon.isPresent()) {
			simple.bondCarbon();
			buildSimple(nextCarbon.get(), simple);
		}
	}

	private Substituent buildRadical(Atom carbon) {
		Substituent radical;

		switch (carbon.getBonded(Element.H).size()) {
			case 3: // -CH3
				radical = new Substituent(1);
				break;
			case 2: // -CH2-
				Atom nextCarbon = carbon.toAnonymous().getBonded(Element.C).get(0); // There must be one
				Substituent radicalEnd = buildRadical(nextCarbon); // Recursive
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
