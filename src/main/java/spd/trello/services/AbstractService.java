package spd.trello.services;

import spd.trello.domain.perent.Domain;
import spd.trello.repository.InterfaceRepository;

public abstract class AbstractService<T extends Domain> {
    protected InterfaceRepository<T> repository;

    public AbstractService(InterfaceRepository<T> repository) {
        this.repository = repository;
    }
}
