package com.quimify.organic;

import com.quimify.organic.opsin.Opsin;
import com.quimify.organic.opsin.OpsinResult;
import com.quimify.organic.molecules.Molecule;
import com.quimify.organic.molecules.openchain.OpenChain;

import java.util.Optional;

public class OrganicFactory {

    public static Optional<Organic> getFromName(String name, String language) {
        Optional<OpsinResult> opsinResult = language.equals("es") ? Opsin.parseSpanishName(name) : Opsin.parseEnglishName(name);

        if (opsinResult.isEmpty())
            return Optional.empty();

        Organic organic;

        try {
            Optional<Molecule> molecule = Molecule.from(opsinResult.get().getCml(), opsinResult.get().getSmiles());

            if(molecule.isPresent()) {
                Optional<OpenChain> openChain = molecule.get().toOpenChain();
                String structure = openChain.map(OpenChain::getStructure).orElse(null);

                organic = new Organic(name, opsinResult.get().getSmiles(), structure);
            }
            else organic = new Organic(name, opsinResult.get().getSmiles(), (Exception) null);
        } catch (Exception structureException) {
            organic = new Organic(name, opsinResult.get().getSmiles(), structureException);
        }

        return Optional.of(organic);
    }

    public static Organic getFromOpenChain(OpenChain openChain) {
        openChain.standardize();

        String name = openChain.getName();

        Optional<OpsinResult> opsinResult = Opsin.parseSpanishName(name);
        String smiles = opsinResult.map(OpsinResult::getSmiles).orElse(null);

        String structure = openChain.getStructure();

        return new Organic(name, smiles, structure);
    }

}
