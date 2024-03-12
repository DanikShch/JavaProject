package org.example.javalab.repository;

import org.example.javalab.entity.Email;
import org.example.javalab.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RequestRepository  extends JpaRepository<Request,Long> {
    Request findByRequestText(String requestText);
}
