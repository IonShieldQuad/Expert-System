package ionshield.expertsystem.core;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Rule {
    private static SymbolEvaluator SYMBOL_EVALUATOR = new SymbolEvaluator();
    private SymbolPack symbolPack = new RuleSymbolsPack();
    private List<Symbol> symbols = new ArrayList<>();
    private List<Symbol> symbolsThen = new ArrayList<>();
    private List<Symbol> symbolsElse = new ArrayList<>();
    private List<Symbol> inputVars = new ArrayList<>();
    private List<Symbol> outputVars = new ArrayList<>();

    public List<Symbol> getInputVars() {
        return inputVars;
    }

    public List<Symbol> getOutputVars() {
        return outputVars;
    }

    public static Rule parseRule(String input, String thenString, String elseString) {
        if (input == null) {
            return null;
        }
        if (thenString == null) thenString = "";
        if (elseString == null) elseString = "";

        final Rule rule = new Rule();

        input = rule.addSpaces(input);
        thenString = rule.addSpaces(thenString);
        elseString = rule.addSpaces(elseString);

        List<String> substrings = rule.split(input);
        List<String> substringsThen = rule.split(thenString);
        List<String> substringsElse = rule.split(elseString);

        substrings.forEach(s -> rule.symbols.add(Symbol.parseSymbol(s, rule.symbolPack)));
        substringsThen.forEach(s -> rule.symbolsThen.add(Symbol.parseSymbol(s, rule.symbolPack)));
        substringsElse.forEach(s -> rule.symbolsElse.add(Symbol.parseSymbol(s, rule.symbolPack)));

        Set<String> inputNames = new HashSet<>();
        Set<String> outputNames = new HashSet<>();

        //Find all input and output variables
        for (int i = 0; i < rule.symbols.size(); i++) {
            Symbol s = rule.symbols.get(i);
            if (s.getName() != null && !s.getName().isEmpty()) {
                if (i != rule.symbols.size() - 1 && rule.symbols.get(i + 1).getType() == Symbol.Type.OP && rule.symbols.get(i + 1).getValue().equals("=")) {
                    if (!outputNames.contains(s.getName())) {
                        rule.outputVars.add(s);
                        outputNames.add(s.getName());
                    }
                }
                else {
                    if (!inputNames.contains(s.getName())) {
                        rule.inputVars.add(s);
                        inputNames.add(s.getName());
                    }
                }
            }
        }
        for (int i = 0; i < rule.symbolsThen.size(); i++) {
            Symbol s = rule.symbolsThen.get(i);
            if (s.getName() != null && !s.getName().isEmpty()) {
                if (i != rule.symbolsThen.size() - 1 && rule.symbolsThen.get(i + 1).getType() == Symbol.Type.OP && rule.symbolsThen.get(i + 1).getValue().equals("=")) {
                    if (!outputNames.contains(s.getName())) {
                        rule.outputVars.add(s);
                        outputNames.add(s.getName());
                    }
                }
                else {
                    if (!inputNames.contains(s.getName())) {
                        rule.inputVars.add(s);
                        inputNames.add(s.getName());
                    }
                }
            }
        }
        for (int i = 0; i < rule.symbolsElse.size(); i++) {
            Symbol s = rule.symbolsElse.get(i);
            if (s.getName() != null && !s.getName().isEmpty()) {
                if (i != rule.symbolsElse.size() - 1 && rule.symbolsElse.get(i + 1).getType() == Symbol.Type.OP && rule.symbolsElse.get(i + 1).getValue().equals("=")) {
                    if (!outputNames.contains(s.getName())) {
                        rule.outputVars.add(s);
                        outputNames.add(s.getName());
                    }
                }
                else {
                    if (!inputNames.contains(s.getName())) {
                        rule.inputVars.add(s);
                        inputNames.add(s.getName());
                    }
                }
            }
        }

        rule.symbols = rule.toPostfixNotation(rule.symbols);
        rule.symbolsThen = rule.toPostfixNotation(rule.symbolsThen);
        rule.symbolsElse = rule.toPostfixNotation(rule.symbolsElse);

        return rule;
    }

    public Map<String, Symbol> evaluate(Map<String, Symbol> variables) {
        /*for (Symbol symbol : symbols) {
            if (symbol.getName() != null && !symbol.getName().isEmpty()) {
                if (variables.containsKey(symbol.getName()) && symbol.getType() == variables.get(symbol.getName()).getType()) {
                    symbol.setValue(variables.get(symbol.getName()).getValue());
                }
                else {
                    return null;
                }
            }
        }*/

        boolean condition = evaluateCondition(variables);
        Map<String, Symbol> outVars = evaluateAction(variables, condition);

        return outVars;
    }

    private static Symbol updateVariable(Symbol symbol, Map<String, Symbol> variables, boolean emptyAllowed) {
        if (symbol.getName() != null && !symbol.getName().isEmpty()) {
            if (variables.containsKey(symbol.getName())) {
                Symbol ext = variables.get(symbol.getName());
                symbol.setAllowedValues(ext.getAllowedValues());
                symbol.setType(ext.getType());
                symbol.setValue(ext.getValue());
                return symbol;
            }
            else {
                if (emptyAllowed) {
                    return symbol;
                }
                else {
                    return null;
                }
            }
        }
        return symbol;
    }

    public static List<Symbol> fetchArgs(Stack<Symbol> stack, Map<String, Symbol> variables, int number, boolean nullAllowed) {
        List<Symbol> out = Arrays.asList(new Symbol[number]);
        for (int i = number - 1; i >= 0; i--) {
            if (stack.isEmpty()) return null;
            out.set(i, updateVariable(stack.pop(), variables, nullAllowed));
        }
        if (!nullAllowed) {
            for (int i = 0; i < number; i++) {
                if (out.get(i) == null) return null;
            }
        }
        return out;
    }

    private Map<String, Symbol> evaluateAction(Map<String, Symbol> variables, boolean condition) {
        List<Symbol> symbols = condition ? symbolsThen : symbolsElse;
        Stack<Symbol> stack = new Stack<>();
        Map<String, Symbol> out = new HashMap<>();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            String symbolP = symbolPack.parse(symbol.getValue().toString());
            if (symbol.getType() != Symbol.Type.OP) {
                stack.push(symbol);
            }
            else {
                try {
                    Map<String, Symbol> outVars = SYMBOL_EVALUATOR.process(symbolP, stack, variables);
                    out.putAll(outVars);
                }
                catch (SymbolException e) {
                    return null;
                }
            }
        }

        return out;
    }

    private Boolean evaluateCondition(Map<String, Symbol> variables) {
        Stack<Symbol> stack = new Stack<>();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            String symbolP = symbolPack.parse(symbol.getValue().toString());
            if (symbol.getType() != Symbol.Type.OP) {
                stack.push(symbol);
            }
            else {
                try {
                    Map<String, Symbol> outVars = SYMBOL_EVALUATOR.process(symbolP, stack, variables);
                }
                catch (SymbolException e) {
                    return false;
                }
            }
        }
        if (!stack.isEmpty()) {
            Symbol s = stack.pop();
            if (s == null || s.getValue() == null) return false;
            switch (s.getType()) {
                case INT:
                    return ((int)s.getValue()) != 0;
                case REAL:
                    return ((double)s.getValue()) != 0;
                case BOOL:
                    return (boolean)s.getValue();
                case STRING:
                    return !((String)s.getValue()).isEmpty();
            }
        }
        return false;
    }

    private List<Symbol> toPostfixNotation(List<Symbol> symbols) {
        List<Symbol> processedSymbols = new ArrayList<>();

        Stack<Symbol> stack = new Stack<>();
        int i = 0;

        while (i < symbols.size()) {
            Symbol s = symbols.get(i);

            if (s.getType() == Symbol.Type.OP) {
                if (symbolPack.isLeftParenthesis(s.getValue().toString())) {
                    stack.push(s);
                }
                else {
                    if (symbolPack.isRightParenthesis(s.getValue().toString())) {
                        while (!stack.empty() && !symbolPack.isLeftParenthesis(stack.peek().getValue().toString())) {
                            processedSymbols.add(stack.pop());
                        }
                        if (!stack.empty() && symbolPack.isLeftParenthesis(stack.peek().getValue().toString())) {
                            stack.pop();
                        }
                    }
                    else {
                        while (!stack.isEmpty()
                                && symbolPack.getPrecedence(stack.peek().getValue().toString()) >= symbolPack.getPrecedence(s.getValue().toString())
                                && !symbolPack.isLeftParenthesis(stack.peek().getValue().toString())) {
                            processedSymbols.add(stack.pop());
                        }
                        stack.push(s);
                    }
                }
            }
            else {
                processedSymbols.add(s);
            }

            i++;
        }

        while (!stack.isEmpty()) {
            processedSymbols.add(stack.pop());
        }

        return processedSymbols;
    }

    private List<String> split(String input) {
        List<String> substrings = new ArrayList<>();

        boolean isInString = false;

        StringBuilder outString = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '"' && (i > 0 && input.charAt(i - 1) != '\\')) {
                if (isInString) {
                    isInString = false;
                    substrings.add(outString.toString());
                    outString = new StringBuilder();
                } else {
                    isInString = true;
                    outString.append(input.charAt(i));
                }
                continue;
            } else {
                if (isInString) {
                    if (i > 0 && input.charAt(i - 1) == '\\') {
                        outString.deleteCharAt(outString.length() - 1);
                    }
                    outString.append(input.charAt(i));
                    continue;
                }
            }

            if (input.charAt(i) == ' ') {
                substrings.add(outString.toString());
                outString = new StringBuilder();
            }
            else {
                outString.append(input.charAt(i));
            }
        }

        if (outString.length() > 0) {
            substrings.add(outString.toString());
        }
        substrings = substrings.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        return substrings;
    }

    private String addSpaces(String inString) {
        StringBuilder outString = new StringBuilder();
        boolean isInString = false;

        for (int i = 0; i < inString.length(); ++i) {
            if (inString.charAt(i) == '"' && (i > 0 && inString.charAt(i - 1) != '\\')) {
                if (isInString) {
                    isInString = false;
                } else {
                    isInString = true;
                    outString.append(inString.charAt(i));
                    continue;
                }
            }
            else {
                if (isInString) {
                    /*if (i > 0 && inString.charAt(i - 1) == '\\') {
                        outString.deleteCharAt(outString.length() - 1);
                    }*/
                    outString.append(inString.charAt(i));
                    continue;
                }
            }

            String matched = null;
            String matchedName = null;
            for (String symbol : symbolPack.spacedSymbolSet()) {
                for (String s : symbolPack.spacedSymbolMap().get(symbol)) {
                    if (inString.startsWith(s, i)) {
                        if (matched == null || matched.length() < s.length()) {
                            matched = s;
                            matchedName = symbol;
                        }
                    }
                }
            }
            if (matched != null) {
                if (i > 0 && inString.charAt(i - 1) != ' ') {
                    outString.append(" ");
                }

                outString.append(inString, i, i + matched.length());

                if (inString.length() > i + matched.length() && inString.charAt(i + matched.length()) != ' ') {
                    outString.append(" ");
                }

                i += matched.length() - 1;
            }
            else {
                outString.append(inString, i, i + 1);
            }
        }

        return outString.toString();
    }


}
