package organico;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

public class OrganicoResultado {

    private String formula;
    private String nombre;
    private String masa;
    private String url_2d;

    // Para la consola:

    public void mostrar() {
        mostrar("Nombre", nombre);
        mostrar("Fórmula", formula);
        mostrar("PubChem masa", masa);
        mostrar("PubChem 2D", url_2d);

        if(url_2d != null) {
            try {
                Desktop.getDesktop().browse(new URI(url_2d));
            }
            catch(Exception ignore) {}
        }
    }

    private void mostrar(String titulo, String texto) {
        System.out.println(titulo + ": " + Objects.requireNonNullElse(texto, "no encontrado"));
    }

    // Getters y setters:

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMasa() {
        return masa;
    }

    public void setMasa(String masa) {
        this.masa = masa;
    }

    public String getUrl_2d() {
        return url_2d;
    }

    public void setUrl_2d(String url_2d) {
        this.url_2d = url_2d;
    }

}
