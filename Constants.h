#pragma once
#include <map>

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
private:
    Id function;
    unsigned short bonds;
    unsigned short carbons;
    bool iso;

public:
    Substituent();

    Substituent(Id new_function, unsigned short new_bonds);

    Substituent(unsigned short new_carbons, bool new_iso);

    bool equals(Substituent s);

    Id getFunction() const;
    unsigned short getBonds() const;
    unsigned short getCarbons() const;
    bool getIso() const;
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