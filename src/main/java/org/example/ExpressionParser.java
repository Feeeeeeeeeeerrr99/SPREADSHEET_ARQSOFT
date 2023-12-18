package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parses and evaluates the given expression for a cell in a spreadsheet.
 */
public class ExpressionParser {
    private SpreadSheet SpreadSheet;
    private Cell currentCell;

    /**
     * Sets the current cell for which the expression is being parsed.
     *
     * @param cell The current cell.
     */
    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }
    /**
     * Constructor for the ExpressionParser.
     *
     * @param spreadSheet The spreadsheet associated with the parser.
     * @param current     The current cell being parsed.
     */
    public ExpressionParser(SpreadSheet spreadSheet, Cell current) {
        this.SpreadSheet = spreadSheet;
        this.currentCell=current;
    }
    /**
     * Evaluates the given expression and updates the current cell with the result.
     *
     * @param s           The spreadsheet.
     * @param expression  The expression to evaluate.
     * @param currentCell The current cell to update.
     * @return The result of the evaluation.
     * @throws Exception If an error occurs during evaluation.
     */
    public double evaluate(SpreadSheet s, String expression,Cell currentCell) throws Exception {
        try {
            this.SpreadSheet = s;
            String preprocessedFormula = preprocessFormula(expression);
            String rpn = infixToRPN(preprocessedFormula,s);
            double result = buildSyntaxTreeAndCompute(rpn);

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
        } catch (ArithmeticException e) {
            System.out.println("Error detected: Equation divided by zero ");
            return Double.NaN;
        } catch (IllegalArgumentException e) {
            System.out.println("Error detected: Invalid formula syntax");
            return Double.NaN;
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return Double.NaN;  // Return some default value for other exceptions
        }
    }
    /**
     * Converts an infix expression to Reverse Polish Notation (RPN).
     *
     * @param infixExpression The infix expression to convert.
     * @param spreadSheet    The spreadsheet associated with the expression.
     * @return The RPN representation of the infix expression.
     */
    public String infixToRPN(String infixExpression, SpreadSheet spreadSheet) {
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
                    if (cell==null){String cellContent="0";
                        output.append(cellContent).append(" ");}
                    else{
                        String cellContent = Double.toString(cell.getNumericValue());
                        output.append(cellContent).append(" ");}

                } else {
                    // Not a cell reference, append as is
                    output.append(operand).append(" ");
                }
            } else if (isOperator(c)) {
                handleOperator(c, operatorStack, output);
                output.append(" "); // Add space after operator
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
        String solution=output.toString().trim();
        return solution.replaceAll("(?<=\\S)(?=[+\\-*/])|(?<=[+\\-*/])(?=\\S)", " ");

    }
    /**
     * Handles the closing parenthesis during infix to RPN conversion.
     *
     * @param operatorStack The operator stack.
     * @param output        The output StringBuilder for RPN.
     */
    private void handleClosingParenthesis(Stack<Character> operatorStack, StringBuilder output) {
        while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
            output.append(operatorStack.pop()).append(" ");
        }
        if (!operatorStack.isEmpty()) {
            operatorStack.pop(); // Pop the '('
        }
    }
    /**
     * Preprocesses the formula by identifying and replacing basic operations.
     *
     * @param formula The original formula.
     * @return The processed formula with basic operations replaced by their computed results.
     */
    public String preprocessFormula(String formula) {
        // Identify and replace basic operations in the formula
        String processedFormula = formula;
        // Define a pattern for identifying basic operations like SUMA(), PROMEDIO(), MIN(), and MAX()
        Pattern pattern = Pattern.compile("(SUMA|PROMEDIO|MIN|MAX)\\(([^)]+)\\)");

        Matcher matcher = pattern.matcher(formula);
        while (matcher.find()) {
            String operation = matcher.group(1);
            String arguments2=extractContentInOutermostParentheses(processedFormula);
            String innerContent = arguments2.substring(1, arguments2.length() - 1);
            String[] elements2 = splitOutsideParentheses2(innerContent);
            for (int i = 0; i < elements2.length; i++) {
                String element = elements2[i];

                String pattern2 = "^[A-Za-z]+\\d+";
                Pattern regex = Pattern.compile(pattern2);
                Matcher matcher2 = regex.matcher(element);
                if (matcher2.matches()) {
                    if (org.example.SpreadSheet.isValidCellReference(element)) {
                        // Replace cell reference with its actual value
                        Cell cell = org.example.SpreadSheet.getCellByReference(element);
                        assert cell != null;
                        elements2[i] = Double.toString(cell.getNumericValue());
                    }
                } else {
                    if (element.contains("SUMA") || element.contains("PROMEDIO") || element.contains("MAX") || element.contains("MIN")) {
                        FormulaCell formulaCell = new FormulaCell("="+ element);
                        double result = formulaCell.evaluate(org.example.SpreadSheet.getCellMatrix());
                        elements2[i]=String.valueOf(result);
                    }
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
    /**
     * Extracts content within the outermost parentheses of an expression.
     *
     * @param expression The expression containing parentheses.
     * @return The content within the outermost parentheses.
     */
    public static String extractContentInOutermostParentheses(String expression) {
        int nestingLevel = 0;
        StringBuilder result = new StringBuilder();
        int outermostStart = -1; // Track the start index of the outermost parentheses

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                if (nestingLevel == 0) {
                    outermostStart = i;
                }
                nestingLevel++;
            } else if (c == ')') {
                nestingLevel--;
                if (nestingLevel == 1) { //ESTO ANTES ESTABA EN 0
                    //nothing
                } else if (nestingLevel < 0) {
                    // Handle case where there is a second parenthesis without a corresponding opening parenthesis
                    throw new IllegalArgumentException("Invalid parentheses in expression");
                }else if (nestingLevel==0){
                    String outermostContent = expression.substring(outermostStart, i + 1);
                    result.append(handleContent(outermostContent));
                    outermostStart = -1; // Reset for the next outermost set of parentheses
                    break;
                }
            }
        }

        if (nestingLevel > 0) {
            // Handle case where there is a second opening parenthesis without a corresponding closing parenthesis
            throw new IllegalArgumentException("Invalid parentheses in expression");
        }

        return result.toString();
    }
    /**
     * Handles the content within parentheses, replacing cell references with actual values.
     *
     * @param content The content within parentheses.
     * @return The processed content with cell references replaced.
     */
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
    /**
     * Handles ranges within parentheses.
     *
     * @param range The range within parentheses.
     * @return The processed range.
     */
    private static String handleRange(String range) {
        // Implement logic to handle range, e.g., convert "A1:A3" to "A1 A2 A3"
        // For simplicity, this example just returns the original range
        return range;
    }

    /**
     * Checks if the given string contains a semicolon.
     *
     * @param s The input string.
     * @return True if the string contains a semicolon, false otherwise.
     */
    private static boolean containsSemicolon(String s) {
        return s.contains(";");
    }
    /**
     * Splits the input string outside parentheses based on semicolons.
     *
     * @param input The input string to split.
     * @return An array of substrings separated by semicolons outside parentheses.
     */
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
    /**
     * Checks if a character is an operand (letter, digit, or dot).
     *
     * @param c The character to check.
     * @return True if the character is an operand, false otherwise.
     */
    private boolean isOperand(char c) {
        return Character.isLetterOrDigit(c) || c == '.';
    }
    /**
     * Checks if a string represents a numeric value.
     *
     * @param str The string to check.
     * @return True if the string represents a numeric value, false otherwise.
     */
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str.replace(",", ".")); // Replace comma with dot for decimal parsing
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Determines the precedence of an operator.
     *
     * @param operator The operator character.
     * @return The precedence level of the operator.
     */
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
    /**
     * Handles the conversion of infix expression to RPN for an operator.
     *
     * @param operator      The operator character.
     * @param operatorStack The operator stack.
     * @param output        The output StringBuilder for RPN.
     */
    private void handleOperator(char operator, Stack<Character> operatorStack, StringBuilder output) {
        while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
            output.append(operatorStack.pop());
        }
        operatorStack.push(operator);
    }
    /**
     * Represents a node in the syntax tree, which can be either an operator or an operand.
     */
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
    /**
     * Builds the syntax tree from the RPN expression and computes the result.
     *
     * @param rpnExpression The RPN expression.
     * @return The result of the expression evaluation.
     */
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
    /**
     * Recursively evaluates the syntax tree to compute the result.
     *
     * @param root The root of the syntax tree.
     * @return The result of the expression evaluation.
     */
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
    /**
     * Checks if a character is an operator.
     *
     * @param c The character to check.
     * @return True if the character is an operator, false otherwise.
     */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    /**
     * Applies the specified operator to two operands.
     *
     * @param operator The operator character.
     * @param operand1 The first operand.
     * @param operand2 The second operand.
     * @return The result of applying the operator to the operands.
     */
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