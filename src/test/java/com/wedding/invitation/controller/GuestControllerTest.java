package com.wedding.invitation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedding.invitation.model.GuestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllGuests_returnsOkAndJsonArray() throws Exception {
        mockMvc.perform(get("/api/guests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createGuest_withValidData_returns201AndGuest() throws Exception {
        GuestRequest request = new GuestRequest("Иван Иванов", true, "Ждём с нетерпением!");

        ResultActions result = mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.attending").value(true))
                .andExpect(jsonPath("$.comment").value("Ждём с нетерпением!"))
                .andExpect(jsonPath("$.createdAt").exists());

        result.andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createGuest_withValidDataNoComment_returns201() throws Exception {
        GuestRequest request = new GuestRequest("Мария Петрова", false, null);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Мария Петрова"))
                .andExpect(jsonPath("$.attending").value(false))
                .andExpect(jsonPath("$.comment").isEmpty());
    }

    @Test
    void createGuest_withoutName_returns400AndValidationError() throws Exception {
        String body = "{\"attending\":true,\"comment\":\"тест\"}";

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void createGuest_withoutAttending_returns400AndValidationError() throws Exception {
        String body = "{\"name\":\"Иван\",\"comment\":\"тест\"}";

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.attending").exists());
    }

    @Test
    void createGuest_withBlankName_returns400() throws Exception {
        String body = "{\"name\":\"\",\"attending\":true,\"comment\":null}";

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getAllGuests_afterCreate_returnsListWithGuest() throws Exception {
        GuestRequest request = new GuestRequest("Тест Гость", true, "Комментарий");

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/guests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.name=='Тест Гость')]").exists());
    }
}
