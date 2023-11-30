package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {
    private Stack<Operator> operators;
    private Stack<Double> values;
    private SpreadSheet SpreadSheet;
    private Cell currentCell;
    private DependencyManager dependencyManager;

    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }
    public ExpressionParser() {
        this.operators = new Stack<>();
        this.values = new Stack<>();
    }
    public ExpressionParser(SpreadSheet spreadSheet, DependencyManager dependencyManager,Cell current) {
        this.SpreadSheet = spreadSheet;
        this.dependencyManager = dependencyManager;
        this.currentCell=current;
    }

    public double evaluate(SpreadSheet s, String expression,Cell currentCell) throws Exception {
        this.SpreadSheet = s;
        String preprocessedFormula = preprocessFormula(expression);
        String rpn = infixToRPN(preprocessedFormula,s);
        double result=buildSyntaxTreeAndCompute(rpn);
        // Check for circular dependencies
        if (currentCell.isVisited()) {
            System.out.println("Circular dependency detected involving cell: " + currentCell.getCellName());
            // Take appropriate action to resolve circular dependency
            // For example, break the loop, set a default value, etc.
            currentCell.setVisited(false);  // Reset the visited flag
            return Double.NaN;  // Return some default value
        }
        // Update the current cell with the new value
        currentCell.setVisited(true);
        currentCell.updateValue(Double.toString(result));
        currentCell.notifyDependents();
        currentCell.setVisited(false); // Reset visited status
        return result;
    }
    public String infixToRPN(String infixExpression, SpreadSheet spreadSheet) throws CircularDependencyException {
        StringBuilder output = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < infixExpression.length(); i++) {
            char c = infixExpression.charAt(i);

            if (isOperand(c)) {
                StringBuilder operand = new StringBuilder();
                while (i < infixExpression.length() && (Character.isLetterOrDigit(infixExpression.charAt(i)) || infixExpression.charAt(i) == '.')) {
                    operand.append(infixExpression.charAt(i));
                    i++;
                }
                i--; // Move back one position to handle the non-operand character in the next iteration

                // Check if the operand is a cell reference
                String cellReference = operand.toString();
                if (org.example.SpreadSheet.isValidCellReference(cellReference)) {
                    // Replace cell reference with its actual value
                    Cell cell = org.example.SpreadSheet.getCellByReference(cellReference);
                    currentCell.addDependent(cell);

                    assert cell != null;
                    String cellContent= Double.toString(cell.getNumericValue());

                    output.append(cellContent).append(" ");
                } else {
                    // Not a cell reference, append as is
                    output.append(operand.toString()).append(" ");
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
            String arguments2=extractContentInOutermostParentheses(formula);
            String innerContent = arguments2.substring(1, arguments2.length() - 1);
            String[] elements2 = splitOutsideParentheses2(innerContent);
            for (int i = 0; i < elements2.length; i++) {
                String element = elements2[i];
                if (element.contains("SUMA") || element.contains("PROMEDIO") || element.contains("MAX") || element.contains("MIN")) {
                    FormulaCell formulaCell = new FormulaCell("="+ element);
                    double result = formulaCell.evaluate(org.example.SpreadSheet.getCellMatrix());
                    elements2[i]=String.valueOf(result);
                }
            }
            String result3 = String.join(";", elements2);
            FormulaCell formulaCell = new FormulaCell('='+operation +'('+result3+')');
            double result = formulaCell.evaluate(org.example.SpreadSheet.getCellMatrix());
            int result2 = (int) result;

            // Replace the matched part in the formula with the computed result as a string
            processedFormula = processedFormula.replace(operation+arguments2, String.valueOf(result2));
        }

        return processedFormula;
    }

    public static String extractContentInOutermostParentheses(String expression) {
        Stack<Integer> stack = new Stack<>();
        StringBuilder result = new StringBuilder();
        int outermostStart = -1; // Track the start index of the outermost parentheses

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                if (stack.isEmpty()) {
                    outermostStart = i;
                }
                stack.push(i);
            } else if (c == ')') {
                if (!stack.isEmpty()) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        String outermostContent = expression.substring(outermostStart, i + 1);
                        result.append(handleContent(outermostContent));
                        outermostStart = -1; // Reset for the next outermost set of parentheses
                    }
                }
            }
        }

        return result.toString();
    }

    private static String handleContent(String content) {
        if (containsSemicolon(content)) {
            // Split content into parts based on semicolon
            String[] parts = content.split(";");
            StringBuilder result = new StringBuilder();

            // Process each part
            for (String part : parts) {
                if (part.contains(":")) {
                    // If the part contains a colon, treat it as a range and process accordingly
                    result.append(handleRange(part)).append(";");
                } else {
                    // If no colon, append the part as is
                    result.append(part).append(";");
                }
            }

            // Remove the trailing semicolon and return the result
            return result.deleteCharAt(result.length() - 1).toString();
        } else {
            // If no semicolon, return the content as is
            return content;
        }
    }

    private static String handleRange(String range) {
        // Implement logic to handle range, e.g., convert "A1:A3" to "A1 A2 A3"
        // For simplicity, this example just returns the original range
        return range;
    }

    // Helper function to check if a string contains a semicolon
    private static boolean containsSemicolon(String s) {
        return s.contains(";");
    }

    private static String[] splitOutsideParentheses2(String input) {
        // Initialize variables
        int depth = 0;
        int start = 0;
        List<String> result = new ArrayList<>();

        // Iterate through the characters of the input string
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Check if the character is a semicolon outside of parentheses
            if (c == ';' && depth == 0) {
                // Add the substring from the last start index to the current index
                result.add(input.substring(start, i));
                // Update the start index for the next substring
                start = i + 1;
            } else if (c == '(') {
                // Increase the depth when an opening parenthesis is encountered
                depth++;
            } else if (c == ')') {
                // Decrease the depth when a closing parenthesis is encountered
                depth = Math.max(0, depth - 1);
            }
        }

        // Add the remaining substring after the last semicolon
        result.add(input.substring(start));

        // Convert the list to an array
        return result.toArray(new String[0]);
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
        return Character.isLetterOrDigit(c) || c == '.';
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str.replace(",", ".")); // Replace comma with dot for decimal parsing
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
        Double operand; // Add an operand field as Double to distinguish from operators
        TreeNode left, right;

        TreeNode(char operator) {
            this.operator = operator;
            this.left = this.right = null;
        }

        TreeNode(Double operand) {
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

    private static double evaluateSyntaxTree(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Invalid syntax tree");
        }
        if (root.operator != 0) { // Check if it's an operator
            double operand1 = evaluateSyntaxTree(root.left);
            double operand2 = evaluateSyntaxTree(root.right);
            return applyOperator(root.operator, operand1, operand2);
        } else if (root.operand != null) { // Check if it's an operand
            return root.operand;
        } else {
            throw new IllegalArgumentException("Invalid syntax tree node");
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