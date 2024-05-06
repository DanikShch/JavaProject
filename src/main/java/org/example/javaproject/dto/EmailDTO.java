package org.example.javaproject.dto;

public class EmailDTO {

    Long id;
    String email;

    public EmailDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailDTO(String email) {
        this.email = email;
    }

    public EmailDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
