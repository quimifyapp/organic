package organico.componentes;

import organico.Organico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cadena extends Organico {

	private final List<Carbono> carbonos;

	// Constructores:

	public Cadena() {
		carbonos = new ArrayList<>();
	}

	public Cadena(int enlaces_previos) {
		carbonos = new ArrayList<>();
		comenzar(enlaces_previos);
	}

	public Cadena(Cadena nueva) {
		carbonos = new ArrayList<>();
		agregarCopiaDe(nueva);
	}

	public Cadena(List<Carbono> nueva) {
		carbonos = new ArrayList<>();
		agregarCopiaDe(nueva);
	}

	private void agregarCopiaDe(List<Carbono> otros) {
		for(Carbono carbono : otros)
			carbonos.add(new Carbono(carbono));
	}

	private void agregarCopiaDe(Cadena otra) {
		agregarCopiaDe(otra.carbonos);
	}

	public void comenzar(int enlaces_previos) {
		carbonos.add(new Carbono(enlaces_previos));
	}

	// Modificadores:

	public void enlazar(Sustituyente sustituyente) {
		getUltimo().enlazar(sustituyente);
	}

	public void enlazar(Id funcion) {
		enlazar(new Sustituyente(funcion));
	}

	private void enlazar(Sustituyente sustituyente, int veces) {
		getUltimo().enlazar(sustituyente, veces);
	}

	public void enlazar(Id funcion, int veces) {
		enlazar(new Sustituyente(funcion), veces);
	}

	private void enlazar(List<Carbono> otra) {
		Carbono ultimo = getUltimo();
		ultimo.enlazarCarbono();
		agregarCopiaDe(otra);
	}

	public void enlazarCarbono() {
		Carbono ultimo = getUltimo();
		ultimo.enlazarCarbono();
		carbonos.add(new Carbono(ultimo.getEnlacesLibres() + 1));
	}

	private void transformarEn(Cadena otra) {
		carbonos.clear();
		agregarCopiaDe(otra);
	}

	public void invertirOrden() {
		transformarEn(getInversa());
	}

	public void corregirRadicalesPorLaIzquierda() { // CH2(CH3)-C≡ → CH3-CH2-C≡
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

						// Aquí se tiene en cuenta que, de haber un radical, solo podría ser metil
						Sustituyente CH3 = new Sustituyente(1); // Sustituyente metil
						if(i > 1 && carbonos.get(1).contiene(Id.radical) // Hay un radical en el segundo carbono
								&& carbonos.get(1).getSustituyentesSinHidrogeno().get(0).equals(CH3)) // Y es metil
							antiguo = new Sustituyente(i + 1, true);
						else antiguo = new Sustituyente(i);

						// Se enlaza tal radical:
						carbonos.get(i).enlazar(antiguo);

						// Se elimina el radical que será el camino de la cadena principal:
						carbonos.get(i).eliminarConEnlaces(mayor_radical);

						// Se elimina el camino antiguo de la cadena principal:
						carbonos.subList(0, i).clear();
					}
					else carbonos.get(0).eliminar(mayor_radical); // Será el camino de la cadena principal

					// Se convierte el radical en el nuevo camino de la cadena principal:
					Cadena parte_izquierda = mayor_radical.getCadena();
					parte_izquierda.enlazar(carbonos);

					// Se efectúa el cambio:
					transformarEn(parte_izquierda);
					hubo_correcion = true;
				}
				else hubo_correcion = false;
			}
			else hubo_correcion = false;

			// Se comprueba si este carbono no podría estar en un radical, ergo debe pertenecer a la cadena principal:
			List<Sustituyente> sustituyentes = carbonos.get(i).getSustituyentesSinHidrogeno(); // (Se puede asumir que los
			// carbonos anteriores sí podían estar en un radical gracias a los 'break')

			if(sustituyentes.size() > 0) { // Hay sustituyentes distintos del hidrógeno
				if(!(i == 1 && sustituyentes.size() == 1 && sustituyentes.get(0).getCarbonos() == 1))
					break; // Y estos no son un solo metil en el segundo carbono (no podría formar un radical 'iso')
				else if(carbonos.get(i).getEnlacesLibres() > 0)
					break; // Le sigue un alqueno o alquino
			}
		}
	}

	public void componerAldehido() {
		if(getFuncionPrioritaria().compareTo(Id.aldehido) >= 0) // No hay otra de mayor prioridad, puede haber aldehídos
			sustituirCetonaConPor(Id.hidrogeno, Id.aldehido);
	}

	public void sustituirCetonaConPor(Id complementaria, Id sustituta) { // C(O)(A)- → C(B)-
		sustituirCetonaConPorEn(complementaria, sustituta, carbonos.get(0));
		sustituirCetonaConPorEn(complementaria, sustituta, getUltimo());
	}

	private void sustituirCetonaConPorEn(Id complementaria, Id sustituta, Carbono terminal) { // C(O)(A)- → C(B)-
		if(terminal.contiene(Id.cetona) && terminal.contiene(complementaria)) {
			terminal.eliminarConEnlaces(Id.cetona);
			terminal.eliminarConEnlaces(complementaria);
			terminal.enlazar(sustituta);
		}
	}

	public void sustituirTerminalPor(Id terminal, Id funcion) { // COOH-C(A)- → COOH(CA)-
		if(getFuncionPrioritaria() != terminal) { // Hay una función de mayor prioridad, se debe descomponer el terminal
			if(carbonos.size() >= 2) // Para poder acceder a cadena.get(1)
				sustituirTerminalDePorEn(terminal, carbonos.get(0), funcion, carbonos.get(1));
			if(carbonos.size() >= 2) // Para poder acceder a cadena.get(cadena.size() - 2)
				sustituirTerminalDePorEn(terminal, getUltimo(), funcion, carbonos.get(carbonos.size() - 2));
		}
	}

	private void sustituirTerminalDePorEn(Id terminal, Carbono carbono, Id funcion, Carbono otro) { // CX-C≡ → C(CX)≡
		if(carbono.contiene(terminal)) {
			carbonos.remove(carbono);
			otro.eliminarEnlace();
			otro.enlazar(funcion);
		}
	}

	public void descomponerAldehido() { // COOH-CHO → COOH-CH(O)
		if(getFuncionPrioritaria() != Id.aldehido) { // Hay otra de mayor prioridad, se debe descomponer el aldehído
			descomponerAldehidoEn(carbonos.get(0));
			descomponerAldehidoEn(getUltimo());
		}
	}

	private void descomponerAldehidoEn(Carbono carbono) { // COOH-CHO → COOH-CH(O)
		if(carbono.contiene(Id.aldehido)) {
			carbono.eliminarConEnlaces(Id.aldehido);
			carbono.enlazar(Id.cetona);
			carbono.enlazar(Id.hidrogeno);
		}
	}

	// Consultas:

	public boolean contiene(Id funcion) {
		for(Carbono carbono : carbonos)
			if(carbono.contiene(funcion))
				return true;

		return false;
	}

	public boolean estaCompleta() {
		return getEnlacesLibres() == 0;
	}

	public boolean hayFunciones() { // Sin hidrógeno
		for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
			if(funcion != Id.hidrogeno)
				for(Carbono carbono : carbonos)
					if(carbono.contiene(funcion))
						return true;

		return false;
	}

	@Override
	public boolean equals(Object otra) {
		boolean es_igual = false;

		if(otra != null && otra.getClass() == this.getClass()) {
			Cadena nueva = (Cadena) otra;

			if(carbonos.size() == nueva.getSize())
				for(int i = 0; i < carbonos.size(); i++)
					if(carbonos.get(i).equals(nueva.carbonos.get(i))) {
						es_igual = true;
						break;
					}
		}

		return es_igual;
	}

	// Métodos get:

	public int getSize() {
		return carbonos.size();
	}

	private Cadena getInversa() {
		Cadena inversa = new Cadena(carbonos);

		// Le da la vuelta a los carbonos:
		Collections.reverse(inversa.carbonos);

		// Ajusta los enlaces (no son simétricos):
		if(inversa.getSize() > 1) {
			for(int i = 0, j = carbonos.size() - 2; i < inversa.getSize() - 1; i++)
				inversa.carbonos.get(i).setEnlacesLibres(carbonos.get(j--).getEnlacesLibres());

			inversa.carbonos.get(inversa.getSize() - 1).setEnlacesLibres(0); // Se supone que no tiene enlaces sueltos
		}

		return inversa;
	}

	private Carbono getUltimo() {
		return carbonos.get(carbonos.size() - 1);
	}

	public int getEnlacesLibres() {
		return getUltimo().getEnlacesLibres();
	}

	public int getCantidadDe(Id funcion) {
		int cantidad = 0;

		for(Carbono carbono : carbonos)
			cantidad += carbono.getCantidadDe(funcion);

		return cantidad;
	}

	public Id getFuncionPrioritaria() { // Con hidrógeno
		for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
			for(Carbono carbono : carbonos)
				if(carbono.contiene(funcion))
					return funcion;

		return null;
	}

	public List<Id> getFuncionesOrdenadas() { // Sin hidrógeno ni éter
		List<Id> funciones = new ArrayList<>(); // Funciones presentes sin repetición y en orden

		for(Id funcion : Id.values()) // Todas las funciones recogidas en Id
			if(funcion != Id.hidrogeno && funcion != Id.eter) // Excepto hidrógeno y éter
				for(Carbono carbono : carbonos)
					if(carbono.contiene(funcion)) {
						funciones.add(funcion);
						break;
					}

		return funciones;
	}

	public List<Integer> getPosicionesDe(Id funcion) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos con la función

		for(int i = 0; i < carbonos.size(); i++)
			if(carbonos.get(i).contiene(funcion))
				posiciones.add(i);

		return posiciones;
	}

	public List<Integer> getPosicionesDe(Sustituyente sustituyente) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos enlazados al sustituyente

		for(int i = 0; i < carbonos.size(); i++)
			if(carbonos.get(i).estaEnlazadoA(sustituyente))
				posiciones.add(i);

		return posiciones;
	}

	public List<Sustituyente> getRadicales() {
		List<Sustituyente> sustituyentes = new ArrayList<>();

		for(Carbono carbono : carbonos)
			sustituyentes.addAll(carbono.getSustituyentesTipo(Id.radical));

		return sustituyentes;
	}

	public List<Sustituyente> getRadicalesUnicos() {
		List<Sustituyente> unicos = new ArrayList<>();

		for(Carbono carbono : carbonos)
			for(Sustituyente sustituyente : carbono.getSustituyentesTipo(Id.radical))
				if(!unicos.contains(sustituyente))
					unicos.add(sustituyente);

		return unicos;
	}

	public List<Sustituyente> getSustituyentesUnicos() {
		List<Sustituyente> unicos = new ArrayList<>();

		for(Carbono carbono : carbonos)
			for(Sustituyente sustituyente : carbono.getSustituyentes())
				if(!unicos.contains(sustituyente))
					unicos.add(sustituyente);

		return unicos;
	}

	public List<Sustituyente> getSustituyentesSinHidrogeno() {
		List<Sustituyente> sin_hidrogeno = new ArrayList<>();

		for(Carbono carbono : carbonos)
			sin_hidrogeno.addAll(carbono.getSustituyentesSinHidrogeno());

		return sin_hidrogeno;
	}

	// Texto:

	public String getFormula() {
		StringBuilder formula = new StringBuilder();

		// Se escribe el primero:
		Carbono primero = carbonos.get(0);
		formula.append(primero); // Como CH

		// Se escribe el resto con los enlaces libres del anterior:
		int enlaces_libres_anterior = primero.getEnlacesLibres();
		for(int i = 1; i < carbonos.size(); i++) {
			formula.append(enlaceDeOrden(enlaces_libres_anterior)); // Como CH=
			formula.append(carbonos.get(i)); // Como CH=CH

			enlaces_libres_anterior = carbonos.get(i).getEnlacesLibres();
		}

		// Se escribe los enlaces libres del último:
		if(enlaces_libres_anterior > 0 && enlaces_libres_anterior < 4) // Ni está completo ni es el primero vacío
			formula.append(enlaceDeOrden(enlaces_libres_anterior - 1)); // Como CH=CH-CH2-C≡

		return formula.toString();
	}

	@Override
	public String toString() {
		return getFormula();
	}

}
