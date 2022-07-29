public class Sustituyente {

    private Id funcion; // El tipo de sustituyente
    private int enlaces; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonos;
    private boolean iso;

	/* EJEMPLOS:

	cetona:     =O              ->  { Id:cetona,    enlaces: 2,  carbonos: 0,  iso: false }

	propil:     -CH2-CH2-CH3    ->  { Id::radical,  enlaces: 1,  carbonos: 3,  iso: false }

                           CH3
                          /
	isopentil:  -CH2-CH2-CH2    ->  { Id::radical,  enlaces: 1,  carbonos: 5,  iso: true  }
                          \
                           CH3
	*/

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

    public Sustituyente(Id funcion, int enlaces) {
        construir(funcion, enlaces);
    }

    public Sustituyente(int carbonos, boolean iso) {
        construir(carbonos, iso);
    }

    public Sustituyente(Id funcion) {
        switch(funcion) {
            case acido:
            case amida:
            case nitrilo:
            case aldehido:
                construir(funcion, 3);
                break;
            case cetona:
                construir(funcion, 2);
                break;
            case carboxil:
            case carbamoil:
            case cianuro:
            case alcohol:
            case amina:
            case nitro:
            case bromo:
            case cloro:
            case fluor:
            case yodo:
            case hidrogeno:
                construir(funcion, 1);
                break;
            default: // Id.alqueno, Id.alquino, Id.radical (error)
                break;
        }
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

    public boolean esTipo(Id funcion) {
        return this.funcion.equals(funcion);
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
                break;
            case carbamoil:
                resultado.append("C");
            case amida:
                resultado.append("OHN2");
                break;
            case cianuro:
                resultado.append("C");
            case nitrilo:
                resultado.append("N");
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
                resultado.append("CH2".repeat(Math.max(0, carbonos - 2)));
                resultado.append(iso ? "(CH3)2" : "CH2CH3");
                break;
            case hidrogeno:
                resultado.append("H");
                break;
            default: // Id.alqueno, Id.alquino (error)
                break;
        }

        return resultado.toString();
    }

    // Getters y setters:

    public Id getFuncion() {
        return funcion;
    }

    public void setFuncion(Id funcion) {
        this.funcion = funcion;
    }

    public int getEnlaces() {
        return enlaces;
    }

    public void setEnlaces(int enlaces) {
        this.enlaces = enlaces;
    }

    public int getCarbonos() {
        return carbonos;
    }

    public void setCarbonos(int carbonos) {
        this.carbonos = carbonos;
    }

    public boolean getIso() {
        return iso;
    }

    public void setIso(boolean iso) {
        this.iso = iso;
    }

}
