package org.example.javaproject.service;

import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.DomainDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
import org.example.javaproject.entity.Request;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.EmailRepository;
import org.example.javaproject.repository.EmailTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailTypeServiceTest {
    @InjectMocks
    EmailTypeService emailTypeService;
    @Mock
    Cache cache;

    @Mock
    EmailTypeRepository emailTypeRepository;

    @Mock
    EmailRepository emailRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddValidDomain() {
        String domain = "@gmail.com";
        when(cache.get(domain)).thenReturn(null);
        when(emailTypeRepository.findByName(domain)).thenReturn(null);
        assertDoesNotThrow(() -> emailTypeService.addDomain(domain));
        verify(emailTypeRepository, times(1)).save(any(EmailType.class));
        verify(cache, times(1)).put(anyString(), any(EmailType.class));
    }

    @Test
    void testAddInvalidDomain() {
        String domain = "Not domain";
        assertThrows(ServiceException.class, () -> emailTypeService.addDomain(domain));
        verify(emailTypeRepository, never()).save(any(EmailType.class));
        verify(cache, never()).put(anyString(), any(EmailType.class));
    }

    @Test
    void testAddExistingDomain() {
        String domain = "@existing.domain";
        EmailType emailType = new EmailType(domain);
        when(emailTypeRepository.findByName(domain)).thenReturn(emailType);
        assertThrows(ServiceException.class, () -> emailTypeService.addDomain(domain));
        verify(emailTypeRepository, never()).save(any(EmailType.class));
        verify(cache, never()).put(anyString(), any(EmailType.class));
    }

    @Test
    void testUpdateDomain() {
        String oldDomain = "@gmail.com";
        String newDomain = "@mail.ru";
        EmailType oldEmailType = new EmailType(oldDomain);
        when(emailTypeRepository.findByName(oldDomain)).thenReturn(oldEmailType);
        assertDoesNotThrow(() -> emailTypeService.updateDomain(oldDomain,newDomain));
        verify(cache, times(1)).remove(anyString());
        verify(cache, times(1)).put(anyString(), any(EmailType.class));
    }

    @Test
    void testUpdateInvalidDomain() {
        String oldDomain = "@gmail.com";
        String newDomain = "Not domain";
        EmailType oldEmailType = new EmailType(oldDomain);
        when(emailTypeRepository.findByName(oldDomain)).thenReturn(oldEmailType);
        assertThrows(ServiceException.class, () -> emailTypeService.updateDomain(oldDomain,newDomain));
        verify(cache, never()).remove(anyString());
        verify(cache, never()).put(anyString(), any(EmailType.class));
    }

    @Test
    void testDeleteExistingDomain() {
        String domain = "@example.com";
        EmailType emailType = new EmailType(domain);
        Email email = new Email("test@example.com");
        Request request = new Request("qwe test@example.com sad sad");
        email.getRequests().add(request);
        request.getEmails().add(email);
        emailType.getEmails().add(email);
        when(emailTypeRepository.findByName(domain)).thenReturn(emailType);
        for (Email emailEntity : emailType.getEmails()) {
            for (Request requestEntity : email.getRequests()) {
                request.getEmails().remove(any(Email.class));
            }
        }

        assertDoesNotThrow(() -> emailTypeService.deleteDomain(domain));
        verify(emailRepository).deleteAll(anySet());
        verify(emailTypeRepository).delete(emailType);
        verify(cache).remove(domain);
    }

    @Test
    void testDeleteNonExistingDomain() {
        String domain = "NonExistDomain";
        when(emailTypeRepository.findByName(domain)).thenReturn(null);
        assertThrows(ServiceException.class,() -> emailTypeService.deleteDomain(domain));
        verify(emailTypeRepository, never()).save(any(EmailType.class));
        verify(cache, never()).put(anyString(), any(EmailType.class));
    }

    @Test
    void testGetDomains() {
        List<EmailType> emailTypes = new ArrayList<>();
        emailTypes.add(new EmailType("@example1.com"));
        emailTypes.add(new EmailType("@example2.com"));
        when(emailTypeRepository.findAll()).thenReturn(emailTypes);
        List<DomainDTO> result = emailTypeService.getDomains();
        assertEquals(emailTypes.size(), result.size());
        verify(emailTypeRepository).findAll();
        for (EmailType emailType : emailTypes) {
            verify(cache).put(emailType.getName(), emailType);
        }
    }
}