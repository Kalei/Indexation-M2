/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation.functions;

import java.util.Arrays;
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
 * @author Kalei
 */
public class JaccardQuery {

    Dico db = null;
    Map<String, Double> result = new HashMap<>();
    LinkedFile fileList;

    public JaccardQuery(String req, Dico db) {

        List<String> reqList = Arrays.asList(req.toLowerCase().replaceAll("( ){2,}", " ").trim().split(" "));
        this.db = db;

        double orResult = db.orReqTfIdf(reqList);
        Map<String, Double> andResult = db.andReqLinkedFileTfIdf(reqList);

        for (String k : andResult.keySet()) {
            andResult.put(k, andResult.get(k) / orResult);
        }

        result = sortMapByValues(andResult);
    }

    public Map<String, Double> getResult() {
        return result;
    }

    public static < K, V extends Comparable< ? super V>> Map< K, V> sortMapByValues(final Map< K, V> mapToSort) {
        List list = new LinkedList(mapToSort.entrySet());

        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
