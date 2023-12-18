package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * The SpreadSheet_Manager class manages a collection of spreadsheets.
 */
public class SpreadSheet_Manager {
    private List<SpreadSheet> spreadsheets;

    /**
     * Constructs a SpreadSheet_Manager with an empty list of spreadsheets.
     */
    public SpreadSheet_Manager() {
        spreadsheets = new ArrayList<>();
    }

    /**
     * Adds a new spreadsheet to the manager.
     *
     * @param spreadsheet The spreadsheet to be added.
     */
    public void addSpreadSheet(SpreadSheet spreadsheet) {
        spreadsheets.add(spreadsheet);
    }

    /**
     * Gets a specific spreadsheet by index.
     *
     * @param index The index of the spreadsheet to retrieve.
     * @return The spreadsheet at the specified index, or null if the index is out of bounds.
     */
    public SpreadSheet getSpreadSheet(int index) {
        if (index >= 0 && index < spreadsheets.size()) {
            return spreadsheets.get(index);
        } else {
            return null;
        }
    }
}
