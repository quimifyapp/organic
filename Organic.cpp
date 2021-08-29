#include <string>
#include <vector>
#include <map>
#include <algorithm>

using namespace std;

/*
    -AROMÁTICOS
    -¿CICLOS?
    -ETER, ESTER, AMINAS MULTIPLES
*/

enum class Id { //Substituent's kinds ordered by nomenclature priority
    acid, carboxyl,
    amide, carbamoyl,
    nitrile, cyanide,
    aldehyde,
    ketone,
    alcohol,
    amine,
    alkene, alkyne,
    nitro,
    halogen, bromine, chlorine, fluorine, iodine,
    radical,
    hydrogen
};

namespace sbts {
    Substituent acid(Id::acid, 3);
    Substituent carboxyl(Id::carboxyl, 1);
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

    Substituent methyl(1, false);
    //Handy methyl constant

    const map<Id, Substituent> list = {
        {Id::acid, acid},
        {Id::carboxyl, carboxyl},
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

class Substituent {
private:
    // The substituent's kind:
    Id function;
    // Amount of e- it shares with the carbon:
    unsigned short bonds;
    // Only for radicals:
    unsigned short carbons = 0;
    bool iso = false;

    /* EXAMPLES:
    *=O                ---> ketone {function = Id:ketone, bonds = 2}

    *-CH2-CH2-CH3      ---> propyl {function = Id::radical, bonds = 1, carbons = 3, iso = false}

                  CH3
                 /
    *-CH2-CH2-CH2      ---> isopentyl {function = Id::radical, bonds = 1, carbons = 5, iso = true}
                 \
                  CH3 */

public:
    Substituent() {}

    Substituent(Id new_function, unsigned short new_bonds) {
        function = new_function;
        bonds = new_bonds;
    }

    Substituent(unsigned short new_carbons, bool new_iso) {
        function = Id::radical;
        bonds = 1;
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

class Carbon {
private:
    vector<Substituent> substituents;
    unsigned short free_bonds = 4;

    //WRITERS:
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

    Carbon(unsigned short used_bonds) {
        free_bonds -= used_bonds;
    }

    //INTERFACE:
    void addSubstituent(Substituent sub) {
        substituents.push_back(sub);
        free_bonds -= sub.getBonds();
    }

    void deleteSubstituent(Substituent sub) {
        for (unsigned short i = 0; i < substituents.size(); i++)
            if (substituents[i].equals(sub)) {
                substituents.erase(substituents.begin() + i);
                break;
            }
    }

    void deleteSubstituentWithBonds(Substituent sub) {
        deleteSubstituent(sub);
        free_bonds += sub.getBonds();
    }

    void bondCarbon() {
        free_bonds -= 1;
    }

    void deleteBond() {
        free_bonds += 1;
    }

    //DATA INQUIRES:
    bool thereIs(Id function) {
        for (unsigned short i = 0; i < substituents.size(); i++)
            if (substituents[i].getFunction() == function)
                return true;
        return false;
    }

    bool thereIs(Substituent s) {
        for (unsigned short i = 0; i < substituents.size(); i++)
            if (substituents[i].equals(s))
                return true;
        return false;
    }

    bool isHalogen(Id function) {
        return (function == Id::bromine || function == Id::chlorine || function == Id::fluorine || function == Id::iodine)
            ? true : false;
    }

    unsigned short freeBonds() {
        return free_bonds;
    }

    vector<Substituent> getAllSubstituents() {
        return substituents;
    }

    vector<Substituent> getAllSubstituents(Id function) {
        vector<Substituent> result;
        for (unsigned short i = 0; i < substituents.size(); i++)
            if (substituents[i].getFunction() == function)
                result.push_back(substituents[i]);
        return result;
    }

    vector<Substituent> getUniqueSubstituents(Id function) {
        vector<Substituent> result;
        for (unsigned short j = 0; j < substituents.size(); j++) {
            bool add = true;
            for (unsigned short k = 0; k < result.size(); k++)
                if (substituents[j].equals(result[k])) {
                    add = false;
                    break;
                }
            if (add) result.push_back(substituents[j]);
        }
        return result;
    }

    //RESULT:
    string toString() {
        const static map<Id, string> texts = { {Id::acid,"OOH"},{Id::carboxyl,"COOH"},{Id::amide,"ONH2"},
            {Id::carbamoyl,"CONH2"},{Id::nitrile,"N"},{Id::cyanide,"CN"},{Id::aldehyde,"HO"},{Id::ketone,"O"},
            {Id::alcohol,"OH"},{Id::amine,"NH2"},{Id::nitro,"NO2"},{Id::bromine,"Br"},{Id::chlorine,"Cl"},
            {Id::fluorine,"F"},{Id::iodine,"I"} };

        vector<Substituent> subs_temp = substituents;

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
                        if (subs_temp[i].getFunction() != Id::radical)
                            result += texts.find(subs_temp[i].getFunction())->second;
                        else {
                            unsigned short quantity = quantities[i];
                            if (subs_temp[i].getFunction() != Id::radical) {
                                if (quantity == 1 && !thereIs(Id::hydrogen)) {
                                    result += texts.find(subs_temp[i].getFunction())->second;
                                }
                                else {
                                    result += texts.find(subs_temp[0].getFunction())->second + ")"
                                        + toDigit(quantity);
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
                        string text = texts.find(subs_temp[0].getFunction())->second;
                        if (text.size() != 1 && !isHalogen(subs_temp[0].getFunction()))
                            result += "(" + text + ")" + toDigit(quantity);
                        else
                            result += text + toDigit(quantity);
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
        return result;
    }
};

class Organic {
protected:
    vector<Carbon> carbons;
    vector<Id> functions;

    //WRITERS:
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
            if (n < 15)
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
        switch (hundred) {
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

    class Locator {
    public:
        string positions = "";
        string multiplier = "";
        string text = "";

        /* EXAMPLES:
        2,3-diol = {"2,3", "di", "ol"}
        tetrain = {"", "tetra", "in"}
        fluoro = {"", "", "fluoro"} */

        Locator() {}

        Locator(string new_positions, string new_multiplier, string new_text) {
            positions = new_positions;
            multiplier = new_multiplier;
            text = new_text;
        }

        string toString() {
            if (positions != "")
                return positions + "-" + multiplier + text;
            else return multiplier + text;
        }
    };

    Locator locatorFor(vector<unsigned short> positions, string text) {
        string s_positions;
        if (positions.size()) {
            for (unsigned short i = 0; i < positions.size() - 1; i++) {
                s_positions += to_string(positions[i] + 1);
                s_positions += ",";
            }
            s_positions += to_string(positions[positions.size() - 1] + 1);
        }
        return Locator(s_positions, quantifier(positions.size()), text);
    }

    string radicalName(Substituent radical) {
        return (radical.getIso())
            ? "iso" + multiplier(radical.getCarbons()) + "il"
            : multiplier(radical.getCarbons()) + "il";
    }

    //DATA INQUIRES:
    bool thereIs(Id function) {
        if (function == Id::alkene) {
            for (unsigned short i = 0; i < carbons.size(); i++)
                if (carbons[i].freeBonds() == 1)
                    return true;
            return false;
        }
        if (function == Id::alkyne) {
            for (unsigned short i = 0; i < carbons.size(); i++)
                if (carbons[i].freeBonds() == 2)
                    return true;
            return false;
        }
        for (unsigned short i = 0; i < carbons.size(); i++)
            if (carbons[i].thereIs(function))
                return true;
        return false;
    }

    vector<unsigned short> listBonds(unsigned short bonds) {
        vector<unsigned short> positions;
        bonds -= 1;
        for (unsigned short i = 0; i < carbons.size(); i++)
            if (carbons[i].freeBonds() == bonds)
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
            for (unsigned short i = 0; i < carbons.size(); i++)
                for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                    if (carbons[i].getAllSubstituents()[j].getFunction() == function)
                        positions.push_back(i);
            return positions;
        }
    }

    vector<unsigned short> listPositionsOf(Substituent sub) {
        vector<unsigned short> positions;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                if (carbons[i].getAllSubstituents()[j].equals(sub))
                    positions.push_back(i);
        }
        return positions;
    }

    void listUniqueFunctions() {
        functions.clear();
        for (unsigned short i = 0; i < carbons.size(); i++) {
            if (carbons[i].freeBonds() == 1) {
                if (find(functions.begin(), functions.end(), Id::alkene) == functions.end())
                    functions.push_back(Id::alkene);
            }
            else if (carbons[i].freeBonds() == 2) {
                if (find(functions.begin(), functions.end(), Id::alkyne) == functions.end())
                    functions.push_back(Id::alkyne);
            }
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                if (find(functions.begin(), functions.end(),
                    carbons[i].getAllSubstituents()[j].getFunction()) == functions.end() &&
                    carbons[i].getAllSubstituents()[j].getFunction() != Id::hydrogen)
                    functions.push_back(carbons[i].getAllSubstituents()[j].getFunction());
        }
        sort(functions.begin(), functions.end());
    }

    vector<Substituent> getUniqueSubstituents(Id function) {
        vector<Substituent> result;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                if (carbons[i].getAllSubstituents()[j].getFunction() == function) {
                    bool add = true;
                    for (unsigned short k = 0; k < result.size(); k++)
                        if (carbons[i].getAllSubstituents()[j].equals(result[k])) {
                            add = false;
                            break;
                        }
                    if (add) result.push_back(carbons[i].getAllSubstituents()[j]);
                }
        }
        return result;
    }

    vector<Substituent> getUniqueSubstituents() {
        vector<Substituent> result;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++) {
                bool add = true;
                for (unsigned short k = 0; k < result.size(); k++)
                    if (carbons[i].getAllSubstituents()[j].equals(result[k])) {
                        add = false;
                        break;
                    }
                if (add) result.push_back(carbons[i].getAllSubstituents()[j]);
            }
        }
        return result;
    }

    vector<Substituent> getAllSubstituents(Id function) {
        vector<Substituent> result, subs;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            subs = carbons[i].getAllSubstituents();
            for (unsigned short j = 0; j < subs.size(); j++)
                if (subs[j].getFunction() == function)
                    result.push_back(subs[j]);
        }
        return result;
    }

    vector<Substituent> getAllSubstituents() {
        vector<Substituent> result, subs;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            subs = carbons[i].getAllSubstituents();
            result.insert(result.end(), subs.begin(), subs.end());
        }
        return result;
    }

    bool isHalogen(Id function) {
        return (function == Id::bromine || function == Id::chlorine ||
            function == Id::fluorine || function == Id::iodine);
    }

    //UTILITIES:
    bool isLetter(char ch) {
        return ch > 96 && ch < 123;
    }

    bool isVowel(char ch) {
        return (ch == 'e') || (ch == 'i') || (ch == 'a') || (ch == 'o') || (ch == 'u');
    }

    bool isDigit(char ch) {
        return ch > 48 && ch < 58;
    }

    unsigned short compareAlphabetically(string a, string b) {
        unsigned short min_length = (a.length() < b.length()) ? a.length() : b.length();
        for (unsigned short i = 0; i < min_length; i++) {
            if (a.at(i) < b.at(i))
                return 0;
            if (a.at(i) > b.at(i))
                return 1;
        }
        return 2;
    }

    vector<Locator> sortPrefixesAlphabetically(vector<Locator> prefixes) {
        //Ordena alfabéticamente los prefijos sin tener en cuenta los multiplicadores
        unsigned short i = 0;
        while (i < prefixes.size() - 1)
            switch (compareAlphabetically(prefixes[i].text, prefixes[i + 1].text)) {
            case 1:
                swap(prefixes[i], prefixes[i + 1]);
                i = 0;
                break;
            default:
                i++;
            }
        return prefixes;
    }

    unsigned short sum(vector<unsigned short> vector) {
        unsigned short sum = 0;
        for (unsigned short i = 0; i < vector.size(); i++)
            sum += vector[i];
        return sum;
    }

    string concadenate(vector<string> vector) {
        string result = "";
        for (unsigned short i = 0; i < vector.size(); i++)
            result += "-" + vector[i];
        return result;
    }

    char firstLetterOf(string s) {
        for (unsigned short i = 0; i < s.size(); i++)
            if (s[i] > 96 && s[i] < 123)
                return s[i];
    }
};

class Basic : private Organic {
private:
    //PROCESSORS:
    Basic(vector<Carbon> v) {
        for (unsigned short i = 0; i < v.size(); i++) {
            nextCarbon();
            for (unsigned short j = 0; j < v[i].getAllSubstituents().size(); j++)
                addSubstituent(v[i].getAllSubstituents()[j]);
        }
    }

    void correct() {
        //Radical en el primer carbono sin terminal -> extensión por el principio
        if (functions.size() && thereIs(Id::radical)) {
            bool changed = false;
            unsigned short i = 0;
            while (i < carbons.size()) {
                //Se extrae el radical más largo:
                Substituent largest_radical(Id::radical, 1);
                vector<Substituent> radicals = carbons[i].getAllSubstituents(Id::radical);
                unsigned short a, b;
                for (unsigned short j = 0; j < radicals.size(); j++) {
                    a = largest_radical.getCarbons();
                    b = radicals[j].getCarbons();
                    if (largest_radical.getIso()) a -= 1;
                    if (radicals[j].getIso()) b -= 1;
                    if (b > a || (a == b && radicals[j].getIso()))
                        largest_radical = radicals[j];
                }
                if (largest_radical.getCarbons()) {
                    //Ha habido al menos un radical en el carbono basic_chain[i]  
                    //Por la izquierda:
                    bool change_left = false;
                    if (largest_radical.getCarbons() - largest_radical.getIso() > i) {
                        //La longitud del radical es mayor a la cadena por la izquierda de basic_chain[i]
                        change_left = true;
                        for (unsigned short k = 0; k < i && change_left; k++) {
                            if (carbons[k].freeBonds()) {
                                change_left = false;
                                break;
                            }
                            //Los carbonos desde el primero hasta antes que basic_chain[i]
                            for (unsigned short j = 0; j < carbons[k].getAllSubstituents().size(); j++)
                                //Los sustituyentes de esos carbonos
                                if (carbons[k].getAllSubstituents()[j].getFunction() != Id::hydrogen &&
                                    !(k == carbons.size() - 2 &&
                                        carbons[k].getAllSubstituents()[j].equals(sbts::methyl) &&
                                        carbons[k].getAllSubstituents().size() == 2 &&
                                        carbons[k].thereIs(Id::hydrogen))) {
                                    //El sustituyente ni es hidrógeno, ni metil en el primero (iso)
                                    change_left = false;
                                    break;
                                }
                        }
                    }
                    //Por la derecha:
                    bool change_right = false;
                    unsigned short carbons_right = carbons.size() - 1 - i;
                    if (largest_radical.getCarbons() - largest_radical.getIso() > carbons_right) {
                        //La longitud del radical es mayor a la cadena por la derecha de basic_chain[i]
                        change_right = true;
                        if (!carbons[i].freeBonds()) {
                            for (unsigned short k = i + 1; k < carbons.size() && change_right; k++) {
                                //Los carbonos desde después que basic_chain[i] hasta el final
                                for (unsigned short j = 0; j < carbons[k].getAllSubstituents().size(); j++)
                                    //Los sustituyentes de esos carbonos
                                    if (carbons[k].getAllSubstituents()[j].getFunction() != Id::hydrogen &&
                                        !(k == carbons.size() - 2 &&
                                            carbons[k].getAllSubstituents()[j].equals(sbts::methyl) &&
                                            carbons[k].getAllSubstituents().size() == 2 &&
                                            carbons[k].thereIs(Id::hydrogen))) {
                                        //El sustituyente ni es hidrógeno, ni un solo metil en el penúltimo (iso)
                                        change_right = false;
                                        break;
                                    }
                            }
                        }
                        else change_right = false;
                    }
                    //Se hace el cambio si se puede:
                    if (change_left || change_right) {
                        //Si puede por ambos, se hará por la derecha por eficiencia
                        vector<Carbon> apendix;
                        //Será el radical convertido a cadena que se apenda
                        Carbon CH2(2);
                        CH2.addSubstituent(sbts::hydrogen);
                        CH2.addSubstituent(sbts::hydrogen);
                        //Eslabón CH2, constante útil
                        Carbon CH3(1);
                        CH3.addSubstituent(sbts::hydrogen);
                        CH3.addSubstituent(sbts::hydrogen);
                        CH3.addSubstituent(sbts::hydrogen);
                        //Eslabón CH3, constante útil
                        for (unsigned short i = 0; i < largest_radical.getCarbons() - 1; i++)
                            apendix.push_back(CH2);
                        if (largest_radical.getIso()) {
                            apendix[apendix.size() - 2].deleteSubstituentWithBonds(sbts::hydrogen);
                            apendix[apendix.size() - 2].addSubstituent(sbts::methyl);
                            apendix[apendix.size() - 1].deleteBond();
                            apendix[apendix.size() - 1].addSubstituent(sbts::hydrogen);
                        }
                        else
                            apendix.push_back(CH3);
                        carbons[i].deleteSubstituent(largest_radical);
                        Substituent new_radical;
                        vector<Carbon>::iterator it;
                        bool iso;
                        if (change_right) {
                            iso = carbons.size() > 2 && carbons[carbons.size() - 2].thereIs(sbts::methyl);
                            new_radical = Substituent(carbons.size() - 1 - i + iso, iso);
                            //La cadena convertida en sustituyente
                            if (new_radical.getCarbons()) {
                                carbons[i].addSubstituent(new_radical);
                                carbons[i].deleteBond();
                            }
                            while (carbons.size() - 1 > i)
                                carbons.pop_back();
                            it = carbons.end();
                        }
                        else { //Left
                            iso = carbons.size() > 2 && carbons[1].thereIs(sbts::methyl);
                            new_radical = Substituent(i + iso, iso);
                            //La cadena convertida en sustituyente
                            if (new_radical.getCarbons()) {
                                carbons[i].addSubstituent(new_radical);
                                carbons[i].deleteBond();
                            }
                            for (unsigned short j = i; j; j--)
                                carbons.erase(carbons.begin());
                            reverse(apendix.begin(), apendix.end());
                            it = carbons.begin();
                        }
                        carbons.insert(it, apendix.begin(), apendix.end());
                        i = 0;
                        if (!changed) changed = true;
                    }
                    else i++;
                }
                else i++;
            }
            if (changed) listUniqueFunctions();
        }
        if (functions.size()) {
            //Cetona y alcohol terminales -> ácido
            if (carbons[0].thereIs(Id::ketone) && carbons[0].thereIs(Id::alcohol)) {
                carbons[0].deleteSubstituentWithBonds(sbts::list.find(Id::ketone)->second);
                carbons[0].deleteSubstituentWithBonds(sbts::list.find(Id::alcohol)->second);
                carbons[0].addSubstituent(sbts::list.find(Id::acid)->second);
                listUniqueFunctions();
            }
            if (carbons[carbons.size() - 1].thereIs(Id::ketone) && carbons[carbons.size() - 1].thereIs(Id::alcohol)) {
                carbons[carbons.size() - 1].deleteSubstituentWithBonds(sbts::list.find(Id::ketone)->second);
                carbons[carbons.size() - 1].deleteSubstituentWithBonds(sbts::list.find(Id::alcohol)->second);
                carbons[carbons.size() - 1].addSubstituent(sbts::list.find(Id::acid)->second);
                listUniqueFunctions();
            }
            //Amida no principal -> carbamoil del anterior
            if (functions[0] != Id::amide) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (carbons[0].thereIs(Id::amide)) {
                    carbons[1].deleteBond();
                    carbons[1].addSubstituent(sbts::carbamoyl);
                    carbons.erase(carbons.begin());
                    listUniqueFunctions();
                }
                else if (carbons[carbons.size() - 1].thereIs(Id::amide)) {
                    carbons[carbons.size() - 1 - 1].deleteBond();
                    carbons[carbons.size() - 1 - 1].addSubstituent(sbts::carbamoyl);
                    carbons.pop_back();
                    listUniqueFunctions();
                }
            }
            //Nitrilo no principal -> ciano del anterior
            if (functions[0] != Id::nitrile) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (carbons[0].thereIs(Id::nitrile)) {
                    carbons[1].deleteBond();
                    carbons[1].addSubstituent(sbts::cyanide);
                    carbons.erase(carbons.begin());
                    listUniqueFunctions();
                }
                else if (carbons[carbons.size() - 1].thereIs(Id::nitrile)) {
                    carbons[carbons.size() - 1 - 1].deleteBond();
                    carbons[carbons.size() - 1 - 1].addSubstituent(sbts::cyanide);
                    carbons.pop_back();
                    listUniqueFunctions();
                }
            }
            //Cetona e hidrógeno terminales -> aldehído
            if (functions[0] >= Id::aldehyde) {
                //Es (o será) principal, no hay otro con mayor preferencia
                if (carbons[0].thereIs(Id::ketone) && carbons[0].thereIs(Id::hydrogen)) {
                    carbons[0].deleteSubstituent(sbts::ketone);
                    carbons[0].deleteSubstituent(sbts::hydrogen);
                    carbons[0].addSubstituent(sbts::aldehyde);
                    listUniqueFunctions();
                }
                if (carbons[carbons.size() - 1].thereIs(Id::ketone) && carbons[carbons.size() - 1].thereIs(Id::hydrogen)) {
                    carbons[carbons.size() - 1].deleteSubstituent(sbts::ketone);
                    carbons[carbons.size() - 1].deleteSubstituent(sbts::hydrogen);
                    carbons[carbons.size() - 1].addSubstituent(sbts::aldehyde);
                    listUniqueFunctions();
                }
            }
            //Aldehído sin ser el grupo principal -> cetona
            if (functions[0] != Id::aldehyde) {
                //Hay otro terminal de mayor preferencia uno de los dos extremos
                if (carbons[carbons.size() - 1].thereIs(Id::aldehyde)) {
                    carbons[carbons.size() - 1].deleteSubstituent(sbts::aldehyde);
                    carbons[carbons.size() - 1].addSubstituent(sbts::ketone);
                    carbons[carbons.size() - 1].addSubstituent(sbts::hydrogen);
                    listUniqueFunctions();
                }
                else if (carbons[0].thereIs(Id::aldehyde)) {
                    carbons[0].deleteSubstituent(sbts::aldehyde);
                    carbons[0].addSubstituent(sbts::ketone);
                    carbons[0].addSubstituent(sbts::hydrogen);
                    listUniqueFunctions();
                }
            }
        }
    }

    void reorder() {
        Basic reversed;
        vector<Carbon> v;
        for (unsigned short i = 0; i < functions.size(); i++) {
            v = carbons;
            reverse(v.begin(), v.end());
            reversed = Basic(v);
            unsigned short normal_sum = sum(listPositionsOf(functions[i]));
            unsigned short reversed_sum = sum(reversed.listPositionsOf(functions[i]));
            if (normal_sum != reversed_sum) {
                if (normal_sum > reversed_sum)
                    carbons = reversed.carbons;
                break;
            }
            else if (functions[i] == Id::radical) {
                //Misma suma y radicales
                vector<string> normal_radicals;
                vector<Substituent> subs = getAllSubstituents(Id::radical);
                for (unsigned short j = 0; j < subs.size(); j++)
                    subs[j].getIso() ?
                    normal_radicals.push_back("iso" + multiplier(subs[j].getCarbons())) :
                    normal_radicals.push_back(multiplier(subs[j].getCarbons()));
                vector<string> reversed_radicals;
                subs = reversed.getAllSubstituents(Id::radical);
                for (unsigned short j = 0; j < subs.size(); j++)
                    subs[j].getIso() ?
                    reversed_radicals.push_back("iso" + multiplier(subs[j].getCarbons())) :
                    reversed_radicals.push_back(multiplier(subs[j].getCarbons()));
                for (unsigned short j = 0; j < normal_radicals.size(); j++) {
                    unsigned short result = compareAlphabetically(normal_radicals[j], reversed_radicals[j]);
                    if (result == 1)
                        carbons = reversed.carbons;
                    if (result != 2)
                        break;
                }
            }
        }
    }

    bool isRedundant(Id function, vector<unsigned short> positions) {
        if (carbons.size() == 1)
            return true;
        else if (carbons.size() == 2) {
            if (getAllSubstituents().size() == 1)
                //Solo es uno
                return true;
            if (function == Id::alkene || function == Id::alkyne)
                //Es alqueno o alquino
                return true;
            if (thereIs(Id::alkyne))
                //Con un alquino solo cabe un sustituyente en cada carbono
                return true;
            vector<Id> functions_temp;
            for (unsigned short i = 0; i < functions.size(); i++)
                if (functions[i] != Id::alkene && functions[i] != Id::alkyne)
                    functions_temp.push_back(functions[i]);
            if (functions_temp.size() == 2) {
                //Hay dos funciones no alqueno o alquino
                unsigned short sum = sbts::list.find(functions_temp[0])->second.getBonds() +
                    sbts::list.find(functions_temp[1])->second.getBonds();
                if (sum > 3)
                    //No caben en un solo carbono
                    return true;
                if (sum > 1 && thereIs(Id::alkene))
                    //No caben en un solo carbono con un alqueno
                    return true;
                if (functions[0] == function)
                    //De los dos, es el de mayor preferencia (1)
                    return true;
            }
        }
        else if (carbons.size() == 3) {
            if (function == Id::ketone && functions[0] >= Id::aldehyde)
                //Propanona
                return true;
            if (functions.size() == 1 && (function == Id::alkene || function == Id::alkyne))
                //Es propeno, propadieno o propino 
                return true;
        }
        return false;
    }

    //WRITERS:
    Locator prefixFor(Id function) {
        const static map<Id, string> texts = { {Id::carbamoyl, "carbamoil"},{Id::cyanide, "ciano"},
            {Id::ketone, "oxo"}, {Id::alcohol, "hidroxi"},{Id::amine, "amino"},{Id::nitro, "nitro"},
            {Id::bromine, "bromo"},{Id::chlorine, "cloro"},{Id::fluorine, "fluoro"},{Id::iodine, "yodo"} };

        vector<unsigned short> positions = listPositionsOf(function);
        if (isRedundant(function, positions))
            return Locator("", quantifier(positions.size()), texts.find(function)->second);
        if (isHalogen(function) && getUniqueSubstituents().size() == 1)
            return Locator("", "per", texts.find(function)->second);
        return locatorFor(positions, texts.find(function)->second);
    }

    string sufixFor(Id function) {
        const static map<Id, string> texts = { {Id::acid, "oico"},{Id::amide, "amida"},{Id::nitrile, "nitrilo"},
            {Id::aldehyde, "al"},{Id::ketone, "ona"},{Id::alcohol, "ol"},{Id::amine, "amina"} };

        vector<unsigned short> positions = listPositionsOf(function);
        if (sbts::list.find(function)->second.getBonds() == 3 ||
            isRedundant(sbts::list.find(function)->second.getFunction(), positions))
            return quantifier(positions.size()) + texts.find(function)->second;
        return locatorFor(positions, texts.find(function)->second).toString();
    }

public:
    Basic() {
        nextCarbon();
    }

    //RESULTS:
    string getName() {
        listUniqueFunctions();
        if (carbons.size() == 1 && functions.size() == 1 && functions[0] == Id::ketone)
            return "dióxido de carbono";
        correct();
        reorder();

        unsigned short count = 0;
        string sufix;
        if (functions.size() &&
            functions[0] != Id::nitro &&
            functions[0] != Id::radical &&
            functions[0] != Id::alkene &&
            functions[0] != Id::alkyne &&
            !isHalogen(functions[0]))
            sufix = sufixFor(functions[count++]);

        vector<Locator> prefixes;
        Locator locator;
        while (count < functions.size()) {
            if (functions[count] != Id::alkene &&
                functions[count] != Id::alkyne &&
                functions[count] != Id::radical) {
                locator = prefixFor(functions[count]);
                if (locator.text != "")
                    prefixes.push_back(locator);
            }
            count++;
        }

        //Cadenas simples
        vector<Substituent> radicals = getUniqueSubstituents(Id::radical);
        for (unsigned short i = 0; i < radicals.size(); i++) {
            locator = locatorFor(listPositionsOf(radicals[i]), radicalName(radicals[i]));
            if (locator.text != "")
                prefixes.push_back(locator);
        }

        string pre = (thereIs(Id::acid)) ? "ácido " : "";
        if (prefixes.size()) {
            prefixes = sortPrefixesAlphabetically(prefixes);
            for (unsigned short i = 0; i < prefixes.size() - 1; i++) {
                pre += prefixes[i].toString();
                if (!isLetter(prefixes[i + 1].toString().at(0)))
                    pre += "-";
            }
            pre += prefixes[prefixes.size() - 1].toString();
        }

        string bonds;
        vector<unsigned short> positions = listPositionsOf(Id::alkene);
        if (positions.size()) {
            if (!isRedundant(Id::alkene, positions))
                locator = locatorFor(positions, "en");
            else
                locator = Locator("", quantifier(positions.size()), "en");
            if (isDigit(locator.toString().at(0)))
                bonds += "-";
            bonds += locator.toString();
        }
        positions = listPositionsOf(Id::alkyne);
        if (positions.size()) {
            if (!isRedundant(Id::alkyne, positions))
                locator = locatorFor(positions, "in");
            else
                locator = Locator("", quantifier(positions.size()), "in");
            if (isDigit(locator.toString().at(0)))
                bonds += "-";
            bonds += locator.toString();
        }

        if (bonds == "")
            bonds = "an";
        if (sufix == "" || !isVowel(firstLetterOf(sufix)))
            bonds += "o";
        if (sufix != "" && isDigit(sufix.at(0)))
            bonds += "-";

        string mult = multiplier(carbons.size());
        if (!isVowel(firstLetterOf(bonds)))
            mult += "a";

        return pre + mult + bonds + sufix;
    }

    string getFormula() {
        string s = "";
        for (unsigned short i = 0; i < carbons.size(); i++) {
            /*if (i) {
                if (carbons[i - 1].free_bonds) {
                    if (carbons[i - 1].free_bonds == 1) s += "=";
                    else s += "≡";
                }
                else s += "-";
            }*/
            s += carbons[i].toString();
        }
        return s;
    }

    //DATA INQUIRES:
    vector<Id> availableSubstituents() {
        vector<Id> result;
        switch (carbons.back().freeBonds()) {
        case 4:
        case 3:
            result.push_back(Id::acid);
            result.push_back(Id::amide);
            result.push_back(Id::nitrile);
            result.push_back(Id::aldehyde);
        case 2:
            result.push_back(Id::ketone);
        case 1:
            result.push_back(Id::alcohol);
            result.push_back(Id::amine);
            result.push_back(Id::nitro);
            result.push_back(Id::halogen);
            result.push_back(Id::radical);
            result.push_back(Id::hydrogen);
        }
        reverse(result.begin(), result.end());
        return result;
    }

    unsigned short freeBonds() {
        return carbons.back().freeBonds();
    }

    //INTERFACE:
    void nextCarbon() {
        if (carbons.size()) {
            carbons.back().bondCarbon();
            carbons.push_back(Carbon(carbons.back().freeBonds() + 1));
        }
        else carbons.push_back(Carbon(0));
    }

    void addSubstituent(Substituent sub) {
        carbons.back().addSubstituent(sub);
    }
};

class Cyclic : protected Organic {
protected:
    /*EXAMPLES:
     B
      \
       CH - CH2
      /        \
    CH2        CH2 ---> CH(A)-CH(B)-CH2-CH2-CH2-CH2
      \        /
       CH2 - CH
               \
                A*/
    void listUniqueFunctions() {
        functions.clear();
        for (unsigned short i = 0; i < carbons.size(); i++) {
            if (carbons[i].freeBonds() == 1) {
                if (find(functions.begin(), functions.end(), Id::alkene) == functions.end())
                    functions.push_back(Id::alkene);
            }
            else if (carbons[i].freeBonds() == 2) {
                if (find(functions.begin(), functions.end(), Id::alkyne) == functions.end())
                    functions.push_back(Id::alkyne);
            }
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                if (find(functions.begin(), functions.end(),
                    carbons[i].getAllSubstituents()[j].getFunction()) == functions.end() &&
                    carbons[i].getAllSubstituents()[j].getFunction() != Id::hydrogen)
                    functions.push_back(carbons[i].getAllSubstituents()[j].getFunction());
        }
        sort(functions.begin(), functions.end());
    }

    void correct() {

    }

public:
    Cyclic() {
        nextCarbon();
        carbons.back().bondCarbon();
    }

    void nextCarbon() {
        if (carbons.size()) {
            carbons.back().bondCarbon();
            carbons.push_back(Carbon(carbons.back().freeBonds() + 1));
        }
        else carbons.push_back(Carbon(0));
    }
    /*
    vector<Id> availableSubstituents() {
        vector<Id> result;
        unsigned short free = cycle.back().free_bonds;
        if (free) {
            result.push_back(Id::carboxyl);
            result.push_back(Id::carbamoyl);
            result.push_back(Id::cyanide);
            if (free == 2) result.push_back(Id::ketone);
            result.push_back(Id::alcohol);
            result.push_back(Id::amine);
            result.push_back(Id::nitro);
            result.push_back(Id::halogen);
            result.push_back(Id::radical);
            result.push_back(Id::hydrogen);
        }
        return result;
    }

    string getFormula() {
        string s = "-";
        for (unsigned short i = 0; i < cycle.size(); i++) {
            //if (i) {
              //  if (carbons[i - 1].free_bonds) {
                //    if (carbons[i - 1].free_bonds == 1) s += "=";
                  //  else s += "≡";
                //}
                //else s += "-";
            //}
            s += cycle[i].toString();
        }
        return s + "-";
    }
    */
};

class Aromatic : private Cyclic {
private:
    bool used_principal_sub = false;

    void listUniqueFunctions() {
        functions.clear();
        for (unsigned short i = 0; i < carbons.size(); i++) {
            for (unsigned short j = 0; j < carbons[i].getAllSubstituents().size(); j++)
                if (find(functions.begin(), functions.end(), carbons[i].getAllSubstituents()[j].getFunction()) == functions.end() &&
                    carbons[i].getAllSubstituents()[j].getFunction() != Id::hydrogen)
                    functions.push_back(carbons[i].getAllSubstituents()[j].getFunction());
        }
        sort(functions.begin(), functions.end());
    }

public:
    Aromatic() {
        nextCarbon();
        nextCarbon();
        nextCarbon();
        nextCarbon();
        nextCarbon();
        carbons.back().bondCarbon();
    }

    void addSubstituent(unsigned short index, Substituent sub) {
        carbons[index].addSubstituent(sub);
    }

    unsigned short freeBonds(unsigned short index) {
        return carbons[index].freeBonds();
    }

    vector<Id> availableSubstituents() {
        static vector<Id> available_subs{ Id::hydrogen,Id::radical,Id::halogen,Id::nitro,Id::amine,Id::alcohol };
        static vector<Id> principal_subs{ Id::cyanide,Id::carbamoyl,Id::carboxyl };
        if (!used_principal_sub) {
            vector<Id> result = available_subs;
            result.insert(result.end(), principal_subs.begin(), principal_subs.end());
            used_principal_sub = true;
            return result;
        }
        else return available_subs;
    }

    bool isRedundant(Id function, vector<unsigned short> positions) {
        return false;
    }

    string prefixName(Id function) {
        const static map<Id, string> texts = { {Id::carbamoyl, "carbamoil"},{Id::cyanide, "ciano"},
            {Id::alcohol, "hidroxi"},{Id::amine, "amino"},{Id::nitro, "nitro"},{Id::bromine, "bromo"},
            {Id::chlorine, "cloro"},{Id::fluorine, "fluoro"},{Id::iodine, "yodo"} };
        return texts.find(function)->second;
    }

    Locator prefixFor(Id function) {
        vector<unsigned short> positions = listPositionsOf(function);
        if (isRedundant(function, positions))
            return Locator("", quantifier(positions.size()), prefixName(function));
        return locatorFor(positions, prefixName(function));
    }

    Locator doublePrefixFor(Substituent sub1, Substituent sub2, unsigned short distance) {
        Locator locator;
        string text1 = (sub1.getFunction() == Id::radical)
            ? radicalName(sub1)
            : prefixName(sub1.getFunction());
        if (sub1.equals(sub2)) {
            locator.multiplier = "di";
            locator.text = text1;
        }
        else {
            string text2 = (sub2.getFunction() == Id::radical)
                ? radicalName(sub2)
                : prefixName(sub2.getFunction());
            locator.text = (compareAlphabetically(text1, text2))
                ? text2 + text1
                : text1 + text2;
        }

        switch (distance) {
        case(1):
            locator.positions = "o";
            break;
        case(2):
            locator.positions = "m";
            break;
        case(3):
            locator.positions = "p";
            break;
        }

        return locator;
    }

    string getName() {
        listUniqueFunctions();

        unsigned short count = 0;

        //Prefijos
        string pre = (thereIs(Id::acid)) ? "ácido " : "";
        if (listPositionsOf(Id::hydrogen).size() == 4) {
            //Only two substituents: o,m,p-...bencene nomenclature
            vector<Substituent> subs = getAllSubstituents();
            for (unsigned short i = 1; i < subs.size(); i++)
                //First one must have index 0 because the cycle has been ordered
                if (subs[i].getFunction() != Id::hydrogen) {
                    pre = doublePrefixFor(subs[0], subs[i], i).toString();
                    break;
                }
        }
        else {
            vector<Locator> prefixes;
            Locator locator;
            while (count < functions.size()) {
                if (functions[count] != Id::radical) {
                    locator = prefixFor(functions[count]);
                    if (locator.text != "")
                        prefixes.push_back(locator);
                }
                count++;
            }
            //Prefijos de radicales
            vector<Substituent> radicals = getUniqueSubstituents(Id::radical);
            for (unsigned short i = 0; i < radicals.size(); i++) {
                locator = locatorFor(listPositionsOf(radicals[i]), radicalName(radicals[i]));
                if (locator.text != "")
                    prefixes.push_back(locator);
            }
            //Unión de los prefijos
            if (prefixes.size()) {
                prefixes = sortPrefixesAlphabetically(prefixes);
                for (unsigned short i = 0; i < prefixes.size() - 1; i++) {
                    pre += prefixes[i].toString();
                    if (!isLetter(prefixes[i + 1].toString().at(0)))
                        pre += "-";
                }
                pre += prefixes[prefixes.size() - 1].toString();
            }
        }

        //CHO benzaldehido
        //OH fenol
        //COOH benzoico o 

        return pre + "benceno";
    }

    string getFormula() {
        string result = "~";
        string pre;
        for (unsigned short i = 0; i < carbons.size(); i++) {
            if (i)
                result += i % 2 ? "=" : "-";
            if (carbons[i].getAllSubstituents().size()) {
                pre = carbons[i].toString();
                if (pre[1] == 'H' || pre[1] == '(')
                    result += pre;
                else
                    result += "C(" + pre.substr(1, pre.size() - 1) + ")";
            }
            else
                result += "C*";
        }
        return result + "~";
    }
};