package interfaz;

import organico.intermediarios.opsin.OpsinResultado;

import java.util.Optional;
import java.util.Scanner;

public class ProbarOpsin {

	public static void main(String[] args) {
		while(true) {
			System.out.print("Introduce un nombre en español o en inglés: ");
			String input = new Scanner(System.in).nextLine();

			// El input debe ser un nombre en español:
			Optional<OpsinResultado> resultado = organico.intermediarios.opsin.Opsin.procesarNombreES(input);

			if(resultado.isPresent()) // Lo encuentra?
				System.out.println("es: " + resultado.get().getSmiles());
			else {
				// El input debe ser un nombre en inglés:
				resultado = organico.intermediarios.opsin.Opsin.procesarNombreEN(input);

				if(resultado.isPresent()) // Lo encuentra?
					System.out.println("en: " + resultado.get().getSmiles());
				else System.out.println("No se ha encontrado ni en español ni en inglés");
			}
		}
	}

}
