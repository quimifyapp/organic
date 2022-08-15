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
			String tipo = atomo.getAttribute("elementType");

			molecula.add(new Atomo(id, tipo));
		}

		// Se enlazan entre sí:
		NodeList enlaces_xml = xml.getElementsByTagName("bond");
		for(int i = 0; i < enlaces_xml.getLength(); i++) {
			Element enlace = (Element) enlaces_xml.item(i);

			String[] id_string = enlace.getAttribute("id").replaceAll("a", "").split("_");

			Integer[] id_int = {
					Integer.valueOf(id_string[0]),
					Integer.valueOf(id_string[1])
			};

			Atomo[] atomos = {
					molecula.stream().filter(atomo -> atomo.getId().equals(id_int[0])).findAny()
							.orElseThrow(NoSuchElementException::new),
					molecula.stream().filter(atomo -> atomo.getId().equals(id_int[1])).findAny()
							.orElseThrow(NoSuchElementException::new)
			};

			atomos[0].enlazar(atomos[1]);
			atomos[1].enlazar(atomos[0]);
		}
	}

	// Consultas internas:

	private List<Atomo> getCarbonos() {
		return molecula.stream().filter(atomo -> atomo.getTipo() == Atomos.C).collect(Collectors.toList());
	}

	private List<Atomo> getCarbonosExtremos() {
		List<Atomo> carbonos = getCarbonos();
		return carbonos.stream().filter(carbono -> carbono.getCantidadDe(Atomos.C) < 2).collect(Collectors.toList());
	}

	private Optional<Atomo> getOxigenoPuente() {
		return molecula.stream().filter(Atomo::esOxigenoPuente).findAny();
	}

	private int getCarbonosAlAlcanceDe(Atomo carbono) {
		int cantidad;

		List<Atomo> enlazados = carbono.getEnlazadosSeparadosCarbonos();

		cantidad = enlazados.size();
		for(Atomo enlazado : enlazados)
			cantidad += getCarbonosAlAlcanceDe(enlazado);

		return cantidad;
	}

	// Texto:

	public Optional<String> getFormula() {
		Optional<String> formula = Optional.empty();

		// TODO: arreglar lo de los puentes y decidir si es (eter | ester)

		// Se comprueba que hay ningún ciclo:
		if(!smiles.matches(".*[0-9].*")) {
			// Se buscan los extremos de la molécula:
			List<Atomo> carbonos_extremos = getCarbonosExtremos();

			if(carbonos_extremos.size() == 2) { // Debería cumplirse si no tiene radicales
				Atomo carbono_extremo = carbonos_extremos.get(0);
				int contiguos = 1 + getCarbonosAlAlcanceDe(carbono_extremo);

				if(contiguos == getCarbonos().size()) { // Todos los carbonos están unidos, podría ser un 'Simple'
					Simple simple = new Simple();

					// Primer carbono:
					simple.enlazarCarbono();
					carbono_extremo.getSustituyentes().forEach(simple::enlazar); // Son solo los no-carbonos

					// El resto:
					List<Atomo> carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
					while(carbonos_separados.size() > 0) {
						simple.enlazarCarbono();

						Atomo carbono = carbonos_separados.get(0);
						carbono.getSustituyentes().forEach(simple::enlazar); // Son solo los no-carbonos

						carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
					}

					// Finalmente:
					simple.corregir();
					formula = Optional.of(simple.getFormula());

					// TODO: que no solo sirva para compuestos lineales sin radicales
					// TODO: método que ponga los carbonos de la cadena simple en lista
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
			}
			else if (carbonos_extremos.size() == 3) { // Debería cumplirse si no tiene radicales y tiene un puente
				Optional<Atomo> oxigeno_puente = getOxigenoPuente();

				if(oxigeno_puente.isPresent()) { // Podría ser un 'Eter' o un 'Ester'
					List<Atomo> enlazados_al_oxigeno = oxigeno_puente.get().getEnlazadosCarbonos();
					int contiguos_izquierda = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(0));
					int contiguos_derecha = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(1));

					if(contiguos_izquierda + contiguos_derecha == getCarbonos().size()) { // No tiene más puentes
						// Por un lado (C*-(...)-O-C):

						Cadena primaria = new Cadena();
						Atomo carbono_extremo = enlazados_al_oxigeno.get(0);

						// Primer carbono:
						primaria.enlazarCarbono();
						carbono_extremo.getSustituyentes().forEach(primaria::enlazar); // Son solo los no-carbonos

						// El resto:
						List<Atomo> carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
						while(carbonos_separados.size() > 0) {
							primaria.enlazarCarbono();

							Atomo carbono = carbonos_separados.get(0);
							carbono.getSustituyentes().forEach(primaria::enlazar); // Son solo los no-carbonos

							carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
						}

						// Por el otro lado (C-O-C*):

						Cadena secundaria = new Cadena(1);
						carbono_extremo = enlazados_al_oxigeno.get(1);

						// Primer carbono:
						carbono_extremo.getSustituyentes().stream()
								.filter(sustituyente -> !sustituyente.esTipo(Funciones.eter)) // El éter no se duplica
								.forEach(secundaria::enlazar); // Son solo los no-carbonos

						// El resto:
						carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
						while(carbonos_separados.size() > 0) {
							secundaria.enlazarCarbono();

							Atomo carbono = carbonos_separados.get(0);
							carbono.getSustituyentes().forEach(secundaria::enlazar); // Son solo los no-carbonos

							carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
						}

						// Finalmente:
						Eter eter = new Eter(primaria.getInversa(), secundaria);
						eter.corregir();
						formula = Optional.of(eter.getFormula());
					}
				}
			}
			else if (carbonos_extremos.size() == 0) {
				// Error...
			}
		}

		return formula;
	}

}
