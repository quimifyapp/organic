package organico;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import organico.componentes.Atomo;
import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.intermediarios.pubchem.PubChem;
import organico.intermediarios.pubchem.PubChemResultado;
import organico.tipos.Eter;
import organico.tipos.Simple;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
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
			String cml = opsin_resultado.get().getCml(); // Chemical Markup Language

			try {
				List<Atomo> atomos = parsearCML(cml);


			}
			catch(Exception exception) {
				// Error...
			}

			resultado = Optional.of(organico_resultado);
		}
		else resultado = Optional.empty();

		return resultado;
	}

	private static List<Atomo> parsearCML(String cml) throws Exception {
		List<Atomo> atomos = new ArrayList<>();

		// Se procesa el Chemical Markup Language:
		DocumentBuilder constructor = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xml = constructor.parse(new InputSource(new StringReader(cml)));

		// Se recogen los átomos:
		NodeList atomos_xml = xml.getElementsByTagName("atom");
		for(int i = 0; i < atomos_xml.getLength(); i++) {
			Element atomo = (Element) atomos_xml.item(i);

			int id = Integer.parseInt(atomo.getAttribute("id").replaceAll("a", ""));

			atomos.add(new Atomo(id, atomo.getAttribute("elementType")));
		}

		// Se enlazan entre sí:
		NodeList enlaces_xml = xml.getElementsByTagName("bond");
		for(int i = 0; i < enlaces_xml.getLength(); i++) {
			Element enlace = (Element) enlaces_xml.item(i);

			String[] id = enlace.getAttribute("id").replaceAll("a", "").split("_");
			int[] ids = {Integer.parseInt(id[0]), Integer.parseInt(id[1])};

			Atomo[] enlazados = {atomos.stream().filter(atomo -> atomo.getId() == ids[0]).findAny().orElse(null),
					atomos.stream().filter(atomo -> atomo.getId() == ids[1]).findAny().orElse(null)};

			enlazados[0].enlazarA(enlazados[1]);
			enlazados[1].enlazarA(enlazados[0]);
		}

		return atomos;
	}

}
