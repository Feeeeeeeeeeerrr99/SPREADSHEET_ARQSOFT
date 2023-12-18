package org.example;
import java.util.HashSet;
import java.util.Set;
/**
 * The FormulaCell class manages all considerations of functions and the format of cells with formulas.
 */
public class FormulaCell extends Cell {
    private String formula;
    private Double value;
    private Set<String> dependentReferences = new HashSet<>();
    private String actualCell;

    /**
     * Constructs a FormulaCell with the specified formula.
     *
     * @param formula The formula for this cell.
     */
    public FormulaCell(String formula) {
        this.formula = formula;
    }

    /**
     * Constructs a FormulaCell with the specified formula and actual cell reference.
     *
     * @param formulavalue The formula for this cell.
     * @param actualCell   The actual cell reference.
     */
    public FormulaCell(String formulavalue, String actualCell) {
        formula = formulavalue;
        this.actualCell = actualCell;
    }

    /**
     * Gets the set of dependent references for this formula cell.
     *
     * @return The set of dependent references.
     */
    public Set<String> getDependentReferences() {
        return dependentReferences;
    }

    /**
     * Sets the value of the formula cell based on the provided text.
     *
     * @param text The text value to set.
     */
    public void setValue(String text) {
        this.value = Double.parseDouble(text);
    }

    /**
     * Gets the formula string of this formula cell.
     *
     * @return The formula string.
     */
    public String getFormulaString() {
        return this.formula;
    }

    /**
     * Evaluates the formula cell using the provided cell matrix.
     *
     * @param cells The cell matrix.
     * @return The result of the evaluation.
     */
    public double evaluate(Cell[][] cells) {
        if (formula.startsWith("=SUMA(") || formula.startsWith("=MIN(") || formula.startsWith("=MAX(") || formula.startsWith("=PROMEDIO(")) {
            int startIndex = formula.indexOf("(") + 1;
            int endIndex = formula.indexOf(")");
            String cellRange = formula.substring(startIndex, endIndex);

            String[] cellReferences = cellRange.split(";");

            if (formula.startsWith("=SUMA(")) {
                double sum = 0;
                for (String cellReference : cellReferences) {
                    if (cellReference.contains(":")) {
                        // Handle range references
                        String[] range = cellReference.split(":");
                        int[] startCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[0]);
                        int[] endCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[1]);

                        for (int i = startCoordinates[0]; i <= endCoordinates[0]; i++) {
                            for (int j = startCoordinates[1]; j <= endCoordinates[1]; j++) {
                                if (isValidCell(i, j, cells)) {
                                    Cell cell = cells[i][j];
                                    if (cell instanceof NumberCell) {
                                        sum += ((NumberCell) cell).getValue();
                                    } else if (cell instanceof FormulaCell) {
                                        sum += ((FormulaCell) cell).evaluate(cells);
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle individual cell references or direct numerical values
                        try {
                            double value = Double.parseDouble(cellReference);
                            sum += value;
                        } catch (NumberFormatException e) {
                            int[] coordinates = SpreadSheet.convertCellReferenceToCoordinates(cellReference);
                            int row = coordinates[0];
                            int col = coordinates[1];
                            if (isValidCell(row, col, cells)) {
                                Cell cell = cells[row][col];
                                if (cell instanceof NumberCell) {
                                    sum += ((NumberCell) cell).getValue();
                                } else if (cell instanceof FormulaCell) {
                                    sum += ((FormulaCell) cell).evaluate(cells);
                                }
                            }
                        }
                    }
                }
                return sum;
            } else if (formula.startsWith("=MIN(")) {
                double min = Double.MAX_VALUE;
                for (String cellReference : cellReferences) {
                    if (cellReference.contains(":")) {
                        // Handle range references
                        String[] range = cellReference.split(":");
                        int[] startCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[0]);
                        int[] endCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[1]);

                        for (int i = startCoordinates[0]; i <= endCoordinates[0]; i++) {
                            for (int j = startCoordinates[1]; j <= endCoordinates[1]; j++) {
                                if (isValidCell(i, j, cells)) {
                                    Cell cell = cells[i][j];
                                    if (cell instanceof NumberCell) {
                                        min = Math.min(min, ((NumberCell) cell).getValue());
                                    } else if (cell instanceof FormulaCell) {
                                        min = Math.min(min, ((FormulaCell) cell).evaluate(cells));
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle individual cell references or direct numerical values
                        try {
                            double value = Double.parseDouble(cellReference);
                            min = Math.min(min, value);
                        } catch (NumberFormatException e) {
                            int[] coordinates = SpreadSheet.convertCellReferenceToCoordinates(cellReference);
                            int row = coordinates[0];
                            int col = coordinates[1];
                            if (isValidCell(row, col, cells)) {
                                Cell cell = cells[row][col];
                                if (cell instanceof NumberCell) {
                                    min = Math.min(min, ((NumberCell) cell).getValue());
                                } else if (cell instanceof FormulaCell) {
                                    min = Math.min(min, ((FormulaCell) cell).evaluate(cells));
                                }
                            }
                        }
                    }
                }
                return min;
            } else if (formula.startsWith("=MAX(")) {
                double max = Double.MIN_VALUE;
                for (String cellReference : cellReferences) {
                    if (cellReference.contains(":")) {
                        // Handle range references
                        String[] range = cellReference.split(":");
                        int[] startCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[0]);
                        int[] endCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[1]);

                        for (int i = startCoordinates[0]; i <= endCoordinates[0]; i++) {
                            for (int j = startCoordinates[1]; j <= endCoordinates[1]; j++) {
                                if (isValidCell(i, j, cells)) {
                                    Cell cell = cells[i][j];
                                    if (cell instanceof NumberCell) {
                                        max = Math.max(max, ((NumberCell) cell).getValue());
                                    } else if (cell instanceof FormulaCell) {
                                        max = Math.max(max, ((FormulaCell) cell).evaluate(cells));
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle individual cell references or direct numerical values
                        try {
                            double value = Double.parseDouble(cellReference);
                            max = Math.max(max, value);
                        } catch (NumberFormatException e) {
                            int[] coordinates = SpreadSheet.convertCellReferenceToCoordinates(cellReference);
                            int row = coordinates[0];
                            int col = coordinates[1];
                            if (isValidCell(row, col, cells)) {
                                Cell cell = cells[row][col];
                                if (cell instanceof NumberCell) {
                                    max = Math.max(max, ((NumberCell) cell).getValue());
                                } else if (cell instanceof FormulaCell) {
                                    max = Math.max(max, ((FormulaCell) cell).evaluate(cells));
                                }
                            }
                        }
                    }
                }
                return max;
            } else if (formula.startsWith("=PROMEDIO(")) {
                double sum = 0;
                int count = 0;
                for (String cellReference : cellReferences) {
                    if (cellReference.contains(":")) {
                        // Handle range references
                        String[] range = cellReference.split(":");
                        int[] startCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[0]);
                        int[] endCoordinates = SpreadSheet.convertCellReferenceToCoordinates(range[1]);

                        for (int i = startCoordinates[0]; i <= endCoordinates[0]; i++) {
                            for (int j = startCoordinates[1]; j <= endCoordinates[1]; j++) {
                                if (isValidCell(i, j, cells)) {
                                    Cell cell = cells[i][j];
                                    if (cell instanceof NumberCell) {
                                        sum += ((NumberCell) cell).getValue();
                                        count++;
                                    } else if (cell instanceof FormulaCell) {
                                        sum += ((FormulaCell) cell).evaluate(cells);
                                        count++;
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle individual cell references or direct numerical values
                        try {
                            double value = Double.parseDouble(cellReference);
                            sum += value;
                            count++;
                        } catch (NumberFormatException e) {
                            int[] coordinates = SpreadSheet.convertCellReferenceToCoordinates(cellReference);
                            int row = coordinates[0];
                            int col = coordinates[1];
                            if (isValidCell(row, col, cells)) {
                                Cell cell = cells[row][col];
                                if (cell instanceof NumberCell) {
                                    sum += ((NumberCell) cell).getValue();
                                    count++;
                                } else if (cell instanceof FormulaCell) {
                                    sum += ((FormulaCell) cell).evaluate(cells);
                                    count++;
                                }
                            }
                        }
                    }
                }
                if (count > 0) {
                    return sum / count;
                }
            }
        }
        return Double.NaN;
    }

    /**
     * Checks if the cell coordinates are valid within the given cell matrix.
     *
     * @param row   The row index.
     * @param col   The column index.
     * @param cells The cell matrix.
     * @return True if the cell coordinates are valid, false otherwise.
     */
    private boolean isValidCell(int row, int col, Cell[][] cells) {
        return row >= 0 && row < cells.length && col >= 0 && col < cells[0].length;
    }

    /**
     * Gets the formula data of the FormulaCell.
     *
     * @return The formula as a string.
     */
    @Override
    public String getData() {
        return formula;
    }

    /**
     * Updates the value of the FormulaCell based on the provided formula.
     *
     * @param formula The new formula to update.
     * @throws Exception If there is an error updating the value.
     */
    @Override
    protected void updateValue(String formula) throws Exception {
        setValue(String.valueOf(formula));
    }

    /**
     * Gets the reference of the FormulaCell.
     * For FormulaCell, the reference is the actual formula.
     *
     * @return The formula as the reference.
     */
    @Override
    public String getReference() {
        return formula;
    }

    /**
     * Gets the numeric value of the FormulaCell.
     *
     * @return The numeric value of the formula.
     */
    public Double getNumericValue() {
        return value;
    }

    /**
     * Sets the numeric value of the FormulaCell.
     *
     * @param result The numeric value to set.
     */
    @Override
    public void setNumericValue(Double result) {
        this.value = result;
    }
}
