package organico.interfaz.probar;

import organico.componentes.Id;
import organico.componentes.Sustituyente;
import organico.tipos.Simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProbarSimple {

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		while(true) {
			Simple simple = getSimple();

			System.out.println();
			System.out.println("Fórmula: " + simple);
			simple.corregir(); // Es necesario
			System.out.println("Corregida: " + simple);

			String nombre = simple.getNombre();
			Resultado.analizar(nombre);
		}
	}

	private static Simple getSimple() {
		Simple simple = new organico.tipos.Simple();

		List<Integer> elecciones = new ArrayList<>();
		boolean primer_carbono = true;
		while(!simple.estaCompleta()) {
			System.out.println("Fórmula: " + simple);

			if(!primer_carbono)
				System.out.println("0: C");

			List<Id> disponibles = simple.getSustituyentesDisponibles();
			for(int i = 0; i < disponibles.size(); i++) {
				if(disponibles.get(i) != Id.radical)
					System.out.println((i + 1) + ": " + new Sustituyente(disponibles.get(i)));
				else System.out.println((i + 1) + ": " + "-CH2(...)CH3");
			}

			System.out.print("Elección: ");
			int eleccion = scanner.nextInt();
			elecciones.add(eleccion);

			if(eleccion == 0 && !primer_carbono)
				simple.enlazarCarbono();
			else if(disponibles.get(eleccion - 1) != Id.radical)
				simple.enlazar(disponibles.get(eleccion - 1));
			else {
				System.out.println();
				System.out.println("0: -CH2(...)CH3");
				System.out.println("1: -CH2(...)CH(CH3)2");

				System.out.print("Elección: ");
				eleccion = scanner.nextInt();
				elecciones.add(eleccion);

				System.out.print("Carbonos en el radical: ");
				int carbonos = scanner.nextInt();
				elecciones.add(carbonos);

				simple.enlazar(new Sustituyente(carbonos, eleccion == 1));
			}
			System.out.println();

			if(primer_carbono)
				primer_carbono = false;
		}

		System.out.println();

		System.out.print("Secuencia:");
		for(int eleccion : elecciones)
			System.out.print(" " + eleccion);
		System.out.println();

		return simple;
	}

}
