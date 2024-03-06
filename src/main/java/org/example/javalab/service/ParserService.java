package org.example.javalab.service;


import org.example.javalab.entity.EmailEntity;
import org.example.javalab.entity.NumberEntity;
import org.example.javalab.repository.EmailRepository;
import org.example.javalab.repository.NumberRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javalab.entity")
public class ParserService {
    NumberRepository numberRepository;
    EmailRepository emailRepository;
    public NumberEntity saveNumberEntity(NumberEntity numberEntity){
        return numberRepository.save(numberEntity);
    }
    public EmailEntity saveEmailEntity(EmailEntity emailEntity) { return emailRepository.save(emailEntity); }
    public ParserService(NumberRepository numberRepository, EmailRepository emailRepository) {
        this.numberRepository = numberRepository;
        this.emailRepository = emailRepository;
    }
    public List<String> extractPhoneNumbers(String text) {
        List<String> phoneNumbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\+?[0-9\\-]{7,}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            phoneNumbers.add(matcher.group());
        }
        for (String phoneNumber : phoneNumbers) {
            saveNumberEntity(new NumberEntity(phoneNumber));
        }
        return phoneNumbers;
    }

    public List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        for (String email : emails) {
            saveEmailEntity(new EmailEntity(email));
        }
        return emails;
    }
}
