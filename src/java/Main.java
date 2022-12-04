import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.compounds.open_chain.Simple;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Simple simple = new Simple();

        simple.bond(FunctionalGroup.hydrogen);
        simple.bond(FunctionalGroup.ketone);
        simple.bond(FunctionalGroup.amine);

        simple.correctSubstituents();

        System.out.println(simple.getName());
    }

}
