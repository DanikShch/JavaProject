package org.example.javalab.repository;

import org.example.javalab.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository  extends JpaRepository<Request, Long> {
    Request findByText(String text);
}
