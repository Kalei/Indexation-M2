Rovelli Hugo TP2

IDE: Netbeans

Suivre les instruction:

1 - Tapper default pour répertoir par défaut ('./test/corpus), corpus simple pour vérifier le bon fonctonnement des requêtes ou le chemin vers le corpus voulu ('~' permet d'aller dans le repertoir de l'utilisateur courant).

2 - query

3 - les mots à rechercher doivent être entre '', les opérateurs sans.

Je suis contient qu'il existe de meilleurs mannière de traiter chaque requête que celle de mon programme.
Le code est un peu lourd mais il me semble qu'il traite l'ensemble des cas recherchés.

Une méthode de substitution dans la requête aurait pu être mis en place ex:

'a' and 'b' or 'c' and ('a' or 'b')
{id_resultat1} or 'c' and ('a' or 'b')
{id_resultat1} or 'c' and {id_resultat2}
{id_resultat1} or {id_resultat3}

les id_resultat auraient pointés vers une élément d'une liste contenant le résultat de la sous requete.
