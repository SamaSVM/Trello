package spd.trello.services;

import spd.trello.domain.Resource;

public abstract class AbstractService <T extends Resource> {
    public abstract T create();
}
