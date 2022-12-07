import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.compounds.open_chain.OpenChain;
import com.quimify.organic.compounds.open_chain.Simple;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        OpenChain openChain = new Simple();

        List<Group> bondableGroups = openChain.getBondableGroups();

        while(bondableGroups.size() > 0) {
            for(int i = 0; i < bondableGroups.size(); i++)
                System.out.println(i + ": " + bondableGroups.get(i));

            if(openChain.canBondCarbon())
                System.out.println(-1 + ": -C");

            System.out.println();
            System.out.println(openChain.getStructure());
            System.out.print("Bond: ");
            int input = new Scanner(System.in).nextInt();

            if(input == -1)
                openChain.bondCarbon();
            else if(bondableGroups.get(input) == Group.radical) {
                System.out.print("Normal 0, Iso 1: ");
                int isoCode = new Scanner(System.in).nextInt();
                System.out.print("Carbons: ");
                int carbons = new Scanner(System.in).nextInt();

                openChain = openChain.bond(new Substituent(carbons, isoCode == 1));
            }
            else openChain = openChain.bond(bondableGroups.get(input));

            bondableGroups = openChain.getBondableGroups();

            System.out.println();
        }

        System.out.println("Estructura introducida: ");
        System.out.println(openChain.getStructure());

        openChain.correct();

        System.out.println("Estructura introducida corregida: ");
        System.out.println(openChain.getStructure());

        String name = openChain.getName();

        System.out.println("Nombre según esa estructura: ");
        System.out.println(name);

        System.out.println("Estructura según ese nombre: ");
        System.out.println(OrganicFactory.getFromName(name).getStructure());
    }

}
