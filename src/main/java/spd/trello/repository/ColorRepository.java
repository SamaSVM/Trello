package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Color;

import java.util.UUID;

@Repository
public interface ColorRepository extends AbstractRepository<Color> {
}