package spd.trello.services;

import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.perent.Domain;
import spd.trello.repository.InterfaceRepository;

import java.util.List;
import java.util.UUID;

public abstract class AbstractService<T extends Domain> {
    protected InterfaceRepository<T> repository;

    public AbstractService(InterfaceRepository<T> repository) {
        this.repository = repository;
    }

    public T findById(UUID id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T create(T entity) {
        repository.create(entity);
        return repository.findById(entity.getId());
    }

    public T update(Member member, T entity) {
        checkMember(member);
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }

    private void checkMember(Member member) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot delete!");
        }
    }
}
