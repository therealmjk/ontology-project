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

        String filename = args[0];
        String column = args[1];
       
        
        String suggestion = ("null".equals(args[2])) ? "" : args[2];
        String slice = ("null".equals(args[3])) ? "" : args[3];
        

        Ontology ontology = new Ontology();

        try {
            ontology.addOntology(filename, column, suggestion, slice);

        } catch (Exception ex) {
            Logger.getLogger(OntologyMatch.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
