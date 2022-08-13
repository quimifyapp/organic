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

	private List<Atomo> getCarbonos() {
		return molecula.stream().filter(atomo -> atomo.getTipo() == Atomos.C).collect(Collectors.toList());
	}

	private Optional<Atomo> getCarbonoExtremo() {
		Optional<Atomo> extremo = Optional.empty();

		for(Atomo carbono : getCarbonos())
			if(carbono.getCantidadDe(Atomos.C) < 2) {
				extremo = Optional.of(carbono);
				break;
			}

		return extremo;
	}

	private Optional<Atomo> getOxigenoPuente() {
		Optional<Atomo> extremo = Optional.empty();

		for(Atomo enlazado : molecula)
			if(enlazado.esOxigenoPuente()) {
				extremo = Optional.of(enlazado);
				break;
			}

		return extremo;
	}

	private int getCarbonosAlAlcanceDe(Atomo carbono) {
		int cantidad;

		List<Atomo> enlazados = carbono.getEnlazadosCarbonosSeparados();

		cantidad = enlazados.size();
		for(Atomo enlazado : enlazados)
			cantidad += getCarbonosAlAlcanceDe(enlazado);

		return cantidad;
	}

	private boolean esEter() {
		for(Atomo atomo : molecula)
			for(Atomo enlazado : atomo.getEnlazadosSinCarbonos())
				if(enlazado.esTipo(Atomos.O) && enlazado.getCantidadDe(Atomos.C) == 2)
					return true;

		return false;
	}

	// Texto:

	public Optional<String> getFormula() {
		Optional<String> formula = Optional.empty();

		// Se comprueba que hay ningún ciclo:
		if(!smiles.matches(".*[0-9].*")) {
			// Se busca un extremo de la molécula:
			Optional<Atomo> extremo = getCarbonoExtremo();

			if(extremo.isPresent()) { // Debe haberlo
				int contiguos = 1 + getCarbonosAlAlcanceDe(extremo.get());

				if(contiguos == getCarbonos().size()) { // Todos los carbonos están unidos, podría ser un 'Simple'
					Simple simple = new Simple();

					// Primero: 'extremo'
					// Segundo: [*] de los 3 posibles sustituyentes...
						// No carbonos: mirar si son reconocidos
						// Carbonos: mirar si hay más de un camino que no sea recto o iso...
							// Ninguno: escoger cualquiera, repetir [*]
							// Uno: escoger ese, repetir [*]
							// Más de uno...
								// El camino ya recorrido sí es recto o iso...
									// Pasa a ser sustituyente del carbono, repetir [*]
								// No: no es posible, se aborta
				}
				else {
					Optional<Atomo> oxigeno_puente = getOxigenoPuente();

					if(oxigeno_puente.isPresent()) { // Podría ser un 'Eter' o un 'Ester'
						List<Atomo> dos_extremos = oxigeno_puente.get().getEnlazadosCarbonos();

						int contiguos_izquierda = 1 + getCarbonosAlAlcanceDe(dos_extremos.get(0));
						int contiguos_derecha = 1 + getCarbonosAlAlcanceDe(dos_extremos.get(1));

						if(contiguos_izquierda + contiguos_derecha == getCarbonos().size()) { //
							formula = Optional.of("Éter o éster");
						}
					}
					else formula = Optional.of("PSEUDO éter o éster");
				}
			}
			else {
				// Error...
			}
		}

		return formula;
	}

}
