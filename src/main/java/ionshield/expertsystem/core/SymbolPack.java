package ionshield.expertsystem.core;

import javafx.util.Pair;

import java.util.*;

public abstract class SymbolPack {
    public SymbolPack() {
        initSymbols();
    }

    private final Map<String, Set<String>> symbolsMap = new HashMap<>();
    private final Map<String, Set<String>> spacedSymbolsMap = new HashMap<>();
    private final Map<String, Integer> precedenceMap = new HashMap<>();
    private final List<Pair<String, String>> parenthesesList = new ArrayList<>();

    protected void add(String symbol, String string) {
        if (!symbolsMap.containsKey(symbol)) {
            symbolsMap.put(symbol, new HashSet<>());
        }
        symbolsMap.get(symbol).add(string);
    }

    protected void addSpaced(String symbol, String string) {
        if (!spacedSymbolsMap.containsKey(symbol)) {
            spacedSymbolsMap.put(symbol, new HashSet<>());
        }
        spacedSymbolsMap.get(symbol).add(string);
        add(symbol, string);
    }


    /**Called after construction to add symbols to the map*/
    protected abstract void initSymbols();

    protected int getSymbolCount() {
        return this.symbolsMap.size();
    }

    protected void setPrecedence(String symbol, int value) {
        precedenceMap.put(symbol, value);
    }

    public int getPrecedence(String symbol) {
        if (precedenceMap.containsKey(symbol)) {
            return precedenceMap.get(symbol);
        }
        return -1;
    }

    public void addParentheses(String left, String right) {
        if (left == null || left.isEmpty() || right == null || right.isEmpty()) throw new IllegalArgumentException("Empty parenthesis");
        parenthesesList.add(new Pair<>(left, right));
    }

    public boolean isParenthesis(String input) {
        for (Pair<String, String> p : parenthesesList) {
            if (p.getKey().equals(input) || p.getValue().equals(input)) return true;
        }
        return false;
    }

    public boolean isLeftParenthesis(String input) {
        for (Pair<String, String> p : parenthesesList) {
            if (p.getKey().equals(input)) return true;
        }
        return false;
    }

    public boolean isRightParenthesis(String input) {
        for (Pair<String, String> p : parenthesesList) {
            if (p.getValue().equals(input)) return true;
        }
        return false;
    }

    public String getMatchingParenthesis(String input) {
        for (Pair<String, String> p : parenthesesList) {
            if (p.getKey().equals(input)) {
                return p.getValue();
            }
            if (p.getValue().equals(input)) {
                return p.getKey();
            }
        }
        throw new IllegalArgumentException(input + " is not a valid parenthesis");
    }

    public String parse(String input) {
        for (String s : symbolsMap.keySet()) {
            Set<String> set = symbolsMap.get(s);
            if (set.contains(input)) {
                return s;
            }
        }
        return null;
    }



    public final Set<String> symbolSet() {
        return this.symbolsMap.keySet();
    }

    public final Set<String> spacedSymbolSet() {
        return this.spacedSymbolsMap.keySet();
    }

    public final Map<String, Set<String>> symbolMap() {
        return this.symbolsMap;
    }

    public final Map<String, Set<String>> spacedSymbolMap() {
        return this.spacedSymbolsMap;
    }
}
