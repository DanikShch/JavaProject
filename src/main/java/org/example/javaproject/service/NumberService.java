package org.example.javaproject.service;

import jakarta.transaction.Transactional;
import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.entity.Number;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.NumberRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javaproject.entity")
public class NumberService {
    Cache cache;
    NumberRepository numberRepository;
    private static final String NUMBER_REGEX = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?) {1,2}\\d{1,9}\\b";

    public NumberService(NumberRepository numberRepository, Cache cache) {
        this.cache = cache;
        this.numberRepository = numberRepository;
    }

    private boolean checkNumber(String text) {
        Pattern numberPattern = Pattern.compile(NUMBER_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher numberMatcher = numberPattern.matcher(text);
        return numberMatcher.find();
    }

    @Transactional
    public void addNumber(String number) {
        if (checkNumber(number)) {
            if (numberRepository.findByName(number) != null) {
                throw new ServiceException("Number already exists");
            }
            Number numberEntity = new Number(number);
            numberRepository.save(numberEntity);
            cache.put(number, numberEntity);
        } else {
            throw new ServiceException("Invalid number");
        }
    }

    @Transactional
    public void updateNumber(String number, String newNumber) {
        Number numberEntity = numberRepository.findByName(number);
        if (!checkNumber(newNumber) || numberEntity == null || numberRepository.findByName(newNumber) != null) {
            throw new ServiceException("Invalid new number or existing old number");
        }
        numberEntity.setName(newNumber);
        cache.remove(number);
        cache.put(newNumber, numberEntity);
    }

    @Transactional
    public void deleteNumber(String number) {
        Number numberEntity = numberRepository.findByName(number);
        if (numberEntity == null) {
            throw new ServiceException("Invalid number");
        }
        numberRepository.delete(numberEntity);
        cache.remove(number);
    }

    @Transactional
    public List<NumberDTO> getNumbers() {
        List<Number> numberEntities = numberRepository.findAll();
        List<NumberDTO> numbers = new ArrayList<>();
        for (Number number : numberEntities) {
            numbers.add(new NumberDTO(number.getName()));
            cache.put(number.getName(), number);
        }
        return numbers;
    }

}
