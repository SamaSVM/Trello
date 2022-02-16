package spd.trello.services;

import spd.trello.domain.perent.Domain;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.AbstractRepository;

import java.util.List;
import java.util.UUID;

public abstract class AbstractService<E extends Domain, R extends AbstractRepository<E>> implements CommonService<E>{
    R repository;

    public AbstractService(R repository) {
        this.repository = repository;
    }

    @Override
    public E save(E entity) {
        return repository.save(entity);
    }

    @Override
    public E update(E entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public E getById(UUID id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<E> getAll() {
        return repository.findAll();
    }
}

