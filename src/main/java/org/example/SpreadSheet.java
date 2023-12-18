package org.example;
import java.util.*;
/**
 * The SpreadSheet class manages all considerations inside a SpreadSheet needs.
 */
public class SpreadSheet {
    private static int rows;
    private static int columns;
    private static String[][] data;
    private static Cell[][] cellMatrix;
    private static DependencyManager dependencyManager;

    /**
     * Constructs a SpreadSheet with the specified number of rows and columns.
     *
     * @param Rows    The number of rows in the spreadsheet.
     * @param Columns The number of columns in the spreadsheet.
     */
    public SpreadSheet(int Rows, int Columns) {
        rows = Rows;
        columns = Columns;
        data = new String[rows][columns];
        cellMatrix = new Cell[rows][columns];
    }

    /**
     * Sets the value of a cell in the spreadsheet based on the given cell reference and formula.
     *
     * @param name    The cell reference (e.g., "A1", "B2").
     * @param formula The formula or value to set in the cell.
     * @throws Exception If there is an error in setting the cell reference.
     */
    public void setCellreference(String name, String formula) throws Exception {
        int[] coordinates = convertCellReferenceToCoordinates(name);
        if (formula.startsWith("=")) {
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue("0");
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        } else if (isNumeric(formula)) {
            cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        } else {
            cellMatrix[coordinates[0]][coordinates[1]] = new StringCell(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setValue(formula);
            cellMatrix[coordinates[0]][coordinates[1]].setCellName(name);
        }
    }

    /**
     * Retrieves a cell from the spreadsheet based on the given cell reference.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @return The cell corresponding to the cell reference.
     */
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

    /**
     * Checks if a cell reference is in the correct format (e.g., "A1", "B2").
     *
     * @param cellReference The cell reference to check.
     * @return True if the cell reference is in the correct format, false otherwise.
     */
    public static boolean isValidCellReference(String cellReference) {
        // Regular expression to check if the cell reference is in the correct format (e.g., A1, B2)
        String cellReferencePattern = "^[A-Z]+\\d+$";
        return cellReference.matches(cellReferencePattern);
    }

    /**
     * Converts row and column indices to a cell reference (e.g., "A1", "B2").
     *
     * @param row    The row index.
     * @param col    The column index.
     * @return The cell reference corresponding to the row and column indices.
     */
    public static String convertToCellReference(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            char columnLetter = (char) ('A' + col);
            return columnLetter + String.valueOf(row + 1); // Add 1 to row to match Excel's 1-based indexing
        } else {
            // Handle invalid coordinates
            return "Invalid Cell";
        }
    }

    /**
     * Sets the value of a cell in the spreadsheet based on the given cell reference, value, formula, and current cell.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @param cellValue     The value to set in the cell.
     * @param formula       The formula associated with the cell.
     * @param currentCell   The current cell being processed.
     */
    public static void setValueByCellReference(String cellReference, String cellValue, String formula, Cell currentCell) {
        try {
            int[] coordinates = convertCellReferenceToCoordinates(cellReference);
            data[coordinates[0]][coordinates[1]] = cellValue;

            if (formula.startsWith("=")) {
                cellMatrix[coordinates[0]][coordinates[1]] = createCell(formula);
                cellMatrix[coordinates[0]][coordinates[1]].setValue(cellValue);
                cellMatrix[coordinates[0]][coordinates[1]].setCellName(cellReference);
            } else if (!formula.startsWith("=") && !isNumeric(formula)) {
                cellMatrix[coordinates[0]][coordinates[1]] = new StringCell(cellValue);
                cellMatrix[coordinates[0]][coordinates[1]].setValue(cellValue);
                cellMatrix[coordinates[0]][coordinates[1]].setCellName(cellReference);
                cellMatrix[coordinates[0]][coordinates[1]].setStringValue(formula);
            } else if (isNumeric(formula)) {
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
                    if (dependentCell != null && !dependentCell.addDependent(cell)) {
                        // Circular dependency detected
                        System.out.println("Circular Dependency Detected involving cell: " + dependentCell.getData());
                        // Take appropriate action to resolve circular dependency
                        // For example, break the loop, set a default value, etc.
                        dependentCell.setVisited(false);
                    }
                }
            }
        } catch (Exception e) {
            // Handle any other exceptions here
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Sets the dependency manager for the spreadsheet.
     *
     * @param manager The dependency manager to set.
     */
    public static void setDependencyManager(DependencyManager manager) {
        dependencyManager = manager;
    }

    /**
     * Converts a cell reference to row and column indices.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @return An array containing row and column indices.
     */
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
    /**
     * Gets the number of rows in the spreadsheet.
     *
     * @return The number of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns in the spreadsheet.
     *
     * @return The number of columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Gets the cell at the specified row and column indices.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return The cell at the specified indices.
     * @throws IllegalArgumentException If the cell coordinates are invalid.
     */
    public Cell getCell(int row, int column) {
        if (isValidCell(row, column)) {
            return cellMatrix[row][column];
        } else {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
    }

    /**
     * Gets the entire cell matrix of the spreadsheet.
     *
     * @return The cell matrix.
     */
    public static Cell[][] getCellMatrix() {
        return cellMatrix;
    }

    /**
     * Checks if the specified cell coordinates are valid.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return True if the cell coordinates are valid, false otherwise.
     */
    private boolean isValidCell(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    /**
     * Gets the variable string from the cell at the specified cell reference.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @return The variable string from the cell.
     * @throws IllegalArgumentException If the cell reference is invalid.
     */
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

    /**
     * Parses the row index from the cell reference.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @return The parsed row index (adjusted to zero-based index).
     */
    private int parseRow(String cellReference) {
        // Assuming the row index is specified as the numeric part of the cell reference
        String rowPart = cellReference.replaceAll("[^0-9]", "");
        return Integer.parseInt(rowPart) - 1; // Adjust to zero-based index
    }

    /**
     * Parses the column index from the cell reference.
     *
     * @param cellReference The cell reference (e.g., "A1", "B2").
     * @return The parsed column index (adjusted to zero-based index).
     */
    private int parseColumn(String cellReference) {
        // Assuming the column index is specified as the alphabetic part of the cell reference
        String colPart = cellReference.replaceAll("[^A-Za-z]", "").toUpperCase();
        int result = 0;
        for (char c : colPart.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result - 1; // Adjust to zero-based index
    }

    /**
     * Prints the spreadsheet with row numbers and column headers.
     */
    public void printSpreadsheet() {
        // Print column headers (A, B, C, ...)
        System.out.print("\t"); // Create space for row numbers
        for (int col = 0; col < columns; col++) {
            char columnLetter = (char) ('A' + col);
            System.out.print(columnLetter + "\t" + "\t");
        }
        System.out.println(); // Move to the next line

        // Print the data with row numbers
        for (int row = 0; row < rows; row++) {
            System.out.print((row + 1) + "\t"); // Row number
            for (int col = 0; col < columns; col++) {
                System.out.print(data[row][col] + "\t"); // Data value
            }
            System.out.println(); // Move to the next line
        }
    }


    /**
     * Creates a cell based on the given value. Detects whether the value is numeric,
     * a formula, or a string and creates the corresponding cell type.
     *
     * @param value The value to create a cell for.
     * @return The created cell.
     */
    private static Cell createCell(String value) {
        if (isNumeric(value)) {
            return new NumberCell(Double.parseDouble(value));
        } else if (isFormula(value)) {
            return new FormulaCell(value);
        } else {
            return new StringCell(value);
        }
    }

    /**
     * Checks if the given value is numeric.
     *
     * @param value The value to check.
     * @return True if the value is numeric, false otherwise.
     */
    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given value is a formula (starts with "=").
     *
     * @param value The value to check.
     * @return True if the value is a formula, false otherwise.
     */
    private static boolean isFormula(String value) {
        return value.startsWith("=");
    }

    /**
     * Sets the data of the spreadsheet using the provided CSV data.
     *
     * @param csvData The CSV data to set.
     */
    public void setData(String[][] csvData) {
        data = csvData;
    }

    /**
     * Computes the values of all cells in the spreadsheet.
     */
    public void computeValues() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                computeCellValue(row, col);
            }
        }
    }

    /**
     * Computes the value of a specific cell in the spreadsheet.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    private void computeCellValue(int row, int col) {
        Cell cell = cellMatrix[row][col];
        if (cell instanceof FormulaCell formulaCell) {
            try {
                String formulita = formulaCell.getFormulaString();
                double result = someMethod(this, cell, formulita);
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

    /**
     * Sets the data of the spreadsheet based on the current cell values.
     */
    public void setDataFromCell() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (Objects.equals(cellMatrix[row][col], null)) {
                    data[row][col] = "null";
                } else {
                    if (cellMatrix[row][col] instanceof StringCell) {
                        data[row][col] = cellMatrix[row][col].getStringValue();
                    } else {
                        data[row][col] = cellMatrix[row][col].getNumericValue() + "\t";
                    }
                }
            }
        }
    }

    /**
     * Performs a computation using an expression parser on the provided formula.
     *
     * @param currentSpreadSheet The current spreadsheet.
     * @param cell               The current cell.
     * @param formula            The formula to evaluate.
     * @return The result of the computation.
     * @throws Exception If an error occurs during evaluation.
     */
    public double someMethod(SpreadSheet currentSpreadSheet, Cell cell, String formula) throws Exception {
        ExpressionParser parser = new ExpressionParser(currentSpreadSheet, cell);
        cell.setFormulaString(formula);
        String formulaWithoutEquals = formula.substring(1);
        parser.setCurrentCell(cell);
        return parser.evaluate(currentSpreadSheet, formulaWithoutEquals, cell);
    }
}