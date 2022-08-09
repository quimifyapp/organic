package organico.interfaz.nombrar;

import organico.componentes.Funciones;
import organico.componentes.Sustituyente;
import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.tipos.Eter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class NombrarEterAleatorio {

	private static List<Integer> secuencia;

	public static void main(String[] args) {
		while(true) {
			Eter eter = getEterAleatorio();

			eter.corregir(); // Es necesario
			String nombre = eter.getNombre();

			Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);

			if(opsin_resultado.isEmpty()) {
				System.out.print("Secuencia BUG:");
				for(int eleccion : secuencia)
					System.out.print(" " + eleccion);
				System.out.println();
			}
		}
	}

	private static Eter getEterAleatorio() {
		Eter eter = new Eter();

		List<Integer> elecciones = new ArrayList<>();
		boolean primer_carbono = true;

		while(!eter.estaCompleta()) {
			List<Funciones> disponibles = eter.getSustituyentesDisponibles();

			int eleccion = ThreadLocalRandom.current().nextInt(0, 5) == 0 && !primer_carbono
					? 0
					: (ThreadLocalRandom.current().nextInt(0, 2) == 0
						? (disponibles.size() > 1 ? ThreadLocalRandom.current().nextInt(1, disponibles.size()) : 1)
						: disponibles.size() - 1);
			elecciones.add(eleccion);

			if(eleccion == 0 && !primer_carbono)
				eter.enlazarCarbono();
			else if(disponibles.get(eleccion - 1) != Funciones.radical)
				eter.enlazar(disponibles.get(eleccion - 1));
			else {
				int carbonos = ThreadLocalRandom.current().nextInt(3, 5);
				if(eleccion == 1)
					elecciones.add(1);
				else elecciones.add(0);
				elecciones.add(carbonos);

				eter.enlazar(new Sustituyente(carbonos, eleccion == 1));
			}

			if(primer_carbono)
				primer_carbono = false;
		}

		secuencia = elecciones;

		return eter;
	}

}
