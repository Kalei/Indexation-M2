/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author uapv9906226
 */
class Word implements Serializable {

    protected String word;
    protected Map<String, LinkedFile> containFile = new HashMap();
    protected int total_occurence;

    public Word(String word, String path, int nbTermes, int tmpPosition) {
        this.word = word;
        total_occurence = 1;
        LinkedFile tmpFile = new LinkedFile(path, nbTermes, tmpPosition,this.word);

        containFile.put(path, tmpFile);
    }

    protected void addFreq(String path, int nbTerms, int tmpPosition) {
        if (containFile.containsKey(path)) {
            containFile.get(path).occurence++;
            containFile.get(path).addPosition(tmpPosition);
        } else {
            LinkedFile tmpFile = new LinkedFile(path, nbTerms, tmpPosition,this.word);

            containFile.put(path, tmpFile);
        }
        total_occurence++;
    }

    protected Map<String, LinkedFile> getLinkedFiles() {
        return containFile;
    }

    protected LinkedFile getLinkedFile(String fileName) {
        if (containFile.containsKey(fileName)) {
            return containFile.get(fileName);
        } else {
            return null;
        }
    }

    protected List<String> getFilesName() {
        List<String> filesList = new ArrayList<>();
        for (Map.Entry<String, LinkedFile> entry : containFile.entrySet()) {
            filesList.add(entry.getKey());
        }
        return filesList;
    }

    protected boolean existFile(String word) {
        return containFile.containsKey(word);
    }

    protected double getGlobalFrequency() {
        int nbLinkedFiles = containFile.size();
        double tmpResult = (double) 0;

        for (Map.Entry<String, LinkedFile> entry : containFile.entrySet()) {
            String string = entry.getKey();
            LinkedFile linkedFile = entry.getValue();

            tmpResult += linkedFile.getFrequency();

        }

        return tmpResult / nbLinkedFiles;
    }

    public double getTfIdf(String path, int nbDocs) {
        if(containFile.containsKey(path)){
            double idf = Math.log((double) nbDocs / (double) containFile.size());
            //System.err.println(nbDocs + " " + containFile.size() + " " + nbDocs + " " + idf);
            return (double) containFile.get(path).getOccurence() * (double) idf;
        }
        else{
            return 0.0;
        }
    }

    public Double getAllTfIdfSum(int nbDocs) {
        double result = (double) 0;

        for (Map.Entry<String, LinkedFile> entry : containFile.entrySet()) {
            String path = entry.getKey();
            result += getTfIdf(path, nbDocs);
        }

        return result;
    }

    public Map<String, Double> getAllTfIdf(int nbDocs) {
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, LinkedFile> entry : containFile.entrySet()) {
            String path = entry.getKey();

            result.put(path, getTfIdf(path, nbDocs));
        }

        return result;
    }

    public Map<String, Integer> getOccurence(int nbDocs) {
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<String, LinkedFile> entry : containFile.entrySet()) {
            String path = entry.getKey();

            result.put(path, containFile.get(path).occurence);
        }

        return result;
    }
}
