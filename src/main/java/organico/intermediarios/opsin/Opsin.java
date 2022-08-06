package organico.intermediarios.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

public class Opsin {

    protected static final es.opsin.NameToStructure opsin_es = es.opsin.NameToStructure.getInstance();
    protected static final NameToStructure opsin_en = NameToStructure.getInstance();

    public static OpsinResultado procesarNombreES(String nombre) {
        // Nuestra adaptación al español de OPSIN rechaza el prefijo "ácido", por eso se elimina:
        nombre = nombre.replaceFirst("ácido ", "");

        es.opsin.OpsinResult opsin_result = opsin_es.parseChemicalName(nombre);

        return new OpsinResultado(opsin_result);
    }

    public static OpsinResultado procesarNombreEN(String nombre) {
        OpsinResult opsin_result = opsin_en.parseChemicalName(nombre);

        return new OpsinResultado(opsin_result);
    }

}
