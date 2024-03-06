package org.example.javalab.repository;

import org.example.javalab.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity,Long> {
}
