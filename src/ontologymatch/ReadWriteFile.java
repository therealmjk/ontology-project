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

public class ReadWriteFile {

    public static List readAndGetOntologyNamesFromFile(String filename, String colName, String seperator, int sheetNumber) throws Exception {
        //get clumn index by column name
        Utility.colIndex = getComulnIndex(filename, colName, seperator, sheetNumber);

        //get extension
        String ext = Utility.getFileExtension(filename);

        switch (ext) {
            case "xlsx":
                return Utility.readAndGetOntologyXlxs(filename, colName, sheetNumber);
            case "csv":
                return Utility.readAndGetOntologyCsv(filename, colName);
            default:
                return Utility.readAndGetOntologyFile(filename, colName, seperator);
        }
    }

    private static int getComulnIndex(String filename, String columunName, String seperator, int sheetNumber) {
        //get extension
        String ext = Utility.getFileExtension(filename);

        switch (ext) {
            case "xlsx":
                return Utility.getIndexXlsxFile(filename, columunName, sheetNumber);
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
                break;
            case "csv":
            default:
                Utility.writeOntologyToFile(filename, colName, outputFileName, seperator);
        }
    }
}
