/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologymatch;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Muhd Jibril Kazim
 */
public class OntologyMatch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        String filename = args[0];
//        String column = args[1];
//        String suggestion = ("null".equals(args[2])) ? "" : args[2];
//        String slice = ("null".equals(args[3])) ? "" : args[3];
//        String seperator = ("null".equals(args[4])) ? null : args[4];

        //running from IDE (COMMENT ALL ABOVE)
//        String filename = "data3.xlsx";
        String filename = "csv1.csv";
        String suggestion = "PO,TO,PATO,RO,CO_125,CO_325,CO_333,CO_345,CO_348,"
                + "CO_334,CO_347,CO_338,CO_335,CO_340,CO_715,CO_337,CO_339,"
                + "CO_322,CO_020,CO_346,CO_350,CO_327,CO_341,CO_330,CO_320,"
                + "CO_324,CO_336,CO_331,CO_121,CO_321,CO_357,CO_343,NCBITAXON";

        String slice = "agrold";
        String seperator = ",";

        Ontology ontology = new Ontology();

        try {
//            ontology.addOntology(filename, column, suggestion, slice);

            //running from IDE (COMMENT ABOVE LINE)
//            ontology.addOntology(filename, "gramene_trait", suggestion, slice, seperator);
//            ontology.addOntology(filename, "developmental_stage", suggestion, slice, seperator);
//            ontology.addOntology(filename, "plant_anatomy", suggestion, slice, seperator);
//            ontology.addOntology(filename, "description", suggestion, slice, seperator);
//            ontology.addOntology(filename, "objective", suggestion, slice, seperator);
//            ontology.addOntology(filename, "VARIABLE NAME", suggestion, slice, seperator);
//            ontology.addOntology(filename, column, suggestion, slice, seperator);
            ontology.addOntology(filename, "PROPERTY", suggestion, slice, seperator);

        } catch (Exception ex) {
            Logger.getLogger(OntologyMatch.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
