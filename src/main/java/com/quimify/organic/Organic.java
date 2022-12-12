package com.quimify.organic;

import com.quimify.organic.components.Chain;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

// This class wraps up organic utilities.

public class Organic {

    private static final Set<Group> halogenGroups = Set.of(
            Group.bromine,
            Group.chlorine,
            Group.fluorine,
            Group.iodine
    );

    // Queries:

    protected static boolean isHalogen(Group group) {
        return halogenGroups.contains(group);
    }

    protected static boolean isBond(Group group) {
        return group == Group.alkene || group ==  Group.alkyne;
    }

    // Text: TODO refactor, translation

    protected static String quantifierFor(int number) {
        String quantifier;

        if(number < 10) { // [1, 9]
            switch(number) {
                case 1:
                    quantifier = "met";
                    break;
                case 2:
                    quantifier = "et";
                    break;
                case 3:
                    quantifier = "prop";
                    break;
                case 4:
                    quantifier = "but";
                    break;
                default: // 0, 5, 6, 7, 8, 9
                    quantifier = greekPrefixFor(number);
                    break;
            }
        }
        else if(number == 11) // 11
            quantifier = "undec";
        else { // 10 U [12, 999]
            int decenas = number / 10;
            int unidades = number - (decenas * 10);

            if(number < 15) // 10 U [12, 14]
                quantifier = greekPrefixFor(unidades) + "dec";
            else if(number < 20) // [15, 19]
                quantifier = greekPrefixFor(unidades) + "adec";
            else if(number == 20) // 20
                quantifier = "icos";
            else if(number == 21) // 21
                quantifier = "heneicos";
            else if(number < 25) // [22, 25]
                quantifier = greekPrefixFor(unidades) + "cos";
            else if(number < 30) // [26, 29]
                quantifier = greekPrefixFor(unidades) + "acos";
            else if(number < 100) { // [30, 99]
                quantifier = greekPrefixFor(unidades);

                if(unidades > 4)
                    quantifier += "a";

                quantifier += greekPrefixFor(decenas);

                if(decenas == 4)
                    quantifier += "cont";
                else quantifier += "acont";
            }
            else if(number == 100) // 100
                quantifier = "hect";
            else if(number < 999) {  // [101, 999]
                int centenas = number / 100;
                decenas = decenas - (centenas * 10);

                quantifier = quantifierFor(10 * decenas + unidades); // Recursivo

                switch(centenas) {
                    case 1: // [101, 199]
                        quantifier += "ahect";
                        break;
                    case 2: // [200, 299]
                        quantifier += "adict";
                        break;
                    case 3: // [300, 399]
                        quantifier += "atrict";
                        break;
                    case 4: // [400, 499]
                        quantifier += "atetract";
                        break;
                    default: // [500, 999]
                        quantifier += "a" + greekPrefixFor(centenas) + "act";
                        break;
                }
            }
            else throw new IllegalArgumentException("Can't handle this amount of carbons: " + number + "."); // > 999
        }

        return quantifier;
    }

    private static String greekPrefixFor(int digit) {
        String greekPrefix;

        switch(digit) {
            case 0:
                greekPrefix = "";
                break;
            case 1:
                greekPrefix = "hen";
                break;
            case 2:
                greekPrefix = "do";
                break;
            case 3:
                greekPrefix = "tri";
                break;
            case 4:
                greekPrefix = "tetra";
                break;
            case 5:
                greekPrefix = "pent";
                break;
            case 6:
                greekPrefix = "hex";
                break;
            case 7:
                greekPrefix = "hept";
                break;
            case 8:
                greekPrefix = "oct";
                break;
            case 9:
                greekPrefix = "non";
                break;
            default:
                throw new IllegalArgumentException("There is no greek prefix for: " + digit + ".");
        }

        return greekPrefix;
    }

    protected static String multiplierFor(int number) {
        String multiplier;

        switch(number) {
            case 1:
                multiplier = "";
                break;
            case 2:
                multiplier = "di";
                break;
            case 3:
                multiplier = "tri";
                break;
            case 4:
                multiplier = "tetra";
                break;
            default:
                multiplier = quantifierFor(number) + "a";
                break;
        }

        return multiplier;
    }

    protected static class Locator {

        // Esta clase representa un localizador de un nombre IUPAC, como "2,3-diol".

        private String posiciones, multiplicador, lexema;

        // EJEMPLOS:
        /*
            "2,3-diol"  =   { posiciones: "2,3",  multiplicador: "di",     lexema: "ol"     }
            "tetrain"   =   { posiciones: "",     multiplicador: "tetra",  lexema: "in"     }
            "fluoro"    =   { posiciones: "",     multiplicador: "",       lexema: "fluoro" }
        */

        private void construir(String posiciones, String multiplicador, String nombre) {
            this.posiciones = posiciones;
            this.multiplicador = multiplicador;
            this.lexema = nombre;
        }

        public Locator(String multiplicador, String lexema) {
            construir("", multiplicador, lexema);
        }

        public Locator(List<Integer> posiciones, String lexema) {
            StringBuilder auxiliar = new StringBuilder();

            if(posiciones.size() > 0) {
                for(int i = 0; i < posiciones.size() - 1; i++)
                    auxiliar.append(posiciones.get(i) + 1).append(",");
                auxiliar.append(posiciones.get(posiciones.size() - 1) + 1);
            }

            construir(auxiliar.toString(), Organic.multiplierFor(posiciones.size()), lexema);
        }

        // No se tienen en cuenta los multiplicadores ni las posiciones, como propone la IUPAC.
        // Ej.: "2,3-diol" → "ol"
        public static void ordenarAlfabeticamente(List<Locator> localizadores) {
            localizadores.sort(Comparator.comparing(Locator::getLexema));
        }

        @Override
        public String toString() {
            String resultado = "";

            if(!posiciones.equals(""))
                resultado = posiciones + "-";
            resultado += multiplicador + lexema;

            return resultado;
        }

        // Getters y setters:

        public String getLexema() {
            return lexema;
        }

    }

    protected static Locator getPrefixForIn(Group group, Chain chain, boolean isRedundant) {
        Locator prefix;

        List<Integer> indexes = chain.getIndexesOf(group);
        String name = prefixNameParticleFor(group);

        if (isRedundant) // Sobran los localizadores porque son evidentes
            prefix = new Locator(multiplierFor(indexes.size()), name); // Como "difluoro"
        else prefix = new Locator(indexes, name); // Como "1,2-difluoro"

        return prefix;
    }

    protected static String getBondNameForIn(Group bond, Chain chain, boolean isRedundant) {
        String bondName = "";

        List<Integer> indexes = chain.getIndexesOf(bond);

        if (indexes.size() > 0) {
            Locator locator;

            if (isRedundant) // Sobran los localizadores porque son evidentes isRedundantInName(bond)
                locator = new Locator(multiplierFor(indexes.size()), bondNameParticleFor(bond)); // Como "dien"
            else locator = new Locator(indexes, bondNameParticleFor(bond)); // Como "1,2-dien"

            String locatorName = locator.toString();

            if (startsWithDigit(locatorName))
                bondName += "-"; // Guion antes de los localizadores

            bondName += locatorName;
        }

        return bondName;
    }

    protected static String radicalNameParticleFor(Substituent radical) {
        String nameParticle;

        if(radical.isIso())
            nameParticle = "iso";
        else nameParticle = "";

        nameParticle += quantifierFor(radical.getCarbonCount()) + "il";

        return nameParticle;
    }

    protected static String prefixNameParticleFor(Group group) {
        String prefixNameParticle;

        switch(group) {
            case carbamoyl:
                prefixNameParticle = "carbamoil";
                break;
            case cyanide:
                prefixNameParticle = "ciano";
                break;
            case ketone:
                prefixNameParticle = "oxo";
                break;
            case alcohol:
                prefixNameParticle = "hidroxi";
                break;
            case amine:
                prefixNameParticle = "amino";
                break;
            case nitro:
                prefixNameParticle = "nitro";
                break;
            case bromine:
                prefixNameParticle = "bromo";
                break;
            case chlorine:
                prefixNameParticle = "cloro";
                break;
            case fluorine:
                prefixNameParticle = "fluoro";
                break;
            case iodine:
                prefixNameParticle = "yodo";
                break;
            default:
                throw new IllegalArgumentException("No existen prefijos para la función " + group + ".");
        }

        return prefixNameParticle;
    }

    protected static String bondNameParticleFor(Group bond) {
        String bondNameParticle;

        switch(bond) {
            case alkene:
                bondNameParticle = "en";
                break;
            case alkyne:
                bondNameParticle = "in";
                break;
            default:
                throw new IllegalArgumentException("La función " + bond + " no es un tipo de enlace.");
        }

        return bondNameParticle;
    }

    protected static String suffixNameParticleFor(Group group) {
        String suffixNameParticle;

        switch(group) {
            case acid:
                suffixNameParticle = "oico";
                break;
            case amide:
                suffixNameParticle = "amida";
                break;
            case nitrile:
                suffixNameParticle = "nitrilo";
                break;
            case aldehyde:
                suffixNameParticle = "al";
                break;
            case ketone:
                suffixNameParticle = "ona";
                break;
            case alcohol:
                suffixNameParticle = "ol";
                break;
            case amine:
                suffixNameParticle = "amina";
                break;
            default:
                throw new IllegalArgumentException("No existen sufijos para la función " + group + ".");
        }

        return suffixNameParticle;
    }

    protected static String bondSymbolFor(int bondOrder) {
        switch(bondOrder) {
            case 0:
                return "-";
            case 1:
                return "=";
            case 2:
                return "≡";
            default:
                throw new IllegalArgumentException("No existen enlaces de orden " + bondOrder + ".");
        }
    }

    protected static String molecularQuantifierFor(int count) {
        return count != 1 ? String.valueOf(count) : ""; // As in "CO2" or "CO"
    }

    // Text utils: TODO private?

    protected static boolean doesNotStartWithVowel(String text) {
        return "aeiou".indexOf(firstLetterOf(text)) == -1;
    }

    protected static boolean doesNotStartWithLetter(String text) {
        return text.charAt(0) != firstLetterOf(text);
    }

    private static char firstLetterOf(String text) {
        return (char) text.chars().filter(c -> String.valueOf((char) c).matches("[a-zA-Z]"))
                .findFirst().orElse(0);
    }

    protected static boolean startsWithDigit(String text) {
        return text.matches("^\\d.*$");
    }

}
