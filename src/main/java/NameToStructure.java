import com.quimify.organic.Organic;
import com.quimify.organic.OrganicFactory;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

public class NameToStructure {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        while (true) {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            try {
                Optional<Organic> organic = OrganicFactory.getFromName(name);

                if (organic.isPresent()) {
                    System.out.println("Smiles: " + organic.get().getSmiles());
                    System.out.println("Structure: " + organic.get().getStructure());
                }
                else System.out.println("Not found");

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            System.out.println();
        }
    }

}
