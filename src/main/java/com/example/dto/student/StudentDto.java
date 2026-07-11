package com.example.dto.student;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDto {
    private Integer id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String createdDate;

    public StudentDto(String name, String surname, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }
}
