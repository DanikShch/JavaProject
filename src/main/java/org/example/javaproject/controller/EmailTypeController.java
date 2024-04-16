package org.example.javaproject.controller;

import org.example.javaproject.dto.DomainDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.EmailTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailTypeController {
    public static final String SUCCESS_MSG = "Success";
    private final EmailTypeService emailTypeService;

    public EmailTypeController(EmailTypeService emailTypeService) {
        this.emailTypeService = emailTypeService;
    }

    @PostMapping("/addDomain")
    public ResponseEntity<MessageDTO> addDomain(@RequestParam String domain) {
        emailTypeService.addDomain(domain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getDomains")
    public ResponseEntity<List<DomainDTO>> getDomains() {
        return new ResponseEntity<>(emailTypeService.getDomains(), HttpStatus.OK);
    }

    @PutMapping("/updateDomain")
    public ResponseEntity<MessageDTO> updateDomain(@RequestParam String domain, String newDomain) {
        emailTypeService.updateDomain(domain, newDomain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteDomain")
    public ResponseEntity<MessageDTO> deleteDomain(@RequestParam String domain) {
        emailTypeService.deleteDomain(domain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
