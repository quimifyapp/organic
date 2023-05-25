package com.quimify.organic.molecules.openchain;

import com.quimify.organic.Nomenclature;
import com.quimify.organic.components.*;

import java.util.*;
import java.util.stream.Collectors;

// This class represents compounds composed of a single finite carbon chain with substituents.

public final class Simple extends Nomenclature implements OpenChain {

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

    private static final Set<Group> bondableGroups = Set.of(
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

    // Interface:

    public boolean isDone() {
        return chain.isDone();
    }

    public List<Group> getBondableGroups() {
        List<Group> bondableGroups = new ArrayList<>();

        if (chain.getFreeBondCount() >= 3) {
            bondableGroups.add(Group.acid);
            bondableGroups.add(Group.amide);
            bondableGroups.add(Group.nitrile);
            bondableGroups.add(Group.aldehyde);
        }

        if (chain.getFreeBondCount() >= 2)
            bondableGroups.add(Group.ketone);

        if (chain.getFreeBondCount() >= 1) {
            bondableGroups.add(Group.alcohol);
            bondableGroups.add(Group.amine);

            if (wouldBePriority(Group.ether))
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

    public OpenChain bond(Group group) {
        return bond(new Substituent(group));
    }

    public OpenChain bond(Substituent substituent) {
        OpenChain openChain;

        if (substituent.getGroup() == Group.ether) {
            openChain = new Ether(chain);
            openChain.bond(Group.ether);
        }
        else if (bondableGroups.contains(substituent.getGroup())) {
            openChain = this;
            chain.bond(substituent);
        }
        else throw new IllegalArgumentException("Can't bond " + substituent.getGroup() + " to a Simple.");

        return openChain;
    }

    public boolean canBondCarbon() {
        return chain.canBondCarbon();
    }

    public void bondCarbon() {
        chain.bondCarbon();
    }

    public void standardize() {
        correctSubstituents(); // C(O)(OH)- → COOH-
        correctChainStructure(); // CH2(CH3)-CH2- → CH3-CH2-CH2-
        correctChainOrientation(); // butan-3-ol → butan-2-ol
    }

    public String getName() {
        if (chain.getSize() == 1 && chain.getIndexesOf(Group.ketone).size() == 2)
            return "dióxido de carbono";

        List<Group> groups = chain.getGroups();
        groups.removeIf(group -> group == Group.hydrogen);

        int groupIndex = 0;

        // Se procesa el sufijo:
        String suffix = "";

        if (groups.size() > 0 && !isHalogen(groups.get(0)) && !isBond(groups.get(0)))
            if (groups.get(0) != Group.nitro && groups.get(0) != Group.radical)
                suffix = getSuffixNameFor(groups.get(groupIndex++));

        // Se procesan los prefijos:
        List<Locator> prefixes = new ArrayList<>();

        while (groupIndex < groups.size()) {
            if (!isBond(groups.get(groupIndex)) && groups.get(groupIndex) != Group.radical) {
                Group group = groups.get(groupIndex);
                boolean redundant = isRedundantInName(group);

                prefixes.add(getPrefixForIn(group, chain, redundant));
            }

            groupIndex++;
        }

        Set<Substituent> uniqueRadicals = new HashSet<>(chain.getSubstituents());
        uniqueRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

        for (Substituent radical : uniqueRadicals)
            prefixes.add(new Locator(chain.getIndexesOf(radical), radicalNameParticleFor(radical)));

        StringBuilder prefix = new StringBuilder(chain.isBondedTo(Group.acid) ? "ácido " : "");
        if (prefixes.size() > 0) {
            Locator.orderAplhabetically(prefixes);

            for (int i = 0; i < prefixes.size() - 1; i++) {
                prefix.append(prefixes.get(i).toString());

                if (doesNotStartWithLetter(prefixes.get(i + 1).toString()))
                    prefix.append("-");
            }

            prefix.append(prefixes.get(prefixes.size() - 1));
        }

        // Se procesan los enlaces:
        String bonds = getBondNameForIn(Group.alkene, chain, isRedundantInName(Group.alkene)) +
                getBondNameForIn(Group.alkyne, chain, isRedundantInName(Group.alkyne));

        if (bonds.equals(""))
            bonds = "an";
        if (suffix.equals("") || doesNotStartWithVowel(suffix))
            bonds += "o";
        if (!suffix.equals("") && startsWithDigit(suffix))
            bonds += "-";

        // Se procesa el cuantificador:
        String quantifier = quantifierFor(chain.getSize());

        if (doesNotStartWithVowel(bonds))
            quantifier += "a";

        return prefix + quantifier + bonds + suffix;
    }

    public String getStructure() {
        return chain.getInverseOrientation().toString();
    }

    // Private:

    private boolean wouldBePriority(Group group) {
        return chain.getPriorityGroup().map(priorityBondedGroup ->
                priorityBondedGroup.ordinal() >= group.ordinal()).orElse(true);
    }

    private void correctSubstituents() {
        // Breaking substituents down:

        breakDownTerminalToKetoneWith(Group.acid, Group.alcohol); // CHOOH → CH(O)(OH)
        breakDownTerminalToKetoneWith(Group.amide, Group.amine); // CH(ONH2) → CH(O)(NH2)
        breakDownTerminalToKetoneWith(Group.aldehyde, Group.hydrogen); // CHO → CH(O)

        // Grouping substituents:

        groupKetoneWithToTerminal(Group.alcohol, Group.acid); // CH(O)(OH) → CHOOH
        groupKetoneWithToTerminal(Group.amine, Group.amide); // CH(O)(NH2) → CH(ONH2)

        Optional<Group> priorityGroup = chain.getPriorityGroup();

        if (priorityGroup.isPresent() && priorityGroup.get().ordinal() > Group.aldehyde.ordinal()) // Would be priority
            groupKetoneWithToTerminal(Group.hydrogen, Group.aldehyde); // CH(O) → CHO

        // Moving out carbons into substituents:

        moveOutWithAs(Group.amide, Group.carbamoyl); // CONH2-COOH → C(OOH)(CONH2)
        moveOutWithAs(Group.nitrile, Group.cyanide); // CN-COOH → C(OOH)(CN)
    }

    private void breakDownTerminalToKetoneWith(Group terminal, Group companion) {
        breakDownTerminalToKetoneWithIn(terminal, companion, chain.getFirstCarbon());
        breakDownTerminalToKetoneWithIn(terminal, companion, chain.getLastCarbon());
    }

    private void breakDownTerminalToKetoneWithIn(Group terminal, Group companion, Carbon carbon) {
        if (carbon.isBondedTo(terminal)) {
            carbon.unbond(terminal); // C(A)- → C-
            carbon.bond(Group.ketone); // C- → C(O)-
            carbon.bond(companion); // C- → C(O)(B)-
        }
    }

    private void groupKetoneWithToTerminal(Group companion, Group terminal) {
        groupKetoneWithToTerminalIn(companion, terminal, chain.getFirstCarbon());
        groupKetoneWithToTerminalIn(companion, terminal, chain.getLastCarbon());
    }

    private void groupKetoneWithToTerminalIn(Group companion, Group terminal, Carbon carbon) {
        if (carbon.isBondedTo(Group.ketone) && carbon.isBondedTo(companion)) {
            carbon.unbond(Group.ketone); // C(O)(A)- → C(A)-
            carbon.unbond(companion); // C(A)- → C-
            carbon.bond(terminal);// C- → C(B)-
        }
    }

    private void moveOutWithAs(Group terminal, Group substitute) {
        if (!wouldBePriority(terminal)) {
            if (chain.getSize() > 1)
                moveOutWithAsIn(terminal, substitute, chain.getFirstCarbon(), chain.getCarbon(1));
            if (chain.getSize() > 1) // Might have changed
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
        chain.correctChainToTheLeft(); // CF(CH3)(CH2CH3) → CH3-CH2-CF(CH3)
        chain.correctChainToTheRight(); // CH3-CH2-CF(CH3) → CH3-CH2-CF-CH3
    }

    private void correctChainOrientation() { // TODO separate in more methods
        Chain inverseOrientation = chain.getInverseOrientation();

        List<Group> groups = chain.getGroups();
        groups.removeIf(group -> group == Group.hydrogen);

        boolean corrected = false;
        for (int i = 0; i < groups.size() && !corrected; i++) {
            // Se calculan las sumas de sus posiciones:
            int normalSum = chain.getIndexesOf(groups.get(i))
                    .stream().mapToInt(Integer::intValue).sum();

            int inverseSum = inverseOrientation.getIndexesOf(groups.get(i))
                    .stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corrected = correctChainOrientationBy(normalSum - inverseSum);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if (!corrected && chain.isBondedTo(Group.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:

            List<Substituent> radicals = chain.getSubstituents();
            radicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

            List<Substituent> reversedRadicals = inverseOrientation.getSubstituents();
            reversedRadicals.removeIf(substituent -> substituent.getGroup() != Group.radical);

            List<String> radicalNames = radicals.stream()
                    .map(Nomenclature::radicalNameParticleFor).collect(Collectors.toList());

            List<String> reversedRadicalNames = reversedRadicals.stream()
                    .map(Nomenclature::radicalNameParticleFor).collect(Collectors.toList());

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for (int i = 0; i < radicalNames.size() && !corrected; i++)
                corrected = correctChainOrientationBy(radicalNames.get(i).compareTo(reversedRadicalNames.get(i)));
        }
    }

    private boolean correctChainOrientationBy(int comparaison) {
        if (comparaison == 0)
            return false; // Undecided

        if (comparaison > 0)
            chain.reverseOrientation();

        return true; // Correction has been done
    }

    // Naming:

    private boolean isRedundantInName(Group group) {
        if (group != Group.radical && !isBond(group) && new Substituent(group).getBondCount() == 3)
            return true; // Terminal substituents are either in first carbon or both first and last

        switch (chain.getSize()) {
            case 1: // Like methanol
                return true;
            case 2: // Like ethanol, ethene, or chloroethyne
                int substituentsWithoutHydrogen = chain.getSubstituents().size() - chain.getAmountOf(Group.hydrogen);
                return substituentsWithoutHydrogen == 1 || isBond(group) || chain.isBondedTo(Group.alkyne);
            case 3: // It's propadiene
                return group == Group.alkene && chain.getAmountOf(Group.alkene) == 2;
            default:
                return false;
        }
    }

    private String getSuffixNameFor(Group bond) { // TODO fix repeated code
        String name = suffixNameParticleFor(bond);

        List<Integer> indexes = chain.getIndexesOf(bond);

        if (isRedundantInName(bond)) // The locators are unnecessary
            return multiplierFor(indexes.size()) + name; // I.E. "dioico"

        return new Locator(indexes, name).toString(); // I.E. "2-3-diona"
    }

}
