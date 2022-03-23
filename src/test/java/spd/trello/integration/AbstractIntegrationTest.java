package spd.trello.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.perent.Domain;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public class AbstractIntegrationTest<E extends Domain> implements CommonIntegrationTest<E> {

    private final MockMvc mockMvc;

    public AbstractIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public MvcResult create(String urlTemplate, E entity) throws Exception {
        return mockMvc.perform(
                post(urlTemplate, entity)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity))
        ).andReturn();
    }

    @Override
    public MvcResult findAll(String urlTemplate) throws Exception {
        return mockMvc.perform(get(urlTemplate)).andReturn();
    }

    @Override
    public MvcResult findById(String urlTemplate, UUID id) throws Exception {
        return mockMvc.perform(
                get(urlTemplate + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Override
    public MvcResult deleteById(String urlTemplate, UUID id) throws Exception {
        return mockMvc.perform(
                delete(urlTemplate + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Override
    public MvcResult update(String urlTemplate, UUID id, E entity) throws Exception {
        return mockMvc.perform(
                put(urlTemplate + "/{id}", id, entity)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity))
        ).andReturn();
    }


    public Object getValue(MvcResult mvcResult, String jsonPath) throws UnsupportedEncodingException {
        return JsonPath.read(mvcResult.getResponse().getContentAsString(), jsonPath);
    }
}
