package organico.tipos;

import organico.Organico;
import organico.componentes.Cadena;
import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.List;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public final class Simple extends Organico {

    private final Cadena cadena;

    // Constante:

    private static final Cadena CO2 = new Cadena(List.of(new Carbono(Id.cetona, 2)));

    // Constructores:

    public Simple() {
        cadena = new Cadena(0);
    }

    private Simple(Cadena nueva) {
        cadena = new Cadena(nueva);
    }

    // Interfaz:

    public boolean estaCompleta() {
        return cadena.estaCompleta();
    }

    public List<Id> getSustituyentesDisponibles() {
        List<Id> disponibles = new ArrayList<>();

        switch(getEnlacesLibres()) {
            case 4: // El primer carbono
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

    public void enlazar(Sustituyente sustituyente) {
        cadena.enlazar(sustituyente);
    }

    public void enlazar(Id funcion) {
        enlazar(new Sustituyente(funcion));
    }

    public void enlazarCarbono() {
        cadena.enlazarCarbono();
    }

    public void corregir() {
        if(estaCompleta() && hayFunciones()) {
            // Se corrigen los radicales que podrían formar parte de la cadena principal:
            cadena.corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
            if(contiene(Id.radical)) { // Para ahorrar la inversión de la cadena
                invertirOrden(); // En lugar de corregirlos por la derecha
                cadena.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
            }

            // Composición:

            // Cetona con hidrógeno → aldehído de poder ser principal:
            cadena.componerAldehido(); // CH(O)- → C(HO)

            // Cetona con alcohol → ácido:
            cadena.sustituirCetonaConPor(Id.alcohol, Id.acido); // C(O)(OH)- → COOH-

            // Cetona con amina → amida:
            cadena.sustituirCetonaConPor(Id.amina, Id.amida); // C(O)(NH2)- → CONH2-

            // Descomposición:

            // Aldehído no principal → cetona con hidrógeno:
            cadena.descomponerAldehido(); // COOH-CHO → COOH-CH(O)

            // Amida no principal → carbamoil del anterior:
            cadena.sustituirTerminalPor(Id.amida, Id.carbamoil); // CONH2-COOH → C(OOH)(CONH2)

            // Nitrilos no principal → cianuro del anterior:
            cadena.sustituirTerminalPor(Id.nitrilo, Id.cianuro); // CN-COOH → C(OOH)(CN)

            // Orden:

            // Corrige el orden de la molécula según la prioridad y los localizadores:
            corregirOrden(); // butan-3-ol → butan-2-ol
        }
    }

    // Modificadores:

    private void corregirOrden() {
        boolean corregido = false;

        Simple inversa = new Simple(cadena);
        inversa.invertirOrden();

        List<Id> funciones = getFuncionesOrdenadas();
        for(int i = 0; i < funciones.size() && !corregido; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = inversa.getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corregido = corregirOrdenSegun(suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if(!corregido && contiene(Id.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:
            List<String> normales = new ArrayList<>();
            getRadicales().forEach(radical -> normales.add(Organico.nombreDeRadical(radical)));

            List<String> inversos = new ArrayList<>();
            inversa.getRadicales().forEach(radical -> inversos.add(Organico.nombreDeRadical(radical)));

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for(int i = 0; i < normales.size() && !corregido; i++)
                corregido = corregirOrdenSegun(normales.get(i).compareTo(inversos.get(i)));
        }
    }

    private boolean corregirOrdenSegun(int comparacion) {
        boolean corregido;

        if(comparacion != 0) { // No son iguales
            if(comparacion > 0) // El inverso va antes alfabéticamente
                invertirOrden();

            corregido = true; // Ya se ha corregido el orden según los radicales alfabéticamente
        }
        else corregido = false; // Indecidible

        return corregido;
    }

    // Internos:

    private boolean esRedundante(Id funcion) {
        boolean es_redundante;

        // Sustituyentes terminales:
        if(funcion != Id.radical && !(esAlquenoOAlquino(funcion))) // Por: new Sustituyente(funcion)
            es_redundante = new Sustituyente(funcion).getEnlaces() == 3; // Solo puede ir en el primero y/o último
        // Derivados del propeno:
        else if(getSize() == 3)
            es_redundante = funcion == Id.alqueno && getCantidadDe(Id.alqueno) == 2; // Es propadieno
        // Derivados del etano:
        else if(getSize() == 2) {
            if(esAlquenoOAlquino(funcion)) // Solo hay una posición para el enlace
                es_redundante = true;
            else es_redundante = getSustituyentesSinHidrogeno().size() == 1; // Solo hay uno, como cloroetino o etanol
        }
        // Derivados del metano:
        else es_redundante = getSize() == 1;

        return es_redundante;
    }

    // Texto:

    ////////////////////////////////////
    private Localizador getPrefijoPara(Id funcion) {
        Localizador prefijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombreDePrefijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
        else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

        return prefijo;
    }

    private String getEnlacePara(Id tipo) {
        String enlace = "";

        List<Integer> posiciones = getPosicionesDe(tipo);
        String nombre = nombreDeEnlace(tipo);

        if(posiciones.size() > 0) {
            Localizador localizador;

            if(esRedundante(tipo)) // Sobran los localizadores porque son evidentes
                localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
            else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

            String localizador_to_string = localizador.toString();

            if(empiezaPorDigito(localizador_to_string))
                enlace += "-"; // Guion antes de los localizadores

            enlace += localizador_to_string;
        }

        return enlace;
    }

    private String getSufijoPara(Id funcion) {
        String sufijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombreDeSufijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            sufijo = multiplicadorDe(posiciones.size()) + nombre; // Como "dioico"
        else sufijo = new Localizador(posiciones, nombre).toString(); // Como "2-3-diona"

        return sufijo;
    }

    public String getNombre() { // Se asume que ya la CadenaSimple está corregida con corregir()
        if(cadena.equals(CO2)) // Caso excepcional
            return "dióxido de carbono";

        List<Id> funciones = getFuncionesOrdenadas(); // Sin hidrógeno
        int funcion = 0;

        // Se procesa el sufijo:
        String sufijo;
        if(funciones.size() > 0 && funciones.get(0) != Id.nitro && funciones.get(0) != Id.radical // Nunca son sufijos
                && !esHalogeno(funciones.get(0)) && !esAlquenoOAlquino(funciones.get(0)))
            sufijo = getSufijoPara(funciones.get(funcion++));
        else sufijo = "";

        // Se procesan los prefijos:
        List<Localizador> prefijos = new ArrayList<>();

        while(funcion < funciones.size()) {
            if(!esAlquenoOAlquino(funciones.get(funcion)) && funciones.get(funcion) != Id.radical) {
                Localizador localizador = getPrefijoPara(funciones.get(funcion));

                if(!localizador.getLexema().equals("")) // TODO: else?
                    prefijos.add(localizador);
            }

            funcion++;
        }

        List<Sustituyente> radicales = getRadicalesUnicos();
        for(Sustituyente radical : radicales) {
            Localizador localizador = new Localizador(getPosicionesDe(radical), nombreDeRadical(radical));

            if(!localizador.getLexema().equals("")) // TODO: else?
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
        String cuantificador = cuantificadorDe(getSize());

        if(Organico.noEmpiezaPorVocal(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + sufijo;
    }
    ////////////////////////////////////

    public String getFormula() {
        return cadena.getFormula();
    }

    // Alias:

    private void invertirOrden() {
        cadena.invertirOrden();
    }

    private int getSize() {
        return cadena.getSize();
    }

    private int getEnlacesLibres() {
        return cadena.getEnlacesLibres();
    }

    private int getCantidadDe(Id funcion) {
        return cadena.getCantidadDe(funcion);
    }

    private boolean hayFunciones() { // Sin hidrógeno
        return cadena.hayFunciones();
    }

    private boolean contiene(Id funcion) {
        return cadena.contiene(funcion);
    }

    private Id getFuncionPrioritaria() {
        return cadena.getFuncionPrioritaria();
    }

    private List<Id> getFuncionesOrdenadas() {
        return cadena.getFuncionesOrdenadas();
    }

    private List<Integer> getPosicionesDe(Id funcion) {
        return cadena.getPosicionesDe(funcion);
    }

    private List<Integer> getPosicionesDe(Sustituyente sustituyente) {
        return cadena.getPosicionesDe(sustituyente);
    }

    private List<Sustituyente> getRadicales() {
        return cadena.getRadicales();
    }

    private List<Sustituyente> getRadicalesUnicos() {
        return cadena.getRadicalesUnicos();
    }

    private List<Sustituyente> getSustituyentesUnicos() {
        return cadena.getSustituyentesUnicos();
    }

    private List<Sustituyente> getSustituyentesSinHidrogeno() {
        return cadena.getSustituyentesSinHidrogeno();
    }

    @Override
    public String toString() {
        return getFormula();
    }

}
