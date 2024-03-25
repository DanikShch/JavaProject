package org.example.javalab.controller;

import org.example.javalab.dto.EmailDTO;
import org.example.javalab.dto.MessageDTO;
import org.example.javalab.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class EmailController {
    private static final String SUCCESS_MSG = "Success";
    private static final String FAILED_MSG = "Failed";
    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/addEmail")
    public ResponseEntity<MessageDTO> addEmail(@RequestParam String email) {
        if (service.addEmail(email)) {
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        } else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }

    @GetMapping("/getEmails")
    public ResponseEntity<List<EmailDTO>> getEmails(@RequestParam(required = false) String domain) {
        return new ResponseEntity<List<EmailDTO>>(service.getEmails(domain), HttpStatus.OK);
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<MessageDTO> updateEmail(@RequestParam String email, String newEmail) {
        if (service.updateEmail(email, newEmail)) {
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        } else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<MessageDTO> deleteEmail(@RequestParam String email) {
        if (service.deleteEmail(email)) {
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        } else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
}
