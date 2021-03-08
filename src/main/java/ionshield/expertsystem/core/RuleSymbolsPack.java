package ionshield.expertsystem.core;

public class RuleSymbolsPack extends SymbolPack{
    @Override
    protected void initSymbols() {
        /*add("if", "if");
        add("if", "если");
        add("then", "то");
        add("then", "then");
        add("else", "else");
        add("else", "иначе");
        add("end", "end");
        add("end", "конец");*/

        addSpaced(";", ";");
        addSpaced(",", ",");
        addSpaced(".", ".");
        addSpaced("(", "(");
        addSpaced(")", ")");
        addSpaced(":", ":");
        addSpaced("=", "=");
        addSpaced("==", "==");
        addSpaced("!=", "!=");
        addSpaced("!=", "<>");
        addSpaced(">", ">");
        addSpaced("<", ">");
        addSpaced("<=", "<=");
        addSpaced(">=", ">=");

        add("not", "not");
        add("or", "or");
        add("and", "and");
        add("not", "не");
        add("or", "или");
        add("and", "и");
        addSpaced("not", "!");
        addSpaced("or", "||");
        addSpaced("and", "&&");
        add("xor", "xor");
        add("xor", "иили");
        addSpaced("xor", "^^");;

        addSpaced("+", "+");
        addSpaced("-", "-");
        addSpaced("*", "*");
        addSpaced("/", "/");
        addSpaced("%", "%");
        addSpaced("^", "^");

        /*setPrecedence("end", 1);
        setPrecedence("else", 2);
        setPrecedence("then", 3);
        setPrecedence("if", 4);*/

        addParentheses("(", ")");
        addParentheses("[", "]");
        addParentheses("{", "}");

        setPrecedence("=", 10);
        setPrecedence("and", 12);
        setPrecedence("or", 12);
        setPrecedence("xor", 12);
        setPrecedence("==", 14);
        setPrecedence("!=", 14);
        setPrecedence(">", 16);
        setPrecedence(">=", 16);
        setPrecedence("<", 16);
        setPrecedence("<=", 16);

        setPrecedence("+", 20);
        setPrecedence("-", 20);
        setPrecedence("*", 22);
        setPrecedence("/", 22);
        setPrecedence("%", 22);

        setPrecedence("^", 23);

        setPrecedence("!", 24);

        setPrecedence("(", 30);
        setPrecedence(")", 0);
    }
}
