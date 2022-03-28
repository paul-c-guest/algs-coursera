package DynamicConnectivity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Scanner scanner;
    private static Algorithm alg;
    private static int size;

    public static void main(String[] args) {
        size = 5000000;
        alg = new WeightedQUCompression(size);
        logic();
    }

    public static void logic() {
        scanner = new Scanner(System.in);

        while (true) {
            System.out.print("-------\nchoose:\n-------"
                    + "\nmake union (U)"
                    + "\nmake N random unions (R)"
                    + "\nconnection test (C)"
                    + "\nmake N unions & connection tests (M)"
                    + "\ndo " + size + " U&C tests (T)"
                    + "\ndisplay the connection arrays (D)"
                    + "\nexit (X)\n");
            char command = scanner.nextLine().trim().toLowerCase().charAt(0);

            if (command == 'x') {
                System.out.println("\nciao!\n");
                break;

            } else if (command == 'u') {
                System.out.println("connect which two nodes?: (maximum is " + size + ")");
                int[] pair = getPair();
                alg.union(pair[0], pair[1]);

            } else if (command == 'c') {
                System.out.println("check between which two nodes?: max is " + size + ")");
                int[] pair = getPair();
                long timeAtStartOfOp = System.currentTimeMillis();
                if (alg.connected(pair[0], pair[1])) {
                    System.out.print("connected, ");
                } else {
                    System.out.print("not connected, ");
                }
                System.out.println(System.currentTimeMillis() - timeAtStartOfOp);

            } else if (command == 'r') {
                System.out.println("how many random connections to attempt? (array size is " + size + ")");
                int tries = Integer.parseInt(scanner.nextLine());
                randomUnions(tries);

            } else if (command == 'm') {
                System.out.println("how many test rounds? (array size is " + size + ")");
                int tries = Integer.parseInt(scanner.nextLine());
                long t = System.currentTimeMillis();
                randomUnionsAndConnections(tries);
                System.out.println("work took: " + (System.currentTimeMillis() - t) + " miliseconds");

            } else if (command == 't') {
                long t = System.currentTimeMillis();
                randomUnionsAndConnections(size);
                System.out.println("work took: " + (System.currentTimeMillis() - t) + " miliseconds");

            } else if (command == 'd' && size < 500) {
                printArray(alg);
            }
        }
    }

    private static void randomUnions(int tries) {
        Random r = new Random();
        for (int i = 0; i < tries; i++) {
            alg.union(r.nextInt(size), r.nextInt(size));
        }
    }

    private static void randomUnionsAndConnections(int tries) {
        Random r = new Random();
        for (int i = 0; i < tries; i++) {
            alg.union(r.nextInt(size), r.nextInt(size));
            alg.connected(r.nextInt(size), r.nextInt(size));
        }
    }

    private static int[] getPair() {
        int[] pair = new int[2];

        pair[0] = Integer.parseInt(scanner.nextLine());
        pair[1] = Integer.parseInt(scanner.nextLine());

        return pair;
    }

    private static void printArray(Algorithm alg) {
        String node = "\n  array index: ", parent = "root / parent: ";
        int length = alg.get().length;

        for (int i = 0; i < length; i++) {
            node += String.format("%02d", i) + " ";
            parent += String.format("%02d", alg.get()[i]) + " ";
        }

        System.out.println(node);

        for (int l = 0; l < node.length() - 2; l++) {
            System.out.print("-");
        }
        System.out.println("");

        System.out.println(parent);

        System.out.println("\n");
    }

}
