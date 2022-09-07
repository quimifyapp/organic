package interfaz.nombrar;

import organico.componentes.Funciones;
import organico.componentes.Sustituyente;
import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.tipos.Simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class NombrarSimpleAleatorio {

	private static List<Integer> secuencia;

	public static void main(String[] args) {
		while(true) {
			Simple simple = getSimpleAleatorio();

			simple.corregir(); // Es necesario
			String nombre = simple.getNombre();

			simple.corregir(); // No debería alterar nada esta vez
			String nombre2 = simple.getNombre();

			Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);

			if(opsin_resultado.isEmpty() || !nombre.equals(nombre2)) {
				System.out.print("Secuencia BUG:");
				for(int eleccion : secuencia)
					System.out.print(" " + eleccion);
				System.out.println();
			}
		}
	}

	private static Simple getSimpleAleatorio() {
		Simple simple = new organico.tipos.Simple();
		simple.enlazarCarbono();

		List<Integer> elecciones = new ArrayList<>();
		boolean primer_carbono = true;

		while(!simple.estaCompleta()) {
			List<Funciones> disponibles = simple.getSustituyentesDisponibles();

			int eleccion = ThreadLocalRandom.current().nextInt(0, 2) == 0 && !primer_carbono
					? 0
					: (ThreadLocalRandom.current().nextInt(0, 2) == 0
						? ThreadLocalRandom.current().nextInt(1, disponibles.size())
						: disponibles.size() - 1);
			elecciones.add(eleccion);

			if(eleccion == 0 && !primer_carbono)
				simple.enlazarCarbono();
			else if(disponibles.get(eleccion - 1) != Funciones.radical)
				simple.enlazar(disponibles.get(eleccion - 1));
			else {
				int carbonos = ThreadLocalRandom.current().nextInt(3, 5);
				if(eleccion == 1)
					elecciones.add(1);
				else elecciones.add(0);
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
