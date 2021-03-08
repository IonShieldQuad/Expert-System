package ionshield.expertsystem.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class SymbolEvaluator {
    private Map<String, BiFunction<Stack<Symbol>, Map<String, Symbol>, Map<String, Symbol>>> functions = new HashMap<>();

    public Map<String, Symbol> process(String key, Stack<Symbol> stack, Map<String, Symbol> vars) throws SymbolException {
        if (!functions.containsKey(key)) throw new SymbolException(key + " not found");
        return functions.get(key).apply(stack, vars);
    }

    public SymbolEvaluator() {
        functions.put("==", (stack, vars)  -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    if (args.get(1).getType() == Symbol.Type.BOOL) {
                        boolean val = (boolean) args.get(0).getValue() == (boolean) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                case INT:
                    if (args.get(1).getType() == Symbol.Type.INT) {
                        boolean val = (int) args.get(0).getValue() == (int) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    if (args.get(1).getType() == Symbol.Type.REAL) {
                        boolean val = (double)(int) args.get(0).getValue() == (double) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                case REAL:
                    if (args.get(1).getType() == Symbol.Type.INT) {
                        boolean val = (double) args.get(0).getValue() == (double)(int) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    if (args.get(1).getType() == Symbol.Type.REAL) {
                        boolean val = (double) args.get(0).getValue() == (double) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                default:
                    boolean val = args.get(1).getValue().equals(args.get(0).getValue());
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
            }
            return out;
        });

        functions.put("!=", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    if (args.get(1).getType() == Symbol.Type.BOOL) {
                        boolean val = (boolean) args.get(0).getValue() != (boolean) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                case INT:
                    if (args.get(1).getType() == Symbol.Type.INT) {
                        boolean val = (int) args.get(0).getValue() != (int) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    if (args.get(1).getType() == Symbol.Type.REAL) {
                        boolean val = (double)(int) args.get(0).getValue() != (double) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                case REAL:
                    if (args.get(1).getType() == Symbol.Type.INT) {
                        boolean val = (double) args.get(0).getValue() != (double)(int) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    if (args.get(1).getType() == Symbol.Type.REAL) {
                        boolean val = (double) args.get(0).getValue() != (double) args.get(1).getValue();
                        stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    }
                    break;
                default:
                    boolean val = !args.get(1).getValue().equals(args.get(0).getValue());
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
            }
            return out;
        });

        functions.put("not", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 1, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    boolean val = !(boolean) args.get(0).getValue();
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    break;
                default:
                    throw new SymbolException("Type error: OP '!' requires BOOL");
            }
            return out;
        });
        functions.put("and", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    if (args.get(1).getType() != Symbol.Type.BOOL) throw new SymbolException("Type error: OP 'and' requires BOOL");
                    boolean val = (boolean) args.get(0).getValue() & (boolean) args.get(1).getValue();
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    break;
                default:
                    throw new SymbolException("Type error: OP 'and' requires BOOL");
            }
            return out;
        });
        functions.put("or", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    if (args.get(1).getType() != Symbol.Type.BOOL) throw new SymbolException("Type error: OP 'or' requires BOOL");
                    boolean val = (boolean) args.get(0).getValue() | (boolean) args.get(1).getValue();
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    break;
                default:
                    throw new SymbolException("Type error: OP 'or' requires BOOL");
            }
            return out;
        });
        functions.put("xor", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, false);
            if (args == null) throw new SymbolException("Error fetching args");
            switch (args.get(0).getType()) {
                case BOOL:
                    if (args.get(1).getType() != Symbol.Type.BOOL) throw new SymbolException("Type error: OP 'xor' requires BOOL");
                    boolean val = (boolean) args.get(0).getValue() ^ (boolean) args.get(1).getValue();
                    stack.push(new Symbol("", Symbol.Type.BOOL, val));
                    break;
                default:
                    throw new SymbolException("Type error: OP 'xor' requires BOOL");
            }
            return out;
        });
        functions.put("=", (stack, vars) -> {
            Map<String, Symbol> out = new HashMap<>();
            List<Symbol> args = Rule.fetchArgs(stack, vars, 2, true);
            if (args == null || args.get(1) == null || args.get(0) == null) throw new SymbolException("Error fetching args");
            args.get(0).setValueAndType(args.get(1).getValue());
            out.put(args.get(0).getName(), args.get(0));
            return out;
        });

    }
}
