import java.util.*;

import static java.util.Collections.swap;

public class Organico {

    private final List<Carbono> carbonos = new ArrayList<>();

    private static final List<Id> halogenos = Arrays.asList(Id.bromo, Id.cloro, Id.fluor, Id.yodo);

    // Consultas generales:

    protected static boolean esHalogeno(Id funcion) {
        return halogenos.contains(funcion);
    }

    protected static boolean esHalogeno(Sustituyente sustituyente) {
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

    protected static void ordenarPorFunciones(List<Sustituyente> sustituyentes) {
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

    // Devuelve las funciones presentes sin repetición y por orden de prioridad (hidrógeno no incluido)
    protected List<Id> getFunciones() {
        List<Id> funciones = new ArrayList<>();

        for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
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
        List<Sustituyente> sustituyentes = new ArrayList<>();

        for(Carbono carbono : carbonos)
            for(Sustituyente sustituyente : carbono.getSustituyentes())
                if(!sustituyente.esTipo(Id.hidrogeno))
                    sustituyentes.add(sustituyente);

        return sustituyentes;
    }

    // Texto:

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

    protected static String nombreDelRadical(Sustituyente radical) {
        String resultado = "";

        if(radical.getIso())
            resultado += "iso";
        resultado += multiplicador(radical.getCarbonos()) + "il";

        return resultado;
    }

}
