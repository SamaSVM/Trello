package spd.trello.repository;

import java.util.UUID;

public interface InterfaceRepository<E> {
	E findById(UUID id);
	void create(E entity);
	E update(E entity);
	boolean delete(UUID id);
}
