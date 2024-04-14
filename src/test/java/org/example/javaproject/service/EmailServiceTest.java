package org.example.javaproject.service;

import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
import org.example.javaproject.entity.Request;
import org.example.javaproject.repository.EmailRepository;
import org.example.javaproject.repository.EmailTypeRepository;
import org.example.javaproject.repository.NumberRepository;
import org.example.javaproject.repository.RequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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
    public void testAddEmail() {
        // Arrange
        String email = "test@example.com";
        String domain = "@example.com";
        EmailType emailType = new EmailType(domain);
        Email emailEntity = new Email(email);
        emailEntity.setEmailType(emailType);

        when(emailTypeRepository.findByName(domain)).thenReturn(null);
        when(emailRepository.save(any(Email.class))).thenReturn(emailEntity);
        when(emailTypeRepository.save(any(EmailType.class))).thenReturn(emailType);

        // Act
        assertDoesNotThrow(() -> emailService.addEmail(email));

        // Assert
        verify(emailRepository, times(1)).save(any(Email.class));
        verify(emailTypeRepository, times(1)).save(any(EmailType.class));
        verify(cache, times(2)).put(anyString(), any());
    }

    @Test
    public void testGetEmails_WithDomain_ReturnsEmailDTOList() {
        // Arrange
        String domain = "example.com";
        EmailType emailType = new EmailType("example.com");
        Set<Email> emails = new HashSet<>();
        emails.add(new Email("test1@example.com"));
        emails.add(new Email("test2@example.com"));

        when(cache.contains(domain)).thenReturn(true);
        when(cache.get(domain)).thenReturn(emailType);
        when(emailRepository.findByEmailType(domain)).thenReturn(emails);

        // Act
        List<EmailDTO> result = emailService.getEmails(domain);

        // Assert
        assertEquals(2, result.size());
        assertEquals("test1@example.com", result.get(0).getEmail());
        assertEquals("test2@example.com", result.get(1).getEmail());

        // Verify interactions with mocks
        verify(cache, times(1)).contains(domain);
        verify(cache, times(1)).get(domain);
        verify(emailRepository, times(1)).findByEmailType(domain);
        verify(cache, times(1)).put(emailType.getName(), emailType);
        verify(cache, times(2)).put(anyString(), any(Email.class));
    }

    @Test
    public void testGetEmails_WithNullDomain_ReturnsAllEmailDTOs() {
        // Arrange
        List<Email> emails = new ArrayList<>();
        emails.add(new Email("test1@example.com"));
        emails.add(new Email("test2@example.com"));

        when(emailRepository.findAll()).thenReturn(emails);

        // Act
        List<EmailDTO> result = emailService.getEmails(null);

        // Assert
        assertEquals(2, result.size());
        assertEquals("test1@example.com", result.get(0).getEmail());
        assertEquals("test2@example.com", result.get(1).getEmail());

        // Verify interactions with mocks
        verify(cache, never()).contains(anyString());
        verify(cache, never()).get(anyString());
        verify(emailRepository, times(1)).findAll();
        verify(cache, times(2)).put(anyString(), any(Email.class));
    }

    @Test
    public void testGetEmails_WithInvalidDomain_ThrowsRuntimeException() {
        // Arrange
        String domain = "invalid.com";

        when(cache.contains(domain)).thenReturn(false);
        when(emailTypeRepository.findByName(domain)).thenReturn(null);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.getEmails(domain);
        });

        // Verify interactions with mocks
        verify(cache, times(1)).contains(domain);
        verify(cache, never()).get(anyString());
        verify(emailTypeRepository, times(1)).findByName(domain);
        verify(cache, never()).put(anyString(), any(EmailType.class));
        verify(cache, never()).put(anyString(), any(Email.class));
    }

    @Test
    public void testUpdateEmail() {
        // Arrange
        String email = "old@example.com";
        String newEmail = "new@example.com";
        String domain = "@example.com";
        Email emailEntity = new Email(email);
        EmailType emailType = new EmailType(domain);

        when(emailRepository.findByName(email)).thenReturn(emailEntity);
        when(emailTypeRepository.findByName(domain)).thenReturn(emailType);

        // Act
        assertDoesNotThrow(() -> emailService.updateEmail(email, newEmail));

        // Assert
        assertEquals(newEmail, emailEntity.getName());
        assertEquals(emailType, emailEntity.getEmailType());
        verify(cache, times(1)).remove(email);
        verify(cache, times(1)).put(newEmail, emailEntity);
        verify(cache, times(1)).put(domain, emailType);
    }

    @Test
    public void testDeleteEmail() {
        // Arrange
        String email = "test@example.com";
        Email emailEntity = new Email(email);

        when(emailRepository.findByName(email)).thenReturn(emailEntity);

        // Act
        assertDoesNotThrow(() -> emailService.deleteEmail(email));

        // Assert
        verify(emailRepository, times(1)).delete(emailEntity);
        verify(cache, times(1)).remove(email);
    }

}