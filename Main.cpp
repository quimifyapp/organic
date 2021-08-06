#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>

//Random:
#include <ctime>
#include <cstdlib> 

using namespace std;

enum class Id { //Substituent's kinds ordered by nomenclature priority
    acid,
    amide,
    carbamoyl,
    nitrile,
    cyanide,
    aldehyde,
    ketone,
    alcohol,
    amine,
    alkene,
    alkyne,
    nitro,
    halogen,
    bromine,
    chlorine,
    fluorine,
    iodine,
    radical,
    hydrogen
};

class Substituent {
private:
    Id function; 
    // The substituent's kind
    unsigned short bonds; 
    // Amount of e- it shares with the carbon
    unsigned short carbons = 0; 
    // Only for radicals
    bool iso = false; 
    // Only for radicals

    /* EXAMPLES:
    *=O                ---> ketone {function = Id:ketone, bonds = 2}

    *-CH2-CH2-CH3      ---> propyl {function = Id::radical, bonds = 1, carbons = 5, iso = false}        

                  CH3
                 / 
    *-CH2-CH2-CH2      ---> isopentyl {function = Id::radical, bonds = 1, carbons = 5, iso = true}
                 \
                  CH3
    */

public:
    Substituent(Id new_function, unsigned short new_bonds) {
        function = new_function;
        bonds = new_bonds;
    }

    Substituent(Id new_function, unsigned short new_bonds, unsigned short new_carbons, bool new_iso) {
        function = new_function;
        bonds = new_bonds;
        carbons = new_carbons;
        iso = new_iso;
    }

    bool equals(Substituent s) {
        return function == s.function && bonds == s.bonds && carbons == s.carbons;
    }

    Id getFunction() const { return function; }
    unsigned short getBonds() const { return bonds; }
    unsigned short getCarbons() const { return carbons; }
    bool getIso() const { return iso; }
};

namespace substituents {
    Substituent acid(Id::acid, 3);
    Substituent amide(Id::amide, 3);
    Substituent carbamoyl(Id::carbamoyl, 1);
    Substituent nitrile(Id::nitrile, 3);
    Substituent cyanide(Id::cyanide, 1);
    Substituent aldehyde(Id::aldehyde, 3);
    Substituent ketone(Id::ketone, 2);
    Substituent alcohol(Id::alcohol, 1);
    Substituent amine(Id::amine, 1);
    Substituent nitro(Id::nitro, 1);
    Substituent bromine(Id::bromine, 1);
    Substituent chlorine(Id::chlorine, 1);
    Substituent fluorine(Id::fluorine, 1);
    Substituent iodine(Id::iodine, 1);
    Substituent hydrogen(Id::hydrogen, 1);

    Substituent methyl(Id::radical, 1, 1, false);
    //Handy methyl constant

    const map<Id, Substituent> list = {
        {Id::acid, acid},
        {Id::amide, amide},
        {Id::carbamoyl, carbamoyl},
        {Id::nitrile, nitrile},
        {Id::cyanide, cyanide},
        {Id::aldehyde, aldehyde},
        {Id::ketone, ketone},
        {Id::alcohol, alcohol},
        {Id::amine, amine},
        {Id::nitro, nitro},
        {Id::bromine, bromine},
        {Id::chlorine, chlorine},
        {Id::fluorine, fluorine},
        {Id::iodine, iodine},
        {Id::hydrogen, hydrogen}
    };
}

class Carbon {
private:
    vector<Substituent> subs;
    unsigned short used_bonds;

    string replaceAll(std::string str, const std::string& from, const std::string& to) {
        size_t start_pos = 0;
        while ((start_pos = str.find(from, start_pos)) != std::string::npos) {
            str.replace(start_pos, from.length(), to);
            start_pos += to.length(); 
        }
        return str;
    }

    string toDigit(unsigned short n) {
        if (n != 1) return to_string(n);
        else return "";
    }

public:
    Carbon(unsigned short previous_bonds) {
        used_bonds = previous_bonds;
    }

    unsigned short freeBonds() {
        return 4 - used_bonds;
    }

    void addSubstituent(Substituent sub) {
        subs.push_back(sub);
        used_bonds += sub.getBonds();
    }

    void removeSubstituent(Substituent sub) {
        for (unsigned short i = 0; i < subs.size(); i++)
            if (subs[i].equals(sub)) {
                subs.erase(subs.begin() + i);
                //used_bonds -= sub.getBonds();
                break;
            }
    }

    bool thereIs(Id function) {
        for (unsigned short i = 0; i < subs.size(); i++)
            if (subs[i].getFunction() == function)
                return true;
        return false;
    }

    unsigned short amountOf(Id function) {
        unsigned short count = 0;
        for (Substituent sub : subs)
            if (sub.getFunction() == function)
                count++;
        return count;
    }

    void bondCarbon() {
        used_bonds += 1;
    }

    void deleteBond() {
        used_bonds -= 1;
    }

    vector<Substituent> getSubstituents() {
        return subs;
    }

    string toString() {
        const static map<Id, string> texts = {{Id::acid,"OOH"},{Id::amide,"ONH2"},{Id::carbamoyl,"CONH2"},
            {Id::nitrile,"N"},{Id::cyanide,"CN"},{Id::aldehyde,"HO"},{Id::ketone,"O"},{Id::alcohol,"OH"},{Id::amine,"NH2"},
            {Id::nitro,"NO2"},{Id::bromine,"Br"},{Id::chlorine,"Cl"},{Id::fluorine,"F"},{Id::iodine,"I"}};

        vector<Substituent> subs_temp = subs;

        string result = "C";
        if (thereIs(Id::hydrogen)) {
            result += "H";
            unsigned short count = 0;
            unsigned short i = 0;
            while (i < subs_temp.size()) {
                if (subs_temp[i].getFunction() == Id::hydrogen) {
                    subs_temp.erase(subs_temp.begin() + i);
                    count++;
                    i = 0;
                }
                else i++;
            }
            if (count > 1) result += to_string(count);
        }
        
        if (subs_temp.size()) {
            unsigned short i = 0;
            while (i < subs_temp.size() - 1) {
                if (subs_temp[i].getFunction() > subs_temp[i + 1].getFunction()) {
                    swap(subs_temp[i], subs_temp[i + 1]);
                    i = 0;
                }
                else i++;
            } 
            vector<unsigned short> quantities;
            for (unsigned short i = 0; i < subs_temp.size(); i++) {
                unsigned short count = 1;
                if (i != subs_temp.size() - 1) {
                    for (unsigned short j = i + 1; j < subs_temp.size(); j++) {
                        if (subs_temp[i].equals(subs_temp[j])) {
                            subs_temp.erase(subs_temp.begin() + j);
                            j = i; //For loop will add one
                            count++;
                        }
                    }
                }
                quantities.push_back(count);
            }

            if (subs_temp.size() > 1) {
                for (unsigned short i = 0; i < subs_temp.size(); i++) {
                    if (subs_temp[i].getBonds() > 1)
                        result += texts.find(subs_temp[i].getFunction())->second;
                    else {
                        result += "(";
                        if(subs_temp[i].getFunction() != Id::radical)
                            result += texts.find(subs_temp[i].getFunction())->second;
                        else {
                            unsigned short quantity = quantities[i];
                            if (subs_temp[i].getFunction() != Id::radical) {
                                if (quantity == 1 && !thereIs(Id::hydrogen)) {
                                    result += texts.find(subs_temp[i].getFunction())->second;
                                }
                                else {
                                    result += "(" + texts.find(subs_temp[0].getFunction())->second + ")" + toDigit(quantity);
                                }
                            }
                            else {
                                if (!subs_temp[i].getIso()) {
                                    for (unsigned short j = 0; j < subs_temp[i].getCarbons() - 1; j++)
                                        result += "CH2";
                                    result += "CH3";
                                }
                                else {
                                    for (unsigned short j = 0; j < subs_temp[i].getCarbons() - 2; j++)
                                        result += "CH2";
                                    result += "(CH3)2";
                                }
                            }
                        }
                        result += ")" + toDigit(quantities[i]);
                    }
                }
            }
            else {
                unsigned short quantity = quantities[0];
                if (subs_temp[0].getFunction() != Id::radical) {
                    if (quantity == 1 && !thereIs(Id::hydrogen)) {
                        result += texts.find(subs_temp[0].getFunction())->second;
                    }
                    else {
                        result += "(" + texts.find(subs_temp[0].getFunction())->second + ")" + toDigit(quantity);
                    }
                }
                else {
                    if (!subs_temp[0].getIso()) {
                        result += "(";
                        for (unsigned short j = 0; j < subs_temp[0].getCarbons() - 1; j++) 
                            result += "CH2";
                        result += "CH3)" + toDigit(quantities[0]);
                    }
                    else {
                        result += "(";
                        for (unsigned short j = 0; j < subs_temp[0].getCarbons() - 2; j++) 
                            result += "CH2";
                        result += "(CH3)2";
                        result += ")" + toDigit(quantities[0]);
                    }
                }
            }
        }
        //result = replaceAll(result, "HH", "H2"); 
        return result;
    }
};

class Piece {
    /* EXAMPLES:
    2,3-diol = {"2,3", "di", "ol"}
    tetrain = {"", "tetra", "in"}
    fluoro = {"", "", "fluoro"} */
public:
    string positions = "";
    string multiplier = "";
    string text = "";
    
    string toString(){
        if (positions != "")
            return positions + "-" + multiplier + text;
        else return multiplier + text;
    }
};

class Chain {
private:
    vector<Carbon> chain;
    vector<Id> functions;

    string greekPrefix(unsigned short n) {
        switch (n) {
        case 5:
            return "pent";
        case 6:
            return "hex";
        case 7:
            return "hept";
        case 8:
            return "oct";
        case 9:
            return "non";
        case 0:
            return "";
        case 1:
            return "hen";
        case 2:
            return "do";
        case 3:
            return "tri";
        case 4:
            return "tetra";
        }
    }

    string multiplier(unsigned short n) {
        if (n < 10) { // [1, 9]
            switch (n) {
            case 0:
                return "";
            case 1:
                return "met";
            case 2:
                return "et";
            case 3:
                return "prop";
            case 4:
                return "but";
            }
            return greekPrefix(n); // [5, 9]
        }
        unsigned short ten = n / 10;
        unsigned short unit = n - (ten * 10);
        if (n < 20) { // [10, 19]
            if (n == 11) 
                return "undec";
            if(n < 15) 
                return greekPrefix(unit) + "dec";
            return greekPrefix(unit) + "adec";
        } 
        if (n < 30) { // [20, 29]
            switch (n) {
            case 20:
                return "icos";
            case 21:
                return "heneicos";
            }
            if (n < 25) return greekPrefix(unit) + "cos";
            return greekPrefix(unit) + "acos";
        }
        string s;
        if (n < 100) { // [30, 99]
            s = greekPrefix(unit);
            if (unit > 4)
                s += "a";
            s += greekPrefix(ten);
            if (ten == 4)
                s += "cont";
            else s += "acont";
            return s;
        } 
        // [100, 999]
        unsigned short hundred = n / 100; 
        ten = ten - (hundred * 10);
        s = multiplier(ten * 10 + unit);
        if (n == 100) 
            return "hect";
        switch (hundred)
        {
        case 1:
            return s + "ahect";
        case 2:
            return s + "adict";
        case 3:
            return s + "atrict";
        case 4:
            return s + "atetract";
        default:
            return s + "a" + greekPrefix(hundred) + "act";
        }
    }

    bool thereIs(Id function) {
        if (function == Id::alkene) {
            for (unsigned short i = 0; i < chain.size(); i++)
                if (chain[i].freeBonds() == 1)
                    return true;
            return false;
        }
        if (function == Id::alkyne) {
            for (unsigned short i = 0; i < chain.size(); i++)
                if (chain[i].freeBonds() == 2)
                    return true;
            return false;
        }
        for (unsigned short i = 0; i < chain.size(); i++)
            if (chain[i].thereIs(function))
                return true;
        return false;
    }

    vector<unsigned short> listBonds(unsigned short bonds) {
        vector<unsigned short> positions;
        bonds -= 1;
        for (unsigned short i = 0; i < chain.size(); i++)
            if (chain[i].freeBonds() == bonds)
                positions.push_back(i);
        return positions;
    }

    vector<unsigned short> listPositionsOf(Id function) {
        switch (function) {
        case Id::alkene:
            return listBonds(2);
        case Id::alkyne:
            return listBonds(3);
        default:
            vector<unsigned short> positions;
            for (unsigned short i = 0; i < chain.size(); i++)
                    for (unsigned short j = 0; j < chain[i].amountOf(function); j++) 
                        positions.push_back(i);
            return positions;
        }
    }

    vector<unsigned short> listPositionsOf(Substituent sub) {
        vector<unsigned short> positions;
        for (unsigned short i = 0; i < chain.size(); i++)
            for (Substituent s : chain[i].getSubstituents())
                if (s.equals(sub))
                    positions.push_back(i);
        return positions;
    }

    void listFunctions() {
        functions.clear();
        for (Carbon c : chain) {
            if (c.freeBonds() == 1) {
                if (find(functions.begin(), functions.end(), Id::alkene) == functions.end())
                    functions.push_back(Id::alkene);
            }
            else if (c.freeBonds() == 2) {
                if (find(functions.begin(), functions.end(), Id::alkyne) == functions.end())
                    functions.push_back(Id::alkyne);
            }    
            for (Substituent s : c.getSubstituents()) 
                if (find(functions.begin(), functions.end(), s.getFunction()) == functions.end() &&
                    s.getFunction() != Id::hydrogen)
                    functions.push_back(s.getFunction());
        }
        sort(functions.begin(), functions.end());
    }

    vector<Substituent> listSubstituents(Id function) {
        vector<Substituent> result;
        for (Carbon c : chain)
            for (Substituent s : c.getSubstituents())
                if (s.getFunction() == function) {
                    bool add = true;
                    for (Substituent s2 : result) {
                        if (s.equals(s2)) {
                            add = false;
                            break;
                        }
                    }
                    if(add) result.push_back(s);
                }
                    
        return result;
    }

    string quantifier(unsigned short n) {
        switch (n) {
        case 1: return "";
        case 2: return "di";
        case 3: return "tri";
        case 4: return "tetra";
        default:
            return multiplier(n) + "a";
        }
    }

    char firstLetterOf(string s) {
        for (char c : s)
            if (c > 96 && c < 123) 
                return c;
    }

    unsigned short sum(vector<unsigned short> vector) {
        unsigned short sum = 0;
        for (unsigned short n : vector)
            sum += n;
        return sum;
    }

    Chain(vector<Carbon> v) {
        for (Carbon c : v) {
            nextCarbon();
            for (Substituent s : c.getSubstituents())
                addSubstituent(s);
        }
    }

    bool isLetter(char ch) {
        return ch > 96 && ch < 123;
    }

    bool isVowel(char ch){ 
        return (ch == 'e') || (ch == 'i') || (ch == 'a') || (ch == 'o') || (ch == 'u'); 
    }

    bool isDigit(char ch) {
        return ch > 48 && ch < 58;
    }

    void reorder() {
        Chain reversed;
        vector<Carbon> v;
        for (Id function : functions) {
            v = chain;
            reverse(v.begin(), v.end());
            reversed = Chain(v);
            unsigned short normal_sum = sum(listPositionsOf(function));
            unsigned short reversed_sum = sum(reversed.listPositionsOf(function));
            if (normal_sum != reversed_sum) {
                if (normal_sum > reversed_sum)
                    chain = reversed.chain;
                break;
            }
        }
    }

    vector<Piece> sortPrefixes(vector<Piece> prefixes) {
        //Ordena alfabéticamente los prefijos sin tener en cuenta los multiplicadores
        unsigned short i = 0;
        while (i < prefixes.size() - 1) {
            unsigned short min_length;
            if (prefixes[i].text.length() < prefixes[i + 1].text.length())
                min_length = prefixes[i].text.length();
            else
                min_length = prefixes[i + 1].text.length();

            for (unsigned short j = 0; j < min_length; j++) {
                if (prefixes[i].text.at(j) > prefixes[i + 1].text.at(j)) {
                    swap(prefixes[i], prefixes[i + 1]);
                    i = 0;
                    break;
                }
                else if (prefixes[i].text.at(j) < prefixes[i + 1].text.at(j)) {
                    i++;
                    break;
                }
            }
        }
        return prefixes;
    }

    string concadenate(vector<string> vector) {
        string result = "";
        for (string s : vector)
            result += "-" + s;
        return result;
    }

    bool isHalogen(Id function) {
        if (function == Id::bromine || function == Id::chlorine || function == Id::fluorine || function == Id::iodine)
            return true;
        return false;
    }

    bool everySubstituentIs(Id function) {
        for (Carbon c : chain)
            for (Substituent s : c.getSubstituents())
                if (s.getFunction() != function)
                    return false;
        return true;
    }

    bool isRedundant(Id function, vector<unsigned short> positions) {
        /*
        -dobles o triples enlaces si solo son uno (eteno, propeno si no hay nada más,...?)
                -propeno
                    -grupos k no son finales
                    -grupos si son finales

                    //propano si hay doble sust de 3 enlaces
        */
        if (chain.size() == 1)
            return true;
        else if (chain.size() == 2) {
            if (functions.size() == 1)
                //Solo es uno
                return true;
            if (function == Id::alkene || function == Id::alkyne)
                //Es alqueno o alquino
                return true;
            if (functions.size() == 2 && (thereIs(Id::alkene) || thereIs(Id::alkyne)))
                //Solo es uno y hay un alqueno o alquino
                return true;
            vector<Id> functions_temp; //Functions sin los alquenos y alquinos
            for (Id f : functions)
                if (f != Id::alkene && f != Id::alkyne)
                    functions_temp.push_back(f);
            if (functions_temp.size() == 2) {
                //Hay dos sustituyentes
                unsigned short sum = substituents::list.find(functions_temp[0])->second.getBonds() +
                    substituents::list.find(functions_temp[1])->second.getBonds();
                if (sum > 3)
                    //No caben en un solo carbono
                    return true;
                if (sum > 1 && thereIs(Id::alkene))
                    //No caben en un solo carbono con un alqueno
                    return true;
                if (thereIs(Id::alkyne))
                    //No caben en un solo carbono con un alquino
                    return true;
                if (functions[0] == function)
                    //De los dos, es el de mayor preferencia (1)
                    return true;
            }
        }
        else if (chain.size() == 3) {
            if (function == Id::ketone && functions[0] >= Id::aldehyde)
                //Propanona
                return true;
            if (functions.size() == 1 && (function == Id::alkene || function == Id::alkyne))
                //Es propeno, propadieno o propino 
                return true;
        }
        return false;
    }

    Piece pieceFor(Id function, vector<unsigned short> positions, string text) {
        Piece piece;
        piece.multiplier = quantifier(positions.size());
        piece.text = text;
        if (isRedundant(function, positions))
            return piece;
        for (unsigned short i = 0; i < positions.size() - 1; i++) {
            piece.positions += to_string(positions[i] + 1);
            piece.positions += ",";
        }
        piece.positions += to_string(positions[positions.size() - 1] + 1);
        return piece;
    }

    Piece prefixForRadical(Substituent radical) {
        string s;
        if (radical.getIso())
            s += "iso";
        s += multiplier(radical.getCarbons()) + "il";
        return pieceFor(Id::radical, listPositionsOf(radical), s);
    }

    Piece prefixFor(Id function) {
        const static map<Id, string> texts = {{Id::carbamoyl, "carbamoil"},{Id::cyanide, "ciano"},
            {Id::ketone, "oxo"}, {Id::alcohol, "hidroxi"},{Id::amine, "amino"},{Id::nitro, "nitro"},
            {Id::bromine, "bromo"},{Id::chlorine, "cloro"},{Id::fluorine, "fluoro"},{Id::iodine, "yodo"}};

        vector<unsigned short> positions = listPositionsOf(function);
        //if (isHalogen(function) && everySubstituentIs(function) && chain.size() > 1) // per???????????????????????
          //  return "per" + texts.find(function)->second;
        return pieceFor(function, positions, texts.find(function)->second);
    }

    string sufixFor(Id function) {
        const static map<Id, string> texts = {{Id::acid, "oico"},{Id::amide, "amida"},{Id::nitrile, "nitrilo"},
            {Id::aldehyde, "al"},{Id::ketone, "ona"},{Id::alcohol, "ol"},{Id::amine, "amina"}};

        vector<unsigned short> positions = listPositionsOf(function);
        if ((substituents::list.find(function)->second).getBonds() == 3) 
            return quantifier(positions.size()) + texts.find(function)->second;
        return pieceFor(function, positions, texts.find(function)->second).toString();
    }

    void correct() {
        /*cadena principal vs. radicales*/
        if (functions.size()) {
            //Radical en el primer carbono -> extensión por el principio
            if (chain[0].thereIs(Id::radical)) { 
                Substituent largest_radical(Id::radical, 1, 0, false);
                for (Substituent s : chain[0].getSubstituents()) {
                    if (s.getFunction() == Id::radical) {
                        unsigned short a = largest_radical.getCarbons();
                        unsigned short b = s.getCarbons();
                        if (largest_radical.getIso()) a =- 1;
                        if (s.getIso()) b = -1;
                        if (b > a || (a == b && s.getIso())) 
                            largest_radical = s;
                    }
                }
                chain[0].removeSubstituent(largest_radical);
                vector<Carbon> subchain;
                Carbon CH2(2);
                CH2.addSubstituent(substituents::hydrogen);
                CH2.addSubstituent(substituents::hydrogen);
                for (unsigned short i = 0; i < largest_radical.getCarbons() - 1; i++) 
                    subchain.push_back(CH2);
                if (largest_radical.getIso()) {
                    subchain[subchain.size() - 2].addSubstituent(substituents::methyl);
                    subchain[subchain.size() - 2].deleteBond();
                }
                else {
                    Carbon CH3(1);
                    CH3.addSubstituent(substituents::hydrogen);
                    CH3.addSubstituent(substituents::hydrogen);
                    CH3.addSubstituent(substituents::hydrogen);
                    subchain.push_back(CH3);
                }
                reverse(subchain.begin(), subchain.end());
                chain.insert(chain.begin(), subchain.begin(), subchain.end());
            }
            //Radical en el primer último -> extensión por el final
            if (chain[chain.size() - 1].thereIs(Id::radical)) {
                Substituent largest_radical(Id::radical, 1, 0, false);
                for (Substituent s : chain[chain.size() - 1].getSubstituents()) {
                    if (s.getFunction() == Id::radical) {
                        unsigned short a = largest_radical.getCarbons();
                        unsigned short b = s.getCarbons();
                        if (largest_radical.getIso()) a = -1;
                        if (s.getIso()) b = -1;
                        if (b > a || (a == b && s.getIso()))
                            largest_radical = s;
                    }
                }
                chain[chain.size() - 1].removeSubstituent(largest_radical);
                vector<Carbon> subchain;
                Carbon CH2(2);
                CH2.addSubstituent(substituents::hydrogen);
                CH2.addSubstituent(substituents::hydrogen);
                for (unsigned short i = 0; i < largest_radical.getCarbons() - 1; i++)
                    subchain.push_back(CH2);
                if (largest_radical.getIso()) {
                    subchain[subchain.size() - 2].addSubstituent(substituents::methyl);
                    subchain[subchain.size() - 2].deleteBond();
                    subchain[subchain.size() - 1].addSubstituent(substituents::hydrogen);
                }
                else {
                    Carbon CH3(1);
                    CH3.addSubstituent(substituents::hydrogen);
                    CH3.addSubstituent(substituents::hydrogen);
                    CH3.addSubstituent(substituents::hydrogen);
                    subchain.push_back(CH3);
                }
                chain.insert(chain.begin(), subchain.begin(), subchain.end());
            }
            //Radical más largo que el resto de la cadena (simple o iso-) -> se cambian

            //Amida no principal -> carbamoil del anterior
            if (functions[0] != Id::amide) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (chain[0].thereIs(Id::amide)) {
                    chain[1].deleteBond();
                    chain[1].addSubstituent(substituents::carbamoyl);
                    chain.erase(chain.begin());
                    listFunctions();
                }
                else if (chain[chain.size() - 1].thereIs(Id::amide)) {
                    chain[chain.size() - 1 - 1].deleteBond();
                    chain[chain.size() - 1 - 1].addSubstituent(substituents::carbamoyl);
                    chain.pop_back();
                    listFunctions();
                }
            }
            //Nitrilo no principal -> ciano del anterior
            if (functions[0] != Id::nitrile) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (chain[0].thereIs(Id::nitrile)) {
                    chain[1].deleteBond();
                    chain[1].addSubstituent(substituents::cyanide);
                    chain.erase(chain.begin());
                    listFunctions();
                }
                else if (chain[chain.size() - 1].thereIs(Id::nitrile)) {
                    chain[chain.size() - 1 - 1].deleteBond();
                    chain[chain.size() - 1 - 1].addSubstituent(substituents::cyanide);
                    chain.pop_back();
                    listFunctions();
                }
            }
            //Cetona e hidrógeno terminales -> aldehído
            if (functions[0] >= Id::aldehyde) {
                //Es (o será) principal, no hay otro con mayor preferencia
                if (chain[0].thereIs(Id::ketone) && chain[0].thereIs(Id::hydrogen)) {
                    chain[0].removeSubstituent(substituents::ketone);
                    chain[0].removeSubstituent(substituents::hydrogen);
                    chain[0].addSubstituent(substituents::aldehyde);
                    listFunctions();
                }
                if (chain[chain.size() - 1].thereIs(Id::ketone) && chain[chain.size() - 1].thereIs(Id::hydrogen)) {
                    chain[chain.size() - 1].removeSubstituent(substituents::ketone);
                    chain[chain.size() - 1].removeSubstituent(substituents::hydrogen);
                    chain[chain.size() - 1].addSubstituent(substituents::aldehyde);
                    listFunctions();
                }
            }
            //Aldehído sin ser el grupo principal -> cetona
            if (functions[0] != Id::aldehyde) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (chain[chain.size() - 1].thereIs(Id::aldehyde)) {
                    chain[chain.size() - 1].removeSubstituent(substituents::aldehyde);
                    chain[chain.size() - 1].addSubstituent(substituents::ketone);
                    chain[chain.size() - 1].addSubstituent(substituents::hydrogen);
                    listFunctions();
                }
                else if (chain[0].thereIs(Id::aldehyde)) {
                    chain[0].removeSubstituent(substituents::aldehyde);
                    chain[0].addSubstituent(substituents::ketone);
                    chain[0].addSubstituent(substituents::hydrogen);
                    listFunctions();
                }
            }
        }
    }

public:
    string getName() {
        listFunctions();
        correct();
        reorder();

        /*-REDUNDANCIA
        -RADICALES:
                -VERIFICAR
                    -ARREGLAR CADENA
                -REORDENAR
                    -SOLO cuando dos simple chain mismo numero (mismos carbonos), es x orden alfabetico
                -NOMBRAR
            -ETER, ESTER, AMINAS MULTIPLES
            -AROMÁTICOS
            -¿CICLOS?*/
        unsigned short count = 0;
        string sufix;
        if (functions.size() && 
            functions[0] != Id::nitro && 
            functions[0] != Id::radical && 
            functions[0] != Id::alkene && 
            functions[0] != Id::alkyne && 
            !isHalogen(functions[0]))
                sufix = sufixFor(functions[count++]);
          
        vector<Piece> prefixes;
        Piece piece;
        while (count < functions.size()) {
            if (functions[count] != Id::alkene && 
                functions[count] != Id::alkyne &&
                functions[count] != Id::radical) {
                piece = prefixFor(functions[count]);
                if (piece.text != "") 
                    prefixes.push_back(piece);
            }
            count++;
        }

        //Aquí las cadenas simples
        vector<Substituent> radicals = listSubstituents(Id::radical);
        for (Substituent radical : radicals) {
            piece = prefixForRadical(radical);
            if (piece.text != "")
                prefixes.push_back(piece);
        }
            
        string pre;
        if (prefixes.size()) {
            prefixes = sortPrefixes(prefixes);
            for (unsigned short i = 0; i < prefixes.size() - 1; i++) {
                pre += prefixes[i].toString();
                if(!isLetter(prefixes[i + 1].toString().at(0)))
                    pre += "-";
            }
            pre += prefixes[prefixes.size() - 1].toString();
        }

        string bonds;
        vector<unsigned short> positions = listPositionsOf(Id::alkene);
        if (positions.size()) {
            piece = pieceFor(Id::alkene, positions, "en");
            if (piece.text != "") {
                if (isDigit(piece.text.at(0)))
                    bonds += "-";
                bonds += piece.toString();
            }
        }
        positions = listPositionsOf(Id::alkyne);
        if (positions.size()) {
            piece = pieceFor(Id::alkyne, listPositionsOf(Id::alkyne), "in");
            if (piece.text != "") {
                if (isDigit(piece.text.at(0)))
                    bonds += "-";
                bonds += piece.toString();
            }
        }
        
        if (bonds == "") 
            bonds = "an";
        if (sufix == "" || !isVowel(firstLetterOf(sufix)))
            bonds += "o";
        if (sufix != "" && isDigit(sufix.at(0))) 
            bonds += "-";

        string mult = multiplier(chain.size());
        if (!isVowel(firstLetterOf(bonds)))
            mult += "a";
        if (!isLetter(bonds.at(0))) 
            mult += "-";
        if (thereIs(Id::acid)) 
            pre = "ácido " + pre;

        return pre + mult + bonds + sufix;
    }

    string getFormula() {
        string s = ""; 
        for (unsigned short i = 0; i < chain.size(); i++) {
            /*if (i) {
                if (chain[i - 1].freeBonds()) {
                    if (chain[i - 1].freeBonds() == 1) s += "=";
                    else s += "≡";
                }
                else s += "-";
            }
            */
            s += chain[i].toString() ;
        }
        return s;
    }

    Chain() {
        nextCarbon();
    }

    void nextCarbon() {
        if (chain.size()) {
            chain.back().bondCarbon();
            chain.push_back(Carbon(chain.back().freeBonds() + 1));
        }
        else chain.push_back(Carbon(0));
    }

    void addSubstituent(Substituent sub) {
        chain.back().addSubstituent(sub);
    }

    vector<Id> availableSubstituents() {
        vector<Id> result;
        unsigned short free = chain.back().freeBonds();
        if (free) { // 1 bond
            result.push_back(Id::hydrogen);
            result.push_back(Id::alcohol);
            result.push_back(Id::amine);
            result.push_back(Id::nitro);
            result.push_back(Id::halogen);
        }
        if (free > 1) // 2 bonds
            result.push_back(Id::ketone);
        if (free > 2) { //3 bonds
            result.push_back(Id::aldehyde);
            result.push_back(Id::nitrile);
            result.push_back(Id::amide);
            result.push_back(Id::acid);
        }
        if (free)
            result.push_back(Id::radical);
        return result;
    }

    unsigned short freeBonds() {
        return chain.back().freeBonds();
    }
};

unsigned short getRandomNumber(unsigned short min, unsigned short max) {
    static constexpr double fraction{1.0 / (RAND_MAX + 1.0)};
    return min + static_cast<unsigned short>((max - min + 1) * (std::rand() * fraction));
}

void aleatorios() {
    std::srand(static_cast<unsigned int>(std::time(nullptr)));
    try {
        while (true) {
            Chain chain2;
            unsigned short amount = getRandomNumber(0, 20);
            for (unsigned short i = 0; i < amount; i++) {
                for (vector<Id> available = chain2.availableSubstituents(); available.size() && chain2.freeBonds() > 1; available = chain2.availableSubstituents()) {
                    //chain2.addSubstituent(substituents::hydrogen);

                    unsigned short number;
                    do {
                        number = getRandomNumber(0, available.size() - 1);
                        //cout << number + 1 << endl;
                        if (available[number] != Id::radical && (substituents::list.find(available[number])->second.getBonds() < chain2.freeBonds()))
                            break;
                    } while (true);
                    chain2.addSubstituent(substituents::list.find(available[number])->second);
                    if (!getRandomNumber(0, 2))
                        break;
                }
                chain2.nextCarbon();
            }
            for (vector<Id> available = chain2.availableSubstituents(); available.size(); available = chain2.availableSubstituents()) {
                unsigned short number;
                do {
                    number = getRandomNumber(0, available.size() - 1);
                    //cout << number + 1 << endl;
                } while (available[number] == Id::radical);
                chain2.addSubstituent(substituents::list.find(available[number])->second);
            }
            //string s =  chain2.getFormula() + ": " + chain2.getName();
            cout << chain2.getFormula() << endl << chain2.getName() << endl << endl;
        }
    }
    catch (...) {
        cout << "Error";
    }
}

int main() {
    const map<Id, string> texts = {{Id::acid, "=O & -OH"},{Id::amide, "=O & -NH2"},{Id::nitrile, "-=N"},
        {Id::aldehyde, "=O & -H"},{Id::ketone, "=O"},{Id::alcohol, "-OH"},{Id::amine, "-NH2"},
        {Id::nitro, "-NO2"},{Id::halogen, "-X"},{Id::radical, "-CH2-CH2..."},{Id::hydrogen, "-H"}};

    //aleatorios();

    Chain chain;
    do {
        Chain chain;

        /*chain.addSubstituent(substituents::iodine);
        chain.addSubstituent(substituents::iodine);
        chain.nextCarbon();
        chain.addSubstituent(substituents::iodine);
        chain.nextCarbon();
        chain.addSubstituent(substituents::iodine);
        chain.nextCarbon();
        chain.addSubstituent(substituents::iodine);
        chain.addSubstituent(substituents::iodine);*/
        //peryodobuta-1,3-dieno
        /*chain.addSubstituent(substituents::hydrogen);
        chain.nextCarbon();
        chain.nextCarbon();
        chain.addSubstituent(substituents::iodine);
        chain.addSubstituent(substituents::iodine);
        chain.nextCarbon();
        chain.addSubstituent(substituents::chlorine);
        chain.nextCarbon();
        chain.addSubstituent(substituents::hydrogen);
        chain.addSubstituent(substituents::hydrogen);*/
        //CH -= C - CI2 - C(Cl) = CH2
        /*for (unsigned short n = 0; n < 999; n++) {
            Chain ch;
            chain = ch;
            chain.addSubstituent(substituents::hydrogen);
            chain.nextCarbon();
            for (unsigned short i = 0; i < n - 4; i++) {
                chain.nextCarbon();
                chain.addSubstituent(substituents::hydrogen);
                chain.addSubstituent(substituents::hydrogen);
            }
            chain.nextCarbon();
            chain.addSubstituent(substituents::hydrogen);
            chain.nextCarbon();
            chain.nextCarbon();
            chain.addSubstituent(substituents::hydrogen);
            chain.addSubstituent(substituents::hydrogen);

            cout << n + 1 << ": " << chain.getName() << endl;
        }*/
        //Cadenas con bucle

        bool first = true;
        for (vector<Id> available = chain.availableSubstituents(); available.size(); available = chain.availableSubstituents()) {
            cout << " ---> " << chain.getFormula() << endl;
            if (!first)
                cout << " ----------------------" << endl << " 0) " << "-C-" << endl;
            else first = false;

            cout << " ----------------------" << endl;
            for (unsigned short i = 0; i < available.size(); i++)
                cout << ' ' << i + 1 << ") " << texts.find(available[i])->second << endl << " ----------------------" << endl;

            cout << " Seleccionar: ";
            unsigned short input;
            cin >> input;
            if (input) {
                input -= 1; 
                for (unsigned short i = 0; i < available.size(); i++)
                    if (input == i) {
                        if (available[i] == Id::radical) {
                            cout << " ----------------------" << endl << " 1) -CH2-...-CH3" << endl << 
                                " ----------------------"<< endl << "                  CH3" << endl << 
                                "                 /" << endl << " 2) -CH2-...-CH2" <<  endl << 
                                "                 \\" << endl << "                  CH3" << 
                                endl << " ----------------------" << endl << " Seleccionar: ";
                            cin >> input;
                            unsigned short carbons;
                            if (input == 1) {
                                do {
                                    cout << endl << " -CH2-...-CH3" << endl <<
                                        "  {---------}" << endl << endl << " Carbonos del radical: ";
                                    cin >> carbons;
                                } while (!carbons);
                                chain.addSubstituent(Substituent(Id::radical, 1, carbons, false));
                            }
                            else if (input == 2) {
                                while(true){
                                    cout << endl << "               CH3" << endl <<
                                        "              /" << endl << " -CH2-...-CH2" << endl <<
                                        "  {---------} \\" << endl << "               CH3"
                                        << endl << endl << " Carbonos de la cadena recta: ";
                                    cin >> carbons;
                                    if (carbons)
                                        chain.addSubstituent(Substituent(Id::radical, 1, carbons + 2, true));
                                    else if (chain.freeBonds() > 1) {
                                        chain.addSubstituent(substituents::methyl);
                                        chain.addSubstituent(substituents::methyl);
                                    }
                                    else continue;
                                    break;
                                }
                            }
                        }
                        else if (available[i] == Id::halogen) {
                            do {
                                cout << " ----------------------" << endl;
                                cout << " 1) " << "-Br" << endl << " ----------------------" << endl;
                                cout << " 2) " << "-Cl" << endl << " ----------------------" << endl;
                                cout << " 3) " << "-F" << endl << " ----------------------" << endl;
                                cout << " 4) " << "-I" << endl << " ----------------------" << endl;
                                cout << " Seleccionar: ";
                                cin >> input;
                            } while (input > 4);
                            switch (input)
                            {
                            case 1:
                                chain.addSubstituent(substituents::bromine);
                                break;
                            case 2:
                                chain.addSubstituent(substituents::chlorine);
                                break;
                            case 3:
                                chain.addSubstituent(substituents::fluorine);
                                break;
                            case 4:
                                chain.addSubstituent(substituents::iodine);
                                break;
                            }
                        }
                        else chain.addSubstituent(substituents::list.find(available[i])->second);
                        break;
                    }
            }
            else chain.nextCarbon();
        }
        
        cout << " " << chain.getName() << endl;
        cout << " ---> " << chain.getFormula() << endl;
        system("pause");
    } while (true);

    return 0;
}