package org.example;

public class NumberCell extends Cell{
    private double value;

    public NumberCell(double number) {
        value = number;
    }

    @Override
    public String getData() {
        return String.valueOf(value);
    }
    @Override
    protected void updateValue(String value) {
        this.value = Double.parseDouble(value);
    }
    public double getValue() {
        return value;
    }
    @Override
    public String getReference() {
        // For NumberCell, you can return a reference indicating it's a constant value
        return "Constant";
    }
    @Override
    public void setNumericValue(Double result) {
        this.value = result;
    }
}
