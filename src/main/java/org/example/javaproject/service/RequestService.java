package org.example.javaproject.service;

import jakarta.transaction.Transactional;
import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.dto.RequestDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
import org.example.javaproject.entity.Number;
import org.example.javaproject.entity.Request;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.EmailRepository;
import org.example.javaproject.repository.EmailTypeRepository;
import org.example.javaproject.repository.NumberRepository;
import org.example.javaproject.repository.RequestRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javaproject.entity")
public class RequestService {
    private static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
    private static final String NUMBER_REGEX = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?) {1,2}\\d{1,9}\\b";
    private static final String EMAIL_TYPE_REGEX = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    Cache cache;
    RequestRepository requestRepository;
    EmailRepository emailRepository;
    EmailTypeRepository emailTypeRepository;
    NumberRepository numberRepository;

    public RequestService(RequestRepository requestRepository, EmailRepository emailRepository,
                          EmailTypeRepository emailTypeRepository, NumberRepository numberRepository, Cache cache) {
        this.requestRepository = requestRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
        this.numberRepository = numberRepository;
        this.cache = cache;
    }

    @Transactional
    public List<NumberDTO> extractPhoneNumbers(String text) {
        List<NumberDTO> phoneNumbers = new ArrayList<>();
        Pattern pattern = Pattern.compile(NUMBER_REGEX);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String number = matcher.group();
            if (numberRepository.findByName(number) == null) {
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
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            emails.add(new Email(matcher.group()));
        }
        if (request == null) {
            request = new Request(text);
            requestRepository.save(request);
        }
        for (Email email : emails) {
            parsedEmails.add(new EmailDTO(email.getName()));
            Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher emailTypeMatcher = emailTypePattern.matcher(email.getName());
            if (emailRepository.findByName(email.getName()) != null) {
                email = emailRepository.findByName(email.getName());
                email.getRequests().add(request);
            }
            if (emailTypeMatcher.find()) {
                String typeName = emailTypeMatcher.group();
                EmailType emailType = emailTypeRepository.findByName(typeName);
                if (emailType == null) {
                    emailType = new EmailType(typeName);
                    emailTypeRepository.save(emailType);
                }
                emailType.getEmails().add(email);
                email.setEmailType(emailType);
            }
            email.getRequests().add(request);
            request.getEmails().add(email);
            cache.put(email.getName(), email);
        }
        cache.put(text, request);
        return parsedEmails;
    }

    @Transactional
    public List<RequestDTO> getRequests(String email) {
        List<RequestDTO> requestDTOS = new ArrayList<>();
        List<Request> requests;
        Email emailEntity;
        if (email == null) {
            requests = requestRepository.findAll();
        } else {
            if (cache.contains(email)) {
                emailEntity = (Email) cache.get(email);
            } else {
                emailEntity = emailRepository.findByName(email);
            }
            if (emailEntity != null) {
                requests = new ArrayList<>(emailEntity.getRequests());
                cache.put(email, emailEntity);
            } else {
                return null;
            }
        }
        for (Request request : requests) {
            requestDTOS.add(new RequestDTO(request.getText()));
        }
        return requestDTOS;
    }

    @Transactional
    public void updateRequest(String request, String newRequest) {
        Request requestEntity = requestRepository.findByText(request);
        if (requestEntity == null || requestRepository.findByText(newRequest) != null) {
            throw new ServiceException("Invalid request text");
        } else {
            for (Email email : requestEntity.getEmails()) {
                email.getRequests().remove(requestEntity);
            }
            requestEntity.setText(newRequest);
            cache.remove(request);
            cache.put(newRequest, requestEntity);
        }
    }

    @Transactional
    public void deleteRequest(String request) {
        Request requestEntity = requestRepository.findByText(request);
        if (requestEntity != null) {
            for (Email email : requestEntity.getEmails()) {
                email.getRequests().remove(requestEntity);
            }
            requestRepository.delete(requestEntity);
            cache.remove(request);
        } else {
            throw new ServiceException("Invalid request");
        }
    }
}

