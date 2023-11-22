package org.example;

public class OperatorNode implements ExpressionNode{
    private Operator operator;
    private ExpressionNode left;
    private ExpressionNode right;

    public OperatorNode(Operator operator, ExpressionNode left, ExpressionNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate() {
        return operator.apply(left.evaluate(), right.evaluate());
    }
}
