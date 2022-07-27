import java.lang.reflect.Array;
import java.util.*;

import static java.util.Collections.swap;

public class Carbono {

    private final List<Sustituyente> sustituyentes = new ArrayList<>();
    int enlaces_libres;

    static final Map<Id, String> formulas = Map.ofEntries(
            Map.entry(Id.acido,"OOH"), Map.entry(Id.carboxil, "COOH"), Map.entry(Id.amida, "ONH2"),
            Map.entry(Id.carbamoil, "CONH2"), Map.entry(Id.nitrilo, "N"), Map.entry(Id.cianuro, "CN"),
            Map.entry(Id.aldehido, "HO"), Map.entry(Id.cetona, "O"), Map.entry(Id.alcohol, "OH"),
            Map.entry(Id.amina, "NH2"), Map.entry(Id.nitro, "NO2"), Map.entry(Id.bromo, "Br"),
            Map.entry(Id.cloro, "Cl"), Map.entry(Id.fluor, "F"), Map.entry(Id.yodo, "I")
    );

    static final List<Id> halogenos = Arrays.asList(Id.bromo, Id.cloro, Id.fluor, Id.yodo);

    public Carbono(int enlaces_previos) {
        enlaces_libres = 4 - enlaces_previos;
    }

    // Modificadores:

    public void nuevoSustituyente(Sustituyente sustituyente){
        sustituyentes.add(sustituyente);
        enlaces_libres -= sustituyente.getEnlaces();
    }

    public void eliminarSustituyente(Sustituyente sustituyente) {
        sustituyentes.remove(sustituyente); // No se ha eliminado su enlace
    }

    public void eliminarSustituyenteConEnlaces(Sustituyente sustituyente) {
        eliminarSustituyente(sustituyente);
        enlaces_libres += sustituyente.getEnlaces();
    }

    public void enlazarCarbono()
    {
        enlaces_libres--;
    }

    public void eliminarEnlace()
    {
        enlaces_libres++;
    }

    // Consultas:

    public boolean estaEnlazadoA(Id funcion) {
        for(Sustituyente sustituyente : sustituyentes)
            if(sustituyente.getFuncion().equals(funcion))
                return true;

        return false;
    }

    public boolean estaEnlazadoA(Sustituyente sustituyente)
    {
        return sustituyentes.contains(sustituyente);
    }

    public boolean esHalogeno(Id funcion)
    {
        return halogenos.contains(funcion);
    }

    public int getEnlacesLibres()
    {
        return enlaces_libres;
    }

    public List<Sustituyente> getSustituyentes()
    {
        return sustituyentes;
    }

    public List<Sustituyente> getUnicosSustituyentes()
    {
        return new ArrayList<>(new HashSet<>(sustituyentes));
    }

    // String:

    private String cuantificadorMolecular(int cantidad) {
        return (cantidad != 1)
                ? String.valueOf(cantidad) // Como en "CO2"
                : ""; // Como en "CO"
    }

    @Override
    public String toString()
    {
        StringBuilder resultado = new StringBuilder("C");

        List<Sustituyente> copia = sustituyentes; // Se modificará

        if (estaEnlazadoA(Id.hidrogeno)) { // Hay hidrógenos
            resultado.append("H");

            // Se cuenta cuántos hay:
            int hidrogenos = 0;

            for(Sustituyente sustituyente : copia)
                if(sustituyente.getFuncion() == Id.hidrogeno)
                    hidrogenos++;

            // Se eliminan:
            copia.removeIf(sustituyente -> sustituyente.getFuncion().equals(Id.hidrogeno));

            // Se escriben los hidrógenos (tras el carbono y no entre paréntesis):
            if (hidrogenos > 1)
                resultado.append(hidrogenos);
        }

        if (copia.size() > 0) { // Hay más sustituyentes que solo hidrógenos
            // Se ordenan en base a la prioridad de su función:
            for(int i = 0; i < copia.size() - 1;) { // Sin incremento
                if(copia.get(i).getFuncion().compareTo(copia.get(i + 1).getFuncion()) > 0) { // .get(i) > .get(i + 1)
                    swap(copia, i, i + 1);
                    i = 0;
                }
                else i++; // .get(i) <= .get(i + 1)
            }

            // Cuenta las cantidades de cada sustituyente, a la vez que elimina los duplicados:
            List<Integer> cantidades = new ArrayList<>();
            for(int i = 0, cantidad; i < copia.size(); i++) {
                cantidad = 1;

                if(i != copia.size() - 1)
                    for (int k = i + 1; k < copia.size(); k++)
                        if (copia.get(i).equals(copia.get(k)))
                        {
                            cantidad++;
                            copia.remove(k);
                            k = i; // El bucle for lo incrementará después
                        }

                cantidades.add(cantidad);
            }

            // Se escriben el resto de sustituyentes (tras los hidrógenos y entre paréntesis):
            if (copia.size() > 1) { // Hay más de un tipo de sustituyente
                for (int i = 0; i < copia.size(); i++) {
                    if (copia.get(i).getEnlaces() == 1) {
                        resultado.append("(");

                        if (copia.get(i).getFuncion() == Id.radical) {
                            int cantidad = cantidades.get(i);

                            if (copia.get(i).getFuncion() != Id.radical) {
                                if (cantidad != 1 || estaEnlazadoA(Id.hidrogeno)) {
                                    resultado.append(formulas.get(copia.get(i).getFuncion()))
                                            .append(")").append(cuantificadorMolecular(cantidad));
                                }
                                else resultado.append(formulas.get(copia.get(i).getFuncion()));
                            }
                            else {
                                if (!copia.get(i).getIso())
                                    resultado.append("CH2".repeat(Math.max(0, copia.get(i).getCarbonos() - 1)))
                                            .append("CH3");
                                else resultado.append("CH2".repeat(Math.max(0, copia.get(i).getCarbonos() - 2)))
                                        .append("(CH3)2");
                            }
                        }
                        else resultado.append(formulas.get(copia.get(i).getFuncion()));

                        resultado.append(")").append(cuantificadorMolecular(cantidades.get(i)));
                    }
					else resultado.append(formulas.get(copia.get(i).getFuncion()));
                }
            }
            else { // Hay un único tipo de sustituyente
                int cantidad = cantidades.get(0);

                if (copia.get(0).getFuncion() != Id.radical) {
                    if (cantidad != 1 || estaEnlazadoA(Id.hidrogeno)) {
                        String text = formulas.get(copia.get(0).getFuncion());

                        if (text.length() != 1 && esHalogeno(copia.get(0).getFuncion()))
                            resultado.append("(").append(text).append(")").append(cuantificadorMolecular(cantidad));
                        else resultado.append(text).append(cuantificadorMolecular(cantidad));
                    }
                    else resultado.append(formulas.get(copia.get(0).getFuncion()));
                }
                else {
                    if (!copia.get(0).getIso())
                        resultado.append("(").append("CH2".repeat(Math.max(0, copia.get(0).getCarbonos() - 1)))
                                .append("CH3)").append(cuantificadorMolecular(cantidades.get(0)));
                    else resultado.append("(").append("CH2".repeat(Math.max(0, copia.get(0).getCarbonos() - 2)))
                            .append("(CH3)2)").append(cuantificadorMolecular(cantidades.get(0)));
                }
            }
        }

        return resultado.toString();
    }

}
