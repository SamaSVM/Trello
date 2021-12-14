package spd.trello.services;

import spd.trello.domain.Resource;

import java.util.Scanner;

public abstract class AbstractService <T extends Resource> {
    public abstract T create(Scanner scanner);
}
