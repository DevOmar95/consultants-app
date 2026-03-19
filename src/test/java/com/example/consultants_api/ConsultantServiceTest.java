package com.example.consultants_api;

import com.example.consultants_api.kafka.ConsultantEventProducer;
import com.example.consultants_api.model.Consultant;
import com.example.consultants_api.repository.ConsultantRepository;
import com.example.consultants_api.service.ConsultantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultantServiceTest {

    @Mock
    private ConsultantRepository consultantRepository;

    @Mock
    private ConsultantEventProducer consultantEventProducer;

    @InjectMocks
    private ConsultantService consultantService;

    @Test
    void getAll_returnsAllConsultants() {
        Consultant c1 = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        Consultant c2 = new Consultant(2L, "Jane", "Smith", "jane@test.com", "456", "React", "Corp");
        when(consultantRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Consultant> result = consultantService.getAll();

        assertEquals(2, result.size());
        verify(consultantRepository).findAll();
    }

    @Test
    void getAll_returnsEmptyList() {
        when(consultantRepository.findAll()).thenReturn(Collections.emptyList());

        List<Consultant> result = consultantService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void create_savesAndFiresEvent() {
        Consultant consultant = new Consultant(null, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        Consultant saved = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantRepository.save(any(Consultant.class))).thenReturn(saved);

        Consultant result = consultantService.create(consultant);

        assertEquals(1L, result.getId());
        verify(consultantEventProducer).sendCreatedEvent(saved);
    }

    @Test
    void delete_firesDeletedEvent() {
        consultantService.delete(1L);

        verify(consultantRepository).deleteById(1L);
        verify(consultantEventProducer).sendDeletedEvent(1L);
    }

    @Test
    void getById_found_returnsConsultant() {
        Consultant c = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantRepository.findById(1L)).thenReturn(Optional.of(c));

        Consultant result = consultantService.getById(1L);

        assertEquals("John", result.getFirstName());
        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    void getById_notFound_throwsException() {
        when(consultantRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> consultantService.getById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void update_updatesFieldsAndFiresEvent() {
        Consultant existing = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        Consultant updated = new Consultant(null, "Johnny", "Doe", "johnny@test.com", "789", "Python", "NewCorp");
        when(consultantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(consultantRepository.save(any(Consultant.class))).thenReturn(existing);

        Consultant result = consultantService.update(1L, updated);

        assertEquals("Johnny", result.getFirstName());
        assertEquals("johnny@test.com", result.getEmail());
        assertEquals("789", result.getPhone());
        assertEquals("Python", result.getSpecialty());
        assertEquals("NewCorp", result.getCompany());
        verify(consultantEventProducer).sendUpdatedEvent(any(Consultant.class));
    }

    @Test
    void update_notFound_throwsException() {
        Consultant updated = new Consultant(null, "Johnny", "Doe", "johnny@test.com", "789", "Python", "NewCorp");
        when(consultantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultantService.update(99L, updated));
        verify(consultantRepository, never()).save(any());
        verify(consultantEventProducer, never()).sendUpdatedEvent(any());
    }

    @Test
    void search_returnsMatchingConsultants() {
        Consultant c1 = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        when(consultantRepository.search("Java")).thenReturn(List.of(c1));

        List<Consultant> result = consultantService.search("Java");

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getSpecialty());
        verify(consultantRepository).search("Java");
    }

    @Test
    void search_noResults_returnsEmptyList() {
        when(consultantRepository.search("xyz")).thenReturn(Collections.emptyList());

        List<Consultant> result = consultantService.search("xyz");

        assertTrue(result.isEmpty());
    }
}
