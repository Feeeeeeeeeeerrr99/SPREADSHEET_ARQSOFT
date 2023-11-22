package org.example;

public interface Operator extends ExpressionElement {
    double apply(double operand1, double operand2);
}
