package org.ruitx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {

        printTitle();

        Conways game = new Conways();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        String mapPath = "";
        while (true) {
            try {
                if (mapPath != null && !mapPath.isEmpty()) {
                    System.out.println();
                    System.out.println("Map '" + mapPath + "' selected");
                }
                printMainMenu();
                System.out.print("[option]> ");
                input = reader.readLine().toUpperCase();
                MainMenuEnum choice = checkChoice(input);
                switch (choice) {
                    case LOAD:
                        mapPath = game.loadMap();
                        break;
                    case PRINT:
                        game.printMap();
                        break;
                    case DRAW:
                        game.drawMap();
                        break;
                    case RNDSTABLE:
                        mapPath = game.generateRandomStableMap();
                        break;
                    case RANDOM:
                        mapPath = game.generateRandomMap();
                        break;
                    case EXIT:
                        return;

                    default:
                        System.out.println("Not an option.");
                }
            } catch (IOException e) {
                System.out.println("Something went wrong. Please try again.\nDetails: " + e.getMessage());
            }
        }
    }

    public static MainMenuEnum checkChoice(String choice) {
        for (int i = 0; i < MainMenuEnum.values().length; i++) {
            if (choice.equals(MainMenuEnum.values()[i].getOption())) {
                return MainMenuEnum.values()[i];
            }
        }
        return null;
    }

    public static void printMainMenu() {
        System.out.printf("%s\t%s\n", "", "");
        for (int i = 0; i < MainMenuEnum.values().length; i++) {
            System.out.printf("[%s]\t%s\n", MainMenuEnum.values()[i].getOption(), MainMenuEnum.values()[i].getDesc());
        }
        System.out.println();
    }

    public static void printTitle() {
        System.out.println();
        System.out.println("Conway's Game of Life");
    }
}