package org.example;

/**
 * The NumberCell class represents a cell that stores a numeric value.
 */
public class NumberCell extends Cell {
    private double value;

    /**
     * Constructs a NumberCell with the specified numeric value.
     *
     * @param number The numeric value to be stored in the cell.
     */
    public NumberCell(double number) {
        value = number;
    }

    /**
     * Gets the numeric value stored in the cell.
     *
     * @return The numeric value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Gets the data stored in the cell as a string.
     *
     * @return The string representation of the numeric value.
     */
    @Override
    public String getData() {
        return String.valueOf(value);
    }

    /**
     * Updates the numeric value of the cell based on the given value.
     *
     * @param value The value used for the update.
     */
    @Override
    protected void updateValue(String value) {
        this.value = Double.parseDouble(value);
    }

    /**
     * Gets the reference of the cell.
     *
     * @return The reference of the cell, indicating it's a constant value.
     */
    @Override
    public String getReference() {
        return "Constant";
    }

    /**
     * Sets the numeric value for the cell.
     *
     * @param result The numeric value to be set.
     */
    @Override
    public void setNumericValue(Double result) {
        this.value = result;
    }
}
