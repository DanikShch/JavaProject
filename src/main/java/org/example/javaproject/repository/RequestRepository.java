package org.example.javaproject.repository;

import org.example.javaproject.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository  extends JpaRepository<Request, Long> {
    Request findByText(String text);
}
