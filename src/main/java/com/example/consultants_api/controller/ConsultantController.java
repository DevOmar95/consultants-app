package com.example.consultants_api.controller;

import com.example.consultants_api.model.Consultant;
import com.example.consultants_api.service.ConsultantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ConsultantController {

    private final ConsultantService consultantService;

    @GetMapping
    public List<Consultant> getAll(@RequestParam(value = "search", required = false) String search) {
        if (search != null && !search.isBlank()) {
            return consultantService.search(search);
        }
        return consultantService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultant> getById(@PathVariable Long id) {
        return ResponseEntity.ok(consultantService.getById(id));
    }

    @PostMapping
    public Consultant create(@Valid @RequestBody Consultant consultant) {
        return consultantService.create(consultant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultant> update(@PathVariable Long id, @Valid @RequestBody Consultant updated) {
        return ResponseEntity.ok(consultantService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consultantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of("total", consultantService.getAll().size());
    }
}