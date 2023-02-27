import com.quimify.organic.components.Group;
import com.quimify.organic.components.Substituent;
import com.quimify.organic.molecules.openchain.OpenChain;
import com.quimify.organic.molecules.openchain.Simple;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RandomStructureToName {

    private static final Random random = new Random();

    // Settings:

    private static final boolean allowDuplicates = false;
    private static final long numberOfOpenChains = 1 << 14; // 2^N

    private static final boolean printToConsole = true;

    private static final String structuresOutputPath = "structures.txt";
    private static final String namesOutputPath = "names.txt";

    private static final int bondCarbonPeriod = 8;
    private static final int bondHydrogenPeriod = 2;
    private static final int bondIsoRadicalPeriod = 10;

    private static final int maximumCarbonsInRadicals = 3;

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        HashSet<String> structures = new HashSet<>();

        StringBuilder names = new StringBuilder();

        int openChainCount = 0;

        while (openChainCount <= numberOfOpenChains) {
            OpenChain openChain = getRandomOpenChain();

            String structure = openChain.getStructure();

            if (!allowDuplicates && structures.contains(structure))
                continue;

            structures.add(structure);

            String name = openChain.getName();
            names.append('\n').append(name);

            if (printToConsole) {
                System.out.println(openChainCount);
                System.out.println(structure);
                System.out.println(name);
                System.out.println();
            }

            openChainCount++;
        }

        try {
            PrintWriter structuresFile = new PrintWriter(structuresOutputPath, StandardCharsets.UTF_8);

            for (String structure : structures)
                structuresFile.println(structure);

            structuresFile.close();

            System.out.println("Structures saved to: " + structuresOutputPath);

            PrintWriter namesFile = new PrintWriter(namesOutputPath, StandardCharsets.UTF_8);

            namesFile.println(names);
            namesFile.close();

            System.out.println("Names saved to: " + namesOutputPath);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    // Private:

    private static OpenChain getRandomOpenChain() {
        OpenChain openChain = new Simple();

        while(!openChain.isDone()) {
            if(openChain.canBondCarbon() && random.nextInt(bondCarbonPeriod) == 0) {
                openChain.bondCarbon();
                continue;
            }

            if (random.nextInt(bondHydrogenPeriod) == 0) {
                openChain = openChain.bond(new Substituent(Group.hydrogen));
                continue;
            }

            List<Group> bondableGroups = openChain.getBondableGroups();
            Substituent randomSubstituent = getRandomSubstituent(bondableGroups);

            openChain = openChain.bond(randomSubstituent);
        }

        openChain.standardize();

        return openChain;
    }

    private static Substituent getRandomSubstituent(List<Group> bondableGroups) {
        Group group = bondableGroups.get(random.nextInt(bondableGroups.size()));

        Substituent substituent;

        if (group != Group.radical)
            substituent = new Substituent(group);
        else if (random.nextInt(bondIsoRadicalPeriod) == 0 && maximumCarbonsInRadicals >= 3) {
            int extraCarbons = maximumCarbonsInRadicals - 3;
            substituent = Substituent.radical(3 + (extraCarbons == 0 ? 0 : random.nextInt(extraCarbons)), true);
        }
        else substituent = Substituent.radical(1 + random.nextInt(maximumCarbonsInRadicals - 1));

        return substituent;
    }

}
