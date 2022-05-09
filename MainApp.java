import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MainApp {

    public static void main(String[] args){

        /* -------------------------Déclaration des données-------------------------*/
        Graphe graphe = new Graphe("Ressources/CommunesFrance_10000coord.tgoGraph");
        Vertex source = graphe.listSommets.get(431  ); //nice
        Vertex destination = graphe.listSommets.get(5457 ); //lille
        Vertex[] pereSommet = new Vertex[graphe.nbSommets];
        /* -------------------------Affichage du graphe-------------------------*/
        //graphe.print();

        /* -------------------------Partie Dijkstra-------------------------*/
        long start = System.nanoTime();
        double[] distanceDijkstra = MethodesGraphe.dijkstra(graphe, source,destination,pereSommet);
        System.out.println("Plus court chemin avec Dijkstra depuis : "+ source + " vers : "+destination+ " = "+ distanceDijkstra[graphe.listSommets.indexOf(destination)]);
        long end = System.nanoTime();
        long executionDijkstra =end - start;
        System.out.println(executionDijkstra);
        /* -------------------------Partie A*-------------------------*/
        start = System.nanoTime();
        double distance = MethodesGraphe.AStar(graphe,source,destination);
        System.out.println("Plus court chemin avec A* depuis : "+ source + " vers : "+destination+ " = "+ distance);
        end = System.nanoTime();
        long executionAStar =end - start;
        System.out.println(executionAStar);
        /* -------------------------Partie Dijkstra avec SkipList-------------------------*/
        start = System.nanoTime();
        double[] distanceDijkstraSkipList = MethodesGraphe.dijkstraSkipList(graphe, source,destination,pereSommet);
        end = System.nanoTime();
        System.out.println("Plus court chemin avec Dijkstra depuis : "+ source + " vers : "+destination+ " = "+ distanceDijkstraSkipList[graphe.listSommets.indexOf(destination)]);
        long executionDijkstraSkipList =end - start;
        System.out.println(executionDijkstraSkipList);
        /* -------------------------Meilleur ville ou habiter (Question 4)-------------------------*/
        MethodesGraphe.villeHabiter(graphe);

        /* -------------------------Meilleur trajet (Question 5)-------------------------*/
        System.out.println(MethodesGraphe.unPassageParVille(graphe,200000));
    }
}
