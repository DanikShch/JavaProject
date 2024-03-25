package org.example.javalab.service;


import jakarta.transaction.Transactional;
import org.example.javalab.component.Cache;
import org.example.javalab.dto.EmailDTO;
import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
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
public class EmailService {
    private static String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
    private static String emailTypeRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    Cache cache;
    NumberRepository numberRepository;
    EmailRepository emailRepository;
    EmailTypeRepository emailTypeRepository;
    RequestRepository requestRepository;

    public EmailService(NumberRepository numberRepository, EmailRepository emailRepository, EmailTypeRepository emailTypeRepository, RequestRepository requestRepository, Cache cache) {
        this.numberRepository = numberRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
        this.requestRepository = requestRepository;
        this.cache = cache;
    }
    
    @Transactional
    public boolean addEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        Pattern emailTypePattern = Pattern.compile(emailTypeRegex);
        Matcher emailTypeMatcher = emailTypePattern.matcher(email);
        EmailType emailType;
        if (matcher.find() && emailTypeMatcher.find()) {
            String domain = emailTypeMatcher.group();
            Email emailEntity = new Email(email);
            emailRepository.save(emailEntity);
            if (emailTypeRepository.findByName(domain) == null) {
                emailType = new EmailType(domain);
                emailTypeRepository.save(emailType);
            } else {
                emailType = emailTypeRepository.findByName(domain);
            }
            emailType.getEmails().add(emailEntity);
            emailEntity.setEmailType(emailType);
            cache.put(emailEntity.getName(), emailEntity);
            cache.put(emailType.getName(), emailType);
        }
        return false;
    }

    @Transactional
    public List<EmailDTO> getEmails(String domain) {
        List<EmailDTO> emailNames = new ArrayList<>();
        List<Email> emails;
        EmailType emailType;
        if (domain == null) {
            emails = emailRepository.findAll();
        } else {
            if (cache.contains(domain)) {
                emailType = (EmailType) cache.get(domain);
            } else {
                emailType = emailTypeRepository.findByName(domain);
            }
            if (emailType != null) {
                emails = new ArrayList<>(emailRepository.findByEmailType(domain));
                cache.put(emailType.getName(), emailType);
            } else {
                return null;
            }
        }
        for (Email email : emails) {
            cache.put(email.getName(), email);
            emailNames.add(new EmailDTO(email.getName()));
        }
        return emailNames;
    }

    @Transactional
    public boolean updateEmail(String email, String newEmail) {
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) {
            return false;
        }
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            Pattern emailTypePattern = Pattern.compile(emailTypeRegex, Pattern.CASE_INSENSITIVE);
            Matcher emailTypeMatcher = emailTypePattern.matcher(newEmail);
            if (emailTypeMatcher.find()) {
                String domain = emailTypeMatcher.group();
                EmailType emailType = emailTypeRepository.findByName(domain);
                if (emailType == null) {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                }
                    emailEntity.setName(newEmail);
                    emailEntity.setEmailType(emailType);
                cache.put(emailType.getName(), emailType);
            }
            cache.remove(email);
            cache.put(emailEntity.getName(), emailEntity);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteEmail(String email) {
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            for (Request request : emailEntity.getRequests()) {
                request.getEmails().remove(emailEntity);
            }
            emailRepository.delete(emailEntity);
            cache.remove(email);
            return true;
        }
        return false;
    }
}
