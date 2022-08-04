package organico.pubchem;

import java.util.Optional;

public class PubChemResultado {

	private Optional<String> masa = Optional.empty();
	private String url_2d;
	private Optional<String> url_3d = Optional.empty();

	// Getters y setters:

	public Optional<String> getMasa() {
		return masa;
	}

	public void setMasa(String masa) {
		this.masa = Optional.ofNullable(masa);
	}

	public String getUrl_2d() {
		return url_2d;
	}

	public void setUrl_2d(String url_2d) {
		this.url_2d = url_2d;
	}

	public Optional<String> getUrl_3d() {
		return url_3d;
	}

	public void setUrl_3d(String url_3d) {
		this.url_3d = Optional.ofNullable(url_3d);
	}

}
