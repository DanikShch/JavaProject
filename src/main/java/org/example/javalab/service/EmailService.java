package org.example.javalab.service;


import jakarta.transaction.Transactional;
import org.example.javalab.dto.EmailDTO;
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
public class EmailService {
    private static String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
    private static String emailTypeRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    NumberRepository numberRepository;
    EmailRepository emailRepository;
    EmailTypeRepository emailTypeRepository;
    RequestRepository requestRepository;

    public EmailService(NumberRepository numberRepository, EmailRepository emailRepository, EmailTypeRepository emailTypeRepository, RequestRepository requestRepository) {
        this.numberRepository = numberRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
        this.requestRepository = requestRepository;
    }


    @Transactional
    public boolean addEmail(String email) {
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        Pattern emailTypePattern = Pattern.compile(emailTypeRegex);
        Matcher emailTypeMatcher = emailTypePattern.matcher(email);
        EmailType emailType;
        if(matcher.find()&&emailTypeMatcher.find()){
            String domain = emailTypeMatcher.group();
            Email emailEntity = new Email(email);
            emailRepository.save(emailEntity);
            if (emailTypeRepository.findByTypeName(domain) == null) {
                emailType = new EmailType(domain);
                emailTypeRepository.save(emailType);
            }
            else {
                emailType= emailTypeRepository.findByTypeName(domain);
            }
            emailType.getEmails().add(emailEntity);
            emailEntity.setEmailType(emailType);


        }
        return true;
    }

    @Transactional
    public List<EmailDTO> getEmails(String domain){
        List<EmailDTO> emailNames = new ArrayList<>();
        List<Email> emails;
        if(domain==null){
            emails = emailRepository.findAll();
        }
        else{
            EmailType emailType = emailTypeRepository.findByTypeName(domain);
            if(emailType!=null){
                emails = new ArrayList<>(emailType.getEmails());
            }
            else {
                return null;
            }
        }
        for(Email email : emails){
            emailNames.add(new EmailDTO(email.getEmail()));
        }
        return emailNames;
    }

    @Transactional
    public boolean updateEmail(String email, String newEmail) {
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) return false;
        Email emailEntity = emailRepository.findByEmail(email);
        if (emailEntity != null) {
            Pattern emailTypePattern = Pattern.compile(emailTypeRegex, Pattern.CASE_INSENSITIVE);
            Matcher emailTypeMatcher = emailTypePattern.matcher(newEmail);
            if (emailTypeMatcher.find()) {
                String domain = emailTypeMatcher.group();
                EmailType emailType = emailTypeRepository.findByTypeName(domain);
                if (emailType == null) {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                }
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailType(emailType);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteEmail(String email) {
        Email emailEntity = emailRepository.findByEmail(email);
        if (emailEntity != null) {
            for(Request request : emailEntity.getRequests()){
                request.getEmails().remove(emailEntity);
            }
            emailRepository.delete(emailEntity);
            return true;
        }
        return false;
    }
}
