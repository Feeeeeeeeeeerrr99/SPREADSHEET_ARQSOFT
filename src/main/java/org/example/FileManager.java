package org.example;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.List;

import java.io.IOException;
import java.io.FileReader;


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

    static String[][] readCSV(String filePath) {
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
                        String cellValue = Double.toString(cell.getNumericValue());
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

    public static boolean compareCSVFiles(String filePath1, String filePath2) throws IOException {
        try (CSVReader reader1 = new CSVReader(new FileReader(filePath1));
             CSVReader reader2 = new CSVReader(new FileReader(filePath2))) {

            String[] line1, line2;

            do {
                line1 = reader1.readNext();
                line2 = reader2.readNext();

                if (!compareArrays(line1, line2)) {
                    return false;
                }

            } while (line1 != null && line2 != null);

            return line1 == null && line2 == null;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean compareArrays(String[] array1, String[] array2) {
        if (array1 == null || array2 == null) {
            return array1 == array2;
        }

        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (!array1[i].equals(array2[i])) {
                return false;
            }
        }

        return true;
    }
}
