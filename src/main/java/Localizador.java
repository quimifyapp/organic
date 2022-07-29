import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Esta clase representa un localizador de un nombre IUPAC, como "2,3-diol".

class Localizador {

    private String posiciones, multiplicador, nombre;

    /* EJEMPLOS:\
        "2,3-diol"  =   { posiciones: "2,3",    multiplicador: "di",    nombre: "ol"     }
        "tetrain"   =   { posiciones: "",       multiplicador: "tetra", nombre: "in"     }
        "fluoro"    =   { posiciones: "",       multiplicador: "",      nombre: "fluoro" }
	*/

    private void construir(String posiciones, String multiplicador, String nombre) {
        this.posiciones = posiciones;
        this.multiplicador = multiplicador;
        this.nombre = nombre;
    }

    public Localizador(String posiciones, String multiplicador, String nombre) {
        construir(posiciones, multiplicador, nombre);
    }

    public Localizador(List<Integer> indices, String nombre) {
        StringBuilder auxiliar = new StringBuilder();

        if(indices.size() > 0) {
            for(int i = 0; i < indices.size() - 1; i++)
                auxiliar.append(indices.get(i) + 1).append(",");
            auxiliar.append(indices.get(indices.size() - 1) + 1);
        }

        construir(auxiliar.toString(), Organico.cuantificador(indices.size()), nombre);
    }

    // No se tienen en cuenta los multiplicadores ni las posiciones, como propone la IUPAC.
    // Ej.: "2,3-diol" -> "ol"
    public static void ordenarAlfabeticamente(List<Localizador> localizadores) {
        localizadores.sort(Comparator.comparing(Localizador::getNombre));
    }

    @Override
    public String toString() {
        String resultado = "";

        if(!posiciones.equals(""))
            resultado = posiciones + "-";
        resultado += multiplicador + nombre;

        return resultado;
    }

    // Getters y setters:

    public String getPosiciones() {
        return posiciones;
    }

    public void setPosiciones(String posiciones) {
        this.posiciones = posiciones;
    }

    public String getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(String multiplicador) {
        this.multiplicador = multiplicador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
