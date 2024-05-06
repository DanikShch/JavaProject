package org.example.javaproject.controller;

import org.example.javaproject.component.CustomLogger;
import org.example.javaproject.dto.DomainDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.CounterService;
import org.example.javaproject.service.EmailTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173/")
@RestController
public class EmailTypeController {
    public static final String SUCCESS_MSG = "Success";
    public static final String COUNTER_MSG = "Counter = ";
    private final EmailTypeService emailTypeService;
    private final CustomLogger logger;
    private final CounterService counterService;

    public EmailTypeController(EmailTypeService emailTypeService, CustomLogger logger, CounterService counterService) {
        this.emailTypeService = emailTypeService;
        this.logger = logger;
        this.counterService = counterService;
    }

    @PostMapping("/addDomain")
    public ResponseEntity<MessageDTO> addDomain(@RequestParam String domain) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        emailTypeService.addDomain(domain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getDomains")
    public ResponseEntity<List<DomainDTO>> getDomains() {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        return new ResponseEntity<>(emailTypeService.getDomains(), HttpStatus.OK);
    }

    @PutMapping("/updateDomain")
    public ResponseEntity<MessageDTO> updateDomain(@RequestParam String domain, String newDomain) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        emailTypeService.updateDomain(domain, newDomain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteDomain")
    public ResponseEntity<MessageDTO> deleteDomain(@RequestParam String domain) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        emailTypeService.deleteDomain(domain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
