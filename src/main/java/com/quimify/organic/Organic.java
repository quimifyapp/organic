package com.quimify.organic;

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

    // Text:

    private static String greekPrefixFor(int digit) {
        return switch (digit) {
            case 0 -> "";
            case 1 -> "hen";
            case 2 -> "do";
            case 3 -> "tri";
            case 4 -> "tetra";
            case 5 -> "pent";
            case 6 -> "hex";
            case 7 -> "hept";
            case 8 -> "oct";
            case 9 -> "non";
            default -> throw new IllegalArgumentException("There is no greek prefix for: " + digit + ".");
        };
    }

    protected static String quantifierFor(int number) {
        String quantifier;

        if(number < 10) { // [1, 9]
            quantifier = switch (number) {
                case 1 -> "met";
                case 2 -> "et";
                case 3 -> "prop";
                case 4 -> "but";
                default -> // 0, 5, 6, 7, 8, 9
                        greekPrefixFor(number);
            };
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

                switch (centenas) {
                    case 1 -> quantifier += "ahect"; // [101, 199]
                    case 2 -> quantifier += "adict"; // [200, 299]
                    case 3 -> quantifier += "atrict"; // [300, 399]
                    case 4 -> quantifier += "atetract"; // [400, 499]
                    default -> quantifier += "a" + greekPrefixFor(centenas) + "act"; // [500, 999]
                }
            }
            else throw new IllegalArgumentException("Can't handle this amount of carbons: " + number + "."); // > 999
        }

        return quantifier;
    }

    protected static String multiplierFor(int number) {
        return switch (number) {
            case 1 -> "";
            case 2 -> "di";
            case 3 -> "tri";
            case 4 -> "tetra";
            default -> quantifierFor(number) + "a";
        };
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

    protected static String radicalNameParticleFor(Substituent radical) {
        String nameParticle;

        if(radical.isIso())
            nameParticle = "iso";
        else nameParticle = "";

        nameParticle += quantifierFor(radical.getCarbonCount()) + "il";

        return nameParticle;
    }

    protected static String prefixNameParticleFor(Group group) {
        return switch (group) {
            case carbamoyl -> "carbamoil";
            case cyanide -> "ciano";
            case ketone -> "oxo";
            case alcohol -> "hidroxi";
            case amine -> "amino";
            case nitro -> "nitro";
            case bromine -> "bromo";
            case chlorine -> "cloro";
            case fluorine -> "fluoro";
            case iodine -> "yodo";
            default -> throw new IllegalArgumentException("No existen prefijos para la función " + group + ".");
        };
    }

    protected static String bondNameParticleFor(Group bond) {
        return switch (bond) {
            case alkene -> "en";
            case alkyne -> "in";
            default -> throw new IllegalArgumentException("La función " + bond + " no es un tipo de enlace.");
        };
    }

    protected static String suffixNameParticleFor(Group group) {
        return switch (group) {
            case acid -> "oico";
            case amide -> "amida";
            case nitrile -> "nitrilo";
            case aldehyde -> "al";
            case ketone -> "ona";
            case alcohol -> "ol";
            case amine -> "amina";
            default -> throw new IllegalArgumentException("No existen sufijos para la función " + group + ".");
        };
    }

    protected static String bondSymbolFor(int bondOrder) {
        return switch (bondOrder) {
            case 0 -> "-";
            case 1 -> "=";
            case 2 -> "≡";
            default -> throw new IllegalArgumentException("No existen enlaces de orden " + bondOrder + ".");
        };
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

    protected static boolean startsWithDigit(String text) {
        return text.matches("^\\d.*$");
    }

    // Private:

    private static char firstLetterOf(String text) {
        return (char) text.chars().filter(c -> String.valueOf((char) c).matches("[a-zA-Z]"))
                .findFirst().orElse(0);
    }

}
