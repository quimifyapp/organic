import com.quimify.organic.OrganicFactory;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.compounds.open_chain.Simple;

public class Main {

    public static void main(String[] args) {
        Simple simple = new Simple();

        simple.bond(FunctionalGroup.amine);
        simple.bondCarbon();
        simple.bond(FunctionalGroup.amine);

        simple.correctSubstituents();

        System.out.println(simple.getName());
        System.out.println(OrganicFactory.getFromName("etinodiamina").getStructure());
    }

}
