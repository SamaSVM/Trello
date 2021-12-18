package spd.trello.services;

import spd.trello.domain.Board;

import java.util.Scanner;

public class BoardService extends AbstractService<Board, Scanner> {

    @Override
    public Board create(Scanner source) {
        Board result = new Board();
        source.next();
        System.out.println("Введіть назву доски!");
        result.setName(source.next());
        System.out.println("Введіть опис доски!");
        result.setDescription(source.next());
        return result;
    }
}
