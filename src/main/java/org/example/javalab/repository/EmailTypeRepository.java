package org.example.javalab.repository;

import org.example.javalab.entity.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTypeRepository extends JpaRepository<EmailType, Long> {
    EmailType findByName(String name);
}
