package org.example;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;


public abstract class Cell {
    private Set<Cell> visited = new HashSet<>();
    private boolean visited1 = false;
    private List<Cell> dependencies = new ArrayList<>();
    private Double numericValue;
    private String StringValue;
    private static Cell currentCell;
    private String actualCellname;
    private String formulaString;
    private final List<Cell> dependents = new ArrayList<>();


    public void setFormulaString(String formulaString1) {
        this.formulaString = formulaString1;
    }
    public String getFormulaString() {
        return formulaString;
    }
    public void setCellName(String name){
        this.actualCellname=name;
    }

    public String getCellName(){return actualCellname;}
    public boolean addDependent(Cell dependent) {
        if (this.isDependent(dependent)) {
            return false;
        }
        dependents.add(dependent);
        return false;
    }

    public boolean isDependent(Cell cell) {
        if (dependents.contains(cell)) {
            return true;
        }
        for (Cell dependent : dependents) {
            if (dependent.isDependent(cell)) {
                return true;
            }
        }
        return false;
    }


    public static void setCurrentCell(Cell cell) {
        currentCell = cell;
    }
    public static Cell getCurrentCell() {
        return currentCell;
    }
    public abstract String getData();

    public void setValue(String value) throws Exception {
            this.numericValue = Double.parseDouble(value);
            visited.clear();
            StringValue = value;
            updateValue(value);
            notifyDependents();
    }

    protected abstract void updateValue(String value) throws Exception;

    public void addDependency(Cell cell) throws Exception {
        dependents.add(cell);
    }

    void notifyDependents() throws Exception {
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
    public String getStringValue() {
        return StringValue;
    }
    public void setStringValue(String s){this.StringValue=s;}
    public abstract String getReference();
    public abstract void setNumericValue(Double result);

    public void setVisited(boolean visited) {
        this.visited1 = visited;
    }
    public boolean isVisited() {
        return visited1;
    }
}