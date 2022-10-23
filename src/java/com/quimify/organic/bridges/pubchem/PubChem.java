package com.quimify.organic.bridges.pubchem;

import com.quimify.utils.Download;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PubChem {

	private static final Logger logger = Logger.getLogger(PubChem.class.getName());

	String smiles;

	private static final String DIR = "https://pubchem.ncbi.nlm.nih.gov/";
	private static final String REST = DIR + "rest/pug/compound/";
	private static final String PNG_2D = DIR + "image/imagefly.cgi?width=500&height=500&cid=";

	private static final String SMILES = "smiles/";
	private static final String PNG = "/PNG";

	// --------------------------------------------------------------------------------

	// Constructor:

	public PubChem(String smiles) {
		this.smiles = smiles;
	}

	public PubChemResult getResult() {
		PubChemResult resultado = new PubChemResult();

		smiles = Download.formatForHTTP(smiles);

		String url = REST + SMILES + smiles + "/cids/TXT";
		try {
			String cid = new Download(url).getText();

			if(!cid.equals("0")) {
				resultado.setUrl_2d(PNG_2D + cid); // Este es de buena calidad (500 x 500 px) :)

				String base = REST + "cid/" + cid + "/property/";
				try {
					resultado.setMasa(new Download(base + "molecularweight/TXT").getText());
				}
				catch(IOException ex) {
					logger.log(Level.SEVERE, "Excepción al descargar \"" + base + "molecularweight/TXT" + "\": " + ex);
				}

				try {
					resultado.setNombre_ingles(new Download(base + "iupacname/TXT").getText());
				}
				catch(IOException ex) {
					logger.log(Level.SEVERE, "Excepción al descargar \"" + base + "iupacname/TXT" + "\": " + ex);
				}
			}
		}
		catch(IOException ex) {
			logger.log(Level.SEVERE, "Excepción al descargar \"" + url + "\": " + ex);
		}

		if(resultado.getUrl_2d() == null)
			resultado.setUrl_2d(REST + SMILES + smiles + PNG); // Este es de mala calidad (300 x 300) px :(

		return resultado;
	}

}
