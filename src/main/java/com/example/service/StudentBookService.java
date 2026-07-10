package com.example.service;

import com.example.repository.StudentBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentBookService {
    @Autowired
    private StudentBookRepository studentBookRepository;
}
