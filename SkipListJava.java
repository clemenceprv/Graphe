import java.util.Iterator;
import java.util.Random;
import java.util.NoSuchElementException;

public class SkipListJava<K extends Comparable<K>, V> implements Iterable<K> {

    private int listsize;

    private double pb;

    protected static final Random randomGen = new Random();

    protected static final double DEFAULT_PB = 0.5;

    private NodeKeyValue<K, V> head;

    public Boolean isEmpty() {
        return this.getFirst() ==null;
    }

    public SkipListJava() {
        this(DEFAULT_PB);
    }

    public SkipListJava(double pb) {
        this.head = new NodeKeyValue<K, V>(null, null, 0);
        this.pb = pb;
        this.listsize = 0;
    }

    public NodeKeyValue<K, V> getFirst(){

        if(head.getDownList()==(null)){
            return head.getNext();
        }

        NodeKeyValue<K, V> tempHead =head.getDownList();
        while (true){
            if(tempHead.getDownList()==(null)){
                break;
            }
            tempHead = tempHead.getDownList();
        }
        return tempHead.getNext();
    }


    public V get(K key) {
        checkKeyValid(key);
        NodeKeyValue<K, V> listnode = findNode(key);
        if (listnode.getKey().compareTo(key) == 0)
            return listnode.getValue();
        else
            return null;
    }

    protected NodeKeyValue<K, V> findNode(K key) {
        NodeKeyValue<K, V> listnode = head;
        NodeKeyValue<K, V> next = null;
        NodeKeyValue<K, V> down = null;
        K nodeKey = null;
        while (true) {
            next = listnode.getNext();
            //Ici on cherche a <= b SUSSSS
            while (next != null && lessThanEqual(/*a*/ next.getKey(),/*b*/ key)) {
                boolean tempbool = lessThanEqual(/*a*/ next.getKey(),/*b*/ key);
                K temp = next.getKey();
                K temp2 = key;
                listnode = next;
                next = listnode.getNext();
            }
            nodeKey = listnode.getKey();
            if (nodeKey != null && nodeKey.compareTo(key) == 0)
                break;
            down = listnode.getDownList();
            if (down != null) {
                //On change de niveau
                listnode = down;
            } else {
                break;
            }
        }
        //Noeud cherché
        //Dans le cas ou le noeud cherché n'est pas trouvé listnode=null
        return listnode;
    }


    public void add(K key, V value) {
        checkKeyValid(key);
        //partie de verification si le noeud existe déja
        NodeKeyValue<K, V> listnode = findNode(key);
        if (listnode.getKey() != null && listnode.getKey().compareTo(key) == 0) {
            listnode.setValue(value);
            return;
        }
        //Le traitement commence ici si on ne sort pas de la fonction


        NodeKeyValue<K, V> newlistNode = new NodeKeyValue<K, V>(key, value, listnode.getLevel());
        horizontalInsertList(listnode, newlistNode);
        int curLevel = listnode.getLevel();
        int headlistLevel = head.getLevel();
        while (isBuildLevel()) {
            if (curLevel >= headlistLevel) {
                NodeKeyValue<K, V> newHeadEle = new NodeKeyValue<K, V>(null, null, headlistLevel + 1);
                verticalLink(newHeadEle, head);
                head = newHeadEle;
                headlistLevel = head.getLevel();
            }
            while (listnode.getUp() == null) {
                listnode = listnode.getPrevious();
            }
            listnode = listnode.getUp();
            NodeKeyValue<K, V> tmp = new NodeKeyValue<K, V>(key, value, listnode.getLevel());
            horizontalInsertList(listnode, tmp);
            verticalLink(tmp, newlistNode);
            newlistNode = tmp;
            curLevel++;
        }
        listsize++;
    }


    public void remove(K key) {
        checkKeyValid(key);
        NodeKeyValue<K, V> listnode = findNode(key);
        if (listnode == null || listnode.getKey().compareTo(key) != 0)
            throw new NoSuchElementException("Key does not exist!");
        while (listnode.getDownList() != null)
            listnode = listnode.getDownList();
        NodeKeyValue<K, V> previous = null;
        NodeKeyValue<K, V> next = null;
        for (; listnode != null; listnode = listnode.getUp()) {
            previous = listnode.getPrevious();
            next = listnode.getNext();
            if (previous != null)
                previous.setNext(next);
            if (next != null)
                next.setPreviousVal(previous);
        }
        while (head.getNext() == null && head.getDownList() != null) {
            head = head.getDownList();
            head.setUp(null);
        }
        listsize--;
    }


    public boolean contains(K key) {
        return get(key) != null;
    }


    public int listsize() {
        return listsize;
    }


    public boolean empty() {
        return listsize == 0;
    }



    protected void checkKeyValid(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key must be not null!");
    }


    protected boolean lessThanEqual(K a, K b) {
        //compareTo return -1 if a<b
        //return 0 if a=b
        //return 1 if a>b
        return a.compareTo(b) <= 0;
        //Ici on cherche a <= b
    }


    protected boolean isBuildLevel() {
        return randomGen.nextDouble() < pb;
    }


    protected void horizontalInsertList(NodeKeyValue<K, V> a, NodeKeyValue<K, V> b) {
        b.setPreviousVal(a);
        b.setNext(a.getNext());
        if (a.getNext() != null)
            a.getNext().setPreviousVal(b);
        a.setNext(b);
    }


    protected void verticalLink(NodeKeyValue<K, V> a, NodeKeyValue<K, V> b) {
        a.setDown(b);
        b.setUp(a);
    }


    @Override
    public String toString() {
        StringBuilder stringbuild = new StringBuilder();
        NodeKeyValue<K, V> listnode = head;
        while (listnode.getDownList() != null)
            listnode = listnode.getDownList();
        while (listnode.getPrevious() != null)
            listnode = listnode.getPrevious();
        if (listnode.getNext() != null)
            listnode = listnode.getNext();
        while (listnode != null) {
            stringbuild.append(listnode.toString()).append("\n");
            listnode = listnode.getNext();
        }
        return stringbuild.toString();
    }


    @Override
    public Iterator<K> iterator() {
        return new SkipListIterator<K, V>(head);
    }


    public void print(){
        NodeKeyValue<K, V> listnode = head;
        NodeKeyValue<K, V> next = null;
        while (true) {
            next = listnode.getNext();
            int lvl =  next.getLevel();
            System.out.print(" level : " + lvl+" ") ;
            while (next != null) {
                System.out.print(next );
                next = next.getNext();
            }
            if( listnode.getLevel()==0){
                break;
            }
            listnode = listnode.getDownList();
            System.out.println();
        }

    }






    public static void main(String[] args) {
        /*
        SkipListJava<Integer, Integer> skip = new SkipListJava<>();
        //int[] data = {1,2,3,4,5,6,7,8,9,10};
        int[] data = {1,2,3};
        for (int i = 0; i < 3; i++) {
            skip.add(data[i], i+100);
        }



        System.out.println(skip);

        assert skip.listsize() == 10;
        int count = 0;
        for (Integer i : skip)
            assert i.equals(count++);
        skip.remove(23);
        System.out.println(skip);
        skip.remove(25);
        skip.remove(33);
        skip.remove(30);
        System.out.println(skip);
        skip.remove(28);
        skip.add(25, "25");
        System.out.println(skip);
        assert skip.listsize() == 0;
        assert skip.empty();


        skip.print();
        //skip.remove(3);
        skip.add(1,200);
        skip.add(2,99);
        System.out.println();
        System.out.println();
        System.out.println();
        skip.print();
        System.out.println();
        System.out.println();
        System.out.println("ommok kahba " + skip.getFirst());
        System.out.println(skip.head.getNext());
        */



        SkipListJava<Vertex, Double> skipTest = new SkipListJava<>();
        System.out.println(skipTest.getFirst());
        //int[] data = {1,2,3,4,5,6,7,8,9,10};
        Vertex v1 = new Vertex("b",1);
        Vertex v2 = new Vertex("c",2);
        Vertex v3 = new Vertex("d",3);
        Vertex v4 = new Vertex("e",4);
        Vertex v5 = new Vertex("f",5);
        Vertex v6 = new Vertex("g",6);
        Vertex v7 = new Vertex("h",7);
        v1.cout=7;
        v2.cout=1;
        v6.cout=1;
        v7.cout=7;
        Vertex[] dataTest = {v1,v2,v3,v4,v5,v6,v7};
        for (int i = 0; i < 7; i++) {
            skipTest.add(dataTest[i],dataTest[i].cout );
        }
        skipTest.print();



        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        skipTest.remove(v6);
        v6.cout=10;
        skipTest.add(v6,v6.cout);
        skipTest.print();



    }
}