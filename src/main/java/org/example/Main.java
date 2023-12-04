package org.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import javax.swing.text.Position;
import java.io.*;
import java.util.List;

import static org.example.FileManager.readCSV;


public class Main {
    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.run();
    }

    public Main() {
    }

    private FileManager FM = new FileManager();
    SpreadSheet_Manager manager = new SpreadSheet_Manager();
    DependencyManager dependencyManager = new DependencyManager();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean end = false;

    public void run() throws Exception {
        boolean end = false;
        while (!end) {
            menu();
        }
    }

    public void menu() throws Exception {
        System.out.println("SPREADSHEET UI");
        System.out.println("----");
        System.out.println("1. Load existing SpreadSheet");
        System.out.println("2. Create new SpreadSheet");
        System.out.println("3. Save the existing SpreadSheet");
        System.out.println("------------------------------------");
        System.out.println("4. Edit SpreadSheet");
        System.out.println("5. Print existing SpreadSheet");
        System.out.println("9. TEST: Perform a pre-stablished test");
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

    public void loadSpreadSheet() {
        SpreadSheet ls = FileManager.createSpreadsheet("extraccion");
        manager.addSpreadSheet(ls);
        // Print the spreadsheet
        ls.printSpreadsheet();
    }

    public void createSpreadSheet() {
        System.out.println("Provide the rows of the new SpreadSheet");
        String numberofrows = "";
        try {
            numberofrows = reader.readLine();
        } catch (Exception e) {
            System.out.println("Error reading line");
        }
        System.out.println("Provide the columns of the new SpreadSheet");
        String numberofcolumns = "";
        try {
            numberofcolumns = reader.readLine();
        } catch (Exception e) {
            System.out.println("Error reading line");
        }
        SpreadSheet spreadsheet = new SpreadSheet(Integer.parseInt(numberofrows), Integer.parseInt(numberofcolumns));
        manager.addSpreadSheet(spreadsheet);
        // Print the spreadsheet
        spreadsheet.printSpreadsheet();
    }

    public void SaveSpreadSheet() {
        SpreadSheet retrievedSpreadsheet = manager.getSpreadSheet(0);
        FM.exportToCSV(retrievedSpreadsheet, "extraccion");
    }

    public void EditSpreadSheet() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            SpreadSheet spreadSheet = manager.getSpreadSheet(0);
            SpreadSheet.setDependencyManager(dependencyManager);
            System.out.println("Enter cell reference(e.g., A1) or EXIT to close EDITOR):");
            String command = scanner.nextLine().toUpperCase();
            Cell currentCell;
            if (command.equals("EXIT")) {
                // Exit the program
                break;
            } else {
                System.out.println("Enter formula (e.g., =3*SUM(A1:B2)):");
                String formula = scanner.nextLine();
                spreadSheet.setCellreference(command, formula);
                currentCell = SpreadSheet.getCellByReference(command);
                if (formula.startsWith("=")) {
                    ExpressionParser parser = new ExpressionParser(spreadSheet, dependencyManager, currentCell);
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
        }
    }

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
        //Anem a probar formules
        for (int i = 0; i < commands.length; i++) {
            String[] postions = {"C1", "C2", "C3", "C4", "C5", "D1", "D2", "D3", "D4", "D5"};
            spreadSheet.setCellreference(postions[i], "=" + commands[i][0]);
            Cell currentCell = org.example.SpreadSheet.getCellByReference(postions[i]);
            ExpressionParser parser = new ExpressionParser(spreadSheet, dependencyManager, currentCell);
            String formulaWithoutEquals = commands[i][0];
            try {
                if (currentCell != null) {
                    parser.setCurrentCell(currentCell);
                }
                assert currentCell != null;
                double result = parser.evaluate(spreadSheet, formulaWithoutEquals, currentCell);
                org.example.SpreadSheet.setValueByCellReference(postions[i], String.valueOf(result), "=" + formulaWithoutEquals, currentCell);
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