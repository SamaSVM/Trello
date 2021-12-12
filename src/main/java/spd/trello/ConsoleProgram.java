package spd.trello;

import spd.trello.domain.*;
import spd.trello.services.*;

import java.util.Scanner;

public class ConsoleProgram {
    public static void main(String[] args) {
        BoardService boardService = new BoardService();
        CardService cardService = new CardService();
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println("Введіть:\n1 - Створити нову доску.\n2 - Створити картку.\n3 - Завершити роботу програми.");
            if (scanner.hasNext("3")){
                break;
            }
            if (scanner.hasNext("1")){
                scanner.next();
                Board board = boardService.create();
                System.out.println("Введіть назву доски!");
                board.setName(scanner.next());
                if(scanner.hasNextLine()) {
                    System.out.println(board);
                    continue;
                }
            }
            if (scanner.hasNext("2")){
                scanner.next();
                Card card = cardService.create();
                System.out.println("Введіть назву картки!");
                card.setName(scanner.next());
                if(scanner.hasNextLine()) {
                    System.out.println(card);
                    continue;
                }
            }
            scanner.next();
        }
    }
}
