package org.example.javalab.controller;

import org.example.javalab.dto.DomainDTO;
import org.example.javalab.dto.MessageDTO;
import org.example.javalab.service.EmailTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailTypeController {
    private static final String SUCCESS_MSG = "Success";
    private static final String FAILED_MSG = "Failed";
    private final EmailTypeService emailTypeService;

    public EmailTypeController(EmailTypeService emailTypeService) {
        this.emailTypeService = emailTypeService;
    }
    @PostMapping("/addDomain")
    public ResponseEntity<MessageDTO> addDomain(@RequestParam String domain) {
        if (emailTypeService.addDomain(domain)) {
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        } else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
    @GetMapping("/getDomains")
    public ResponseEntity<List<DomainDTO>> getDomains(){
        return new ResponseEntity<>(emailTypeService.getDomains(), HttpStatus.OK);
    }
    @PutMapping("/updateDomain")
    public ResponseEntity<MessageDTO> updateDomain(@RequestParam String domain, String newDomain) {
        if(emailTypeService.updateDomain(domain,newDomain)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }

    @DeleteMapping("/deleteDomain")
    public ResponseEntity<MessageDTO> deleteDomain(@RequestParam String domain) {
        if(emailTypeService.deleteDomain(domain)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }


}
