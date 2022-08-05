package organico;

import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.*;

import static java.util.Collections.swap;

// Esta clase generaliza distintos tipos de compuestos orgánicos: cadenas simples, cíclicos, ésteres...

public class Organico {

    private final List<Carbono> carbonos = new ArrayList<>();

    // Consultas generales:

    private static final List<Id> halogenos = Arrays.asList(Id.bromo, Id.cloro, Id.fluor, Id.yodo);

    protected static boolean esHalogeno(Id funcion) {
        return halogenos.contains(funcion);
    }

    public static boolean esHalogeno(Sustituyente sustituyente) {
        return esHalogeno(sustituyente.getFuncion());
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

    protected Carbono getUltimo() {
        return carbonos.get(carbonos.size() - 1);
    }

    protected int getEnlacesLibres() {
        return getUltimo().getEnlacesLibres();
    }

    protected boolean contiene(Id funcion) {
        for(Carbono carbono : carbonos)
            if(carbono.contiene(funcion))
                return true;

        return false;
    }

    protected boolean hayFunciones() { // Sin hidrógeno
        for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
            if(funcion != Id.hidrogeno)
                for(Carbono carbono : carbonos)
                    if(carbono.contiene(funcion))
                        return true;

        return false;
    }

    // Internas:

    protected List<Carbono> inversaDe(List<Carbono> cadena) {
        List<Carbono> inversa = new ArrayList<>(cadena);

        // Le da la vuelta a los carbonos:
        Collections.reverse(inversa);

        // Ajusta los enlaces (no son simétricos):
        if(inversa.size() > 1) {
            for(int i = 0, j = cadena.size() - 2; i < inversa.size() - 1; i++)
                inversa.get(i).setEnlacesLibres(cadena.get(j--).getEnlacesLibres());

            inversa.get(inversa.size() - 1).setEnlacesLibres(0); // Se supone que cadena no tiene enlaces sueltos
        }

        return inversa;
    }

    // Métodos get:

    protected Id getFuncionPrioritaria() { // Con hidrógeno
        for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
            for(Carbono carbono : carbonos)
                if(carbono.contiene(funcion))
                    return funcion;

        return null;
    }

    protected List<Id> getFuncionesOrdenadasEn(List<Carbono> cadena) { // Sin hidrógeno ni éter
        List<Id> funciones = new ArrayList<>(); // Funciones presentes sin repetición y en orden

        for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
            if(!(funcion == Id.hidrogeno || funcion == Id.eter))
                for(Carbono carbono : cadena)
                    if(carbono.contiene(funcion))
                        funciones.add(funcion);

        return funciones;
    }

    protected List<Integer> getPosicionesDeEn(Id funcion, List<Carbono> cadena) {
        List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos con la función

        for(int i = 0; i < cadena.size(); i++)
            if(cadena.get(i).contiene(funcion))
                posiciones.add(i);

        return posiciones;
    }

    protected List<Integer> getPosicionesDeEn(Sustituyente sustituyente, List<Carbono> cadena) {
        List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos enlazados al sustituyente

        for(int i = 0; i < cadena.size(); i++)
            if(cadena.get(i).estaEnlazadoA(sustituyente))
                posiciones.add(i);

        return posiciones;
    }

    protected List<Sustituyente> getSustituyentesUnicos() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Carbono carbono : carbonos)
            for(Sustituyente sustituyente : carbono.getSustituyentes())
                if(!unicos.contains(sustituyente))
                    unicos.add(sustituyente);

        return unicos;
    }

    protected List<Sustituyente> getSustituyentesSinHidrogeno() {
        List<Sustituyente> sin_hidrogeno = new ArrayList<>();

        for(Carbono carbono : carbonos)
            sin_hidrogeno.addAll(carbono.getSustituyentesSinHidrogeno());

        return sin_hidrogeno;
    }

    protected List<Sustituyente> getRadicalesUnicos() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Carbono carbono : carbonos)
            for(Sustituyente sustituyente : carbono.getSustituyentesTipo(Id.radical))
                if(!unicos.contains(sustituyente))
                    unicos.add(sustituyente);

        return unicos;
    }

    protected List<Sustituyente> getRadicales() {
        List<Sustituyente> sustituyentes = new ArrayList<>();

        for(Carbono carbono : carbonos)
            sustituyentes.addAll(carbono.getSustituyentesTipo(Id.radical));

        return sustituyentes;
    }

    protected List<Carbono> getCarbonos() {
        return carbonos;
    }

    // Interfaz:

    public boolean estaCompleta() {
        return getEnlacesLibres() == 0;
    }

    public void enlazarCarbono() {
        Carbono ultimo = getUltimo();
        ultimo.enlazarCarbono();
        carbonos.add(new Carbono(ultimo.getEnlacesLibres() + 1));
    }

    public void enlazarSustituyente(Sustituyente sustituyente) {
        getUltimo().enlazarSustituyente(sustituyente);
    }

    public void enlazarSustituyente(Id funcion) {
        enlazarSustituyente(new Sustituyente(funcion));
    }

    // Texto:

    protected static class Localizador {

        // Esta clase representa un localizador de un nombre IUPAC, como "2,3-diol".

        private String posiciones, multiplicador, lexema;

        // EJEMPLOS:
        /*
            "2,3-diol"  =   { posiciones: "2,3",    multiplicador: "di",    lexema: "ol"     }
            "tetrain"   =   { posiciones: "",       multiplicador: "tetra", lexema: "in"     }
            "fluoro"    =   { posiciones: "",       multiplicador: "",      lexema: "fluoro" }
        */

        private void construir(String posiciones, String multiplicador, String nombre) {
            this.posiciones = posiciones;
            this.multiplicador = multiplicador;
            this.lexema = nombre;
        }

        public Localizador(String posiciones, String multiplicador, String lexema) {
            construir(posiciones, multiplicador, lexema);
        }

        public Localizador(String multiplicador, String lexema) {
            construir("", multiplicador, lexema);
        }

        public Localizador(List<Integer> posiciones, String lexema) {
            StringBuilder auxiliar = new StringBuilder();

            if(posiciones.size() > 0) {
                for(int i = 0; i < posiciones.size() - 1; i++)
                    auxiliar.append(posiciones.get(i) + 1).append(",");
                auxiliar.append(posiciones.get(posiciones.size() - 1) + 1);
            }

            construir(auxiliar.toString(), Organico.multiplicadorDe(posiciones.size()), lexema);
        }

        // No se tienen en cuenta los multiplicadores ni las posiciones, como propone la IUPAC.
        // Ej.: "2,3-diol" → "ol"
        public static void ordenarAlfabeticamente(List<Localizador> localizadores) {
            localizadores.sort(Comparator.comparing(Localizador::getLexema));
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

    protected static String prefijoGriegoDe(int numero) {
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

    protected static String cuantificadorDe(int numero) {
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
                    resultado = prefijoGriegoDe(numero);
                    break;
            }
        }
        else if(numero == 11) // 11
            resultado = "undec";
        else { // 10 U [12, 999]
            int decenas = numero / 10;
            int unidades = numero - (decenas * 10);

            if(numero < 15) // 10 U [12, 14]
                resultado = prefijoGriegoDe(unidades) + "dec";
            else if(numero < 20) // [15, 19]
                resultado = prefijoGriegoDe(unidades) + "adec";
            else if(numero == 20) // 20
                resultado = "icos";
            else if(numero == 21) // 21
                resultado = "heneicos";
            else if(numero < 25) // [22, 25]
                resultado = prefijoGriegoDe(unidades) + "cos";
            else if(numero < 30) // [26, 29]
                resultado = prefijoGriegoDe(unidades) + "acos";
            else if(numero < 100) { // [30, 99]
                resultado = prefijoGriegoDe(unidades);

                if(unidades > 4)
                    resultado += "a";

                resultado += prefijoGriegoDe(decenas);

                if(decenas == 4)
                    resultado += "cont";
                else resultado += "acont";
            }
            else if(numero == 100) // 100
                resultado = "hect";
            else if(numero < 999) {  // [101, 999]
                int centenas = numero / 100;
                decenas = decenas - (centenas * 10);

                resultado = cuantificadorDe(10 * decenas + unidades); // Recursivo

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
                        resultado += "a" + prefijoGriegoDe(centenas) + "act";
                        break;
                }
            }
            else resultado = null; // 'numero' < 0 | 'numero' > 999
        }

        return resultado;
    }

    protected static String multiplicadorDe(int numero) {
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
                resultado = cuantificadorDe(numero) + "a";
                break;
        }

        return resultado;
    }

    protected static String nombreDeRadical(Sustituyente radical) {
        String resultado;

        if(radical.getIso())
            resultado = "iso";
        else resultado = "";

        resultado += cuantificadorDe(radical.getCarbonos()) + "il";

        return resultado;
    }

    protected static String nombreDePrefijo(Id funcion) {
        String nombre_prefijo;

        switch(funcion) {
            case carbamoil:
                nombre_prefijo = "carbamoil";
                break;
            case cianuro:
                nombre_prefijo = "ciano";
                break;
            case cetona:
                nombre_prefijo = "oxo";
                break;
            case alcohol:
                nombre_prefijo = "hidroxi";
                break;
            case amina:
                nombre_prefijo = "amino";
                break;
            case nitro:
                nombre_prefijo = "nitro";
                break;
            case bromo:
                nombre_prefijo = "bromo";
                break;
            case cloro:
                nombre_prefijo = "cloro";
                break;
            case fluor:
                nombre_prefijo = "fluoro";
                break;
            case yodo:
                nombre_prefijo = "yodo";
                break;
            default:
                throw new IllegalArgumentException("No existen prefijos para la función " + funcion);
        }

        return nombre_prefijo;
    }

    protected static String nombreDeEnlace(Id enlace) {
        String nombre_enlace;

        switch(enlace) {
            case alqueno:
                nombre_enlace = "en";
                break;
            case alquino:
                nombre_enlace = "in";
                break;
            default:
                throw new IllegalArgumentException("La función " + enlace + " no es un tipo de enlace.");
        }

        return nombre_enlace;
    }

    protected static String nombreDeSufijo(Id funcion) {
        String nombre_sufijo;

        switch(funcion) {
            case acido:
                nombre_sufijo = "oico";
                break;
            case amida:
                nombre_sufijo = "amida";
                break;
            case nitrilo:
                nombre_sufijo = "nitrilo";
                break;
            case aldehido:
                nombre_sufijo = "al";
                break;
            case cetona:
                nombre_sufijo = "ona";
                break;
            case alcohol:
                nombre_sufijo = "ol";
                break;
            case amina:
                nombre_sufijo = "amina";
                break;
            default:
                throw new IllegalArgumentException("No existen sufijos para la función " + funcion);
        }

        return nombre_sufijo;
    }

    protected static String enlaceDeOrden(int orden) {
        switch(orden) {
            case 0:
                return "-";
            case 1:
                return "=";
            case 2:
                return "≡";
            default:
                throw new IllegalArgumentException("No existen enlaces de orden " + orden);
        }
    }

    protected static String formulaDe(List<Carbono> cadena) {
        StringBuilder formula = new StringBuilder();

        // Se escribe el primero:
        Carbono primero = cadena.get(0);
        formula.append(primero); // Como CH

        // Se escribe el resto con los enlaces libres del anterior:
        int enlaces_libres_anterior = primero.getEnlacesLibres();
        for(int i = 1; i < cadena.size(); i++) {
            formula.append(enlaceDeOrden(enlaces_libres_anterior)); // Como CH=
            formula.append(cadena.get(i)); // Como CH=CH

            enlaces_libres_anterior = cadena.get(i).getEnlacesLibres();
        }

        // Se escribe los enlaces libres del último:
        if(enlaces_libres_anterior > 0 && enlaces_libres_anterior < 4) // Ni está completo ni es el primero vacío
            formula.append(enlaceDeOrden(enlaces_libres_anterior - 1)); // Como CH=CH-CH2-C≡

        return formula.toString();
    }

    protected static char primeraLetraDe(String texto) {
        for(char c : texto.toCharArray())
            if(c >= 'a' && c <= 'z')
                return c;

        return 0;
    }

    protected static boolean noEmpiezaPorVocal(String texto) {
        char primera = primeraLetraDe(texto);
        return primera != 'a' && primera != 'e' && primera != 'i' && primera != 'o' && primera != 'u';
    }

    protected static boolean noEmpiezaPorLetra(String texto) {
        return texto.charAt(0) != primeraLetraDe(texto);
    }

    protected static boolean empiezaPorDigito(String texto) {
        char primera = texto.charAt(0);
        return primera >= '0' && primera <= '9';
    }

}
