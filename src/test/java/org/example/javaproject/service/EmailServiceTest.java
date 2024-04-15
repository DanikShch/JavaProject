package org.example.javaproject.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.javaproject.service.EmailService.EMAIL_REGEX;
import static org.example.javaproject.service.EmailService.EMAIL_TYPE_REGEX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;
    @Mock
    private NumberRepository numberRepository;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private EmailTypeRepository emailTypeRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private Cache cache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddEmailWithExistingDomain() {
        // Arrange
        String email = "test@example.com";
        String domain = "@example.com";
        EmailType emailType = new EmailType(domain);
        Email emailEntity = new Email(email);
        emailEntity.setEmailType(emailType);
        when(emailRepository.findByName(email)).thenReturn(null);
        when(emailTypeRepository.findByName(domain)).thenReturn(emailType);
        assertDoesNotThrow(() -> emailService.addEmail(email));
        verify(emailTypeRepository, never()).save(any(EmailType.class));
    }

    @Test
    void testAddEmailWitNonExistingDomain() {
        // Arrange
        String email = "test@example.com";
        String domain = "@example.com";
        EmailType emailType = new EmailType(domain);
        Email emailEntity = new Email(email);
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX);
        Matcher emailTypeMatcher = emailTypePattern.matcher(email);
        emailEntity.setEmailType(emailType);
        assertTrue(matcher.find());
        assertTrue(emailTypeMatcher.find());
        when(emailRepository.findByName(email)).thenReturn(null);
        when(emailTypeRepository.findByName(domain)).thenReturn(null);
        assertDoesNotThrow(() -> emailService.addEmail(email));
        verify(cache, times(1)).put(anyString(), any(Email.class));
        verify(cache, times(1)).put(anyString(), any(EmailType.class));
    }

    @Test
    void testAddInvalidEmail() {
        String email = "Not email =(";
        String domain = "Not domain =(";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX);
        Matcher emailTypeMatcher = emailTypePattern.matcher(email);
        assertFalse(matcher.find()&&emailTypeMatcher.find());
        assertThrows(RuntimeException.class, () -> emailService.addEmail(email));
    }

    @Test
    void testAddExistingEmail() {
        // Arrange
        String email = "test@example.com";
        String domain = "@example.com";
        EmailType emailType = new EmailType(domain);
        Email emailEntity = new Email(email);
        emailEntity.setEmailType(emailType);
        when(emailRepository.findByName(email)).thenReturn(emailEntity);
        assertThrows(RuntimeException.class, () -> emailService.addEmail(email));
    }

    @Test
    void testGetEmails_WithDomain_ReturnsEmailDTOList() {
        String domain = "example.com";
        EmailType emailType = new EmailType("example.com");
        Set<Email> emails = new LinkedHashSet<>();
        emails.add(new Email("test1@example.com"));
        emails.add(new Email("test2@example.com"));

        when(cache.contains(domain)).thenReturn(true);
        when(cache.get(domain)).thenReturn(emailType);
        when(emailRepository.findByEmailType(domain)).thenReturn(emails);

        List<EmailDTO> result = emailService.getEmails(domain);

        assertEquals(2, result.size());
        assertEquals("test1@example.com", result.get(0).getEmail());
        assertEquals("test2@example.com", result.get(1).getEmail());

        verify(cache, times(1)).contains(domain);
        verify(cache, times(1)).get(domain);
        verify(emailRepository, times(1)).findByEmailType(domain);
        verify(cache, times(1)).put(emailType.getName(), emailType);
        verify(cache, times(2)).put(anyString(), any(Email.class));
    }

    @Test
    void testGetEmails_WithNullDomain_ReturnsAllEmailDTOs() {
        List<Email> emails = new ArrayList<>();
        emails.add(new Email("test1@example.com"));
        emails.add(new Email("test2@example.com"));

        when(emailRepository.findAll()).thenReturn(emails);
        List<EmailDTO> result = emailService.getEmails(null);

        assertEquals(2, result.size());
        assertEquals("test1@example.com", result.get(0).getEmail());
        assertEquals("test2@example.com", result.get(1).getEmail());

        verify(cache, never()).contains(anyString());
        verify(cache, never()).get(anyString());
        verify(emailRepository, times(1)).findAll();
        verify(cache, times(2)).put(anyString(), any(Email.class));
    }

    @Test
    void testGetEmails_WithInvalidDomain_ThrowsRuntimeException() {
        String domain = "invalid.com";

        when(cache.contains(domain)).thenReturn(false);
        when(emailTypeRepository.findByName(domain)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            emailService.getEmails(domain);
        });

        verify(cache, times(1)).contains(domain);
        verify(cache, never()).get(anyString());
        verify(emailTypeRepository, times(1)).findByName(domain);
        verify(cache, never()).put(anyString(), any(EmailType.class));
        verify(cache, never()).put(anyString(), any(Email.class));
    }

    @Test
    void testUpdateEmailExistingDomain() {
        // Arrange
        String email = "old@example.com";
        String newEmail = "new@example.com";
        String domain = "@example.com";
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailTypeMatcher = emailTypePattern.matcher(newEmail);
        Email emailEntity = new Email(email);
        EmailType emailType = new EmailType(domain);
        assertTrue(emailMatcher.find());
        assertTrue(emailTypeMatcher.find());
        when(emailRepository.findByName(email)).thenReturn(emailEntity);
        when(emailTypeRepository.findByName(domain)).thenReturn(emailType);

        // Act
        assertDoesNotThrow(() -> emailService.updateEmail(email, newEmail));


        verify(cache, times(1)).remove(email);
        verify(cache, times(1)).put(newEmail, emailEntity);
        verify(cache, times(1)).put(domain, emailType);
    }

    @Test
    void testUpdateEmailNonExistingDomain() {
        // Arrange
        String email = "old@example.com";
        String newEmail = "new@example.com";
        String domain = "@example.com";
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailTypeMatcher = emailTypePattern.matcher(newEmail);
        Email emailEntity = new Email(email);
        assertTrue(emailMatcher.find());
        assertTrue(emailTypeMatcher.find());
        when(emailRepository.findByName(email)).thenReturn(emailEntity);
        when(emailTypeRepository.findByName(domain)).thenReturn(null);
        EmailType emailType = new EmailType(domain);
        // Act
        assertDoesNotThrow(() -> emailService.updateEmail(email, newEmail));


        verify(cache, times(1)).remove(email);
        verify(cache, times(1)).put(newEmail, emailEntity);
    }

    @Test
    void testUpdateWrongEmail() {
        // Arrange
        String email = "old@example.com";
        String newEmail = "Not email example.com";
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        assertFalse(emailMatcher.find());
        assertThrows(ServiceException.class, () -> emailService.updateEmail(email, newEmail));
    }

    @Test
    void testUpdateEmailNonExistingEmail() {
        // Arrange
        String email = "non.existing@example.com";
        String newEmail = "new@example.com";
        String domain = "@example.com";
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        Pattern emailTypePattern = Pattern.compile(EMAIL_TYPE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailTypeMatcher = emailTypePattern.matcher(newEmail);
        Email emailEntity = new Email(email);
        EmailType emailType = new EmailType(domain);
        assertTrue(emailMatcher.find());
        assertTrue(emailTypeMatcher.find());
        when(emailRepository.findByName(email)).thenReturn(null);
        assertThrows(ServiceException.class, () -> emailService.updateEmail(email, newEmail));
        verify(cache, never()).remove(email);
        verify(cache, never()).put(newEmail, emailEntity);
        verify(cache, never()).put(domain, emailType);
    }


    @Test
    public void testDeleteExistingEmail() {
        // Arrange
        String email = "test@example.com";
        Email emailEntity = new Email(email);
        when(emailRepository.findByName(email)).thenReturn(emailEntity);

        Request request = new Request();
        request.setEmails(new HashSet<>());
        request.getEmails().add(emailEntity);

        Set<Request> requests = new HashSet<>();
        requests.add(request);

        emailEntity.setRequests(requests);
        for (Request requestEntity : emailEntity.getRequests()) {
            requestEntity.getEmails().remove(emailEntity);
        }
        emailService.deleteEmail(email);

        // Assert
        verify(emailRepository, times(1)).findByName(email);
        verify(emailRepository, times(1)).delete(emailEntity);
        verify(cache, times(1)).remove(email);
    }

    @Test
    public void testDeleteNonExistingEmail() {
        // Arrange
        String email = "test@example.com";
        when(emailRepository.findByName(email)).thenReturn(null);
        // Act
        assertThrows(ServiceException.class, () -> emailService.deleteEmail(email));
    }

}