package org.example;

public class NumberCell extends Cell{
    private double number;

    public NumberCell(double Number) {
        number = Number;
    }
    public void setValue(double text) {this.number = text;}
    public double getContent() {
        return number;
    }
    @Override
    public String getData() {
        return String.valueOf(number);
    }
    @Override
    protected void updateValue(String value) {
        this.number = Double.parseDouble(value);
    }
    public double getValue() {
        return number;
    }
    @Override
    public String getReference() {
        // For NumberCell, you can return a reference indicating it's a constant value
        return "Constant";
    }
    @Override
    public void setNumericValue(Double result) {
        this.number = result;
    }
}
