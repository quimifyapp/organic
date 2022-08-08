package organico.intermediarios.pubchem;

import java.util.Optional;

public class PubChemResultado {

	private Optional<String> masa = Optional.empty();
	private Optional<String> url_2d = Optional.empty();
	private Optional<String> nombre_ingles = Optional.empty();

	// Getters y setters:

	public Optional<String> getMasa() {
		return masa;
	}

	public void setMasa(String masa) {
		this.masa = Optional.ofNullable(masa);
	}

	public Optional<String> getUrl_2d() {
		return url_2d;
	}

	public void setUrl_2d(String url_2d) {
		this.url_2d = Optional.ofNullable(url_2d);
	}

	public Optional<String> getNombre_ingles() {
		return nombre_ingles;
	}

	public void setNombre_ingles(String nombre_ingles) {
		this.nombre_ingles = Optional.ofNullable(nombre_ingles);
	}

}
