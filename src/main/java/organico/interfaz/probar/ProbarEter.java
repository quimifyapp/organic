package organico.interfaz.probar;

import organico.componentes.Id;
import organico.componentes.Sustituyente;
import organico.tipos.Eter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProbarEter {

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		while(true) {
			Eter eter = getEter();

			System.out.println();
			System.out.println("Fórmula: " + eter);
			eter.corregir(); // Es necesario
			System.out.println("Corregida: " + eter);

			String nombre = eter.getNombre();
			Resultado.analizar(nombre);
		}
	}

	private static Eter getEter() {
		Eter eter = new organico.tipos.Eter();

		List<Integer> elecciones = new ArrayList<>();
		boolean primer_carbono = true;

		while(!eter.estaCompleta()) {
			System.out.println("Fórmula: " + eter);

			if(!primer_carbono)
				System.out.println("0: C");

			List<Id> disponibles = eter.getSustituyentesDisponibles();
			for(int i = 0; i < disponibles.size(); i++) {
				if(disponibles.get(i) != Id.radical)
					System.out.println((i + 1) + ": " + new Sustituyente(disponibles.get(i)));
				else System.out.println((i + 1) + ": " + "-CH2(...)CH3");
			}

			System.out.print("Elección: ");
			int eleccion = scanner.nextInt();
			elecciones.add(eleccion);

			if(eleccion == 0 && !primer_carbono)
				eter.enlazarCarbono();
			else if(disponibles.get(eleccion - 1) != Id.radical)
				eter.enlazar(disponibles.get(eleccion - 1));
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

				eter.enlazar(new Sustituyente(carbonos, eleccion == 1));
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

		return eter;
	}

}
