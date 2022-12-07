package com.quimify.organic.compounds.open_chain;

import com.quimify.organic.Organic;
import com.quimify.organic.components.*;

import java.util.*;
import java.util.stream.Collectors;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public final class Simple extends Organic implements OpenChain {

    private final Chain chain;

    // Constants:

    public static final Set<Atom> bondableAtoms = Set.of(
            Atom.N,
            Atom.O,
            Atom.OH,
            Atom.NH2,
            Atom.NO2,
            Atom.Br,
            Atom.Cl,
            Atom.F,
            Atom.I,
            Atom.H
    );

    private static final Set<Group> BONDABLE_GROUPS = Set.of(
            Group.acid,
            Group.amide,
            Group.nitrile,
            Group.aldehyde,
            Group.ketone,
            Group.alcohol,
            Group.amine,
            Group.nitro,
            Group.bromine,
            Group.chlorine,
            Group.fluorine,
            Group.iodine,
            Group.radical,
            Group.hydrogen
    );

    // Constructors:

    public Simple() {
        this.chain = new Chain(0);
    }

    private Simple(Chain chain) {
        this.chain = new Chain(chain);
    }

    // OPEN CHAIN --------------------------------------------------------------------

    public Simple getReversed() {
        return new Simple(chain.getInverseOriented());
    }

    public int getFreeBonds() {
        return chain.getFreeBonds();
    }

    public boolean isDone() {
        return chain.isDone();
    }

    public void bondCarbon() {
        chain.bondCarbon();
    }

    public void bond(Substituent substituent) {
        if (BONDABLE_GROUPS.contains(substituent.getGroup()))
            chain.bond(substituent);
        else throw new IllegalArgumentException("Can't bond " + substituent.getGroup() + " to a Simple.");
    }

    public void bond(Group group) {
        bond(new Substituent(group));
    }

    public void correct() {
        correctSubstituents(); // C(O)(OH) → COOH
        correctChainStructure(); // CH2(CH3)-CH2- → CH3-CH2-CH2-
        correctChainOrientation(); // butan-3-ol → butan-2-ol
    }

    public List<Group> getBondableGroups() {
        List<Group> bondableGroups = new ArrayList<>();

        if (getFreeBonds() >= 3) {
            bondableGroups.add(Group.acid);
            bondableGroups.add(Group.amide);
            bondableGroups.add(Group.nitrile);
            bondableGroups.add(Group.aldehyde);
        }

        if (getFreeBonds() >= 2)
            bondableGroups.add(Group.ketone);

        if (getFreeBonds() >= 1) {
            bondableGroups.add(Group.alcohol);
            bondableGroups.add(Group.amine);

            if (canBondEther())
                bondableGroups.add(Group.ether);

            bondableGroups.add(Group.nitro);
            bondableGroups.add(Group.bromine);
            bondableGroups.add(Group.chlorine);
            bondableGroups.add(Group.fluorine);
            bondableGroups.add(Group.iodine);
            bondableGroups.add(Group.radical);
            bondableGroups.add(Group.hydrogen);
        }

        return bondableGroups;
    }

    public String getName() {
        if (chain.getSize() == 1 && chain.getIndexesOf(Group.ketone).size() == 2)
            return "dióxido de carbono";

        List<Group> bondedGroups = chain.getBondedGroups();
        bondedGroups.removeIf(group -> group == Group.hydrogen);
        int functionalGroup = 0;

        // Se procesa el sufijo:
        String suffix = "";

        if(bondedGroups.size() > 0 && !isHalogen(bondedGroups.get(0)) && !isBond(bondedGroups.get(0)))
            if(bondedGroups.get(0) != Group.nitro && bondedGroups.get(0) != Group.radical)
                suffix = getSuffixNameFor(bondedGroups.get(functionalGroup++));

        // Se procesan los prefijos:
        List<Locator> prefixes = new ArrayList<>();

        while (functionalGroup < bondedGroups.size()) {
            if (bondedGroups.get(functionalGroup) != Group.radical && !isBond(bondedGroups.get(functionalGroup)))
                prefixes.add(getPrefixFor(bondedGroups.get(functionalGroup)));

            functionalGroup++;
        }

        Set<Substituent> uniqueRadicals = chain.getUniqueSubstituents();
        uniqueRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

        for (Substituent radical : uniqueRadicals)
            prefixes.add(new Locator(chain.getIndexesOf(radical), getRadicalNameParticle(radical)));

        StringBuilder prefijo = new StringBuilder(chain.hasFunctionalGroup(Group.acid) ? "ácido " : "");
        if (prefixes.size() > 0) {
            Locator.ordenarAlfabeticamente(prefixes);

            for (int i = 0; i < prefixes.size() - 1; i++) {
                prefijo.append(prefixes.get(i).toString());

                if (doesNotStartWithLetter(prefixes.get(i + 1).toString()))
                    prefijo.append("-");
            }

            prefijo.append(prefixes.get(prefixes.size() - 1));
        }

        // Se procesan los enlaces:
        String enlaces = getBondNameFor(Group.alkene) + getBondNameFor(Group.alkyne);

        if (enlaces.equals(""))
            enlaces = "an";
        if (suffix.equals("") || Organic.doesNotStartWithVowel(suffix))
            enlaces += "o";
        if (!suffix.equals("") && Organic.startsWithDigit(suffix))
            enlaces += "-";

        // Se procesa el cuantificador:
        String cuantificador = cuantificadorDe(chain.getSize());

        if (Organic.doesNotStartWithVowel(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + suffix;
    }

    public String getStructure() {
        return getReversed().chain.getStructure();
    }

    // PRIVATE -----------------------------------------------------------------------

    // Queries:

    private boolean canBondEther() {
        return chain.getSubstituents().stream().allMatch(substituent ->
                substituent.getGroup().ordinal() > Group.ether.ordinal());
    }

    // Modifiers:

    private void correctSubstituents() {
        // Breaking substituents down:

        breakDownTerminalToKetoneWith(Group.acid, Group.alcohol); // CHOOH → CH(O)(OH)
        breakDownTerminalToKetoneWith(Group.amide, Group.amine); // CH(ONH2) → CH(O)(NH2)
        breakDownTerminalToKetoneWith(Group.aldehyde, Group.hydrogen); // CHO → CH(O)

        // Grouping substituents:

        groupKetoneWithToTerminal(Group.alcohol, Group.acid); // CH(O)(OH) → CHOOH
        groupKetoneWithToTerminal(Group.amine, Group.amide); // CH(O)(NH2) → CH(ONH2)

        if (chain.getPriorityBondedGroup().compareTo(Group.aldehyde) > 0) // Would be priority
            groupKetoneWithToTerminal(Group.hydrogen, Group.aldehyde); // CH(O) → CHO

        // Moving out carbons into substituents:

        moveOutWithAs(Group.amide, Group.carbamoyl); // CONH2-COOH → C(OOH)(CONH2)
        moveOutWithAs(Group.nitrile, Group.cyanide); // CN-COOH → C(OOH)(CN)
    }

    public void breakDownTerminalToKetoneWith(Group terminal, Group companion) {
        breakDownTerminalToKetoneWithIn(terminal, companion, chain.getFirstCarbon());
        breakDownTerminalToKetoneWithIn(terminal, companion, chain.getLastCarbon());
    }

    private void breakDownTerminalToKetoneWithIn(Group terminal, Group companion, Carbon carbon) {
        if(carbon.isBondedTo(terminal)) {
            carbon.removeWithBonds(terminal); // C(A)- → C-
            carbon.bond(Group.ketone); // C- → C(O)-
            carbon.bond(companion); // C- → C(O)(B)-
        }
    }

    public void groupKetoneWithToTerminal(Group companion, Group terminal) {
        groupKetoneWithToTerminalIn(companion, terminal, chain.getFirstCarbon());
        groupKetoneWithToTerminalIn(companion, terminal, chain.getLastCarbon());
    }

    private void groupKetoneWithToTerminalIn(Group companion, Group terminal, Carbon carbon) {
        if(carbon.isBondedTo(Group.ketone) && carbon.isBondedTo(companion)) {
            carbon.removeWithBonds(Group.ketone); // C(O)(A)- → C(A)-
            carbon.removeWithBonds(companion); // C(A)- → C-
            carbon.bond(terminal);// C- → C(B)-
        }
    }

    public void moveOutWithAs(Group terminal, Group substitute) {
        if(terminal != chain.getPriorityBondedGroup()) {
            if(chain.getSize() > 1)
                moveOutWithAsIn(terminal, substitute, chain.getFirstCarbon(), chain.getCarbon(1));
            if(chain.getSize() > 1) // Might have changed
                moveOutWithAsIn(terminal, substitute, chain.getLastCarbon(), chain.getCarbon(chain.getSize() - 2));
        }
    }

    private void moveOutWithAsIn(Group terminal, Group substitute, Carbon ending, Carbon before) {
        if (ending.isBondedTo(terminal)) {
            chain.removeCarbon(ending); // C(A)-C(X)- → C(X)=
            before.freeBond(); // C(X)= → C(X)≡
            before.bond(substitute); // C(X)≡ → C(CA)(X)-
        }
    }

    private void correctChainStructure() {
        // Se corrigen los radicales que podrían formar parte de la cadena principal:
        chain.correctChainStructureToTheLeft(); // Comprobará internamente si hay radicales
        if (chain.hasFunctionalGroup(Group.radical)) { // Para ahorrar el invertir la cadena
            chain.invertOrientation(); // En lugar de corregirlos por la derecha
            chain.correctChainStructureToTheLeft(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
        }
    }

    private void correctChainOrientation() {
        Simple reversed = getReversed();

        List<Group> bondedGroups = chain.getBondedGroups();
        bondedGroups.removeIf(group -> group == Group.hydrogen);

        boolean corrected = false;
        for (int i = 0; i < bondedGroups.size() && !corrected; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = chain.getIndexesOf(bondedGroups.get(i))
                    .stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = reversed.chain.getIndexesOf(bondedGroups.get(i))
                    .stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corrected = correctChainOrientationBy(suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if (!corrected && chain.hasFunctionalGroup(Group.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:

            List<Substituent> radicals = chain.getSubstituents();
            radicals.removeIf(substituent -> substituent.getGroup() != Group.radical);
            List<String> radicalNames = radicals.stream()
                    .map(Organic::getRadicalNameParticle).collect(Collectors.toList());

            List<Substituent> reversedRadicals = chain.getSubstituents();
            reversedRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);
            List<String> reversedRadicalNames = radicals.stream()
                    .map(Organic::getRadicalNameParticle).collect(Collectors.toList());

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for (int i = 0; i < radicalNames.size() && !corrected; i++)
                corrected = correctChainOrientationBy(radicalNames.get(i).compareTo(reversedRadicalNames.get(i)));
        }
    }

    private boolean correctChainOrientationBy(int comparaison) {
        boolean corrected;

        if (comparaison != 0) { // No son iguales
            if (comparaison > 0) // El inverso va antes alfabéticamente
                chain.invertOrientation();
            corrected = true; // Ya se ha corregido el orden según los radicales alfabéticamente
        } else corrected = false; // Indecidible

        return corrected;
    }

    // Text:
    // TODO: poner en común en Chain
    private boolean isRedundantInName(Group group) {
        boolean isRedundant;

        if (group != Group.radical && !(isBond(group)) && new Substituent(group).getBondCount() == 3)
            isRedundant = true; // Sustituyente terminal: solo puede ir en el primero y/o último
        else if (chain.getSize() == 3) // Derivados del propeno
            isRedundant = group == Group.alkene && chain.getAmountOf(Group.alkene) == 2; // Propadieno
        else if (chain.getSize() == 2) { // Derivados del etano
            if(!isBond(group) && !chain.hasFunctionalGroup(Group.alkyne)) {
                List<Substituent> substituents = chain.getSubstituents();
                substituents.removeIf(substituent -> substituent.getGroup() == Group.hydrogen);
                isRedundant = substituents.size() == 1; // Hay uno, como cloroetino o etanol
            }
            else isRedundant = true; // Hay una posición posible
        } else isRedundant = chain.getSize() == 1; // Derivados del metano

        return isRedundant;
    }

    private Locator getPrefixFor(Group group) {
        Locator prefix;

        List<Integer> indexes = chain.getIndexesOf(group);
        String name = getPrefixNameParticle(group);

        if (isRedundantInName(group)) // Sobran los localizadores porque son evidentes
            prefix = new Locator(multiplicadorDe(indexes.size()), name); // Como "difluoro"
        else prefix = new Locator(indexes, name); // Como "1,2-difluoro"

        return prefix;
    }

    private String getBondNameFor(Group bond) {
        String bondName = "";

        List<Integer> indexes = chain.getIndexesOf(bond);
        String name = getBondNameParticle(bond);

        if (indexes.size() > 0) {
            Locator locator;

            if (isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
                locator = new Locator(multiplicadorDe(indexes.size()), name); // Como "dien"
            else locator = new Locator(indexes, name); // Como "1,2-dien"

            String localizador_to_string = locator.toString();

            if (startsWithDigit(localizador_to_string))
                bondName += "-"; // Guion antes de los localizadores

            bondName += localizador_to_string;
        }

        return bondName;
    }

    private String getSuffixNameFor(Group bond) {
        String suffixName;

        List<Integer> indexes = chain.getIndexesOf(bond);
        String name = getSuffixNameParticle(bond);

        if (isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
            suffixName = multiplicadorDe(indexes.size()) + name; // Como "dioico"
        else suffixName = new Locator(indexes, name).toString(); // Como "2-3-diona"

        return suffixName;
    }

    @Override
    public String toString() {
        return getStructure();
    }

    // Getters:

    public Chain getChain() {
        return chain;
    }

}
