import java.util.*;

public class Organico {

    private final List<Carbono> carbonos = new ArrayList<>();
    private final Set<Id> funciones = new HashSet<>();

    private static final List<Id> halogenos = Arrays.asList(Id.bromo, Id.cloro, Id.fluor, Id.yodo);

    // Consultas:

    public static boolean esHalogeno(Id funcion) {
        return halogenos.contains(funcion);
    }

    public static boolean esHalogeno(Sustituyente sustituyente) {
        return esHalogeno(sustituyente.getFuncion());
    }

    // Texto:

    public static String prefijoGriego(int numero) {
        String resultado;

        switch (numero) {
            case 0:
                resultado = "";
                break;
            case 1:
                resultado = "hen";
                break;
            case 2:
                resultado = "do";
                break;
            case 3:
                resultado = "tri";
                break;
            case 4:
                resultado = "tetra";
                break;
            case 5:
                resultado = "pent";
                break;
            case 6:
                resultado = "hex";
                break;
            case 7:
                resultado = "hept";
                break;
            case 8:
                resultado = "oct";
                break;
            case 9:
                resultado = "non";
                break;
            default:
                resultado = null;
                break;
        }

        return resultado;
    }

    public static String multiplicador(int numero) {
        String resultado;

        if (numero < 10) { // [1, 9]
            switch (numero) {
                case 1:
                    resultado = "met";
                    break;
                case 2:
                    resultado = "et";
                    break;
                case 3:
                    resultado = "prop";
                    break;
                case 4:
                    resultado = "but";
                    break;
                default: // 0, 5, 6, 7, 8, 9
                    resultado = prefijoGriego(numero);
                    break;
            }
        }
        else if(numero == 11) // 11
            resultado = "undec";
        else { // 10 U [12, 999]
            int decenas = numero / 10;
            int unidades = numero - (decenas * 10);

            if(numero < 15) // 10 U [12, 14]
                resultado = prefijoGriego(unidades) + "dec";
            else if(numero < 20) // [15, 19]
                resultado = prefijoGriego(unidades) + "adec";
            else if (numero == 20) // 20
                resultado = "icos";
            else if (numero == 21) // 21
                resultado = "heneicos";
            else if (numero < 25) // [22, 25]
                    resultado = prefijoGriego(unidades) + "cos";
            else if(numero < 30) // [26, 29]
                    resultado = prefijoGriego(unidades) + "acos";
            else if (numero < 100) { // [30, 99]
                resultado = prefijoGriego(unidades);

                if (unidades > 4)
                    resultado += "a";

                resultado += prefijoGriego(decenas);

                if (decenas == 4)
                    resultado += "cont";
                else resultado += "acont";
            }
            else if(numero == 100) // 100
                resultado = "hect";
            else if(numero < 999) {  // [101, 999]
                int centenas = numero / 100;
                decenas = decenas - (centenas * 10);

                resultado = multiplicador(10 * decenas + unidades); // Recursivo

                switch (centenas) {
                    case 1: // [101, 199]
                        resultado += "ahect";
                        break;
                    case 2: // [200, 299]
                        resultado += "adict";
                        break;
                    case 3: // [300, 399]
                        resultado += "atrict";
                        break;
                    case 4: // [400, 499]
                        resultado += "atetract";
                        break;
                    default: // [500, 999]
                        resultado += "a" + prefijoGriego(centenas) + "act";
                        break;
                }
            }
            else resultado = null; // 'numero' < 0 | 'numero' > 999
        }

        return resultado;
    }

    public static String cuantificador(int numero) {
        String resultado;

        switch (numero) {
            case 1:
                resultado = "";
                break;
            case 2:
                resultado = "di";
                break;
            case 3:
                resultado = "tri";
                break;
            case 4:
                resultado = "tetra";
                break;
            default:
                resultado = multiplicador(numero) + "a";
                break;
        }

        return resultado;
    }

    static class Localizador {
        public String posiciones, multiplicador, nombre;

		/* EJEMPLOS:
            "2,3-diol"  =   { posiciones: "2,3",   multiplicador: "di",       nombre: "ol"      }
            "tetrain"   =   { posiciones: "",      multiplicador: "tetra",    nombre: "in"      }
            "fluoro"    =   { posiciones: "",      multiplicador: "",         nombre: "fluoro"  }
		*/

        private void construir(String posiciones, String multiplicador, String nombre) {
            this.posiciones = posiciones;
            this.multiplicador = multiplicador;
            this.nombre = nombre;
        }

        public Localizador(String posiciones, String multiplicador, String nombre) {
            construir(posiciones, multiplicador, nombre);
        }

        public Localizador(List<Integer> indices, String nombre) {
            StringBuilder auxiliar = new StringBuilder();

            if(indices.size() > 0) {
                for(int i = 0; i < indices.size() - 1; i++)
                    auxiliar.append(indices.get(i) + 1).append(",");
                auxiliar.append(indices.get(indices.size() - 1) + 1);
            }

            construir(auxiliar.toString(), cuantificador(indices.size()), nombre);
        }

        @Override
        public String toString() {
            String resultado = "";

            if (!posiciones.equals(""))
                resultado = posiciones + "-";
            resultado += multiplicador + nombre;

            return resultado;
        }
    }

    public static String nombreDelRadical(Sustituyente radical)
    {
        String resultado = "";

        if(radical.getIso())
            resultado += "iso";
        resultado += multiplicador(radical.getCarbonos()) + "il";

        return resultado;
    }

}
