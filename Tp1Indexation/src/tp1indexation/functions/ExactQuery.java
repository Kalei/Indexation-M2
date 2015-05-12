package tp1indexation.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static tp1indexation.functions.JaccardQuery.sortMapByValues;

/**
 *
 * @author uapv9906226
 */
public class ExactQuery {

    private Dico dataBase;
    private List<String> reqList;
    private Map<String, Double> result;

    public ExactQuery(String req, Dico db) {

        this.dataBase = db;
        this.reqList = new LinkedList<String>(Arrays.asList(req.toLowerCase().replaceAll("( ){2,}", " ").trim().split(" ")));

        boolean find = false;
        int idToDelete = -1;
        String reqPosWord = null;
        int reqPos = 0;

        //On regarde si il existe un / dans la requete
        for (int i = 0; i < reqList.size(); i++) {
            if (reqList.get(i).contains("/")) {
                req = req.replace(reqList.get(i), "");
                reqPos = Integer.parseInt(reqList.get(i).replace("/", ""));
                
                idToDelete = i;
                find = true;
            }
        }

        List<String> exactsResult = new ArrayList<>();

        if (find) {
            reqList.remove(idToDelete);
            exactsResult = db.findSentence(reqList, reqPos, reqList.get(reqList.size() - 1));
                            
        } else {
            exactsResult = db.findSentence(reqList);
        }

        //On applique jaccard pour l'ensemeble des mots
        result = new JaccardQuery(req, db).getResult();
        
        //Si une requete exacte ou exacte poistionelle est trouvée
        if (!exactsResult.isEmpty()) {
            for (String exactPath : exactsResult) {
                for (Map.Entry<String, Double> entry : result.entrySet()) {
                    String jaccardPath = entry.getKey();
                    Double value = entry.getValue();

                    if (jaccardPath.equals(exactPath)) {
                        //On ajoute un poid aux documents aillant une requête exacte
                        result.put(jaccardPath, (value + (double) 0.5));
                    }
                }
            }
        }


        result = sortMapByValues(result);
    }

    public Map<String, Double> getResult() {

        return result;
    }
}
