import java.util.*;

import static java.util.Collections.swap;

public class Carbono {

    private final List<Sustituyente> sustituyentes = new ArrayList<>();
    private int enlaces_libres;

    public Carbono(int enlaces_previos) {
        enlaces_libres = 4 - enlaces_previos;
    }

    // Modificadores:

    public void nuevoSustituyente(Sustituyente sustituyente) {
        sustituyentes.add(sustituyente);
        enlaces_libres -= sustituyente.getEnlaces();
    }

    public void eliminarSustituyente(Sustituyente sustituyente) {
        sustituyentes.remove(sustituyente); // No se ha eliminado su enlace
    }

    public void eliminarSustituyenteConEnlaces(Sustituyente sustituyente) {
        eliminarSustituyente(sustituyente);
        enlaces_libres += sustituyente.getEnlaces();
    }

    public void enlazarCarbono() {
        enlaces_libres--;
    }

    public void eliminarEnlace() {
        enlaces_libres++;
    }

    // Consultas:

    public List<Sustituyente> getSustituyentesCon(Id funcion) {
        List<Sustituyente> resultado = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(sustituyente.getFuncion().equals(funcion))
                resultado.add(sustituyente);

        return resultado;
    }

    public List<Sustituyente> getUnicosSustituyentes() {
        List<Sustituyente> unicos = new ArrayList<>();

        for(Sustituyente sustituyente : sustituyentes)
            if(!unicos.contains(sustituyente))
                unicos.add(sustituyente);

        return unicos;
    }

    public int cantidadDe(Sustituyente sustituyente) {
        return Collections.frequency(sustituyentes, sustituyente);
    }

    public boolean estaEnlazadoA(Id funcion) {
        for(Sustituyente sustituyente : sustituyentes)
            if(sustituyente.getFuncion().equals(funcion))
                return true;

        return false;
    }

    public boolean estaEnlazadoA(Sustituyente sustituyente) {
        for(Sustituyente otro_sustituyente : sustituyentes)
            if(otro_sustituyente.equals(sustituyente))
                return true;

        return false;
    }

    // Texto:

    private String cuantificadorMolecular(int cantidad) {
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
        for(int i = 0; i < unicos.size() - 1;) // Sin incremento
            if(unicos.get(i).getFuncion().compareTo(unicos.get(i + 1).getFuncion()) > 0) { // get(i) > get(i + 1)
                swap(unicos, i, i + 1);
                i = 0;
            }
            else i++; // get(i) <= get(i + 1)

        // Se escribe los hidrógenos:
        Sustituyente hidrogeno = new Sustituyente(Id.hidrogeno);
        int cantidad = cantidadDe(hidrogeno);
        if(cantidad > 0) {
            resultado.append(hidrogeno).append(cuantificadorMolecular(cantidad));
            unicos.remove(unicos.size() - 1); // Se borra el hidrógeno de la lista
        }

        // Se escribe el resto de sustituyentes:
        if(unicos.size() == 1) { // Solo hay un tipo además del hidrógeno
            String sustituyente = unicos.get(0).toString();

            if(!estaEnlazadoA(Id.hidrogeno) || sustituyente.length() == 1 || Organico.esHalogeno(unicos.get(0)))
                resultado.append(sustituyente); // Como en "CN", "COH", "CH3Br"...
            else resultado.append("(").append(sustituyente).append(")"); // Como en "CH(OH)3", "CH3(CH2CH3)"...

            resultado.append((cuantificadorMolecular(cantidadDe(unicos.get(0)))));
        }
        else if(unicos.size() > 1) // Hay más de un tipo además del hidrógeno
            for(Sustituyente sustituyente : unicos)
                resultado.append("(").append(sustituyente).append(")") // Como en "C(OH)3(Cl)", "CH2(NO2)(CH3)"...
                        .append(cuantificadorMolecular(cantidadDe(sustituyente)));

        return resultado.toString();
    }

    // Getters y setters:

    public List<Sustituyente> getSustituyentes() {
        return sustituyentes;
    }

    public int getEnlacesLibres() {
        return enlaces_libres;
    }

    public void setEnlacesLibres(int enlaces_libres) {
        this.enlaces_libres = enlaces_libres;
    }

}