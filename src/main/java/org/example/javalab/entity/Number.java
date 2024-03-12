package org.example.javalab.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "numbers")
public class Number {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number_name")
    private String numberName;

    public Number() {
    }
    public Number(String numberName) {
        this.numberName = numberName;
    }

    public String getNumberName() {
        return numberName;
    }

    public void setNumberName(String number) {
        this.numberName = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
