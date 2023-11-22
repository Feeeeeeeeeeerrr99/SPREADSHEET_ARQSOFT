package org.example;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;


public abstract class Cell {
    private Set<Cell> visited = new HashSet<>();
    private List<Cell> dependencies = new ArrayList<>();
    private Double numericValue;
    private String originalValue;
    private static Cell currentCell;



    public static void setCurrentCell(Cell cell) {
        currentCell = cell;
    }
    public static Cell getCurrentCell() {
        return currentCell;
    }
    public abstract String getData();

    public void setValue(String value) throws Exception {
        this.numericValue=Double.parseDouble (value);
        visited.clear();
        originalValue = value;
        updateValue(value);
        notifyDependents();
    }

    protected abstract void updateValue(String value) throws Exception;

    public void addDependency(Cell cell) throws Exception {
        dependencies.add(cell);
    }

    private void notifyDependents() throws Exception {
        for (Cell dependent : dependencies) {
            if (!visited.contains(dependent)) {
                visited.add(dependent);
                dependent.updateValue(dependent.getData());
                dependent.notifyDependents();
            } else {
                throw new Exception("Circular Dependency Detected!");
            }
        }
    }

    public Double getNumericValue() {
        return numericValue;
    }
    public String getOriginalValue() {
        return originalValue;
    }

    public abstract String getReference();
    public abstract void setNumericValue(Double result);
}