package ionshield.expertsystem.core;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DependencyTree {
    private Map<String, Set<String>> data = new HashMap<>();

    public DependencyTree (Collection<Rule> rules) {
        for (Rule rule : rules) {
            processRule(rule);
        }
    }

    private void processRule(Rule rule) {
        List<Symbol> inputs = rule.getInputVars();
        List<Symbol> outputs = rule.getOutputVars();
        for (Symbol out : outputs) {
            data.computeIfAbsent(out.getName(), k -> new HashSet<>());
            data.get(out.getName()).addAll(inputs.stream().map(Symbol::getName).collect(Collectors.toList()));
        }
    }

    public Set<String> getBaseDependencies(String variableName) {
        if (variableName == null) return new HashSet<>();
        return getBaseDependenciesInternal(variableName, new HashSet<>());
    }

    private Set<String> getBaseDependenciesInternal(String variableName, Set<String> checked) {
        HashSet<String> dependencies = new HashSet<>();
        if (!data.containsKey(variableName) || data.get(variableName) == null || data.get(variableName).isEmpty()) {
            dependencies.add(variableName);
            return dependencies;
        }
        for (String s : data.get(variableName)) {
            if (!checked.contains(s)) {
                checked.add(s);
                dependencies.addAll(getBaseDependenciesInternal(s, checked));
            }
        }
        return dependencies;
    }
}

