package com.example.view.student;

import com.example.dto.student.StudentDto;
import com.example.service.StudentService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Optional;

@Route("/student/create-update")
@PageTitle("Student Create Update")
public class StudentFormView extends VerticalLayout implements BeforeEnterObserver {

    private final StudentService studentService;

    private TextField nameField = new TextField("Name");
    private TextField surnameField = new TextField("Surname");
    private TextField phoneNumberField = new TextField("Phone Number");

    private Button addBtn = new Button("Add Book");
    private Button cancelBtn = new Button("Cancel");

    private StudentDto selectedStudent = null;

    public StudentFormView(StudentService studentService) {
        this.studentService = studentService;
        addForm();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters =
                event.getLocation().getQueryParameters();

        String idParam = queryParameters
                .getParameters()
                .getOrDefault("id", List.of(""))
                .getFirst();

        if (idParam.isBlank()){
            return;
        }

        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Notification.show("Invalid book id in URL");
            return;
        }

        Optional<StudentDto> optional = studentService.getById(id);
        optional.ifPresent(dto -> {
            selectedStudent = dto;

            nameField.setValue(dto.getName());
            surnameField.setValue(dto.getSurname());
            phoneNumberField.setValue(dto.getPhoneNumber());
            addBtn.setText("Update Book");
            addBtn.setThemeVariants(ButtonVariant.LUMO_WARNING);
            addBtn.setVisible(true);
            cancelBtn.setVisible(true);
        });
    }

    public void addForm() {
        FormLayout form = new FormLayout();
        addBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        addBtn.addClickListener(e -> {
            String name = nameField.getValue();
            String surname = surnameField.getValue();
            String phoneNumber = phoneNumberField.getValue();
            if (selectedStudent == null) { // CREATE
                studentService.create(new StudentDto(name, surname, phoneNumber));
                Notification.show("Student Created").setPosition(Notification.Position.TOP_CENTER);
            } else { //UPDATE
                selectedStudent.setName(name);
                selectedStudent.setSurname(surname);
                selectedStudent.setPhoneNumber(phoneNumber);
                studentService.update(selectedStudent);
                Notification.show("Student Updated").setPosition(Notification.Position.TOP_CENTER);
            }
            clearForm();
            UI.getCurrent().navigate(StudentView.class);
        });

        cancelBtn.setVisible(false);
        cancelBtn.addClickListener(event -> clearForm());

        form.setAutoResponsive(true);
        form.addFormRow(nameField, surnameField, phoneNumberField);
        form.addFormRow(addBtn, cancelBtn);
        add(form);
    }

    public void clearForm() {
        addBtn.setText("Add Student");
        addBtn.removeThemeVariants(ButtonVariant.LUMO_WARNING);
        addBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        nameField.clear();
        surnameField.clear();
        phoneNumberField.clear();
        cancelBtn.setVisible(false);
        selectedStudent = null;
    }
}
