/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologymatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Muhd Jibril Kazim
 */
public class Utility {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    public static int colIndex = 0;
    public static XSSFWorkbook workbook = null;
    public static XSSFSheet sheet = null;
    public static Map<String, String> ontMap = null;
    public static Map<String, String> uriMap = null;
    public static Map<String, String> matchMap = null;
    public static Map<String, String> qOntMap = null;
    public static Map<String, String> eOntMap = null;

    public static int getIndexXlsxFile(String filename, String columunName, int sheetNumber) {

        try {

            InputStream XlsxFileToRead = new FileInputStream(filename);
            workbook = new XSSFWorkbook(XlsxFileToRead);
            sheet = workbook.getSheetAt(sheetNumber);
            Row firstRow = sheet.getRow(0);
            Iterator firstRowCells = firstRow.cellIterator();

            while (firstRowCells.hasNext()) {
                Cell cell = (XSSFCell) firstRowCells.next();

                if (cell.getStringCellValue().equals(columunName)) {
                    return cell.getColumnIndex();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OntologyMatch.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public static int getIndexCsvFile(String filename, String columunName, String seperator) {
        BufferedReader fileReader;

        try {
            fileReader = new BufferedReader(new FileReader(filename));
            String line = fileReader.readLine();

            String[] tokens = line.split(seperator);

            if (tokens.length > 0) {
                for (int i = 0; i < tokens.length; i++) {
                    //remove quotes
                    tokens[i] = tokens[i].replace("\"", "");

                    if (tokens[i].equals(columunName)) {
                        return i;
                    }
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(OntologyMatch.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public static List readAndGetOntologyXlxs(String filename, String colName, int sheetNumber) throws Exception {

        List valueList = new ArrayList();

        //read excel file
        InputStream XlsxFileToRead = new FileInputStream(filename);
        workbook = new XSSFWorkbook(XlsxFileToRead);
        sheet = workbook.getSheetAt(sheetNumber);

        //get ontology-names from a specific column 
        for (Row row : sheet) {
            //get all cells in a specific column
            Cell cell = row.getCell(colIndex);
            if (cell != null && cell.getCellType() == cell.CELL_TYPE_STRING) {
                //get cell value
                String cellValue = cell.getStringCellValue();

                //add to array list to store
                if (!valueList.contains(cellValue) && !cellValue.equals(colName) && validateEmpty(cellValue)) {
                    valueList.add(cellValue);
                }
            }
        }

        return valueList;
    }

    public static List readAndGetOntologyCsv(String filename, String colName) throws Exception {

        List valueList = new ArrayList();
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());

            if (line.size() > colIndex) {
                String s = line.get(colIndex);

                if (!valueList.contains(s) && !s.equals(colName) && validateEmpty(s)) {
                    valueList.add(s);
                }

            }
        }

        return valueList;
    }

    public static List readAndGetOntologyFile(String filename, String colName, String seperator) throws Exception {

        List valueList = new ArrayList();
        String line;
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        fileReader.readLine();

        while ((line = fileReader.readLine()) != null) {
            //Get all tokens available in line
            String[] tokens = line.split(seperator);

            if (tokens.length > 0) {
                for (int i = 0; i < tokens.length; i++) {

                    String token = tokens[i];

                    if (i == colIndex) {
                        //remove quotes
                        token = token.replace("\"", "");

                        if (!valueList.contains(token) && !token.equals(colName) && validateEmpty(token)) {
                            valueList.add(token);
                        }
                    }
                }

            }
        }

        return valueList;
    }

    public static List<String> parseLine(String line) {
        return parseLine(line, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String line, char separators) {
        return parseLine(line, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String line, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (line == null && line.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = line.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else //Fixed : allow "" in custom quote enclosed
                {
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                }
            } else if (ch == customQuote) {

                inQuotes = true;

                //Fixed : allow "" in empty quote enclosed
                if (chars[0] != '"' && customQuote == '\"') {
                    curVal.append('"');
                }

                //double quotes in column will hit this!
                if (startCollectChar) {
                    curVal.append('"');
                }

            } else if (ch == separators) {

                result.add(curVal.toString());

                curVal = new StringBuffer();
                startCollectChar = false;

            } else if (ch == '\r') {
                //ignore LF characters
                continue;
            } else if (ch == '\n') {
                //the end, break!
                break;
            } else {
                curVal.append(ch);
            }

        }

        result.add(curVal.toString());

        return result;
    }

    public static Boolean validateEmpty(String str) {
        if (str == null) {
            return false;
        }

        return !("null".equals(str) || "Null".equals(str) || "NULL".equals(str) || "".equals(str));
    }

    public static String getFileExtension(String filename) {
        //get extension
        String[] fileParts = filename.split(Pattern.quote("."));
        return fileParts[fileParts.length - 1];
    }

    public static void writeOntologyToXlsxFile(String colName, String outputFileName) throws Exception {

        //create new column name
        String newColName = colName + "_id";

        //add new column for ontologies
        for (Row row : sheet) {
            //get all cells in a specific column
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);

                if (cell != null) {
                    if (cell.getColumnIndex() == colIndex) {
                        //create 2 new cells
                        Cell newCell1 = row.createCell(colIndex + 1);
                        Cell newCell2 = row.createCell(colIndex + 2);
                        Cell newCell3 = row.createCell(colIndex + 3);

                        Cell newCell4 = row.createCell(colIndex + 4);
                        Cell newCell5 = row.createCell(colIndex + 5);
                        //get previous cell
                        Cell prevCell = row.getCell(colIndex);

                        if (prevCell != null && prevCell.getCellType() == prevCell.CELL_TYPE_STRING) {
                            //get previous cell value
                            String prevCellValue = prevCell.getStringCellValue();

                            //get value using previous as key from mapper
                            String ontologyValue = ontMap.get(prevCellValue);
                            String uriValue = uriMap.get(prevCellValue);
                            String matchValue = matchMap.get(prevCellValue);
                            String qOntologyValue = qOntMap.get(prevCellValue);
                            String eOntologyValue = eOntMap.get(prevCellValue);

                            newCell1.setCellValue(ontologyValue);
                            newCell2.setCellValue(uriValue);
                            newCell3.setCellValue(matchValue);

                            newCell4.setCellValue(qOntologyValue);
                            newCell5.setCellValue(eOntologyValue);

                            //set title of column
                            if (prevCellValue.equals(colName)) {
                                newCell1.setCellValue(newColName);
                                newCell2.setCellValue(newColName + "_uri");
                                newCell3.setCellValue(newColName + "_match");
                                newCell4.setCellValue("ont_quality");
                                newCell5.setCellValue("ont_entity");
                            }
                        }
                    }
                }
            }
        }

        //write data to new file
        FileOutputStream outputStream = new FileOutputStream(outputFileName);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public static void writeOntologyToFile(String filename, String colName, String outputFileName, String seperator) throws Exception {
        String line;
        String header;
        String quote = "";
        String newColName = colName + "_id";

        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        FileWriter fileWriter = new FileWriter(outputFileName);

        if ("csv".equals(Utility.getFileExtension(filename))) {
            quote = "\"";
        }

        //Write header
        header = fileReader.readLine();

        String[] headerTokens = header.split(seperator);
        if (headerTokens.length > 0) {
            for (int i = 0; i < headerTokens.length; i++) {
                //append
                fileWriter.append(headerTokens[i]);
                fileWriter.append(seperator);

                if (i == colIndex) {
                    fileWriter.append(quote + newColName + quote);
                    fileWriter.append(seperator);
                    fileWriter.append(quote + newColName + "_uri" + quote);
                    fileWriter.append(seperator);
                    fileWriter.append(quote + newColName + "_match" + quote);
                    fileWriter.append(seperator);
                    fileWriter.append(quote + "ont_quality" + quote);
                    fileWriter.append(seperator);
                    fileWriter.append(quote + "ont_entity" + quote);
                    fileWriter.append(seperator);
                }
            }
            //seperate by jump to next line
            fileWriter.append("\n");
        }

        //Write rest of file
        while ((line = fileReader.readLine()) != null) {
            //Get all tokens available in line
            String[] tokens = line.split(seperator);

            if (tokens.length > 0) {
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    //append
                    fileWriter.append(token);
                    fileWriter.append(seperator);

                    if (i == colIndex) {
                        //replace quotes
                        token = token.replace(quote, "");
                        //get value using previous as key from mapper
                        String ontologyValue = ontMap.get(token);
                        String uriValue = uriMap.get(token);
                        String matchValue = matchMap.get(token);

                        String qOntologyValue = qOntMap.get(token);
                        String eOntologyValue = eOntMap.get(token);
                        //append
                        fileWriter.append(quote + ontologyValue + quote);
                        fileWriter.append(seperator);
                        fileWriter.append(quote + uriValue + quote);
                        fileWriter.append(seperator);
                        fileWriter.append(quote + matchValue + quote);
                        fileWriter.append(seperator);
                        fileWriter.append(quote + qOntologyValue + quote);
                        fileWriter.append(seperator);
                        fileWriter.append(quote + eOntologyValue + quote);
                        fileWriter.append(seperator);
                    }
                }
                //seperate by line
                fileWriter.append("\n");
            }
        }

        //flush and close writer
        fileWriter.flush();
        fileWriter.close();
    }
}
