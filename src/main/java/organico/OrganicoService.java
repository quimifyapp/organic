package organico;

import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.intermediarios.pubchem.PubChem;
import organico.intermediarios.pubchem.PubChemResultado;
import organico.tipos.Eter;
import organico.tipos.Generico;
import organico.tipos.Simple;

import java.util.*;

public class OrganicoService {

	// Privados:

	private static void completarConPubChem(OrganicoResultado resultado, String smiles) {
		PubChemResultado pub_chem_resultado = PubChem.procesarSmiles(smiles);

		resultado.setUrl_2d(pub_chem_resultado.getUrl_2d());

		if(pub_chem_resultado.getMasa().isPresent())
			resultado.setMasa(pub_chem_resultado.getMasa().get());
	}

	// Públicos:

	public static OrganicoResultado nombrar(Simple simple) {
		OrganicoResultado resultado = new OrganicoResultado();

		simple.corregir(); // Es necesario

		// Nombre:
		String nombre = simple.getNombre();
		resultado.setNombre(nombre);

		// Características:
		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		// Fórmula:
		resultado.setFormula(simple.getFormula());

		return resultado;
	}

	public static OrganicoResultado nombrar(Eter eter) {
		OrganicoResultado resultado = new OrganicoResultado();

		eter.corregir(); // Es necesario

		// Nombre:
		String nombre = eter.getNombre();
		resultado.setNombre(nombre);

		// Características:
		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		// Fórmula:
		resultado.setFormula(eter.getFormula());

		return resultado;
	}

	public static Optional<OrganicoResultado> formular(String nombre) {
		Optional<OrganicoResultado> resultado;

		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		if(opsin_resultado.isPresent()) {
			OrganicoResultado organico_resultado = new OrganicoResultado();

			// Nombre:
			organico_resultado.setNombre(nombre);

			// Características:
			completarConPubChem(organico_resultado, opsin_resultado.get().getSmiles());

			// Fórmula:
			try {
				Generico generico = new Generico(opsin_resultado.get().getCml(), opsin_resultado.get().getSmiles());

				Optional<String> formula = generico.getFormula();
				formula.ifPresent(organico_resultado::setFormula);
			}
			catch(IllegalArgumentException ignore) {} // Es común que se produzcan errores no inesperados
			catch (Exception exception) {
				// Error...
			}

			resultado = Optional.of(organico_resultado);
		}
		else resultado = Optional.empty();

		return resultado;
	}

}
