package org.example.javalab.service;

import jakarta.transaction.Transactional;
import org.example.javalab.component.Cache;
import org.example.javalab.dto.NumberDTO;
import org.example.javalab.entity.Number;
import org.example.javalab.repository.NumberRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javalab.entity")
public class NumberService {
    Cache cache;
    NumberRepository numberRepository;
    private static String numberRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?) {1,2}\\d{1,9}\\b";

    public NumberService(NumberRepository numberRepository, Cache cache) {
        this.cache = cache;
        this.numberRepository = numberRepository;
    }

    private boolean checkNumber(String text) {
        Pattern numberPattern = Pattern.compile(numberRegex, Pattern.CASE_INSENSITIVE);
        Matcher numberMatcher = numberPattern.matcher(text);
        return numberMatcher.find();
    }

    @Transactional
    public boolean addNumber(String number) {
        if (checkNumber(number)) {
            if (numberRepository.findByName(number) != null) {
                return false;
            }
            Number numberEntity = new Number(number);
            numberRepository.save(numberEntity);
            cache.put(number, numberEntity);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateNumber(String number, String newNumber) {
        Number numberEntity = numberRepository.findByName(number);
        if (!checkNumber(newNumber) || numberEntity == null || numberRepository.findByName(newNumber) != null) {
            return false;
        }
        numberEntity.setName(newNumber);
        cache.remove(number);
        cache.put(newNumber, numberEntity);
        return true;
    }

    @Transactional
    public boolean deleteNumber(String number) {
        Number numberEntity = numberRepository.findByName(number);
        if (numberEntity == null) {
            return false;
        }
        numberRepository.delete(numberEntity);
        cache.remove(number);
        return true;
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
