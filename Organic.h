#pragma once

class Carbon {
private:
    vector<Substituent> substituents;
    unsigned short free_bonds;

    //WRITERS:
    string replaceAll(std::string str, const std::string& from, const std::string& to);

    string toDigit(unsigned short n);

public:

    Carbon(unsigned short used_bonds);

    //INTERFACE:
    void addSubstituent(Substituent sub);

    void deleteSubstituent(Substituent sub);

    void deleteSubstituentWithBonds(Substituent sub);

    void bondCarbon();

    void deleteBond();

    //DATA INQUIRES:
    bool thereIs(Id function);

    bool thereIs(Substituent s);

    bool isHalogen(Id function);

    unsigned short freeBonds();

    vector<Substituent> getAllSubstituents();

    vector<Substituent> getAllSubstituents(Id function);

    vector<Substituent> getUniqueSubstituents(Id function);

    //RESULT:
    string toString();
};

class Organic {
protected:
    vector<Carbon> carbons;
    vector<Id> functions;

    //WRITERS:
    string greekPrefix(unsigned short n);

    string multiplier(unsigned short n);

    string quantifier(unsigned short n);

    class Locator {};

    Locator locatorFor(vector<unsigned short> positions, string text);

    string radicalName(Substituent radical);

    //DATA INQUIRES:
    bool thereIs(Id function);

    vector<unsigned short> listBonds(unsigned short bonds);

    vector<unsigned short> listPositionsOf(Id function);

    vector<unsigned short> listPositionsOf(Substituent sub);

    void listUniqueFunctions();

    vector<Substituent> getUniqueSubstituents(Id function);

    vector<Substituent> getUniqueSubstituents();

    vector<Substituent> getAllSubstituents(Id function);

    vector<Substituent> getAllSubstituents();

    bool isHalogen(Id function);

    //UTILITIES:
    bool isLetter(char ch);

    bool isVowel(char ch);

    bool isDigit(char ch);

    unsigned short compareAlphabetically(string a, string b);

    vector<Locator> sortPrefixesAlphabetically(vector<Locator> prefixes);

    unsigned short sum(vector<unsigned short> vector);

    string concadenate(vector<string> vector);

    char firstLetterOf(string s);
};

class Basic : private Organic {
private:
    //PROCESSORS:
    Basic(vector<Carbon> v);

    void correct();

    void reorder();

    bool isRedundant(Id function, vector<unsigned short> positions);

    //WRITERS:
    Locator prefixFor(Id function);

    string sufixFor(Id function);

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
protected:
    void listUniqueFunctions();

    void correct();

public:
    Cyclic();

    void nextCarbon();
};

class Aromatic : private Cyclic {
private:
    bool used_principal_sub;

    void listUniqueFunctions();

public:
    Aromatic();

    void addSubstituent(unsigned short index, Substituent sub);

    unsigned short freeBonds(unsigned short index);

    vector<Id> availableSubstituents();

    bool isRedundant(Id function, vector<unsigned short> positions);
    string prefixName(Id function);

    Locator prefixFor(Id function);

    Locator doublePrefixFor(Substituent sub1, Substituent sub2, unsigned short distance);

    string getName();

    string getFormula();
};