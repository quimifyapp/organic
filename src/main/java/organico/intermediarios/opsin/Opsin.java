package organico.intermediarios.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

public class Opsin {

    protected static final es.opsin.NameToStructure opsin_es = es.opsin.NameToStructure.getInstance();
    protected static final NameToStructure opsin_en = NameToStructure.getInstance();

    public static OpsinResultado procesarNombreES(String nombre) {
        // Nuestra adaptación al español de la librería OPSIN rechaza el prefijo "ácido", por eso se elimina:
        nombre = nombre.replaceFirst("ácido|acido", "");

        // La librería OPSIN rechaza el prefijo "di" de los éteres simétricos en algunos casos:
        nombre = corregirEter(nombre);

        es.opsin.OpsinResult opsin_result = opsin_es.parseChemicalName(nombre);

        return new OpsinResultado(opsin_result);
    }
    public static OpsinResultado procesarNombreEN(String nombre) {
        nombre = corregirEter(nombre);

        OpsinResult opsin_result = opsin_en.parseChemicalName(nombre);

        return new OpsinResultado(opsin_result);
    }

    private static String corregirEter(String nombre) {
        if(nombre.contains("éter") || nombre.contains("eter") || nombre.contains("ether")) {
            String arreglado = nombre.replaceFirst("di", "");

            // Puede que el "di" no sea porque es simétrico, sino que sea el cuantificador de un sustituyente:
            if(arreglado.trim().split("\\s+").length == 2) // Como "etil éter"
                nombre = arreglado;
        }

        return nombre;
    }

}
