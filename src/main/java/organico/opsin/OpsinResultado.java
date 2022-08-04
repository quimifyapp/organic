package organico.opsin;

import uk.ac.cam.ch.wwmm.opsin.*;

public class OpsinResultado {

    private boolean correcto;
    private String smiles;

    // Constructores:

    public OpsinResultado(es.opsin.OpsinResult opsin_result) {
        correcto = opsin_result.getStatus() == es.opsin.OpsinResult.OPSIN_RESULT_STATUS.SUCCESS;

        if(correcto) {
            // ...
            smiles = opsin_result.getSmiles();
        }
    }

    public OpsinResultado(OpsinResult opsin_result) {
        correcto = opsin_result.getStatus() == OpsinResult.OPSIN_RESULT_STATUS.SUCCESS;

        if(correcto) {
            // ...
            smiles = opsin_result.getSmiles();
        }
    }

    // Métodos get:

    public String getSmiles() {
        return smiles;
    }

}
