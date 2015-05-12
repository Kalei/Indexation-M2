/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import tp1indexation.functions.Dico;
import tp1indexation.functions.ExactQuery;
import tp1indexation.functions.Fichier;
import tp1indexation.functions.JaccardQuery;
import tp1indexation.functions.Query;

/**
 *
 * @author uapv9906226
 */
public class MainDico {
    
    public static void main(String[] args) throws FileNotFoundException, NullPointerException {
        
        Scanner reader = new Scanner(System.in);
        String repertoire = "";
        System.out.println("Veuillez indiquer le chemin vers le fichier ou tapper load pour charger (\'default\' ./test/corpus (corpus simpliste)) :");
        repertoire = reader.nextLine().trim();
        
        if (repertoire.equals("default")) {
            repertoire = "./test/corpus";
        }
        
        try {
            String choice = "";
            Dico currentDico = new Dico();
            List<String> result = null;
            
            if (!repertoire.equals("load")) {
                repertoire = repertoire.replace("~", System.getProperty("user.home"));
                String[] fileList = Fichier.fileList(repertoire);
                currentDico.setNbDoc(fileList.length);
                for (int i = 0; i < fileList.length; i++) {
                    //System.err.println(fileList[i]);
                    String txt = Fichier.lire(repertoire + File.separator + fileList[i]);
                    currentDico.termFrequency(txt, repertoire + File.separator + fileList[i]);
                }
            } else {
                choice = "load";
            }
            
            while (!choice.equals("exit")) {
                String req = "";
                if (choice.equals("")) {
                    System.out.println("Liste des action..... ");
                    System.out.println("-----------------------------------");
                    System.out.println("[1] display");
                    System.out.println("[2] save ");
                    System.out.println("[3] load ");
                    System.out.println("[4] query ");
                    System.out.println("[5] tfidf (sur un mot sans Jaccard)");
                    System.out.println("[6] Jaccard Tf Idf");
                    System.out.println("[7] exact query");
                    System.out.println("[9] exit");
                    System.out.println("-----------------------------------");
                    System.out.print("Tappez votre choix : ");
                    choice = reader.nextLine().trim();
                }
                
                switch (choice) {
                    case "display":
                    case "1":
                        currentDico.display();
                        choice = "";
                        break;
                    
                    case "save":
                    case "2":
                        currentDico.serialize();
                        choice = "";
                        break;
                    
                    case "load":
                    case "3":
                        Dico toLoadDico = Dico.unSerialize();
                        //toLoadDico.display();
                        System.out.println("Votre corpus a été chargé.");
                        currentDico = toLoadDico;
                        
                        choice = "";
                        break;
                    
                    case "query":
                    case "4":
                        System.out.println("Entrez votre requête (chaine de caractère entre \'\' ex: \'ma_chaine1\' OR \'ma_chaine2\'): ");
                        req = reader.nextLine();
                        System.out.println("-------------------------------------------");
                        System.out.println("Résultat requête :");
                        
                        result = new Query(req, currentDico).getResult();
                        if (!result.isEmpty()) {
                            for (String file : result) {
                                System.out.println(file);
                            }
                        } else {
                            System.out.println("Aucun résultat n'a été trouvé.");
                        }
                        System.out.println("-------------------------------------------");
                        choice = "";
                        break;
                    
                    case "tfidf":
                    case "5":
                        System.out.println("Entrez un mot à évaluer : ");
                        req = reader.nextLine();
                        System.out.println("-------------------------------------------");
                        System.out.println("Résultat requête :");
                        
                        System.out.println("Tfidf du mot " + req + " : " + currentDico.getTfIdf(req));
                        System.out.println("-------------------------------------------");
                        choice = "";
                        break;
                    
                    case "Jaccard_tfidf":
                    case "6":
                        System.out.println("Entrez votre requête phrase (ex: a b): ");
                        req = reader.nextLine();
                        System.out.println("-------------------------------------------");
                        System.out.println("Résultat requête :");
                        
                        Map<String, Double> resultMap = new JaccardQuery(req, currentDico).getResult();
                        
                        if (!resultMap.isEmpty()) {
                            int i = 0;
                            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                                i++;
                                String key = entry.getKey();
                                Double value = entry.getValue();
                                if (value < (double) 1 && value > (double) 0) {
                                    System.out.println("File : " + key + " Jaccard: " + value);
                                }
                                
                                if(i==10){
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Aucun résultat n'a été trouvé.");
                        }
                        
                        System.out.println("-------------------------------------------");
                        choice = "";
                        break;
                    
                    case "exact_query":
                    case "7":
                        System.out.println("Entrez votre requête phrase (ex: Man on fire): ");
                        req = reader.nextLine();
                        System.out.println("-------------------------------------------");
                        System.out.println("Résultat requête :");
                        
                        Map<String, Double> exactResult = new ExactQuery(req, currentDico).getResult();
                        
                        if (!exactResult.isEmpty()) {
                            int i = 0;
                            for (Map.Entry<String, Double> entry : exactResult.entrySet()) {
                                i++;
                                String key = entry.getKey();
                                Double value = entry.getValue();
                                if (value > (double) 0) {
                                    System.out.println("File : " + key + " Jaccard: " + value);
                                }
                                
                                if(i==10){
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Aucun résultat n'a été trouvé.");
                        }
                        choice = "";
                        break;
                    
                    case "exit":
                    case "9":
                        break;
                    
                    default:
                        choice = "";
                        break;
                }
            }
        } catch (NullPointerException e) {
        }
    }
}
