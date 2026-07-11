package com.example.view;

import com.example.dto.student.StudentDto;
import com.example.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;


@Route("/student")
@PageTitle("Student CRUD")
public class StudentView extends VerticalLayout {
    private final StudentService studentService;

    private Grid<StudentDto> grid = new Grid<>(StudentDto.class, false);

    public StudentView(StudentService studentService) {
        this.studentService = studentService;
        addForm();
        getByIdForm();
        addGrid();
    }

    public void addForm() {
        TextField nameField = new TextField("Name");
        TextField surnameField = new TextField("Surname");
        TextField phoneField = new TextField("Phone number");

        Button addBtn = new Button("Add Student");
        addBtn.addClickListener(event -> {
            String name = nameField.getValue();
            String surname = surnameField.getValue();
            String phone = phoneField.getValue();

            studentService.create(new StudentDto(name, surname, phone));

            if (studentService.getAll().isPresent()) {
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

    public void addGrid() {
        if (studentService.getAll().isPresent()) {
            grid.setItems(studentService.getAll().get());
            grid.addColumn(StudentDto::getName).setHeader("Name");
            grid.addColumn(StudentDto::getSurname).setHeader("Surname");
            grid.addColumn(StudentDto::getPhoneNumber).setHeader("Phone");
            grid.addComponentColumn(s -> {
                //UPDATE
                Button updateBtn = new Button("Edit");
                updateBtn.addThemeVariants(ButtonVariant.LUMO_WARNING);
                updateBtn.addClickListener(event -> {
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Write Fields Here");

                    TextField editName = new TextField("Name");
                    TextField editSurname = new TextField("Surname");
                    TextField editPhone = new TextField("Phone number");

                    editName.setValue(s.getName());
                    editSurname.setValue(s.getSurname());
                    editPhone.setValue(s.getPhoneNumber());

                    confirmDialog.add(editName, editSurname, editPhone);
                    confirmDialog.addConfirmListener(e -> {
                        Boolean update = studentService.update(new StudentDto(s.getId(), editName.getValue(), editSurname.getValue(), editPhone.getValue(), s.getCreatedDate()));
                        if (!update) {
                            Notification.show("Update Failed").setPosition(Notification.Position.TOP_CENTER);
                        } else {
                            grid.setItems(studentService.getAll().get());
                            Notification.show("Student Edited Successfully").setPosition(Notification.Position.TOP_CENTER);
                        }
                    });
                    confirmDialog.open();
                });

                //DELETE
                Button deleteBtn = new Button("Delete");
                deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
                deleteBtn.addClickListener(event -> {
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Delete Student");
                    confirmDialog.setText("Are you sure you want to delete this?");

                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("Cancel");

                    confirmDialog.addConfirmListener(e -> {
                        Boolean delete = studentService.delete(s.getId());
                        if (!delete) {
                            Notification.show("Delete Failed").setPosition(Notification.Position.TOP_CENTER);
                        } else {
                            grid.setItems(studentService.getAll().get());
                            Notification.show("Student Deleted Successfully").setPosition(Notification.Position.TOP_CENTER);
                        }
                    });
                    confirmDialog.open();
                });

                return new HorizontalLayout(updateBtn, deleteBtn);
            }).setHeader("#");
        }

        add(grid);
    }

    public void getByIdForm() {
        Button getBtn = new Button("Get Student");
        getBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getBtn.addClickListener(event -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Get any student with id");

            TextField idField = new TextField("Id");
            confirmDialog.add(idField);
            confirmDialog.addConfirmListener(e -> {
                Optional<StudentDto> optional = studentService.getById(Integer.parseInt(idField.getValue()));
                if (optional.isEmpty()) {
                    Notification.show("Student Not Found").setPosition(Notification.Position.TOP_CENTER);
                } else {
                    StudentDto dto = optional.get();
                    Notification.show(String.format(
                            "Id: %d, Name: %s, Surname: %s, Phone: %s",
                            dto.getId(), dto.getName(), dto.getSurname(), dto.getPhoneNumber()
                    ));
                }
            });
            confirmDialog.open();
        });
        add(getBtn);
    }
}
