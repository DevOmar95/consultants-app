package com.example.consultants_api.repository;

import com.example.consultants_api.model.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

    @Query("SELECT c FROM Consultant c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.specialty) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.company) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Consultant> search(@Param("q") String q);
}