package spd.trello.integration;

import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.perent.Domain;

import java.util.UUID;

public interface CommonIntegrationTest<E extends Domain> {
    MvcResult create(String urlTemplate, E entity) throws Exception;

    MvcResult findAll(String urlTemplate) throws Exception;

    MvcResult findById(String urlTemplate, UUID id) throws Exception;

    MvcResult deleteById(String urlTemplate, UUID id) throws Exception;

    MvcResult update(String urlTemplate, UUID id, E entity) throws Exception;
}
