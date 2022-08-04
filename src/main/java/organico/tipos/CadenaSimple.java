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

    // Constructores:

    private void construir() {
        carbonos.add(new Carbono(0)); // (C)
    }

    private void copiar(List<Carbono> nuevos) {
        carbonos.clear();
        carbonos.addAll(nuevos);
    }

    private void copiar(CadenaSimple nueva) {
        copiar(nueva.carbonos);
    }

    public CadenaSimple() {
        construir();
    }

    public CadenaSimple(CadenaSimple nueva) {
        copiar(nueva);
    }

    // Internos:

    private Carbono getUltimo() {
        return carbonos.get(carbonos.size() - 1);
    }

    private int getEnlacesLibres() {
        return getUltimo().getEnlacesLibres();
    }

    private boolean esRedundante(Id funcion) {
        boolean es_redundante;

        // Derivados del propano:
        if(carbonos.size() == 3) {
            if(funcion == Id.cetona && getFuncionPrioritaria().compareTo(Id.aldehido) > 0) // Es propanona
                es_redundante = true;
                // Es propeno | propadieno | propino:
            else es_redundante = hayFunciones() && (funcion == Id.alqueno || funcion == Id.alquino);
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
                    else es_redundante = funcion.equals(sustituyentes.get(0).getFuncion()); // Es la prioritaria (orden)
                }
                else es_redundante = false;
            }
        }
        // Derivados del metano:
        else if(carbonos.size() == 1)
            es_redundante = true;
        else if(funcion != Id.radical && funcion != Id.alqueno && funcion != Id.alquino)
            es_redundante = new Sustituyente(funcion).getEnlaces() == 3; // Solo puede ser terminal
        else es_redundante = false;

        return es_redundante;
    }

    // Modificadores:

    private void invertirOrden() {
        List<Carbono> inversos = new ArrayList<>(carbonos);
        Collections.reverse(inversos);

        carbonos.clear();
        construir();

        // Se vuelve a formar la cadena en el orden natural para preservar los enlaces (si no, estarían al revés):
        if(inversos.size() > 0) {
            for(Sustituyente sustituyente : inversos.get(0).getSustituyentes())
                enlazarSustituyente(sustituyente);

            for(int i = 1; i < inversos.size(); i++) { // A partir del primero
                enlazarCarbono();
                for(Sustituyente sustituyente : inversos.get(i).getSustituyentes())
                    enlazarSustituyente(sustituyente);
            }
        }
    }

    private boolean corregirPorSegun(CadenaSimple inversa, int comparacion) {
        boolean corregido;

        if(comparacion != 0) { // No son iguales
            if(comparacion > 0) // El inverso va antes alfabéticamente
                copiar(inversa);

            corregido = true; // Ya se ha corregido el orden según los radicales alfabéticamente
        }
        else corregido = false; // Indecidible

        return corregido;
    }

    private void corregirOrden() {
        boolean corregido = false;

        CadenaSimple inversa = new CadenaSimple(this);
        inversa.invertirOrden();

        List<Id> funciones = getFuncionesOrdenadas();
        for(int i = 0; i < funciones.size() && !corregido; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = inversa.getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corregido = corregirPorSegun(inversa, suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if(!corregido && contiene(Id.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:
            List<String> normales = new ArrayList<>();
            getRadicales().forEach(radical -> normales.add(Organico.nombreRadical(radical)));

            List<String> inversos = new ArrayList<>();
            inversa.getRadicales().forEach(radical -> inversos.add(Organico.nombreRadical(radical)));

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for(int i = 0; i < normales.size() && !corregido; i++)
                corregido = corregirPorSegun(inversa, normales.get(i).compareTo(inversos.get(i)));
        }
    }

    private void descomponerAldehidoNoPrincipalEn(Carbono carbono) { // COOH-CHO → COOH-CH(O)
        if(carbono.contiene(Id.aldehido)) {
            carbono.eliminarSustituyenteConEnlaces(Id.aldehido);
            carbono.enlazarSustituyente(Id.cetona);
            carbono.enlazarSustituyente(Id.hidrogeno);
        }
    }

    private void descomponerAldehidoNoPrincipal() { // COOH-CHO → COOH-CH(O)
        if(getFuncionPrioritaria() != Id.aldehido) { // Hay otra de mayor prioridad, se debe descomponer el aldehído
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

    private void sustituirTerminalPor(Id terminal, Id funcion) { // COOH-C(A)- → COOH(CA)-
        if(getFuncionPrioritaria() != terminal) { // Hay una función de mayor prioridad, se debe descomponer el terminal
            sustituirTerminalDePorEn(terminal, carbonos.get(0), funcion, carbonos.get(1));
            sustituirTerminalDePorEn(terminal, getUltimo(), funcion, carbonos.get(carbonos.size() - 2));
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
        sustituirCetonaConPorEn(complementaria, sustituta, carbonos.get(0));
        sustituirCetonaConPorEn(complementaria, sustituta, getUltimo());
    }

    private void componerAldehido() {
        if(getFuncionPrioritaria().compareTo(Id.aldehido) >= 0) // No hay otra de mayor prioridad, puede haber aldehídos
            sustituirCetonaConPor(Id.hidrogeno, Id.aldehido);
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

                        Sustituyente metil = new Sustituyente(1); // Para hacer el código más legible
                        if(i > 1 && carbonos.get(1).contiene(Id.radical) // Hay un radical en el segundo carbono
                                && carbonos.get(1).getSustituyentesSinHidrogeno().get(0).equals(metil)) // Y es metil
                            antiguo = new Sustituyente(i + 1, true);
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
                    List<Carbono> parte_izquierda = mayor_radical.getRadical();
                    parte_izquierda.addAll(carbonos);

                    // Se efectúa el cambio:
                    copiar(parte_izquierda);
                    hubo_correcion = true;
                }
                else hubo_correcion = false;
            }
            else hubo_correcion = false;

            // Se comprueba si este carbono no podría estar en un radical, ergo debe pertenecer a la cadena principal:
            List<Sustituyente> sustituyentes = carbonos.get(i).getSustituyentesSinHidrogeno(); // (Se puede asumir que
            // los carbonos anteriores sí podían estar en un radical, debido a los 'break')

            if(sustituyentes.size() > 0) { // Hay sustituyentes distintos del hidrógeno
                if(!(i == 1 && sustituyentes.size() == 1 && sustituyentes.get(0).getCarbonos() == 1))
                    break; // Y estos no son un solo metil en el segundo carbono (no podría formar un radical 'iso')
                else if(carbonos.get(i).getEnlacesLibres() > 0)
                    break; // Le sigue un alqueno o alquino
            }
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

    public void corregir() {
        if(estaCompleta() && getFuncionesOrdenadas().size() > 0) {
            // Radicales:

            // Se corrigen los radicales que podrían formar parte de la cadena principal:
            corregirRadicales(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3

            // Composición:

            // Cetona con hidrógeno → aldehído de poder ser principal:
            componerAldehido();

            // Cetona con alcohol → ácido (siempre puede ser principal):
            sustituirCetonaConPor(Id.alcohol, Id.acido); // C(O)(OH)- → COOH-

            // Cetona con amina → amida de poder ser principal:
            sustituirCetonaConPor(Id.amina, Id.amida); // C(O)(NH2)- → CONH2-

            // Descomposición:

            // Aldehído no principal → cetona con hidrógeno:
            descomponerAldehidoNoPrincipal(); // COOH-CHO → COOH-CH(O)

            // Amida no principal → carbamoil del anterior:
            sustituirTerminalPor(Id.amida, Id.carbamoil); // CONH2-COOH → C(OOH)(CONH2)

            // Nitrilos no principal → cianuro del anterior:
            sustituirTerminalPor(Id.nitrilo, Id.cianuro); // CN-COOH → C(OOH)(CN)

            // Orden:

            // Corrige el orden de la molécula según la prioridad y los localizadores:
            corregirOrden(); // butan-3-ol → butan-2-ol
        }
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

    private Localizador getPrefijoPara(Id funcion) {
        Localizador prefijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombrePrefijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            prefijo = new Localizador(multiplicador(posiciones.size()), nombre); // Como "difluoro"
        else if(esHalogeno(funcion) && getSustituyentesUnicos().size() == 1) // Solo hay carbonos y el halógeno
            prefijo = new Localizador("per", nombre); // Como "perfluoro"
        else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

        return prefijo;
    }

    private String getEnlacePara(Id tipo) {
        String enlace = "";

        List<Integer> posiciones = getPosicionesDe(tipo);
        String nombre = nombreEnlace(tipo);

        if(posiciones.size() > 0) {
            Localizador localizador;

            if(esRedundante(tipo)) // Sobran los localizadores porque son evidentes
                localizador = new Localizador(multiplicador(posiciones.size()), nombre); // Como "dien"
            else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

            String localizador_to_string = localizador.toString();

            if(empiezaPorDigito(localizador_to_string))
                enlace += "-"; // Guión antes de los localizadores

            enlace += localizador_to_string;
        }

        return enlace;
    }

    private String getSufijoPara(Id funcion) {
        String sufijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombreSufijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            sufijo = multiplicador(posiciones.size()) + nombre; // Como "dioico"
        else sufijo = new Localizador(posiciones, nombre).toString(); // Como "2-3-diona"

        return sufijo;
    }

    public String getNombre() { // Se asume que ya la CadenaSimple está corregida con corregir()
        if(carbonos.size() == 1 && carbonos.get(0).getCantidadDe(Id.cetona) == 2) // Caso excepcional
            return "dióxido de carbono"; // CO2

        List<Id> funciones = getFuncionesOrdenadas();
        int funcion = 0;

        // Se procesa el sufijo:
        String sufijo;
        if(funciones.size() > 0 && !esHalogeno(funciones.get(0)) // Nunca son sufijos
                && funciones.get(0) != Id.alqueno && funciones.get(0) != Id.alquino
                && funciones.get(0) != Id.nitro && funciones.get(0) != Id.radical)
            sufijo = getSufijoPara(funciones.get(funcion++));
        else sufijo = "";

        // Se procesan los prefijos:
        List<Localizador> prefijos = new ArrayList<>();
        Localizador localizador;

        while(funcion < funciones.size()) {
            if(funciones.get(funcion) != Id.alqueno
                    && funciones.get(funcion) != Id.alquino
                    && funciones.get(funcion) != Id.radical) {
                localizador = getPrefijoPara(funciones.get(funcion));

                if(!localizador.getNombre().equals("")) // TODO: else?
                    prefijos.add(localizador);
            }

            funcion++;
        }

        List<Sustituyente> radicales = getRadicalesUnicos();
        for(Sustituyente radical : radicales) {
            localizador = new Localizador(getPosicionesDe(radical), nombreRadical(radical));

            if(!localizador.getNombre().equals("")) // TODO: else?
                prefijos.add(localizador);
        }

        StringBuilder prefijo = new StringBuilder(contiene(Id.acido) ? "ácido " : "");
        if(prefijos.size() > 0) {
            Localizador.ordenarAlfabeticamente(prefijos);

            for(int i = 0; i < prefijos.size() - 1; i++) {
                prefijo.append(prefijos.get(i).toString());

                if(noEmpiezaPorLetra(prefijos.get(i + 1).toString()))
                    prefijo.append("-");
            }

            prefijo.append(prefijos.get(prefijos.size() - 1));
        }

        // Se procesan los enlaces:
        String enlaces = getEnlacePara(Id.alqueno) + getEnlacePara(Id.alquino);

        if(enlaces.equals(""))
            enlaces = "an";
        if(sufijo.equals("") || Organico.noEmpiezaPorVocal(sufijo))
            enlaces += "o";
        if(!sufijo.equals("") && Organico.empiezaPorDigito(sufijo))
            enlaces += "-";

        // Se procesa el cuantificador:
        String cuantificador = cuantificador(carbonos.size());

        if(Organico.noEmpiezaPorVocal(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + sufijo;
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
