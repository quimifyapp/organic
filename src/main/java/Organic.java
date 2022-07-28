import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
public class Organic {

    private final List<Carbono> carbonos = new ArrayList<>();
    private final Set<Id> funciones = new HashSet<>();

    // TEXTO:
    string greekPrefix(unsigned short n)
    {
        switch (n)
        {
            case 1: return "hen";
            case 2: return "do";
            case 3: return "tri";
            case 4: return "tetra";
            case 5: return "pent";
            case 6: return "hex";
            case 7: return "hept";
            case 8: return "oct";
            case 9: return "non";
        }

        return ""; // Case 0
    }

    string multiplier(unsigned short n)
    {
        // [1, 9]
        if (n < 10)
        {
            switch (n)
            {
                case 0: return "";
                case 1: return "met";
                case 2: return "et";
                case 3: return "prop";
                case 4: return "but";
            }
            // [5, 9]
            return greekPrefix(n);
        }
        // [10, 19]
        unsigned short ten = n / 10;
        unsigned short unit = n - (ten * 10);
        if (n < 20)
        {
            if (n == 11) return "undec";
            if (n < 15) return greekPrefix(unit) + "dec";
            return greekPrefix(unit) + "adec";
        }
        // [20, 29]
        if (n < 30)
        {
            if (n == 20) return "icos";
            if (n == 21) return "heneicos";
            if (n < 25) return greekPrefix(unit) + "cos";
            return greekPrefix(unit) + "acos";
        }
        // [30, 99]
        string s;
        if (n < 100)
        {
            s = greekPrefix(unit);
            if (unit > 4)
                s += "a";
            s += greekPrefix(ten);
            if (ten == 4)
                s += "cont";
            else
                s += "acont";
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
            case 1: return s + "ahect";
            case 2: return s + "adict";
            case 3: return s + "atrict";
            case 4: return s + "atetract";
            default: return s + "a" + greekPrefix(hundred) + "act";
        }
    }

    string quantifier(unsigned short n)
    {
        switch (n)
        {
            case 1: return "";
            case 2: return "di";
            case 3: return "tri";
            case 4: return "tetra";
        }

        return multiplier(n) + "a";
    }

    class Locator
    {
        public:
        string positions, multiplier, text;
        // EXAMPLES:
		/*
		"2,3-diol" = {"2,3", "di", "ol"}
		"tetrain" = {"", "tetra", "in"}
		"fluoro" = {"", "", "fluoro"}
		*//*
        Locator() {}

        Locator(string positions, string multiplier, string text) :
        positions(positions), multiplier(multiplier), text(text)
    {
    }

        string toString()
        {
            if (positions != "")
                return positions + "-" + multiplier + text;
            else return multiplier + text;
        }
    };

    Locator locatorFor(vector<unsigned short> positions, string text)
    {
        string s_positions;
        if (positions.size())
        {
            for (unsigned short i = 0; i < positions.size() - 1; i++)
            {
                s_positions += to_string(positions[i] + 1);
                s_positions += ",";
            }
            s_positions += to_string(positions[positions.size() - 1] + 1);
        }
        return Locator(s_positions, quantifier(positions.size()), text);
    }

    string radicalName(Substituent radical)
    {
        return (radical.getIso())
                ? "iso" + multiplier(radical.getCarbons()) + "il"
                : multiplier(radical.getCarbons()) + "il";
    }

}*/
