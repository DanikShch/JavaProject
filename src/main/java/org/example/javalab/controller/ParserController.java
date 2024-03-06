package org.example.javalab.controller;

import org.example.javalab.dto.ListDTO;
import org.example.javalab.service.ParserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ParserController {
    private final ParserService service;

    public ParserController(ParserService service) {
        this.service = service;
    }
    @GetMapping("/numbers")
    public ListDTO process(@RequestParam String text){
        List<String> numbersList = service.extractPhoneNumbers(text);
        List<String> emailsList = service.extractEmails(text);
        return new ListDTO(numbersList,emailsList);
    }

}
