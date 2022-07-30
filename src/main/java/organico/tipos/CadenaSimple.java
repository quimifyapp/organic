package organico.tipos;

import organico.Organico;
import organico.componentes.Carbono;
import organico.componentes.Id;
import organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.List;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public class CadenaSimple extends Organico {

    // Constructor:

    public CadenaSimple() {
        carbonos.add(new Carbono(0));
    }

    // Consultas particulares:

    private Carbono ultimoCarbono() {
        return carbonos.get(carbonos.size() - 1);
    }

    private int getEnlacesLibres() {
        return ultimoCarbono().getEnlacesLibres();
    }

    public List<Id> tiposDeSustituyentesDisponibles() {
        List<Id> disponibles = new ArrayList<>();

        switch(getEnlacesLibres()) {
            case 4:
            case 3:
                disponibles.add(Id.acido);
                disponibles.add(Id.amida);
                disponibles.add(Id.nitrilo);
                disponibles.add(Id.aldehido);
            case 2:
                disponibles.add(Id.cetona);
            case 1:
                disponibles.add(Id.alcohol);
                disponibles.add(Id.amina);
                disponibles.add(Id.nitro);
                disponibles.add(Id.bromo);
                disponibles.add(Id.cloro);
                disponibles.add(Id.fluor);
                disponibles.add(Id.yodo);
                disponibles.add(Id.radical);
                disponibles.add(Id.hidrogeno);
                break;
        }

        return disponibles;
    }

    // Interfaz:

    public void enlazarCarbono() {
        Carbono ultimo = ultimoCarbono();

        ultimo.enlazarCarbono();
        carbonos.add(new Carbono(ultimo.getEnlacesLibres() + 1));
    }

    public void enlazarSustituyente(Sustituyente sustituyente) {
        ultimoCarbono().enlazarSustituyente(sustituyente);
    }

    // Texto:

    public String getFormula() {
        StringBuilder resultado = new StringBuilder();

        resultado.append(carbonos.get(0));

        for(int i = 1; i < carbonos.size(); i++) {
            switch(carbonos.get(i - 1).getEnlacesLibres()) {
                case 0:
                    resultado.append("-");
                    break;
                case 1:
                    resultado.append("=");
                    break;
                case 2:
                    resultado.append("≡");
                    break;
                default:
                    // Error...
                    break;
            }

            resultado.append(carbonos.get(i));
        }

        return resultado.toString();
    }

}
