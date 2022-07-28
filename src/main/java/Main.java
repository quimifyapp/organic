import java.util.Arrays;
import java.util.List;

class Main {

    // TODO: "but-1-eno" -> "but-1-ene" -> OPSIN -> "C=CCC" -> "CH2=CH3-CH3-CH3"
    // TODO: "CH2=CH3-CH3-CH3" -> ? -> "but-1-eno"

    // TODO: comparar resultados de funciones de C++ con sus adaptaciones
    // TODO: los errores del .py?
    // TODO: cómo cambiar los resource de OPSIN usando Maven?

    // TODO: Convertidor:
        // 1. Quitar enlaces de hidrógenos a carbonos, y esos átomos
        // 2. Hay más átomos que C, H?
            // ? Ver si hay una sola cadena con funciones o si hay más
            // : Empezar por alguno terminal

    public static void main(String[] args) {

        List<Integer> indices = Arrays.asList(0, 1, 1, 2, 4, 5);
        System.out.println(new Organico.Localizador(indices, "propil"));

        /*
        Carbono carbono = new Carbono(0);

        carbono.nuevoSustituyente(new Sustituyente(Id.hidrogeno));

        carbono.nuevoSustituyente(new Sustituyente(Id.cetona));
        carbono.nuevoSustituyente(new Sustituyente(Id.cloro));

        System.out.println(carbono);
        */

        /*
        NameToStructure nts = NameToStructure.getInstance();

        while(true) {
            String name = new Scanner(System.in).nextLine();
            OpsinResult result = nts.parseChemicalName(name);

            System.out.println(result.getPrettyPrintedCml());
            System.out.println(result.getSmiles());
        }
        */
    }



}
