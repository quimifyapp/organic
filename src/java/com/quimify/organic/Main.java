package com.quimify.organic;

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

        System.out.println(OrganicFactory.getFromOpenChain(openChain).getUrl_2d());
        System.out.println(Opsin.procesarNombreEN("methane").get().getSmiles());
    }
}
