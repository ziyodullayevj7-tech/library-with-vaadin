package com.example.service;

import com.example.dto.student.StudentDto;
import com.example.entity.StudentEntity;
import com.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public void create(StudentDto studentDto) {
        StudentEntity entity = new StudentEntity();
        entity.setName(studentDto.getName());
        entity.setSurname(studentDto.getSurname());
        entity.setPhone(studentDto.getPhoneNumber());
        entity.setCreatedDate(LocalDateTime.now());
        studentRepository.save(entity);
    }

    public Optional<List<StudentDto>> getAll() {
        List<StudentDto> response = new ArrayList<>();
        Optional<List<StudentEntity>> optional = studentRepository.getAllByVisibleIsTrue(Boolean.TRUE);
        if (optional.isEmpty()){
            System.out.println("No students found");
        }else{
            optional.get().forEach(entity -> response.add(toDto(entity)));
            return  Optional.of(response);
        }
        return Optional.empty();
    }

    public StudentDto toDto(StudentEntity entity) {
        StudentDto dto = new StudentDto(entity.getId(), entity.getName(), entity.getSurname(), entity.getPhone(), entity.getCreatedDate().toString());
        return dto;
    }

    public Boolean update(StudentDto dto) {
        Optional<StudentEntity> optional = studentRepository.getById(dto.getId());
        if (optional.isEmpty()) return false;
        StudentEntity entity = optional.get();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(dto.getPhoneNumber());
        entity.setCreatedDate(LocalDateTime.now());
        studentRepository.save(entity);
        return true;
    }

    public Boolean delete(Integer id) {
        Optional<StudentEntity> optional = studentRepository.getById(id);
        if (optional.isEmpty()) return false;
        StudentEntity entity = optional.get();
        entity.setVisible(false);
        studentRepository.save(entity);
        return true;
    }

    public Optional<StudentDto> getById(Integer id) {
        return studentRepository.getById(id).map(this::toDto);
    }
}
