package org.example;

public class StringCell extends Cell{
    private String Textvalue;
    private Double value;
    public StringCell(String text) {Textvalue = text;
        this.value = Double.parseDouble(text);}
    public void setValue(String text) {
        this.Textvalue = text;
    }
    public String getContent() {return Textvalue;}

    @Override
    public String getData() {
        return Textvalue;
    }

    @Override
    protected void updateValue(String Value) {
        this.Textvalue = Value;
        value = Double.parseDouble(Value);
    }
    @Override
    public String getReference() {
        // For StringCell, you can return a reference indicating it's a constant value
        return "Constant";
    }
    @Override
    public void setNumericValue(Double result) {
        // StringCell cannot have a numeric value, so you may choose to ignore or handle accordingly
    }
}
