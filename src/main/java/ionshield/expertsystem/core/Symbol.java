package ionshield.expertsystem.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Symbol {
    private static final Set<String> BOOL_STRINGS_FALSE = new HashSet<>(Arrays.asList("0", "false", "ложь", "ложно", "нет"));
    private static final Set<String> BOOL_STRINGS_TRUE = new HashSet<>(Arrays.asList("1", "true", "истина", "истинно", "да"));

    private static final Set<String> INT_STRINGS = new HashSet<>(Arrays.asList("integer", "int", "long", "short"));
    private static final Set<String> REAL_STRINGS = new HashSet<>(Arrays.asList("real", "float", "double", "number"));
    private static final Set<String> STRING_STRINGS = new HashSet<>(Arrays.asList("string", "text", "char", "varchar"));
    private static final Set<String> BOOL_STRINGS = new HashSet<>(Arrays.asList("bool", "boolean", "logic"));
    private static final Set<String> ENUM_STRINGS = new HashSet<>(Arrays.asList("enum", "enumeration", "list", "select"));


    private String name;
    private Type type;
    private Object value;
    private Set<?> allowedValues;

    public Symbol(String name, Type type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Symbol(String name, Object value, Set<?> allowedValues) {
        if (allowedValues == null || !allowedValues.contains(value)) throw new IllegalArgumentException("Value" + value + " is not in allowed values set");
        this.name = name;
        this.type = Type.ENUM;
        this.value = value;
        this.allowedValues = allowedValues;
    }

    public static Symbol parseSymbol(String name, Type type, String input) {
        Symbol s = parseSymbol(type, input);
        if (s == null) return null;
        s.setName(name);
        return s;
    }

    public static Symbol parseSymbol(String name, String input) {
        Symbol s = parseSymbol(input);
        if (s == null) return null;
        s.setName(name);
        return s;
    }

    public static Symbol parseSymbol(Type type, String input) {
        Symbol symbol = new Symbol("", type, "");
        String inputLower = input.trim().toLowerCase();
        switch (type) {
            case INT: {
                symbol.value = Integer.parseInt(inputLower);
            }
            case REAL: {
                symbol.value = Double.parseDouble(inputLower);
            }
            case BOOL: {
                if (BOOL_STRINGS_FALSE.contains(inputLower)) {
                    symbol.value = Boolean.FALSE;
                }
                if (BOOL_STRINGS_TRUE.contains(inputLower)) {
                    symbol.value = Boolean.TRUE;
                }
                throw new IllegalArgumentException("Input \"" + input + "\" is not a boolean value");
            }
            case ENUM: {
                if (symbol.allowedValues != null && symbol.allowedValues.contains(inputLower)) {
                    symbol.value = input;
                }
                throw new IllegalArgumentException("Input \"" + input + "\" is not in allowed values set");
            }
            case STRING:
            case OP: {
                symbol.value = input;
            }
        }
        return symbol;
    }

    public static Symbol parseSymbol(String input) {
        String inputLower = input.trim().toLowerCase();

        try {
            int value = Integer.parseInt(inputLower);
            return new Symbol("", Type.INT, value);
        } catch (NumberFormatException ignore) {}
        try {
            double value = Double.parseDouble(inputLower);
            return new Symbol("", Type.REAL, value);
        } catch (NumberFormatException ignore) {}

        if (BOOL_STRINGS_FALSE.contains(inputLower)) {
            return new Symbol("", Type.BOOL, false);
        }
        if (BOOL_STRINGS_TRUE.contains(inputLower)) {
            return new Symbol("", Type.BOOL, true);
        }
        if (input.length() > 1 && inputLower.startsWith("\"") && inputLower.endsWith("\"")) {
            return new Symbol("", Type.STRING, input.trim().substring(1, input.trim().length() - 1));
        } else {
            return new Symbol(inputLower, Type.STRING, "");
        }
    }

    public static Symbol parseSymbol(String input, SymbolPack symbolPack) {
        String inputLower = input.trim().toLowerCase();

        try {
            int value = Integer.parseInt(inputLower);
            return new Symbol("", Type.INT, value);
        } catch (NumberFormatException ignore) {}
        try {
            double value = Double.parseDouble(inputLower);
            return new Symbol("", Type.REAL, value);
        } catch (NumberFormatException ignore) {}

        if (BOOL_STRINGS_FALSE.contains(inputLower)) {
            return new Symbol("", Type.BOOL, false);
        }
        if (BOOL_STRINGS_TRUE.contains(inputLower)) {
            return new Symbol("", Type.BOOL, true);
        }

        String op = symbolPack.parse(inputLower);
        if (op != null) {
            return new Symbol("", Type.OP, op);
        }

        if (input.length() > 1 && inputLower.startsWith("\"") && inputLower.endsWith("\"")) {
            return new Symbol("", Type.STRING, input.trim().substring(1, input.trim().length() - 1));
        } else {
            return new Symbol(inputLower, Type.STRING, "");
        }
    }

    public boolean parse(String input) {
        String inputLower = input.trim().toLowerCase();
        switch (type) {
            case INT: {
                value = Integer.parseInt(inputLower);
                return true;
            }
            case REAL: {
                value = Double.parseDouble(inputLower);
                return true;
            }
            case BOOL: {
                if (BOOL_STRINGS_FALSE.contains(inputLower)) {
                    value = Boolean.FALSE;
                    return true;
                }
                if (BOOL_STRINGS_TRUE.contains(inputLower)) {
                    value = Boolean.TRUE;
                    return true;
                }
                throw new IllegalArgumentException("Input \"" + input + "\" is not a boolean value");
            }
            case ENUM: {
                if (allowedValues != null && allowedValues.contains(inputLower)) {
                    value = inputLower;
                    return true;
                }
                throw new IllegalArgumentException("Input \"" + input + "\" is not in allowed values set");
            }
            case STRING:
            case OP: {
                value = input;
                return true;
            }
        }
        return false;
    }

    public static Type parseType(String input) {
        if (input == null) return null;
        input = input.trim().toLowerCase();



        if (INT_STRINGS.contains(input)) return Type.INT;
        if (REAL_STRINGS.contains(input)) return Type.REAL;
        if (STRING_STRINGS.contains(input)) return Type.STRING;
        if (BOOL_STRINGS.contains(input)) return Type.BOOL;
        if (ENUM_STRINGS.contains(input)) return Type.ENUM;
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValueAndType(Object value) {
        this.value = value;
        if (value instanceof Boolean) {
            this.type = Type.BOOL;
        }
        if (value instanceof Integer) {
            this.type = Type.INT;
        }
        if (value instanceof Double) {
            this.type = Type.REAL;
        }
        if (value instanceof String) {
            this.type = Type.STRING;
        }
    }

    public Set<?> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(Set<?> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public enum Type {
        INT, REAL, STRING, BOOL, ENUM, OP
    }

    @Override
    public String toString() {
        return type + " " + name + ": " + value;
    }
}
