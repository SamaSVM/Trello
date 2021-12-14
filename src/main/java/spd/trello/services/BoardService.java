package spd.trello.services;

import spd.trello.domain.Board;

import java.util.Scanner;

public class BoardService extends AbstractService<Board> {
    @Override
    public Board create(Scanner scanner) {
        Board result = new Board();
        scanner.next();
        System.out.println("Введіть назву доски!");
        result.setName(scanner.next());
        System.out.println("Введіть опис доски!");
        result.setDescription(scanner.next());
        return result;
    }
}
