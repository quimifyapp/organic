package com.quimify.organic.opsin;


import java.util.Optional;

public class Opsin {

    public static Optional<OpsinResult> parseSpanishName(String name) {
        OpsinSpanish opsinSpanish = new OpsinSpanish(name);

        if (!opsinSpanish.isPresent())
            return Optional.empty();

        return Optional.of(new OpsinResult(opsinSpanish.getSmiles(), opsinSpanish.getCml()));
    }

    public static Optional<OpsinResult> parseEnglishName(String name) {
        OpsinEnglish opsinEnglish = new OpsinEnglish(name);

        if (!opsinEnglish.isPresent())
            return Optional.empty();

        return Optional.of(new OpsinResult(opsinEnglish.getSmiles(), opsinEnglish.getCml()));
    }

}
