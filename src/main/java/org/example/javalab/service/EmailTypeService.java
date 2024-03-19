package org.example.javalab.service;

import jakarta.transaction.Transactional;
import org.example.javalab.component.Cache;
import org.example.javalab.dto.DomainDTO;
import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.example.javalab.entity.Request;
import org.example.javalab.repository.EmailRepository;
import org.example.javalab.repository.EmailTypeRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javalab.entity")
public class EmailTypeService {
    Cache cache;
    EmailTypeRepository emailTypeRepository;
    EmailRepository emailRepository;
    private static String emailTypeRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";

    public EmailTypeService(EmailTypeRepository emailTypeRepository, EmailRepository emailRepository, Cache cache) {
        this.emailTypeRepository = emailTypeRepository;
        this.emailRepository = emailRepository;
        this.cache = cache;
    }

    private boolean checkDomain(String text) {
        Pattern emailPattern = Pattern.compile(emailTypeRegex, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.find();
    }

    @Transactional
    public boolean addDomain(String domain) {
        if (checkDomain(domain)) {
            if (emailTypeRepository.findByName(domain) != null) {
                return false;
            }
            EmailType emailType = new EmailType(domain);
            emailTypeRepository.save(emailType);
            cache.put(domain,emailType);
            return true;
        }
        return false;
    }
    @Transactional
    public boolean updateDomain(String domain, String newDomain) {
        EmailType emailType = emailTypeRepository.findByName(domain);
        if(!checkDomain(newDomain)||emailType==null||emailTypeRepository.findByName(newDomain)!=null)
            return false;
        for(Email email : emailType.getEmails())
        {
            for(Request request : email.getRequests()){
                request.getEmails().remove(email);
            }
            cache.remove(email.getName());
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailType.getEmails().clear();
        emailType.setName(newDomain);
        cache.remove(domain);
        cache.put(newDomain,emailType);
        return true;
    }

    @Transactional
    public boolean deleteDomain(String domain) {
        EmailType emailType = emailTypeRepository.findByName(domain);
        if(emailType==null)
            return false;
        for(Email email : emailType.getEmails())
        {
            for(Request request : email.getRequests()){
                request.getEmails().remove(email);
            }
            cache.remove(email.getName());
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailTypeRepository.delete(emailType);
        cache.remove(domain);
        return true;
    }

    @Transactional
    public List<DomainDTO> getDomains(){
        List<EmailType> emailTypes = emailTypeRepository.findAll();
        List<DomainDTO> domains = new ArrayList<>();
        for(EmailType emailType : emailTypes){
            domains.add(new DomainDTO(emailType.getName()));
            cache.put(emailType.getName(),emailType);
        }
       return domains;
    }

}
