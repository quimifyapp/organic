import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

import java.util.Scanner;

class Main {

    public static void main(String[] args) {
        Carbono carbono = new Carbono(0);

        carbono.nuevoSustituyente(new Sustituyente(Id.cloro));
        carbono.nuevoSustituyente(new Sustituyente(2, true));
        carbono.nuevoSustituyente(new Sustituyente(Id.alcohol));
        carbono.nuevoSustituyente(new Sustituyente(Id.hidrogeno));

        System.out.println(carbono);

        NameToStructure nts = NameToStructure.getInstance();

        while(true) {
            String name = new Scanner(System.in).nextLine();
            OpsinResult result = nts.parseChemicalName(name);

            System.out.println(result.getPrettyPrintedCml());
            System.out.println(result.getSmiles());
        }
    }

    // TODO: los errores del .py?
    // TODO: cómo cambiar los resource de OPSIN usando Maven?
    // TODO: "but-1-eno" -> "but-1-ene" -> OPSIN -> "C=CCC" -> "CH2=CH3-CH3-CH3"
    // TODO: "CH2=CH3-CH3-CH3" -> ? -> "but-1-eno"

    // Convertidor:

    // 1. Quitar enlaces de hidrógenos a carbonos, y esos átomos
    // 2. Hay más átomos que C, H?
        // ? Ver si hay una sola cadena con funciones o si hay más
        // : Empezar por alguno terminal

}
