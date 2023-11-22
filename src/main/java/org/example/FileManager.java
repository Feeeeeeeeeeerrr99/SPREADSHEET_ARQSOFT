package org.example;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;

import java.io.IOException;


public class FileManager {
    public static SpreadSheet createSpreadsheet(String filePath) {
        String[][] csvData = readCSV(filePath);

        if (csvData.length == 0 || csvData[0].length == 0) {
            throw new IllegalArgumentException("CSV file is empty");
        }
        int rows = csvData.length;
        int columns = csvData[0].length;
        SpreadSheet spreadsheet = new SpreadSheet(rows, columns);
        spreadsheet.setData(csvData);
        return spreadsheet;
    }

    private static String[][] readCSV(String filePath) {
        String[][] records = new String[0][0];

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> dataList = csvReader.readAll();
            records = dataList.toArray(new String[0][0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
        return records;
    }
    public void exportToCSV(SpreadSheet spreadsheet, String fileName) {
        String filePath = fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int rows = spreadsheet.getRows();
            int columns = spreadsheet.getColumns();

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    Cell cell = spreadsheet.getCell(row, col);
                    if (cell != null) {
                        String cellValue = cell.getData();
                        writer.write(cellValue + (col < columns - 1 ? "," : "\n"));
                    } else {
                        writer.write(col < columns - 1 ? "," : "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
