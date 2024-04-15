package org.example.javaproject.repository;

import org.example.javaproject.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Set;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Email findByName(String name);

    //@Query("SELECT e FROM  Email e WHERE e.emailType.name = :emailType")
    @Query(value = "SELECT e.* FROM emails e INNER JOIN email_types et ON e.email_type_id = et.id WHERE et.name = :emailType", nativeQuery = true)
    Set<Email> findByEmailType(@Param("emailType") String emailType);
}
