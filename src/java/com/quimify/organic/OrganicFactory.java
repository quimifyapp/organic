package com.quimify.organic;

import com.quimify.organic.bridges.opsin.Opsin;
import com.quimify.organic.bridges.opsin.OpsinResult;
import com.quimify.organic.bridges.pubchem.PubChem;
import com.quimify.organic.bridges.pubchem.PubChemResult;
import com.quimify.organic.compounds.Molecule;
import com.quimify.organic.compounds.open_chain.OpenChain;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganicFactory {

    private static final Logger logger = Logger.getLogger(OrganicFactory.class.getName());

    public static final OrganicResult organicNotFound = new OrganicResult(false); // Constante auxiliar

    // PUBLIC ------------------------------------------------------------------------

    public static OrganicResult getFromName(String name) {
        OrganicResult organicResult;

        Optional<OpsinResult> opsinResult = Opsin.parseSpanishName(name);
        if(opsinResult.isPresent()) {
            organicResult = new OrganicResult(true);

            organicResult.setName(name); // El input del usuario (puede estar mal escrito)

            complementViaPubChem(organicResult, opsinResult.get().getSmiles()); // Características

            // Structure:
            try {
                Molecule molecule = new Molecule(opsinResult.get().getCml(), opsinResult.get().getSmiles());

                Optional<String> formula = molecule.getStructure();
                formula.ifPresent(organicResult::setStructure);
            }
            catch(IllegalArgumentException exception) {
                logger.warning("Excepción al generar la fórmula de \"" + name + "\": " + exception); // It happens often
            }
            catch (Exception exception) {
                logger.log(Level.SEVERE, "Excepción al generar la fórmula de \"" + name + "\": " + exception);
            }
        }
        else organicResult = organicNotFound;

        return organicResult;
    }

    // TODO: handle exceptions
    public static OrganicResult getFromOpenChain(OpenChain openChain) {
        if(!openChain.isDone()) {
            logger.log(Level.SEVERE, "OpenChain incompleta: \"" + openChain.getStructure() + "\".");
            return organicNotFound;
        }

        OrganicResult organicResult = new OrganicResult(true);

        openChain.correctSubstituents(); // It´s necessary

        // Name:
        String name = openChain.getName();
        organicResult.setName(name);

        // Properties:
        Optional<OpsinResult> opsinResult = Opsin.parseSpanishName(name);
        opsinResult.ifPresent(result -> complementViaPubChem(organicResult, result.getSmiles()));

        // Structure:
        organicResult.setStructure(openChain.getStructure());

        return organicResult;
    }

    // PRIVATE -----------------------------------------------------------------------

    private static void complementViaPubChem(OrganicResult organicResult, String smiles) {
        PubChemResult pubChemResult = new PubChem(smiles).getResult();

        if(pubChemResult.getMasa() != null)
            organicResult.setMolecularMass(Float.valueOf(pubChemResult.getMasa()));

        organicResult.setUrl2D(pubChemResult.getUrl_2d());
    }

}
