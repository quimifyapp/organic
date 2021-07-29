#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>

using namespace std;

enum class Id { //The sub. kinds ordered by nomenclature priority
    acid,
    amide,
    nitrile,
    aldehyde,
    ketone,
    alcohol,
    amine,
    alkene,
    alkyne,
    nitro,
    bromine,
    chlorine,
    fluorine,
    iodine,
    simple_chain,
    hydrogen
};

class Substituent {
private:
    Id function; // The sub. kind
    unsigned short bonds; // Amount of e- it shares with the carbon
    unsigned short carbons; // Lenght of the chain, only for chain radicals (Function::simple_chain), zero if not

public:
    Substituent(Id new_function, unsigned short new_bonds) {
        function = new_function;
        bonds = new_bonds;
        carbons = 0;
    }

    Substituent(Id new_function, unsigned short new_bonds, unsigned short new_carbons) {
        function = new_function;
        bonds = new_bonds;
        carbons = new_carbons;
    }

    Id getFunction() const { return function; }
    unsigned short getBonds() const { return bonds; }
    unsigned short getCarbons() const { return carbons; }
};

namespace substituents {
    Substituent acid(Id::acid, 3);
    Substituent amide(Id::amide, 3);
    Substituent nitrile(Id::nitrile, 3);
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

    const map<Id, Substituent> list = {
        {Id::acid, acid},
        {Id::amide, amide},
        {Id::nitrile, nitrile},
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
    //Terminal - C or intermediate - C -
    unsigned short used_bonds;
 
public:
    Carbon(unsigned short previous_bonds) {
        used_bonds = previous_bonds;
    }

    unsigned short freeBonds() {
        //Terminal carbons have 3 free bonds, intermediate have only 2
        return 4 - used_bonds;
    }

    void addSubstituent(Substituent sub) {
        subs.push_back(sub);
        used_bonds += sub.getBonds();
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

    vector<Substituent> getSubstituents() {
        return subs;
    }

    string toString() {
        string s = "C";
        if (thereIs(Id::hydrogen)) {
            s += "H";
            unsigned short i = 0;
            for (Substituent sub : subs)
                if (sub.getFunction() == Id::hydrogen)
                    i++;
            if (i > 1) s += to_string(i);
        } 
        if (thereIs(Id::chlorine)) {
            s += "(Cl";
            unsigned short i = 0;
            for (Substituent sub : subs)
                if (sub.getFunction() == Id::chlorine)
                    i++;
            if (i > 1) s += to_string(i);
            s += ")";
        }
        return s;
    }

};

class Chain {
private:
    vector<Carbon> chain;
    vector<Id> functions;

    unsigned short digitsIn(unsigned short n) {
        unsigned short i = 1;
        for (unsigned short i = 1; n > 9; i++)
            n /= 10;
        return i;
    }

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
            if(n < 15) return greekPrefix(unit) + "dec";
            return greekPrefix(unit) + "adec";
        } 
        if (n < 30) { // [20, 29]
            switch (n) {
            case 20:
                return "eicos";
            case 21:
                return "heneicos";
            }
            if (n < 25) return greekPrefix(unit) + "cos";
            return greekPrefix(unit) + "acos";
        }
        // [30, 99]
        string s = greekPrefix(unit);
        if (unit > 4) 
            s += "a";
        s += greekPrefix(ten);
        if (ten == 4) 
            return s + "cont";
        return s + "acont";
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
        bonds--;
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

    void listFunctions() {
        for (Carbon c : chain) {
            if (c.freeBonds() == 1) {
                //if (find(functions.begin(), functions.end(), Id::alkene) == functions.end())
                    //functions.push_back(Id::alkene);
            }
            else if (c.freeBonds() == 2) {
                //if (find(functions.begin(), functions.end(), Id::alkyne) == functions.end())
                    //functions.push_back(Id::alkyne);
            }    
            for (Substituent s : c.getSubstituents()) 
                if (find(functions.begin(), functions.end(), s.getFunction()) == functions.end()
                    && s.getFunction() != Id::hydrogen)
                    functions.push_back(s.getFunction());
        }
        sort(functions.begin(), functions.end());
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
            if (c > 60 && c < 123) 
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

    bool isVowel(char ch){ 
        return (ch == 'e') || (ch == 'i') || (ch == 'a') || (ch == 'o') || (ch == 'u'); 
    }

    void reorderChain() {
        Chain reversed;
        vector<Carbon> v;
        for (Id function : functions) {
            v = chain;
            reverse(v.begin(), v.end());
            reversed = Chain(v);
            vector<unsigned short> normal_pos = listPositionsOf(function);
            vector<unsigned short> reversed_pos = reversed.listPositionsOf(function);
            if (sum(normal_pos) < sum(reversed_pos))
                break;
            else if (sum(normal_pos) > sum(reversed_pos)) {
                chain = reversed.chain;
                break;
            }
                
        }
    }

    vector<string> sortAlphabetically(vector<string> vector) {
        for (unsigned short i = 0; i < vector.size() - 1; i++)
            if (firstLetterOf(vector[i]) > firstLetterOf(vector[i + 1])) {
                swap(vector[i], vector[i + 1]);
                i = 0;
            }
        return vector;
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

    string pieceFor(vector<unsigned short> positions, string text) {
        string s;
        if (positions.size()) {
            for (unsigned short i = 0; i < positions.size() - 1; i++) {
                s += to_string(positions[i] + 1);
                s += ",";
            }
            return s + to_string(positions[positions.size() - 1] + 1) + "-" +
                quantifier(positions.size()) + text;
        }
        return "";
    }

    string prefixFor(Id function) {
        const static map<Id, string> texts = {{Id::alkene, "en"},{Id::alkyne, "in"},{Id::bromine, "bromo"},
            {Id::chlorine, "cloro"},{Id::fluorine, "fluoro"},{Id::iodine, "yodo"}};

        vector<unsigned short> positions = listPositionsOf(function);
        if (isHalogen(function) && everySubstituentIs(function))
            return "per" + texts.find(function)->second;
        return pieceFor(positions, texts.find(function)->second);
    }

    string sufixFor(Id function) {
        const static map<Id, string> texts = {{Id::acid, "oico"},{Id::amide, "amide"},{Id::nitrile, "nitrilo"},
            {Id::aldehyde, "al"},{Id::ketone, "ona"},{Id::alcohol, "ol"},{Id::amine, "amide"} };

        vector<unsigned short> positions = listPositionsOf(function);
        if ((substituents::list.find(function)->second).getBonds() == 3) 
            return quantifier(positions.size()) + texts.find(function)->second;
        return pieceFor(positions, texts.find(function)->second);
    }

public:

    string getName() {
        listFunctions();
        //SOLO cuando dos simple chain mismo numero, es x orden alfabetico
        reorderChain();

        /*
        FALTA:
            -REDUNDANCIA
            -VERIFICAR
            -ARREGLAR CADENA
            -RADICALES:
                -VERIFICAR
                    -ARREGLAR CADENA
                -REORDENAR
                -NOMBRAR
            -ETER, ESTER, AMINAS MULTIPLES
            -AROMÁTICOS
            -¿CICLOS?
        */

        // redundancia u obviedad a la hora de nombrar
        /*
        dobles o triples enlaces si solo son uno
        grupos si es eteno
        grupos si son finales
        grupos si no son finales y es propeno
        */

        unsigned short count = 0;
        string sufix;
        if (functions.size() && functions[0] != Id::nitro
            && functions[0] != Id::simple_chain
            && !isHalogen(functions[0]))
                sufix = sufixFor(functions[count++]);
          
        vector<string> prefixes;
        string prefix, s;
        while (count < functions.size()) {
            s = prefixFor(functions[count]);
            if (s != "") prefixes.push_back(s);
            count++;
        }
        
        if (prefixes.size()) {
            sortAlphabetically(prefixes);
            prefix += prefixes[0];
            for (unsigned short i = 1; i < prefixes.size(); i++)
                prefix += "-" + prefixes[i];
        }

        if (thereIs(Id::acid)) prefix = "ácido " + prefix;

        string bonds;
        s = prefixFor(Id::alkene);
        if (s != "") bonds += "-" + s;
        s = prefixFor(Id::alkyne);
        if (s != "") bonds += "-" + s;
        if (bonds == "") bonds = "an";
        
        if (sufix == "" || !isVowel(firstLetterOf(sufix))) 
            bonds += "o";
        
        string mult = multiplier(chain.size());
        if(isVowel(firstLetterOf(bonds)))
            return prefix + mult + bonds + sufix;
        return prefix + mult + "a" + bonds + sufix;

        /*
        reorderBy(functions[0]);
        vector<unsigned short> positions = listPositionsOf(functions[0]);
        vector<string> parts;
        string s = "";
        switch (functions[0]) {
        case Id::alkene:
            parts.push_back(interfixFor(positions, functions[0]));
            if (thereIs(Id::alkyne))
                parts.push_back(interfixFor(listPositionsOf(Id::alkyne), Id::alkyne));
            break;
        case Id::alkyne:
            parts.push_back(interfixFor(positions, functions[0]));
        default:
            return multiplier(chain.size()) + "ano";
        }

        string intermediate = concadenate(parts);
        //string intermediate = concadenate(sortAlphabetically(parts));
        if (isVowel(firstLetterOf(intermediate)))
            return multiplier(chain.size()) + intermediate + 'o';
        return multiplier(chain.size()) + 'a' + intermediate + 'o';
        */
        /*
        if (thereIs(Function::acid))
            return "acido " + prefix(chain.size()) + "anoico";
        return "Carbonos: " +  std::to_string(chain.size()) + " " + prefix(chain.size());*/
    }

    string getFormula() {
        string s = "";
        for (Carbon c : chain)
            s += c.toString();
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
        //cout << "free " << free << endl;
        if (free > 2) {
            result.push_back(Id::acid);
            result.push_back(Id::amide);
            result.push_back(Id::nitrile);
            result.push_back(Id::aldehyde);
        }
        if (free > 1)  // 2 bonds
            result.push_back(Id::ketone);
        if (free > 0) { // 1 bond
            result.push_back(Id::alcohol);
            result.push_back(Id::amine);
            result.push_back(Id::nitro);
            result.push_back(Id::bromine);
            result.push_back(Id::chlorine);
            result.push_back(Id::fluorine);
            result.push_back(Id::iodine);
            result.push_back(Id::simple_chain);
            result.push_back(Id::hydrogen);
        }
        return result;
    }

    /*vector<Id> availableSubstituent() {
        unsigned short free = chain.back().freeBonds();
        vector<Id> result;
        for (unsigned short i = 0; i < substituents::list.size(); i++)
            if (Groups::list[i].getBonds() <= free)
                result.push_back(Groups::list[i].getId());
        if (!chain.back().isTerminal() && free) result.push_back(Function::simple_chain);
        return result;
    }*/
    /*unsigned short currentSize() {
        return chain.size();
    }*/
    /*unsigned short freeBonds() {
        return chain.back().freeBonds();
    }*/
};

int main() {
    const map<Id, string> texts = { {Id::acid, "=O & -OH"},{Id::amide, "=O & -NH2"},{Id::nitrile, "-=N"},
           {Id::aldehyde, "=O & -H"},{Id::ketone, "=O"},{Id::alcohol, "-OH"},{Id::amine, "-NH2"},
           {Id::nitro, "-NO2"},{Id::bromine, "-Br"},{Id::chlorine, "-Cl"},{Id::fluorine, "-F"},
           {Id::iodine, "-I"},{Id::simple_chain, "-CH2-CH2..."},{Id::hydrogen, "-H"} };

    Chain chain;

    /*for (int i = 0; i < 100; i++) {
        chain.addSubstituent(substituents::hydrogen);
        chain.addSubstituent(substituents::hydrogen);
        chain.addSubstituent(substituents::hydrogen);
        for (int j = 2; j < i; j++) {
            chain.nextCarbon();
            chain.addSubstituent(substituents::hydrogen);
            chain.addSubstituent(substituents::hydrogen);
        }
        chain.nextCarbon();
        chain.addSubstituent(substituents::hydrogen);
        chain.addSubstituent(substituents::hydrogen);
        chain.addSubstituent(substituents::hydrogen);
        cout << chain.getName() << " ino" << endl;
        chain = Chain();
    }*/

    /*chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);
    chain.nextCarbon();
    chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);
    chain.nextCarbon();
    chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);
    chain.nextCarbon();
    chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);
    chain.addSubstituent(substituents::iodine);*/

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
   
    /*chain.addSubstituent(substituents::acid);
    chain.nextCarbon();
    chain.addSubstituent(substituents::hydrogen);
    chain.addSubstituent(substituents::hydrogen);
    chain.nextCarbon();
    chain.addSubstituent(substituents::hydrogen);
    chain.addSubstituent(substituents::hydrogen);
    chain.nextCarbon();
    chain.addSubstituent(substituents::hydrogen);
    chain.addSubstituent(substituents::hydrogen);
    chain.nextCarbon();
    chain.addSubstituent(substituents::acid);*/

    bool first = true;
    for (vector<Id> available = chain.availableSubstituents(); available.size(); available = chain.availableSubstituents()) {
        cout << " ---> " << chain.getFormula() << endl;
        if (!first)
            cout << " ---------------" << endl << " 0) " << "-C-" << endl;
            //<< " ---------------" << endl << endl; ///////////////////////////
        else first = false;
        
        cout << " ---------------" << endl;
        for (unsigned short i = 0; i < available.size(); i++) {
                cout << ' ' << i + 1 << ") " << texts.find(available[i])->second << endl << " ---------------" << endl;
        }

        unsigned short input;
        cin >> input;
        if (input) {
            input -= 1; // To use it as an index
            for (unsigned short i = 0; i < available.size(); i++) {
                if (input == i) {
                    if (available[i] == Id::simple_chain) {
                        cout << "Carbonos en la cadena: ";
                        unsigned short carbons;
                        cin >> carbons;
                        chain.addSubstituent(Substituent(Id::simple_chain, 1, carbons));
                    }
                    else{
                        chain.addSubstituent(substituents::list.find(available[i])->second);
                    }
                    break;
                }
            }
        }
        else {
            chain.nextCarbon();
        }
    }
    cout << " ---> " << chain.getFormula() << endl << " " << chain.getName();
    cout << endl;
    system("pause");
    return 0;
}