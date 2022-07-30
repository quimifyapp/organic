package organico.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

public class Opsin {

    protected static final es.opsin.NameToStructure opsin_es = es.opsin.NameToStructure.getInstance();
    protected static final NameToStructure opsin_en = NameToStructure.getInstance();

    public static OpsinResultado procesarNombreES(String nombre) {
        OpsinResultado resultado = new OpsinResultado();

        es.opsin.OpsinResult opsin_result = opsin_es.parseChemicalName(nombre);
        resultado.setSmiles(opsin_result.getExtendedSmiles());

        return resultado;
    }

    public static OpsinResultado procesarNombreEN(String nombre) {
        OpsinResultado resultado = new OpsinResultado();

        OpsinResult opsin_result = opsin_en.parseChemicalName(nombre);
        resultado.setSmiles(opsin_result.getExtendedSmiles());

        return resultado;
    }

}
