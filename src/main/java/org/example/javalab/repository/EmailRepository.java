package org.example.javalab.repository;

import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.example.javalab.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface EmailRepository extends JpaRepository<Email,Long> {
    Email findByEmail(String email);

    Set<Email> findByEmailType(EmailType emailType);
}
