#pragma once
#include <map>

using namespace std;

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

class Substituent {
public:
    Substituent();

    Substituent(Id new_function, unsigned short new_bonds);

    Substituent(unsigned short new_carbons, bool new_iso);

    unsigned short getBonds() const;
};

namespace sbts{
    Substituent acid, carboxyl, amide, carbamoyl, nitrile, 
        cyanide, ketone, alcohol, amine, nitro, bromine, 
        chlorine, fluorine, iodine, hydrogen, methyl;

    const map<Id, Substituent> list;
}

class Carbon;

class Organic {};

class Basic : private Organic {
public:
    Basic();

    //RESULTS:
    string getName();

    string getFormula();

    //DATA INQUIRES:
    vector<Id> availableSubstituents();

    unsigned short freeBonds();

    //INTERFACE:
    void nextCarbon();

    void addSubstituent(Substituent sub);
};

class Cyclic : protected Organic {
public:
    Cyclic();

    void nextCarbon();
};

class Aromatic : private Cyclic {
public:
    Aromatic();

    void addSubstituent(unsigned short index, Substituent sub);

    unsigned short freeBonds(unsigned short index);

    vector<Id> availableSubstituents();

    string getName();

    string getFormula();
};