package org.example;

import java.util.Set;

public class FormulaCell extends Cell{
    private String formula;
    private double number;
    private SpreadSheet ss;


    public FormulaCell(String formula) {
        this.formula = formula;
        //this.number=Double.parseDouble(formula);
    }
    public void setValue(String text) {this.formula = text;}
    public String getContent() {return formula;}
    public void setNumber(String formulavalue){this.number= Double.parseDouble(formulavalue);}

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

    private boolean isValidCell(int row, int col, Cell[][] cells) {
        return row >= 0 && row < cells.length && col >= 0 && col < cells[0].length;
    }
    @Override
    public String getData() {
        return formula;
    }

    @Override
    protected void updateValue(String formula) throws Exception {
        this.formula = formula;
        ExpressionParser parser = new ExpressionParser();
        String formulaWithoutEquals = formula.substring(1);
        double result = parser.evaluate(this.ss, formulaWithoutEquals);
        setValue(String.valueOf(result));
    }

    @Override
    public String getReference() {
        // For FormulaCell, you can return the actual formula as the reference
        return formula;
    }
    public Double getNumericValue() {
        return number;
    }
    @Override
    public void setNumericValue(Double result) {
        this.number = result;
    }

    private boolean isValidCell(int row, int col) {
        return true;
    }
}
