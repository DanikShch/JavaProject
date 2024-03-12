package org.example.javalab.controller;

import org.example.javalab.dto.MessageDTO;
import org.example.javalab.dto.NumberDTO;
import org.example.javalab.service.NumberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NumberController {
    private static final String SUCCESS_MSG = "Success";
    private static final String FAILED_MSG = "Failed";
    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }
    @PostMapping("/addNumber")
    public ResponseEntity<MessageDTO> addNumber(@RequestParam String number) {
        if (numberService.addNumber(number)) {
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        } else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
    @GetMapping("/getNumbers")
    public ResponseEntity<List<NumberDTO>> getNumbers(){
        return new ResponseEntity<>(numberService.getNumbers(), HttpStatus.OK);
    }
    @PutMapping("/updateNumber")
    public ResponseEntity<MessageDTO> updateNumber(@RequestParam String number, String newNumber) {
        if(numberService.updateNumber(number,newNumber)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }

    @DeleteMapping("/deleteNumber")
    public ResponseEntity<MessageDTO> deleteNumber(@RequestParam String number) {
        if(numberService.deleteNumber(number)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
}
