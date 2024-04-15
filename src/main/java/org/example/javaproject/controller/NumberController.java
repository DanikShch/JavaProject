package org.example.javaproject.controller;

import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.service.NumberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NumberController {
    private static final String SUCCESS_MSG = "Success";
    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @PostMapping("/addNumber")
    public ResponseEntity<MessageDTO> addNumber(@RequestParam String number) {
        numberService.addNumber(number);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @GetMapping("/getNumbers")
    public ResponseEntity<List<NumberDTO>> getNumbers() {
        return new ResponseEntity<>(numberService.getNumbers(), HttpStatus.OK);
    }

    @PutMapping("/updateNumber")
    public ResponseEntity<MessageDTO> updateNumber(@RequestParam String number, String newNumber) {
        numberService.updateNumber(number, newNumber);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteNumber")
    public ResponseEntity<MessageDTO> deleteNumber(@RequestParam String number) {
        numberService.deleteNumber(number);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }
}
