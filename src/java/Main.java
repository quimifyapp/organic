import com.quimify.organic.OrganicFactory;
import com.quimify.organic.bridges.opsin.Opsin;
import com.quimify.organic.components.FunctionalGroup;
import com.quimify.organic.compounds.open_chain.OpenChain;
import com.quimify.organic.compounds.open_chain.Simple;

public class Main {

    public static void main(String[] args) {
        OpenChain openChain = new Simple();
        openChain.bond(FunctionalGroup.hydrogen);
        openChain.bond(FunctionalGroup.hydrogen);
        openChain.bond(FunctionalGroup.hydrogen);
        openChain.bond(FunctionalGroup.hydrogen);

        OrganicFactory.getFromName("agua");

        System.out.println(OrganicFactory.getFromOpenChain(openChain).getUrl_2d());
        System.out.println(Opsin.procesarNombreES("ethylmethylamine").get().getSmiles());
        System.out.println(Opsin.procesarNombreEN("ethylmethylamine").get().getSmiles());
    }

}
