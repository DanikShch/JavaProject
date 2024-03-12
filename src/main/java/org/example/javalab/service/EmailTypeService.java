package org.example.javalab.service;

import jakarta.transaction.Transactional;
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
    EmailTypeRepository emailTypeRepository;
    EmailRepository emailRepository;
    private static String emailTypeRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";

    public EmailTypeService(EmailTypeRepository emailTypeRepository, EmailRepository emailRepository) {
        this.emailTypeRepository = emailTypeRepository;
        this.emailRepository = emailRepository;
    }

    private boolean checkDomain(String text) {
        Pattern emailPattern = Pattern.compile(emailTypeRegex, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.find();
    }

    @Transactional
    public boolean addDomain(String domain) {
        if (checkDomain(domain)) {
            if (emailTypeRepository.findByTypeName(domain) != null) {
                return false;
            }
            emailTypeRepository.save(new EmailType(domain));
            return true;
        }
        return false;
    }
    @Transactional
    public boolean updateDomain(String domain, String newDomain) {
        EmailType emailType = emailTypeRepository.findByTypeName(domain);
        if(!checkDomain(newDomain)||emailType==null||emailTypeRepository.findByTypeName(newDomain)!=null)
            return false;
        for(Email email : emailType.getEmails())
        {
            for(Request request : email.getRequests()){
                request.getEmails().remove(email);
            }
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailType.getEmails().clear();
        emailType.setTypeName(newDomain);
        return true;
    }

    @Transactional
    public boolean deleteDomain(String domain) {
        EmailType emailType = emailTypeRepository.findByTypeName(domain);
        if(emailType==null)
            return false;
        for(Email email : emailType.getEmails())
        {
            for(Request request : email.getRequests()){
                request.getEmails().remove(email);
            }
        }
        emailRepository.deleteAll(emailType.getEmails());
        emailTypeRepository.delete(emailType);
        return true;
    }

    @Transactional
    public List<DomainDTO> getDomains(){
        List<EmailType> emailTypes = emailTypeRepository.findAll();
        List<DomainDTO> domains = new ArrayList<>();
        for(EmailType emailType : emailTypes){
            domains.add(new DomainDTO(emailType.getTypeName()));
        }
       return domains;
    }

}
