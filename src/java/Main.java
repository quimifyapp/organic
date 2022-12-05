import com.quimify.organic.OrganicFactory;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while(true) {
            String name = new Scanner(System.in).nextLine();
            System.out.println(OrganicFactory.getFromName(name).getStructure());
        }

    }

}
