import java.util.HashSet;
import java.util.Set;

public class Carbon {

    private Set<Substituent> substituents = new HashSet<>();
    int free_bonds;

    public Carbon(int previous_bonds) {
        free_bonds = 4 - previous_bonds;
    }

    // INTERFACE:

    public void addSubstituent(Substituent sub){
        substituents.add(sub);
    }

    public void deleteSubstituent(Substituent sub) {
        substituents.remove(sub);
    }

    publ

}
