import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.compounds.open_chain.Simple;

import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class TestMoleculeToStructure {

    public static void main(String[] args) {
        while(true) {
            Simple simple = new Simple();

            while(!simple.isDone()) {
                if(new Random().nextBoolean() || simple.getFreeBonds() == 4) {
                    if(new Random().nextBoolean()) {
                        simple.bond(simple.getOrderedBondableGroups().stream().filter(functionalGroup ->
                                functionalGroup != FunctionalGroup.ether).findAny().get());
                    }
                    else simple.bond(new Random().nextBoolean()
                                ? new Substituent(new Random().nextInt(1) + 1)
                                : new Substituent(new Random().nextInt(3) + 3, true));
                }
                else simple.bondCarbon();
            }

            simple.correctSubstituents();

            String generatedStructure = OrganicFactory.getFromName(simple.getName()).getStructure();

            if(!simple.getStructure().equals(generatedStructure)) {
                System.out.println(simple.getStructure());
                System.out.println("=/=");
                System.out.println(generatedStructure);
                System.out.println();
            }
        }
    }

}
