import organico.componentes.*;
import organico.intermediarios.opsin.Opsin;
import organico.intermediarios.opsin.OpsinResultado;
import organico.intermediarios.pubchem.PubChem;
import organico.intermediarios.pubchem.PubChemResultado;
import organico.tipos.*;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Main {

    // Formular: "but-1-eno" → [OpsinResultado → Organico] → OrganicoResultado
    // Nombrar: "CH2=CH3-CH3-CH3" → [Organico → "but-1-eno"] → OrganicoResultado

    // ////////////////////////////////////////////////////////

    // TODO: [OpsinResultado → Organico]
    //      1. Quitar enlaces de hidrógenos a carbonos, y esos átomos
    //      2. Hay más átomos que C, H?
    //          ? Ver si hay una sola cadena con funciones o si hay más
    //          : Empezar por alguno terminal
    //      3. ...

    // TODO: [Organico → "but-1-eno"]

    // TODO: Aprender: alertas sobre el proceso con cada solución (Ej.: se ha escogido otra cadena principal más larga)

    // ////////////////////////////////////////////////////////

    // TODO: Eter corregir()

    // TODO: OPSIN amidas (aminas, nitrilos, carbamoil?)

    // TODO: -CHO es carbaldehído (añadirlo para ciclos?)

    private static final Scanner scanner = new Scanner(System.in);

    private static void probarEter() {
        while(true) {
            probarOpsinPubChem(inputEter());
        }
    }

    private static void probarCadenaSimple() {
        while(true) {
            probarOpsinPubChem(inputCadenaSimple());
        }
    }

    private static void probarCadenaSimpleAleatorio() {
        while(true) {
            probarOpsinPubChem(inputCadenaSimpleAleatorio());
        }
    }

    public static void main(String[] args) {
        new Opsin(); // Para cargar sus recursos ya y no esperar después

        // Elegir uno:

        //probarOPSIN();

        probarCadenaSimple();
        //probarCadenaSimpleAleatorio();

        //probarEter();

        /*
        List<Integer> indices = Arrays.asList(0, 1, 1, 2);

        List<organico.componentes.organico.Localizador> localizadores = new ArrayList<>();
        localizadores.add(new organico.componentes.organico.Localizador(indices, "propil"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "hidroxi"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "amino"));
        localizadores.add(new organico.componentes.organico.Localizador(indices, "carbamoil"));

        organico.componentes.organico.Localizador.ordenarAlfabeticamente(localizadores);

        System.out.println(localizadores);
        */

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

    private static String inputCadenaSimple() {
        Simple cadena_simple = new Simple();

        List<Integer> elecciones = new ArrayList<>();
        boolean primer_carbono = true;
        while(!cadena_simple.estaCompleta()) {
            System.out.println("Fórmula: " + cadena_simple);

            if(!primer_carbono)
                System.out.println("0: C");

            List<Id> disponibles = cadena_simple.getSustituyentesDisponibles();
            for(int i = 0; i < disponibles.size(); i++) {
                if(disponibles.get(i) != Id.radical)
                    System.out.println((i + 1) + ": " + new Sustituyente(disponibles.get(i)));
                else System.out.println((i + 1) + ": " + "-CH2(...)CH3");
            }

            System.out.print("Elección: ");
            int eleccion = scanner.nextInt();
            elecciones.add(eleccion);

            if(eleccion == 0 && !primer_carbono)
                cadena_simple.enlazarCarbono();
            else if(disponibles.get(eleccion - 1) != Id.radical)
                cadena_simple.enlazar(disponibles.get(eleccion - 1));
            else {
                System.out.println();
                System.out.println("0: -CH2(...)CH3");
                System.out.println("1: -CH2(...)CH(CH3)2");

                System.out.print("Elección: ");
                eleccion = scanner.nextInt();
                elecciones.add(eleccion);

                System.out.print("Carbonos en el radical: ");
                int carbonos = scanner.nextInt();
                elecciones.add(carbonos);

                cadena_simple.enlazar(new Sustituyente(carbonos, eleccion == 1));
            }
            System.out.println();

            if(primer_carbono)
                primer_carbono = false;
        }

        System.out.println();

        System.out.print("Secuencia:");
        for(int eleccion : elecciones)
            System.out.print(" " + eleccion);
        System.out.println();

        System.out.println();
        System.out.println("Fórmula: " + cadena_simple);
        cadena_simple.corregir();
        System.out.println("Corregida: " + cadena_simple);

        return cadena_simple.getNombre();
    }

    private static String inputCadenaSimpleAleatorio() {
        Simple cadena_simple = new Simple();

        List<Integer> elecciones = new ArrayList<>();
        boolean primer_carbono = true;
        while(!cadena_simple.estaCompleta()) {
            System.out.println("Fórmula: " + cadena_simple);

            if(!primer_carbono)
                System.out.println("0: C");

            List<Id> disponibles = cadena_simple.getSustituyentesDisponibles();
            for(int i = 0; i < disponibles.size(); i++) {
                if(disponibles.get(i) != Id.radical)
                    System.out.println((i + 1) + ": " + new Sustituyente(disponibles.get(i)));
                else System.out.println((i + 1) + ": " + "-CH2(...)CH3");
            }

            System.out.print("Elección: ");
            int eleccion = ThreadLocalRandom.current().nextInt(0, 2) == 0 && !primer_carbono
                    ? 0
                    : (ThreadLocalRandom.current().nextInt(0, 2) == 0
                    ? ThreadLocalRandom.current().nextInt(1, disponibles.size())
                    : disponibles.size() - 1);
            elecciones.add(eleccion);

            if(eleccion == 0 && !primer_carbono)
                cadena_simple.enlazarCarbono();
            else if(disponibles.get(eleccion - 1) != Id.radical)
                cadena_simple.enlazar(disponibles.get(eleccion - 1));
            else {
                System.out.println();
                System.out.println("0: -CH2(...)CH3");
                System.out.println("1: -CH2(...)CH(CH3)2");

                System.out.print("Elección: ");
                eleccion = ThreadLocalRandom.current().nextInt(0, 2);
                elecciones.add(eleccion);

                System.out.print("Carbonos en el radical: ");
                int carbonos = ThreadLocalRandom.current().nextInt(3, 5);
                elecciones.add(carbonos);

                cadena_simple.enlazar(new Sustituyente(carbonos, eleccion == 1));
            }
            System.out.println();

            if(primer_carbono)
                primer_carbono = false;
        }

        System.out.println();

        System.out.print("Secuencia:");
        for(int eleccion : elecciones)
            System.out.print(" " + eleccion);
        System.out.println();

        System.out.println();
        System.out.println("Fórmula: " + cadena_simple);
        cadena_simple.corregir();
        System.out.println("Corregida: " + cadena_simple);

        return cadena_simple.getNombre();
    }

    private static String inputEter() {
        Eter eter = new Eter();

        List<Integer> elecciones = new ArrayList<>();
        boolean primer_carbono = true;

        while(!eter.estaCompleta()) {
            System.out.println("Fórmula: " + eter);

            if(!primer_carbono)
                System.out.println("0: C");

            List<Id> disponibles = eter.getSustituyentesDisponibles();
            for(int i = 0; i < disponibles.size(); i++) {
                if(disponibles.get(i) != Id.radical)
                    System.out.println((i + 1) + ": " + new Sustituyente(disponibles.get(i)));
                else System.out.println((i + 1) + ": " + "-CH2(...)CH3");
            }

            System.out.print("Elección: ");
            int eleccion = scanner.nextInt();
            elecciones.add(eleccion);

            if(eleccion == 0 && !primer_carbono)
                eter.enlazarCarbono();
            else if(disponibles.get(eleccion - 1) != Id.radical)
                eter.enlazar(disponibles.get(eleccion - 1));
            else {
                System.out.println();
                System.out.println("0: -CH2(...)CH3");
                System.out.println("1: -CH2(...)CH(CH3)2");

                System.out.print("Elección: ");
                eleccion = scanner.nextInt();
                elecciones.add(eleccion);

                System.out.print("Carbonos en el radical: ");
                int carbonos = scanner.nextInt();
                elecciones.add(carbonos);

                eter.enlazar(new Sustituyente(carbonos, eleccion == 1));
            }
            System.out.println();

            if(primer_carbono)
                primer_carbono = false;
        }

        System.out.println();

        System.out.print("Secuencia:");
        for(int eleccion : elecciones)
            System.out.print(" " + eleccion);
        System.out.println();

        System.out.println();
        System.out.println("Fórmula: " + eter);
        eter.corregir();
        System.out.println("Corregida: " + eter);

        return eter.getNombre();
    }

    private static void probarOpsinPubChem(String nombre) {
        System.out.println("Nombre: " + nombre);
        System.out.println();

        OpsinResultado opsin_resultado = Opsin.procesarNombreES(nombre);
        String smiles = opsin_resultado.getSmiles();

        System.out.println("OPSIN smiles: " + opsin_resultado.getSmiles());

        if(smiles != null) {
            PubChemResultado pub_chem_resultado = PubChem.procesarSmiles(smiles);

            System.out.print("PubChem masa: ");
            if(pub_chem_resultado.getMasa().isPresent())
                System.out.println(pub_chem_resultado.getMasa().get() + " g/mol");
            else System.out.println("no encontrada");

            String url_2d = pub_chem_resultado.getUrl_2d();
            System.out.println("PubChem 2D: " + url_2d);
            System.out.println();

            try {
                //Desktop.getDesktop().browse(new URI(url_2d));
            }
            catch(Exception ignore) {}
        }
        else System.out.println("Error en OPSIN");

        try {
            //System.out.println("Enter para continuar...");
            //System.in.read();
        }
        catch(Exception ignore) {}
    }

    private static void probarOPSIN() {
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

                if (smiles != null) { // Lo encuentra?
                    System.out.print("en: ");
                    System.out.println(smiles);
                } else System.out.println("No se ha encontrado ni en español ni en inglés");
            }
        }
    }

}
