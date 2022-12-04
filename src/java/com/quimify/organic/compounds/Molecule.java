package com.quimify.organic.compounds;

import com.quimify.organic.Organic;
import com.quimify.organic.components.Atom;
import com.quimify.organic.components.Element;
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
		for(int i = 0; i < xmlBonds.getLength(); i++) {
			org.w3c.dom.Element enlace = (org.w3c.dom.Element) xmlBonds.item(i);

			String[] id_string = enlace.getAttribute("id").replace("a", "").split("_");

			Integer[] id_int = {
					Integer.valueOf(id_string[0]),
					Integer.valueOf(id_string[1])
			};

			Atom[] atoms = {
					molecule.stream().filter(atom -> atom.getId().equals(id_int[0])).findAny()
							.orElseThrow(NoSuchElementException::new),
					molecule.stream().filter(atom -> atom.getId().equals(id_int[1])).findAny()
							.orElseThrow(NoSuchElementException::new)
			};

			atoms[0].bond(atoms[1]);
			atoms[1].bond(atoms[0]);
		}
	}

	public Optional<String> getStructure() {
		if(isSimpleOpenChain())
			return Optional.of("It's simple!");

		return Optional.empty();
	}

	// Private methods:

	private boolean isSimpleOpenChain() {
		if(isOpenChain())
			return getExtremeCarbons().stream().anyMatch(this::isSimpleCarbon);
		else return false;
	}

	private boolean isOpenChain() {
		return !isCycle(); // By definition
	}

	private boolean isCycle() {
		return smiles.matches(".*[0-9].*"); // SMILES uses digits only for cycles
	}

	private List<Atom> getExtremeCarbons() {
		List<Atom> carbons = getCarbons();
		return carbons.stream().filter(carbon -> carbon.getBonded(Element.C).size() == 1).collect(Collectors.toList());
	}

	private List<Atom> getCarbons() {
		return molecule.stream().filter(atom -> atom.getElement() == Element.C).collect(Collectors.toList());
	}

	private boolean isSimpleCarbon(Atom carbon) {
		boolean isSimpleCarbon;

		Set<Atom> nonSubstituentBondedAtoms = new HashSet<>();
		for(Atom bondedAtom : carbon.getBondedAtomsCutOff()) {
			Atom anonymousBondedAtom = bondedAtom.toAnonymous();

			if (Simple.bondableAtoms.stream().noneMatch(anonymousBondedAtom::equals))
				if(!(bondedAtom.isElement(Element.C) && isRadicalCarbon(bondedAtom)))
					nonSubstituentBondedAtoms.add(bondedAtom);
		}

		if(nonSubstituentBondedAtoms.stream().allMatch(bondedAtom -> bondedAtom.isElement(Element.C))) {
			if (nonSubstituentBondedAtoms.size() == 1)
				isSimpleCarbon = isSimpleCarbon(nonSubstituentBondedAtoms.stream().findAny().get());
			else isSimpleCarbon = nonSubstituentBondedAtoms.size() == 0;
		}
		else isSimpleCarbon = false; // There are bonded atoms that are not substituents nor carbons

		return isSimpleCarbon;
	}

	private boolean isRadicalCarbon(Atom carbon) {
		boolean isRadicalCarbon;

		// It must be one of the following: CH2-C..., CH3, CH(CH3)2
		if(carbon.getBondedAtoms().size() == 3) {
			switch (carbon.getBonded(Element.H).size()) {
				case 3:
					isRadicalCarbon = true; // CH3
					break;
				case 2:
					Stream<Atom> bondedCarbons = carbon.getBondedAtomsCutOff().stream()
							.filter(bondedAtom -> bondedAtom.isElement(Element.C));

					isRadicalCarbon = carbon.getBonded(Element.C).size() == 1
							&& bondedCarbons.allMatch(this::isRadicalCarbon); // CH2-C...
					break;
				case 1:
					Stream<Atom> bondedCH3 = carbon.getBondedAtomsCutOff().stream().filter(bondedAtom ->
							bondedAtom.getBondedAtoms().size() == 3 && bondedAtom.getBonded(Element.H).size() == 3);

					isRadicalCarbon = bondedCH3.count() == 2; // CH(CH3)2
					break;
				default:
					isRadicalCarbon = false; // No hydrogen
			}
		}
		else isRadicalCarbon = false;

		return isRadicalCarbon;
	}

}
