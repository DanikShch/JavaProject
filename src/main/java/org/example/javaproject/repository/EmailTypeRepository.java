package org.example.javaproject.repository;

import org.example.javaproject.entity.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTypeRepository extends JpaRepository<EmailType, Long> {
    EmailType findByName(String name);
}
