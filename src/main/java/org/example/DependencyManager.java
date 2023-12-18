package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * The DependencyManager class manages dependencies between cells in a spreadsheet.
 * It provides methods to register and retrieve cells based on their references.
 */
public class DependencyManager {
    /**
     * A map that stores cell references as keys and corresponding cells as values.
     */
    private final Map<String, Cell> cellMap = new HashMap<>();

    /**
     * Registers a cell with the specified reference in the DependencyManager.
     *
     * @param cellReference The reference of the cell to be registered.
     * @param cell The cell object to be registered.
     */
    public void registerCell(String cellReference, Cell cell) {
        cellMap.put(cellReference, cell);
    }

    /**
     * Retrieves the cell associated with the specified reference from the DependencyManager.
     *
     * @param cellReference The reference of the cell to be retrieved.
     * @return The cell object associated with the specified reference, or null if not found.
     */
    public Cell getCell(String cellReference) {
        return cellMap.get(cellReference);
    }
}
