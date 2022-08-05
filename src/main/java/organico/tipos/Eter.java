package organico.tipos;

import organico.Organico;
import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.List;


// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Eter extends Organico {

	private final List<Carbono> primaria; // R
	private boolean hay_eter; // -O-
	private final List<Carbono> secundaria; // R'
	private List<Carbono> cadena; // Apunta a la cadena que se está formando (primarios | secundarios)

	// Constructores:

	public Eter() {
		primaria = getCarbonos();
		secundaria = new ArrayList<>();

		cadena = primaria;
		cadena.add(new Carbono(0)); // (C)

		hay_eter = false;
	}

	// Internos:

	@Override
	protected Carbono getUltimo() {
		return cadena.get(cadena.size() - 1);
	}

	private void empezarCadenaSecundaria() {
		cadena = secundaria;
		cadena.add(new Carbono(1)); // (-C)

		hay_eter = true;
	}

	private boolean esRedundante(Id funcion) {
		return false;
	}

	// Interfaz:

	public List<Id> getSustituyentesDisponibles() {
		List<Id> disponibles = new ArrayList<>();

		switch(getEnlacesLibres()) {
			case 4: // El primer carbono
			case 3:
			case 2:
			case 1:
				if(!hay_eter && getEnlacesLibres() == 1)
					disponibles.add(Id.eter); // Los éteres admiten funciones de prioridad menor al éter
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
		// ...
	}

	@Override
	public void enlazarCarbono() {
		Carbono ultimo = getUltimo();
		ultimo.enlazarCarbono();
		cadena.add(new Carbono(ultimo.getEnlacesLibres() + 1));
	}

	@Override
	public void enlazarSustituyente(Sustituyente sustituyente) {
		getUltimo().enlazarSustituyente(sustituyente);

		if(sustituyente.esTipo(Id.eter))
			empezarCadenaSecundaria();
	}

	// Texto:

	private Localizador getPrefijoParaEn(Id funcion, List<Carbono> cadena) {
		Localizador prefijo;

		List<Integer> posiciones = getPosicionesDeEn(funcion, cadena);
		String nombre = nombreDePrefijo(funcion);

		if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
			prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
		else if(esHalogeno(funcion) && getSustituyentesUnicos().size() == 1) // Solo hay carbonos y el halógeno
			prefijo = new Localizador("per", nombre); // Como "perfluoro"
		else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

		return prefijo;
	}

	private String getEnlaceParaEn(Id tipo, List<Carbono> cadena) {
		String enlace = "";

		List<Integer> posiciones = getPosicionesDeEn(tipo, cadena);
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


	private String getNombreCadena(List<Carbono> cadena) { // TODO: iso, terc
		List<Id> funciones = getFuncionesOrdenadasEn(cadena); // Sin hidrógeno
		int funcion = 0;

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

		List<Sustituyente> radicales = getRadicalesUnicos();
		for(Sustituyente radical : radicales) {
			localizador = new Localizador(getPosicionesDeEn(radical, cadena), nombreDeRadical(radical));

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
		String enlaces = getEnlaceParaEn(Id.alqueno, cadena) + getEnlaceParaEn(Id.alquino, cadena);


		// Se procesa el cuantificador:
		String cuantificador = cuantificadorDe(cadena.size());

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

		formula += formulaDe(primaria);

		if(hay_eter)
			formula += formulaDe(secundaria);

		return formula;
	}

	@Override
	public String toString() {
		return getFormula();
	}

}
