package org.example.javaproject.controller;


import org.example.javaproject.component.CustomLogger;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.dto.RequestDTO;
import org.example.javaproject.service.CounterService;
import org.example.javaproject.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class RequestController {
    public static final String SUCCESS_MSG = "Success";
    public static final String COUNTER_MSG = "Counter = ";

    private final RequestService requestService;
    private final CustomLogger logger;
    private final CounterService counterService;

    public RequestController(RequestService requestService, CustomLogger logger, CounterService counterService) {
        this.requestService = requestService;
        this.logger = logger;
        this.counterService = counterService;
    }

    @PostMapping("/extractEmails")
    public ResponseEntity<List<EmailDTO>> extractEmails(@RequestParam String text) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        return new ResponseEntity<>(requestService.extractEmails(text), HttpStatus.OK);
    }

    @PostMapping("/extractNumbers")
    public ResponseEntity<List<NumberDTO>> extractNumbers(@RequestParam String text) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        return new ResponseEntity<>(requestService.extractPhoneNumbers(text), HttpStatus.OK);
    }

    @GetMapping("/getRequests")
    public ResponseEntity<List<RequestDTO>> getRequests(@RequestParam(required = false) String email) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        return new ResponseEntity<>(requestService.getRequests(email), HttpStatus.OK);
    }

    @PutMapping("/updateRequest")
    public ResponseEntity<MessageDTO> updateRequest(@RequestParam String request, String newRequest) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        requestService.updateRequest(request, newRequest);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteRequest")
    public ResponseEntity<MessageDTO> deleteRequest(@RequestParam String request) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        requestService.deleteRequest(request);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
