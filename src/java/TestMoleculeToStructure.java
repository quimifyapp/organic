import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.compounds.open_chain.Simple;

import java.util.*;
import java.util.stream.Collectors;

public class TestMoleculeToStructure {

    public static void main(String[] args) {
        int count = 0;

        while(true) {
            Simple simple = new Simple();

            while(!simple.isDone()) {
                if(new Random().nextBoolean() || simple.getFreeBonds() == 4) {
                    if(new Random().nextBoolean()) {
                        List<FunctionalGroup> bondableGroups = simple.getOrderedBondableGroups().stream()
                                .filter(group ->
                                        !Set.of(FunctionalGroup.ether, FunctionalGroup.radical).contains(group))
                                .collect(Collectors.toList());

                        simple.bond(bondableGroups.get(new Random().nextInt(bondableGroups.size())));
                    }
                    else simple.bond(new Random().nextBoolean()
                                ? new Substituent(new Random().nextInt(1) + 1)
                                : new Substituent(new Random().nextInt(3) + 3, true));
                }
                else simple.bondCarbon();
            }

            simple.correctSubstituents();

            String generatedStructure = OrganicFactory.getFromName(simple.getName()).getStructure();

            System.out.println(simple.getStructure());

            if(!simple.getStructure().equals(generatedStructure)) {
                if(!simple.getStructure().replace("(CH3)", "")
                        .equals(generatedStructure.replace("(CH3)", ""))) {
                    System.out.println("=/=");
                    System.out.println(generatedStructure);
                    System.out.println("Tested: " + count);
                    System.out.println();
                }
            }

            count++;
        }
    }

}
