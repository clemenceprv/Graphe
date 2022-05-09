public class Vertex implements Comparable<Vertex> {
    int id;
    String nom;
    double x;
    double y;
    //le cout fixé en arrivant a ce Vertex
    public double cout = Double.POSITIVE_INFINITY;
    //l'ESTIMATION total (soit le cout fixé + distance estimée (vol oiseau))
    public double coutTotalEstime =  Double.POSITIVE_INFINITY;

    public Vertex(String nom,int id) {
        this.id= id;
        this.nom = nom;
    }

    public Vertex(String nom,int id, double x, double y) {
        this.nom = nom;
        this.id = id;
        this.x =x;
        this.y =y;
    }

    public Vertex() {}

    public String toString(){
        return nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public int compareTo(Vertex o) {
        if(this.cout < o.cout){
            return -1;
        }
        else if(this.cout == o.cout){
            if(this.id<o.id){
                return -1;
            }
            else if(this.id==o.id){
                return 0;
            }
            else{
                return 1;
            }
        }
        else{
            return 1;
        }
    }

}
