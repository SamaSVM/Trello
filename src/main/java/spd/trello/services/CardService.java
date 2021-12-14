package spd.trello.services;

import spd.trello.domain.Card;

import java.util.Scanner;

public class CardService extends AbstractService<Card>{
    @Override
    public Card create(Scanner scanner) {
        Card result = new Card();
        scanner.next();
        System.out.println("Введіть назву картки!");
        result.setName(scanner.next());
        System.out.println("Введіть опис картки!");
        result.setDescription(scanner.next());
        return result;
    }
}
