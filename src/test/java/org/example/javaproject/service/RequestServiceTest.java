package org.example.javaproject.service;

import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.dto.RequestDTO;
import org.example.javaproject.entity.Email;
import org.example.javaproject.entity.EmailType;
import org.example.javaproject.entity.Number;
import org.example.javaproject.entity.Request;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.EmailRepository;
import org.example.javaproject.repository.EmailTypeRepository;
import org.example.javaproject.repository.NumberRepository;
import org.example.javaproject.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestServiceTest {
    @InjectMocks
    RequestService requestService;
    @Mock
    Cache cache;

    @Mock
    EmailRepository emailRepository;
    @Mock
    EmailTypeRepository emailTypeRepository;
    @Mock
    RequestRepository requestRepository;
    @Mock
    NumberRepository numberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testExtractNumbersNoMatches() {
        String text = "This is a text, but there no numbers =(";
        List<NumberDTO> result = requestService.extractPhoneNumbers(text);
        assertTrue(result.isEmpty());
        verifyNoMoreInteractions(numberRepository);
    }

    @Test
    void testExtractNumbersMatchesExist() {
        String text = "There is a number 123 4567890 and another one 12 3456789";
        Number number2 = new Number("12 3456789");
        when(numberRepository.findByName("123 4567890")).thenReturn(null);
        when(numberRepository.findByName("12 3456789")).thenReturn(number2);
        List<NumberDTO> result = requestService.extractPhoneNumbers(text);
        assertEquals(2, result.size());
        assertEquals("123 4567890", result.get(0).getNumber());
        assertEquals("12 3456789", result.get(1).getNumber());
        verify(numberRepository).findByName("123 4567890");
        verify(numberRepository).findByName("12 3456789");
        verify(numberRepository).save(any(Number.class));
        verifyNoMoreInteractions(numberRepository);
    }

    @Test
    void testGetAllRequests() {
        String email = null;

        List<Request> allRequests = new ArrayList<>();
        allRequests.add(new Request("Request 1"));
        allRequests.add(new Request("Request 2"));

        when(requestRepository.findAll()).thenReturn(allRequests);
        List<RequestDTO> result = requestService.getRequests(email);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getRequest());
        assertEquals("Request 2", result.get(1).getRequest());

        verify(requestRepository).findAll();
        verifyNoMoreInteractions(requestRepository);
        verifyZeroInteractions(emailRepository);
        verifyNoMoreInteractions(cache);
    }

    @Test
    void testGetRequestsValidEmail() {
        String email = "test@example.com";

        Email emailEntity = new Email(email);
        Request request1 = new Request("Request 1");
        Request request2 = new Request("Request 2");
        emailEntity.getRequests().add(request1);
        emailEntity.getRequests().add(request2);

        when(cache.contains(email)).thenReturn(false);
        when(emailRepository.findByName(email)).thenReturn(emailEntity);

        List<RequestDTO> result = requestService.getRequests(email);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(cache).contains(email);
        verify(emailRepository).findByName(email);
        verify(cache).put(email, emailEntity);
        verifyZeroInteractions(requestRepository);
        verifyNoMoreInteractions(emailRepository);
    }

    @Test
    void testGetRequestsCachedEmail() {
        String email = "test@example.com";

        Email emailEntity = new Email(email);
        Request request1 = new Request("Request 1");
        Request request2 = new Request("Request 2");
        emailEntity.getRequests().add(request1);
        emailEntity.getRequests().add(request2);

        when(cache.contains(email)).thenReturn(true);
        when(cache.get(email)).thenReturn(emailEntity);

        List<RequestDTO> result = requestService.getRequests(email);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(cache).contains(email);
        verify(cache).get(email);
        verifyZeroInteractions(requestRepository);
        verifyZeroInteractions(emailRepository);
    }

    @Test
    void testGetRequestsInvalidEmail() {
        String email = "nonexisting@example.com";

        when(cache.contains(email)).thenReturn(false);
        when(emailRepository.findByName(email)).thenReturn(null);

        List<RequestDTO> result = requestService.getRequests(email);

        assertNull(result);

        verify(cache).contains(email);
        verify(emailRepository).findByName(email);
        verifyZeroInteractions(requestRepository);
        verifyZeroInteractions(cache);
    }

    @Test
    void testDeleteRequest() {
        String requestText = "Request 1";
        Request requestEntity = new Request(requestText);

        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");
        email1.getRequests().add(requestEntity);
        email2.getRequests().add(requestEntity);
        requestEntity.getEmails().add(email1);
        requestEntity.getEmails().add(email2);

        when(requestRepository.findByText(requestText)).thenReturn(requestEntity);
        requestService.deleteRequest(requestText);

        verify(requestRepository).findByText(requestText);
        verify(requestRepository).delete(requestEntity);
        verify(cache).remove(requestText);

        assertFalse(email1.getRequests().contains(requestEntity));
        assertFalse(email2.getRequests().contains(requestEntity));
    }

    @Test
    void testDeleteNonExistingRequest() {
        String requestText = "NonExistingRequest";

        when(requestRepository.findByText(requestText)).thenReturn(null);

        assertThrows(ServiceException.class, () -> requestService.deleteRequest(requestText));

        verify(requestRepository).findByText(requestText);
        verifyNoMoreInteractions(requestRepository);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(cache);
    }

    @Test
    void testUpdateRequest() {
        String requestText = "Request 1";
        String newRequestText = "Updated Request 1";

        Request requestEntity = new Request(requestText);

        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");
        email1.getRequests().add(requestEntity);
        email2.getRequests().add(requestEntity);
        requestEntity.getEmails().add(email1);
        requestEntity.getEmails().add(email2);

        when(requestRepository.findByText(requestText)).thenReturn(requestEntity);
        when(requestRepository.findByText(newRequestText)).thenReturn(null);

        requestService.updateRequest(requestText, newRequestText);

        verify(requestRepository).findByText(requestText);
        verify(requestRepository).findByText(newRequestText);
        verify(cache).remove(requestText);
        verify(cache).put(newRequestText, requestEntity);

        assertEquals(newRequestText, requestEntity.getText());
        assertTrue(email1.getRequests().isEmpty());
        assertTrue(email2.getRequests().isEmpty());
    }

    @Test
    void testUpdateRequestNonExistingRequest() {
        String requestText = "NonExistingRequest";
        String newRequestText = "NewRequest";

        when(requestRepository.findByText(requestText)).thenReturn(null);
        assertThrows(ServiceException.class, () -> {
            requestService.updateRequest(requestText, newRequestText);
        });

        verify(requestRepository).findByText(requestText);
        verifyNoMoreInteractions(requestRepository);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(cache);
    }

    @Test
    void testUpdateRequestExistingNewRequest() {
        String requestText = "Request 1";
        String newRequestText = "Request 2";

        Request requestEntity = new Request(requestText);

        when(requestRepository.findByText(requestText)).thenReturn(requestEntity);
        when(requestRepository.findByText(newRequestText)).thenReturn(requestEntity);

        assertThrows(ServiceException.class, () -> {
            requestService.updateRequest(requestText, newRequestText);
        });

        verify(requestRepository).findByText(requestText);
        verify(requestRepository).findByText(newRequestText);
        verifyNoMoreInteractions(requestRepository);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(cache);
    }

    @Test
    void testExtractEmailsExistingRequest() {
        String requestText = "Test request test@example.com";
        String emailText = "test@example.com";
        String emailType = "@example.com";

        List<EmailDTO> expectedEmails = new ArrayList<>();
        expectedEmails.add(new EmailDTO(emailText));

        Request existingRequest = new Request(requestText);
        Email existingEmail = new Email(emailText);
        existingRequest.getEmails().add(existingEmail);


        when(requestRepository.findByText(requestText)).thenReturn(existingRequest);
        when(emailRepository.findByName(emailText)).thenReturn(existingEmail);
        when(emailTypeRepository.findByName(emailType)).thenReturn(null);

        // Act
        List<EmailDTO> parsedEmails = requestService.extractEmails(requestText);

        // Assert
        verify(requestRepository).findByText(requestText);
        verify(requestRepository, never()).save(any(Request.class));
        verify(emailRepository, never()).save(any(Email.class));

        assertEquals(expectedEmails.size(), parsedEmails.size());
    }

    @Test
    void testExtractEmailsNonExistingRequest() {
        String requestText = "Test request test1@example.com test2@exa.com";
        String emailText1 = "test1@example.com";
        String emailText2 = "test2@exa.com";
        String emailType1 = "@example.com";
        String emailType2 = "@exa.com";

        List<EmailDTO> expectedEmails = new ArrayList<>();
        expectedEmails.add(new EmailDTO(emailText1));
        expectedEmails.add(new EmailDTO(emailText2));

        when(requestRepository.findByText(requestText)).thenReturn(null);
        when(emailRepository.findByName(emailText1)).thenReturn(null);
        when(emailRepository.findByName(emailText2)).thenReturn(null);
        when(emailTypeRepository.findByName(emailType1)).thenReturn(null);
        when(emailTypeRepository.findByName(emailType2)).thenReturn(null);

        List<EmailDTO> parsedEmails = requestService.extractEmails(requestText);

        verify(requestRepository).findByText(requestText);
        verify(requestRepository).save(any(Request.class));
        verify(emailRepository, times(2)).findByName(anyString());
        verify(emailTypeRepository, times(2)).findByName(anyString());
        verify(cache).put(eq(requestText), any(Request.class));
        verify(cache, times(2)).put(anyString(), any(Email.class));

        assertEquals(expectedEmails.size(), parsedEmails.size());
    }

}