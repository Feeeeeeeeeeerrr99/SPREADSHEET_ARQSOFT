package org.example;

/**
 * The StringCell class represents a cell that stores a string value.
 */
public class StringCell extends Cell {
    private String Textvalue;

    /**
     * Constructs a StringCell with the specified string value.
     *
     * @param text The string value to be stored in the cell.
     */
    public StringCell(String text) {
        Textvalue = text;
    }

    /**
     * Sets the string value for the cell.
     *
     * @param text The string value to be set.
     */
    public void setValue(String text) {
        this.Textvalue = text;
    }

    /**
     * Gets the string value stored in the cell.
     *
     * @return The string value.
     */
    @Override
    public String getData() {
        return Textvalue;
    }

    /**
     * Updates the string value of the cell based on the given value.
     *
     * @param value The value used for the update.
     */
    @Override
    protected void updateValue(String value) {
        this.Textvalue = value;
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
     *               (StringCell cannot have a numeric value, so this method may choose to ignore or handle accordingly.)
     */
    @Override
    public void setNumericValue(Double result) {
        // StringCell cannot have a numeric value, so you may choose to ignore or handle accordingly
    }
}
