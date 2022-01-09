package spd.trello.repository;

import java.util.List;
import java.util.UUID;

public interface InterfaceRepository<E> {
	E findById(UUID id);
	List<E> findAll();
	void create(E entity);
	E update(E entity);
	boolean delete(UUID id);
}
