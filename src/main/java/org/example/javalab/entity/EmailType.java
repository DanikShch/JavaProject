package org.example.javalab.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "email_types")
public class EmailType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long emailTypeId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "emailType")
    private Set<Email> emails = new HashSet<>();

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String typeName) {
        this.name = typeName;
    }

    public EmailType() {
    }

    public EmailType(String name, Set<Email> emails) {
        this.name = name;
        this.emails = emails;
    }
    public EmailType(String name) {
        this.name = name;
    }
}
