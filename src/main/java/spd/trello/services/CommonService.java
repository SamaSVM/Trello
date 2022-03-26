package spd.trello.services;

import spd.trello.domain.perent.Domain;

import java.util.List;
import java.util.UUID;

public interface CommonService<E extends Domain> {
    E save(E entity);

    E update(E entity);

    void delete(UUID id);

    E getById(UUID id);

    List<E> getAll();
}
