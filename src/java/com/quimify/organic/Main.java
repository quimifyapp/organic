package com.quimify.organic;

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

        System.out.println(openChain.getName());
    }
}
