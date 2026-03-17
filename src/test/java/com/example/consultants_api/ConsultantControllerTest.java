package com.example.consultants_api;

import com.example.consultants_api.controller.ConsultantController;
import com.example.consultants_api.model.Consultant;
import com.example.consultants_api.security.JwtUtil;
import com.example.consultants_api.security.UserDetailsServiceImpl;
import com.example.consultants_api.service.ConsultantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultantController.class)
class ConsultantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultantService consultantService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAll_returns200() throws Exception {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantService.getAll()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/consultants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser
    void create_returns200() throws Exception {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantService.create(any(Consultant.class))).thenReturn(c);

        mockMvc.perform(post("/api/consultants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getAll_withoutAuth_returns403() throws Exception {
        mockMvc.perform(get("/api/consultants"))
                .andExpect(status().isUnauthorized());
    }
}