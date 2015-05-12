/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp1indexation.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author uapv9906226
 */
public class Query {

    private List<String> result = new ArrayList<>();
    private Dico dataBase;
    private List<String> reqList;


    public Query(String req, Dico db) {
        dataBase = db;

        req = req.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
        req = req.toLowerCase().replaceAll("( ){2,}", " ").trim();
        //System.err.println(req);

        reqList = Arrays.asList(req.split(" "));
        int it = 0;

        for (int i = 0; i < reqList.size(); i++) {
            reqList.set(i, reqList.get(i).trim());
        }
        boolean error = false;

        while (it < reqList.size() && !error) {
            //On commence le traitement lors ce que l'on recontre une chaine de caractere
            //ex : 'a', 'soleil',...
            if (reqList.get(it).matches("\'([^\"]*)\'")) {
                //On v�rifie que nous ayons un �lement par la suite...
                //Et on traite selon l'op�rateur
                if (it + 1 < reqList.size()) {
                    //On v�rifie l'op�rateur qui suit
                    switch (reqList.get(it + 1)) {
                        case "or":
                            //Traitement dans le cas d'un op�rateur or
                            //ex: 'a' or 'b'

                            //On v�rifie que la chaine trait� n'est pas n�gative
                            if (it - 1 >= 0) {
                                if (reqList.get(it - 1).equals("not")) {
                                    List<String> tmp_result = dataBase.getAllFiles();
                                    List<String> tmp_not_result = dataBase.orReq(reqList.get(it).replace("'", ""));

                                    tmp_result.removeAll(tmp_not_result);

                                    result.addAll(tmp_result);
                                } else if (reqList.get(it - 1).equals("(")) {
                                    String sub_req = "";
                                    int sub_it = it;

                                    //On cr�e une concat�ne toute l'expression contenu entre parenthese 
                                    //jusqu'a la parenthese fermante
                                    while (!reqList.get(sub_it).equals(")")) {
                                        //System.err.println("Sub req : " + reqList.get(sub_it));
                                        sub_req += " " + reqList.get(sub_it);
                                        sub_it++;
                                    }

                                    if (it - 2 >= 0) {
                                        if (reqList.get(it - 2).equals("not")) {

                                            List<String> tmp_result = dataBase.getAllFiles();
                                            List<String> tmp_not_result = new Query(sub_req, dataBase).getResult();

                                            System.err.println(sub_req);
                                            tmp_result.removeAll(tmp_not_result);

                                            result.addAll(tmp_result);

                                        } else {
                                            result.addAll(new Query(sub_req.trim(), dataBase).getResult());
                                        }
                                    } else {
                                        result.addAll(new Query(sub_req.trim(), dataBase).getResult());
                                    }
                                    //On r�ccup�re le dernier el�ment de la perenth�se
                                    it = sub_it;
                                } else {
                                    //Dans le cas ou la chaine est positive
                                    result.addAll(dataBase.orReq(reqList.get(it).replace("'", "")));
                                }
                            } else {
                                //Dans le cas ou nous avons un or sans n�gation
                                result.addAll(dataBase.orReq(reqList.get(it).replace("'", "")));
                            }
                            //Incr�ment pour passer sur l'op�rateur
                            //Ici or
                            it++;
                            break;
                        case "and":
                            //Traitement du and
                            List<String> andWords = new ArrayList<>();//mots sans n�gation
                            List<String> notWords = new ArrayList<>();//mots avec n�gation
                            List<String> tmp_not_result = new ArrayList<>();//r�sultat avec n�gation
                            List<String> tmp_result = new ArrayList<>();//r�sultat sans n�gation
                            
                            //On r�ccup�re l'�l�ment actuel
                            if (it - 1 >= 0) {
                                //cas d'une n�gation
                                if (reqList.get(it - 1).equals("not")) {
                                    notWords.add(reqList.get(it).replace("'", ""));
                                } else {
                                    //cas sans n�gation
                                    andWords.add(reqList.get(it).replace("'", ""));
                                }
                            } else {
                                andWords.add(reqList.get(it).replace("'", ""));
                            }

                            boolean endAnds = false;
                            int tmp_it = it;

                            while (!endAnds && tmp_it < reqList.size()) {
                                switch (reqList.get(tmp_it)) {
                                    case "or":
                                        //Incrementation qui suit le switch permet d'�viter de traiter le or en temps que tel
                                        //ex: 'a' and 'b' or 'c' and 'd' 
                                        // -> resultat de 'a' and 'b'
                                        // -> resultat de 'c' and 'd'                                       
                                        //System.err.println("La : " + reqList.get(tmp_it));
                                        endAnds = true;
                                        break;
                                    case "and":
                                        //On traite dans le cas d'un and suivit d'une n�gation
                                        if (reqList.get(tmp_it + 1).equals("not")) {
                                            //Dans le cas d'un and suivit d'une n�gation puis d'une parenth�se ouvrante
                                            if (reqList.get(tmp_it + 2).equals("(")) {
                                                String sub_req = "";

                                                //On r�cup�re le premer �l�ment de la sous requete
                                                int sub_it = tmp_it + 3;

                                                //On cr�e une concat�ne toute l'expression contenu entre parenthese 
                                                //jusqu'a la parenthese fermante
                                                while (!reqList.get(sub_it).equals(")")) {
                                                    sub_req += " " + reqList.get(sub_it);
                                                    sub_it++;
                                                }

                                                //On r�ccup�re le dernier el�ment de la perenth�se
                                                tmp_it = sub_it;

                                                //On appelle r�cursivement la requete afin de r�ccup�rer le r�sultat de la sous requete
                                                //On l'ajoute aux r�sultat n�gatif
                                                tmp_not_result.addAll(new Query(sub_req.trim(), dataBase).getResult());
                                            } else {
                                                //N�gation sans sous requ�te entre parentheses
                                                notWords.add(reqList.get(tmp_it + 2).replace("'", ""));
                                            }
                                            //On passe sur l'�l�ment suivant
                                            tmp_it++;
                                        } else {
                                            if (reqList.get(tmp_it + 1).equals("(")) {
                                                String sub_req = "";
                                                int sub_it = tmp_it + 2;

                                                //On cr�e une concat�ne toute l'expression contenu entre parenthese 
                                                //jusqu'a la parenthese fermante
                                                while (!reqList.get(sub_it).equals(")")) {
                                                    //System.err.println("Sub req : " + reqList.get(sub_it));
                                                    sub_req += " " + reqList.get(sub_it);
                                                    sub_it++;
                                                }

                                                //On r�ccup�re le dernier el�ment de la perenth�se
                                                tmp_it = sub_it;

                                                //On appelle r�cursivement la requete afin de r�ccup�rer le r�sultat de la sous requete
                                                //On l'ajoute aux r�sultat
                                                tmp_result.addAll(new Query(sub_req.trim(), dataBase).getResult());
                                            } else {
                                                //Sans sous requ�te entre parentheses
                                                andWords.add(reqList.get(tmp_it + 1).replace("'", ""));
                                            }
                                        }
                                        //On incr�mente pour passer a la chaine suivante
                                        tmp_it++;
                                        break;
                                    default:
                                        //Par d�faut on passe � l'�l�ment suivants
                                        tmp_it++;
                                        break;
                                }
                            }

                            //On r�cup�re l'indice de l'it�rateur temporaire
                            it = tmp_it;

                            //On chercher le r�sultat pour les chaine non n�gative
                            if (!andWords.isEmpty()) {
                                tmp_result = dataBase.andReq(andWords);
                            }

                            if (!notWords.isEmpty()) {
                                //R�sultat pour les chaines n�gatives
                                tmp_not_result = dataBase.orReq(notWords);
                            }
                            
                            if (!tmp_not_result.isEmpty()) {
                                //On enl�ve du tableau de r�sultats les r�sultats n�gatifs
                                tmp_result.removeAll(tmp_not_result);
                            }

                            //On ajoute au r�sultat global
                            result.addAll(tmp_result);

                            break;
                        case "not":
                            List<String> tmpNotResult = dataBase.orReq(reqList.get(it + 2).replace("'", ""));
                            List<String> tmpResult = dataBase.orReq(reqList.get(it).replace("'", ""));

                            tmpResult.removeAll(tmpNotResult);

                            if (reqList.get(it + 1).endsWith("(")) {
                                List<String> tmpResultPar = dataBase.getAllFiles();
                                String sub_req = "";
                                int sub_it = it + 3;

                                while (!reqList.get(sub_it).equals(")")) {
                                    //System.err.println("Sub req : " + reqList.get(sub_it));
                                    sub_req += " " + reqList.get(sub_it);
                                    sub_it++;
                                }

                                System.err.println("Sub req : " + sub_req.trim());
                                it = sub_it;
                                List<String> tmpNotResultPar = new Query(sub_req.trim(), dataBase).getResult();

                                tmpResultPar.removeAll(tmpNotResultPar);

                                result.addAll(tmpResultPar);
                            }

                            result.addAll(tmpResult);

                            break;
                        default:
                            error = true;
                            break;
                    }
                } else {
                    //Si une chaine de caract�re est trouv� en fin de requ�te
                    //ex: 'a'
                    //ex2: not 'a'
                    //ex3: 'a' and 'b' or 'a'
                    if (it - 1 >= 0) {
                        if (reqList.get(it - 1).equals("not")) {
                            List<String> tmp_result = dataBase.getAllFiles();
                            List<String> tmp_not_result = dataBase.orReq(reqList.get(it).replace("'", ""));

                            tmp_result.removeAll(tmp_not_result);

                            result.addAll(tmp_result);
                        } else {
                            result.addAll(dataBase.orReq(reqList.get(it).replace("'", "")));
                        }
                    } else {
                        result.addAll(dataBase.orReq(reqList.get(it).replace("'", "")));
                    }
                }
            }

            it++;

            if (error) {
                System.err.println("ERREUR LORS DE LA SAISIE DE LA REQUETE!");
            }
        }
        //On �vite les doublons dans la liste de r�sultat
        Set set = new HashSet();
        set.addAll(result);
        result = new ArrayList(set);
    }

    public List<String> getResult() {
        return result;
    }
}