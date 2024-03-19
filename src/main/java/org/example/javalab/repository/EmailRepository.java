package org.example.javalab.repository;

import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Set;

public interface EmailRepository extends JpaRepository<Email,Long> {
    Email findByName(String name);

    Set<Email> findByEmailType(EmailType emailType);
}
