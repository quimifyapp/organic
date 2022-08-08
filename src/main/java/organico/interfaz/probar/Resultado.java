package organico.interfaz.probar;

import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.intermediarios.pubchem.PubChem;
import organico.intermediarios.pubchem.PubChemResultado;

import java.awt.*;
import java.net.URI;

public class Resultado {

	public static void analizar(String nombre) {
		System.out.println("Nombre: " + nombre);
		System.out.println();

		OpsinResultado opsin_resultado = Opsin.procesarNombreES(nombre);
		String smiles = opsin_resultado.getSmiles();

		System.out.println("OPSIN smiles: " + opsin_resultado.getSmiles());

		if(smiles != null) {
			PubChemResultado pub_chem_resultado = PubChem.procesarSmiles(smiles);

			System.out.print("Nombre inglés: ");
			if(pub_chem_resultado.getNombre_ingles().isPresent())
				System.out.println(pub_chem_resultado.getNombre_ingles().get());
			else System.out.println("no encontrado");

			System.out.print("PubChem masa: ");
			if(pub_chem_resultado.getMasa().isPresent())
				System.out.println(pub_chem_resultado.getMasa().get() + " g/mol");
			else System.out.println("no encontrada");

			System.out.print("PubChem 2D: ");
			if(pub_chem_resultado.getUrl_2d().isPresent()) {
				System.out.println(pub_chem_resultado.getUrl_2d().get());
				try {
					Desktop.getDesktop().browse(new URI(pub_chem_resultado.getUrl_2d().get()));
				}
				catch(Exception ignore) {}
			}
			else System.out.println("no encontrado");

			System.out.println();
		}
		else System.out.println("Error en OPSIN");

		try {
			System.out.println("Enter para continuar...");
			System.in.read();
		}
		catch(Exception ignore) {}
	}

}
