import com.quimify.organic.Organic;
import com.quimify.organic.OrganicFactory;

import java.util.Optional;
import java.util.Scanner;

public class NameToStructure {

    public static void main(String[] args) {
        while (true) {
            System.out.print("Name: ");
            String name = new Scanner(System.in).nextLine();

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
