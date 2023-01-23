package com.quimify.organic.opsin;

import java.util.Optional;

public class Opsin {

    public static Optional<OpsinResult> parseSpanishName(String name) {
        OpsinES opsinES = new OpsinES(name);

        if (!opsinES.isPresent())
            return Optional.empty();

        return Optional.of(new OpsinResult(opsinES.getSmiles(), opsinES.getCml()));
    }

}
