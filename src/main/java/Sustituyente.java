public class Sustituyente {

    private Id funcion; // El tipo de sustituyente
    private int enlaces; // Número de e- que comparte con el carbono

    // Solo para radicales:
    private int carbonos;
    private boolean iso;
    // EXAMPLES:
	/*
	R=O -> "cetona" {Id:cetona, enlaces = 2}

	R-CH2-CH2-CH3 -> "propil" {Id::radical, enlaces = 1, carbonos = 3, iso = false}

				 CH3
				/
	R-CH2-CH2-CH2    -> "isopentil" {Id::radical, e. = 1, c. = 5, iso = true}
				\
				 CH3
	*/

    public Sustituyente(Id funcion, int enlaces) {
        construirSimple(funcion, enlaces);
    }

    private void construirSimple(Id funcion, int enlaces) {
        this.funcion = funcion;
        this.enlaces = enlaces;
        carbonos = 0;
        iso = false;
    }

    public Sustituyente(int carbonos, boolean iso) {
        construirRadical(carbonos, iso);
    }

    private void construirRadical(int carbonos, boolean iso) {
        funcion = Id.radical;
        enlaces = 1;
        this.carbonos = carbonos;
        this.iso = iso;
    }

    public Sustituyente(Id funcion) {
        switch(funcion) {
            case acido:
            case amida:
            case nitrilo:
            case aldehido:
                construirSimple(funcion, 3);
                break;
            case cetona:
                construirSimple(funcion, 2);
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
                construirSimple(funcion, 1);
                break;
            default: // Id.alqueno, Id.alquino, Id.radical (error)
                break;
        }
    }

    public boolean equals(Sustituyente s) {
        return funcion == s.funcion && enlaces == s.enlaces && carbonos == s.carbonos && iso == s.iso;
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
