package org.example;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * The Cell class represents a cell in a spreadsheet. It provides methods for managing dependencies,
 * updating cell values, and handling circular dependencies.
 */
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

    /**
     * Sets the formula string for the cell.
     *
     * @param formulaString1 The formula string to be set.
     */
    public void setFormulaString(String formulaString1) {
        this.formulaString = formulaString1;
    }

    /**
     * Gets the formula string for the cell.
     *
     * @return The formula string.
     */
    public String getFormulaString() {
        return formulaString;
    }

    /**
     * Sets the name of the cell.
     *
     * @param name The name to be set for the cell.
     */
    public void setCellName(String name) {
        this.actualCellname = name;
    }

    /**
     * Gets the name of the cell.
     *
     * @return The name of the cell.
     */
    public String getCellName() {
        return actualCellname;
    }

    /**
     * Adds a dependent cell to the list of dependents.
     *
     * @param dependent The dependent cell to be added.
     * @return True if the dependent was added, false if it is already a dependent.
     */
    public boolean addDependent(Cell dependent) {
        if (this.isDependent(dependent)) {
            return false;
        }
        dependents.add(dependent);
        return false;
    }

    /**
     * Checks if the given cell is a dependent of the current cell.
     *
     * @param cell The cell to check for dependency.
     * @return True if the cell is a dependent, false otherwise.
     */
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

    /**
     * Sets the current cell to the specified cell.
     *
     * @param cell The cell to set as the current cell.
     */
    public static void setCurrentCell(Cell cell) {
        currentCell = cell;
    }

    /**
     * Gets the current cell.
     *
     * @return The current cell.
     */
    public static Cell getCurrentCell() {
        return currentCell;
    }

    /**
     * Gets the data stored in the cell.
     *
     * @return The data stored in the cell.
     */
    public abstract String getData();

    /**
     * Sets the value of the cell and updates its dependents.
     *
     * @param value The value to set.
     * @throws Exception If an error occurs during the update or circular dependency is detected.
     */
    public void setValue(String value) throws Exception {
        this.numericValue = Double.parseDouble(value);
        visited.clear();
        StringValue = value;
        updateValue(value);
        notifyDependents();
    }

    /**
     * Updates the value of the cell based on the given value.
     *
     * @param value The value used for the update.
     * @throws Exception If an error occurs during the update.
     */
    protected abstract void updateValue(String value) throws Exception;

    /**
     * Adds a dependency to the cell.
     *
     * @param cell The cell to add as a dependency.
     * @throws Exception If an error occurs during the addition of the dependency.
     */
    public void addDependency(Cell cell) throws Exception {
        dependents.add(cell);
    }

    /**
     * Notifies dependents of the cell to update their values.
     *
     * @throws Exception If a circular dependency is detected during the notification.
     */
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

    /**
     * Gets the numeric value stored in the cell.
     *
     * @return The numeric value.
     */
    public Double getNumericValue() {
        return numericValue;
    }

    /**
     * Gets the string value stored in the cell.
     *
     * @return The string value.
     */
    public String getStringValue() {
        return StringValue;
    }

    /**
     * Sets the string value for the cell.
     *
     * @param s The string value to be set.
     */
    public void setStringValue(String s) {
        this.StringValue = s;
    }

    /**
     * Gets the reference of the cell.
     *
     * @return The reference of the cell.
     */
    public abstract String getReference();

    /**
     * Sets the numeric value for the cell.
     *
     * @param result The numeric value to be set.
     */
    public abstract void setNumericValue(Double result);

    /**
     * Sets the visited status of the cell.
     *
     * @param visited The visited status to be set.
     */
    public void setVisited(boolean visited) {
        this.visited1 = visited;
    }

    /**
     * Checks if the cell has been visited.
     *
     * @return True if the cell has been visited, false otherwise.
     */
    public boolean isVisited() {
        return visited1;
    }
}
