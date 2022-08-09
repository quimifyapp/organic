package organico.interfaz.formula;

import organico.OrganicoResultado;
import organico.OrganicoService;

import java.util.Optional;
import java.util.Scanner;

public class Formular {

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		while(true) {
			System.out.print("Introduce un nombre: ");
			String nombre = scanner.nextLine();

			Optional<OrganicoResultado> resultado = OrganicoService.formular(nombre);
			if(resultado.isPresent())
				resultado.get().mostrar();
			else System.out.println("No encontrado");

			System.out.println();
		}
	}

}
