package org.example;

import java.util.ArrayList;
import java.util.List;

public class SpreadSheet_Manager {
    private List<SpreadSheet> spreadsheets;
    public SpreadSheet_Manager() {
        spreadsheets = new ArrayList<>();
    }

    // Method to add a new spreadsheet to the manager
    public void addSpreadSheet(SpreadSheet spreadsheet) {
        spreadsheets.add(spreadsheet);
    }

    // Method to get a specific spreadsheet by index
    public SpreadSheet getSpreadSheet(int index) {
        if (index >= 0 && index < spreadsheets.size()) {
            return spreadsheets.get(index);
        } else {
            return null;
        }
    }
}

