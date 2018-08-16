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

import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteFile {

    public static XSSFWorkbook workbook = null;
    public static XSSFSheet sheet = null;
    public static int colIndex = 0;
    public static Map<String, String> ontMap = null;
    public static Map<String, String> uriMap = null;
    public static Map<String, String> matchMap = null;
    public static Map<String, String> qOntMap = null;
    public static Map<String, String> eOntMap = null;

    public static List readAndGetOntologyNamesFromFile(String filename, String colName, String seperator) throws Exception {
        //get clumn index by column name
        colIndex = getComulnIndex(filename, colName, seperator);

        //get extension
        String ext = Utility.getFileExtension(filename);

        switch (ext) {
            case "xlsx":
                return Utility.readAndGetOntologyXlxs(filename, colName, colIndex);
            case "csv":
                return Utility.readAndGetOntologyCsv(filename, colName, colIndex);
            default:
                return Utility.readAndGetOntologyFile(filename, colName, colIndex, seperator);
        }
    }

    private static int getComulnIndex(String filename, String columunName, String seperator) {
        //get extension
        String ext = Utility.getFileExtension(filename);

        switch (ext) {
            case "xlsx":
                return Utility.getIndexXlsxFile(filename, columunName);
            case "csv":
            default:
                return Utility.getIndexCsvFile(filename, columunName, seperator);
        }
    }

    public static void writeOntologyToFile(String filename, String colName, String outputFileName, String seperator) throws Exception {

        //get extension
        String ext = Utility.getFileExtension(filename);

        switch (ext) {
            case "xlsx":
                Utility.writeOntologyToXlsxFile(colName, outputFileName);
            case "csv":
            default:
                Utility.writeOntologyToFile(filename, colName, outputFileName, seperator);
        }
    }
}
