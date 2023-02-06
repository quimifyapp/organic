import com.quimify.organic.Organic;
import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class StructureToName {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        OpenChain openChain = new Simple();
        List<Integer> inputSequence = new ArrayList<>();

        while(!openChain.isDone()) {
            List<Group> bondableGroups = openChain.getBondableGroups();

            for (Group bondableGroup : bondableGroups)
                System.out.println(bondableGroup.ordinal() + ": " + bondableGroup);

            boolean canBondCarbon = openChain.canBondCarbon();

            if(canBondCarbon)
                System.out.println("-1: -C");

            System.out.println();
            System.out.println(openChain.getStructure());

            System.out.print("Bond: ");
            int input = new Scanner(System.in).nextInt();
            inputSequence.add(input);

            if(input == -1 && canBondCarbon) {
                openChain.bondCarbon();
                System.out.println();
                continue;
            }

            Group group = Group.values()[input];

            if(group == Group.radical) {
                System.out.print("Normal or iso? [0/1]: ");
                int isoCode = new Scanner(System.in).nextInt();
                inputSequence.add(isoCode);

                System.out.print("Carbons: ");
                int carbons = new Scanner(System.in).nextInt();
                inputSequence.add(carbons);

                openChain = openChain.bond(new Substituent(carbons, isoCode == 1));
            }
            else openChain = openChain.bond(group);

        }

        System.out.print("Input sequence:\t\t\t");
        System.out.println(inputSequence);
        System.out.print("Input structure: \t\t");
        System.out.println(openChain.getStructure());

        Organic organic = OrganicFactory.getFromOpenChain(openChain);

        System.out.print("Corrected structure:\t");
        System.out.println(organic.getStructure());

        String name = organic.getName();
        System.out.print("Name given structure:\t");
        System.out.println(name);

        System.out.print("Structure given name:\t");

        try {
            Optional<Organic> organicFromName = OrganicFactory.getFromName(name);

            if (organicFromName.isPresent())
                System.out.print(organicFromName.get().getStructure());
            else System.out.print("Not found");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        System.out.println();
    }

}
