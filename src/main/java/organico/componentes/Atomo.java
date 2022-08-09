package organico.componentes;

import java.util.HashSet;
import java.util.Set;

public class Atomo {

	private final int id;
	private final Atomos atomo;
	private final Set<Atomo> enlazados;

	private final boolean es_sufijo;
	private final boolean esta_en_ciclo;

	// Constructor:

	public Atomo(int id, boolean es_sufijo, boolean esta_en_ciclo, String simbolo) {
		enlazados = new HashSet<>();

		this.id = id;
		this.es_sufijo = es_sufijo;
		this.esta_en_ciclo = esta_en_ciclo;

		switch(simbolo) {
			case "C":
				atomo = Atomos.C;
				break;
			case "H":
				atomo = Atomos.H;
				break;
			case "N":
				atomo = Atomos.N;
				break;
			case "O":
				atomo = Atomos.O;
				break;
			case "Br":
				atomo = Atomos.Br;
				break;
			case "Cl":
				atomo = Atomos.Cl;
				break;
			case "F":
				atomo = Atomos.F;
				break;
			case "I":
				atomo = Atomos.I;
				break;
			default:
				throw new IllegalArgumentException("No se contempla el átomo: " + simbolo);
		}
	}

	// Modificadores:

	public void enlazar(Atomo otro) {

	}

}
