package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import static org.example.FileManager.readCSV;
import java.io.IOException;

/**
 * The Main class represents the main entry point for the spreadsheet application.
 * It provides a user interface for interacting with spreadsheets.
 */
public class Main {

    private FileManager FM = new FileManager();
    SpreadSheet_Manager manager = new SpreadSheet_Manager();
    DependencyManager dependencyManager = new DependencyManager();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean end = false;

    /**
     * Main method to start the program.
     *
     * @param args Command-line arguments (not used in this application).
     * @throws Exception If an exception occurs during program execution.
     */
    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.run();
    }

    /**
     * Default constructor for the Main class.
     */
    public Main() {
    }

    /**
     * Runs the main program loop.
     */
    public void run() {
        boolean end = false;
        while (!end) {
            try {
                menu();
            } catch (IOException e) {
                System.out.println("Error reading input. Please try again.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the main menu and handles user input.
     *
     * @throws Exception If an exception occurs during menu execution.
     */
    public void menu() throws Exception {
        System.out.println("SPREADSHEET UI");
        System.out.println("------------------");
        System.out.println("1. Load existing SpreadSheet");
        System.out.println("2. Create new SpreadSheet");
        System.out.println("3. Save the actual SpreadSheet");
        System.out.println("------------------------------------");
        System.out.println("4. Edit SpreadSheet");
        System.out.println("5. Print existing SpreadSheet");
        System.out.println("9. TEST: Perform a pre-established formula test");
        System.out.println("0. Exit");
        String str = "";
        try {
            str = reader.readLine();
        } catch (Exception e) {
            System.out.println("Error reading the line");
        }
        switch (str) {
            case "1":
                loadSpreadSheet();
                break;
            case "2":
                createSpreadSheet();
                break;
            case "3":
                SaveSpreadSheet();
                break;
            case "4":
                EditSpreadSheet();
                break;
            case "5":
                manager.getSpreadSheet(0).printSpreadsheet();
                break;
            case "9":
                runTest();
                break;
            case "0":
                end = true;
                break;
            default:
        }
    }

    /**
     * Loads an existing spreadsheet.
     */
    public void loadSpreadSheet() {
        SpreadSheet ls = FileManager.createSpreadsheet("TemplateSpreadSheet");
        manager.addSpreadSheet(ls);
        ls.printSpreadsheet();
    }

    /**
     * Creates a new spreadsheet based on user input.
     */
    public void createSpreadSheet() {
        System.out.println("Provide the rows of the new SpreadSheet:");
        int numberOfRows = readNumericInput();

        System.out.println("Provide the columns of the new SpreadSheet:");
        int numberOfColumns = readNumericInput();

        SpreadSheet spreadsheet = new SpreadSheet(numberOfRows, numberOfColumns);
        manager.addSpreadSheet(spreadsheet);
        spreadsheet.printSpreadsheet();
    }

    /**
     * Reads a numeric input from the user.
     *
     * @return The numeric input entered by the user.
     */
    private int readNumericInput() {
        while (true) {
            try {
                String input = reader.readLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric value.");
            } catch (IOException e) {
                System.out.println("Error reading input. Please try again.");
            }
        }
    }

    /**
     * Saves the current spreadsheet to a CSV file.
     */
    public void SaveSpreadSheet() {
        SpreadSheet retrievedSpreadsheet = manager.getSpreadSheet(0);
        FM.exportToCSV(retrievedSpreadsheet, "TemplateSpreadSheet");
    }

    /**
     * Edits the current spreadsheet based on user input.
     */
    public void EditSpreadSheet() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                SpreadSheet spreadSheet = manager.getSpreadSheet(0);
                SpreadSheet.setDependencyManager(dependencyManager);
                System.out.println("Enter cell reference (e.g., A1) or EXIT to close EDITOR):");
                String command = scanner.nextLine().toUpperCase();
                Cell currentCell;
                if (command.equals("EXIT")) {
                    // Exit the editor
                    break;
                } else {
                    System.out.println("Enter formula (e.g., =3*SUM(A1:B2)):");
                    String formula = scanner.nextLine();
                    spreadSheet.setCellreference(command, formula);
                    currentCell = SpreadSheet.getCellByReference(command);
                    if (formula.startsWith("=")) {
                        ExpressionParser parser = new ExpressionParser(spreadSheet, currentCell);
                        String formulaWithoutEquals = formula.substring(1);
                        try {
                            if (currentCell != null) {
                                parser.setCurrentCell(currentCell);
                            }
                            assert currentCell != null;
                            double result = parser.evaluate(spreadSheet, formulaWithoutEquals, currentCell);
                            SpreadSheet.setValueByCellReference(command, String.valueOf(result), formula, currentCell);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    } else {
                        SpreadSheet.setValueByCellReference(command, formula, formula, currentCell);
                    }
                }
                spreadSheet.computeValues();
                spreadSheet.setDataFromCell();
                spreadSheet.printSpreadsheet();
            } catch (IOException e) {
                System.out.println("Error reading input. Please try again.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: Introduce valid cell reference");
            }
        }
    }

    /**
     * Runs a pre-established formula test.
     *
     * @throws Exception If an exception occurs during the test.
     */
    public void runTest() throws Exception {
        String[][] commands = readCSV("commands.txt");
        SpreadSheet SpreadSheet = new SpreadSheet(Integer.parseInt("5"), Integer.parseInt("5"));
        manager.addSpreadSheet(SpreadSheet);
        SpreadSheet spreadSheet = manager.getSpreadSheet(0);
        org.example.SpreadSheet.setDependencyManager(dependencyManager);
        spreadSheet.setCellreference("A1", "1");
        spreadSheet.setCellreference("A2", "2");
        spreadSheet.setCellreference("B1", "3");
        spreadSheet.setCellreference("B2", "4");
        // Anem a probar formules
        for (int i = 0; i < commands.length; i++) {
            String[] positions = {"C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5"};
            spreadSheet.setCellreference(positions[i], "=" + commands[i][0]);
            Cell currentCell = org.example.SpreadSheet.getCellByReference(positions[i]);
            ExpressionParser parser = new ExpressionParser(spreadSheet, currentCell);
            String formulaWithoutEquals = commands[i][0];
            try {
                if (currentCell != null) {
                    parser.setCurrentCell(currentCell);
                }
                assert currentCell != null;
                double result = parser.evaluate(spreadSheet, formulaWithoutEquals, currentCell);
                org.example.SpreadSheet.setValueByCellReference(positions[i], String.valueOf(result), "=" + formulaWithoutEquals, currentCell);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        spreadSheet.computeValues();
        spreadSheet.setDataFromCell();
        spreadSheet.printSpreadsheet();
        SpreadSheet retrievedSpreadsheet = manager.getSpreadSheet(0);
        FM.exportToCSV(retrievedSpreadsheet, "SpreadSheetTestResults");

        if (FileManager.compareCSVFiles("SpreadSheetTestResults", "SpreadSheetTestResults_OK")) {
            System.out.println("SUCCESSFUL TEST!");
        } else {
            System.out.println("TEST FAILED!");
        }
    }
}
