package org.example.javalab.repository;

import org.example.javalab.entity.Number;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumberRepository extends JpaRepository<Number,Long> {
    Number findByNumber(String number);
}
