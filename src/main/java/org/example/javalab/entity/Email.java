package org.example.javalab.entity;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email_name")
    private String emailName;

    @ManyToOne
    @JoinColumn(name = "email_type_id")
    private EmailType emailType;
    @ManyToMany(mappedBy = "emails")
    private Set<Request> requests = new HashSet<>();

    public Email() {
    }
    public Email(String emailName) {
        this.emailName = emailName;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    public Email(String emailName, EmailType emailType) {
        this.emailName = emailName;
        this.emailType = emailType;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailName() {
        return emailName;
    }

    public void setEmailName(String email) {
        this.emailName = email;
    }
}
