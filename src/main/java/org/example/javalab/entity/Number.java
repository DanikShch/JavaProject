package org.example.javalab.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "numbers")
public class Number {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    public Number() {
    }
    public Number(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String number) {
        this.name = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
