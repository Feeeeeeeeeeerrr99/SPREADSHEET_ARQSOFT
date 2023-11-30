package org.example;
import java.util.HashMap;
import java.util.Map;

public class DependencyManager {
    private final Map<String, Cell> cellMap = new HashMap<>();

    public void registerCell(String cellReference, Cell cell) {
        cellMap.put(cellReference, cell);
    }

    public Cell getCell(String cellReference) {
        return cellMap.get(cellReference);
    }
}
