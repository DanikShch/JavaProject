package org.example.javaproject.controller;

import org.example.javaproject.component.CustomLogger;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.service.CounterService;
import org.example.javaproject.service.NumberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NumberController {
    public static final String SUCCESS_MSG = "Success";
    public static final String COUNTER_MSG = "Counter = ";
    private final NumberService numberService;
    private final CustomLogger logger;
    private final CounterService counterService;

    public NumberController(NumberService numberService, CustomLogger logger, CounterService counterService) {
        this.numberService = numberService;
        this.logger = logger;
        this.counterService = counterService;
    }

    @PostMapping("/addNumber")
    public ResponseEntity<MessageDTO> addNumber(@RequestParam String number) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        numberService.addNumber(number);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getNumbers")
    public ResponseEntity<List<NumberDTO>> getNumbers() {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        return new ResponseEntity<>(numberService.getNumbers(), HttpStatus.OK);
    }

    @PutMapping("/updateNumber")
    public ResponseEntity<MessageDTO> updateNumber(@RequestParam String number, String newNumber) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        numberService.updateNumber(number, newNumber);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteNumber")
    public ResponseEntity<MessageDTO> deleteNumber(@RequestParam String number) {
        logger.info(COUNTER_MSG + counterService.incrementAndGet());
        numberService.deleteNumber(number);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
