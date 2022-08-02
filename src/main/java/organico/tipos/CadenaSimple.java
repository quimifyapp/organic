package organico.tipos;

import organico.Organico;
import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public class CadenaSimple extends Organico {

    // Constructor:

    private void construir() {
        carbonos.add(new Carbono(0)); // =C=
    }

    public CadenaSimple() {
        construir();
    }

    // Internos:

    private Carbono getUltimo() {
        return carbonos.get(carbonos.size() - 1);
    }

    private int getEnlacesLibres() {
        return getUltimo().getEnlacesLibres();
    }

    // La cadena debe haber sido previamente corregida
    private boolean esRedundante(Id funcion) {
        boolean es_redundante;

        // Derivados del propano:
        if(carbonos.size() == 3) {
            List<Id> funciones = getFunciones();

            if(funcion == Id.cetona && funciones.get(0).compareTo(Id.aldehido) > 0) // Es propanona
                es_redundante = true;
            // Es propeno | propadieno | propino:
            else es_redundante = funciones.size() == 1 && (funcion == Id.alqueno || funcion == Id.alquino);
        }
        // Derivados del etano:
        else if(carbonos.size() == 2) {
            if(funcion == Id.alqueno || funcion == Id.alquino) // Solo hay una posición para el enlace
                es_redundante = true;
            else if(getSustituyentesSinHidrogeno().size() == 1) // Solo hay un sustituyente (como cloroetano, etenol...)
                es_redundante = true;
            else if(contiene(Id.alquino)) // Solo cabe un sustituyente en cada carbono (como C(NO2)≡CCl)
                es_redundante = true;
            else { // Hay más de un sustituyente, no es alquino y la función no es alqueno o alquino
                List<Sustituyente> sustituyentes = getSustituyentesSinHidrogeno();

                if(sustituyentes.size() == 2) { // Hay dos funciones distintas de alqueno (y alquino)
                    int suma_enlaces = sustituyentes.get(0).getEnlaces() + sustituyentes.get(1).getEnlaces();

                    if(suma_enlaces > 3 || (suma_enlaces > 1 && contiene(Id.alqueno))) // No caben en un solo carbono
                        es_redundante = true;
                    // Ya solo queda que la función sea la de mayor prioridad:
                    else es_redundante = funcion.compareTo(sustituyentes.get(0).getFuncion()) > 0
                            || funcion.compareTo(sustituyentes.get(1).getFuncion()) > 0;
                }
                else es_redundante = false;
            }
        }
        // Derivados del metano:
        else es_redundante = carbonos.size() == 1; // Si tiene más de 3 carbonos, no hay redundancias

        return es_redundante;
    }

    private void construir(List<Carbono> nuevos) {
        carbonos.clear();
        construir();

        if(nuevos.size() > 0) {
            for(Sustituyente sustituyente : nuevos.get(0).getSustituyentes())
                enlazarSustituyente(sustituyente);

            for(int i = 1; i < nuevos.size(); i++) { // A partir del primero
                enlazarCarbono();
                for(Sustituyente sustituyente : nuevos.get(i).getSustituyentes())
                    enlazarSustituyente(sustituyente);
            }
        }
    }

    private void invertirOrden() {
        List<Carbono> inversos = new ArrayList<>(carbonos);

        Collections.reverse(inversos);
        construir(inversos);
    }

    private void corregirOrden() {
        // ...
    }

    private void descomponerAldehidoNoPrincipalEn(Carbono carbono) { // COOH-CHO → COOH-CH(O)
        if(carbono.contiene(Id.aldehido)) {
            carbono.eliminarSustituyenteConEnlaces(Id.aldehido);
            carbono.enlazarSustituyente(Id.cetona);
            carbono.enlazarSustituyente(Id.hidrogeno);
        }
    }

    private void descomponerAldehidoNoPrincipal() { // COOH-CHO → COOH-CH(O)
        if(getFunciones().get(0) != Id.aldehido) { // Hay otra función de mayor prioridad
            descomponerAldehidoNoPrincipalEn(carbonos.get(0));
            descomponerAldehidoNoPrincipalEn(getUltimo());
        }
    }

    private void sustituirTerminalDePorEn(Id terminal, Carbono carbono, Id funcion, Carbono otro) { // CX-C≡ → C(CX)≡
        if(carbono.contiene(terminal)) {
            carbonos.remove(carbono);
            otro.eliminarEnlace();
            otro.enlazarSustituyente(funcion);
        }
    }

    private void sustituirTerminalPor(Id terminal, Id funcion) { // CH2-C(A)- → CH2(CA)-
        if(contiene(terminal) && getFunciones().get(0) != terminal) { // Hay otra función de mayor prioridad
            sustituirTerminalDePorEn(terminal, carbonos.get(0), funcion, carbonos.get(1));
            sustituirTerminalDePorEn(terminal, getUltimo(), funcion, carbonos.get(carbonos.size() - 1));
        }
    }

    private void sustituirCetonaConPorEn(Id complementaria, Id sustituta, Carbono terminal) { // C(O)(A)- → C(B)-
        if(terminal.contiene(Id.cetona) && terminal.contiene(complementaria)) {
            terminal.eliminarSustituyenteConEnlaces(Id.cetona);
            terminal.eliminarSustituyenteConEnlaces(complementaria);
            terminal.enlazarSustituyente(sustituta);
        }
    }

    private void sustituirCetonaConPor(Id complementaria, Id sustituta) { // C(O)(A)- → C(B)-
        if(getFunciones().get(0).compareTo(sustituta) >= 0) { // La función sustituta no se ve opacada
            sustituirCetonaConPorEn(complementaria, sustituta, carbonos.get(0));
            sustituirCetonaConPorEn(complementaria, sustituta, getUltimo());
        }
    }

    private void corregirRadicalesPorLaIzquierda() { // CH2(CH3)-C≡ → CH3-CH2-C≡
        boolean hubo_correcion; // Para actualizar el iterador tras iteración

        for (int i = 0; i < carbonos.size(); i = hubo_correcion ? 0 : i + 1) { // Sin incremento
            if(carbonos.get(i).getSustituyentesTipo(Id.radical).size() > 0) { // Este carbono tiene radicales
                // Se obtiene el mayor radical de este carbono:
                Sustituyente mayor_radical = carbonos.get(i).getMayorRadical();

                // Se calcula si el "camino" por este radical es preferible a la cadena principal:
                int comparacion = Integer.compare(mayor_radical.getCarbonosRectos(), i);

                if(comparacion == 1 || (comparacion == 0 && mayor_radical.getIso())) {
                    // Se corrige la cadena por la izquierda:
                    if(i != 0) {
                        // Se convierte el camino antiguo de la cadena principal en radical:
                        Sustituyente antiguo;

                        if(i > 1 && carbonos.get(1).contiene(Id.radical) // Hay un radical en el segundo carbono
                                && carbonos.get(1).getSustituyentesSinHidrogeno().get(0).getCarbonos() == 1) {
                            antiguo = new Sustituyente(i + 1, true); // Y es metil
                        }
                        else antiguo = new Sustituyente(i);

                        // Se enlaza tal radical:
                        carbonos.get(i).enlazarSustituyente(antiguo);

                        // Se elimina el radical que será el camino de la cadena principal:
                        carbonos.get(i).eliminarSustituyenteConEnlaces(mayor_radical);

                        // Se elimina el camino antiguo de la cadena principal:
                        carbonos.subList(0, i).clear();
                    }
                    else carbonos.get(0).eliminarSustituyente(mayor_radical); // Será el camino de la cadena principal

                    // Se convierte el radical en el nuevo camino de la cadena principal:
                    List<Carbono> nuevo = mayor_radical.getCadenaRadical();
                    nuevo.addAll(carbonos);
                    construir(nuevo);

                    hubo_correcion = true;
                }
                else hubo_correcion = false;
            }
            else hubo_correcion = false;

            // Se comprueba si este carbono no podría estar en un radical, ergo debe pertenecer a la cadena principal:
            List<Sustituyente> sustituyentes = carbonos.get(i).getSustituyentesSinHidrogeno(); // (Se puede asumir que
            // los carbonos anteriores sí podían estar en un radical, debido a los 'break')

            if(sustituyentes.size() > 0) // Hay sustituyentes distintos del hidrógeno
                if(!(i == 1 && sustituyentes.size() == 1 && sustituyentes.get(0).getCarbonos() == 1))
                    break; // Y estos no son un solo metil en el segundo carbono (no podría formar un radical 'iso')
                else if(carbonos.get(i).getEnlacesLibres() > 0)
                    break; // Le sigue un alqueno o alquino
        }
    }

    private void corregirRadicales() { // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
        corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
        if(contiene(Id.radical)) { // Para ahorrar la inversión de la cadena
            invertirOrden(); // En lugar de corregirlos por la derecha
            corregirRadicalesPorLaIzquierda();
        }
    }

    // Interfaz:

    public boolean estaCompleta() {
        return getEnlacesLibres() == 0;
    }

    public void corregir() {
        if(estaCompleta() && getFunciones().size() > 0) {
            // Se corrigen los radicales que podrían formar parte de la cadena principal:
            corregirRadicales(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3

            // Cetona con alcohol → ácido (siempre puede ser principal):
            sustituirCetonaConPor(Id.alcohol, Id.acido); // C(O)(OH)- → COOH-

            // Cetona con amina → amida de poder ser principal:
            sustituirCetonaConPor(Id.amina, Id.amida); // C(O)(NH2)- → CONH2-

            // Cetona con hidrógeno → aldehído de poder ser principal:
            sustituirCetonaConPor(Id.hidrogeno, Id.aldehido); // CH(O)- → CHO-

            // Amida no principal → carbamoil del anterior:
            sustituirTerminalPor(Id.amida, Id.carbamoil); // CONH2-COOH → C(OOH)(CONH2)

            // Nitrilos no principal → cianuro del anterior:
            sustituirTerminalPor(Id.nitrilo, Id.cianuro); // CN-COOH → C(OOH)(CN)

            // Descompone los aldehídos no principales en cetonas con hidrógeno:
            descomponerAldehidoNoPrincipal(); // COOH-CHO → COOH-CH(O)

            // Corrige el orden de la molécula según la prioridad y los localizadores:
            corregirOrden(); // butan-3-ol → butan-2-ol
        }
    }

    public List<Id> sustituyentesDisponibles() {
        List<Id> disponibles = new ArrayList<>();

        switch(getEnlacesLibres()) {
            case 4:
            case 3:
                disponibles.add(Id.acido);
                disponibles.add(Id.amida);
                disponibles.add(Id.nitrilo);
                disponibles.add(Id.aldehido);
            case 2:
                disponibles.add(Id.cetona);
            case 1:
                disponibles.add(Id.alcohol);
                disponibles.add(Id.amina);
                disponibles.add(Id.nitro);
                disponibles.add(Id.bromo);
                disponibles.add(Id.cloro);
                disponibles.add(Id.fluor);
                disponibles.add(Id.yodo);
                disponibles.add(Id.radical);
                disponibles.add(Id.hidrogeno);
                // Hasta aquí
                break;
        }

        return disponibles;
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
        getUltimo().enlazarSustituyente(funcion);
    }

    public void enlazarSustituyente(Sustituyente sustituyente, int veces) {
        getUltimo().enlazarSustituyente(sustituyente, veces);
    }

    public void enlazarSustituyente(Id funcion, int veces) {
        getUltimo().enlazarSustituyente(funcion, veces);
    }

    // Texto:

    public String getNombre() {
        String nombre;

        if(estaCompleta())
            nombre = "prueba";
        else nombre = "";

        return nombre;
    }

    public String getFormula() {
        StringBuilder formula = new StringBuilder();

        Carbono anterior = carbonos.get(0);
        formula.append(anterior); // Como CH

        for(int i = 1; i < carbonos.size(); i++) {
            formula.append(enlaceDeOrden(anterior.getEnlacesLibres() + 1)); // Como CH=
            formula.append(carbonos.get(i)); // Como CH=CH
            anterior = carbonos.get(i);
        }

        int enlaces_ultimo = getUltimo().getEnlacesLibres();
        if(enlaces_ultimo != 4)
            formula.append(enlaceDeOrden(enlaces_ultimo)); // Como CH=CH-

        return formula.toString();
    }

    @Override
    public String toString() {
        return getFormula();
    }

}
