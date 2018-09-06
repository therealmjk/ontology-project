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
public class OntologyMatch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //validate args
        /*if (args.length < 6) {
            System.out.print("Six arguments are needed.");
            System.out.println("Please provide 'null' where not needed");
            System.exit(0);
        }

        String filename = args[0];
        String column = args[1];
        String suggestion = ("null".equals(args[2])) ? "" : args[2];
        String slice = ("null".equals(args[3])) ? "" : args[3];
        String seperator = ("null".equals(args[4])) ? "," : args[4];
        String sheet = ("null".equals(args[5])) ? "0" : args[5];
         */
        //running from IDE (COMMENT ALL ABOVE)
        String filename = "data3.xlsx";
//        String filename = "csv1.csv";
        String column = "PROPERTY";
        String suggestion = "PO,TO,PATO,RO,CO_125,CO_325,CO_333,CO_345,CO_348,"
                + "CO_334,CO_347,CO_338,CO_335,CO_340,CO_715,CO_337,CO_339,"
                + "CO_322,CO_020,CO_346,CO_350,CO_327,CO_341,CO_330,CO_320,"
                + "CO_324,CO_336,CO_331,CO_121,CO_321,CO_357,CO_343,NCBITAXON";

        String slice = "agrold";
        String seperator = ",";
        String sheet = "0";

        //convert sheet to integer
        int sheetNumber = 0;
        try {
            sheetNumber = Integer.parseInt(sheet);
        } catch (NumberFormatException e) {
        }

        Ontology ontology = new Ontology();
        try {
            ontology.addOntology(filename, column, suggestion, slice, seperator, sheetNumber);
        } catch (Exception ex) {
        }

    }

}
