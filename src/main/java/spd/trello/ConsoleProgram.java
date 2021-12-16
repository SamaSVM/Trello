package spd.trello;

import spd.trello.services.*;

import java.util.Scanner;

public class ConsoleProgram {
    public static void main(String[] args) {
        startConsoleProgram();
    }

    private static void startConsoleProgram() {
        BoardService boardService = new BoardService();
        CardService cardService = new CardService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введіть:\n1 - Створити нову доску.\n2 - Створити картку.\n" +
                    "3 - Завершити роботу програми.");
            if (scanner.hasNext("3")) {
                break;
            }
            if (scanner.hasNext("1")) {
                System.out.println(boardService.create(scanner));
                continue;
            }
            if (scanner.hasNext("2")) {
                System.out.println(cardService.create(scanner));
                continue;
            }
            scanner.next();
        }
    }
}
