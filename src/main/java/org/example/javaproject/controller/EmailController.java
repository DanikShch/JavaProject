package org.example.javaproject.controller;

import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class EmailController {
    public static final String SUCCESS_MSG = "Success";

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/addEmail")
    public ResponseEntity<MessageDTO> addEmail(@RequestParam String email) {
        service.addEmail(email);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getEmails")
    public ResponseEntity<List<EmailDTO>> getEmails(@RequestParam(required = false) String domain) {
        return new ResponseEntity<>(service.getEmails(domain), HttpStatus.OK);
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<MessageDTO> updateEmail(@RequestParam String email, String newEmail) {
        service.updateEmail(email, newEmail);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<MessageDTO> deleteEmail(@RequestParam String email) {
        service.deleteEmail(email);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
