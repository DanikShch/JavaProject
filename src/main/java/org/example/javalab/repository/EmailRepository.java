package org.example.javalab.repository;

import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Set;

public interface EmailRepository extends JpaRepository<Email,Long> {
    Email findByName(String name);

    @Query("SELECT e FROM  Email e WHERE e.emailType.name = :emailType")
    Set<Email> findByEmailType(@Param("emailType") String emailType);
}
