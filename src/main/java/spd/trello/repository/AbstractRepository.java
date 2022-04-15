package spd.trello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spd.trello.domain.perent.Domain;

import java.util.UUID;

public interface AbstractRepository<E extends Domain> extends JpaRepository<E, UUID> {
}
