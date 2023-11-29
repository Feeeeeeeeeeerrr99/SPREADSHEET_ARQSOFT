package org.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.run();
    }

    public Main() {
    }

    private FileManager FM = new FileManager();
    SpreadSheet_Manager manager = new SpreadSheet_Manager();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean end = false;
    public void run() throws Exception {
        boolean end = false;
        while(!end) {
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
        System.out.println("0. Exit");
        String str="";
        try {
            str = reader.readLine();
        } catch(Exception e) {
            System.out.println("Error reading the line");
        }
        switch(str) {
            case "1": loadSpreadSheet(); break;
            case "2": createSpreadSheet(); break;
            case "3": SaveSpreadSheet(); break;
            case "4": EditSpreadSheet(); break;
            case "5": manager.getSpreadSheet(0).printSpreadsheet(); break;
            case "0": end=true; break;
            default:
        }

        //TEST1
    }
    public void loadSpreadSheet(){
        SpreadSheet ls = FileManager.createSpreadsheet("extraccion");
        manager.addSpreadSheet(ls);
        // Print the spreadsheet
        ls.printSpreadsheet();
    }
    public void createSpreadSheet(){
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
        SpreadSheet spreadsheet = new SpreadSheet( Integer.parseInt(numberofrows),  Integer.parseInt(numberofcolumns));
        manager.addSpreadSheet(spreadsheet);
        // Print the spreadsheet
        spreadsheet.printSpreadsheet();
    }

    public void SaveSpreadSheet(){
        SpreadSheet retrievedSpreadsheet = manager.getSpreadSheet(0);
        FM.exportToCSV(retrievedSpreadsheet, "extraccion");}

    public void EditSpreadSheet() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            SpreadSheet spreadSheet = manager.getSpreadSheet(0);
            System.out.println("Enter cell reference(e.g., A1) or EXIT to close EDITOR):");
            String command = scanner.nextLine().toUpperCase();
            if (command.equals("EXIT")) {
                // Exit the program
                break;
            } else {
                System.out.println("Enter formula (e.g., =3*SUM(A1:B2)):");
                String formula = scanner.nextLine();
                ExpressionParser parser = new ExpressionParser();
                Cell currentCell = SpreadSheet.getCellByReference(command);
                if (currentCell != null) {
                    parser.setCurrentCell(currentCell);
                }
                if (formula.startsWith("=")) {
                    String formulaWithoutEquals = formula.substring(1);
                    //SpreadSheet.setValueByCellReference(command,formula,formula, currentCell);
                    try {
                        double result = parser.evaluate(spreadSheet, formulaWithoutEquals);
                        SpreadSheet.setValueByCellReference(command, String.valueOf(result),formula, currentCell);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                } else {
                    SpreadSheet.setValueByCellReference(command,formula,formula, currentCell);
                }
            }
            spreadSheet.computeValues();
            spreadSheet.printSpreadsheet();
        }
    }
}