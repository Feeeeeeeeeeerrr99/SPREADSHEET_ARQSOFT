package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyGraph {
    private Map<String, Set<String>> dependencies;

    public DependencyGraph() {
        this.dependencies = new HashMap<>();
    }

    public void addDependency(String dependentCell, String dependency) {
        dependencies.computeIfAbsent(dependentCell, k -> new HashSet<>()).add(dependency);
    }

    public Set<String> getDependencies(String cell) {
        return dependencies.getOrDefault(cell, new HashSet<>());
    }

    public void removeDependencies(String cell) {
        dependencies.remove(cell);
    }
}

