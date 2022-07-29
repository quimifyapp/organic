import java.util.ArrayList;
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

        List<Integer> indices = Arrays.asList(0, 1, 1, 2);

        List<Localizador> localizadores = new ArrayList<>();
        localizadores.add(new Localizador(indices, "propil"));
        localizadores.add(new Localizador(indices, "hidroxi"));
        localizadores.add(new Localizador(indices, "amino"));
        localizadores.add(new Localizador(indices, "carmaboil"));

        Localizador.ordenarAlfabeticamente(localizadores);

        System.out.println(localizadores);

        /*
        List<Sustituyente> sustituyentes = new ArrayList<>();
        sustituyentes.add(new Sustituyente(Id.hidrogeno));
        sustituyentes.add(new Sustituyente(1, false));
        sustituyentes.add(new Sustituyente(3, true));
        sustituyentes.add(new Sustituyente(Id.acido));
        sustituyentes.add(new Sustituyente(Id.acido));

        Organico.ordenarPorFunciones(sustituyentes);
        System.out.println(sustituyentes);
        */

        /*
        Carbono carbono = new Carbono(0);

        carbono.nuevoSustituyente(new Sustituyente(Id.hidrogeno));

        carbono.nuevoSustituyente(new Sustituyente(Id.cetona));
        carbono.nuevoSustituyente(new Sustituyente(Id.cloro));

        System.out.println(carbono);
        /*

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
