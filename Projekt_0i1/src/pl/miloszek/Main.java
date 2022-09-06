package pl.miloszek;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        int[] Array = new int[18];
        losowe(Array);

        try {
            // nazwa raportu zapisywana z datą i godziną
            Date nowDate = new Date();
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            PrintWriter zapis = new PrintWriter(".\\raporty\\"+ date.format(nowDate) + "_raport.txt");

            GraphNodes graph_Nodes = new GraphNodes();

            // dla każdego elementu Array wyszukuje wielokrotności
            for (int j : Array) {
                if (ifZeroAndOne(String.valueOf(j))) {
                    System.out.println(j + " ---> " + j);
                    zapis.println(j + " ---> " + j);
                    continue;
                }
                System.out.println(j + " ---> " + graph_Nodes.findMultiple(j));
                zapis.println(j + " ---> " + graph_Nodes.findMultiple(j));
            }
            zapis.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nastąpił błąd związany z plikiem!");
        }
    }

    // sprawdza czy liczba składa się tylko z jedynek i zer
    public static boolean ifZeroAndOne(String value) {
        char[] Array = value.toCharArray();
        for (int i = 0; i < value.length(); i++) {
            if (Array[i] != '1' && Array[i] != '0') {
                return false;
            }
        }
        return true;
    }

    // losuje wartości do tablicy
    public static void losowe(int[] Array) {
        Random random = new Random();
        for (int i = 0; i < Array.length; i++) {
            Array[i] = random.nextInt(999_999) + 1;
        }
    }
}

class Node {
    protected BigInteger ID;
    protected String value;

    protected Node nextInPath;

    public Node() {}
    public Node(BigInteger ID, String value) {
        this.ID = ID;
        this.value = value;
    }
}

class GraphNodes {
    private BigInteger counter = new BigInteger("0");
    private int power = 1;

    private final Node noneNode = new Node();
    private Node firstNode = noneNode;
    private Node actualToDouble = noneNode;

    private Node theFirstNodeAtFloor = noneNode;
    private Node atTheSameFloor = noneNode;

    public GraphNodes() {
        generateGraphOfNodes();
    }

    // tworzy węzeł zerowy
    public void startNode() {
        Node newNode = new Node(counter, "1");
        firstNode = newNode;
        actualToDouble = newNode;
    }

    // tworzy dwa kolejne węzły z aktualnego węzła
    public void createTwoBranches() {
        BigInteger jeden = new BigInteger("1");
        counter = counter.add(jeden);
        Node newNode_1 = new Node(counter, actualToDouble.value + "0");

        counter = counter.add(jeden);
        Node newNode_2 = new Node(counter, actualToDouble.value + "1");

        // sprawdza, czy pierwszy nowo utworzony węzeł jest pierwszym na poziomie
        if (ifFirstAtFloor(newNode_1.ID)) {
            theFirstNodeAtFloor = newNode_1;
        } else {
            // łączy węzły na tym samym poziomie, ale wychodzące z innego węzła
            atTheSameFloor.nextInPath = newNode_1;
        }

        // sprawdza, czy aktualny węzeł jest ostatnim węzłem na poziomie
        if (ifEndAtFloor(actualToDouble.ID)) {
            actualToDouble.nextInPath = theFirstNodeAtFloor;
        }

        // łączy dwa nowo utworzone węzły
        newNode_1.nextInPath = newNode_2;
        atTheSameFloor = newNode_2;
    }

    // sprawdza, czy węzeł pierwszy na poziomie
    public Boolean ifFirstAtFloor(BigInteger ID) {
        BigInteger jeden = new BigInteger("1");
        return ID.add(jeden).equals(BigInteger.valueOf((long) Math.pow(2, power)));
    }

    // sprawdza, czy węzeł ostatni na poziomie
    public Boolean ifEndAtFloor(BigInteger ID) {
        BigInteger dwa = new BigInteger("2");
        if (ID.add(dwa).equals(BigInteger.valueOf((long)Math.pow(2, power)))) {
            power++;
            return true;
        }
        else {
            return false;
        }    }

    // iteracyjnie wywołuje funkcję createTwoBranches(), co tworzy drzewo binarne
    public void generateGraphOfNodes() {
        startNode();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 1_048_576; j++) {
                createTwoBranches();
                actualToDouble = actualToDouble.nextInPath;
            }
        }
    }

    // 1 000 000 000 000 000 000 000 001
    // znajduje wielokrotność
    public String findMultiple(int number) {
        Node nodeToCheck = firstNode;
        while (true) {
            if (nodeToCheck.nextInPath == null) {
                return "BRAK";
            }

            BigInteger mod = new BigInteger(nodeToCheck.value).mod(new BigInteger(String.valueOf(number)));
            if (mod.equals(new BigInteger("0"))) {
                return nodeToCheck.value;
            }
            nodeToCheck = nodeToCheck.nextInPath;
        }
    }
}
