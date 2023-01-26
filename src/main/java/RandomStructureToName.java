import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.open_chain.OpenChain;
import com.quimify.organic.molecules.open_chain.Simple;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class RandomStructureToName {

    public static void main(String[] args) { // TODO break up
        StringBuilder structures = new StringBuilder();
        StringBuilder names = new StringBuilder();
        int count = 0;

        while (count <= 262144) {
            OpenChain openChain = new Simple();

            while(!openChain.isDone()) {
                if(openChain.canBondCarbon() && new Random().nextInt(3) == 0) {
                    openChain.bondCarbon();
                    continue;
                }

                if(new Random().nextBoolean()) {
                    List<Group> bondableGroups = openChain.getBondableGroups();

                    Group group = bondableGroups.get(new Random().nextInt(bondableGroups.size()));

                    if(group == Group.radical) {
                        if(new Random().nextInt(10) == 0)
                            openChain = openChain.bond(new Substituent(new Random().nextInt(3) + 3, true));
                        else openChain = openChain.bond(new Substituent(new Random().nextInt(5) + 1));
                    }
                    else openChain = openChain.bond(group);
                }
                else openChain = openChain.bond(Group.hydrogen);
            }

            openChain.correct();
            String structure = openChain.getStructure();

            if(!structures.toString().contains(structure)) {
                String name = openChain.getName();

                structures.append('\n').append(structure);
                names.append('\n').append(name);

                //System.out.println(count);
                //System.out.println(structure);
                //System.out.println(name);
                //System.out.println();

                count++;
            }
        }

        try {
            PrintWriter structuresFile = new PrintWriter("formulas.txt");
            structuresFile.println(structures);

            structuresFile.close();

            PrintWriter namesFile = new PrintWriter("names.txt");
            namesFile.println(names);

            namesFile.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
