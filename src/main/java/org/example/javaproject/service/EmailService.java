package org.example.javaproject.service;


import jakarta.transaction.Transactional;
import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
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
public class EmailService {
    public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
    public static final String EMAIL_TYPE_REGEX = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
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
    public void addEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX);
        Matcher emailTypeMatcher = emailTypePattern.matcher(email);
        EmailType emailType;
        if (matcher.find() && emailTypeMatcher.find()) {
            email = matcher.group();
            if (emailRepository.findByName(email) != null) {
                throw new ServiceException("Email already exists");
            } else {
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
        } else {
            throw new ServiceException("Wrong email");
        }
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
                throw new ServiceException("Wrong domain");
            }
        }
        for (Email email : emails) {
            cache.put(email.getName(), email);
            emailNames.add(new EmailDTO(email.getId(), email.getName()));
        }
        return emailNames;
    }

    @Transactional
    public void updateEmail(String email, String newEmail) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) {
            throw new ServiceException("Wrong new email");
        }
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
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
        } else {
            throw new ServiceException("Wrong old email");
        }
    }

    @Transactional
    public void deleteEmail(String email) {
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            for (Request request : emailEntity.getRequests()) {
                request.getEmails().remove(emailEntity);
            }
            emailRepository.delete(emailEntity);
            cache.remove(email);
        } else {
            throw new ServiceException("Cant delete email");
        }
    }

    @Transactional
    public void addEmails(List<EmailDTO> emails) {
        emails.stream().map(EmailDTO::getEmail).forEach(this::addEmail);
    }
}
