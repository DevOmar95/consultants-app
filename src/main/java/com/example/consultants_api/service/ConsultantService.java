package com.example.consultants_api.service;

import com.example.consultants_api.kafka.ConsultantEventProducer;
import com.example.consultants_api.model.Consultant;
import com.example.consultants_api.repository.ConsultantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final ConsultantEventProducer consultantEventProducer;

    public ConsultantService(ConsultantRepository consultantRepository,
                             @org.springframework.beans.factory.annotation.Autowired(required = false)
                             ConsultantEventProducer consultantEventProducer) {
        this.consultantRepository = consultantRepository;
        this.consultantEventProducer = consultantEventProducer;
    }

    public List<Consultant> getAll() {
        return consultantRepository.findAll();
    }

    public List<Consultant> search(String q) {
        return consultantRepository.search(q);
    }

    public Consultant getById(Long id) {
        return consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found with id: " + id));
    }

    public Consultant create(Consultant consultant) {
        Consultant saved = consultantRepository.save(consultant);
        if (consultantEventProducer != null) {
            consultantEventProducer.sendCreatedEvent(saved);
        }
        return saved;
    }

    public Consultant update(Long id, Consultant updated) {
        Consultant existing = getById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setSpecialty(updated.getSpecialty());
        existing.setCompany(updated.getCompany());
        Consultant saved = consultantRepository.save(existing);
        if (consultantEventProducer != null) {
            consultantEventProducer.sendUpdatedEvent(saved);
        }
        return saved;
    }

    public void delete(Long id) {
        consultantRepository.deleteById(id);
        if (consultantEventProducer != null) {
            consultantEventProducer.sendDeletedEvent(id);
        }
    }
}