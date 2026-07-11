package com.example.view;

import com.example.dto.student.StudentDto;
import com.example.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route("/student")
@PageTitle("Student CRUD")
public class StudentView extends VerticalLayout {
    private final StudentService studentService;

    private Grid<StudentDto> grid = new Grid<>(StudentDto.class, false);

    public StudentView(StudentService studentService) {
        this.studentService = studentService;
        addForm();
        addGrid();
    }

    public void addForm(){
        TextField nameField = new TextField("Name");
        TextField surnameField = new TextField("Surname");
        TextField phoneField = new TextField("Phone number");

        Button addBtn = new Button("Add Student");
        addBtn.addClickListener(event -> {
            String name = nameField.getValue();
            String surname = surnameField.getValue();
            String phone = phoneField.getValue();

            studentService.create(new StudentDto(name, surname, phone));

            if (studentService.getAll().isPresent()){
                grid.setItems(studentService.getAll().get());
            }

            Notification.show("Student Created").setPosition(Notification.Position.TOP_CENTER);

            nameField.clear();
            surnameField.clear();
            phoneField.clear();
        });

        add(nameField);
        add(surnameField);
        add(phoneField);
        add(addBtn);
    }

    public void addGrid(){
        if (studentService.getAll().isPresent()){
            grid.setItems(studentService.getAll().get());
            grid.addColumn(StudentDto::getName).setHeader("Name");
            grid.addColumn(StudentDto::getSurname).setHeader("Surname");
            grid.addColumn(StudentDto::getPhoneNumber).setHeader("Phone");
        }
        add(grid);
    }
}
