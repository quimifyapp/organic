import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.Carbon;
import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        new Substituent(Group.alkene);

        List<Substituent> sus = new ArrayList<>();
        sus.add(new Substituent(1));
        sus.add(new Substituent(2));
        sus.add(new Substituent(3, true));
        sus.add(new Substituent(3));
        sus.add(new Substituent(4, true));
        sus.add(new Substituent(4));

        sus.sort(Substituent::compareTo);
        System.out.println(sus);

        Carbon a = new Carbon(0);
        a.bond(new Substituent(2));
        a.bond(new Substituent(3, true));
        a.bond(Group.hydrogen);
        a.bond(Group.acid);

        Carbon b = new Carbon(0);
        b.bond(Group.acid);
        b.bond(new Substituent(2));
        b.bond(new Substituent(3, true));
        b.bond(Group.hydrogen);

        System.out.println(a.equals(b));
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());

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

        System.out.print("Input sequence:\t\t\t\t");
        System.out.println(inputSequence);
        System.out.print("Input structure: \t\t\t");
        System.out.println(openChain.getStructure());

        openChain.correct();
        System.out.print("Corrected input structure:\t");
        System.out.println(openChain.getStructure());

        String name = openChain.getName();
        System.out.print("Name given structure:\t\t");
        System.out.println(name);
        System.out.print("Structure given name:\t\t");
        System.out.println(OrganicFactory.getFromName(name).getStructure());
    }

}
