/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation.functions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author uapv9906226
 */
public class Dico implements Serializable {

    private Map<String, Word> words = new HashMap();
    private List<String> allDocs = new ArrayList<>();
    private int nbDocs = 0;

    public void termFrequency(String txt, String path) {
        txt = cleanText(txt);
        String txtWords[] = txt.split(" ");
        int nbWords = txtWords.length;
        allDocs.add(path);
        int tmpPosition = 0;
        for (String word : txtWords) {
            tmpPosition++;
            if (word.length() > 0) {
                if (words.containsKey(word)) {
                    words.get(word).addFreq(path, nbWords, tmpPosition);
                } else {
                    words.put(word, new Word(word, path, nbWords, tmpPosition));
                }
            }
        }
    }

    public void setNbDoc(int nbDoc) {
        this.nbDocs = nbDoc;
    }

    protected String cleanText(String txt) {
        txt = txt.replaceAll("[\\d\\p{Punct}]", " ").toLowerCase().trim();
        return txt;
    }

    public Map<String, Double> getTfIdf(String term) {
        if (words.containsKey(term)) {
            return words.get(term).getAllTfIdf(allDocs.size());
        } else {

            return null;
        }
    }

    private static Map sortMap(Map unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public void display() {
        System.out.println("--------------------------");
        for (Map.Entry<String, Word> entry : words.entrySet()) {
            String mot = entry.getKey();
            Integer occurence = entry.getValue().total_occurence;
            System.out.println("--------------------------");
            System.out.println(mot + " : " + occurence);

            for (Map.Entry<String, LinkedFile> file : entry.getValue().containFile.entrySet()) {
                System.out.println(file.getKey() + " : " + file.getValue().occurence);
            }
            System.out.println("--------------------------");
        }
    }

    public void serialize() {
        try {
            FileOutputStream fichier = new FileOutputStream("advTreatment.ser");
            try (ObjectOutputStream oos = new ObjectOutputStream(fichier)) {
                oos.writeObject(this);
                oos.flush();
            }
        } catch (java.io.IOException e) {
        }
    }

    public static Dico unSerialize() {
        try {
            FileInputStream fichier = new FileInputStream("advTreatment.ser");
            ObjectInputStream ois = new ObjectInputStream(fichier);
            Dico toLoadDico = (Dico) ois.readObject();
            return toLoadDico;
        } catch (java.io.IOException | ClassNotFoundException e) {
        }
        return null;
    }

    protected List<String> orReq(List<String> wordsReq) {
        List<String> result = new ArrayList<>();

        for (String word : wordsReq) {
            if (this.words.containsKey(word)) {
                result.addAll(this.words.get(word).getFilesName());
            } else {
                System.out.println("Le mot " + word + " n'existe pas!");
            }
        }

        return result;
    }

    protected List<String> orReq(String word) {
        List<String> result = new ArrayList<>();

        if (this.words.containsKey(word)) {
            result.addAll(this.words.get(word).getFilesName());
        } else {
            System.out.println("Le mot " + word + " n'existe pas!");
        }

        return result;
    }

    protected List<String> andReq(List<String> wordsReq) {
        List<String> result = new ArrayList<>();
        List<String> tmp_result = new ArrayList<>();
        if (this.words.containsKey(wordsReq.get(0))) {
            result.addAll(this.words.get(wordsReq.get(0)).getFilesName());
        } else {
            System.out.println("Le mot " + wordsReq.get(0) + " n'existe pas!");
        }

        for (int i = 1; i < wordsReq.size(); i++) {
            tmp_result = result;
            if (this.words.containsKey(wordsReq.get(i))) {
                for (int j = 0; j < result.size(); j++) {
                    if (!this.words.get(wordsReq.get(i)).existFile(result.get(j))) {
                        tmp_result.remove(result.get(j));

                        //Décrémentation de j car le dernier maillon a changé
                        j--;
                    }
                }
            } else {
                System.out.println("Le mot " + wordsReq.get(i) + " n'existe pas!");
            }
        }
        return result;
    }

    public List<String> notReq(List<String> results, String[] wordsReq) {
        for (String wordReq : wordsReq) {
            for (String result : results) {
                if (this.words.get(wordReq).existFile(result)) {
                    results.remove(result);
                }
            }
        }

        return results;
    }

    public List<String> findSentence(List<String> reqWords) {

        //On réccupère seulement les fichiers contenant l'ensemble des mots
        List<String> paths = andReq(reqWords);

        List<LinkedFile> tmpWordFile = new ArrayList<>();
        List result = new ArrayList<>();

        if (!reqWords.isEmpty() && !paths.isEmpty()) {
            for (String path : paths) {
                for (String word : reqWords) {
                    //Pour chaque mots on récupère le LinkedFile associer
                    //Contenant l'ensemble des indices positionnels du mot
                    tmpWordFile.add(words.get(word).getLinkedFile(path));
                }

                int n = 0;
                boolean end = false;
                //System.err.println(tmpWordFile.get(0).positions);
                for (int firsWordPostion : tmpWordFile.get(0).positions) {
                    int actualPosition = firsWordPostion;
                    while (n < tmpWordFile.size() && tmpWordFile.get(n++).positions.contains(actualPosition)) {
                        if (n == tmpWordFile.size()) {
                            result.add(path);
                            end = true;
                        }
                        actualPosition++;
                    }

                    n = 0;
                }

                tmpWordFile.clear();
            }
        }

        return result;
    }

    public List<String> findSentence(List<String> reqWords, int reqPos, String reqPosWord) {

        //On réccupère tous les documents contenant les trois mots
        List<String> totalReqWords = reqWords;
        totalReqWords.add(reqPosWord);

        //On réccupère seulement les fichiers contenant l'ensemble des mots
        List<String> paths = andReq(totalReqWords);

        List<LinkedFile> tmpWordFile = new ArrayList<>();
        List result = new ArrayList<>();

        if (!reqWords.isEmpty() && !paths.isEmpty()) {
            for (String path : paths) {
                for (String word : reqWords) {
                    //Pour chaque mots on récupère le LinkedFile associer
                    //Contenant l'ensemble des indices positionnels du mot
                    tmpWordFile.add(words.get(word).getLinkedFile(path));
                }

                int n = 0;
                boolean end = false;
                //System.err.println(tmpWordFile.get(0).positions);
                for (int firsWordPostion : tmpWordFile.get(0).positions) {
                    int actualPosition = firsWordPostion;
                    while (n < tmpWordFile.size() && tmpWordFile.get(n++).positions.contains(actualPosition)) {
                        //On test si le mot de la reqPositionel est bien à reqPos du premier ou dernier mots de la requete exacte
                        if (words.get(reqPosWord).getLinkedFile(path).positions.contains(actualPosition - reqPos) || words.get(reqPosWord).getLinkedFile(path).positions.contains(actualPosition + reqPos)) {
                            result.add(path);
                            end = true;
                        }
                        actualPosition++;
                    }

                    n = 0;
                }

                tmpWordFile.clear();
            }
        }

        //System.err.println(result.toString());
        return result;
    }

    public List<String> getAllFiles() {
        return allDocs;
    }

    Map<String, Double> andReqLinkedFileTfIdf(List<String> wordsReq) {
        Map<String, Double> resultMap = new HashMap<>();

        for (String doc : allDocs) {
            for (String wordReq : wordsReq) {
                if (!resultMap.containsKey(doc)) {
                    //Pour chaque document on récupére le tfidf du mot
                    resultMap.put(doc, (double) words.get(wordReq).getTfIdf(doc, nbDocs));
                } else {
                    resultMap.put(doc, (double) resultMap.get(doc) + (double) words.get(wordReq).getTfIdf(doc, nbDocs));
                }
            }
        }

        return resultMap;
    }

    Double orReqTfIdf(List<String> req) {
        double result = (double) 0;


        for (String word : req) {
            if (words.containsKey(word)) {
                result += words.get(word).getAllTfIdfSum(allDocs.size());
            }
        }

        return (double) result;
    }
}
