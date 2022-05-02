package spd.trello.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.perent.Domain;
import spd.trello.services.CommonService;

import java.util.List;
import java.util.UUID;

@Slf4j
public class AbstractController<E extends Domain, S extends CommonService<E>> implements CommonController<E> {
    S service;

    public AbstractController(S service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<E> create(@RequestBody E resource) {
        E result = service.save(resource);
        log.debug("Save entity in AbstractController {}", result);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @Override
    public ResponseEntity<E> update(@PathVariable UUID id, @RequestBody E resource) {
        resource.setId(id);
        E result = service.update(resource);
        log.debug("Update entity in AbstractController {}", result);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Override
    public HttpStatus delete(@PathVariable UUID id) {
        service.delete(id);
        log.debug("Delete entity in AbstractController with id - {}", id);
        return HttpStatus.OK;
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<E> readById(@PathVariable UUID id) {
        E result = service.getById(id);
        log.debug("Get entity in AbstractController {}", result);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping
    @Override
    public List<E> readAll() {
        List<E> result = service.getAll();
        log.debug("Get all entity in AbstractController {}", result);
        return result;
    }
}
