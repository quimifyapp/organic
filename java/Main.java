import java.util.Map;

class Main {

      // Constant substituents:
      private static final Sustituyente acid = new Sustituyente(Id.acid, 3);
    private static final Sustituyente carboxyl = new Sustituyente(Id.carboxyl, 1);
    private static final Sustituyente amide = new Sustituyente(Id.amide, 3);
    private static final Sustituyente carbamoyl = new Sustituyente(Id.carbamoyl, 1);
    private static final Sustituyente nitrile = new Sustituyente(Id.nitrile, 3);
    private static final Sustituyente cyanide = new Sustituyente(Id.cyanide, 1);
    private static final Sustituyente aldehyde = new Sustituyente(Id.aldehyde, 3);
    private static final Sustituyente ketone = new Sustituyente(Id.ketone, 2);
    private static final Sustituyente alcohol = new Sustituyente(Id.alcohol, 1);
    private static final Sustituyente amine = new Sustituyente(Id.amine, 1);
    private static final Sustituyente nitro = new Sustituyente(Id.nitro, 1);
    private static final Sustituyente bromine = new Sustituyente(Id.bromine, 1);
    private static final Sustituyente chlorine = new Sustituyente(Id.chlorine, 1);
    private static final Sustituyente fluorine = new Sustituyente(Id.fluorine, 1);
    private static final Sustituyente iodine = new Sustituyente(Id.iodine, 1);
    private static final Sustituyente hydrogen = new Sustituyente(Id.hydrogen, 1);

        // Map between substituents and their kinds:
        private static final Map<Id, Sustituyente> list = {
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
