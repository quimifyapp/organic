package organico.componentes;

import organico.Organico;

import java.util.*;

public class Carbono extends Organico {

    private final List<Sustituyente> sustituyentes;
    private int enlaces_libres;

    // Constructor:

    public Carbono(int enlaces_previos) {
        sustituyentes = new ArrayList<>();
        enlaces_libres = 4 - enlaces_previos;
    }

    public Carbono(Id funcion, int veces) {
        sustituyentes = new ArrayList<>();
        enlazar(funcion, veces);
    }

    public Carbono(Carbono otro) {
        sustituyentes = new ArrayList<>(otro.sustituyentes);
        enlaces_libres = otro.enlaces_libres;
    }

    // Consultas:

    public boolean contiene(Id funcion) {
        switch(funcion) {
            case alqueno:
                return enlaces_libres == 1; // Como en -CO=
            case alquino:
                return enlaces_libres == 2; // Como en -CH#
            default:
                for(Sustituyente sustituyente : sustituyentes)
                    if(sustituyente.esTipo(funcion))
                        return true;

                return false;
        }
    }

    public boolean estaEnlazadoA(Sustituyente sustituyente) {
        for(Sustituyente otro_sustituyente : sustituyentes)
            if(otro_sustituyente.equals(sustituyente))
                return true;

        return false;
    }

    public int getCantidadDe(Sustituyente sustituyente) {
        return Collections.frequency(sustituyentes, sustituyente);
    }

    public int getCantidadDe(Id funcion) {
        int cantidad = 0;

        for(Sustituyente sustituyente : sustituyentes)
            if(sustituyente.esTipo(funcion))
                cantidad += 1;

        return cantidad;
    }

    @Override
    public boolean equals(Object otro) {
        boolean es_igual = false;

        if(otro != null && otro.getClass() == this.getClass()) {
            Carbono nuevo = (Carbono) otro;

            if(enlaces_libres == nuevo.enlaces_libres && sustituyentes.size() == nuevo.sustituyentes.size())
                for(int i = 0; i < sustituyentes.size(); i++)
                    if(sustituyentes.get(i).equals(nuevo.sustituyentes.get(i))) {
                        es_igual = true;
                        break;
                    }
        }

        return es_igual;
    }

    // Métodos get:

    public List<Sustituyente> getSustituyentesTipo(Id funcion) {
        List<Sustituyente> resultado = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(sustituyente.esTipo(funcion))
                resultado.add(sustituyente);

        return resultado;
    }

    public List<Sustituyente> getSustituyentesSinHidrogeno() {
        List<Sustituyente> sin_hidrogeno = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(!sustituyente.esTipo(Id.hidrogeno))
                sin_hidrogeno.add(sustituyente);

        return sin_hidrogeno;
    }

    public List<Sustituyente> getUnicosSustituyentes() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(!unicos.contains(sustituyente))
                unicos.add(sustituyente);

        return unicos;
    }

    public List<Sustituyente> getSustituyentes() {
        return sustituyentes;
    }

    public Sustituyente getMayorRadical() {
        Sustituyente mayor_radical;

        List<Sustituyente> radicales = getSustituyentesTipo(Id.radical);
        mayor_radical = radicales.get(0); // Se asume que tiene radicales

        for(int i = 1; i < radicales.size(); i++)
            if(radicales.get(i).esMayorRadicalQue(mayor_radical))
                mayor_radical = radicales.get(i);

        return mayor_radical;
    }

    public int getEnlacesLibres() {
        return enlaces_libres;
    }

    // Métodos set:

    public void setEnlacesLibres(int enlaces_libres) {
        this.enlaces_libres = enlaces_libres;
    }

    // Texto:

    private static String cuantificadorMolecular(int cantidad) {
        return (cantidad != 1)
                ? String.valueOf(cantidad) // Como en "CO2"
                : ""; // Como en "CO"
    }

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder("C");

        // Se recogen los tipos de sustituyente:
        List<Sustituyente> unicos = getUnicosSustituyentes(); // Sin repetirse

        // Se ordenan según la prioridad de su función:
        Organico.ordenarPorFunciones(unicos);

        // Se escribe los hidrógenos:
        Sustituyente hidrogeno = new Sustituyente(Id.hidrogeno);
        int cantidad = getCantidadDe(hidrogeno);
        if(cantidad > 0) {
            resultado.append(hidrogeno).append(cuantificadorMolecular(cantidad));
            unicos.remove(unicos.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes excepto el éter:
        unicos.removeIf(sustituyente -> sustituyente.esTipo(Id.eter));

        if(unicos.size() == 1) { // Solo hay un tipo además del hidrógeno y éter
            Sustituyente unico = unicos.get(0);
            String texto = unico.toString();

            if(texto.length() == 1 || Organico.esHalogeno(unico) || unico.getEnlaces() == 3)
                resultado.append(texto); // Como en "CN", "CCl", "COOH", "C(O)(NH2)", "CHO"...
            else resultado.append("(").append(texto).append(")"); // Como en "CH(OH)3", "CH3(CH2CH3)"...

            resultado.append((cuantificadorMolecular(getCantidadDe(unico))));
        }
        else if(unicos.size() > 1) // Hay más de un tipo además del hidrógeno y éter
            for(Sustituyente sustituyente : unicos)
                resultado.append("(").append(sustituyente).append(")") // Como en "C(OH)3(Cl)", "CH2(NO2)(CH3)"...
                        .append(cuantificadorMolecular(getCantidadDe(sustituyente)));

        // Se escribe el éter:
        if(contiene(Id.eter))
            resultado.append(new Sustituyente(Id.eter));

        return resultado.toString();
    }

    // Modificadores:

    public void enlazar(Sustituyente sustituyente) {
        sustituyentes.add(sustituyente);
        enlaces_libres -= sustituyente.getEnlaces();
    }

    public void enlazar(Id funcion) {
        enlazar(new Sustituyente(funcion));
    }

    public void enlazar(Sustituyente sustituyente, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(sustituyente);
    }

    public void enlazar(Id funcion, int veces) {
        for(int i = 0; i < veces; i++)
            enlazar(funcion);
    }

    public void eliminar(Sustituyente sustituyente) {
        sustituyentes.remove(sustituyente); // No se ha eliminado su enlace
    }

    public void eliminar(Id funcion) {
        eliminar(new Sustituyente(funcion)); // No se ha eliminado su enlace
    }

    public void eliminarConEnlaces(Sustituyente sustituyente) {
        eliminar(sustituyente);
        enlaces_libres += sustituyente.getEnlaces();
    }

    public void eliminarConEnlaces(Id funcion) {
        eliminarConEnlaces(new Sustituyente(funcion));
    }

    public void enlazarCarbono() {
        enlaces_libres--;
    }

    public void eliminarEnlace() {
        enlaces_libres++;
    }

}
