package org.example.javaproject.service;

import jakarta.transaction.Transactional;
import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.DomainDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
import org.example.javaproject.entity.Request;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.EmailRepository;
import org.example.javaproject.repository.EmailTypeRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javaproject.entity")
public class EmailTypeService {
    Cache cache;
    EmailTypeRepository emailTypeRepository;
    EmailRepository emailRepository;
    public static final String EMAIL_TYPE_REGEX = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";

    public EmailTypeService(EmailTypeRepository emailTypeRepository, EmailRepository emailRepository, Cache cache) {
        this.emailTypeRepository = emailTypeRepository;
        this.emailRepository = emailRepository;
        this.cache = cache;
    }

    private boolean checkDomain(String text) {
        Pattern emailPattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.find();
    }

    @Transactional
    public void addDomain(String domain) {
        if (checkDomain(domain)) {
            if (emailTypeRepository.findByName(domain) != null) {
                throw new ServiceException("Email already exists");
            }
            EmailType emailType = new EmailType(domain);
            emailTypeRepository.save(emailType);
            cache.put(domain, emailType);
        } else {
            throw new ServiceException("Invalid domain");
        }
    }

    @Transactional
    public void updateDomain(String domain, String newDomain) {
        EmailType emailType = emailTypeRepository.findByName(domain);
        if (!checkDomain(newDomain) || emailType == null || emailTypeRepository.findByName(newDomain) != null) {
            throw new ServiceException("Invalid or non existing domain");
        }
        for (Email email : emailType.getEmails()) {
            for (Request request : email.getRequests()) {
                request.getEmails().remove(email);
            }
            cache.remove(email.getName());
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailType.getEmails().clear();
        emailType.setName(newDomain);
        cache.remove(domain);
        cache.put(newDomain, emailType);
    }

    @Transactional
    public void deleteDomain(String domain) {
        EmailType emailType = emailTypeRepository.findByName(domain);
        if (emailType == null) {
            throw new ServiceException("Email doesnt exists");
        }
        for (Email email : emailType.getEmails()) {
            for (Request request : email.getRequests()) {
                request.getEmails().remove(email);
            }
            cache.remove(email.getName());
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailTypeRepository.delete(emailType);
        cache.remove(domain);
    }

    @Transactional
    public List<DomainDTO> getDomains() {
        List<EmailType> emailTypes = emailTypeRepository.findAll();
        List<DomainDTO> domains = new ArrayList<>();
        for (EmailType emailType : emailTypes) {
            domains.add(new DomainDTO(emailType.getName()));
            cache.put(emailType.getName(), emailType);
        }
       return domains;
    }

}
