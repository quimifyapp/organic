#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>

#include "Organic.h"

using namespace std;

/*
    -AROMÁTICOS
    -¿CICLOS?  
    -ETER, ESTER, AMINAS MULTIPLES    
*/

//----------------------------------------------------------------------------------------------------------------------------

//Random:
#include <ctime>
#include <cstdlib> 

unsigned short getRandomNumber(unsigned short min, unsigned short max) {
    static constexpr double fraction{ 1.0 / (RAND_MAX + 1.0) };
    return min + static_cast<unsigned short>((max - min + 1) * (std::rand() * fraction));
}

void aleatorios() {
    std::srand(static_cast<unsigned int>(std::time(nullptr)));
    while (true) {
        Basic carbons2;
        unsigned short amount = getRandomNumber(0, 8);
        for (unsigned short i = 0; i < amount; i++) {
            carbons2.addSubstituent(sbts::hydrogen);
            for (vector<Id> available = carbons2.availableSubstituents(); available.size() && carbons2.freeBonds() > 1; available = carbons2.availableSubstituents()) {
                Substituent s;
                unsigned short number;
                number = getRandomNumber(0, available.size() - 1);
                if (available[number] == Id::radical && 1 < carbons2.freeBonds()) {
                    if (getRandomNumber(0, 1)) 
                        carbons2.addSubstituent(Substituent(getRandomNumber(1, 8), false));
                    else if (carbons2.freeBonds() > 2 && getRandomNumber(0, 8)) {
                        carbons2.addSubstituent(sbts::methyl);
                        carbons2.addSubstituent(sbts::methyl);
                    }
                    else 
                        carbons2.addSubstituent(Substituent(getRandomNumber(3, 8), true));
                }
                else if (available[number] == Id::halogen && 1 < carbons2.freeBonds()) {
                    switch (getRandomNumber(0, 3))
                    {
                    case 0:
                        s = sbts::bromine;
                        break;
                    case 1:
                        s = sbts::chlorine;
                        break;
                    case 2:
                        s = sbts::fluorine;
                        break;
                    case 3:
                        s = sbts::iodine;
                        break;
                    }
                    carbons2.addSubstituent(s);
                }
                else if (sbts::list.find(available[number])->second.getBonds() < carbons2.freeBonds()) {
                    s = sbts::list.find(available[number])->second;
                    carbons2.addSubstituent(s);
                }
                if (!getRandomNumber(0, 2))
                    break;
            }
            carbons2.nextCarbon();
        }
        for (vector<Id> available = carbons2.availableSubstituents(); available.size(); available = carbons2.availableSubstituents()) {
            unsigned short number;
            do {
                number = getRandomNumber(0, available.size() - 1);
            } while (available[number] == Id::radical || available[number] == Id::halogen);
            carbons2.addSubstituent(sbts::list.find(available[number])->second);
        }
        cout << carbons2.getFormula() << endl;
        cout << carbons2.getName() << endl << endl;
    }
}

int main() {
    const map<Id, string> texts = {{Id::acid, "-=OOH"},{Id::carboxyl, "-COOH"},{Id::amide, "-=ONH2"},
        {Id::carbamoyl, "-CONH2"},{Id::nitrile, "-=N"},{Id::cyanide, "-CN"},{Id::aldehyde, "-=OH"},
        {Id::ketone, "=O"},{Id::alcohol, "-OH"},{Id::amine, "-NH2"},{Id::nitro, "-NO2"},
        {Id::halogen, "-X"},{Id::radical, "-CH2-CH2..."},{Id::hydrogen, "-H"}};
    aleatorios();

    while (true) {
        Aromatic aromatic;
        bool first = true;
        vector<Id> available;
        for (unsigned short c = 0; c < 6; c++) {
            available = aromatic.availableSubstituents();
            cout << " ---> " << aromatic.getFormula() << endl;
            if (!first)
                cout << " -------------------------" << endl << " 0) " << "-C-" << endl;
            else first = false;

            cout << " -------------------------" << endl;
            for (unsigned short i = 0; i < available.size(); i++)
                cout << ' ' << i + 1 << ") " << texts.find(available[i])->second << endl << " -------------------------" << endl;

            cout << " Seleccionar: ";
            unsigned short input;
            cin >> input;
            input--;
            for (unsigned short i = 0; i < available.size(); i++)
                if (input == i) {
                    if (available[i] == Id::radical) {
                        cout << " -------------------------" << endl << " 1) -CH2-...-CH3" << endl <<
                            " -------------------------" << endl << "                  CH3" << endl <<
                            "                 /" << endl << " 2) -CH2-...-CH2" << endl <<
                            "                 \\" << endl << "                  CH3" <<
                            endl << " -------------------------" << endl << " Seleccionar: ";
                        cin >> input;
                        unsigned short carbons;
                        if (input == 1) {
                            do {
                                cout << endl << " -CH2-...-CH3" << endl <<
                                    "  {---------}" << endl << endl << " Carbonos del radical: ";
                                cin >> carbons;
                            } while (!carbons || carbons > 6);
                            aromatic.addSubstituent(c, Substituent(carbons, false));
                        }
                        else if (input == 2) {
                            while (true) {
                                cout << endl << "               CH3" << endl <<
                                    "              /" << endl << " -CH2-...-CH2" << endl <<
                                    "  {---------} \\" << endl << "               CH3"
                                    << endl << endl << " Carbonos de la cadena recta: ";
                                cin >> carbons;
                                if (carbons > 1 && carbons < 5)
                                    aromatic.addSubstituent(c, Substituent(carbons + 2, true));
                                else continue;
                                break;
                            }
                        }
                    }
                    else if (available[i] == Id::halogen) {
                        do {
                            cout << " -------------------------" << endl;
                            cout << " 1) " << "-Br" << endl << " -------------------------" << endl;
                            cout << " 2) " << "-Cl" << endl << " -------------------------" << endl;
                            cout << " 3) " << "-F" << endl << " -------------------------" << endl;
                            cout << " 4) " << "-I" << endl << " -------------------------" << endl;
                            cout << " Seleccionar: ";
                            cin >> input;
                        } while (input > 4);
                        switch (input)
                        {
                        case 1:
                            aromatic.addSubstituent(c, sbts::bromine);
                            break;
                        case 2:
                            aromatic.addSubstituent(c, sbts::chlorine);
                            break;
                        case 3:
                            aromatic.addSubstituent(c, sbts::fluorine);
                            break;
                        case 4:
                            aromatic.addSubstituent(c, sbts::iodine);
                            break;
                        }
                    }
                    else aromatic.addSubstituent(c, sbts::list.find(available[i])->second);
                    break;
                }
        }
        cout << " " << aromatic.getFormula() << endl;
        cout << " ---> " << aromatic.getName() << endl;
        system("pause");
    }

    while(true) {
        Basic basic_chain;
        /*
        basic_chain.addSubstituent(sbts::hydrogen);
        basic_chain.nextCarbon();
        basic_chain.nextCarbon();
        basic_chain.addSubstituent(sbts::iodine);
        basic_chain.addSubstituent(sbts::iodine);
        basic_chain.nextCarbon();
        basic_chain.addSubstituent(sbts::chlorine);
        basic_chain.nextCarbon();
        basic_chain.addSubstituent(sbts::hydrogen);
        basic_chain.addSubstituent(sbts::hydrogen);
        */
        //CH -= C - CI2 - C(Cl) = CH2
        /*
        for (unsigned short n = 4; n < 999; n++) {
            Basic ch;
            basic_chain = ch;
            basic_chain.addSubstituent(sbts::hydrogen);
            basic_chain.nextCarbon();
            for (unsigned short i = 0; i < n - 4; i++) {
                basic_chain.nextCarbon();
                basic_chain.addSubstituent(sbts::hydrogen);
                basic_chain.addSubstituent(sbts::hydrogen);
            }
            basic_chain.nextCarbon();
            basic_chain.addSubstituent(sbts::hydrogen);
            basic_chain.nextCarbon();
            basic_chain.nextCarbon();
            basic_chain.addSubstituent(sbts::hydrogen);
            basic_chain.addSubstituent(sbts::hydrogen);

            cout << n + 1 << ": " << basic_chain.getName() << endl;
        }
        */
        //Cadenas con bucle
        bool first = true;
        for (vector<Id> available = basic_chain.availableSubstituents(); available.size(); available = basic_chain.availableSubstituents()) {
            cout << " ---> " << basic_chain.getFormula() << endl;
            if (!first)
                cout << " -------------------------" << endl << " 0) " << "-C-" << endl;
            else first = false;

            cout << " -------------------------" << endl;
            for (unsigned short i = 0; i < available.size(); i++)
                cout << ' ' << i + 1 << ") " << texts.find(available[i])->second << endl << " -------------------------" << endl;

            cout << " Seleccionar: ";
            unsigned short input;
            cin >> input;
            if (input) {
                input--; 
                for (unsigned short i = 0; i < available.size(); i++)
                    if (input == i) {
                        if (available[i] == Id::radical) {
                            cout << " -------------------------" << endl << " 1) -CH2-...-CH3" << endl << 
                                " -------------------------"<< endl << "                  CH3" << endl << 
                                "                 /" << endl << " 2) -CH2-...-CH2" <<  endl << 
                                "                 \\" << endl << "                  CH3" << 
                                endl << " -------------------------" << endl << " Seleccionar: ";
                            cin >> input;
                            unsigned short carbons;
                            if (input == 1) {
                                do {
                                    cout << endl << " -CH2-...-CH3" << endl <<
                                        "  {---------}" << endl << endl << " Carbonos del radical: ";
                                    cin >> carbons;
                                } while (!carbons);
                                basic_chain.addSubstituent(Substituent(carbons, false));
                            }
                            else if (input == 2) {
                                while(true){
                                    cout << endl << "               CH3" << endl <<
                                        "              /" << endl << " -CH2-...-CH2" << endl <<
                                        "  {---------} \\" << endl << "               CH3"
                                        << endl << endl << " Carbonos de la cadena recta: ";
                                    cin >> carbons;
                                    if (carbons)
                                        basic_chain.addSubstituent(Substituent(carbons + 2, true));
                                    else if (basic_chain.freeBonds() > 1) {
                                        basic_chain.addSubstituent(sbts::methyl);
                                        basic_chain.addSubstituent(sbts::methyl);
                                    }
                                    else continue;
                                    break;
                                }
                            }
                        }
                        else if (available[i] == Id::halogen) {
                            do {
                                cout << " -------------------------" << endl;
                                cout << " 1) " << "-Br" << endl << " -------------------------" << endl;
                                cout << " 2) " << "-Cl" << endl << " -------------------------" << endl;
                                cout << " 3) " << "-F" << endl << " -------------------------" << endl;
                                cout << " 4) " << "-I" << endl << " -------------------------" << endl;
                                cout << " Seleccionar: ";
                                cin >> input;
                            } while (input > 4);
                            switch (input)
                            {
                            case 1:
                                basic_chain.addSubstituent(sbts::bromine);
                                break;
                            case 2:
                                basic_chain.addSubstituent(sbts::chlorine);
                                break;
                            case 3:
                                basic_chain.addSubstituent(sbts::fluorine);
                                break;
                            case 4:
                                basic_chain.addSubstituent(sbts::iodine);
                                break;
                            }
                        }
                        else basic_chain.addSubstituent(sbts::list.find(available[i])->second);
                        break;
                    }
            }
            else basic_chain.nextCarbon();
        }
        cout << " " << basic_chain.getFormula() << endl;
        cout << " ---> " << basic_chain.getName() << endl;
        system("pause");
    }

    return 0;
}