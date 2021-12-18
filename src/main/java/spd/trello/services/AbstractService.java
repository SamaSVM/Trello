package spd.trello.services;

import spd.trello.domain.Resource;

public abstract class AbstractService<T extends Resource, S> {
    public abstract T create(S source);
}
