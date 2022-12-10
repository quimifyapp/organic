import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        OpenChain openChain = new Simple();
        List<Integer> inputSequence = new ArrayList<>();

        while(!openChain.isDone()) {
            List<Group> bondableGroups = openChain.getBondableGroups();

            for(int i = 0; i < bondableGroups.size(); i++)
                System.out.println(i + ": " + bondableGroups.get(i));

            if(openChain.canBondCarbon())
                System.out.println(-1 + ": -C");

            System.out.println();
            System.out.println(openChain.getStructure());
            System.out.print("Bond: ");
            int input = new Scanner(System.in).nextInt();
            inputSequence.add(input);

            if(input == -1)
                openChain.bondCarbon();
            else if(bondableGroups.get(input) == Group.radical) {
                System.out.print("Normal or iso? [0/1]: ");
                int isoCode = new Scanner(System.in).nextInt();
                inputSequence.add(isoCode);

                System.out.print("Carbons: ");
                int carbons = new Scanner(System.in).nextInt();
                inputSequence.add(carbons);

                openChain = openChain.bond(new Substituent(carbons, isoCode == 1));
            }
            else openChain = openChain.bond(bondableGroups.get(input));

            System.out.println();
        }

        System.out.print("Input sequence: ");
        System.out.println(inputSequence);
        System.out.print("Input structure: ");
        System.out.println(openChain.getStructure());

        openChain.correct();
        System.out.print("Corrected input structure: ");
        System.out.println(openChain.getStructure());

        String name = openChain.getName();
        System.out.print("Name given structure: ");
        System.out.println(name);
        System.out.print("Structure given name: ");
        System.out.println(OrganicFactory.getFromName(name).getStructure());
    }

}
