public class Substituent {

    private Id function;    // The substituent's kind
    private int bonds;    // Amount of e- it shares with the carbon
    // Only for radicals:
    private int carbons;
    private boolean iso;
    // EXAMPLES:
	/*
	R=O -> "ketone" {Id:ketone, bonds = 2}

	R-CH2-CH2-CH3 -> "propyl" {Id::radical, bon. = 1, carbons = 3, iso = false}

				 CH3
				/
	R-CH2-CH2-CH2    -> "isopentyl" {Id::radical, b. = 1, car. = 5, iso = true}
				\
				 CH3
	*/

    public Substituent(Id function, int bonds) {
        this.function = function;
        this.bonds = bonds;
        carbons = 0;
        iso = false;
    }

    public Substituent(int carbons, boolean iso) {
        function = Id.radical;
        bonds = 1;
        this.carbons = carbons;
        this.iso = iso;
    }

    public boolean equals(Substituent s) {
        return function == s.function && bonds == s.bonds && carbons == s.carbons && iso == s.iso;
    }

    public Id getFunction()  {return function; }


    public Id getFunction() {
        return function;
    }

    public void setFunction(Id function) {
        this.function = function;
    }

    public int getBonds() {
        return bonds;
    }

    public void setBonds(int bonds) {
        this.bonds = bonds;
    }

    public int getCarbons() {
        return carbons;
    }

    public void setCarbons(int carbons) {
        this.carbons = carbons;
    }

    public boolean isIso() {
        return iso;
    }

    public void setIso(boolean iso) {
        this.iso = iso;
    }

}
