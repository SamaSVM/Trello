package spd.trello.services;

import spd.trello.domain.perent.Domain;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.AbstractRepository;
import spd.trello.validators.AbstractValidator;

import java.util.List;
import java.util.UUID;

public abstract class AbstractService<E extends Domain, R extends AbstractRepository<E>, V extends AbstractValidator<E>>
        implements CommonService<E> {
    R repository;
    V validator;

    public AbstractService(R repository, V validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public E save(E entity) {
        try {
            validator.validateSaveEntity(entity);
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public E update(E entity) {
        try {
            validator.validateUpdateEntity(entity);
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            repository.deleteById(id);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
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

