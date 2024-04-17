package org.example.javaproject.controller;

import org.example.javaproject.component.CustomLogger;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.CounterService;
import org.example.javaproject.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class EmailController {
    public static final String SUCCESS_MSG = "Success";

    private final EmailService emailService;

    private final CounterService counterService;

    private final CustomLogger logger;

    public EmailController(EmailService emailService, CounterService counterService, CustomLogger logger) {
        this.emailService = emailService;
        this.counterService = counterService;
        this.logger = logger;
    }

    @PostMapping("/addEmail")
    public ResponseEntity<MessageDTO> addEmail(@RequestParam String email) {
        logger.info("Counter = " + counterService.incrementAndGet());
        emailService.addEmail(email);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getEmails")
    public ResponseEntity<List<EmailDTO>> getEmails(@RequestParam(required = false) String domain) {
        logger.info("Counter = " + counterService.incrementAndGet());
        return new ResponseEntity<>(emailService.getEmails(domain), HttpStatus.OK);
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<MessageDTO> updateEmail(@RequestParam String email, String newEmail) {
        logger.info("Counter = " + counterService.incrementAndGet());
        emailService.updateEmail(email, newEmail);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<MessageDTO> deleteEmail(@RequestParam String email) {
        logger.info("Counter = " + counterService.incrementAndGet());
        emailService.deleteEmail(email);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @PostMapping("/addEmails")
    public ResponseEntity<MessageDTO> addEmails(@RequestBody List<EmailDTO> emails) {
        logger.info("Counter = " + counterService.incrementAndGet());
        emailService.addEmails(emails);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
