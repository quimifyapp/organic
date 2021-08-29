#pragma once
#include "Constants.h"

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