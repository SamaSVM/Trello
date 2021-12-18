package spd.trello.services;

import spd.trello.domain.Card;

import java.util.Scanner;

public class CardService extends AbstractService<Card ,Scanner> {
    @Override
    public Card create(Scanner source) {
        Card result = new Card();
        source.next();
        System.out.println("Введіть назву картки!");
        result.setName(source.next());
        System.out.println("Введіть опис картки!");
        result.setDescription(source.next());
        return result;
    }
}
