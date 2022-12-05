package com.quimify.organic.compounds.open_chain;

import com.quimify.organic.Organic;
import com.quimify.organic.components.*;

import java.util.*;

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

    private static final Set<FunctionalGroup> bondableFunctionalGroups = Set.of(
            FunctionalGroup.acid,
            FunctionalGroup.amide,
            FunctionalGroup.nitrile,
            FunctionalGroup.aldehyde,
            FunctionalGroup.ketone,
            FunctionalGroup.alcohol,
            FunctionalGroup.amine,
            FunctionalGroup.nitro,
            FunctionalGroup.bromine,
            FunctionalGroup.chlorine,
            FunctionalGroup.fluorine,
            FunctionalGroup.iodine,
            FunctionalGroup.radical,
            FunctionalGroup.hydrogen
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
        return chain.getEnlacesLibres();
    }

    public boolean isDone() {
        return chain.isDone();
    }

    public void bondCarbon() {
        chain.bondCarbon();
    }

    public void bond(Substituent substituent) {
        if (bondableFunctionalGroups.contains(substituent.getFunctionalGroup()))
            chain.bond(substituent);
        else throw new IllegalArgumentException("No se puede enlazar [" + substituent.getFunctionalGroup() + "] a un Simple.");
    }

    public void bond(FunctionalGroup functionalGroup) {
        bond(new Substituent(functionalGroup));
    }

    public void correct() {
        correctSubstituents(); // C(O)(OH) → COOH
        correctChainStructure(); // CH2(CH3)-CH2- → CH3-CH2-CH2-
        correctChainOrientation(); // butan-3-ol → butan-2-ol
    }

    public List<FunctionalGroup> getOrderedBondableGroups() {
        List<FunctionalGroup> orderedBondableGroups = new ArrayList<>();

        if (getFreeBonds() > 2)
            orderedBondableGroups.addAll(List.of(
                    FunctionalGroup.acid,
                    FunctionalGroup.amide,
                    FunctionalGroup.nitrile,
                    FunctionalGroup.aldehyde
            ));

        if (getFreeBonds() > 1)
            orderedBondableGroups.add(FunctionalGroup.ketone);

        if (getFreeBonds() > 0) {
            orderedBondableGroups.addAll(List.of(
                    FunctionalGroup.alcohol,
                    FunctionalGroup.amine
            ));

            if (canBondEther())
                orderedBondableGroups.add(FunctionalGroup.ether);

            orderedBondableGroups.addAll(List.of(
                    FunctionalGroup.nitro,
                    FunctionalGroup.bromine,
                    FunctionalGroup.chlorine,
                    FunctionalGroup.fluorine,
                    FunctionalGroup.iodine,
                    FunctionalGroup.radical,
                    FunctionalGroup.hydrogen
            ));
        }

        return orderedBondableGroups;
    }

    public String getName() {
        if (chain.getSize() == 1)
            if(chain.getSubstituentsWithoutHydrogen().stream().filter(substituent ->
                    substituent.getFunctionalGroup() == FunctionalGroup.ketone).count() == 2)
                return "dióxido de carbono";

        int functionalGroup = 0;
        List<FunctionalGroup> funciones = chain.getOrderedGroupsWithoutHydrogenNorEther(); // Sin hidrógeno

        // Se procesa el sufijo:
        String sufijo;
        if (funciones.size() > 0 && funciones.get(0) != FunctionalGroup.nitro && funciones.get(0) != FunctionalGroup.radical // Not suffixes
                && !isHalogen(funciones.get(0)) && !esAlquenoOAlquino(funciones.get(0)))
            sufijo = getSuffixNameFor(funciones.get(functionalGroup++));
        else sufijo = "";

        // Se procesan los prefijos:
        List<Localizador> prefijos = new ArrayList<>();

        while (functionalGroup < funciones.size()) {
            if (!esAlquenoOAlquino(funciones.get(functionalGroup)) && funciones.get(functionalGroup) != FunctionalGroup.radical)
                prefijos.add(getPrefixFor(funciones.get(functionalGroup)));

            functionalGroup++;
        }

        List<Substituent> radicales = chain.getUniqueRadicals();
        for (Substituent radical : radicales)
            prefijos.add(new Localizador(chain.getIndexesOfAll(radical), getRadicalNameParticle(radical)));

        StringBuilder prefijo = new StringBuilder(chain.hasFunctionalGroup(FunctionalGroup.acid) ? "ácido " : "");
        if (prefijos.size() > 0) {
            Localizador.ordenarAlfabeticamente(prefijos);

            for (int i = 0; i < prefijos.size() - 1; i++) {
                prefijo.append(prefijos.get(i).toString());

                if (doesNotStartWithLetter(prefijos.get(i + 1).toString()))
                    prefijo.append("-");
            }

            prefijo.append(prefijos.get(prefijos.size() - 1));
        }

        // Se procesan los enlaces:
        String enlaces = getBondNameFor(FunctionalGroup.alkene) + getBondNameFor(FunctionalGroup.alkyne);

        if (enlaces.equals(""))
            enlaces = "an";
        if (sufijo.equals("") || Organic.doesNotStartWithVowel(sufijo))
            enlaces += "o";
        if (!sufijo.equals("") && Organic.startsWithDigit(sufijo))
            enlaces += "-";

        // Se procesa el cuantificador:
        String cuantificador = cuantificadorDe(chain.getSize());

        if (Organic.doesNotStartWithVowel(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + sufijo;
    }

    public String getStructure() {
        return getReversed().chain.getStructure();
    }

    // PRIVATE -----------------------------------------------------------------------

    // Queries:

    private boolean canBondEther() {
        return chain.getSubstituentsWithoutHydrogen().stream().allMatch(substituent ->
                substituent.getFunctionalGroup().compareTo(FunctionalGroup.ether) > 0);
    }

    // Modifiers:

    private void correctSubstituents() {
        // Breaking substituents down:
        chain.breakDownTerminalToKetoneWith(FunctionalGroup.acid, FunctionalGroup.alcohol); // CHOOH → CH(O)(OH)
        chain.breakDownTerminalToKetoneWith(FunctionalGroup.amide, FunctionalGroup.amine); // CH(ONH2) → CH(O)(NH2)
        chain.breakDownTerminalToKetoneWith(FunctionalGroup.aldehyde, FunctionalGroup.hydrogen); // CHO → CH(O)

        // Grouping substituents:
        chain.groupKetoneWithToTerminal(FunctionalGroup.alcohol, FunctionalGroup.acid); // CH(O)(OH) → CHOOH
        chain.groupKetoneWithToTerminal(FunctionalGroup.amine, FunctionalGroup.amide); // CH(O)(NH2) → CH(ONH2)
        if (chain.getProrityFunctionalGroup().compareTo(FunctionalGroup.aldehyde) > 0) // Would be priority
            chain.groupKetoneWithToTerminal(FunctionalGroup.hydrogen, FunctionalGroup.aldehyde); // CH(O) → CHO

        // Moving out carbons into substituents:
        chain.moveOutWithAs(FunctionalGroup.amide, FunctionalGroup.carbamoyl); // CONH2-COOH → C(OOH)(CONH2)
        chain.moveOutWithAs(FunctionalGroup.nitrile, FunctionalGroup.cyanide); // CN-COOH → C(OOH)(CN)
    }

    private void correctChainStructure() {
        // Se corrigen los radicales que podrían formar parte de la cadena principal:
        chain.correctChainStructureToTheLeft(); // Comprobará internamente si hay radicales
        if (chain.hasFunctionalGroup(FunctionalGroup.radical)) { // Para ahorrar el invertir la cadena
            chain.invertOrientation(); // En lugar de corregirlos por la derecha
            chain.correctChainStructureToTheLeft(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
        }
    }

    private void correctChainOrientation() {
        boolean corrected = false;

        Simple reversed = getReversed();

        List<FunctionalGroup> funciones = chain.getOrderedGroupsWithoutHydrogenNorEther();
        for (int i = 0; i < funciones.size() && !corrected; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = chain.getIndexesOfAll(funciones.get(i))
                    .stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = reversed.chain.getIndexesOfAll(funciones.get(i))
                    .stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corrected = correctOrderAccordingTo(suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if (!corrected && chain.hasFunctionalGroup(FunctionalGroup.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:
            List<String> normales = new ArrayList<>();
            chain.getRadicalSubstituents().forEach(radical -> normales.add(Organic.getRadicalNameParticle(radical)));

            List<String> inversos = new ArrayList<>();
            reversed.chain.getRadicalSubstituents().forEach(radical -> inversos.add(Organic.getRadicalNameParticle(radical)));

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for (int i = 0; i < normales.size() && !corrected; i++)
                corrected = correctOrderAccordingTo(normales.get(i).compareTo(inversos.get(i)));
        }
    }

    private boolean correctOrderAccordingTo(int comparaison) {
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
    private boolean isRedundantInName(FunctionalGroup group) {
        boolean isRedundant;

        if (group != FunctionalGroup.radical && !(esAlquenoOAlquino(group)) && new Substituent(group).getBondCount() == 3)
            isRedundant = true; // Sustituyente terminal: solo puede ir en el primero y/o último
        else if (chain.getSize() == 3) // Derivados del propeno
            isRedundant = group == FunctionalGroup.alkene && chain.getAmountOf(FunctionalGroup.alkene) == 2; // Propadieno
        else if (chain.getSize() == 2) { // Derivados del etano
            if (esAlquenoOAlquino(group) || chain.hasFunctionalGroup(FunctionalGroup.alkyne)) // Hay una posición posible
                isRedundant = true;
            else isRedundant = chain.getSubstituentsWithoutHydrogen().size() == 1; // Hay uno, como cloroetino o etanol
        } else isRedundant = chain.getSize() == 1; // Derivados del metano

        return isRedundant;
    }

    private Localizador getPrefixFor(FunctionalGroup functionalGroup) {
        Localizador preffix;

        List<Integer> posiciones = chain.getIndexesOfAll(functionalGroup);
        String nombre = getPrefixNameParticle(functionalGroup);

        if (isRedundantInName(functionalGroup)) // Sobran los localizadores porque son evidentes
            preffix = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
        else preffix = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

        return preffix;
    }

    private String getBondNameFor(FunctionalGroup bond) {
        String bondName = "";

        List<Integer> posiciones = chain.getIndexesOfAll(bond);
        String nombre = getBondNameParticle(bond);

        if (posiciones.size() > 0) {
            Localizador localizador;

            if (isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
                localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
            else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

            String localizador_to_string = localizador.toString();

            if (startsWithDigit(localizador_to_string))
                bondName += "-"; // Guion antes de los localizadores

            bondName += localizador_to_string;
        }

        return bondName;
    }

    private String getSuffixNameFor(FunctionalGroup bond) {
        String suffixName;

        List<Integer> posiciones = chain.getIndexesOfAll(bond);
        String nombre = getSuffixNameParticle(bond);

        if (isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
            suffixName = multiplicadorDe(posiciones.size()) + nombre; // Como "dioico"
        else suffixName = new Localizador(posiciones, nombre).toString(); // Como "2-3-diona"

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
