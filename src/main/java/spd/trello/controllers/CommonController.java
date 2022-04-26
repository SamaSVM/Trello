package spd.trello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import spd.trello.domain.perent.Domain;

import java.util.List;
import java.util.UUID;

public interface CommonController<E extends Domain> {

    ResponseEntity<E> create(E resource, BindingResult bindingResult);

    ResponseEntity<E> update(UUID id, E resource, BindingResult bindingResult);

    HttpStatus delete(UUID id);

    ResponseEntity<E> readById(UUID id);

    List<E> readAll();
}
