/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologymatch;

/**
 *
 * @author Muhd Jibril Kazim
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteFile {

    private static XSSFWorkbook workbook = null;
    private static XSSFSheet sheet = null;
    public static int colIndex = 0;
    public static Map<String, String> ontMap = null;
    public static Map<String, String> uriMap = null;
    public static Map<String, String> matchMap = null;
    public static Map<String, String> qOntMap = null;
    public static Map<String, String> eOntMap = null;

    private static Boolean validateEmpty(String str) {
        if (str == null) {
            return false;
        }

        return !("null".equals(str) || "Null".equals(str) || "NULL".equals(str) || "".equals(str));
    }

    public static List readAndGetOntologyNamesFromFile(String filename, String colName) throws Exception {
        //read excel file
        InputStream XlsxFileToRead = new FileInputStream(filename);
        workbook = new XSSFWorkbook(XlsxFileToRead);
        sheet = workbook.getSheetAt(0);

        //get clumn index by column name
        colIndex = getComulnIndex(filename, colName);
        List valueList = new ArrayList();

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

    private static int getComulnIndex(String Filename, String columunName) {

        try {
            InputStream XlsxFileToRead = new FileInputStream(Filename);
            workbook = new XSSFWorkbook(XlsxFileToRead);
            sheet = workbook.getSheetAt(0);
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

    public static void writeOntologyToFile(String colName, String outputFileName) throws Exception {

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
                        Cell prevCell = row.getCell(ReadWriteFile.colIndex);

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
}
