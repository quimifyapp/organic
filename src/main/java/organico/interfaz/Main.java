package organico.interfaz;

import organico.componentes.Carbono;
import organico.componentes.Id;

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

    // TODO: -CHO es carbaldehído (añadirlo para ciclos?)

    public static void main(String[] args) {
        Carbono carbono = new organico.componentes.Carbono(0);

        carbono.enlazar(Id.hidrogeno);
        carbono.enlazar(Id.cetona);
        carbono.enlazar(Id.cloro);

        System.out.println(carbono);

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
    }

}
