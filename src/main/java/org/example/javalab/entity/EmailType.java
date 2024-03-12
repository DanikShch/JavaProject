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

    @Column(name = "type_name")
    private String typeName;

    @OneToMany(mappedBy = "emailType")
    private Set<Email> emails = new HashSet<>();

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public EmailType() {
    }

    public EmailType(String typeName, Set<Email> emails) {
        this.typeName = typeName;
        this.emails = emails;
    }
    public EmailType(String typeName) {
        this.typeName = typeName;
    }
}
