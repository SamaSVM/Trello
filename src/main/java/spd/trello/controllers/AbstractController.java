package spd.trello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.perent.Domain;
import spd.trello.exception.BadRequestException;
import spd.trello.services.CommonService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public class AbstractController<E extends Domain, S extends CommonService<E>> implements CommonController<E> {
    S service;

    public AbstractController(S service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<E> create(@Valid @RequestBody E resource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throwNewBadRequestException(bindingResult);
        }
        E result = service.save(resource);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @Override
    public ResponseEntity<E> update(@PathVariable UUID id, @Valid @RequestBody E resource, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throwNewBadRequestException(bindingResult);
        }
        resource.setId(id);
        E result = service.update(resource);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Override
    public HttpStatus delete(@PathVariable UUID id) {
        service.delete(id);
        return HttpStatus.OK;
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<E> readById(@PathVariable UUID id) {
        E result = service.getById(id);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping
    @Override
    public List<E> readAll() {
        return service.getAll();
    }

    void throwNewBadRequestException(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        bindingResult.getAllErrors().forEach(err -> stringBuilder.append(err.getDefaultMessage()).append("\n"));
        throw new BadRequestException(stringBuilder.toString());
    }
}
