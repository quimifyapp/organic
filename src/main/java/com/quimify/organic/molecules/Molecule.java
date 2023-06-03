package com.quimify.organic.molecules;

import com.quimify.organic.components.*;
import com.quimify.organic.molecules.openchain.Ether;
import com.quimify.organic.molecules.openchain.OpenChain;
import com.quimify.organic.molecules.openchain.Simple;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// This class builds molecules from serialized data to convert them into known molecule types.

public class Molecule {

    // TODO MAKE ALL POSSIBLE METHODS IN THIS PROJECT STATIC

    private final List<Atom> atoms;

    // Constants:

    private static final Map<Atom, Group> atomToGroup = Map.of(
            Atom.H, Group.hydrogen,
            Atom.N, Group.nitrile,
            Atom.O, Group.ketone,
            Atom.OH, Group.alcohol,
            Atom.NH2, Group.amine,
            Atom.NO2, Group.nitro,
            Atom.Br, Group.bromine,
            Atom.Cl, Group.chlorine,
            Atom.F, Group.fluorine,
            Atom.I, Group.iodine
    );

    // Error messages:

    private static final String unknownFunctionalGroupError = "Unknown functional group of atom with element: %s.";

    // Constructor:

    private Molecule(List<Atom> atoms) {
        this.atoms = atoms;
    }

    public static Optional<Molecule> from(String cml, String smiles) throws Exception {
        if (smiles.matches(".*[0-9].*"))
            return Optional.empty(); // SMILES uses digits only for cycles

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document cmlDocument = documentBuilder.parse(new InputSource(new StringReader(cml)));

        Optional<Map<Integer, Atom>> idToAtom = collectAtoms(cmlDocument);

        if (idToAtom.isEmpty())
            return Optional.empty(); // There were strictly inorganic atoms

        List<Atom> atoms = bondAtoms(idToAtom.get(), cmlDocument);

        return Optional.of(new Molecule(atoms));
    }

    private static Optional<Map<Integer, Atom>> collectAtoms(Document cmlDocument) {
        Map<Integer, Atom> idToAtom = new HashMap<>();

        NodeList cmlAtoms = cmlDocument.getElementsByTagName("atom");
        for (int i = 0; i < cmlAtoms.getLength(); i++) {
            org.w3c.dom.Element cmlAtom = (org.w3c.dom.Element) cmlAtoms.item(i);

            int id = Integer.parseInt(cmlAtom.getAttribute("id").replace("a", ""));
            String symbol = cmlAtom.getAttribute("elementType");

            Element element;
            try {
                element = Element.valueOf(symbol);
            } catch (IllegalArgumentException ignore) {
                return Optional.empty(); // Probably not an organic symbol
            }

            idToAtom.put(id, new Atom(element));
        }

        return Optional.of(idToAtom);
    }

    private static List<Atom> bondAtoms(Map<Integer, Atom> idToAtom, Document cmlXML) {
        NodeList bondsXML = cmlXML.getElementsByTagName("bond");

        for (int i = 0; i < bondsXML.getLength(); i++) {
            org.w3c.dom.Element bondXML = (org.w3c.dom.Element) bondsXML.item(i);

            String[] idsAsStrings = bondXML.getAttribute("id").replace("a", "").split("_");

            int[] ids = {Integer.parseInt(idsAsStrings[0]), Integer.parseInt(idsAsStrings[1])};
            Atom[] atoms = {idToAtom.get(ids[0]), idToAtom.get(ids[1])};

            atoms[0].bond(atoms[1]);
            atoms[1].bond(atoms[0]);
        }

        return new ArrayList<>(idToAtom.values());
    }

    // Queries:

    public Optional<OpenChain> toOpenChain() {
        List<Atom> originCarbons = getOriginCarbons();

        Optional<Simple> simple = toSimple(originCarbons);

        if (simple.isPresent())
            return Optional.of(simple.get());

        Optional<Ether> ether = toEther(originCarbons);

        if (ether.isPresent())
            return Optional.of(ether.get());

        return Optional.empty();
    }

    // Private:

    private List<Atom> getOriginCarbons() {
        Stream<Atom> carbonTips = atoms.stream().filter(Molecule::isCarbonTip);
        return carbonTips.map(carbonTip -> asOrigin(carbonTip, null)).collect(Collectors.toList());
    }

    private static boolean isCarbonTip(Atom atom) {
        return atom.getElement() == Element.C && atom.getAmountOf(Element.C) < 2;
    }

    private static Atom asOrigin(Atom atom, Atom parent) {
        Atom origin = new Atom(atom.getElement());

        for (Atom bondedAtom : atom.getBondedAtoms())
            if (bondedAtom != parent)
                origin.bond(asOrigin(bondedAtom, atom));

        return origin;
    }

    // Simple open chain:

    private static Optional<Simple> toSimple(List<Atom> originCarbons) {
        Optional<Atom> simpleOriginCarbon = getSimpleOriginCarbon(originCarbons);

        if (simpleOriginCarbon.isEmpty())
            return Optional.empty();

        Simple simple = new Simple();
        buildSimpleFrom(simple, simpleOriginCarbon.get());
        simple.standardize();

        return Optional.of(simple);
    }

    private static Optional<Atom> getSimpleOriginCarbon(List<Atom> originCarbons) {
        return originCarbons.stream().filter(Molecule::isSimpleCarbon).findAny();
    }

    private static boolean isSimpleCarbon(Atom carbon) {
        boolean simpleCarbon;

        List<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtoms().stream().filter(bondedAtom ->
                isNotSubstituent(bondedAtom, Simple.bondableAtoms)).collect(Collectors.toList());

        if (nonSubstituentBondedAtoms.size() == 1) {
            Atom nonSubstituent = nonSubstituentBondedAtoms.get(0);

            if (nonSubstituent.getElement() == Element.C)
                simpleCarbon = isSimpleCarbon(nonSubstituent); // Recursive
            else simpleCarbon = false;
        }
        else simpleCarbon = nonSubstituentBondedAtoms.size() == 0;

        return simpleCarbon;
    }

    private static void buildSimpleFrom(Simple simple, Atom simpleCarbon) {
        Optional<Atom> nextCarbon = Optional.empty();

        for (Atom bondedAtom : simpleCarbon.getBondedAtoms()) {
            if (isBondableAtom(bondedAtom, Simple.bondableAtoms))
                simple.bond(asGroup(bondedAtom));
            else if (isRadicalCarbon(bondedAtom))
                simple.bond(buildRadicalFrom(bondedAtom));
            else nextCarbon = Optional.of(bondedAtom);
        }

        if (nextCarbon.isPresent()) {
            simple.bondCarbon();
            buildSimpleFrom(simple, nextCarbon.get()); // Recursive
        }
    }

    // Ether open chain:

    private static Optional<Ether> toEther(List<Atom> originCarbons) {
        Optional<Atom> etherOriginCarbon = getEtherOriginCarbon(originCarbons);

        if (etherOriginCarbon.isEmpty())
            return Optional.empty();

        Ether ether = new Ether();
        buildEtherFrom(ether, etherOriginCarbon.get());
        ether.standardize();

        return Optional.of(ether);
    }

    private static Optional<Atom> getEtherOriginCarbon(List<Atom> originCarbons) {
        if (originCarbons.size() > 2) // It's not just C-O-C
            originCarbons.removeIf(originCarbon -> originCarbon.getAmountOf(Element.O) == 0);

        return originCarbons.stream().filter(originCarbon -> isEtherCarbon(originCarbon, false)).findAny();
    }

    private static boolean isEtherCarbon(Atom carbon, boolean etherFound) {
        boolean etherCarbon;

        List<Atom> nonSubstituentBondedAtoms = carbon.getBondedAtoms().stream().filter(bondedAtom ->
                isNotSubstituent(bondedAtom, Ether.bondableAtoms)).collect(Collectors.toList());

        if (nonSubstituentBondedAtoms.size() == 1) {
            Atom nonSubstituent = nonSubstituentBondedAtoms.stream().findAny().get();

            if (nonSubstituent.getElement() == Element.C)
                etherCarbon = isEtherCarbon(nonSubstituent, etherFound); // Recursive
            else if (nonSubstituent.getElement() == Element.O && !etherFound)
                etherCarbon = isEtherCarbon(nonSubstituent, true); // Recursive
            else etherCarbon = false;
        }
        else etherCarbon = nonSubstituentBondedAtoms.size() == 0;

        return etherCarbon;
    }

    private static void buildEtherFrom(Ether ether, Atom etherCarbon) {
        Optional<Atom> nextAtom = Optional.empty();

        for (Atom bondedAtom : etherCarbon.getBondedAtoms()) {
            if (isBondableAtom(bondedAtom, Ether.bondableAtoms))
                ether.bond(asGroup(bondedAtom));
            else if (isRadicalCarbon(bondedAtom))
                ether.bond(buildRadicalFrom(bondedAtom));
            else nextAtom = Optional.of(bondedAtom);
        }

        if (nextAtom.isPresent()) {
            if (nextAtom.get().getElement() == Element.O) {
                ether.bond(Group.ether);
                nextAtom = Optional.of(nextAtom.get().getBondedAtoms().get(0));
            }
            else ether.bondCarbon(); // It's a carbon

            buildEtherFrom(ether, nextAtom.get()); // Recursive
        }
    }

    // Open chain:

    private static boolean isNotSubstituent(Atom atom, List<Atom> bondableAtoms) {
        return !isBondableAtom(atom, bondableAtoms) && !isRadicalCarbon(atom);
    }

    private static boolean isBondableAtom(Atom atom, List<Atom> bondableAtoms) {
        return bondableAtoms.contains(atom);
    }

    private static boolean isRadicalCarbon(Atom atom) {
        if (atom.getElement() != Element.C)
            return false;

        List<Atom> bondedAtoms = atom.getBondedAtoms();

        if (bondedAtoms.size() != 3)
            return false;

        boolean radical;

        int hydrogenCount = atom.getAmountOf(Element.H);

        if (hydrogenCount == 1) { // -CH(CH3)2
            Stream<Atom> bondedCH3s = bondedAtoms.stream().filter(bonded -> bonded.getAmountOf(Element.H) == 3);
            bondedCH3s = bondedCH3s.filter(bonded -> bonded.getBondedAtoms().size() == 3);

            radical = bondedCH3s.count() == 2;
        } else if (hydrogenCount == 2) { // -CH2-C
            Stream<Atom> bondedCarbons = bondedAtoms.stream().filter(bonded -> bonded.getElement() == Element.C);

            if (atom.getAmountOf(Element.C) == 1)
                radical = bondedCarbons.allMatch(Molecule::isRadicalCarbon); // Recursive
            else radical = false;
        }
        else radical = hydrogenCount == 3; // -CH3

        return radical;
    }

    private static Substituent buildRadicalFrom(Atom radicalCarbon) {
        int hydrogenCount = radicalCarbon.getAmountOf(Element.H);

        if (hydrogenCount == 3)
            return Substituent.radical(1);

        if (hydrogenCount == 1)
            return Substituent.radical(3, true);

        List<Atom> bondedCarbons = radicalCarbon.getBondedAtoms();
        bondedCarbons.removeIf(bondedAtom -> bondedAtom.getElement() != Element.C);

        Atom nextCarbon = bondedCarbons.get(0); // There must be one

        Substituent radicalEnd = buildRadicalFrom(nextCarbon); // Recursive

        return Substituent.radical(1 + radicalEnd.getCarbonCount(), radicalEnd.isIso()); // Appended
    }

    private static Group asGroup(Atom atom) {
        Group group = atomToGroup.get(atom);

        if (group == null)
            throw new IllegalArgumentException(String.format(unknownFunctionalGroupError, atom.getElement()));

        return group;
    }

}
