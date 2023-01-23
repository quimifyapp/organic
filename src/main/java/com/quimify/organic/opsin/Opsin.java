package com.quimify.organic.opsin;

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

}
