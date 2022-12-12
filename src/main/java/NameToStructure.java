import com.quimify.organic.OrganicFactory;
import com.quimify.organic.OrganicResult;

import java.util.Scanner;

public class NameToStructure {

    public static void main(String[] args) {
        while (true) {
            System.out.print("Name: ");
            String name = new Scanner(System.in).nextLine();

            OrganicResult organicResult = OrganicFactory.getFromName(name);

            System.out.println("Structure: " + organicResult.getStructure());
            System.out.println();
        }
    }

}
