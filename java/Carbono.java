import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Carbono {

    private Set<Sustituyente> sustituyentes = new HashSet<>();
    int enlaces_libres;

    public Carbono(int previous_bonds) {
        enlaces_libres = 4 - previous_bonds;
    }

    // INTERFACE:

    public void anadirSustituyente(Sustituyente sus){
        sustituyentes.add(sus);
        enlaces_libres -= sus.getBonds();
    }

    /*
     * En el antiguo codigo en C++ este metodo estaba hecho en dos partes, uno que eliminaba el sustituyente del vector,
     * y otro que, utilizando este metodo, incrementaba los enlaces. Ahora queda mas limpio como uno solo.
     */

    public void eliminarSustituyente(Sustituyente sus) {
        sustituyentes.remove(sus);
        enlaces_libres += sus.getBonds();
    }

    public void enlaceCarbono()
    {
        enlaces_libres--;
    }

    public void eliminarEnlace()
    {
        enlaces_libres++;
    }

    // DATA INQUIRES:
    public boolean estaEnlazadoA(Id function)
    {
        boolean ok = false;
        for(Sustituyente sustituyente : sustituyentes)
        {
            if(sustituyente.getFunction().equals(function))
              ok = true;
        }

        return ok;
    }
    public boolean estaEnlazadoA(Sustituyente sus)
    {
        return sustituyentes.contains(sus);
    }

    public boolean esHalogeno(Id function)
    {
        return function.equals(Id.bromine) || function.equals(Id.chlorine) || function.equals(Id.fluorine) || function.equals(Id.iodine);
    }

    public int getEnlacesLibres()
    {
        return enlaces_libres;
    }

    public Set<Sustituyente> getSustituyentes()
    {
        return sustituyentes;
    }

    public Set<Sustituyente> getUnicosSustituyentes()
    {
        Set<Sustituyente> resultado = new HashSet();
        Iterator it = sustituyentes.iterator();

        while(it.hasNext())
        {
            int contador = 0;

            for(Sustituyente sustituyente : sustituyentes)
            {
                if(it.equals(sustituyente))
                    contador++;
            }

            if(contador == 1)
                resultado.add((Sustituyente) it);

            it.next();
        }

        return resultado;
    }

    // RESULT:

    @Override
    public String toString()
    {
        	final Map<Id, String> texts = {
        {Id.acid,"OOH"},{Id.carboxyl,"COOH"},{Id.amide,"ONH2"},
        {Id.carbamoyl,"CONH2"},{Id.nitrile,"N"},{Id.cyanide,"CN"},
        {Id.aldehyde,"HO"},{Id.ketone,"O"},{Id.alcohol,"OH"},
        {Id.amine,"NH2"},{Id.nitro,"NO2"},{Id.bromine,"Br"},
        {Id.chlorine,"Cl"},{Id.fluorine,"F"},{Id.iodine,"I"}
    };

        Set<Sustituyente> subs_temp = sustituyentes;

        String result = "C";
        if (estaEnlazadoA(Id.hydrogen))
        {
            result += "H";
            int count = 0;
            for (Sustituyente sustituyente : subs_temp)
            {
                if (sustituyente.getFunction() == Id.hydrogen)
                {
                    subs_temp.erase(subs_temp.begin() + i);
                    count++;
                    i = 0;
                }
                else i++;
            }
            if (count > 1) result += to_string(count);
        }

        if (subs_temp.size())
        {
            unsigned short i = 0;
            while (i < subs_temp.size() - 1)
            {
                if (subs_temp[i].getFunction() >
                        subs_temp[i + 1].getFunction())
                {
                    swap(subs_temp[i], subs_temp[i + 1]);
                    i = 0;
                }
                else i++;
            }
            vector<unsigned short> quantities;
            for (unsigned short i = 0; i < subs_temp.size(); i++)
            {
                unsigned short count = 1;
                if (i != subs_temp.size() - 1)
                {
                    for (unsigned short j = i + 1; j < subs_temp.size(); j++)
                    {
                        if (subs_temp[i].equals(subs_temp[j]))
                        {
                            subs_temp.erase(subs_temp.begin() + j);
                            j = i; // For loop will add one
                            count++;
                        }
                    }
                }
                quantities.push_back(count);
            }

            if (subs_temp.size() > 1)
            {
                for (unsigned short i = 0; i < subs_temp.size(); i++)
                {
                    if (subs_temp[i].getBonds() > 1)
                        result += texts.find(subs_temp[i]
                                .getFunction())->second;
					else
                    {
                        result += "(";
                        if (subs_temp[i].getFunction() != Id::radical)
                            result += texts.find(subs_temp[i]
                                    .getFunction())->second;
						else
                        {
                            unsigned short quantity = quantities[i];
                            if (subs_temp[i].getFunction() != Id::radical)
                            {
                                if (quantity == 1 && !thereIs(Id::hydrogen))
                                {
                                    result += texts.find(subs_temp[i]
                                            .getFunction())->second;
                                }
                                else
                                {
                                    result += texts.find(subs_temp[0]
                                            .getFunction())->second + ")" +
                                        toDigit(quantity);
                                }
                            }
                            else
                            {
                                if (!subs_temp[i].getIso())
                                {
                                    for (unsigned short j = 0;
                                    j < subs_temp[i].getCarbons() - 1; j++)
                                    result += "CH2";
                                    result += "CH3";
                                }
                                else
                                {
                                    for (unsigned short j = 0;
                                    j < subs_temp[i].getCarbons() - 2; j++)
                                    result += "CH2";
                                    result += "(CH3)2";
                                }
                            }
                        }
                        result += ")" + toDigit(quantities[i]);
                    }
                }
            }
            else
            {
                unsigned short quantity = quantities[0];
                if (subs_temp[0].getFunction() != Id::radical)
                {
                    if (quantity == 1 && !thereIs(Id::hydrogen))
                    {
                        result += texts.find(subs_temp[0]
                                .getFunction())->second;
                    }
                    else
                    {
                        string text = texts.find(subs_temp[0]
                                .getFunction())->second;
                        if (text.size() != 1 &&
                                !isHalogen(subs_temp[0].getFunction()))
                            result += "(" + text + ")" + toDigit(quantity);
                        else
                            result += text + toDigit(quantity);
                    }
                }
                else
                {
                    if (!subs_temp[0].getIso())
                    {
                        result += "(";
                        for (unsigned short j = 0;
                        j < subs_temp[0].getCarbons() - 1; j++)
                        result += "CH2";
                        result += "CH3)" + toDigit(quantities[0]);
                    }
                    else
                    {
                        result += "(";
                        for (unsigned short j = 0;
                        j < subs_temp[0].getCarbons() - 2; j++)
                        result += "CH2";
                        result += "(CH3)2";
                        result += ")" + toDigit(quantities[0]);
                    }
                }
            }
        }
        return result;
    }
    }
}
