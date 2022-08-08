package organico.interfaz.aleatorio;

import organico.componentes.Id;
import organico.componentes.Sustituyente;
import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.tipos.Simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AleatorioSimple {

	private static List<Integer> secuencia;

	public static void main(String[] args) {
		while(true) {
			Simple simple = getSimpleAleatorio();

			simple.corregir(); // Es necesario
			String nombre = simple.getNombre();

			OpsinResultado opsin_resultado = Opsin.procesarNombreES(nombre);

			if(opsin_resultado.getSmiles() == null) {
				System.out.print("Secuencia BUG:");
				for(int eleccion : secuencia)
					System.out.print(" " + eleccion);
				System.out.println();
			}
		}
	}

	private static Simple getSimpleAleatorio() {
		Simple simple = new organico.tipos.Simple();

		List<Integer> elecciones = new ArrayList<>();
		boolean primer_carbono = true;
		while(!simple.estaCompleta()) {
			List<Id> disponibles = simple.getSustituyentesDisponibles();

			int eleccion = ThreadLocalRandom.current().nextInt(0, 2) == 0 && !primer_carbono
					? 0 : (ThreadLocalRandom.current().nextInt(0, 2) == 0
					? ThreadLocalRandom.current().nextInt(1, disponibles.size()) : disponibles.size() - 1);
			elecciones.add(eleccion);

			if(eleccion == 0 && !primer_carbono)
				simple.enlazarCarbono();
			else if(disponibles.get(eleccion - 1) != Id.radical)
				simple.enlazar(disponibles.get(eleccion - 1));
			else {
				int carbonos = ThreadLocalRandom.current().nextInt(3, 5);
				elecciones.add(carbonos);

				simple.enlazar(new Sustituyente(carbonos, eleccion == 1));
			}

			if(primer_carbono)
				primer_carbono = false;
		}

		secuencia = elecciones;

		return simple;
	}

}
