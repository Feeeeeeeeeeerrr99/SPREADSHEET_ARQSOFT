package org.example;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {
    private Stack<Operator> operators;
    private Stack<Double> values;
    private SpreadSheet SpreadSheet;
    private Cell currentCell;

    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }
    public ExpressionParser() {
        this.operators = new Stack<>();
        this.values = new Stack<>();
    }

    public double evaluate(SpreadSheet s, String expression) throws Exception {
        this.SpreadSheet = s;
        String preprocessedFormula = preprocessFormula(expression);
        String rpn = infixToRPN(preprocessedFormula);
        return buildSyntaxTreeAndCompute(rpn);
    }
    public String infixToRPN(String infixExpression) {
        StringBuilder output = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < infixExpression.length(); i++) {
            char c = infixExpression.charAt(i);

            if (isOperand(c)) {
                // Check for variables (e.g., A1) and replace with their values
                StringBuilder variable = new StringBuilder();
                while (i < infixExpression.length() && (Character.isLetterOrDigit(infixExpression.charAt(i)) || infixExpression.charAt(i) == '_')) {
                    variable.append(infixExpression.charAt(i));
                    i++;
                }
                i--; // Move back one position to handle the non-variable character in the next iteration

                if (isVariable(variable.toString())) {
                    addDependencyToCurrentCell(variable.toString());
                    output.append(getVariableValue(variable.toString())).append(" ");
                } else {
                    output.append(variable.toString()).append(" ");
                }
            } else if (isOperator(c)) {
                handleOperator(c, operatorStack, output);
                output.append(" "); // Add a space after each operator
            } else if (c == '(') {
                operatorStack.push(c);
            } else if (c == ')') {
                handleClosingParenthesis(operatorStack, output);
            }
        }

        // Append remaining operators in the stack to the output with spaces
        while (!operatorStack.isEmpty()) {
            output.append(operatorStack.pop()).append(" ");
        }

        // Trim any leading or trailing whitespace
        return output.toString().trim();
    }

    public String preprocessFormula(String formula) {
        // Identify and replace basic operations in the formula
        String processedFormula = formula;

        // Define a pattern for identifying basic operations like SUMA(), PROMEDIO(), MIN(), and MAX()
        Pattern pattern = Pattern.compile("(SUMA|PROMEDIO|MIN|MAX)\\(([^)]+)\\)");

        Matcher matcher = pattern.matcher(formula);
        while (matcher.find()) {
            String operation = matcher.group(1);
            String arguments = matcher.group(2);
            FormulaCell formulaCell = new FormulaCell('='+operation +'('+arguments +')');
            double result = formulaCell.evaluate(org.example.SpreadSheet.getCellMatrix());
            int result2 = (int) result;

            // Replace the matched part in the formula with the computed result as a string
            processedFormula = processedFormula.replace(matcher.group(), String.valueOf(result2));
        }

        return processedFormula;
    }


    private void addDependencyToCurrentCell(String variable) {
        // Assuming you have a current cell in your context
        // Update this part based on your actual structure
        Cell dependentCell = SpreadSheet.getCellByReference(variable);
        if (currentCell != null && dependentCell != null) {
            try {
                currentCell.addDependency(dependentCell);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Add a method to check if a token is a variable
    private static boolean isVariable(String token) {
        // Implement your logic to check if the token is a variable
        // Example: Assume variables are alphanumeric and can contain underscores
        return token.matches("[a-zA-Z][a-zA-Z0-9_]*");
    }

    // Add a method to get the value of a variable
    private String getVariableValue(String variable) {
        // Implement your logic to get the value associated with the variable
        // Example: Assume you have a SpreadSheet class that can provide cell values
        if (this.SpreadSheet != null) {
            return SpreadSheet.getVariableString(variable);
        } else {
            throw new IllegalStateException("SpreadSheet instance not set. Initialize it before using variables.");
        }
    }

    private boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    private void handleOperator(char operator, Stack<Character> operatorStack, StringBuilder output) {
        while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
            output.append(operatorStack.pop());
        }
        operatorStack.push(operator);
    }

    private void handleClosingParenthesis(Stack<Character> operatorStack, StringBuilder output) {
        while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
            output.append(operatorStack.pop());
        }
        operatorStack.pop();
    }
    static class TreeNode {
        char operator; // Change char to Object to accommodate both operators and operands
        double operand; // Add an operand field
        TreeNode left, right;

        TreeNode(char operator) {
            this.operator = operator;
            this.left = this.right = null;
        }

        TreeNode(double operand) {
            this.operand = operand;
            this.left = this.right = null;
        }
    }
    public static double buildSyntaxTreeAndCompute(String rpnExpression) {
        Stack<TreeNode> stack = new Stack<>();
        String[] tokens = rpnExpression.split("\\s+");

        for (String token : tokens) {
            if (isNumeric(token)) {
                stack.push(new TreeNode(Double.parseDouble(token)));
            } else if (isOperator(token.charAt(0))) {
                TreeNode operand2 = stack.pop();
                TreeNode operand1 = stack.pop();
                TreeNode operatorNode = new TreeNode(token.charAt(0));

                operatorNode.left = operand1;
                operatorNode.right = operand2;

                stack.push(operatorNode);
            }
        }
        return evaluateSyntaxTree(stack.pop());
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double evaluateSyntaxTree(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Invalid syntax tree");
        }
        if (root.operator == 0) { // Check if it's an operand
            return root.operand;
        } else {
            double operand1 = evaluateSyntaxTree(root.left);
            double operand2 = evaluateSyntaxTree(root.right);
            return applyOperator(root.operator, operand1, operand2);
        }
    }
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    private static double applyOperator(char operator, double operand1, double operand2) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 != 0) {
                    return operand1 / operand2;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}