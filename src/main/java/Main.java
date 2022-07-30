import organico.componentes.Id;
import organico.componentes.Sustituyente;
import organico.opsin.Opsin;
import organico.opsin.OpsinResultado;
import organico.tipos.CadenaSimple;

import java.util.Scanner;

class Main {

    // Formular: "but-1-eno" -> [OpsinResultado -> Organico] -> OrganicoResultado
    // Nombrar: "CH2=CH3-CH3-CH3" -> [Organico -> "but-1-eno"] -> OrganicoResultado

    // TODO: los errores del .py?

    // TODO: [OpsinResultado -> Organico]
        // 1. Quitar enlaces de hidrógenos a carbonos, y esos átomos
        // 2. Hay más átomos que C, H?
            // ? Ver si hay una sola cadena con funciones o si hay más
            // : Empezar por alguno terminal
        // 3. ...

    // TODO: [Organico -> "but-1-eno"]


    public static void main(String[] args) {
        /*
        List<Integer> indices = Arrays.asList(0, 1, 1, 2);

        List<organico.componentes.organico.Localizador> localizadores = new ArrayList<>();
        localizadores.add(new organico.componentes.organico.Localizador(indices, "propil"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "hidroxi"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "amino"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "carmaboil"));

        organico.componentes.organico.Localizador.ordenarAlfabeticamente(localizadores);

        System.out.println(localizadores);
        */

        // pent-3-en-1-in-1-ol :

        CadenaSimple cadena_simple = new CadenaSimple();
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.alcohol));

        cadena_simple.enlazarCarbono();

        cadena_simple.enlazarCarbono();
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.hidrogeno));

        cadena_simple.enlazarCarbono();
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.hidrogeno));

        cadena_simple.enlazarCarbono();
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.hidrogeno));
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.hidrogeno));
        cadena_simple.enlazarSustituyente(new Sustituyente(Id.hidrogeno));

        System.out.print("Fórmula: ");
        System.out.println(cadena_simple.getFormula());

        // ////////////////////////

        while(true) {
            String input = new Scanner(System.in).nextLine();

            OpsinResultado resultado = Opsin.procesarNombreES(input); // El input debe ser un nombre en español
            String smiles = resultado.getSmiles();

            if(smiles != null) { // Lo encuentra?
                System.out.print("es: ");
                System.out.println(smiles);
            }
            else {
                resultado = Opsin.procesarNombreEN(input); // El input debe ser un nombre en inglés
                smiles = resultado.getSmiles();

                if(smiles != null) { // Lo encuentra?
                    System.out.print("en: ");
                    System.out.println(smiles);
                }
                else System.out.println("No se ha encontrado ni en español ni en inglés");
            }
        }

        /*
        List<organico.componentes.Sustituyente> sustituyentes = new ArrayList<>();
        sustituyentes.add(new organico.componentes.Sustituyente(organico.componentes.Id.hidrogeno));
        sustituyentes.add(new organico.componentes.Sustituyente(1, false));
        sustituyentes.add(new organico.componentes.Sustituyente(3, true));
        sustituyentes.add(new organico.componentes.Sustituyente(organico.componentes.Id.acido));
        sustituyentes.add(new organico.componentes.Sustituyente(organico.componentes.Id.acido));

        Organico.ordenarPorFunciones(sustituyentes);
        System.out.println(sustituyentes);
        */

        /*
        organico.componentes.Carbono carbono = new organico.componentes.Carbono(0);

        carbono.nuevoSustituyente(new organico.componentes.Sustituyente(organico.componentes.Id.hidrogeno));

        carbono.nuevoSustituyente(new organico.componentes.Sustituyente(organico.componentes.Id.cetona));
        carbono.nuevoSustituyente(new organico.componentes.Sustituyente(organico.componentes.Id.cloro));

        System.out.println(carbono);
        */
    }

}
