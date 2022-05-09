import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Graphe {
    String path ;
    boolean oriente;
    int nbSommets;
    int nbArcs;
    int nbValeursParArc;
    ArrayList<Vertex> listSommets;
    ArrayList<LinkedList<Edge>> listeAdjacence ;

    int[] degreSommets;


    public Graphe(String path){

        this.path = path;
        //----------Importation du fichier et des paramétres du graphe ----------//
        ArrayList<String[]>  tempLignesDesEdgesExporter = new ArrayList<>();
        ArrayList<Vertex> tempListSommets = new ArrayList<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            //Line1
            myReader.nextLine();
            //Line2
            this.oriente=Boolean.parseBoolean(myReader.nextLine().substring(10));
            //Line 3
            myReader.nextLine();
            //Line 4
            String[] line = myReader.nextLine().split(" ");
            this.nbSommets = Integer.parseInt(line[0]);
            this.nbArcs = Integer.parseInt(line[2]);
            //Line 5
            myReader.nextLine();
            //Line 6
            int id = 0;
            String[] vertex = myReader.nextLine().split(" ");
            //Line 7 --> Ligne : Edge[idInitialVertex idFinalVertex values[]]
            while(!vertex[0].equals("Edges")){
                if(vertex.length==2){
                    tempListSommets.add(new Vertex(vertex[1],id));
                }
                else{
                    tempListSommets.add(new Vertex(vertex[1],id,Double.parseDouble(vertex[2]),Double.parseDouble(vertex[3])));
                }
                id++;
                vertex = myReader.nextLine().split(" ");
            }
            //Les lignes qui viennent apres : Edge[idInitialVertex idFinalVertex values[]]
            while(myReader.hasNext()){
                tempLignesDesEdgesExporter.add(myReader.nextLine().split(" "));
            }
            //fermeture de myReader
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        this.listSommets=tempListSommets;
        //---------remplissage de la liste d'adjacence en fonction de nombre de sommets---------//
        ArrayList<LinkedList<Edge>> tempListeAdjacence = new ArrayList<>();
        remplirListeAdjacence(tempListeAdjacence,nbSommets,oriente, tempLignesDesEdgesExporter);
        this.listeAdjacence = tempListeAdjacence;
        //---------remplissage de la liste d'adjacence en fonction de nombre de sommets---------//
        //---------recuperation de nombre de degrés---------********//
        this.nbValeursParArc = listeAdjacence.get(0).get(0).valeurs.length;
        int[] tempDegreSommets = new int[nbSommets];
        for(int i=0; i < nbSommets ; i++) {
            tempDegreSommets[i] = listeAdjacence.get(i).size();
        }
        //---------recuperation de nombre de degrés---------//
        if (oriente==true) {
            ArrayList<LinkedList<Edge>> tempListeAdjacencePourDegre = new ArrayList<>();
            remplirListeAdjacence(tempListeAdjacencePourDegre,nbSommets,false, tempLignesDesEdgesExporter);
            for (int i = 0; i < nbSommets; i++) {
                tempDegreSommets[i] = tempListeAdjacencePourDegre.get(i).size();
            }
        }
        else{
            for (int i = 0; i < nbSommets; i++) {
                tempDegreSommets[i] = listeAdjacence.get(i).size();
            }
        }
        this.degreSommets=tempDegreSommets;
    }

    private void remplirListeAdjacence(ArrayList<LinkedList<Edge>> listeAdjacence, int nbSommets, boolean oriented, ArrayList<String[]>  lignesDesEdgesExporter){
        int longueur = nbSommets;
        for(int i = 0;i<longueur;i++){
            listeAdjacence.add(new LinkedList<>());
        }

        //tempListEdge est une liste chainée pour chaque sommet et ses voisins
        LinkedList<Edge> tempListEdge =new LinkedList<>();

        //On prend le premier sommet present dans la liste pour le comparé avec le suivant
        int indexInit = Integer.parseInt(lignesDesEdgesExporter.get(0)[0]);

        //Traitement de la liste d'adjacence dans le cas d'un graphe orienté
        for(String[] ligneCourrante : lignesDesEdgesExporter){
            //tableau temporaire pour les valeurs de Edge
            double[] tempValeurs = new double[ligneCourrante.length-2];
            //Remplissage du tableau "tempValeurs"
            for(int i=0; i<tempValeurs.length; i++){
                tempValeurs[i]= Double.parseDouble(ligneCourrante[2 + i]);
            }
            int index = Integer.parseInt(ligneCourrante[0]);

            if (index != indexInit) {
                indexInit = index;
                tempListEdge = new LinkedList<>();

            }
            tempListEdge.add(new Edge(Integer.parseInt(ligneCourrante[1]), tempValeurs));
            //On affecte la liste chainée au sommet correspond pour passer au sommet suivant
            listeAdjacence.set(index, tempListEdge);
        }

        //Triatment de la liste d'adjacence dans le cas d'un graphe non orienté
        if(oriented==false) {
            //Creation d'une nouvelle liste chaine qui traite le cas des graphes non orientés
            ArrayList<LinkedList<Edge>> newListeAdjacence = new ArrayList<>();
            //Creation remplissage de la nouvelle
            for (int i = 0; i < longueur; i++) {
                newListeAdjacence.add(new LinkedList<>());
            }

            //Insertion des elements de la nouvelle liste
            for (int i = 0; i < listeAdjacence.size(); i++) {
                LinkedList<Edge> notOrientedList1 = listeAdjacence.get(i);
                for (Edge tempEdge : notOrientedList1) {
                    newListeAdjacence.get(tempEdge.sommetTerminal).add(new Edge(i, tempEdge.valeurs));
                }
            }

            //Insertion des elements de la nouvelle liste dans la liste chainee du graphe
            for (int i = 0; i < listeAdjacence.size(); i++) {
                if (listeAdjacence.get(i).size() == 0) {
                    LinkedList<Edge> temp = new LinkedList<>();
                    for (Edge e : newListeAdjacence.get(i)) {
                        temp.add(e);
                    }
                    for (Edge e : temp) {
                        listeAdjacence.get(i).add(e);
                    }
                } else {
                    for (Edge e : newListeAdjacence.get(i)) {
                        listeAdjacence.get(i).addFirst(e);
                    }
                }
            }
        }
    }




    public void print(){
        System.out.println("Structure du graphe : \n");
        System.out.println("oriente = " + oriente);
        System.out.println("nbSommets = " + nbSommets);
        System.out.println("nbArcs = " + nbArcs);
        System.out.println("nbValeursParArc = " + nbValeursParArc);
        System.out.println("listeAdjacence = " );
        for(int i=0; i<listeAdjacence.size();i++){
            System.out.println();
            System.out.print(i + " : ");
            for (Edge tempEdge : listeAdjacence.get(i)) {
                System.out.print(tempEdge.sommetTerminal);
                tabToString(tempEdge.valeurs);
                System.out.print(" ");
            }
        }
        System.out.println();
        //System.out.println("degrés des sommets = ");
        for(int i =0 ; i<degreSommets.length;i++){
            //System.out.print("|"+listSommets.get(i).nom+" : " + degreSommets[i]+ "|");
        }
    }
    //Affichage d'un tableau sous forme de String
    private static void tabToString(double [] array){
        System.out.print("(");
        if(array.length>0){
            System.out.print(array[0]);
        }
        if(array.length>1){
            for (int i = 1 ; i<array.length;i++){
                System.out.print(","+array[i]);
            }
        }
        System.out.print(")");
    }

}
