package organico.componentes;

import java.util.*;
import java.util.stream.Collectors;

public class Atomo {

	private final Integer id;
	private final Atomos tipo;
	private final List<Atomo> enlazados;

	// Constantes:

	public static final Atomo H = new Atomo(Atomos.H);
	public static final Atomo N = new Atomo(Atomos.N);
	public static final Atomo O = new Atomo(Atomos.O);
	public static final Atomo Br = new Atomo(Atomos.Br);
	public static final Atomo Cl = new Atomo(Atomos.Cl);
	public static final Atomo F = new Atomo(Atomos.F);
	public static final Atomo I = new Atomo(Atomos.I);

	public static final Atomo NH2 = new Atomo(Atomos.N, List.of(H, H));
	public static final Atomo OH = new Atomo(Atomos.O, List.of(H));
	public static final Atomo NO2 = new Atomo(Atomos.N, List.of(O, O));

	// Constructor:

	public Atomo(int id, String simbolo) {
		this.id = id;
		enlazados = new ArrayList<>();

		switch(simbolo) {
			case "C":
				tipo = Atomos.C;
				break;
			case "H":
				tipo = Atomos.H;
				break;
			case "N":
				tipo = Atomos.N;
				break;
			case "O":
				tipo = Atomos.O;
				break;
			case "Br":
				tipo = Atomos.Br;
				break;
			case "Cl":
				tipo = Atomos.Cl;
				break;
			case "F":
				tipo = Atomos.F;
				break;
			case "I":
				tipo = Atomos.I;
				break;
			default:
				throw new IllegalArgumentException("No se contempla el átomo: " + simbolo);
		}
	}

	public Atomo(Atomos tipo, List<Atomo> enlazados) {
		id = null;
		this.tipo = tipo;
		this.enlazados = enlazados;
	}

	private Atomo(Atomos tipo) {
		id = null;
		this.tipo = tipo;
		enlazados = new ArrayList<>();
	}

	private Atomo(Atomo otro) {
		id = otro.id;
		tipo = otro.tipo;
		enlazados = new ArrayList<>(otro.enlazados);
	}

	// Modificadores:

	public void enlazar(Atomo otro) {
		enlazados.add(otro);
	}

	// Consultas:

	public boolean esTipo(Atomos tipo) {
		return this.tipo == tipo;
	}

	public boolean todosSusEnlazadosSonTipo(Atomos tipo) {
		for(Atomo enlazado : enlazados)
			if(!enlazado.esTipo(tipo))
				return false;

		return true;
	}

	public boolean esOxigenoPuente() {
		return esTipo(Atomos.O) && getCantidadDeEnlazados() == 2 && todosSusEnlazadosSonTipo(Atomos.C); // C-O-C
	}

	@Override
	public boolean equals(Object otro) {
		boolean es_igual;

		if(otro != null && otro.getClass() == this.getClass()) {
			Atomo nuevo = (Atomo) otro;

			if(Objects.equals(id, nuevo.id)) { // Nota: Objects.equals(null, null) = true
				if(tipo == nuevo.tipo && enlazados.size() == nuevo.enlazados.size()) {
					es_igual = true;

					List<Atomo> separados = getEnlazadosSeparados();
					List<Atomo> nuevos_separados = nuevo.getEnlazadosSeparados();

					for(int i = 0; i < separados.size(); i++)
						if(!separados.get(i).equals(nuevos_separados.get(i))) {
							es_igual = false;
							break;
						}
				}
				else es_igual = false;
			}
			else es_igual = false;
		}
		else es_igual = false;

		return es_igual;
	}

	// Internos:

	private List<Atomo> getEnlazadosSeparados(List<Atomo> por_separar) {
		List<Atomo> enlazados_separados = new ArrayList<>();

		for(Atomo enlazado : por_separar) {
			Atomo nuevo = new Atomo(enlazado);
			nuevo.enlazados.removeIf(otro -> Objects.equals(id, otro.id));
			enlazados_separados.add(nuevo);
		}

		return enlazados_separados;
	}

	// Métodos get:

	public int getCantidadDeEnlazados() {
		return enlazados.size();
	}

	public int getCantidadDe(Atomos tipo) {
		int cantidad = 0;

		for(Atomo enlazado : enlazados)
			if(enlazado.esTipo(tipo))
				cantidad++;

		return cantidad;
	}

	public List<Atomo> getEnlazadosCarbonos() {
		return enlazados.stream().filter(enlazado -> enlazado.esTipo(Atomos.C)).collect(Collectors.toList());
	}

	public List<Atomo> getEnlazadosSinCarbonos() {
		return enlazados.stream().filter(enlazado -> !enlazado.esTipo(Atomos.C)).collect(Collectors.toList());
	}

	public List<Atomo> getEnlazadosSeparados() {
		return getEnlazadosSeparados(enlazados);
	}

	public List<Atomo> getEnlazadosCarbonosSeparados() {
		return getEnlazadosSeparados(getEnlazadosCarbonos());
	}

	public Optional<Funciones> toFuncion() {
		Optional<Funciones> funcion;

		if(equals(N))
			funcion = Optional.of(Funciones.nitrilo);
		else if(equals(O))
			funcion = Optional.of(Funciones.cetona);
		else if(equals(OH))
			funcion = Optional.of(Funciones.alcohol);
		else if(equals(NH2))
			funcion = Optional.of(Funciones.amina);
		else if(equals(NO2))
			funcion = Optional.of(Funciones.nitro);
		else if(equals(Br))
			funcion = Optional.of(Funciones.bromo);
		else if(equals(Cl))
			funcion = Optional.of(Funciones.cloro);
		else if(equals(F))
			funcion = Optional.of(Funciones.fluor);
		else if(equals(I))
			funcion = Optional.of(Funciones.yodo);
		else if(equals(H))
			funcion = Optional.of(Funciones.hidrogeno);
		else funcion = Optional.empty();

		return funcion;
	}

	// Getters y setters:

	public Integer getId() {
		return id;
	}

	public Atomos getTipo() {
		return tipo;
	}

	public List<Atomo> getEnlazados() {
		return enlazados;
	}

}
