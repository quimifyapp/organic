import com.quimify.organic.OrganicFactory;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while(true) {
            String name;

            //name = "etil metil eter";
            //name = "ácido 4-fluoro-2-oxobutanoico";
            //name = "ácido 3-butil-4-fluoro-2-oxobutanoico";
            //name = "ácido 3-isobutil-4-fluoro-2-oxobutanoico";
            //name = "ácido 3-terbutil-4-fluoro-2-oxobutanoico";
            //name = "ciclopentano";

            name = new Scanner(System.in).nextLine();

            System.out.println(OrganicFactory.getFromName(name).getStructure());
        }
    }

}
