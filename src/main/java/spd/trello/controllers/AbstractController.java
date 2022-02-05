package spd.trello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.perent.Domain;
import spd.trello.services.AbstractService;

import java.util.List;
import java.util.UUID;

public class AbstractController<E extends Domain, S extends AbstractService<E>> {
    S service;

    public AbstractController(S service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<E> create(@RequestBody E resource) {
        E result = service.create(resource);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<E> update(@PathVariable UUID id, @RequestBody E resource) {
        resource.setId(id);
        E result = service.update(resource);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable UUID id) {
        if(service.delete(id)){
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> readById(@PathVariable UUID id) {
        E result = service.findById(id);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping
    public List<E> readAll() {
        return service.findAll();
    }
}
