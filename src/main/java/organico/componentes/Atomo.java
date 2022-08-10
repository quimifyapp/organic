package organico.componentes;

import java.util.HashSet;
import java.util.Set;

public class Atomo {

	private int id;
	private final Atomos tipo;
	private final Set<Atomo> enlazados;

	// Constructor:

	public Atomo(int id, String simbolo) {
		this.id = id;
		enlazados = new HashSet<>();

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

	// Modificadores:

	public void enlazar(Atomo otro) {
		enlazados.add(otro);
	}

	// Consultas:

	public boolean esTipo(Atomos tipo) {
		return this.tipo == tipo;
	}

	public int cantidadDe(Atomos tipo) {
		int cantidad = 0;

		for(Atomo enlazado : enlazados)
			if(enlazado.esTipo(tipo))
				cantidad++;

		return cantidad;
	}

	// Getters y setters:

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Atomos getTipo() {
		return tipo;
	}

	public Set<Atomo> getEnlazados() {
		return enlazados;
	}

}
