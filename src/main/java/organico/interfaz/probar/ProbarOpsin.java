package organico.interfaz.probar;

import organico.intermediarios.opsin.OpsinResultado;

import java.util.Scanner;

public class ProbarOpsin {

	public static void main(String[] args) {
		while(true) {
			System.out.print("Introduce un nombre en español o en inglés: ");
			String input = new Scanner(System.in).nextLine();

			OpsinResultado resultado = organico.intermediarios.opsin.Opsin.procesarNombreES(input); // El input debe
			// ser un nombre en español
			String smiles = resultado.getSmiles();

			if(smiles != null) { // Lo encuentra?
				System.out.print("es: ");
				System.out.println(smiles);
			}
			else {
				resultado = organico.intermediarios.opsin.Opsin.procesarNombreEN(input); // El input debe
				// ser un nombre en inglés
				smiles = resultado.getSmiles();

				if(smiles != null) { // Lo encuentra?
					System.out.print("en: ");
					System.out.println(smiles);
				}
				else System.out.println("No se ha encontrado ni en español ni en inglés");
			}
		}
	}

}
