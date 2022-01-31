package spd.trello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Member;
import spd.trello.domain.perent.Domain;
import spd.trello.services.AbstractService;
import spd.trello.services.MemberService;

import java.util.List;
import java.util.UUID;

public class AbstractController<E extends Domain, S extends AbstractService<E>> {
    S service;

    @Autowired
    MemberService memberService;

    public AbstractController(S service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<E> create(@RequestBody E resource) {
        E result = service.create(resource);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PutMapping("/{memberId}/{id}")
    public ResponseEntity<E> update(@PathVariable UUID memberId, @PathVariable UUID id, @RequestBody E resource) {
        resource.setId(id);
        Member member = memberService.findById(memberId);
        E result = service.update(member, resource);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable UUID id) {
        service.delete(id);
        return HttpStatus.OK;
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
