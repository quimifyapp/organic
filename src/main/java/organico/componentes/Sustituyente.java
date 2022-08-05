package organico.componentes;

import java.util.ArrayList;
import java.util.List;

public class Sustituyente {

    private Id funcion; // El tipo de sustituyente
    private int enlaces; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonos;
    private boolean iso;

	// EJEMPLOS:
    /*
	cetona:     =O              →  { organico.componentes.Id:cetona,    enlaces: 2,  carbonos: 0,  iso: false }

	propil:     -CH2-CH2-CH3    →  { organico.componentes.Id::radical,  enlaces: 1,  carbonos: 3,  iso: false }

                           CH3
                          /
	isopentil:  -CH2-CH2-CH     →  { organico.componentes.Id::radical,  enlaces: 1,  carbonos: 5,  iso: true  }
                          \
                           CH3
	*/

    // Constructores:

    private void construir(Id funcion, int enlaces, int carbonos, boolean iso) {
        this.funcion = funcion;
        this.enlaces = enlaces;
        this.carbonos = carbonos;
        this.iso = iso;
    }

    private void construir(Id funcion, int enlaces) {
        construir(funcion, enlaces, 0, false);
    }

    private void construir(int carbonos, boolean iso) {
        construir(Id.radical, 1, carbonos, iso);
    }

    private void construir(int carbonos) {
        if(carbonos > 0)
            construir(carbonos, false);
        else throw new IllegalArgumentException("No existen radicales con 0 carbonos");
    }

    public Sustituyente(int carbonos, boolean iso) {
        if(iso) {
            switch(carbonos) {
                case 0:
                    throw new IllegalArgumentException("No existen radicales con 0 carbonos");
                case 1:
                    throw new IllegalArgumentException("No existe el \"isometil\"");
                case 2:
                    throw new IllegalArgumentException("No existe el \"isoetil\"");
                default:
                    construir(carbonos, true);
                    break;
            }
        }
        else construir(carbonos);
    }

    public Sustituyente(int carbonos) {
        construir(carbonos);
    }

    public Sustituyente(Id funcion) {
        switch(funcion) {
            case acido:
            case amida:
            case nitrilo:
            case aldehido:
                construir(funcion, 3);
                // Hasta aquí
                break;
            case cetona:
                construir(funcion, 2);
                break;
            case carboxil:
            case carbamoil:
            case cianuro:
            case alcohol:
            case amina:
            case eter:
            case nitro:
            case bromo:
            case cloro:
            case fluor:
            case yodo:
            case hidrogeno:
                construir(funcion, 1);
                // Hasta aquí
                break;
            case radical:
                throw new IllegalArgumentException("No existe un único sustituyente con función de radical");
            default: // Id.alqueno, Id.alquino
                throw new IllegalArgumentException("No existen sustituyentes con función de " + funcion);
        }
    }

    // Consultas particulares:

    public boolean esTipo(Id funcion) {
        return this.funcion == funcion;
    }

    @Override
    public boolean equals(Object sustituyente) {
        boolean resultado;

        if(sustituyente != null && sustituyente.getClass() == this.getClass()) {
            Sustituyente s = (Sustituyente) sustituyente;
            resultado = funcion == Id.radical
                    ? carbonos == s.carbonos && iso == s.iso
                    : funcion == s.funcion && enlaces == s.enlaces;
        }
        else resultado = false;

        return resultado;
    }

    // Para radicales:

    public boolean esMayorRadicalQue(Sustituyente radical) {
        switch(Integer.compare(getCarbonosRectos(), radical.getCarbonosRectos())) {
            case 1: // Lo supera
                return true;
            case 0: // Lo iguala
                return iso && !radical.iso; // Pero es 'iso'
            default:
                return false;
        }
    }

    // Métodos get:

    public Id getFuncion() {
        return funcion;
    }


    public int getEnlaces() {
        return enlaces;
    }


    public int getCarbonos() {
        return carbonos;
    }


    public boolean getIso() {
        return iso;
    }

    // Para radicales:

    public int getCarbonosRectos() {
        return carbonos - (iso ? 1 : 0);
    }

    public List<Carbono> getRadical() {
        List<Carbono> radical = new ArrayList<>();

        if(carbonos > 0) {
            radical.add(new Carbono(0));
            radical.get(0).enlazarSustituyente(Id.hidrogeno, 3); // CH3-
            radical.get(0).enlazarCarbono();

            int anteriores = 1; // CH3-

            if(iso) {
                radical.add(new Carbono(1)); // CH3-C≡
                radical.get(1).enlazarSustituyente(Id.hidrogeno); // CH3-CH=
                radical.get(1).enlazarSustituyente(new Sustituyente(1)); // CH3-CH(CH3)-
                radical.get(1).enlazarCarbono();

                anteriores += 2; // CH3-CH(CH3)-
            }

            Carbono CH2 = new Carbono(1); // -C≡
            CH2.enlazarSustituyente(Id.hidrogeno, 2); // -CH2=
            CH2.enlazarCarbono(); // -CH2-

            for(int i = anteriores; i < carbonos; i++)
                radical.add(CH2);
        }

        return radical;
    }

    // Texto:

    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();

        switch(funcion) {
            case carboxil:
                resultado.append("C");
            case acido:
                resultado.append("OOH");
                // Hasta aquí
                break;
            case carbamoil:
                resultado.append("C");
            case amida:
                resultado.append("ONH2");
                // Hasta aquí
                break;
            case cianuro:
                resultado.append("C");
            case nitrilo:
                resultado.append("N");
                // Hasta aquí
                break;
            case aldehido:
                resultado.append("HO");
                break;
            case cetona:
                resultado.append("O");
                break;
            case alcohol:
                resultado.append("OH");
                break;
            case amina:
                resultado.append("NH2");
                break;
            case eter:
                resultado.append("-O-");
                break;
            case nitro:
                resultado.append("NO2");
                break;
            case bromo:
                resultado.append("Br");
                break;
            case cloro:
                resultado.append("Cl");
                break;
            case fluor:
                resultado.append("F");
                break;
            case yodo:
                resultado.append("I");
                break;
            case radical:
                if(iso)
                    resultado.append("CH2".repeat(Math.max(0, carbonos -  3))).append("CH(CH3)2");
                else resultado.append("CH2".repeat(Math.max(0, carbonos -  1))).append("CH3");
                break;
            case hidrogeno:
                resultado.append("H");
                break;
        }

        return resultado.toString();
    }

}
