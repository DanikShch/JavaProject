package org.example.javalab.service;

import jakarta.transaction.Transactional;
import org.example.javalab.dto.DomainDTO;
import org.example.javalab.dto.NumberDTO;
import org.example.javalab.entity.Email;
import org.example.javalab.entity.EmailType;
import org.example.javalab.entity.Number;
import org.example.javalab.entity.Request;
import org.example.javalab.repository.NumberRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@EntityScan("org.example.javalab.entity")
public class NumberService {
    NumberRepository numberRepository;
    private static String numberRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?){1,2}\\d{1,9}\\b";

    public NumberService(NumberRepository numberRepository) {
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
            if (numberRepository.findByNumber(number) != null) {
                return false;
            }
            numberRepository.save(new Number(number));
            return true;
        }
        return false;
    }
    @Transactional
    public boolean updateNumber(String number, String newNumber) {
        Number numberEntity = numberRepository.findByNumber(number);
        if(!checkNumber(newNumber)||numberEntity==null||numberRepository.findByNumber(newNumber)!=null)
            return false;
        numberEntity.setNumber(newNumber);
        return true;
    }

    @Transactional
    public boolean deleteNumber(String number) {
        Number numberEntity = numberRepository.findByNumber(number);
        if(numberEntity==null)
            return false;
        numberRepository.delete(numberEntity);
        return true;
    }

    @Transactional
    public List<NumberDTO> getNumbers(){
        List<Number> numberEntities = numberRepository.findAll();
        List<NumberDTO> numbers = new ArrayList<>();
        for(Number number : numberEntities){
            numbers.add(new NumberDTO(number.getNumber()));
        }
        return numbers;
    }


}
