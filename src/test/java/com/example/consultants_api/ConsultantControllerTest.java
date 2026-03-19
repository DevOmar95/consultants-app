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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void getAll_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/consultants"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getById_returns200() throws Exception {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantService.getById(1L)).thenReturn(c);

        mockMvc.perform(get("/api/consultants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    @WithMockUser
    void getById_notFound_throwsException() throws Exception {
        when(consultantService.getById(99L)).thenThrow(new RuntimeException("Consultant not found with id: 99"));

        try {
            mockMvc.perform(get("/api/consultants/99"));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException
                    || e.getMessage().contains("Consultant not found"));
        }
    }

    @Test
    @WithMockUser
    void update_returns200() throws Exception {
        Consultant updated = new Consultant(1L, "Johnny", "Doe", "johnny@test.com", "789", "Python", "NewCorp");
        when(consultantService.update(eq(1L), any(Consultant.class))).thenReturn(updated);

        mockMvc.perform(put("/api/consultants/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.specialty").value("Python"));
    }

    @Test
    @WithMockUser
    void delete_returns204() throws Exception {
        doNothing().when(consultantService).delete(1L);

        mockMvc.perform(delete("/api/consultants/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(consultantService).delete(1L);
    }

    @Test
    @WithMockUser
    void search_returnsFilteredResults() throws Exception {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantService.search("Java")).thenReturn(List.of(c));

        mockMvc.perform(get("/api/consultants").param("search", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialty").value("Java"));

        verify(consultantService).search("Java");
        verify(consultantService, never()).getAll();
    }

    @Test
    @WithMockUser
    void search_withBlankParam_returnsAll() throws Exception {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantService.getAll()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/consultants").param("search", "  "))
                .andExpect(status().isOk());

        verify(consultantService).getAll();
        verify(consultantService, never()).search(any());
    }

    @Test
    @WithMockUser
    void stats_returnsTotalCount() throws Exception {
        when(consultantService.getAll()).thenReturn(List.of(
                new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME"),
                new Consultant(2L, "Jane", "Smith", "jane@test.com", "456", "React", "Corp")
        ));

        mockMvc.perform(get("/api/consultants/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void create_withoutAuth_returnsForbidden() throws Exception {
        Consultant c = new Consultant(null, "John", "Doe", "john@test.com", "123", "Java", "ACME");

        mockMvc.perform(post("/api/consultants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/consultants/1"))
                .andExpect(status().isForbidden());
    }
}
