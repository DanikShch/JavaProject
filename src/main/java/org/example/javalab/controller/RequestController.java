package org.example.javalab.controller;


import org.example.javalab.dto.EmailDTO;
import org.example.javalab.dto.MessageDTO;
import org.example.javalab.dto.NumberDTO;
import org.example.javalab.dto.RequestDTO;
import org.example.javalab.service.NumberService;
import org.example.javalab.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class RequestController {
    private static final String SUCCESS_MSG = "Success";
    private static final String FAILED_MSG = "Failed";
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/extractEmails")
    public ResponseEntity<List<EmailDTO>> extractEmails(@RequestParam String text){
        return new ResponseEntity<>(requestService.extractEmails(text), HttpStatus.OK);
    }

    @PostMapping("/extractNumbers")
    public ResponseEntity<List<NumberDTO>> extractNumbers(@RequestParam String text){
        return new ResponseEntity<>(requestService.extractPhoneNumbers(text), HttpStatus.OK);
    }
    @GetMapping("/getRequests")
    public ResponseEntity<List<RequestDTO>> getRequests(@RequestParam(required = false) String email){
        return new ResponseEntity<>(requestService.getRequests(email), HttpStatus.OK);
    }
    @PutMapping("/updateRequest")
    public ResponseEntity<MessageDTO> updateRequest(@RequestParam String request, String newRequest){
        if(requestService.updateRequest(request,newRequest)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
    @DeleteMapping("/deleteRequest")
    public ResponseEntity<MessageDTO> deleteRequest(@RequestParam String request){
        if(requestService.deleteRequest(request)){
            return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
        }
        else {
            return ResponseEntity.ok(new MessageDTO(FAILED_MSG));
        }
    }
}
