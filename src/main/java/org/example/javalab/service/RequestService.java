package org.example.javalab.service;

import jakarta.transaction.Transactional;
import org.example.javalab.dto.EmailDTO;
import org.example.javalab.dto.NumberDTO;
import org.example.javalab.dto.RequestDTO;
import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.example.javalab.entity.Number;
import org.example.javalab.entity.Request;
import org.example.javalab.repository.EmailRepository;
import org.example.javalab.repository.EmailTypeRepository;
import org.example.javalab.repository.NumberRepository;
import org.example.javalab.repository.RequestRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javalab.entity")
public class RequestService {
    private static String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
    private static String numberRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?){1,2}\\d{1,9}\\b";
    private static String emailTypeRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    RequestRepository requestRepository;
    EmailRepository emailRepository;
    EmailTypeRepository emailTypeRepository;
    NumberRepository numberRepository;

    public RequestService(RequestRepository requestRepository, EmailRepository emailRepository,
                          EmailTypeRepository emailTypeRepository, NumberRepository numberRepository) {
        this.requestRepository = requestRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
        this.numberRepository = numberRepository;
    }

    @Transactional
    public List<NumberDTO> extractPhoneNumbers(String text) {
        List<NumberDTO> phoneNumbers = new ArrayList<>();
        Pattern pattern = Pattern.compile(numberRegex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String number = matcher.group();
            if(numberRepository.findByName(number)==null){
                numberRepository.save(new Number(number));
            }
            phoneNumbers.add(new NumberDTO(number));
        }
        return phoneNumbers;
    }
    @Transactional
    public List<EmailDTO> extractEmails(String text) {
        List<Email> emails = new ArrayList<>();
        List<EmailDTO> parsedEmails = new ArrayList<>();
        Request request = requestRepository.findByText(text);
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            emails.add(new Email(matcher.group()));
        }
        if(request == null){
            request = new Request(text);
            requestRepository.save(request);
        }
        for (Email email : emails) {
            parsedEmails.add(new EmailDTO(email.getName()));
            Pattern emailTypePattern = Pattern.compile(emailTypeRegex, Pattern.CASE_INSENSITIVE);
            Matcher emailTypeMatcher = emailTypePattern.matcher(email.getName());
            if(emailRepository.findByName(email.getName()) != null){
                email = emailRepository.findByName(email.getName());
                email.getRequests().add(request);
            }
            if(emailTypeMatcher.find()){
                String typeName = emailTypeMatcher.group();
                EmailType emailType = emailTypeRepository.findByName(typeName);
                if(emailType == null){
                    emailType = new EmailType(typeName);
                    emailTypeRepository.save(emailType);
                }
                emailType.getEmails().add(email);
                email.setEmailType(emailType);
            }
            email.getRequests().add(request);
            request.getEmails().add(email);
        }
        return parsedEmails;
    }
    @Transactional
    public List<RequestDTO> getRequests(String email){
        List<RequestDTO> requestDTOS = new ArrayList<>();
        List<Request> requests;
        if(email==null){
            requests = requestRepository.findAll();
        }
        else{
            Email emailEntity = emailRepository.findByName(email);
            if(emailEntity!=null){
                requests = new ArrayList<>(emailEntity.getRequests());
            }
            else {
                return null;
            }
        }
        for(Request request : requests){
            requestDTOS.add(new RequestDTO(request.getText()));
        }
        return requestDTOS;
    }

    @Transactional
    public boolean updateRequest(String request, String newRequest) {
        Request requestEntity = requestRepository.findByText(request);
        if(requestEntity==null||requestRepository.findByText(newRequest)!=null)
            return false;
        for(Email email : requestEntity.getEmails())
        {
            email.getRequests().remove(requestEntity);
        }
        requestEntity.setText(newRequest);
        return true;
    }

    @Transactional
    public boolean deleteRequest(String request) {
        Request requestEntity = requestRepository.findByText(request);
        if (requestEntity != null) {
            for(Email email : requestEntity.getEmails()){
                email.getRequests().remove(requestEntity);
            }
            requestRepository.delete(requestEntity);
            return true;
        }
        return false;
    }
}

