package organico;

import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.intermediarios.pubchem.PubChem;
import organico.intermediarios.pubchem.PubChemResultado;
import organico.tipos.Eter;
import organico.tipos.Simple;

import java.util.Optional;

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
		resultado.setFormula(simple.getFormula());

		String nombre = simple.getNombre();
		resultado.setNombre(nombre);

		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		return resultado;
	}

	public static OrganicoResultado nombrar(Eter eter) {
		OrganicoResultado resultado = new OrganicoResultado();

		eter.corregir(); // Es necesario
		resultado.setFormula(eter.getFormula());

		String nombre = eter.getNombre();
		resultado.setNombre(nombre);

		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		return resultado;
	}

	public static Optional<OrganicoResultado> formular(String nombre) {
		Optional<OrganicoResultado> resultado;

		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		if(opsin_resultado.isPresent()) {
			OrganicoResultado organico_resultado = new OrganicoResultado();

			// ...
			String cml = opsin_resultado.get().getCml();
			// ...

			completarConPubChem(organico_resultado, opsin_resultado.get().getSmiles());

			resultado = Optional.of(organico_resultado);
		}
		else resultado = Optional.empty();

		return resultado;
	}

}
