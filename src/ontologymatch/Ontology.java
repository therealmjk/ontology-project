/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologymatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Muhd Jibril Kazim
 */
public class Ontology {

    private final int SIZE_INDEX = 10;
    private final int INDEX_ONT = 0;
    private final int INDEX_URI = 1;
    private final int INDEX_MATCH = 2;
    private final int INDEX_Q_ONT = 3;
    private final int INDEX_Q_URI = 4;
    private final int INDEX_Q_MATCH = 5;
    private final int INDEX_E_ONT = 6;
    private final int INDEX_E_URI = 7;
    private final int INDEX_E_MATCH = 8;
    private final int MAX_NUM_IDS = 10;
    private final String QUALITY = "PATO";
    public String permutations = "";

    //url
    private final String url;
    private final String apiKey;

    public Ontology() {
//        this.url = "http://data.bioontology.org/";
//          this.apiKey = "8b5b7825-538d-40e0-9e9e-5ab9274a9aeb";
        this.url = "http://data.agroportal.lirmm.fr/";
        this.apiKey = "1de0a270-29c5-4dda-b043-7c3580628cd5";

//        this.url = "https://www.ebi.ac.uk/ols/api/select?q=leaf blast";
    }

    private String reverseWords(String sentence) {
        sentence = sentence.toLowerCase();
        List<String> sentences = new ArrayList<>();
        String prevWords = "";
        String nextWords = "";
        String finalSentence = "";

        if (sentence.contains(" and ")) {
            String[] words = sentence.toLowerCase().split(" ");

            for (int i = 0; i < words.length; i++) {
                if (!words[i].equals("and") && i + 1 != words.length && words[i + 1].equals("and")) {
                    for (int k = 0; k < i + 1; k++) {
                        prevWords += words[k] + " ";
                    }
                } else if (i != 0 && words[i].equals("and")) {
                    for (int j = i + 1; j < words.length; j++) {
                        nextWords += words[j] + " ";
                    }
                }
            }

            for (String s : nextWords.split(" ")) {
                sentences.add(prevWords + s);
            }

            for (String s : sentences) {
                finalSentence += s + " ";
            }

            return sentence + " " + finalSentence;
        }

        return sentence;
    }

    private String getURL(String urlToRead, String input) throws Exception {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            //post body
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            conn.disconnect();
            return result.toString();
        } catch (IOException e) {
            return "[]";
        }
    }

    private String[] getOntologyId(String text, String ontologyPrefix, String slice) throws Exception {
//        text = URLEncoder.encode(text, "UTF-8");
        ontologyPrefix = ontologyPrefix.toUpperCase();

        String apiUrl = this.url + "annotator?ontologies=" + ontologyPrefix
                + "&ncbo_slice=" + slice
                + "&longest_only=true"
                + "&apikey=" + this.apiKey;
        //post inputs
        String postInput = "{\"text\":\"" + text + "\"}";

        //use list to store ontology uri temporarily
        //so we can easily check for duplicates
        List<String> tempList = new ArrayList<>();

        List<String> eqOntolList = new ArrayList<>();
        List<String> eqUriList = new ArrayList<>();
        List<String> eqMatchList = new ArrayList<>();

        //used variables
        String[] returnArray = new String[SIZE_INDEX];
        String ontologies = "";
        String uri = "";
        String matches = "";

        String matchText = "";
        Boolean allMatch = true;
        //get result of get request
        String result = this.getURL(apiUrl, postInput);
        //covert to json
        JSONParser parser = new JSONParser();
        JSONArray json = (JSONArray) parser.parse(result);

        //loop through
        for (int i = 0; i < json.size(); i++) {
            String ontology;

            JSONObject obj = (JSONObject) json.get(i);
            JSONObject annotatedObj = (JSONObject) obj.get("annotatedClass");
            JSONArray annotationArray = (JSONArray) obj.get("annotations");
            //get the uri
            String idUrl = annotatedObj.get("@id").toString();
            //split the id url
            String parts[] = idUrl.split("/");
            //take the id of the ontology
            ontology = parts[parts.length - 1];

            //check gotten ontology
            if (ontology.contains("#")) {
                //ex. flora-phenotype-ontology.owl#FLOPO:0000583
                //split again
                parts = idUrl.split("#");
                //take ontolgoy id 
                ontology = parts[parts.length - 1];
            }

            //replace _ in id with :
            //check if it contains first
            if (!ontology.contains(":") && ontology.contains("_")) {
                //split id
                String[] splitOntology = ontology.split("_");
                //if it has more than 1 _
                if (splitOntology.length > 2) {
                    //e.g CO_22_323232
                    String id = splitOntology[splitOntology.length - 1];
                    String otherPart = "";
                    //loop thru parts
                    for (int y = 0; y < splitOntology.length - 1; y++) {
                        //concatenate other parts
                        if (y == (splitOntology.length - 2)) {
                            otherPart += splitOntology[y];
                        } else {
                            otherPart += splitOntology[y] + "_";
                        }

                    }
                    //form back id
                    ontology = otherPart + ":" + id;
                } else {
                    //simply replace
                    ontology = ontology.replace("_", ":");
                }
            }

            //extracting the text that the ontology id matches
            for (int j = 0; j < annotationArray.size(); j++) {
                JSONObject annotationObj = (JSONObject) annotationArray.get(j);
                //get text
                matchText = annotationObj.get("text").toString();
                matchText = matchText.toLowerCase();

                //concatenate ontology id, uri and matches
                //check that the ontology uri is not duplicate before concatenating
                if (!tempList.contains(idUrl)) {
                    //check that length of ontologies with range
//                    if (ontologies.split(",").length < MAX_NUM_IDS) {
                    //concatenate
                    if (i == 0) {
                        ontologies += ontology;
                        uri += idUrl;
                        matches += matchText;
                    } else {
                        ontologies += ", " + ontology;
                        uri += ", " + idUrl;
                        matches += ", " + matchText;
                    }
//                    }
                    tempList.add(idUrl);
                    eqOntolList.add(ontology);
                    eqUriList.add(idUrl);
                    eqMatchList.add(matchText);
                }
            }
        }

        //Q stands for quality
        String qOntologies = "";
        String qUri = "";
        String qMatches = "";
        //E stands for entity
        String eOntologies = "";
        String eUri = "";
        String eMatches = "";

        if (eqMatchList.contains(text.toLowerCase())) {
            System.out.println("************FULL CONTAINS**********");
        } else {
            for (String txt : text.split(" ")) {
                if (eqMatchList.contains(txt.toLowerCase())) {
                    allMatch = allMatch && true;
                } else {
                    allMatch = allMatch && false;
                }
            }
        }
        if (!allMatch) {
            System.out.println("************PATH CONTAINS**********");
        } else if (!eqMatchList.contains(text.toLowerCase())) {

            for (int i = 0; i < eqMatchList.size(); i++) {
                //get Quality
                if (eqOntolList.get(i).contains(QUALITY)) {
                    if (i == 0) {
                        qOntologies += eqOntolList.get(i);
                        qUri += eqUriList.get(i);
                        qMatches += eqMatchList.get(i);
                    } else {
                        qOntologies += ", " + eqOntolList.get(i);
                        qUri += ", " + eqUriList.get(i);
                        qMatches += ", " + eqMatchList.get(i);
                    }
                } else if (i == 0) {
                    eOntologies += eqOntolList.get(i);
                    eUri += eqUriList.get(i);
                    eMatches += eqMatchList.get(i);
                } else {
                    eOntologies += ", " + eqOntolList.get(i);
                    eUri += ", " + eqUriList.get(i);
                    eMatches += ", " + eqMatchList.get(i);
                }
            }
        }

        //add to return array
        returnArray[INDEX_ONT] = ontologies;
        returnArray[INDEX_URI] = uri;
        returnArray[INDEX_MATCH] = matches;

        returnArray[INDEX_Q_ONT] = qOntologies;
        returnArray[INDEX_Q_URI] = qUri;
        returnArray[INDEX_Q_MATCH] = qMatches;
        returnArray[INDEX_E_ONT] = eOntologies;
        returnArray[INDEX_E_URI] = eUri;
        returnArray[INDEX_E_MATCH] = eMatches;

        return returnArray;
    }

    public void addOntology(String filename, String colName, String suggestions, String slice, String seperator) throws Exception {
        ReadWriteFile.ontMap = new HashMap<>();
        ReadWriteFile.uriMap = new HashMap<>();
        ReadWriteFile.matchMap = new HashMap<>();
        ReadWriteFile.qOntMap = new HashMap<>();
        ReadWriteFile.eOntMap = new HashMap<>();

        //read and get column names
        List valueList = ReadWriteFile.readAndGetOntologyNamesFromFile(filename, colName, seperator);

        //loop thru all added ontology-names
        for (Object value : valueList) {

            //run thread
            AQ.add(() -> {
                String[] ontologies = null;
                String ontologyName = value.toString().trim();
                System.out.println("ont-name - - " + ontologyName);

                //get ontologies
                try {
                    String reversedName = reverseWords(ontologyName);
                    System.out.println("reversed-name - - " + reversedName);
                    ontologies = this.getOntologyId(reversedName, suggestions, slice);
                } catch (Exception ex) {
                    Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("ontol - - " + ontologies[INDEX_ONT]);
                System.out.println("uri - - " + ontologies[INDEX_URI]);
                System.out.println("match - - " + ontologies[INDEX_MATCH]);

                System.out.println("Q-Ontol - - " + ontologies[INDEX_Q_ONT]);
                System.out.println("Q-Uri - - " + ontologies[INDEX_Q_URI]);
                System.out.println("Q-Match - - " + ontologies[INDEX_Q_MATCH]);
                System.out.println("E-Ontol - - " + ontologies[INDEX_E_ONT]);
                System.out.println("E-Uri - - " + ontologies[INDEX_E_URI]);
                System.out.println("E-Match - - " + ontologies[INDEX_E_MATCH]);

                //add ontology-id by adding it to map with ontology-name
                ReadWriteFile.ontMap.put(ontologyName, ontologies[INDEX_ONT]);
                //add uri by adding it to map with ontology-name
                ReadWriteFile.uriMap.put(ontologyName, ontologies[INDEX_URI]);
                //add matches by adding it to map with ontology-name
                ReadWriteFile.matchMap.put(ontologyName, ontologies[INDEX_MATCH]);

                ReadWriteFile.qOntMap.put(ontologyName, ontologies[INDEX_Q_ONT]);
                ReadWriteFile.eOntMap.put(ontologyName, ontologies[INDEX_E_ONT]);
            });
        }

        //finish thread
        AQ.finish();
        //write to new file
        //create output file name
        String[] fileParts = filename.split(Pattern.quote("."));
        String ext = fileParts[fileParts.length - 1];
        String outputFileName = "";
        for (int a = 0; a < fileParts.length - 1; a++) {
            outputFileName += fileParts[a];
        }

        outputFileName += "_new." + ext;

        //write
        ReadWriteFile.writeOntologyToFile(filename, colName, outputFileName, seperator);
        System.out.println("COMPLETED.");

    }
}
