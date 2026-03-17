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
    void getById_notFound_throwsException() {
        when(consultantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consultantService.getById(99L));
    }

    @Test
    void update_updatesFieldsAndFiresEvent() {
        Consultant existing = new Consultant(1L, "John", "Doe", "john@test.com", "123", "Java", "ACME");
        Consultant updated = new Consultant(null, "Johnny", "Doe", "johnny@test.com", "789", "Python", "NewCorp");
        when(consultantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(consultantRepository.save(any(Consultant.class))).thenReturn(existing);

        Consultant result = consultantService.update(1L, updated);

        assertEquals("Johnny", result.getFirstName());
        assertEquals("Python", result.getSpecialty());
        verify(consultantEventProducer).sendUpdatedEvent(any(Consultant.class));
    }
}