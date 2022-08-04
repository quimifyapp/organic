package organico.pubchem;

import conexion.Conexion;

import java.io.IOException;

public class PubChem {

	private static final String DIR = "https://pubchem.ncbi.nlm.nih.gov/";
	private static final String REST = DIR + "rest/pug/compound/";
	private static final String PNG_2D = DIR + "image/imagefly.cgi?width=500&height=500&cid=";

	private static final String SMILES = "smiles/";

	public static PubChemResultado procesarSmiles(String smiles) {
		PubChemResultado resultado = new PubChemResultado();

		try {
			String cid = new Conexion(REST + SMILES + smiles + "/cids/TXT").getTexto();

			if(!cid.equals("0")) {
				resultado.setUrl_2d(PNG_2D + cid); // Este es de buena calidad (500 x 500 px) :)

				try {
					String masa = new Conexion(REST + "cid/" + cid + "/property/MolecularWeight/TXT").getTexto();
					resultado.setMasa(masa);
				}
				catch(IOException exception) {
					// Error...
				}
			}
			else resultado.setUrl_2d(REST + SMILES + smiles + "/PNG"); // Este es de mala calidad (300 x 300 px) :(
		}
		catch(IOException exception) {
			resultado.setUrl_2d(REST + SMILES + smiles + "/PNG"); // Este es de mala calidad (300 x 300 px) :(
			// Error...
		}

		return resultado;
	}

}
