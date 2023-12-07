package org.example;
import java.util.*;

public class SpreadSheet {
    private static int rows;
    private static int columns;
    private static String[][] data;
    private static Cell[][] cellMatrix;
    private DependencyGraph dependencyGraph = new DependencyGraph();
    private static DependencyManager dependencyManager;



    public SpreadSheet(int Rows, int Columns) {
        rows = Rows;
        columns = Columns;
        data = new String[rows][columns];
        cellMatrix = new Cell[rows][columns];
    }

    public void setCellreference(String name,String formula) throws Exception {
        int[] coordinates = convertCellReferenceToCoordinates(name);
        if (formula.startsWith("=")){
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue("0");
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        }else if (isNumeric(formula)){
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        }else {
            cellMatrix[coordinates[0]][coordinates[1]] = new StringCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        }
    }

    static Cell getCellByReference(String cellReference) {
        if (isValidCellReference(cellReference)) {
            int row = cellReference.charAt(0) - 'A';
            int col = Integer.parseInt(cellReference.substring(1)) - 1;

            if (row >= 0 && row < rows && col >= 0 && col < columns) {
                return cellMatrix[col][row];
            } else {
                System.out.println("Invalid cell reference: " + cellReference);
            }
        } else {
            System.out.println("Invalid cell reference format: " + cellReference);
        }
        return null;
    }
    public static boolean isValidCellReference(String cellReference) {
        // Regular expression to check if the cell reference is in the correct format (e.g., A1, B2)
        String cellReferencePattern = "^[A-Z]+\\d+$";
        return cellReference.matches(cellReferencePattern);
    }
    public static String convertToCellReference(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            char columnLetter = (char) ('A' + col);
            return columnLetter + String.valueOf(row + 1); // Add 1 to row to match Excel's 1-based indexing
        } else {
            // Handle invalid coordinates
            return "Invalid Cell";
        }
    }
    public static void setValueByCellReference(String cellReference, String cellValue,String formula, Cell currentCell) throws Exception {
        int[] coordinates = convertCellReferenceToCoordinates(cellReference);
        data[coordinates[0]][coordinates[1]] = cellValue;

        if (formula.startsWith("=")) {
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(cellValue);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(cellValue);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(cellReference);

        }else if(!formula.startsWith("=")){
            cellMatrix[coordinates[0]][coordinates[1]] = new StringCell(cellValue);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(cellValue);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(cellReference);
            cellMatrix[coordinates[0]][coordinates[1]].setStringValue(formula);
        }
        else{
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(cellValue);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(cellReference);
        }
        if (currentCell != null) {
            Cell.setCurrentCell(currentCell);
        }

        // Register dependencies
        Cell cell = cellMatrix[coordinates[0]][coordinates[1]];
        if (cell instanceof FormulaCell formulaCell) {
            for (String dependentReference : formulaCell.getDependentReferences()) {
                Cell dependentCell = dependencyManager.getCell(dependentReference);
                if (dependentCell != null) {
                    try {
                        dependentCell.addDependent(cell);
                    } catch (CircularDependencyException e) {
                        // Handle circular dependency
                        System.out.println("Circular Dependency Detected: " + e.getMessage());
                        // Take appropriate action to resolve circular dependency
                        // For example, break the loop, set a default value, etc.
                        dependentCell.setVisited(false);
                    }
                }
            }
        }
    }

    public static void setDependencyManager(DependencyManager manager) {
        dependencyManager = manager;
    }

    public String getValueByCellReference(String cellReference) {
        int[] coordinates = convertCellReferenceToCoordinates(cellReference);
        int row = coordinates[0];
        int col = coordinates[1];

        if (isValidCell(row, col)) {
            Cell cell = cellMatrix[row][col];
            if (cell != null) {
                // Check if the cell has a numeric value
                if (cell.getNumericValue() != null) {
                    // If it's a numeric value, return the numeric value
                    return String.valueOf(cell.getNumericValue());
                } else {
                    // If it's not a numeric value, return the original value
                    return cell.getStringValue();
                }
            }
        }
        return "Invalid Cell Reference or Empty Cell";
    }
    static int[] convertCellReferenceToCoordinates(String cellReference) {
        // Extract column letters (e.g., "A", "B", "C")
        String columnPart = cellReference.replaceAll("[0-9]", "");
        char[] columnChars = columnPart.toCharArray();

        int col = 0;
        for (char c : columnChars) {
            col = col * 26 + (c - 'A' + 1);
        }

        int row = Integer.parseInt(cellReference.replaceAll("[A-Z]", "")) - 1; // Adjust for 0-based indexing

        return new int[]{row, col - 1}; // Adjust for 0-based indexing
    }
    public int getRows(){return rows;}
    public int getColumns(){return columns;}
    public Cell getCell(int row, int column) {
        if (isValidCell(row, column)) {
            return cellMatrix[row][column];
        } else {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
    }
    public static Cell[][] getCellMatrix() {
        return cellMatrix;
    }
    private boolean isValidCell(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
    public String getVariableString(String cellReference) {
        // Assuming cellReference is in the format "A1", "B2", etc.
        int row = parseRow(cellReference);
        int col = parseColumn(cellReference);
        // Check if the cell coordinates are valid
        if (isValidCell(row, col)) {
            return data[row][col];
        } else {
            throw new IllegalArgumentException("Invalid cell reference: " + cellReference);
        }
    }
    private int parseRow(String cellReference) {
        // Assuming the row index is specified as the numeric part of the cell reference
        String rowPart = cellReference.replaceAll("[^0-9]", "");
        return Integer.parseInt(rowPart) - 1; // Adjust to zero-based index
    }
    // Helper method to parse the column index from the cell reference
    private int parseColumn(String cellReference) {
        // Assuming the column index is specified as the alphabetic part of the cell reference
        String colPart = cellReference.replaceAll("[^A-Za-z]", "").toUpperCase();
        int result = 0;
        for (char c : colPart.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result - 1; // Adjust to zero-based index
    }

    public void printSpreadsheet() {
        // Print column headers (A, B, C, ...)
        System.out.print("\t"); // Create space for row numbers
        for (int col = 0; col < columns; col++) {
            char columnLetter = (char) ('A' + col);
            System.out.print(columnLetter + "\t"+"\t");
        }
        System.out.println(); // Move to the next line

        // Print the data with row numbers
        for (int row = 0; row < rows; row++) {
            System.out.print((row + 1) + "\t"); // Row number
            for (int col = 0; col < columns; col++) {
                System.out.print(data[row][col]+ "\t"); // Data value
            }
            System.out.println(); // Move to the next line
        }
    }

    private static Cell createCell(String value) {
        if (isNumeric(value)) {
            return new NumberCell(Double.parseDouble(value));
        } else if (isFormula(value)) {
            return new FormulaCell(value);
        } else {
            return new StringCell(value);
        }
    }
    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private static boolean isFormula(String value) {
        return value.startsWith("=");
    }
    public void setData(String[][] csvData) {
        this.data = csvData;
    }

    public void computeValues() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                computeCellValue(row, col);
            }
        }
    }
    private void computeCellValue(int row, int col) {
        Cell cell = cellMatrix[row][col];
        if (cell instanceof FormulaCell formulaCell) {
            try {
                String formulita=formulaCell.getFormulaString();
                double result=someMethod(this,cell,formulita);
                cell.setNumericValue(result);
                cell.setValue(Double.toString(result));
            } catch (Exception e) {
                // Handle evaluation error
                System.out.println("Error evaluating formula in cell " + convertToCellReference(row, col) + ": " + e.getMessage());
            }
        } else if (cell instanceof StringCell) {
            // String cells don't need computation
            cell.setNumericValue(cell.getNumericValue());
        } else if (cell instanceof NumberCell) {
            // Numeric cells don't need computation
            cell.setNumericValue(cell.getNumericValue());
        }
    }

    public void setDataFromCell(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (Objects.equals(cellMatrix[row][col], null)){
                    data[row][col] = "null";
                }else {
                    data[row][col] = cellMatrix[row][col].getStringValue() +"\t";
                }
            }
        }
    }

    public double someMethod(SpreadSheet currentSpreadSheet,Cell cell,String formula) throws Exception {
        ExpressionParser parser = new ExpressionParser(currentSpreadSheet, cell);
        cell.setFormulaString(formula);
        String formulaWithoutEquals = formula.substring(1);
        parser.setCurrentCell(cell);
        assert cell != null;
        return parser.evaluate(currentSpreadSheet, formulaWithoutEquals,cell);
    }

    public void setValueSpreadSheet(int row, int column, String value, Cell currentCell) {
        data[row][column] = value;
        cellMatrix[row][column] = createCell(value);

        if (currentCell != null) {
            Cell.setCurrentCell(currentCell);

            // Update dependencies in the DependencyGraph
            Set<String> dependencies = dependencyGraph.getDependencies(currentCell.getReference());
            dependencyGraph.removeDependencies(currentCell.getReference());
            updateDependencies(row, column, dependencies);
        }
    }

    private void updateDependencies(int row, int col, Set<String> dependencies) {
        String updatedCellReference = convertToCellReference(row, col);
        for (String dependency : dependencies) {
            dependencyGraph.addDependency(dependency, updatedCellReference);
        }
    }
}