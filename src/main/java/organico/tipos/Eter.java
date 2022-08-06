package organico.tipos;

import organico.Organico;
import organico.componentes.Cadena;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.List;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Eter extends Organico {

	private final Cadena primaria; // R
	private boolean hay_eter; // -O-
	private final Cadena secundaria; // R'

	private Cadena seleccionada; // Apunta a la cadena que se está formando (primarios | secundarios)

	// Constructores:

	public Eter() {
		primaria = new Cadena(0); // (C)
		secundaria = new Cadena(1); // (-C)

		seleccionada = primaria;
		hay_eter = false;
	}

	private void empezarCadenaSecundaria() {
		seleccionada = secundaria;
		hay_eter = true;
	}

	// Interfaz:

	public boolean estaCompleta() {
		return seleccionada.estaCompleta();
	}

	public List<Id> getSustituyentesDisponibles() {
		List<Id> disponibles = new ArrayList<>();

		switch(getEnlacesLibres()) {
			case 4: // El primer carbono
			case 3:
			case 2:
			case 1:
				if(hay_eter || getEnlacesLibres() > 1) {
					disponibles.add(Id.nitro);
					disponibles.add(Id.bromo);
					disponibles.add(Id.cloro);
					disponibles.add(Id.fluor);
					disponibles.add(Id.yodo);
					disponibles.add(Id.radical);
					disponibles.add(Id.hidrogeno);
				}
				else disponibles.add(Id.eter); // Los éteres admiten funciones de prioridad menor al éter
				// Hasta aquí
				break;
		}

		return disponibles;
	}

	public void enlazar(Sustituyente sustituyente) {
		seleccionada.enlazar(sustituyente);

		if(sustituyente.esTipo(Id.eter))
			empezarCadenaSecundaria();
	}

	public void enlazar(Id funcion) {
		enlazar(new Sustituyente(funcion));
	}

	public void enlazarCarbono() {
		seleccionada.enlazarCarbono();
	}

	public void corregir() {
		// ...
	}


	// Modificadores:

	// ...

	// Internos:

	private boolean esRedundante(Id funcion) {
		// ...
		return false;
	}

	// Consultas:

	@Override
	public boolean equals(Object otro) {
		boolean es_igual;

		if(otro != null && otro.getClass() == this.getClass()) {
			Eter nuevo = (Eter) otro;

			es_igual = (primaria.equals(nuevo.primaria) && secundaria.equals(nuevo.secundaria))
					|| (primaria.equals(nuevo.secundaria) && secundaria.equals(nuevo.primaria));
		}
		else es_igual = false;

		return es_igual;
	}

	// Texto:

	////////////////////////////////////
	private Localizador getPrefijoParaEn(Id funcion, Cadena cadena) {
		Localizador prefijo;

		List<Integer> posiciones = cadena.getPosicionesDe(funcion);
		String nombre = nombreDePrefijo(funcion);

		if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
			prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
		else if(esHalogeno(funcion) && cadena.getSustituyentesUnicos().size() == 1) // Solo hay carbonos y el halógeno
			prefijo = new Localizador("per", nombre); // Como "perfluoro"
		else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

		return prefijo;
	}

	private String getEnlaceParaEn(Id tipo, Cadena cadena) {
		String enlace = "";

		List<Integer> posiciones = cadena.getPosicionesDe(tipo);
		String nombre = nombreDeEnlace(tipo);

		if(posiciones.size() > 0) {
			Localizador localizador;

			if(esRedundante(tipo)) // Sobran los localizadores porque son evidentes
				localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
			else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

			String localizador_to_string = localizador.toString();

			if(empiezaPorDigito(localizador_to_string))
				enlace += "-"; // Guión *antes* de los localizadores

			enlace += localizador_to_string;
		}

		return enlace;
	}

	private String getNombreCadena(Cadena cadena) { // TODO: iso, terc
		List<Id> funciones = getFuncionesOrdenadas(); // Sin hidrógeno
		int funcion = 1; // La primera es éter

		// Se procesan los prefijos:
		List<Localizador> prefijos = new ArrayList<>();
		Localizador localizador;

		while(funcion < funciones.size()) {
			if(funciones.get(funcion) != Id.alqueno && funciones.get(funcion) != Id.alquino
					&& funciones.get(funcion) != Id.radical) {
				localizador = getPrefijoParaEn(funciones.get(funcion), cadena);

				if(!localizador.getLexema().equals("")) // TODO: else?
					prefijos.add(localizador);
			}

			funcion++;
		}

		List<Sustituyente> radicales = cadena.getRadicalesUnicos();
		for(Sustituyente radical : radicales) {
			localizador = new Localizador(cadena.getPosicionesDe(radical), nombreDeRadical(radical));

			if(!localizador.getLexema().equals("")) // TODO: else?
				prefijos.add(localizador);
		}

		StringBuilder prefijo = new StringBuilder(cadena.contiene(Id.acido) ? "ácido " : "");
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
		String enlaces = getEnlaceParaEn(Id.alqueno, cadena) + getEnlaceParaEn(Id.alquino, cadena);

		// Se procesa el cuantificador:
		String cuantificador = cuantificadorDe(cadena.getSize());

		if(!enlaces.equals("") && Organico.noEmpiezaPorVocal(enlaces))
			cuantificador += "a";

		return prefijo + cuantificador + enlaces + "il";
	}

	public String getNombre() {
		String nombre;

		// TODO: si las dos son iguales

		String nombre_primaria = getNombreCadena(primaria);
		String nombre_secundaria = getNombreCadena(secundaria);

		// TODO: orden alfabético

		if(nombre_primaria.equals(nombre_secundaria))
			nombre = (empiezaPorDigito(nombre_primaria) ? "di " : "di") + nombre_primaria + " éter";
		else nombre = nombre_primaria + " " + nombre_secundaria + " éter";

		return nombre;
	}

	public String getFormula() {
		String formula = "";

		formula += primaria.getFormula();

		if(hay_eter)
			formula += secundaria.getFormula();

		return formula;
	}
	////////////////////////////////////

	// Alias:

	private void invertirOrden() {
		seleccionada.invertirOrden();
	}

	private int getSize() {
		return seleccionada.getSize();
	}

	private int getEnlacesLibres() {
		return seleccionada.getEnlacesLibres();
	}

	private boolean hayFunciones() { // Sin hidrógeno
		return seleccionada.hayFunciones();
	}

	private boolean contiene(Id funcion) {
		return seleccionada.contiene(funcion);
	}

	private Id getFuncionPrioritaria() {
		return seleccionada.getFuncionPrioritaria();
	}

	private List<Id> getFuncionesOrdenadas() {
		return seleccionada.getFuncionesOrdenadas();
	}

	private List<Integer> getPosicionesDe(Id funcion) {
		return seleccionada.getPosicionesDe(funcion);
	}

	private List<Integer> getPosicionesDe(Sustituyente sustituyente) {
		return seleccionada.getPosicionesDe(sustituyente);
	}

	private List<Sustituyente> getRadicales() {
		return seleccionada.getRadicales();
	}

	private List<Sustituyente> getRadicalesUnicos() {
		return seleccionada.getRadicalesUnicos();
	}

	private List<Sustituyente> getSustituyentesUnicos() {
		return seleccionada.getSustituyentesUnicos();
	}

	private List<Sustituyente> getSustituyentesSinHidrogeno() {
		return seleccionada.getSustituyentesSinHidrogeno();
	}

	@Override
	public String toString() {
		return getFormula();
	}

}
