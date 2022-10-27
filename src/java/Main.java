import com.quimify.organic.opsin.es.OpsinES;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    // Nombre orgánico:
    // - Particulas
    // - Excepciones

    // - Tiene dígitos, comas, guiones
    // - "ano" pero no lantano, mangano
    // - met, but, prop,

    /*
    private static final String[] notOrganicStructureParticles = {"tc", "ac", "sc"};

    public static boolean seemsLikeOrganicStructure(String lowerCaseInput) {
        boolean accordingToRegex = lowerCaseInput.matches(".*c[0-9]*h.*")
                || lowerCaseInput.matches("[c]")
        return lowerCaseInput.matches(".*C[0-9]*-?H.*") || ;
    }
     */

    public static boolean isOrganicName(String input) {
        return new OpsinES(input).isPresent();
    }

    public static void main(String[] args) {
        while (true) {
            String input = new Scanner(System.in).nextLine();

            System.out.println(isOrganicName(input));
        }
    }

}
