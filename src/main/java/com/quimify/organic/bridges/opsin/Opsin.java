package com.quimify.organic.bridges.opsin;

import com.quimify.organic.opsin.en.OpsinEN;
import com.quimify.organic.opsin.es.OpsinES;

import java.util.Optional;

public class Opsin {

    public static Optional<OpsinResult> parseSpanishName(String name) {
        Optional<OpsinResult> opsinResult;

        OpsinES opsinES = new OpsinES(name);

        opsinResult = opsinES.isPresent()
                ? Optional.of(new OpsinResult(opsinES.getSmiles(), opsinES.getCml()))
                : Optional.empty();

        return opsinResult;
    }

    public static Optional<OpsinResult> parseEnglishName(String name) {
        Optional<OpsinResult> opsinResult;

        OpsinEN opsinEN = new OpsinEN(name);

        opsinResult = opsinEN.isPresent()
                ? Optional.of(new OpsinResult(opsinEN.getSmiles(), opsinEN.getCml()))
                : Optional.empty();

        return opsinResult;
    }

}
