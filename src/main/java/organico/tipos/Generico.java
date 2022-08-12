package organico.tipos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import organico.Organica;
import organico.componentes.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

// Esta clase representa una molécula cualquiera a partir de un CML en formato XML para intentar redactarle una fórmula.

// Un compuesto genérico, químicamente hablando, podría ser de otro tipo ya contemplado en este programa (simple, éter,
// éster, cíclico...), pero también podría no encajar en ninguno de esos tipos.

public class Generico extends Organica {

	private final Set<Atomo> molecula;
	private final String smiles;

	// Constructor:

	public Generico(String cml, String smiles) throws ParserConfigurationException, IOException, SAXException {
		molecula = new HashSet<>();
		this.smiles = smiles;

		// Se procesa el Chemical Markup Language:
		DocumentBuilder constructor = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xml = constructor.parse(new InputSource(new StringReader(cml)));

		// Se recogen los átomos:
		NodeList atomos_xml = xml.getElementsByTagName("atom");
		for(int i = 0; i < atomos_xml.getLength(); i++) {
			Element atomo = (Element) atomos_xml.item(i);

			int id = Integer.parseInt(atomo.getAttribute("id").replaceAll("a", ""));

			molecula.add(new Atomo(id, atomo.getAttribute("elementType")));
		}

		// Se enlazan entre sí:
		NodeList enlaces_xml = xml.getElementsByTagName("bond");
		for(int i = 0; i < enlaces_xml.getLength(); i++) {
			Element enlace = (Element) enlaces_xml.item(i);

			String[] id = enlace.getAttribute("id").replaceAll("a", "").split("_");
			Integer[] ids = {
					Integer.valueOf(id[0]),
					Integer.valueOf(id[1])};
			Atomo[] atomos = {
					molecula.stream().filter(atomo -> atomo.getId().equals(ids[0])).findAny().orElse(null),
					molecula.stream().filter(atomo -> atomo.getId().equals(ids[1])).findAny().orElse(null)
			};

			atomos[0].enlazar(atomos[1]);
			atomos[1].enlazar(atomos[0]);
		}
	}

	// Consultas internas:

	private Optional<Atomo> getUnCarbonoExtremo() {
		Optional<Atomo> extremo = Optional.empty();

		for(Atomo atomo : getCarbonos())
			if(atomo.getCantidadDe(Atomos.C) < 2) {
				extremo = Optional.of(atomo);
				break;
			}

		return extremo;
	}

	private List<Atomo> getCarbonos() {
		return molecula.stream().filter(atomo -> atomo.getTipo() == Atomos.C).collect(Collectors.toList());
	}

	private boolean esEter() {
		for(Atomo atomo : molecula)
			for(Atomo enlazado : atomo.getEnlazadosSinCarbonos())
				if(enlazado.esTipo(Atomos.O) && enlazado.getCantidadDe(Atomos.C) == 2)
					return true;

		return false;
	}

	// TODO: rehacer usando getEnlazadosCarbonosSeparados()

	private int carbonosAlAlcanceDeSin(Atomo carbono, Atomo anterior) {
		int cantidad = 0;

		List<Atomo> enlazados = carbono.getEnlazadosCarbonos();
		enlazados.remove(anterior);

		cantidad += enlazados.size();

		for(Atomo enlazado : enlazados)
			cantidad += carbonosAlAlcanceDeSin(enlazado, carbono);

		return cantidad;
	}

	private int carbonosAlAlcanceDe(Atomo carbono) {
		return carbonosAlAlcanceDeSin(carbono, null);
	}

	// Texto:

	public Optional<String> getFormula() {
		Optional<String> formula = Optional.empty();

		// Se comprueba que no es cíclica:
		if(!smiles.matches(".*[0-9].*")) {
			// Se busca un extremo de la molécula:
			Optional<Atomo> extremo = getUnCarbonoExtremo();

			if(extremo.isPresent()) { // Si no es una molécula cíclica
				int contiguos = 1 + carbonosAlAlcanceDeSin(extremo.get(), extremo.get());

				if(contiguos == getCarbonos().size()) { // Todos los carbonos están unidos
					Simple simple = new Simple();

					for(Atomo atomo : getCarbonos()) {
						simple.enlazarCarbono();

						for(Atomo enlazado : atomo.getEnlazadosSinCarbonos()) {
							Atomo aislado = new Atomo(enlazado.getTipo(), enlazado.getEnlazadosSinCarbonos());
							Optional<Funciones> funcion = aislado.toFuncion();

							if(funcion.isPresent())
								simple.enlazar(funcion.get());
							else return Optional.empty();
						}
					}

					simple.corregir();

					formula = Optional.of(simple.getFormula());
				}
				else if(esEter()) {
					formula = Optional.of("éter");
				}
				else {
					formula = Optional.of("¿Qué es esto, qué está pasando?");
				}
				// TODO: es eter?
				// TODO: los carbonos están contiguos
				// TODO: get los carbonos contiguos

				// TODO: nombre?
			}
		}

		return formula;
	}

}
