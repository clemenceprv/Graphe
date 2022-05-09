import java.util.*;

public class MethodesGraphe {
    private static double[] distance;

    //--------------------------------------------------Dijkstra--------------------------------------------------//

    public static double[] dijkstra(Graphe graphe, Vertex source,Vertex destination,Vertex[] pereDijkstra){
        distance = new double[graphe.nbSommets];
        Comparator<Vertex> comparator = new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return Double.compare(o1.cout, o2.cout);
            }
        };
        PriorityQueue<Vertex> Z = new PriorityQueue<Vertex>(comparator);
        //Initialisation des couts de chaque sommet a l'infini
        for (int i = 0; i < graphe.listSommets.size(); i++) {
            graphe.listSommets.get(i).cout = Double.POSITIVE_INFINITY;
        }
        int indiceSource = graphe.listSommets.indexOf(source);
        //Initialisation des couts des Vertex successeurs de la source
        for (Edge voisinSource : graphe.listeAdjacence.get(indiceSource)) {
            int indiceVoisinSource = voisinSource.sommetTerminal;
            graphe.listSommets.get(indiceVoisinSource).cout = voisinSource.valeurs[0];
            pereDijkstra[indiceVoisinSource]=source;

        }
        source.cout = 0;
        pereDijkstra[indiceSource]=source;
        for (int i = 0; i < graphe.listSommets.size(); i++) {
            Z.add(graphe.listSommets.get(i));

        }
        Z.poll();
        while (!Z.isEmpty()) {
            Vertex currentVertex = Z.poll();
            if(currentVertex == destination){
                break;
            }
            int indiceCurrentVertex = graphe.listSommets.indexOf(currentVertex);
            for (Edge successeur : graphe.listeAdjacence.get(indiceCurrentVertex)) {
                int indiceVoisin = successeur.sommetTerminal;
                Vertex voisin = graphe.listSommets.get(indiceVoisin);
                //Si λ(X)+I((X,i))<λ(i)
                if (currentVertex.cout + successeur.valeurs[0] < voisin.cout && Z.contains(voisin)) {
                    voisin.cout = currentVertex.cout + successeur.valeurs[0];
                    pereDijkstra[indiceVoisin]=currentVertex;
                    Z.remove(voisin);
                    Z.add(voisin);
                }
            }
        }
        for(int i=0; i<distance.length;i++){
            distance[i]=graphe.listSommets.get(i).cout;
        }
        return distance;
    }


    //fonction d'heuristique
    public static double Distance(double lat_a, double lon_a, double lat_b, double lon_b) {
        // Rayon de la terre en m�tre
        long R = 6378137;
        // Conversion degre en radian
        double lat_a_R = Math.toRadians(lat_a);
        double lon_a_R = Math.toRadians(lon_a);
        double lat_b_R = Math.toRadians(lat_b);
        double lon_b_R = Math.toRadians(lon_b);
        // Formule math�matiques pour calculer la distance � vol d'oiseau
        double dla = (lat_b_R - lat_a_R) / 2;
        double dlo = (lon_b_R - lon_a_R) / 2;
        double a = (Math.sin(dla) * Math.sin(dla))
                + Math.cos(lat_a_R) * Math.cos(lat_b_R) * (Math.sin(dlo) * Math.sin(dlo));
        double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * d);
    }

    //--------------------------------------------------A*--------------------------------------------------//

    //Fonction de plus court chemin avec A* :
    public static double AStar(Graphe graphe, Vertex source,Vertex destination) {
        //Condtion puisqu'on modifie l'attribut cout de vertex dans d'autre fonctions
        for(int i=0;i<graphe.listSommets.size();i++){
            graphe.listSommets.get(i).cout=Double.POSITIVE_INFINITY;
        }

        ArrayList<Vertex> closedList = new ArrayList<>();
        PriorityQueue<Vertex> Z = new PriorityQueue<>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v2, Vertex v1) {
                //return Double.compare(v1.heuristique,v2.heuristique);
                if (v1.coutTotalEstime < v2.coutTotalEstime) {
                    return 1;
                }
                else if (v1.coutTotalEstime < v2.coutTotalEstime) {
                    return 0;
                }
                else {
                    return - 1;
                }
            }
        });
        source.cout=0;
        source.coutTotalEstime=Distance(source.y,source.x,destination.y,destination.x);
        Z.add(source);
        while(!Z.isEmpty()){
            Vertex currentVertex = Z.poll();
            if(currentVertex==destination){
                closedList.add(currentVertex);
                break;
            }
            int indexActualVertex = graphe.listSommets.indexOf(currentVertex);
            for (Edge successeur : graphe.listeAdjacence.get(indexActualVertex)) {
                //on traite les successeurs du currentVertex (voisin)
                int indexVoisin = successeur.sommetTerminal;
                Vertex voisin = graphe.listSommets.get(indexVoisin);
                double cout = successeur.valeurs[0]*1000;
                boolean condition = currentVertex.cout+ cout + Distance(voisin.y,voisin.x,destination.y,destination.x)  <voisin.coutTotalEstime ;
                if (condition){
                    //On change le cout de ce voisin puisqu'il semble plus proche a la destination (d'après la condition)
                    voisin.cout=currentVertex.cout+cout;
                    //On lui affecte sa nouvelle estimation de cout total
                    voisin.coutTotalEstime =voisin.cout + Distance(voisin.y,voisin.x,destination.y,destination.x);
                    if(Z.contains(voisin)){
                        Z.remove(voisin);
                    }
                    Z.add(voisin);
                }
            }
            //System.out.println("Me go from ere "+currentVertex.nom);
            closedList.add(currentVertex);
        }
        for(Vertex v : graphe.listSommets){
            v.cout = v.cout/1000;
        }
        return destination.cout;
    }

    public static HashMap<Vertex,Double> tabToHashMap(double[] tab,Graphe graphe){
        HashMap<Vertex, Double> map = new HashMap<>();
        for(int i=0; i<tab.length;i++){
            map.put(graphe.listSommets.get(i),tab[i]);
        }
        return map;
    }

    //-----------------------------------------------DijkstraSkipList-----------------------------------------------//

    public static double[] dijkstraSkipList(Graphe graphe, Vertex source, Vertex destination, Vertex[] pereDijkstra){
        distance = new double[graphe.nbSommets];
        for (int i = 0; i < graphe.listSommets.size(); i++) {
            graphe.listSommets.get(i).cout = Double.POSITIVE_INFINITY;
        }
        SkipListJava<Vertex,Double> skipList = new SkipListJava<>();
        int indiceSource = graphe.listSommets.indexOf(source);
        //Initialisation des couts des Vertex successeurs de la source
        for (Edge voisinSource : graphe.listeAdjacence.get(indiceSource)) {
            int indiceVoisinSource = voisinSource.sommetTerminal;
            graphe.listSommets.get(indiceVoisinSource).cout = voisinSource.valeurs[0];
            pereDijkstra[indiceVoisinSource]=source;
        }
        source.cout = 0;
        pereDijkstra[indiceSource]=source;
        for (int i = 0; i < graphe.listSommets.size(); i++) {
            skipList.add(graphe.listSommets.get(i),graphe.listSommets.get(i).cout);
        }
        skipList.remove(source);
        while(!skipList.isEmpty()){
            Vertex currentVertex = skipList.getFirst().getKey();
            skipList.remove(currentVertex);
            int indiceCurrentVertex= graphe.listSommets.indexOf(currentVertex);
            //Ici on arrete le programme
            if(currentVertex==destination){
                break;
            }
            for(Edge successeur : graphe.listeAdjacence.get(indiceCurrentVertex)){
                int indiceVoisin = successeur.sommetTerminal;
                Vertex voisin = graphe.listSommets.get(indiceVoisin);
                if(currentVertex.cout + successeur.valeurs[0] < voisin.cout&& skipList.contains(voisin)){
                    skipList.remove(voisin);
                    voisin.cout = currentVertex.cout + successeur.valeurs[0];
                    pereDijkstra[indiceVoisin]=currentVertex;
                    //application du nouveau cout
                    skipList.add(voisin,voisin.cout);
                }
            }
        }
        for(int i=0; i<distance.length;i++){
            distance[i]=graphe.listSommets.get(i).cout;
        }
        return distance;
    }

    //-----------------------------------------------Question 4-----------------------------------------------//

    // Fonction qui retroune le nom de toutes les villes de plus de X habitants
    public static ArrayList<String> villesX(long nbHabs) {
        ArrayList<String> grandesVilles = new ArrayList<String>();
        // On crée un objet de type Excel pour utiliser la fonction Read
        Excel exc = new Excel();
        ArrayList<String[]> outFinale;
        // outFinale prend pour valeur le résulat de la fonction Read
        outFinale = exc.ReadExcel("Ressources/CommunesFrance.csv");
        for (int i = 1; i<outFinale.size(); i++) {
            // on récupère le nom de la ville B qui est en cours
            if( Long.parseLong(outFinale.get(i)[2]) >= nbHabs )
                grandesVilles.add(outFinale.get(i)[0]);
        }
        return grandesVilles;
    }


    public static String villeHabiter(Graphe graphe) {
        // nom des villes de plus de 200 000 habitants
        ArrayList<String> nomsVillesTravail = villesX(200000);
        // On récupère la hashmap associant à chaque ville la distance
        double minSumDistance = Double.POSITIVE_INFINITY;
        // Vertex des villes de plus de 200 000 habitants
        ArrayList<Vertex> villesTravail = new ArrayList<Vertex>();
        // On récupère les Vertex des villes de plus de 200 000 habitants à partir de leur nom
        for(int i = 0; i<graphe.listSommets.size(); i++) {
            for( String villeTravail : nomsVillesTravail ) {
                if( graphe.listSommets.get(i).getNom().equals(villeTravail))
                    villesTravail.add(graphe.listSommets.get(i));
            }
        }
        Vertex villeOptimale = new Vertex();
        //villesTravail liste des villes (Vertex) de 200 000 habitants
        ArrayList<HashMap<Vertex,Double>> toutesDistances = new ArrayList();
        for (Vertex ville : villesTravail){
            toutesDistances.add(tabToHashMap(dijkstraSkipList(graphe,ville,new Vertex(),new Vertex[graphe.nbSommets]),graphe));
        }
        double sumDistance;
        for( Vertex villeCandidate : graphe.listSommets ) {
            sumDistance=0;
            for(HashMap<Vertex,Double> hashMapDistances : toutesDistances){
                sumDistance += hashMapDistances.get(villeCandidate);
            }
            if( sumDistance <= minSumDistance ) {
                minSumDistance = sumDistance;
                villeOptimale = villeCandidate;
            }
        }
        minSumDistance = minSumDistance/villesTravail.size();

        System.out.println("La ville la plus optimale pour minimiser ses trajets est : "+villeOptimale.getNom());
        System.out.println("La distance moyenne de ses trajets est alors de : "+minSumDistance);
        return villeOptimale.getNom();
    }

    //-----------------------------------------------Question 5-----------------------------------------------//

    //Fonction qui retourne le distance minimale sous forme d'un hashMap
    //Key : Ville
    //Value : distance depuis la source vers la ville correspondante
    public static HashMap<Vertex,Double> minimumHashMap(HashMap<Vertex, Double> map) {
        Vertex villePlusProche = new Vertex();
        double distance = Double.POSITIVE_INFINITY;
        double valuerActuelle;
        for(Map.Entry<Vertex, Double> entry : map.entrySet()) {
            valuerActuelle = entry.getValue();
            Vertex key = entry.getKey();
            if ( valuerActuelle < distance ) {
                villePlusProche = key;
                distance = valuerActuelle;
            }
        }
        HashMap<Vertex,Double> retour = new HashMap<Vertex,Double>();
        retour.put(villePlusProche,distance);
        return retour;
    }

    public static double unPassageParVille(Graphe graphe, int nbHabs) {
        ArrayList<Vertex> parcoursOptimal = new ArrayList<Vertex>();
        ArrayList<String> nomVillesVisitees = villesX(nbHabs);
        ArrayList<Vertex> villesVisitees = new ArrayList<Vertex>();
        // On récupère les Vertex des villes de plus de X habitants à partir de leur nom
        for(int i = 0; i<graphe.listSommets.size(); i++) {
            for( String villeTravail : nomVillesVisitees ) {
                if( graphe.listSommets.get(i).getNom().equals(villeTravail)) {
                    villesVisitees.add(graphe.listSommets.get(i));
                }
            }
        }
        double minSumDistance = Double.POSITIVE_INFINITY;
        double sumDistance;
        for(Vertex villeCandidate : villesVisitees){
            ArrayList<Vertex> villeMarquer = new ArrayList<Vertex>();
            sumDistance=0;
            Vertex villeCourante=villeCandidate ;
            villeMarquer.add(villeCourante);
            for(int i=0; i<villesVisitees.size()-1 ;i++){
                HashMap<Vertex,Double> distances = tabToHashMap(dijkstraSkipList(graphe,villeCourante,new Vertex(),
                        new Vertex[graphe.nbSommets]),graphe);
                HashMap<Vertex,Double> distanceParVille = new HashMap<Vertex,Double>();
                for(Map.Entry<Vertex, Double> set: distances.entrySet()){
                    if(villesVisitees.contains(set.getKey())) {
                        distanceParVille.put(set.getKey(), set.getValue());
                    }
                }
                for(int j=0;j<villeMarquer.size();j++){
                    distanceParVille.remove(villeMarquer.get(j));
                }
                HashMap<Vertex,Double> cc =  minimumHashMap(distanceParVille);
                for(Map.Entry<Vertex, Double> set: cc.entrySet()){
                    villeCourante=set.getKey();
                    villeMarquer.add(villeCourante);
                    sumDistance += set.getValue();
                }
            }
            HashMap<Vertex,Double> distances = tabToHashMap(dijkstraSkipList(graphe,villeCourante,new Vertex(),new Vertex[graphe.nbSommets]),graphe);
            sumDistance += distances.get(villeCandidate);
            villeMarquer.add(villeCandidate);
            if(sumDistance<minSumDistance){
                minSumDistance=sumDistance;
                parcoursOptimal = villeMarquer;
            }
        }
        //System.out.println("nombre d'habitants = " +) ;
        System.out.println("Le meilleur trajet qui minimise la distance totale parcourue est:");
        for(int i=0;i<parcoursOptimal.size()-1;i++){
            System.out.print(parcoursOptimal.get(i).nom +" - " );
        }
        System.out.print(parcoursOptimal.get(parcoursOptimal.size()-1).nom);
        System.out.println();
        System.out.print("La distance minimale = " );
        return minSumDistance;
    }

    public static void main(String[] args) {
        Graphe graphe = new Graphe("C:\\Users\\theri\\Desktop\\3A Polytech\\S6\\Graphes et application\\Projet 2022\\Sujet et données\\CommunesFrance_5000coord.tgoGraph");
        villeHabiter(graphe);
        System.out.println(unPassageParVille(graphe,200000));
    }
}
