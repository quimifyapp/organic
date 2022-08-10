package organico.componentes;

import java.util.HashSet;
import java.util.Set;

public class Atomo {

	private int id;
	private final Atomos atomo;
	private final Set<Atomo> enlazados;

	// Constructor:

	public Atomo(int id, String simbolo) {
		this.id = id;
		enlazados = new HashSet<>();

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

	public void enlazarA(Atomo otro) {
		enlazados.add(otro);
	}

	// Getters y setters:

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
