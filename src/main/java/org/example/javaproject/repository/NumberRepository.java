package org.example.javaproject.repository;

import org.example.javaproject.entity.Number;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumberRepository extends JpaRepository<Number, Long> {
    Number findByName(String name);
}
