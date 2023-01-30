package com.quimify.organic;

import com.quimify.organic.opsin.Opsin;
import com.quimify.organic.opsin.OpsinResult;
import com.quimify.organic.molecules.Molecule;
import com.quimify.organic.molecules.open_chain.OpenChain;
import java.util.Optional;

public class OrganicFactory {

    public static Optional<Organic> getFromName(String name) {
        Optional<OpsinResult> opsinResult = Opsin.parseSpanishName(name);

        if (opsinResult.isEmpty())
            return Optional.empty();

        Organic organic;

        String smiles = opsinResult.get().getSmiles();

        try {
            Molecule molecule = new Molecule(opsinResult.get().getCml(), smiles);
            Optional<OpenChain> openChain = molecule.toOpenChain();
            String structure = openChain.map(OpenChain::getStructure).orElse(null);

            organic = new Organic(name, smiles, structure);
        } catch (Exception exception) {
            organic = new Organic(name, smiles, exception);
        }

        return Optional.of(organic);
    }

    public static Organic getFromOpenChain(OpenChain openChain) {
        openChain.correct();

        String name = openChain.getName();

        Optional<OpsinResult> opsinResult = Opsin.parseSpanishName(name);
        String smiles = opsinResult.map(OpsinResult::getSmiles).orElse(null);

        String structure = openChain.getStructure();

        return new Organic(name, smiles, structure);
    }

}
