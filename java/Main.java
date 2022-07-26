import java.util.Map;

class Main {

      // Constant substituents:
    const Substituent acid = new Substituent(Id.acid, 3);
    const Substituent carboxyl = new Substituent(Id.carboxyl, 1);
    const Substituent amide = new Substituent(Id.amide, 3);
    const Substituent carbamoyl = new Substituent(Id.carbamoyl, 1);
    const Substituent nitrile = new Substituent(Id.nitrile, 3);
    const Substituent cyanide = new Substituent(Id.cyanide, 1);
    const Substituent aldehyde = new Substituent(Id.aldehyde, 3);
    const Substituent ketone = new Substituent(Id.ketone, 2);
    const Substituent alcohol = new Substituent(Id.alcohol, 1);
    const Substituent amine = new Substituent(Id.amine, 1);
    const Substituent nitro = new Substituent(Id.nitro, 1);
    const Substituent bromine = new Substituent(Id.bromine, 1);
    const Substituent chlorine = new Substituent(Id.chlorine, 1);
    const Substituent fluorine = new Substituent(Id.fluorine, 1);
    const Substituent iodine = new Substituent(Id.iodine, 1);
    const Substituent hydrogen = new Substituent(Id.hydrogen, 1);

        // Map between substituents and their kinds:
	 const Map<Id, Substituent> list = {
            {Id.acid, acid},
            {Id.carboxyl, carboxyl},
            {Id.amide, amide},
            {Id.carbamoyl, carbamoyl},
            {Id.nitrile, nitrile},
            {Id.cyanide, cyanide},
            {Id.aldehyde, aldehyde},
            {Id.ketone, ketone},
            {Id.alcohol, alcohol},
            {Id.amine, amine},
            {Id.nitro, nitro},
            {Id.bromine, bromine},
            {Id.chlorine, chlorine},
            {Id.fluorine, fluorine},
            {Id.iodine, iodine},
            {Id.hydrogen, hydrogen}
        };
    public static void main(String[] args)
    {
        System.out.println("Hola mundo!");
    }

    // TODO: "but-1-eno" -> "but-1-ene" -> OPSIN -> "C=CCC" -> "CH2=CH3-CH3-CH3"
    // TODO: "CH2=CH3-CH3-CH3" -> ? -> "but-1-eno"

}
