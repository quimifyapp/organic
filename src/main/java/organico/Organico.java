package organico;

import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.*;

import static java.util.Collections.swap;

// Esta clase generaliza distintos organico.tipos de compuestos orgánicos: cadenas simples, cíclicos, ésteres...

public class Organico {

    protected final List<Carbono> carbonos = new ArrayList<>();

    private static final List<Id> halogenos = Arrays.asList(Id.bromo, Id.cloro, Id.fluor, Id.yodo);

    // Consultas generales:

    protected static boolean esHalogeno(Id funcion) {
        return halogenos.contains(funcion);
    }

    public static boolean esHalogeno(Sustituyente sustituyente) {
        return esHalogeno(sustituyente.getFuncion());
    }

    protected static void ordenarFunciones(List<Id> funciones) {
        for(int i = 0; i < funciones.size() - 1;) // Sin incremento
            if(funciones.get(i).compareTo(funciones.get(i + 1)) > 0) {
                swap(funciones, i, i + 1); // get(i) > get(i + 1)
                i = 0;
            }
            else i++; // get(i) <= get(i + 1)
    }

    public static void ordenarPorFunciones(List<Sustituyente> sustituyentes) {
        for(int i = 0; i < sustituyentes.size() - 1;) // Sin incremento
            if(sustituyentes.get(i).getFuncion().compareTo(sustituyentes.get(i + 1).getFuncion()) > 0) {
                swap(sustituyentes, i, i + 1); // get(i) > get(i + 1)
                i = 0;
            }
            else i++; // get(i) <= get(i + 1)
    }

    // Consultas particulares:

    protected boolean contiene(Id funcion) {
        for(Carbono carbono : carbonos)
            if(carbono.contiene(funcion))
                return true;

        return false;
    }

    protected List<Integer> getPosicionesDe(Id funcion) {
        List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos con la función

        for(int i = 0; i < carbonos.size(); i++)
            if(carbonos.get(i).contiene(funcion))
                posiciones.add(i);

        return posiciones;
    }

    protected List<Integer> listPositionsOf(Sustituyente sustituyente) {
        List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos enlazados al sustituyente

        for(int i = 0; i < carbonos.size(); i++)
            if(carbonos.get(i).estaEnlazadoA(sustituyente))
                posiciones.add(i);

        return posiciones;
    }

    protected List<Id> getFunciones() {
        List<Id> funciones = new ArrayList<>(); // Funciones presentes sin repetición, en orden y sin hidrógeno

        for(Id funcion : Id.values()) // Todas las funciones recogidas en organico.componentes.Id
            if(!funcion.equals(Id.hidrogeno))
                for(Carbono carbono : carbonos)
                    if(carbono.contiene(funcion)) {
                        funciones.add(funcion);
                        break;
                    }

        return funciones;
    }

    protected List<Sustituyente> getSustituyentesUnicosTipo(Id funcion) {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Carbono carbono : carbonos)
            for(Sustituyente sustituyente : carbono.getSustituyentesTipo(funcion))
                if(!unicos.contains(sustituyente))
                    unicos.add(sustituyente);

        return unicos;
    }

    protected List<Sustituyente> getSustituyentesUnicos() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Carbono carbono : carbonos)
            for(Sustituyente sustituyente : carbono.getSustituyentes())
                if(!unicos.contains(sustituyente))
                    unicos.add(sustituyente);

        return unicos;
    }

    protected List<Sustituyente> getSustituyentesTipo(Id funcion) {
        List<Sustituyente> sustituyentes = new ArrayList<>();

        for(Carbono carbono : carbonos)
            sustituyentes.addAll(carbono.getSustituyentesTipo(funcion));

        return sustituyentes;
    }

    protected List<Sustituyente> getSustituyentes() {
        List<Sustituyente> sustituyentes = new ArrayList<>();

        for(Carbono carbono : carbonos)
            sustituyentes.addAll(carbono.getSustituyentes());

        return sustituyentes;
    }

    protected List<Sustituyente> getSustituyentesSinHidrogeno() {
        List<Sustituyente> sin_hidrogeno = new ArrayList<>();

        for(Carbono carbono : carbonos)
            sin_hidrogeno.addAll(carbono.getSustituyentesSinHidrogeno());

        return sin_hidrogeno;
    }

    // Texto:

    static class Localizador {

        // Esta clase representa un localizador de un nombre IUPAC, como "2,3-diol".

        private String posiciones, multiplicador, nombre;

        // EJEMPLOS:
        /*
            "2,3-diol"  =   { posiciones: "2,3",    multiplicador: "di",    nombre: "ol"     }
            "tetrain"   =   { posiciones: "",       multiplicador: "tetra", nombre: "in"     }
            "fluoro"    =   { posiciones: "",       multiplicador: "",      nombre: "fluoro" }
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

            construir(auxiliar.toString(), Organico.cuantificador(indices.size()), nombre);
        }

        // No se tienen en cuenta los multiplicadores ni las posiciones, como propone la IUPAC.
        // Ej.: "2,3-diol" -> "ol"
        public static void ordenarAlfabeticamente(List<Localizador> localizadores) {
            localizadores.sort(Comparator.comparing(Localizador::getNombre));
        }

        @Override
        public String toString() {
            String resultado = "";

            if(!posiciones.equals(""))
                resultado = posiciones + "-";
            resultado += multiplicador + nombre;

            return resultado;
        }

        // Getters y setters:

        public String getPosiciones() {
            return posiciones;
        }

        public void setPosiciones(String posiciones) {
            this.posiciones = posiciones;
        }

        public String getMultiplicador() {
            return multiplicador;
        }

        public void setMultiplicador(String multiplicador) {
            this.multiplicador = multiplicador;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

    }

    protected static String prefijoGriego(int numero) {
        String resultado;

        switch(numero) {
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

    protected static String multiplicador(int numero) {
        String resultado;

        if(numero < 10) { // [1, 9]
            switch(numero) {
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
            else if(numero == 20) // 20
                resultado = "icos";
            else if(numero == 21) // 21
                resultado = "heneicos";
            else if(numero < 25) // [22, 25]
                    resultado = prefijoGriego(unidades) + "cos";
            else if(numero < 30) // [26, 29]
                    resultado = prefijoGriego(unidades) + "acos";
            else if(numero < 100) { // [30, 99]
                resultado = prefijoGriego(unidades);

                if(unidades > 4)
                    resultado += "a";

                resultado += prefijoGriego(decenas);

                if(decenas == 4)
                    resultado += "cont";
                else resultado += "acont";
            }
            else if(numero == 100) // 100
                resultado = "hect";
            else if(numero < 999) {  // [101, 999]
                int centenas = numero / 100;
                decenas = decenas - (centenas * 10);

                resultado = multiplicador(10 * decenas + unidades); // Recursivo

                switch(centenas) {
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

    protected static String cuantificador(int numero) {
        String resultado;

        switch(numero) {
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

    protected static String nombreRadical(Sustituyente radical) {
        String resultado;

        if(radical.getIso())
            resultado = "iso";
        else resultado = "";

        resultado += multiplicador(radical.getCarbonos()) + "il";

        return resultado;
    }

    protected static String nombrePrefijo(Id funcion) {
        String prefijo;

        switch(funcion) {
            case carbamoil:
                prefijo = "carbamoil";
                break;
            case cianuro:
                prefijo = "ciano";
                break;
            case cetona:
                prefijo = "oxo";
                break;
            case alcohol:
                prefijo = "hidroxi";
                break;
            case amina:
                prefijo = "amino";
                break;
            case nitro:
                prefijo = "nitro";
                break;
            case bromo:
                prefijo = "bromo";
                break;
            case cloro:
                prefijo = "cloro";
                break;
            case fluor:
                prefijo = "fluoro";
                break;
            case yodo:
                prefijo = "yodo";
                break;
            default:
                // Error...
                prefijo = "";
                break;
        }

        return prefijo;
    }

    protected static String nombreSufijo(Id funcion) {
        String sufijo;

        switch(funcion) {
            case acido:
                sufijo = "oico"; // TODO: "ico"?
                break;
            case amida:
                sufijo = "amida";
                break;
            case nitrilo:
                sufijo = "nitrilo";
                break;
            case aldehido:
                sufijo = "al";
                break;
            case cetona:
                sufijo = "ona";
                break;
            case alcohol:
                sufijo = "ol";
                break;
            case amina:
                sufijo = "amina";
                break;
            default:
                // Error...
                sufijo = "";
                break;
        }

        return sufijo;
    }

    protected String enlaceDeOrden(int orden) {
        switch(orden) {
            case 0:
                return ""; // Fin de la molécula
            case 1:
                return "-";
            case 2:
                return "=";
            case 3:
                return "≡";
            default:
                // Error...
                return "";
        }
    }

}
